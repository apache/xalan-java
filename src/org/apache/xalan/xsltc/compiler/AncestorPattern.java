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
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class AncestorPattern extends RelativePathPattern {
    private final Pattern _left;	// may be null
    private final RelativePathPattern _right;
		
    public AncestorPattern(RelativePathPattern right) {
	this(null, right);
    }

    public AncestorPattern(Pattern left, RelativePathPattern right) {
	_left = left;
	(_right = right).setParent(this);
	if (left != null) {
	    left.setParent(this);
	}
    }
	
    public void setParser(Parser parser) {
	super.setParser(parser);
	if (_left != null) {
	    _left.setParser(parser);
	}
	_right.setParser(parser);
    }
    
    public boolean isWildcard() {
	//!!! can be wildcard
	return false;
    }
	
    public StepPattern getKernelPattern() {
	return _right.getKernelPattern();
    }

    public void reduceKernelPattern() {
	_right.reduceKernelPattern();
    }
	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return _right.typeCheck(stable);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	InstructionHandle loop, eloop;
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final String DOM_CLASS = classGen.getDOMClass();
	final LocalVariableGen local =
	    methodGen.addLocalVariable2("app", 
					Util.getJCRefType(NODE_SIG),
					il.getEnd());

	final de.fub.bytecode.generic.Instruction loadLocal =
	    new ILOAD(local.getIndex());
	final de.fub.bytecode.generic.Instruction storeLocal =
	    new ISTORE(local.getIndex());

	if (_right instanceof StepPattern) {
	    il.append(DUP);
	    il.append(storeLocal);
	    _right.translate(classGen, methodGen);
	    il.append(methodGen.loadDOM());
	    il.append(loadLocal);
	}
	else {
	    _right.translate(classGen, methodGen);
	}

	if (_left != null) {
	    final int getParent = cpg.addMethodref(DOM_CLASS,
						   GET_PARENT,
						   GET_PARENT_SIG);
	    loop = il.append(new INVOKEVIRTUAL(getParent));
	    
	    il.append(DUP);
	    il.append(storeLocal);
	    _falseList.add(il.append(new IFEQ(null)));
	    il.append(loadLocal);

	    _left.translate(classGen, methodGen);


	    final SyntaxTreeNode p = getParent();
	    if (p instanceof Instruction || p instanceof TopLevelElement) {
		// do nothing
	    }
	    else {
		il.append(loadLocal);
	    }
	    final BranchHandle exit = il.append(new GOTO(null));
	    eloop = il.append(methodGen.loadDOM());
	    il.append(loadLocal);
	    local.setEnd(eloop);
	    il.append(new GOTO(loop));
	    exit.setTarget(il.append(NOP));
	    _left.backPatchFalseList(eloop);

	    _trueList.append(_left._trueList);	
	}
	else {
	    il.append(POP2);
	}
	methodGen.removeLocalVariable(local);

	_trueList.append(_right._trueList);
	_falseList.append(_right._falseList);
    }

    public String toString() {
	return "AncestorPattern(" + _left + ", " + _right + ')';
    }
}
