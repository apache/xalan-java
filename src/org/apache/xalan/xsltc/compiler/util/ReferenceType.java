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

public final class ReferenceType extends Type {
    protected ReferenceType() {}

    public String toString() {
	return "reference";
    }

    public boolean identicalTo(Type other) {
	return this == other;
    }

    public String toSignature() {
	return "Ljava/lang/Object;";
    }

    public de.fub.bytecode.generic.Type toJCType() {
	return de.fub.bytecode.generic.Type.OBJECT;
    }

    /**
     * Translates a reference to an object of internal type <code>type</code>. 
     * The translation to int is undefined since references
     * are always converted to reals in arithmetic expressions.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Type type) {
	if (type == Type.String) {
	    translateTo(classGen, methodGen, (StringType) type);
	}
	else if (type == Type.Real) {
	    translateTo(classGen, methodGen, (RealType) type);
	}
	else if (type == Type.Boolean) {
	    translateTo(classGen, methodGen, (BooleanType) type);
	}
	else if (type == Type.NodeSetDTM) {
	    translateTo(classGen, methodGen, (NodeSetDTMType) type);
	}
	else {
	    classGen.getParser().internalError();		// undefined
	}
    }

    /**
     * Translates reference into object of internal type <code>type</code>. 
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    StringType type) {
	final int current = methodGen.getLocalIndex("current");
	ConstantPoolGen cpg = classGen.getConstantPool();
	InstructionList il = methodGen.getInstructionList();

	il.append(new ILOAD(current));
	il.append(methodGen.loadDOM());
	final int stringF = cpg.addMethodref(BASIS_LIBRARY_CLASS,
					     "stringF", 
					     "("
					     + OBJECT_SIG
					     + NODE_SIG
					     + DOM_INTF_SIG
					     + ")" + STRING_SIG);
	il.append(new INVOKESTATIC(stringF));
    }

    /**
     * Translates a reference into an object of internal type <code>type</code>. 
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    RealType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	il.append(methodGen.loadDOM());
	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "numberF", 
				     "(" 
				     + OBJECT_SIG
				     + DOM_INTF_SIG
				     + ")D");
	il.append(new INVOKESTATIC(index));
    }

    /**
     * Translates a reference to an object of internal type <code>type</code>. 
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    BooleanType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "booleanF", 
				     "(" 
				     + OBJECT_SIG
				     + ")Z");
	il.append(new INVOKESTATIC(index));
    }

    /**
     * Casts a reference into a NodeIterator.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    NodeSetDTMType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	int index = cpg.addMethodref(BASIS_LIBRARY_CLASS, "referenceToNodeSetDTM", 
				     "("
				     + OBJECT_SIG
				     + ")"
				     + NODE_ITERATOR_SIG);
	il.append(new INVOKESTATIC(index));
	
	// Reset this iterator
	index = cpg.addInterfaceMethodref(NODE_ITERATOR, RESET, RESET_SIG);
	il.append(new INVOKEINTERFACE(index, 1));
    }

    /**
     * Expects a reference on the stack and translates it to a non-synthesized
     * boolean. It does not push a 0 or a 1 but instead returns branchhandle 
     * list to be appended to the false list.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	InstructionList il = methodGen.getInstructionList();
	translateTo(classGen, methodGen, type);
	return new FlowList(il.append(new IFEQ(null)));
    }

    public Instruction LOAD(int slot) {
	return new ALOAD(slot);
    }
	
    public Instruction STORE(int slot) {
	return new ASTORE(slot);
    }
}

