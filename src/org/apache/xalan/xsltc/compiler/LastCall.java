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
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;

final class LastCall extends FunctionCall {

    private int _type = -1;

    public LastCall(QName fname) {
	super(fname);
    }

    public LastCall(QName fname, int type) {
	this(fname);
	_type = type;
    }

    public boolean hasPositionCall() {
	return true;
    }

    public boolean hasLastCall() {
	return true;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	final InstructionList il = methodGen.getInstructionList();
	final ConstantPoolGen cpg = classGen.getConstantPool();

	boolean lastChild = false;

	// If we're a part of an pattern's predicate we want to know what
	// type of node we want to be looking for (not just any).
	if (getParent() instanceof Expression) {
	    if (getParent().getParent() instanceof Predicate) {
		Predicate pred = (Predicate)getParent().getParent();
		_type = pred.getPosType();
		if ((_type==DOM.ELEMENT) || (_type==DOM.ATTRIBUTE)) _type = -1;
	    }
	}

	// If we're a part of a step-type expression we want the last of the
	// current node's children and not the last in the current iterator.
	if (getParent() instanceof Predicate) {
	    _type = ((Predicate)getParent()).getPosType();
	    if ((_type==DOM.ELEMENT) || (_type==DOM.ATTRIBUTE)) _type = -1;
	    if (getParent().getParent() instanceof Step) {
		lastChild = true;
	    }
	}

	if (methodGen instanceof CompareGenerator) {
	    il.append(((CompareGenerator)methodGen).loadLastNode());
	}
	else if (classGen.isExternal()) {
	    il.append(new ILOAD(LAST_INDEX));
	}
	else if (_type == -1) {
	    final int last = cpg.addInterfaceMethodref(NODE_ITERATOR,
						       "getLast", 
						       "()I");
	    final int git = cpg.addInterfaceMethodref(DOM_INTF,
						      "getTypedAxisIterator", 
						      "(II)"+NODE_ITERATOR_SIG);
	    final int start = cpg.addInterfaceMethodref(NODE_ITERATOR,
							"setStartNode", 
							"(I)"+
							NODE_ITERATOR_SIG);
	    if (lastChild) {
		il.append(methodGen.loadDOM());
		il.append(new PUSH(cpg, Axis.CHILD));
		il.append(new PUSH(cpg, DOM.ELEMENT));
		il.append(new INVOKEINTERFACE(git, 3));
		il.append(methodGen.loadCurrentNode());
		il.append(new INVOKEINTERFACE(start, 2));
	    }
	    else {
		il.append(methodGen.loadIterator());
	    }
	    il.append(new INVOKEINTERFACE(last, 1));
	}
	else {
	    // public int getTypedLast(int type, int node) {
	    final int last = cpg.addInterfaceMethodref(DOM_INTF,
						       "getTypedLast",
						       "(II)I");
	    il.append(methodGen.loadDOM());
	    il.append(new PUSH(cpg, _type));
	    il.append(methodGen.loadContextNode());
	    il.append(new INVOKEINTERFACE(last, 3));

	}
    }
}
