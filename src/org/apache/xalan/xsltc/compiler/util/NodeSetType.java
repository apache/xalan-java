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
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.FlowList;

public final class NodeSetType extends Type {
    protected NodeSetType() {}

    public String toString() {
	return "node-set";
    }

    public boolean identicalTo(Type other) {
	return this == other;
    }

    public String toSignature() {
	return NODE_ITERATOR_SIG;
    }

    public de.fub.bytecode.generic.Type toJCType() {
	return new de.fub.bytecode.generic.ObjectType(NODE_ITERATOR);
    }

    /**
     * Translates a node-set into an object of internal type
     * <code>type</code>. The translation to int is undefined
     * since node-sets are always converted to
     * reals in arithmetic expressions.
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
	else if (type == Type.Node) {
	    translateTo(classGen, methodGen, (NodeType) type);
	}
	else if (type == Type.Reference) {
	    translateTo(classGen, methodGen, (ReferenceType) type);
	}
	else {
	    classGen.getParser().internalError(); // undefined
	}
    }

    /**
     * Translates a node-set into a synthesized boolean.
     * The boolean value of a node-set is "true" if non-empty
     * and "false" otherwise. Notice that the 
     * function getFirstNode() is called in translateToDesynthesized().
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
     * Translates a node-set into a string. The string value of a node-set is
     * value of its first element.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    StringType type) {
	final InstructionList il = methodGen.getInstructionList();
	getFirstNode(classGen, methodGen);
	il.append(DUP);
	final BranchHandle falsec = il.append(new IFEQ(null));
	Type.Node.translateTo(classGen, methodGen, type);
	final BranchHandle truec = il.append(new GOTO(null));
	falsec.setTarget(il.append(POP));
	il.append(new PUSH(classGen.getConstantPool(), ""));
	truec.setTarget(il.append(NOP));
    }

    /**
     * Expects a node-set on the stack and pushes a real.
     * First the node-set is converted to string, and from string to real.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    RealType type) {
	translateTo(classGen, methodGen, Type.String);
	Type.String.translateTo(classGen, methodGen, Type.Real);	
    }

    /**
     * Expects a node-set on the stack and pushes a node.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    NodeType type) {
	getFirstNode(classGen, methodGen);
    }

    /**
     * Translates a node-set into a non-synthesized boolean. It does not 
     * push a 0 or a 1 but instead returns branchhandle list to be appended 
     * to the false list.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	final InstructionList il = methodGen.getInstructionList();
	getFirstNode(classGen, methodGen);
	return new FlowList(il.append(new IFEQ(null)));
    }

    /**
     * Expects a node-set on the stack and pushes a boxed node-set.
     * Node sets are already boxed so the translation is just a NOP.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ReferenceType type) {
	methodGen.getInstructionList().append(NOP);
    }

    /**
     * Translates a node-set into the Java type denoted by <code>clazz</code>. 
     * Expects a node-set on the stack and pushes an object of the appropriate
     * type after coercion.
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Class clazz) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final String DOM_CLASS = classGen.getDOMClass();
	
	il.append(methodGen.loadDOM());
	il.append(SWAP);	// dom ref must be below node index

	final String className = clazz.getName();
	if (className.equals("org.w3c.dom.Node")) {
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOM_CLASS,
							 MAKE_NODE,
							 MAKE_NODE_SIG2)));
	}
	else if (className.equals("org.w3c.dom.NodeList")) {
	    final int mkList = cpg.addMethodref(DOM_CLASS,
						MAKE_NODE_LIST,
						MAKE_NODE_LIST_SIG2);
	    il.append(new INVOKEVIRTUAL(mkList));
	}
	else {
	    classGen.getParser().internalError(); // undefined
	}
    }
    
    /**
     * Some type conversions require gettting the first node from the node-set.
     * This function is defined to avoid code repetition.
     */
    private void getFirstNode(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new INVOKEINTERFACE(cpg.addInterfaceMethodref(NODE_ITERATOR,
								NEXT,
								NEXT_SIG), 1));
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
	methodGen.getInstructionList().append(NOP);
    }

    public Instruction LOAD(int slot) {
	return new ALOAD(slot);
    }
	
    public Instruction STORE(int slot) {
	return new ASTORE(slot);
    }
}
