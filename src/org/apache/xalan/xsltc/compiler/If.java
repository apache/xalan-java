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
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class If extends Instruction {

    private Expression _test;
    private boolean    _ignore = false;

    /**
     * Display the contents of this element
     */
    public void display(int indent) {
	indent(indent);
	Util.println("If");
	indent(indent + IndentIncrement);
	System.out.print("test ");
	Util.println(_test.toString());
	displayContents(indent + IndentIncrement);
    }

    /**
     * Parse the "test" expression and contents of this element.
     */
    public void parseContents(Parser parser) {
	// Parse the "test" expression
	_test = parser.parseExpression(this, "test", null);

        // Make sure required attribute(s) have been set
        if (_test.isDummy()) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "test");
	    return;
        }

	// Ignore xsl:if when test is false (function-available() and
	// element-available())
	Object result = _test.evaluateAtCompileTime();
	if (result != null && result instanceof Boolean) {
	    _ignore = !((Boolean) result).booleanValue();
	}

	parseChildren(parser);
    }

    /**
     * Type-check the "test" expression and contents of this element.
     * The contents will be ignored if we know the test will always fail.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	// Type-check the "test" expression
	if (_test.typeCheck(stable) instanceof BooleanType == false) {
	    _test = new CastExpr(_test, Type.Boolean);
	}
	// Type check the element contents
	if (!_ignore) {
	    typeCheckContents(stable);
	}
	return Type.Void;
    }

    /**
     * Translate the "test" expression and contents of this element.
     * The contents will be ignored if we know the test will always fail.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();
	_test.translateDesynthesized(classGen, methodGen);
	// remember end of condition
	final InstructionHandle truec = il.getEnd();
	if (!_ignore) {
	    translateContents(classGen, methodGen);
	}
	_test.backPatchFalseList(il.append(NOP));
	_test.backPatchTrueList(truec.getNext());
    }
}
