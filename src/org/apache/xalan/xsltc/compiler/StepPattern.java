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
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.Field;

import org.apache.xalan.xsltc.compiler.util.*;

class StepPattern extends RelativePathPattern {

    private static final int NO_CONTEXT = 0;
    private static final int SIMPLE_CONTEXT = 1;
    private static final int GENERAL_CONTEXT = 2;

    protected final int _axis;
    protected final int _nodeType;
    protected Vector _predicates;

    private Step    _step = null;
    private boolean _isEpsilon = false;
    private int     _contextCase;

    public StepPattern(int axis, int nodeType, Vector predicates) {
	_axis = axis;
	_nodeType = nodeType;
	_predicates = predicates;
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	if (_predicates != null) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Predicate exp = (Predicate)_predicates.elementAt(i);
		exp.setParser(parser);
		exp.setParent(this);
	    }
	}
    }

    public int getNodeType() {
	return _nodeType;
    }
    
    public StepPattern getKernelPattern() {
	return this;
    }
	
    public boolean isWildcard() {
	return _isEpsilon && hasPredicates() == false;
    }

    public StepPattern setPredicates(Vector predicates) {
	_predicates = predicates;
	return(this);
    }
    
    protected boolean hasPredicates() {
	return _predicates != null && _predicates.size() > 0;
    }

    public double getDefaultPriority() {
	if (hasPredicates()) {
	    return 0.5;
	}
	else {
	    switch(_nodeType) {
	    case -1:
		return -0.5;	// node()
	    case 0:
		return 0.0;
	    default:
		return (_nodeType >= NodeTest.GTYPE) ? 0.0 : -0.5;
	    }
	}
    }
    
    public void reduceKernelPattern() {
	_isEpsilon = true;
    }
	
    public String toString() {
	final StringBuffer buffer = new StringBuffer("stepPattern(\"");
	buffer.append(Axis.names[_axis])
	    .append("\", ")
	    .append(_isEpsilon ? 
			("epsilon{" + Integer.toString(_nodeType) + "}") :
			 Integer.toString(_nodeType));
	if (_predicates != null)
	    buffer.append(", ").append(_predicates.toString());
	return buffer.append(')').toString();
    }
    
    private int analyzeCases() {
	boolean noContext = true;
	final int n = _predicates.size();

	for (int i = 0; i < n && noContext; i++) {
	    final Predicate pred = (Predicate)_predicates.elementAt(i);
	    if (pred.getExpr().hasPositionCall()) {
		noContext = false;
	    }
	}

	if (noContext) {
	    return NO_CONTEXT;
	}
	else if (n == 1) {
	    return SIMPLE_CONTEXT;
	}
	return GENERAL_CONTEXT;
    }

    private String getNextFieldName() {
	return  "__step_pattern_iter_" + getXSLTC().nextStepPatternSerial();
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (hasPredicates()) {
	    // Type check all the predicates (e -> position() = e)
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Predicate pred = (Predicate)_predicates.elementAt(i);
		pred.typeCheck(stable);
	    }

	    // Analyze context cases
	    _contextCase = analyzeCases();

	    // Create an instance of Step to do the translation
	    if (_contextCase == SIMPLE_CONTEXT) {
		_step = new Step(_axis, _nodeType, null);
		_step.setParser(getParser());
		_step.typeCheck(stable);
	    }
	    else if (_contextCase == GENERAL_CONTEXT) {
		final int len = _predicates.size();
		for (int i = 0; i < len; i++)
		    ((Predicate)_predicates.elementAt(i)).dontOptimize();
		_step = new Step(_axis, _nodeType, _predicates);
		_step.setParser(getParser());
		_step.typeCheck(stable);
	    }
	}
	return _axis == Axis.CHILD ? Type.Element : Type.Attribute;
    }

    private void translateKernel(ClassGenerator classGen, 
				 MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	
	if (_nodeType == DOM.ELEMENT) {
	    final int check = cpg.addInterfaceMethodref(DOM_INTF,
							"isElement", "(I)Z");
	    il.append(methodGen.loadDOM());
	    il.append(SWAP);
	    il.append(new INVOKEINTERFACE(check, 2));
	
	    // Need to allow for long jumps here
	    final BranchHandle icmp = il.append(new IFNE(null));
	    _falseList.add(il.append(new GOTO_W(null)));
	    icmp.setTarget(il.append(NOP));
	}
	else if (_nodeType == DOM.ATTRIBUTE) {
	    final int check = cpg.addInterfaceMethodref(DOM_INTF,
							"isAttribute", "(I)Z");
	    il.append(methodGen.loadDOM());
	    il.append(SWAP);
	    il.append(new INVOKEINTERFACE(check, 2));
	
	    // Need to allow for long jumps here
	    final BranchHandle icmp = il.append(new IFNE(null));
	    _falseList.add(il.append(new GOTO_W(null)));
	    icmp.setTarget(il.append(NOP));
	}
	else {
	    // context node is on the stack
	    final int getType = cpg.addInterfaceMethodref(DOM_INTF,
							  "getType", "(I)I");
	    il.append(methodGen.loadDOM());
	    il.append(SWAP);
	    il.append(new INVOKEINTERFACE(getType, 2));
	    il.append(new PUSH(cpg, _nodeType));
	
	    // Need to allow for long jumps here
	    final BranchHandle icmp = il.append(new IF_ICMPEQ(null));
	    _falseList.add(il.append(new GOTO_W(null)));
	    icmp.setTarget(il.append(NOP));
	}
    }

    private void translateNoContext(ClassGenerator classGen, 
				    MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Push current node on the stack
	il.append(methodGen.loadCurrentNode());
	il.append(SWAP);

	// Overwrite current node with matching node
	il.append(methodGen.storeCurrentNode());

	// If pattern not reduced then check kernel
	if (!_isEpsilon) {
	    il.append(methodGen.loadCurrentNode());
	    translateKernel(classGen, methodGen);
	}

	// Compile the expressions within the predicates
	final int n = _predicates.size();
	for (int i = 0; i < n; i++) {
	    Predicate pred = (Predicate)_predicates.elementAt(i);
	    Expression exp = pred.getExpr();
	    exp.translateDesynthesized(classGen, methodGen);
	    _trueList.append(exp._trueList);
	    _falseList.append(exp._falseList);
	}

	// Backpatch true list and restore current iterator/node
	InstructionHandle restore;
	restore = il.append(methodGen.storeCurrentNode());
	backPatchTrueList(restore);
	BranchHandle skipFalse = il.append(new GOTO(null));

	// Backpatch false list and restore current iterator/node
	restore = il.append(methodGen.storeCurrentNode());
	backPatchFalseList(restore);
	_falseList.add(il.append(new GOTO(null)));

	// True list falls through
	skipFalse.setTarget(il.append(NOP));
    }

    private void translateSimpleContext(ClassGenerator classGen, 
					MethodGenerator methodGen) {
	int index;
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Store matching node into a local variable
	LocalVariableGen match;
	match = methodGen.addLocalVariable("step_pattern_tmp1", 
					   Util.getJCRefType(NODE_SIG),
					   il.getEnd(), null);
	il.append(new ISTORE(match.getIndex()));

	// If pattern not reduced then check kernel
	if (!_isEpsilon) {
	    il.append(new ILOAD(match.getIndex()));
 	    translateKernel(classGen, methodGen);
	}

	// Push current iterator and current node on the stack
	il.append(methodGen.loadCurrentNode());
	il.append(methodGen.loadIterator());

	// Create a new matching iterator using the matching node
	index = cpg.addMethodref(MATCHING_ITERATOR, "<init>", 
				 "(I" + NODE_ITERATOR_SIG + ")V");
	il.append(new NEW(cpg.addClass(MATCHING_ITERATOR)));
	il.append(DUP);
	il.append(new ILOAD(match.getIndex()));
	_step.translate(classGen, methodGen);
	il.append(new INVOKESPECIAL(index));

	// Get the parent of the matching node
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(match.getIndex()));
	index = cpg.addInterfaceMethodref(DOM_INTF, GET_PARENT, GET_PARENT_SIG);
	il.append(new INVOKEINTERFACE(index, 2));

	// Start the iterator with the parent 
	il.append(methodGen.setStartNode());

	// Overwrite current iterator and current node
	il.append(methodGen.storeIterator());
	il.append(new ILOAD(match.getIndex()));
	il.append(methodGen.storeCurrentNode());

	// Translate the expression of the predicate 
	Predicate pred = (Predicate) _predicates.elementAt(0);
	Expression exp = pred.getExpr();
	exp.translateDesynthesized(classGen, methodGen);

	// Backpatch true list and restore current iterator/node
	InstructionHandle restore = il.append(methodGen.storeIterator());
	il.append(methodGen.storeCurrentNode());
	exp.backPatchTrueList(restore);
	BranchHandle skipFalse = il.append(new GOTO(null));

	// Backpatch false list and restore current iterator/node
	restore = il.append(methodGen.storeIterator());
	il.append(methodGen.storeCurrentNode());
	exp.backPatchFalseList(restore);
	_falseList.add(il.append(new GOTO(null)));

	// True list falls through
	skipFalse.setTarget(il.append(NOP));
    }

    private void translateGeneralContext(ClassGenerator classGen, 
					 MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	int iteratorIndex = 0;
	BranchHandle ifBlock = null;
	LocalVariableGen iter, node, node2;
	final String iteratorName = getNextFieldName();

	// Store node on the stack into a local variable
	node = methodGen.addLocalVariable("step_pattern_tmp1", 
					  Util.getJCRefType(NODE_SIG),
					  il.getEnd(), null);
	il.append(new ISTORE(node.getIndex()));

	// Create a new local to store the iterator
	iter = methodGen.addLocalVariable("step_pattern_tmp2", 
					  Util.getJCRefType(NODE_ITERATOR_SIG),
					  il.getEnd(), null);

	// Add a new private field if this is the main class
	if (!classGen.isExternal()) {
	    final Field iterator =
		new Field(ACC_PRIVATE, 
			  cpg.addUtf8(iteratorName),
			  cpg.addUtf8(NODE_ITERATOR_SIG),
			  null, cpg.getConstantPool());
	    classGen.addField(iterator);
	    iteratorIndex = cpg.addFieldref(classGen.getClassName(), 
					    iteratorName,
					    NODE_ITERATOR_SIG);

	    il.append(classGen.loadTranslet());
	    il.append(new GETFIELD(iteratorIndex));
	    il.append(DUP);
	    il.append(new ASTORE(iter.getIndex()));
	    ifBlock = il.append(new IFNONNULL(null));
	    il.append(classGen.loadTranslet());
	}	

	// Compile the step created at type checking time
	_step.translate(classGen, methodGen);
	il.append(new ASTORE(iter.getIndex()));

	// If in the main class update the field too
	if (!classGen.isExternal()) {
	    il.append(new ALOAD(iter.getIndex()));
	    il.append(new PUTFIELD(iteratorIndex));
	    ifBlock.setTarget(il.append(NOP));
	}

	// Get the parent of the node on the stack
	il.append(methodGen.loadDOM());
	il.append(new ILOAD(node.getIndex()));
	int index = cpg.addInterfaceMethodref(DOM_INTF,
					      GET_PARENT, GET_PARENT_SIG);
	il.append(new INVOKEINTERFACE(index, 2));

	// Initialize the iterator with the parent
	il.append(new ALOAD(iter.getIndex()));
	il.append(SWAP);
	il.append(methodGen.setStartNode());

	/* 
	 * Inline loop:
	 *
	 * int node2;
	 * while ((node2 = iter.next()) != NodeIterator.END 
	 *		  && node2 < node);
	 * return node2 == node; 
	 */
	BranchHandle skipNext;
	InstructionHandle begin, next;
	node2 = methodGen.addLocalVariable("step_pattern_tmp3", 
					   Util.getJCRefType(NODE_SIG),
					   il.getEnd(), null);

	skipNext = il.append(new GOTO(null));
	next = il.append(new ALOAD(iter.getIndex()));
	begin = il.append(methodGen.nextNode());
	il.append(DUP);
	il.append(new ISTORE(node2.getIndex()));
	_falseList.add(il.append(new IFEQ(null)));	// NodeIterator.END

	il.append(new ILOAD(node2.getIndex()));
	il.append(new ILOAD(node.getIndex()));
	il.append(new IF_ICMPLT(next));

	il.append(new ILOAD(node2.getIndex()));
	il.append(new ILOAD(node.getIndex()));
	_falseList.add(il.append(new IF_ICMPNE(null)));

	skipNext.setTarget(begin);
    }
	
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (hasPredicates()) {
	    switch (_contextCase) {
	    case NO_CONTEXT:
		translateNoContext(classGen, methodGen);
		break;
		
	    case SIMPLE_CONTEXT:
		translateSimpleContext(classGen, methodGen);
		break;
		
	    default:
		translateGeneralContext(classGen, methodGen);
		break;
	    }
	}
	else if (isWildcard()) {
	    il.append(POP); 	// true list falls through
	}
	else {
	    translateKernel(classGen, methodGen);
	}
    }
}
