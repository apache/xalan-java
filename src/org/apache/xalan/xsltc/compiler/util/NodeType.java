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

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.NodeTest;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.Constants;

public final class NodeType extends Type {
    private final int _type;

    protected NodeType() {
	this(NodeTest.ANODE);
    }

    protected NodeType(int type) {
	_type = type;
    }

    public int getType() {
	return _type;
    }

    public String toString() {
	return "node-type";
    }

    public boolean identicalTo(Type other) {
	return other instanceof NodeType;
    }

    public int hashCode() {
	return _type;
    }

    public String toSignature() {
	return "I";
    }

    public org.apache.bcel.generic.Type toJCType() {
	return org.apache.bcel.generic.Type.INT;
    }

    /**
     * Translates a node into an object of internal type <code>type</code>.
     * The translation to int is undefined since nodes are always converted
     * to reals in arithmetic expressions.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Type type) {
	if (type == Type.String) {
	    translateTo(classGen, methodGen, (StringType) type);
	}
	else if (type == Type.Boolean) {
	    translateTo(classGen, methodGen, (BooleanType) type);
	}
	else if (type == Type.Real) {
	    translateTo(classGen, methodGen, (RealType) type);
	}
	else if (type == Type.NodeSet) {
	    translateTo(classGen, methodGen, (NodeSetType) type);
	}
	else if (type == Type.Reference) {
	    translateTo(classGen, methodGen, (ReferenceType) type);
	}
	else if (type == Type.Object) {
	    translateTo(classGen, methodGen, (ObjectType) type);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), type.toString());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Expects a node on the stack and pushes its string value. 
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    StringType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	switch (_type) {
	case NodeTest.ROOT:
	case NodeTest.ELEMENT:
	    il.append(methodGen.loadDOM());
	    il.append(SWAP); // dom ref must be below node index
	    int index = cpg.addInterfaceMethodref(DOM_INTF,
						  GET_ELEMENT_VALUE,
						  GET_ELEMENT_VALUE_SIG);
	    il.append(new INVOKEINTERFACE(index, 2));
	    break;

	case NodeTest.ANODE:
	case NodeTest.COMMENT:
	case NodeTest.ATTRIBUTE:
	case NodeTest.PI:
	    il.append(methodGen.loadDOM());
	    il.append(SWAP); // dom ref must be below node index
	    index = cpg.addInterfaceMethodref(DOM_INTF,
					      GET_NODE_VALUE,
					      GET_NODE_VALUE_SIG);
	    il.append(new INVOKEINTERFACE(index, 2));
	    break;
	    
	default:
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), type.toString());
	    classGen.getParser().reportError(Constants.FATAL, err);
	    break;
	}
    }

    /**
     * Translates a node into a synthesized boolean.
     * If the expression is "@attr", 
     * then "true" is pushed iff "attr" is an attribute of the current node.
     * If the expression is ".", the result is always "true".	
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    BooleanType type) {
	final InstructionList il = methodGen.getInstructionList();
	FlowList falsel = translateToDesynthesized(classGen, methodGen, type);
	il.append(ICONST_1);
	final BranchHandle truec = il.append(new GOTO(null));
	falsel.backPatch(il.append(ICONST_0));
	truec.setTarget(il.append(NOP));
    }

    /**
     * Expects a node on the stack and pushes a real.
     * First the node is converted to string, and from string to real.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    RealType type) {
	translateTo(classGen, methodGen, Type.String);
	Type.String.translateTo(classGen, methodGen, Type.Real);	
    }

    /**
     * Expects a node on the stack and pushes a singleton node-set. Singleton
     * iterators are already started after construction.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    NodeSetType type) {
	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();

	// Create a new instance of SingletonIterator
	il.append(new NEW(cpg.addClass(SINGLETON_ITERATOR)));
	il.append(DUP_X1);
	il.append(SWAP);
	final int init = cpg.addMethodref(SINGLETON_ITERATOR, "<init>",
					  "(" + NODE_SIG +")V");
	il.append(new INVOKESPECIAL(init));
    }

    /**
     * Subsume Node into ObjectType.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ObjectType type) {
	    methodGen.getInstructionList().append(NOP);	
    }

    /**
     * Translates a node into a non-synthesized boolean. It does not push a 
     * 0 or a 1 but instead returns branchhandle list to be appended to the 
     * false list.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	final InstructionList il = methodGen.getInstructionList();
	return new FlowList(il.append(new IFEQ(null)));
    }

    /**
     * Expects a node on the stack and pushes a boxed node. Boxed nodes
     * are represented by an instance of <code>org.apache.xalan.xsltc.dom.Node</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ReferenceType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new NEW(cpg.addClass(RUNTIME_NODE_CLASS)));
	il.append(DUP_X1);
	il.append(SWAP);
	il.append(new PUSH(cpg, _type));
	il.append(new INVOKESPECIAL(cpg.addMethodref(RUNTIME_NODE_CLASS,
						     "<init>", "(II)V")));
    }

    /**
     * Translates a node into the Java type denoted by <code>clazz</code>. 
     * Expects a node on the stack and pushes an object of the appropriate
     * type after coercion.
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Class clazz) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	il.append(methodGen.loadDOM());
	il.append(SWAP);		// dom ref must be below node index

	String className = clazz.getName();
	if (className.equals("org.w3c.dom.Node")) {
	    int index = cpg.addInterfaceMethodref(DOM_INTF,
						  MAKE_NODE,
						  MAKE_NODE_SIG);
	    il.append(new INVOKEINTERFACE(index, 2));
	}
	else if (className.equals("org.w3c.dom.NodeList")) {
	    int index = cpg.addInterfaceMethodref(DOM_INTF,
						  MAKE_NODE_LIST,
						  MAKE_NODE_LIST_SIG);
	    il.append(new INVOKEINTERFACE(index, 2));
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), className);
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Translates an object of this type to its boxed representation.
     */ 
    public void translateBox(ClassGenerator classGen,
			     MethodGenerator methodGen) {
	translateTo(classGen, methodGen, Type.Reference);
    }

    /**
     * Translates an object of this type to its unboxed representation.
     */ 
    public void translateUnBox(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new CHECKCAST(cpg.addClass(RUNTIME_NODE_CLASS)));
	il.append(new GETFIELD(cpg.addFieldref(RUNTIME_NODE_CLASS,
					       NODE_FIELD,
					       NODE_FIELD_SIG)));
    }

    /**
     * Returns the class name of an internal type's external representation.
     */
    public String getClassName() {
	return(RUNTIME_NODE_CLASS);
    }

    public Instruction LOAD(int slot) {
	return new ILOAD(slot);
    }
	
    public Instruction STORE(int slot) {
	return new ISTORE(slot);
    }
}

