/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class XslElement extends Instruction {

    private AttributeValue _name; // name treated as AVT (7.1.3)
    private AttributeValueTemplate _namespace = null;
    private String _namespacePrefix;
    private boolean _ignore = false;

    /**
     * Displays the contents of the element
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Element " + _name);
	displayContents(indent + IndentIncrement);
    }

    /**
     * Parses the element's contents. Special care taken for namespaces.
     * TODO: The namespace attribute that specifies the namespace to use
     * for the element is an attribute value template and not a string
     * constant. This means that we do not know what namespace is used
     * before runtime. This causes a problem with the way output namespaces
     * are handled at compile-time. We use a shortcut in this method to get
     * around the problem by treating the namaspace attribute as a constant.
     *          (Yes, I know this is a hack, bad, bad, bad.)
     */
    public void parseContents(Parser parser) {

	final SymbolTable stable = parser.getSymbolTable();

	// First try to get namespace from the namespace attribute
	String namespace = getAttribute("namespace");

	// If that is undefied we use the prefix in the supplied QName
	String name = getAttribute("name");
	QName qname = parser.getQNameSafe(name);
	final String prefix = qname.getPrefix();
	if ((namespace == null || namespace == Constants.EMPTYSTRING) && 
	    (prefix != null)) {
	    namespace = lookupNamespace(prefix); 
	    if (namespace == null) {
		final ErrorMsg msg =
		    new ErrorMsg(ErrorMsg.NSPUNDEF_ERR, prefix);
		parser.reportError(Constants.WARNING, msg);
		parseChildren(parser);
		_ignore = true; // Ignore the element if prefix is undeclared
		return;
	    }
	}

	// Next check that the local part of the QName is legal (no whitespace)
	if (qname.getLocalPart().indexOf(' ') > -1) {
	    final ErrorMsg msg = 
		new ErrorMsg("You can't call an element \""+
			     qname.getLocalPart()+"\"");
	    parser.reportError(Constants.WARNING, msg);
	    parseChildren(parser);
	    _ignore = true; // Ignore the element if the local part is invalid
	    return;
	}

	// Check if this element belongs in a specific namespace
	if (namespace != Constants.EMPTYSTRING) {
	    // Get the namespace requested by the xsl:element
	    _namespace = new AttributeValueTemplate(namespace, parser);
	    // Get the current prefix for that namespace (if any)
	    _namespacePrefix = lookupPrefix(namespace);
	    // Is it the default namespace?
	    if ((_namespacePrefix = prefix) == null)
		_namespacePrefix = Constants.EMPTYSTRING;

	    // Construct final element QName
	    if (_namespacePrefix == Constants.EMPTYSTRING)
		name = qname.getLocalPart();
	    else
		name = _namespacePrefix+":"+qname.getLocalPart();
	}

	_name = AttributeValue.create(this, name, parser);

	// Handle the 'use-attribute-sets' attribute
	final String useSets = getAttribute("use-attribute-sets");
	if (useSets.length() > 0) {
	    addElement(new UseAttributeSets(useSets, parser));
	}

	parseChildren(parser);
    }

    /**
     * Run type check on element name & contents
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (!_ignore) {
	    _name.typeCheck(stable);
	    if (_namespace != null)
		_namespace.typeCheck(stable);
	}
	typeCheckContents(stable);
	return Type.Void;
    }

    /**
     * Compiles code that emits the element with the necessary namespace
     * definitions. The element itself is ignored if the element definition
     * was in any way erronous, but the child nodes are still processed.
     * See the overriden translateContents() method as well.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Ignore this element if not correctly declared
	if (!_ignore) {
	    // Compile code that emits the element start tag
	    il.append(methodGen.loadHandler());
	    _name.translate(classGen, methodGen);
	    il.append(DUP2);	// duplicate these 2 args for endElement
	    il.append(methodGen.startElement());

	    // Compile code that emits any needed namespace declaration
	    if (_namespace != null) {
		// public void attribute(final String name, final String value)
		il.append(methodGen.loadHandler());
		il.append(new PUSH(cpg,_namespacePrefix));
		_namespace.translate(classGen,methodGen);
		il.append(methodGen.namespace());
	    }
	}

	// Compile code that emits the element attributes and contents
	translateContents(classGen, methodGen);

	// Ignore this element if not correctly declared
	if (!_ignore) {
	    // Compile code that emits the element end tag
	    il.append(methodGen.endElement());
	}
    }

    /**
     * Override this method to make sure that xsl:attributes are not
     * copied to output if this xsl:element is to be ignored
     */
    public void translateContents(ClassGenerator classGen,
				  MethodGenerator methodGen) {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    final SyntaxTreeNode item =
		(SyntaxTreeNode)getContents().elementAt(i);
	    if ((_ignore) && (item instanceof XslAttribute)) continue;
	    item.translate(classGen, methodGen);
	}
    }

}
