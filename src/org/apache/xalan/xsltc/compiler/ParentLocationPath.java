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

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class ParentLocationPath extends RelativeLocationPath {
    private Expression _step;
    private final RelativeLocationPath _path;
    private Type stype;
    private boolean _orderNodes = false;
    private boolean _axisMismatch = false;

    public ParentLocationPath(RelativeLocationPath path, Expression step) {
	_path = path;
	_step = step;
	_path.setParent(this);
	_step.setParent(this);

	if (_step instanceof Step) {
	    _axisMismatch = checkAxisMismatch();
	}
    }
		
    public void setAxis(int axis) {
	_path.setAxis(axis);
    }

    public int getAxis() {
	return _path.getAxis();
    }

    public RelativeLocationPath getPath() {
	return(_path);
    }

    public Expression getStep() {
	return(_step);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_step.setParser(parser);
	_path.setParser(parser);
    }
    
    public String toString() {
	return "ParentLocationPath(" + _path + ", " + _step + ')';
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	stype = _step.typeCheck(stable);
	_path.typeCheck(stable);

	if (_axisMismatch) enableNodeOrdering();

	return _type = Type.NodeSet;	
    }

    public void enableNodeOrdering() {
	SyntaxTreeNode parent = getParent();
	if (parent instanceof ParentLocationPath)
	    ((ParentLocationPath)parent).enableNodeOrdering();
	else {
	    _orderNodes = true;
	}
    }

    /**
     * This method is used to determine if this parent location path is a
     * combination of two step's with axes that will create duplicate or
     * unordered nodes.
     */
    public boolean checkAxisMismatch() {

	int left = _path.getAxis();
	int right = ((Step)_step).getAxis();

	if (((left == Axis.ANCESTOR) || (left == Axis.ANCESTORORSELF)) &&
	    ((right == Axis.CHILD) ||
	     (right == Axis.DESCENDANT) ||
	     (right == Axis.DESCENDANTORSELF) ||
	     (right == Axis.PARENT) ||
	     (right == Axis.PRECEDING) ||
	     (right == Axis.PRECEDINGSIBLING)))
	    return true;

	if ((left == Axis.CHILD) &&
	    (right == Axis.ANCESTOR) ||
	    (right == Axis.ANCESTORORSELF) ||
	    (right == Axis.PARENT) ||
	    (right == Axis.PRECEDING))
	    return true;

	if ((left == Axis.DESCENDANT) || (left == Axis.DESCENDANTORSELF))
	    return true;

	if (((left == Axis.FOLLOWING) || (left == Axis.FOLLOWINGSIBLING)) &&
	    ((right == Axis.FOLLOWING) ||
	     (right == Axis.PARENT) ||
	     (right == Axis.PRECEDING) ||
	     (right == Axis.PRECEDINGSIBLING)))
	    return true;

	if (((left == Axis.PRECEDING) || (left == Axis.PRECEDINGSIBLING)) &&
	    ((right == Axis.DESCENDANT) ||
	     (right == Axis.DESCENDANTORSELF) ||
	     (right == Axis.FOLLOWING) ||
	     (right == Axis.FOLLOWINGSIBLING) ||
	     (right == Axis.PARENT) ||
	     (right == Axis.PRECEDING) ||
	     (right == Axis.PRECEDINGSIBLING)))
	    return true;

	if ((right == Axis.FOLLOWING) && (left == Axis.CHILD)) {
	    // Special case for '@*/following::*' expressions. The resulting
	    // iterator is initialised with the parent's first child, and this
	    // can cause duplicates in the output if the parent has more than
	    // one attribute that matches the left step.
	    if (_path instanceof Step) {
		int type = ((Step)_path).getNodeType();
		if (type == DOM.ATTRIBUTE) return true;
	    }
	}

	return false;
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

	// Compile path iterator
	_path.translate(classGen, methodGen); // iterator on stack....
	_step.translate(classGen, methodGen);

	// Initialize StepIterator with iterators from the stack
	il.append(new INVOKESPECIAL(initSI));

	// This is a special case for the //* path with or without predicates
	Expression stp = _step;
	if (stp instanceof ParentLocationPath)
	    stp = ((ParentLocationPath)stp).getStep();

	if ((_path instanceof Step) && (stp instanceof Step)) {
	    final int path = ((Step)_path).getAxis();
	    final int step = ((Step)stp).getAxis();
	    if ((path == Axis.DESCENDANTORSELF && step == Axis.CHILD) ||
		(path == Axis.PRECEDING        && step == Axis.PARENT)) {
		final int incl = cpg.addMethodref(NODE_ITERATOR_BASE,
						  "includeSelf",
						  "()" + NODE_ITERATOR_SIG);
		il.append(new INVOKEVIRTUAL(incl));
	    }
	}

	/*
	 * If this pattern contains a sequence of descendant iterators we
	 * run the risk of returning the same node several times. We put
	 * a new iterator on top of the existing one to assure node order
	 * and prevent returning a single node multiple times.
	 */
	if (_orderNodes) {
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
