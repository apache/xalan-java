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
import org.apache.xalan.xsltc.DOM;

final class PositionCall extends FunctionCall {

    private int _type = -1;

    public PositionCall(QName fname) {
	super(fname);
    }

    public PositionCall(QName fname, int type) {
	this(fname);
	_type = type;
    }

    public boolean hasPositionCall() {
	return true;
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	final InstructionList il = methodGen.getInstructionList();

	SyntaxTreeNode parent = getParent();
	SyntaxTreeNode granny = parent.getParent();

	// If we're a part of an expression's predicate we want to know what
	// type of node we want to be looking for
	if ((parent instanceof Expression) && (granny instanceof Predicate)) {
	    _type = ((Predicate)granny).getPosType();
	}
	else {
	    while ((granny != null) && !(granny instanceof StepPattern)) {
		parent = granny;
		granny = granny.getParent();
	    }
	    if ((parent instanceof Predicate) &&
		(granny instanceof StepPattern)){
		_type = ((StepPattern)granny).getNodeType();
	    }
	}

	if (methodGen instanceof CompareGenerator) {
	    il.append(((CompareGenerator)methodGen).loadCurrentNode());
	}
	else if (methodGen instanceof TestGenerator) {
	    il.append(new ILOAD(POSITION_INDEX));
	}
	else if (_type == -1) {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final int getPosition = cpg.addInterfaceMethodref(NODE_ITERATOR,
							      "getPosition", 
							      "()I");
	    il.append(methodGen.loadIterator());
	    il.append(new INVOKEINTERFACE(getPosition, 1));
	}
	else {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    // public int getTypedPosition(int type, int node)
	    final int pos = cpg.addInterfaceMethodref(DOM_INTF,
						      "getTypedPosition",
						      "(II)I");
	    il.append(methodGen.loadDOM());
	    il.append(new PUSH(cpg, _type));
	    il.append(methodGen.loadContextNode());
	    il.append(new INVOKEINTERFACE(pos, 3));
	}
    }
}
