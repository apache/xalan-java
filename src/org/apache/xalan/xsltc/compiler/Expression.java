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
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.*;

import java.util.Vector;

import org.apache.bcel.generic.*;

abstract class Expression extends SyntaxTreeNode {
    /**
     * The type of this expression. It is set after calling 
     * <code>typeCheck()</code>.
     */
    protected Type _type;

    /**
     * True if this expression is of node-set type and its corresponding
     * iterator has been started or reset.
     */
    protected boolean _startReset = false;

    /**
     * Instruction handles that comprise the true list.
     */
    protected FlowList _trueList = new FlowList();

    /**
     * Instruction handles that comprise the false list.
     */
    protected FlowList _falseList = new FlowList();

    public Type getType() {
	return _type;
    }

    public abstract String toString();

    public boolean hasPositionCall() {
	return false;		// default should be 'false' for StepPattern
    }

    public boolean hasLastCall() {
	return false;
    }
		
    /**
     * Returns an object representing the compile-time evaluation 
     * of an expression. We are only using this for function-available
     * and element-available at this time.
     */
    public Object evaluateAtCompileTime() {
	return null;
    }

    /**
     * Type check all the children of this node.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return typeCheckContents(stable);
    }

    /**
     * Translate this node into JVM bytecodes.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	ErrorMsg msg = new ErrorMsg(ErrorMsg.NOT_IMPLEMENTED_ERR,
				    getClass(), this);
	getParser().reportError(FATAL, msg);
    }
	
    /**
     * Translate this node into a fresh instruction list.
     * The original instruction list is saved and restored.
     */
    public final InstructionList compile(ClassGenerator classGen,
					 MethodGenerator methodGen) {
	final InstructionList result, save = methodGen.getInstructionList();
	methodGen.setInstructionList(result = new InstructionList());
	translate(classGen, methodGen);
	methodGen.setInstructionList(save);
	return result;
    }

    /**
     * Redefined by expressions of type boolean that use flow lists.
     */
    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) {
	translate(classGen, methodGen);
	if (_type instanceof BooleanType) {
	    desynthesize(classGen, methodGen);
	}
    }

    /**
     * Expects an object on the stack and if this object can be proven
     * to be a node iterator then the iterator is reset or started
     * depending on the type of this expression.
     * If this expression is a var reference then the iterator 
     * is reset, otherwise it is started.
     */
    public void startResetIterator(ClassGenerator classGen,
				   MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_startReset) {
	    return;			// already started
	}
	_startReset = true;

	if (_type instanceof NodeSetType == false) {
	    return;		// nothing to do
	}

	if ( (this instanceof VariableRefBase) == false ) {
	    il.append(methodGen.loadContextNode());
	    il.append(methodGen.setStartNode());
	}
    }

    /**
     * Synthesize a boolean expression, i.e., either push a 0 or 1 onto the 
     * operand stack for the next statement to succeed. Returns the handle
     * of the instruction to be backpatched.
     */
    public void synthesize(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	_trueList.backPatch(il.append(ICONST_1));
	final BranchHandle truec = il.append(new GOTO_W(null));
	_falseList.backPatch(il.append(ICONST_0));
	truec.setTarget(il.append(NOP));
    }

    public void desynthesize(ClassGenerator classGen,
			     MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();
	_falseList.add(il.append(new IFEQ(null)));
    }

    public FlowList getFalseList() {
	return _falseList;
    }

    public FlowList getTrueList() {
	return _trueList;
    }

    public void backPatchFalseList(InstructionHandle ih) {
	_falseList.backPatch(ih);
    }

    public void backPatchTrueList(InstructionHandle ih) {
	_trueList.backPatch(ih);
    }

    /**
     * Search for a primop in the symbol table that matches the method type 
     * <code>ctype</code>. Two methods match if they have the same arity.
     * If a primop is overloaded then the "closest match" is returned. The
     * first entry in the vector of primops that has the right arity is 
     * considered to be the default one.
     */
    public MethodType lookupPrimop(SymbolTable stable, String op,
				   MethodType ctype) {
	MethodType result = null;
	final Vector primop = stable.lookupPrimop(op);
	if (primop != null) {
	    final int n = primop.size();
	    int minDistance = Integer.MAX_VALUE;
	    for (int i = 0; i < n; i++) {
		final MethodType ptype = (MethodType) primop.elementAt(i);
		// Skip if different arity
		if (ptype.argsCount() != ctype.argsCount()) {
		    continue;
		}
				
		// The first method with the right arity is the default
		if (result == null) {
		    result = ptype;		// default method
		}

		// Check if better than last one found
		final int distance = ctype.distanceTo(ptype);
		if (distance < minDistance) {
		    minDistance = distance;
		    result = ptype;
		}
	    }		
	}	
	return result;
    }	
}
