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

import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class FilterParentPath extends Expression {

    private Expression _filterExpr;
    private Expression _path;
    private boolean _hasDescendantAxis = false;

    public FilterParentPath(Expression filterExpr, Expression path) {
	(_path = path).setParent(this);
	(_filterExpr = filterExpr).setParent(this);
    }
		
    public void setParser(Parser parser) {
	super.setParser(parser);
	_filterExpr.setParser(parser);
	_path.setParser(parser);
    }
    
    public String toString() {
	return "FilterParentPath(" + _filterExpr + ", " + _path + ')';
    }

    public void setDescendantAxis() {
	_hasDescendantAxis = true;
    }

    /**
     * Type check a FilterParentPath. If the filter is not a node-set add a 
     * cast to node-set only if it is of reference type. This type coercion is
     * needed for expressions like $x/LINE where $x is a parameter reference.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type ftype = _filterExpr.typeCheck(stable);
	if (ftype instanceof NodeSetType == false) {
	    if (ftype instanceof ReferenceType)  {
		_filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
	    }
	    /*
	    else if (ftype instanceof ResultTreeType)  {
		_filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
	    }
	    */
	    else if (ftype instanceof NodeType)  {
		_filterExpr = new CastExpr(_filterExpr, Type.NodeSet);
	    }
	    else {
		throw new TypeCheckError(this);
	    }
	}

	// Wrap single node path in a node set
	final Type ptype = _path.typeCheck(stable);
	if (!(ptype instanceof NodeSetType)) {
	    _path = new CastExpr(_path, Type.NodeSet);
	}

	return _type = Type.NodeSet;	
    }
	
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	// Create new StepIterator
	final int initSI = cpg.addMethodref(STEP_ITERATOR_CLASS,
					    "<init>",
					    "("
					    +NODE_ITERATOR_SIG
					    +NODE_ITERATOR_SIG
					    +")V");
	il.append(new NEW(cpg.addClass(STEP_ITERATOR_CLASS)));
	il.append(DUP);

	// Recursively compile 2 iterators
	_filterExpr.translate(classGen, methodGen);
	_path.translate(classGen, methodGen);

	// Initialize StepIterator with iterators from the stack
	il.append(new INVOKESPECIAL(initSI));

	// This is a special case for the //* path with or without predicates
        if (_hasDescendantAxis) {
	    final int incl = cpg.addMethodref(NODE_ITERATOR_BASE,
					      "includeSelf",
					      "()" + NODE_ITERATOR_SIG);
	    il.append(new INVOKEVIRTUAL(incl));
	}

	if (!(getParent() instanceof RelativeLocationPath) &&
	    !(getParent() instanceof FilterParentPath)) {
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
