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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;

final class ProcessingInstructionPattern extends StepPattern {

    private String _name = null;
    private boolean _typeChecked = false;

    /**
     * Handles calls with no parameter (current node is implicit parameter).
     */
    public ProcessingInstructionPattern(String name) {
	super(Axis.CHILD, DOM.PROCESSING_INSTRUCTION, null);
	_name = name;
	//if (_name.equals("*")) _typeChecked = true; no wildcard allowed!
    }

    /**
     *
     */
    public String toString() {
	if (_predicates == null)
	    return "processing-instruction("+_name+")";
	else
	    return "processing-instruction("+_name+")"+_predicates;
    }

    public void reduceKernelPattern() {
	_typeChecked = true;
    }

    public boolean isWildcard() {
	return false;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (hasPredicates()) {
	    // Type check all the predicates (e -> position() = e)
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Predicate pred = (Predicate)_predicates.elementAt(i);
		pred.typeCheck(stable);
	    }
	}
	return Type.NodeSet;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// context node is on the stack
	int gname = cpg.addInterfaceMethodref(DOM_INTF,
					      "getNodeName",
					      "(I)Ljava/lang/String;");
	int cmp = cpg.addMethodref(STRING_CLASS,
				   "equals", "(Ljava/lang/Object;)Z");

	// Push current node on the stack
	il.append(methodGen.loadCurrentNode());
	il.append(SWAP);

	// Overwrite current node with matching node
	il.append(methodGen.storeCurrentNode());

	// If pattern not reduced then check kernel
	if (!_typeChecked) {
	    il.append(methodGen.loadCurrentNode());
	    final int getType = cpg.addInterfaceMethodref(DOM_INTF,
							  "getType", "(I)I");
	    il.append(methodGen.loadDOM());
	    il.append(methodGen.loadCurrentNode());
	    il.append(new INVOKEINTERFACE(getType, 2));
	    il.append(new PUSH(cpg, DOM.PROCESSING_INSTRUCTION));
	    _falseList.add(il.append(new IF_ICMPEQ(null)));
	}

	// Load the requested processing instruction name
	il.append(new PUSH(cpg, _name));
	// Load the current processing instruction's name
	il.append(methodGen.loadDOM());
	il.append(methodGen.loadCurrentNode());
	il.append(new INVOKEINTERFACE(gname, 2));
	// Compare the two strings
	il.append(new INVOKEVIRTUAL(cmp));
	_falseList.add(il.append(new IFEQ(null)));
		
	// Compile the expressions within the predicates
	if (hasPredicates()) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		Predicate pred = (Predicate)_predicates.elementAt(i);
		Expression exp = pred.getExpr();
		exp.translateDesynthesized(classGen, methodGen);
		_trueList.append(exp._trueList);
		_falseList.append(exp._falseList);
	    }
	}

	// Backpatch true list and restore current iterator/node
	InstructionHandle restore;
	restore = il.append(methodGen.storeCurrentNode());
	backPatchTrueList(restore);
	BranchHandle skipFalse = il.append(new GOTO(null));

	// Backpatch false list and restore current iterator/node
	restore = il.append(methodGen.storeCurrentNode());
	backPatchFalseList(restore);
	_falseList.add(il.append(new GOTO(null)));

	// True list falls through
	skipFalse.setTarget(il.append(NOP));
    }
}
