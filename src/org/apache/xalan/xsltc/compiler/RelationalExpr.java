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
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.runtime.Operators;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class RelationalExpr extends Expression implements Operators {
    private int _op;
    private Expression _left, _right;
		
    public RelationalExpr(int op, Expression left, Expression right) {
	_op = op;
	(_left = left).setParent(this);
	(_right = right).setParent(this);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_left.setParser(parser);
	_right.setParser(parser);
    }

    /**
     * Returns true if this expressions contains a call to position(). This is
     * needed for context changes in node steps containing multiple predicates.
     */
    public boolean hasPositionCall() {
	if (_left.hasPositionCall()) return true;
	if (_right.hasPositionCall()) return true;
	return false;
    }

    public boolean hasReferenceArgs() {
	return _left.getType() instanceof ReferenceType ||
	    _right.getType() instanceof ReferenceType;
    }

    public boolean hasNodeArgs() {
	return _left.getType() instanceof NodeType ||
	    _right.getType() instanceof NodeType;
    }

    public boolean hasNodeSetArgs() {
	return _left.getType() instanceof NodeSetType ||
	    _right.getType() instanceof NodeSetType;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	Type tleft = _left.typeCheck(stable); 
	Type tright = _right.typeCheck(stable);

	//bug fix # 2838, cast to reals if both are result tree fragments
	if (tleft instanceof ResultTreeType &&
	    tright instanceof ResultTreeType ) 
  	{
	    _right = new CastExpr(_right, Type.Real);
	    _left = new CastExpr(_left, Type.Real);
	    return _type = Type.Boolean; 
	}

	// If one is of reference type, then convert the other too
	if (hasReferenceArgs()) {
	    Type type = null;
	    Type typeL = null;
	    Type typeR = null;
	    if (tleft instanceof ReferenceType) {
		if (_left instanceof VariableRefBase) {
		    VariableRefBase ref = (VariableRefBase)_left;
		    VariableBase var = ref.getVariable();
		    typeL = var.getType();
		}
	    }
	    if (tright instanceof ReferenceType) {
		if (_right instanceof VariableRefBase) {
		    VariableRefBase ref = (VariableRefBase)_right;
		    VariableBase var = ref.getVariable();
		    typeR = var.getType();
		}
	    }
	    // bug fix # 2838 
	    if (typeL == null)
		type = typeR;
	    else if (typeR == null)
		type = typeL;
	    else {
		type = Type.Real;
	    }
	    if (type == null) type = Type.Real;

	    _right = new CastExpr(_right, type);
            _left = new CastExpr(_left, type);
	    return _type = Type.Boolean;
	}

	if (hasNodeSetArgs()) {
	    // Ensure that the node-set is the left argument
	    if (tright instanceof NodeSetType) {
		final Expression temp = _right; _right = _left; _left = temp;
		_op = (_op == Operators.GT) ? Operators.LT :
		    (_op == Operators.LT) ? Operators.GT :
		    (_op == Operators.GE) ? Operators.LE : Operators.GE;
		tright = _right.getType();
	    }

	    // Promote nodes to node sets
	    if (tright instanceof NodeType) {
		_right = new CastExpr(_right, Type.NodeSet);
	    }
	    // Promote integer to doubles to have fewer compares
	    if (tright instanceof IntType) {
		_right = new CastExpr(_right, Type.Real);
	    }
	    // Promote result-trees to strings
	    if (tright instanceof ResultTreeType) {
		_right = new CastExpr(_right, Type.String);
	    }
	    return _type = Type.Boolean;
	}

	// In the node-boolean case, convert node to boolean first
	if (hasNodeArgs()) {
	    if (tleft instanceof BooleanType) {
		_right = new CastExpr(_right, Type.Boolean);
		tright = Type.Boolean;
	    }
	    if (tright instanceof BooleanType) {
		_left = new CastExpr(_left, Type.Boolean);
		tleft = Type.Boolean;
	    }
	}

	// Lookup the table of primops to find the best match
	MethodType ptype = lookupPrimop(stable, Operators.names[_op],
					new MethodType(Type.Void, 
						       tleft, tright)); 

	if (ptype != null) {
	    Type arg1 = (Type) ptype.argsType().elementAt(0);
	    if (!arg1.identicalTo(tleft)) {
		_left = new CastExpr(_left, arg1);
	    }
	    Type arg2 = (Type) ptype.argsType().elementAt(1);
	    if (!arg2.identicalTo(tright)) {
		_right = new CastExpr(_right, arg1);				
	    }
	    return _type = ptype.resultType();
	}
	throw new TypeCheckError(this);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	if (hasNodeSetArgs() || hasReferenceArgs()) {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();

	    // Call compare() from the BasisLibrary
	    _left.translate(classGen, methodGen);
	    _left.startResetIterator(classGen, methodGen);
	    _right.translate(classGen, methodGen);
	    _right.startResetIterator(classGen, methodGen);

	    il.append(new PUSH(cpg, _op));
	    il.append(methodGen.loadContextNode());
	    il.append(methodGen.loadDOM());

	    int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "compare",
					 "("
					 + _left.getType().toSignature() 
					 + _right.getType().toSignature()
					 + "I"
					 + NODE_SIG
					 + DOM_INTF_SIG
					 + ")Z");
	    il.append(new INVOKESTATIC(index));
	}
	else {
	    translateDesynthesized(classGen, methodGen);
	    synthesize(classGen, methodGen);
	}
    }

    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) {
	if (hasNodeSetArgs() || hasReferenceArgs()) {
	    translate(classGen, methodGen);
	    desynthesize(classGen, methodGen);
	}
	else {
	    BranchInstruction bi = null;
	    final InstructionList il = methodGen.getInstructionList();

	    _left.translate(classGen, methodGen);
	    _right.translate(classGen, methodGen);

	    // TODO: optimize if one of the args is 0

	    boolean tozero = false;
	    Type tleft = _left.getType(); 

	    if (tleft instanceof RealType) {
		il.append(tleft.CMP(_op == LT || _op == LE));
		tleft = Type.Int;
		tozero = true;
	    }

	    switch (_op) {
	    case LT:
		bi = tleft.GE(tozero);	
		break;
		
	    case GT:
		bi = tleft.LE(tozero);
		break;
		
	    case LE:
		bi = tleft.GT(tozero);
		break;
		
	    case GE:
		bi = tleft.LT(tozero);
		break;
		
	    default:
		ErrorMsg msg = new ErrorMsg(ErrorMsg.ILLEGAL_RELAT_OP_ERR,this);
		getParser().reportError(Constants.FATAL, msg);
	    }

	    _falseList.add(il.append(bi));		// must be backpatched
	}
    }

    public String toString() {
	return Operators.names[_op] + '(' + _left + ", " + _right + ')';
    }
}
