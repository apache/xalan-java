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

import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class CastExpr extends Expression {
    private final Expression _left;

    /**
     * Legal conversions between internal types.
     */
    static private MultiHashtable InternalTypeMap = new MultiHashtable();

    static {
	// Possible type conversions between internal types
	InternalTypeMap.put(Type.Boolean, Type.Boolean);
	InternalTypeMap.put(Type.Boolean, Type.Real);
	InternalTypeMap.put(Type.Boolean, Type.String);
	InternalTypeMap.put(Type.Boolean, Type.Reference);

	InternalTypeMap.put(Type.Real, Type.Real);
	InternalTypeMap.put(Type.Real, Type.Int);
	InternalTypeMap.put(Type.Real, Type.Boolean);
	InternalTypeMap.put(Type.Real, Type.String);
	InternalTypeMap.put(Type.Real, Type.Reference);

	InternalTypeMap.put(Type.Int, Type.Int);
	InternalTypeMap.put(Type.Int, Type.Real);
	InternalTypeMap.put(Type.Int, Type.Boolean);
	InternalTypeMap.put(Type.Int, Type.String);
	InternalTypeMap.put(Type.Int, Type.Reference);

	InternalTypeMap.put(Type.String, Type.String);
	InternalTypeMap.put(Type.String, Type.Boolean);
	InternalTypeMap.put(Type.String, Type.Real);
	InternalTypeMap.put(Type.String, Type.Reference);

	InternalTypeMap.put(Type.NodeSet, Type.NodeSet);
	InternalTypeMap.put(Type.NodeSet, Type.Boolean);
	InternalTypeMap.put(Type.NodeSet, Type.Real);
	InternalTypeMap.put(Type.NodeSet, Type.String);
	InternalTypeMap.put(Type.NodeSet, Type.Node);
	InternalTypeMap.put(Type.NodeSet, Type.Reference);
	InternalTypeMap.put(Type.NodeSet, Type.Object);

	InternalTypeMap.put(Type.Node, Type.Node);
	InternalTypeMap.put(Type.Node, Type.Boolean);
	InternalTypeMap.put(Type.Node, Type.Real);
	InternalTypeMap.put(Type.Node, Type.String);
	InternalTypeMap.put(Type.Node, Type.NodeSet);
	InternalTypeMap.put(Type.Node, Type.Reference);
	InternalTypeMap.put(Type.Node, Type.Object);

	InternalTypeMap.put(Type.ResultTree, Type.ResultTree);
	InternalTypeMap.put(Type.ResultTree, Type.Boolean);
	InternalTypeMap.put(Type.ResultTree, Type.Real);
	InternalTypeMap.put(Type.ResultTree, Type.String);
	InternalTypeMap.put(Type.ResultTree, Type.NodeSet);
	InternalTypeMap.put(Type.ResultTree, Type.Reference);
	InternalTypeMap.put(Type.ResultTree, Type.Object);

	InternalTypeMap.put(Type.Reference, Type.Reference);
	InternalTypeMap.put(Type.Reference, Type.Boolean);
	InternalTypeMap.put(Type.Reference, Type.Int);
	InternalTypeMap.put(Type.Reference, Type.Real);
	InternalTypeMap.put(Type.Reference, Type.String);
	InternalTypeMap.put(Type.Reference, Type.Node);
	InternalTypeMap.put(Type.Reference, Type.NodeSet);
	InternalTypeMap.put(Type.Reference, Type.ResultTree);
	InternalTypeMap.put(Type.Reference, Type.Object);

	InternalTypeMap.put(Type.Object, Type.String);

	InternalTypeMap.put(Type.Void, Type.String);
    }

    private boolean _typeTest = false;

    /**
     * Construct a cast expression and check that the conversion is 
     * valid by calling typeCheck().
     */
    public CastExpr(Expression left, Type type) throws TypeCheckError {
	_left = left;
	_type = type;		// use inherited field

	if ((_left instanceof Step) && (_type == Type.Boolean)) {
	    Step step = (Step)_left;
	    if ((step.getAxis() == Axis.SELF) && (step.getNodeType() != -1)) 
		_typeTest = true;
	}
	
	// check if conversion is valid
	setParser(left.getParser());
	setParent(left.getParent());
	left.setParent(this);
	typeCheck(left.getParser().getSymbolTable());
    }
		
    public Expression getExpr() {
	return _left;
    }

    /**
     * Returns true if this expressions contains a call to position(). This is
     * needed for context changes in node steps containing multiple predicates.
     */
    public boolean hasPositionCall() {
	return(_left.hasPositionCall());
    }

    public boolean hasLastCall() {
	return(_left.hasLastCall());
    }

    public String toString() {
	return "cast(" + _left + ", " + _type + ")";
    }

    /**
     * Type checking a cast expression amounts to verifying that the  
     * type conversion is legal. Cast expressions are created during 
     * type checking, but typeCheck() is usually not called on them. 
     * As a result, this method is called from the constructor.
     */	
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	Type tleft = _left.getType();
	if (tleft == null) {
	    tleft = _left.typeCheck(stable);
	}
	if (tleft instanceof NodeType) {
	    tleft = Type.Node;	// multiple instances
	}
	else if (tleft instanceof ResultTreeType) {
	    tleft = Type.ResultTree; // multiple instances
	}
	if (InternalTypeMap.maps(tleft, _type) != null) {
	    return _type;
	}
	throw new TypeCheckError(this);	
    }

    public void translateDesynthesized(ClassGenerator classGen, 
				       MethodGenerator methodGen) {
	FlowList fl;
	final Type ltype = _left.getType();

	// This is a special case for the self:: axis. Instead of letting
	// the Step object create and iterator that we cast back to a single
	// node, we simply ask the DOM for the node type.
	if (_typeTest) {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();

	    final int idx = cpg.addInterfaceMethodref(DOM_INTF,
						      "getType", "(I)I");
	    il.append(new SIPUSH((short)((Step)_left).getNodeType()));
	    il.append(methodGen.loadDOM());
	    il.append(methodGen.loadContextNode());
	    il.append(new INVOKEINTERFACE(idx, 2));
	    _falseList.add(il.append(new IF_ICMPNE(null)));
	}
	else {

	    _left.translate(classGen, methodGen);
	    if (_type != ltype) {
		_left.startResetIterator(classGen, methodGen);
		if (_type instanceof BooleanType) {
		    fl = ltype.translateToDesynthesized(classGen, methodGen,
							_type);
		    if (fl != null) {
			_falseList.append(fl);
		    }
		}
		else {
		    ltype.translateTo(classGen, methodGen, _type);	
		}
	    }
	}
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Type ltype = _left.getType();
	_left.translate(classGen, methodGen);
	if (_type.identicalTo(ltype) == false) {
	    _left.startResetIterator(classGen, methodGen);
	    ltype.translateTo(classGen, methodGen, _type);
	}
    }
}
