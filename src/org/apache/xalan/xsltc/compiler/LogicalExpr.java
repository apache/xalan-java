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

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class LogicalExpr extends Expression {

    public static final int OR  = 0;
    public static final int AND = 1;
	
    private final int  _op;     // operator
    private Expression _left;   // first operand
    private Expression _right;  // second operand

    private static final String[] Ops = { "or", "and" };

    /**
     * Creates a new logical expression - either OR or AND. Note that the
     * left- and right-hand side expressions can also be logical expressions,
     * thus creating logical trees representing structures such as
     * (a and (b or c) and d), etc...
     */
    public LogicalExpr(int op, Expression left, Expression right) {
	_op = op;
	(_left = left).setParent(this);
	(_right = right).setParent(this);
    }

    /**
     * Returns true if this expressions contains a call to position(). This is
     * needed for context changes in node steps containing multiple predicates.
     */
    public boolean hasPositionCall() {
	return (_left.hasPositionCall() || _right.hasPositionCall());
    }

    /**
     * Returns an object representing the compile-time evaluation 
     * of an expression. We are only using this for function-available
     * and element-available at this time.
     */
    public Object evaluateAtCompileTime() {
	final Object leftb = _left.evaluateAtCompileTime();
	final Object rightb = _right.evaluateAtCompileTime();

	// Return null if we can't evaluate at compile time
	if (leftb == null || rightb == null) {
	    return null;
	}

	if (_op == AND) {
	    return (leftb == Boolean.TRUE && rightb == Boolean.TRUE) ?
		Boolean.TRUE : Boolean.FALSE;
	}
	else {
	    return (leftb == Boolean.TRUE || rightb == Boolean.TRUE) ?
		Boolean.TRUE : Boolean.FALSE;
	}
    }

    /**
     * Returns this logical expression's operator - OR or AND represented
     * by 0 and 1 respectively.
     */
    public int getOp() {
	return(_op);
    }

    /**
     * Override the SyntaxTreeNode.setParser() method to make sure that the
     * parser is set for sub-expressions
     */
    public void setParser(Parser parser) {
	super.setParser(parser);
	_left.setParser(parser);
	_right.setParser(parser);
    }

    /**
     * Returns a string describing this expression
     */
    public String toString() {
	return Ops[_op] + '(' + _left + ", " + _right + ')';
    }

    /**
     * Type-check this expression, and possibly child expressions.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	// Get the left and right operand types
	Type tleft = _left.typeCheck(stable); 
	Type tright = _right.typeCheck(stable);

	// Check if the operator supports the two operand types
	MethodType wantType = new MethodType(Type.Void, tleft, tright);
	MethodType haveType = lookupPrimop(stable, Ops[_op], wantType);

	// Yes, the operation is supported
	if (haveType != null) {
	    // Check if left-hand side operand must be type casted
	    Type arg1 = (Type)haveType.argsType().elementAt(0);
	    if (!arg1.identicalTo(tleft))
		_left = new CastExpr(_left, arg1);
	    // Check if right-hand side operand must be type casted
	    Type arg2 = (Type) haveType.argsType().elementAt(1);
	    if (!arg2.identicalTo(tright))
		_right = new CastExpr(_right, arg1);
	    // Return the result type for the operator we will use
	    return _type = haveType.resultType();
	}
	throw new TypeCheckError(this);
    }

    /**
     * Compile the expression - leave boolean expression on stack
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	translateDesynthesized(classGen, methodGen);
	synthesize(classGen, methodGen);
    }

    /**
     * Compile expression and update true/false-lists
     */
    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) {

	final InstructionList il = methodGen.getInstructionList();
	final SyntaxTreeNode parent = getParent();

	// Compile AND-expression
	if (_op == AND) {

	    // Translate left hand side - must be true
	    _left.translateDesynthesized(classGen, methodGen);

	    // Need this for chaining any OR-expression children
	    InstructionHandle middle = il.append(NOP);

	    // Translate left right side - must be true
	    _right.translateDesynthesized(classGen, methodGen);

	    // Need this for chaining any OR-expression children
	    InstructionHandle after = il.append(NOP);

	    // Append child expression false-lists to our false-list
	    _falseList.append(_right._falseList.append(_left._falseList));

	    // Special case for OR-expression as a left child of AND.
	    // The true-list of OR must point to second clause of AND.
	    if ((_left instanceof LogicalExpr) &&
		(((LogicalExpr)_left).getOp() == OR)) {
		_left.backPatchTrueList(middle);
	    }
	    else if (_left instanceof NotCall) {
		_left.backPatchTrueList(middle);
	    }
	    else {
		_trueList.append(_left._trueList);
	    }

	    // Special case for OR-expression as a right child of AND
	    // The true-list of OR must point to true-list of AND.
	    if ((_right instanceof LogicalExpr) &&
		(((LogicalExpr)_right).getOp() == OR)) {
		_right.backPatchTrueList(after);
	    }
	    else if (_right instanceof NotCall) {
		_right.backPatchTrueList(after);
	    }
	    else {
		_trueList.append(_right._trueList);
	    }
	} 
	// Compile OR-expression
	else {
	    // Translate left-hand side expression and produce true/false list
	    _left.translateDesynthesized(classGen, methodGen);

	    // This GOTO is used to skip over the code for the last test
	    // in the case where the the first test succeeds
	    InstructionHandle ih = il.append(new GOTO(null));

	    // Translate right-hand side expression and produce true/false list
	    _right.translateDesynthesized(classGen, methodGen);

	    _left._trueList.backPatch(ih);
	    _left._falseList.backPatch(ih.getNext());
			
	    _falseList.append(_right._falseList);
	    _trueList.add(ih).append(_right._trueList);
	}
    }
}
