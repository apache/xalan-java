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

final class LogicalExpr extends Expression {
    public static final int OR  = 0;
    public static final int AND = 1;
	
    private final int _op;
    private Expression _left, _right;

    private static final String[] Ops = {
	"or", "and"
    };

    public int getOp() {
	return(_op);
    }

    public LogicalExpr(int op, Expression left, Expression right) {
	_op = op;
	(_left = left).setParent(this);
	(_right = right).setParent(this);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_left.setParser(parser);
	_right.setParser(parser);
    }
    
    public String toString() {
	return Ops[_op] + '(' + _left + ", " + _right + ')';
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	Type tleft = _left.typeCheck(stable); 
	Type tright = _right.typeCheck(stable);

	MethodType ptype = lookupPrimop(stable,
					getParser().getQName(Ops[_op]),
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
	translateDesynthesized(classGen, methodGen);
	synthesize(classGen, methodGen);
    }

    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();
	if (_op == AND) {
	    _left.translateDesynthesized(classGen, methodGen);
	    if ((_left instanceof FunctionCall) &&
		(!(_left instanceof ContainsCall)))
		_falseList.add(il.append(new IFEQ(null)));
	    _right.translateDesynthesized(classGen, methodGen);
	    if ((_right instanceof FunctionCall) &&
		(!(_right instanceof ContainsCall)))
		_falseList.add(il.append(new IFEQ(null)));
	    _trueList.append(_right._trueList.append(_left._trueList));
	    _falseList.append(_right._falseList.append(_left._falseList));
	} 
	else {		// _op == OR
	    _left.translateDesynthesized(classGen, methodGen);
	    if ((_left instanceof FunctionCall) &&
		(!(_left instanceof ContainsCall)))
		_falseList.add(il.append(new IFEQ(null)));
	    InstructionHandle ih = il.append(new GOTO(null));
	    _right.translateDesynthesized(classGen, methodGen);
	    if ((_right instanceof FunctionCall) &&
		(!(_right instanceof ContainsCall)))
		_falseList.add(il.append(new IFEQ(null)));

	    _left._trueList.backPatch(ih);
	    _left._falseList.backPatch(ih.getNext());
			
	    _falseList.append(_right._falseList);
	    _trueList.add(ih).append(_right._trueList);
	}
    }
}
