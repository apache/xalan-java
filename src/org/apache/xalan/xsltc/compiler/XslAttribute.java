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
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author Gunnlaugur Briem <gthb@dimon.is>
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class XslAttribute extends Instruction {

    private String _prefix;
    private AttributeValue _name; 	// name treated as AVT (7.1.3)
    private AttributeValueTemplate _namespace = null;
    private boolean _ignore = false;

    /**
     * Returns the name of the attribute
     */
    public AttributeValue getName() {
	return _name;
    }

    /**
     * Displays the contents of the attribute
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Attribute " + _name);
	displayContents(indent + IndentIncrement);
    }
		
    /**
     * Parses the attribute's contents. Special care taken for namespaces.
     */
    public void parseContents(Parser parser) {
	boolean generated = false;
	final SymbolTable stable = parser.getSymbolTable();

	String name = getAttribute("name");
	String namespace = getAttribute("namespace");
	QName qname = parser.getQName(name, false);
	final String prefix = qname.getPrefix();

	if ((prefix != null) && (prefix.equals(XMLNS_PREFIX))) {
	    reportError(this, parser, ErrorMsg.ILLEGAL_ATTR_NAME_ERR, name);
	    return;
	}

	// Ignore attribute if preceeded by some other type of element
	final SyntaxTreeNode parent = getParent();
	final Vector siblings = parent.getContents();
	for (int i = 0; i < parent.elementCount(); i++) {
	    SyntaxTreeNode item = (SyntaxTreeNode)siblings.elementAt(i);
	    if (item == this) break;

	    // These three objects result in one or more attribute output
	    if (item instanceof XslAttribute) continue;
	    if (item instanceof UseAttributeSets) continue;
	    if (item instanceof LiteralAttribute) continue;
	    if (item instanceof Text) continue;

	    // These objects _can_ result in one or more attribute
	    // The output handler will generate an error if not (at runtime)
	    if (item instanceof If) continue;
	    if (item instanceof Choose) continue;
 	    if (item instanceof CopyOf) continue;
 	    if (item instanceof VariableBase) continue;
	    reportWarning(this, parser, ErrorMsg.STRAY_ATTRIBUTE_ERR, name);
	    _ignore = true;
	}

	// Get namespace from namespace attribute?
	if (namespace != null && namespace != Constants.EMPTYSTRING) {
	    _prefix = lookupPrefix(namespace);
	    _namespace = new AttributeValueTemplate(namespace, parser, this);
	}
	// Get namespace from prefix in name attribute?
	else if (prefix != null && prefix != Constants.EMPTYSTRING) {
	    _prefix = prefix;
	    namespace = lookupNamespace(prefix);
	    if (namespace != null) {
		_namespace = new AttributeValueTemplate(namespace, parser, this);
	    }
	}
	
	// Common handling for namespaces:
	if (_namespace != null) {
	    // Generate prefix if we have none
	    if (_prefix == null || _prefix == Constants.EMPTYSTRING) {
		if (prefix != null) {
		    _prefix = prefix;
		}
		else {
		    _prefix = stable.generateNamespacePrefix();
		    generated = true;
		}
	    }
	    else if (prefix != null && !prefix.equals(_prefix)) {
		_prefix = prefix;
	    }

	    name = _prefix + ":" + qname.getLocalPart();

	    /*
	     * TODO: The namespace URI must be passed to the parent 
	     * element but we don't yet know what the actual URI is 
	     * (as we only know it as an attribute value template). 
	     */
	    if ((parent instanceof LiteralElement) && (!generated)) {
		((LiteralElement)parent).registerNamespace(_prefix,
							   namespace,
							   stable, false);
	    }
	}

	if (name.equals(XMLNS_PREFIX)) {
	    reportError(this, parser, ErrorMsg.ILLEGAL_ATTR_NAME_ERR, name);
	    return;
	}

	if (parent instanceof LiteralElement) {
	    ((LiteralElement)parent).addAttribute(this);
	}

	_name = AttributeValue.create(this, name, parser);
	parseChildren(parser);
    }
	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (!_ignore) {
	    _name.typeCheck(stable);
	    if (_namespace != null) {
		_namespace.typeCheck(stable);
	    }
	    typeCheckContents(stable);
	}
	return Type.Void;
    }

    /**
     *
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_ignore) return;
	_ignore = true;

	// Compile code that emits any needed namespace declaration
	if (_namespace != null) {
	    // public void attribute(final String name, final String value)
	    il.append(methodGen.loadHandler());
	    il.append(new PUSH(cpg,_prefix));
	    _namespace.translate(classGen,methodGen);
	    il.append(methodGen.namespace());
	}

	// Save the current handler base on the stack
	il.append(methodGen.loadHandler());
	il.append(DUP);		// first arg to "attributes" call
	
	// Push attribute name
	_name.translate(classGen, methodGen);// 2nd arg

	// Push attribute value - shortcut for literal strings
	if ((elementCount() == 1) && (elementAt(0) instanceof Text)) {
	    il.append(new PUSH(cpg, ((Text)elementAt(0)).getText()));
	}
	else {
	    il.append(classGen.loadTranslet());
	    il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
						   "stringValueHandler",
						   STRING_VALUE_HANDLER_SIG)));
	    il.append(DUP);
	    il.append(methodGen.storeHandler());
	    // translate contents with substituted handler
	    translateContents(classGen, methodGen);
	    // get String out of the handler
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(STRING_VALUE_HANDLER,
							 "getValue",
							 "()" + STRING_SIG)));
	}

	// call "attribute"
	il.append(methodGen.attribute());
	// Restore old handler base from stack
	il.append(methodGen.storeHandler());
    }
}
