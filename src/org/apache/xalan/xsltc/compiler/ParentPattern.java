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
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class ParentPattern extends RelativePathPattern {
    private final Pattern _left;
    private final RelativePathPattern _right;
		
    public ParentPattern(Pattern left, RelativePathPattern right) {
	(_left = left).setParent(this);
	(_right = right).setParent(this);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_left.setParser(parser);
	_right.setParser(parser);
    }
    
    public boolean isWildcard() {
	return false;
    }
	
    public StepPattern getKernelPattern() {
	return _right.getKernelPattern();
    }
	
    public void reduceKernelPattern() {
	_right.reduceKernelPattern();
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	_left.typeCheck(stable);
	return _right.typeCheck(stable);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final LocalVariableGen local =
	    methodGen.addLocalVariable2("ppt", 
					Util.getJCRefType(NODE_SIG),
					il.getEnd());
	
	final org.apache.bcel.generic.Instruction loadLocal =
	    new ILOAD(local.getIndex());
	final org.apache.bcel.generic.Instruction storeLocal =
	    new ISTORE(local.getIndex());

	if (_right.isWildcard()) {
	    il.append(methodGen.loadDOM());
	    il.append(SWAP);
	}
	else if (_right instanceof StepPattern) {
	    il.append(DUP);
	    il.append(storeLocal);
	    
	    _right.translate(classGen, methodGen);
	    
	    il.append(methodGen.loadDOM());
	    local.setEnd(il.append(loadLocal));
	}
	else {
	    _right.translate(classGen, methodGen);

	    if (_right instanceof AncestorPattern) {
		il.append(methodGen.loadDOM());
		il.append(SWAP);
	    }
	}

	final int getParent = cpg.addInterfaceMethodref(DOM_INTF,
							GET_PARENT,
							GET_PARENT_SIG);
	il.append(new INVOKEINTERFACE(getParent, 2));

	final SyntaxTreeNode p = getParent();
	if (p == null || p instanceof Instruction || 
	    p instanceof TopLevelElement) 
	{
	    _left.translate(classGen, methodGen);
	}
	else {
	    il.append(DUP);
	    il.append(storeLocal);
	    
	    _left.translate(classGen, methodGen);

	    il.append(methodGen.loadDOM());
	    local.setEnd(il.append(loadLocal));
	}

	methodGen.removeLocalVariable(local);
	
	/*
	 * If _right is an ancestor pattern, backpatch _left false
	 * list to the loop that searches for more ancestors.
	 */
	if (_right instanceof AncestorPattern) {
	    final AncestorPattern ancestor = (AncestorPattern) _right;
	    _left.backPatchFalseList(ancestor.getLoopHandle());    // clears list
	}

	_trueList.append(_right._trueList.append(_left._trueList));
	_falseList.append(_right._falseList.append(_left._falseList));
    }

    public String toString() {
	return "Parent(" + _left + ", " + _right + ')';
    }
}
