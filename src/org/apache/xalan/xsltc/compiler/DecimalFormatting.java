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

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.xalan.xsltc.runtime.AttributeList;
import org.apache.xalan.xsltc.compiler.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class DecimalFormatting extends TopLevelElement {

    private static final String DFS_CLASS = "java.text.DecimalFormatSymbols";
    private static final String DFS_SIG   = "Ljava/text/DecimalFormatSymbols;";

    private QName _name = null;

    /**
     * No type check needed for the <xsl:decimal-formatting/> element
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return Type.Void;
    }

    /**
     * Parse the name of the <xsl:decimal-formatting/> element
     */
    public void parseContents(Parser parser) {
	// Get the name of these decimal formatting symbols
	_name = parser.getQNameIgnoreDefaultNs(getAttribute("name"));
	if (_name == null) {
	    _name = parser.getQNameIgnoreDefaultNs(EMPTYSTRING);
	}

	// Check if a set of symbols has already been registered under this name
	SymbolTable stable = parser.getSymbolTable();
	if (stable.getDecimalFormatting(_name) != null) {
	    reportWarning(this, parser, ErrorMsg.SYMBOLS_REDEF_ERR,
		_name.toString());
	}
	else {
	    stable.addDecimalFormatting(_name, this);
	}
    }

    /**
     * This method is called when the constructor is compiled in
     * Stylesheet.compileConstructor() and not as the syntax tree is traversed.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();
	
	// DecimalFormatSymbols.<init>();
	final int init = cpg.addMethodref(DFS_CLASS, "<init>", "()V");

	// Push the format name on the stack for call to addDecimalFormat()
	il.append(classGen.loadTranslet());
	il.append(new PUSH(cpg, _name.toString()));

	// Manufacture a DecimalFormatSymbols on the stack
	// for call to addDecimalFormat()
	il.append(new NEW(cpg.addClass(DFS_CLASS)));
	il.append(DUP);
	il.append(new INVOKESPECIAL(init));

	String tmp = getAttribute("NaN");
	if ((tmp == null) || (tmp.equals(EMPTYSTRING))) {
	    int nan = cpg.addMethodref(DFS_CLASS,
				       "setNaN", "(Ljava/lang/String;)V");
	    il.append(DUP);
	    il.append(new PUSH(cpg, "NaN"));
	    il.append(new INVOKEVIRTUAL(nan));
	}

	tmp = getAttribute("infinity");
	if ((tmp == null) || (tmp.equals(EMPTYSTRING))) {
	    int inf = cpg.addMethodref(DFS_CLASS,
				       "setInfinity",
				       "(Ljava/lang/String;)V");
	    il.append(DUP);
	    il.append(new PUSH(cpg, "Infinity"));
	    il.append(new INVOKEVIRTUAL(inf));
	}
	    
	final int nAttributes = _attributes.getLength();
	for (int i = 0; i < nAttributes; i++) {
	    final String name = _attributes.getQName(i);
	    final String value = _attributes.getValue(i);

	    boolean valid = true;
	    int method = 0;

	    if (name.equals("decimal-separator")) {
		// DecimalFormatSymbols.setDecimalSeparator();
		method = cpg.addMethodref(DFS_CLASS,
					  "setDecimalSeparator", "(C)V");
	    }
	    else if (name.equals("grouping-separator")) {
		method =  cpg.addMethodref(DFS_CLASS,
					   "setGroupingSeparator", "(C)V");
	    }
	    else if (name.equals("minus-sign")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setMinusSign", "(C)V");
	    }
	    else if (name.equals("percent")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setPercent", "(C)V");
	    }
	    else if (name.equals("per-mille")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setPerMill", "(C)V");
	    }
	    else if (name.equals("zero-digit")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setZeroDigit", "(C)V");
	    }
	    else if (name.equals("digit")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setDigit", "(C)V");
	    }
	    else if (name.equals("pattern-separator")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setPatternSeparator", "(C)V");
	    }
	    else if (name.equals("NaN")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setNaN", "(Ljava/lang/String;)V");
	        il.append(DUP);
		il.append(new PUSH(cpg, value));
		il.append(new INVOKEVIRTUAL(method));
		valid = false;
	    }
	    else if (name.equals("infinity")) {
		method = cpg.addMethodref(DFS_CLASS,
					  "setInfinity",
					  "(Ljava/lang/String;)V");
	        il.append(DUP);
		il.append(new PUSH(cpg, value));
		il.append(new INVOKEVIRTUAL(method));
		valid = false;
	    }
	    else {
		valid = false;
	    }

	    if (valid) {
		il.append(DUP);
		il.append(new PUSH(cpg, value.charAt(0)));
		il.append(new INVOKEVIRTUAL(method));
	    }

	}

	final int put = cpg.addMethodref(TRANSLET_CLASS,
					 "addDecimalFormat",
					 "("+STRING_SIG+DFS_SIG+")V");
	il.append(new INVOKEVIRTUAL(put));
    }

    /**
     * Creates the default, nameless, DecimalFormat object in
     * AbstractTranslet's format_symbols hashtable.
     * This should be called for every stylesheet, and the entry
     * may be overridden by later nameless xsl:decimal-format instructions.
     */
    public static void translateDefaultDFS(ClassGenerator classGen,
					   MethodGenerator methodGen) {

	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();
	final int init = cpg.addMethodref(DFS_CLASS, "<init>", "()V");

	// Push the format name, which is empty, on the stack
	// for call to addDecimalFormat()
	il.append(classGen.loadTranslet());
	il.append(new PUSH(cpg, EMPTYSTRING));

	// Manufacture a DecimalFormatSymbols on the stack
	// for call to addDecimalFormat()
	il.append(new NEW(cpg.addClass(DFS_CLASS)));
	il.append(DUP);
	il.append(new INVOKESPECIAL(init));

	int nan = cpg.addMethodref(DFS_CLASS,
				   "setNaN", "(Ljava/lang/String;)V");
	il.append(DUP);
	il.append(new PUSH(cpg, "NaN"));
	il.append(new INVOKEVIRTUAL(nan));

	int inf = cpg.addMethodref(DFS_CLASS,
				   "setInfinity",
				   "(Ljava/lang/String;)V");
	il.append(DUP);
	il.append(new PUSH(cpg, "Infinity"));
	il.append(new INVOKEVIRTUAL(inf));

	final int put = cpg.addMethodref(TRANSLET_CLASS,
					 "addDecimalFormat",
					 "("+STRING_SIG+DFS_SIG+")V");
	il.append(new INVOKEVIRTUAL(put));
    }
}
