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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;

import org.apache.xalan.xsltc.compiler.util.*;

final class ApplyImports extends Instruction {

    private QName      _modeName;
    private String     _functionName;
    private int        _precedence;

    public void display(int indent) {
	indent(indent);
	Util.println("ApplyTemplates");
	indent(indent + IndentIncrement);
	if (_modeName != null) {
	    indent(indent + IndentIncrement);
	    Util.println("mode " + _modeName);
	}
    }

    /**
     * Returns true if this <xsl:apply-imports/> element has parameters
     */
    public boolean hasWithParams() {
	return hasContents();
    }

    /**
     * Determine the lowest import precedence for any stylesheet imported
     * or included by the stylesheet in which this <xsl:apply-imports/>
     * element occured. The templates that are imported by the stylesheet in
     * which this element occured will all have higher import precedence than
     * the integer returned by this method.
     */
    private int getMinPrecedence(int max) {
	Stylesheet stylesheet = getStylesheet();
	Stylesheet root = getParser().getTopLevelStylesheet();

	int min = max;

	Enumeration templates = root.getContents().elements();
	while (templates.hasMoreElements()) {
	    SyntaxTreeNode child = (SyntaxTreeNode)templates.nextElement();
	    if (child instanceof Template) {
		Stylesheet curr = child.getStylesheet();
		while ((curr != null) && (curr != stylesheet)) {
		    if (curr._importedFrom != null)
			curr = curr._importedFrom;
		    else if (curr._includedFrom != null)
			curr = curr._includedFrom;
		    else
			curr = null;
		}
		if (curr == stylesheet) {
		    int prec = child.getStylesheet().getImportPrecedence();
		    if (prec < min) min = prec;
		}
	    }
	}
	return (min);
    }

    /**
     * Parse the attributes and contents of an <xsl:apply-imports/> element.
     */
    public void parseContents(Parser parser) {
	// Indicate to the top-level stylesheet that all templates must be
	// compiled into separate methods.
	Stylesheet stylesheet = getStylesheet();
	stylesheet.setTemplateInlining(false);

	// Get the mode we are currently in (might not be any)
	Template template = getTemplate();
	_modeName = template.getModeName();
	_precedence = template.getImportPrecedence();

	// Get the method name for <xsl:apply-imports/> in this mode
	stylesheet = parser.getTopLevelStylesheet();

	// Get the [min,max> precedence of all templates imported under the
	// current stylesheet
	final int maxPrecedence = _precedence;
	final int minPrecedence = getMinPrecedence(maxPrecedence);
	final Mode mode = stylesheet.getMode(_modeName);
	_functionName = mode.functionName(minPrecedence, maxPrecedence);

	parseChildren(parser);	// with-params
    }

    /**
     * Type-check the attributes/contents of an <xsl:apply-imports/> element.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	typeCheckContents(stable);		// with-params
	return Type.Void;
    }

    /**
     * Translate call-template. A parameter frame is pushed only if
     * some template in the stylesheet uses parameters. 
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Stylesheet stylesheet = classGen.getStylesheet();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final int current = methodGen.getLocalIndex("current");

	// Push the arguments that are passed to applyTemplates()
	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
	// Wrap the current node inside an iterator
	int init = cpg.addMethodref(SINGLETON_ITERATOR,
				    "<init>", "("+NODE_SIG+")V");
	il.append(new NEW(cpg.addClass(SINGLETON_ITERATOR)));
	il.append(DUP);
	il.append(methodGen.loadCurrentNode());
	il.append(new INVOKESPECIAL(init));

	il.append(methodGen.loadHandler());

	// Construct the translet class-name and the signature of the method
	final String className = classGen.getStylesheet().getClassName();
	final String signature = classGen.getApplyTemplatesSig();
	final int applyTemplates = cpg.addMethodref(className,
						    _functionName,
						    signature);
	il.append(new INVOKEVIRTUAL(applyTemplates));
    }

}
