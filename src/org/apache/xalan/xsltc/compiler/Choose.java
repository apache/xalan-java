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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class Choose extends Instruction {

    /**
     * Display the element contents (a lot of when's and an otherwise)
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Choose");
	indent(indent + IndentIncrement);
	displayContents(indent + IndentIncrement);
    }
	
    /**
     * Translate this Choose element. Generate a test-chain for the various
     * <xsl:when> elements and default to the <xsl:otherwise> if present.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Vector whenElements = new Vector();
	Otherwise otherwise = null;
	Enumeration elements = elements();

	// These two are for reporting errors only
	ErrorMsg error = null;
	final int line = getLineNumber();

	// Traverse all child nodes - must be either When or Otherwise
	while (elements.hasMoreElements()) {
	    Object element = elements.nextElement();
	    // Add a When child element
	    if (element instanceof When) {
		whenElements.addElement(element);
	    }
	    // Add an Otherwise child element
	    else if (element instanceof Otherwise) {
		if (otherwise == null) {
		    otherwise = (Otherwise)element;
		}
		else {
		    error = new ErrorMsg(ErrorMsg.MULTIPLE_OTHERWISE_ERR, this);
		    getParser().reportError(Constants.ERROR, error);
		}
	    }
	    else if (element instanceof Text) {
		((Text)element).ignore();
	    }
	    // It is an error if we find some other element here
	    else {
		error = new ErrorMsg(ErrorMsg.WHEN_ELEMENT_ERR, this);
		getParser().reportError(Constants.ERROR, error);
	    }
	}

	// Make sure that there is at least one <xsl:when> element
	if (whenElements.size() == 0) {
	    error = new ErrorMsg(ErrorMsg.MISSING_WHEN_ERR, this);
	    getParser().reportError(Constants.ERROR, error);
	    return;
	}

	InstructionList il = methodGen.getInstructionList();

	// next element will hold a handle to the beginning of next
	// When/Otherwise if test on current When fails
	BranchHandle nextElement = null;
	Vector exitHandles = new Vector();
	InstructionHandle exit = null;

	Enumeration whens = whenElements.elements();
	while (whens.hasMoreElements()) {
	    final When when = (When)whens.nextElement();
	    final Expression test = when.getTest();

	    InstructionHandle truec = il.getEnd();

	    if (nextElement != null) 
		nextElement.setTarget(il.append(NOP));
	    test.translateDesynthesized(classGen, methodGen);

	    if (test instanceof FunctionCall) {
		FunctionCall call = (FunctionCall)test;
		try {
		    Type type = call.typeCheck(getParser().getSymbolTable());
		    if (type != Type.Boolean) {
			test._falseList.add(il.append(new IFEQ(null)));
		    }
		}
		catch (TypeCheckError e) { 
		    // handled later!
		}
	    }
	    // remember end of condition
	    truec = il.getEnd();

	    // The When object should be ignored completely in case it tests
	    // for the support of a non-available element
	    if (!when.ignore()) when.translateContents(classGen, methodGen);

	    // goto exit after executing the body of when
	    exitHandles.addElement(il.append(new GOTO(null)));
	    if (whens.hasMoreElements() || otherwise != null) {
		nextElement = il.append(new GOTO(null));
		test.backPatchFalseList(nextElement);
	    }
	    else
		test.backPatchFalseList(exit = il.append(NOP));
	    test.backPatchTrueList(truec.getNext());
	}
	
	// Translate any <xsl:otherwise> element
	if (otherwise != null) {
	    nextElement.setTarget(il.append(NOP));
	    otherwise.translateContents(classGen, methodGen);
	    exit = il.append(NOP);
	}

	// now that end is known set targets of exit gotos
	Enumeration exitGotos = exitHandles.elements();
	while (exitGotos.hasMoreElements()) {
	    BranchHandle gotoExit = (BranchHandle)exitGotos.nextElement();
	    gotoExit.setTarget(exit);
	}
    }
}
