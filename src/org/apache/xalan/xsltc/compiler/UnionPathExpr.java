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

import java.util.Vector;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.compiler.util.Type;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class UnionPathExpr extends Expression {

    private final Expression _pathExpr;
    private final Expression _rest;
    private boolean _reverse = false;

    // linearization for top level UnionPathExprs
    private Expression[] _components;
    
    public UnionPathExpr(Expression pathExpr, Expression rest) {
	_pathExpr = pathExpr;
	_rest     = rest;
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	// find all expressions in this Union
	final Vector components = new Vector();
	flatten(components);
	final int size = components.size();
	_components = (Expression[])components.toArray(new Expression[size]);
	for (int i = 0; i < size; i++) {
	    _components[i].setParser(parser);
	    _components[i].setParent(this);
	    if (_components[i] instanceof Step) {
		final Step step = (Step)_components[i];
		final int axis = step.getAxis();
		final int type = step.getNodeType();
		// Put attribute iterators first
		if ((axis == Axis.ATTRIBUTE) || (type == DOM.ATTRIBUTE)) {
		    _components[i] = _components[0];
		    _components[0] = step;
		}
		// Check if the union contains a reverse iterator
		if (Axis.isReverse[axis]) _reverse = true;
	    }
	}
	// No need to reverse anything if another expression lies on top of this
	if (getParent() instanceof Expression) _reverse = false;
    }
    
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final int length = _components.length;
	for (int i = 0; i < length; i++) {
	    if (_components[i].typeCheck(stable) != Type.NodeSet) {
		_components[i] = new CastExpr(_components[i], Type.NodeSet);
	    }
	}
	return _type = Type.NodeSet;	
    }

    public String toString() {
	return "union(" + _pathExpr + ", " + _rest + ')';
    }
	
    private void flatten(Vector components) {
	components.addElement(_pathExpr);
	if (_rest != null) {
	    if (_rest instanceof UnionPathExpr) {
		((UnionPathExpr)_rest).flatten(components);
	    }
	    else {
		components.addElement(_rest);
	    }
	}
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	final int init = cpg.addMethodref(UNION_ITERATOR_CLASS,
					  "<init>",
					  "("+DOM_INTF_SIG+")V");
	final int iter = cpg.addMethodref(UNION_ITERATOR_CLASS,
					  ADD_ITERATOR,
					  ADD_ITERATOR_SIG);

	// Create the UnionIterator and leave it on the stack
	il.append(new NEW(cpg.addClass(UNION_ITERATOR_CLASS)));
	il.append(DUP);
	il.append(methodGen.loadDOM());
	il.append(new INVOKESPECIAL(init));

	// Add the various iterators to the UnionIterator
	final int length = _components.length;
	for (int i = 0; i < length; i++) {
	    _components[i].translate(classGen, methodGen);
	    il.append(new INVOKEVIRTUAL(iter));
	}

	// Order the iterator only if strictly needed
	if (_reverse) {
	    final int order = cpg.addInterfaceMethodref(DOM_INTF,
							ORDER_ITERATOR,
							ORDER_ITERATOR_SIG);
	    il.append(methodGen.loadDOM());
	    il.append(SWAP);
	    il.append(methodGen.loadContextNode());
	    il.append(new INVOKEINTERFACE(order, 3));

	}
    }
}
