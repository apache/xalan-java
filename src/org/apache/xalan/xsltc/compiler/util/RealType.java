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
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.Constants;

public final class RealType extends NumberType {
    protected RealType() {}

    public String toString() {
	return "real";
    }

    public boolean identicalTo(Type other) {
	return this == other;
    }

    public String toSignature() {
	return "D";
    }

    public org.apache.bcel.generic.Type toJCType() {
	return org.apache.bcel.generic.Type.DOUBLE;
    }

    /**
     * @see	org.apache.xalan.xsltc.compiler.util.Type#distanceTo
     */
    public int distanceTo(Type type) {
	if (type == this) {
	    return 0;
	}
	else if (type == Type.Int) {
	    return 1;
	}
	else {
	    return Integer.MAX_VALUE;
	}
    }

    /**
     * Translates a real into an object of internal type <code>type</code>. The
     * translation to int is undefined since reals are never converted to ints.
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
	else if (type == Type.Reference) {
	    translateTo(classGen, methodGen, (ReferenceType) type);
	}
	else if (type == Type.Int) {
	    translateTo(classGen, methodGen, (IntType) type);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), type.toString());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Expects a real on the stack and pushes its string value by calling
     * <code>Double.toString(double d)</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    StringType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new INVOKESTATIC(cpg.addMethodref(BASIS_LIBRARY_CLASS,
						    "realToString",
						    "(D)" + STRING_SIG)));
    }

    /**
     * Expects a real on the stack and pushes a 0 if that number is 0.0 and
     * a 1 otherwise.
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
     * Expects a real on the stack and pushes a truncated integer value
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    IntType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new INVOKESTATIC(cpg.addMethodref(BASIS_LIBRARY_CLASS,
						    "realToInt","(D)I")));
    }

    /**
     * Translates a real into a non-synthesized boolean. It does not push a 
     * 0 or a 1 but instead returns branchhandle list to be appended to the 
     * false list. A NaN must be converted to "false".
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	LocalVariableGen local;
	final FlowList flowlist = new FlowList();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Store real into a local variable
	il.append(DUP2);
	local = methodGen.addLocalVariable("real_to_boolean_tmp", 
					   org.apache.bcel.generic.Type.DOUBLE,
					   il.getEnd(), null);
	il.append(new DSTORE(local.getIndex()));

	// Compare it to 0.0
	il.append(DCONST_0);
	il.append(DCMPG);
	flowlist.add(il.append(new IFEQ(null)));

	//!!! call isNaN
	// Compare it to itself to see if NaN
	il.append(new DLOAD(local.getIndex()));
	il.append(new DLOAD(local.getIndex()));
	il.append(DCMPG);
	flowlist.add(il.append(new IFNE(null)));	// NaN != NaN
	return flowlist;
    }

    /**
     * Expects a double on the stack and pushes a boxed double. Boxed 
     * double are represented by an instance of <code>java.lang.Double</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ReferenceType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new NEW(cpg.addClass(DOUBLE_CLASS)));
	il.append(DUP_X2);
	il.append(DUP_X2);
	il.append(POP);
	il.append(new INVOKESPECIAL(cpg.addMethodref(DOUBLE_CLASS,
						     "<init>", "(D)V")));
    }

    /**
     * Translates a real into the Java type denoted by <code>clazz</code>. 
     * Expects a real on the stack and pushes a number of the appropriate
     * type after coercion.
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    final Class clazz) {
	final InstructionList il = methodGen.getInstructionList();
	if (clazz == Character.TYPE) {
	    il.append(D2I);
	    il.append(I2C);
	}
	else if (clazz == Byte.TYPE) {
	    il.append(D2I);
	    il.append(I2B);
	}
	else if (clazz == Short.TYPE) {
	    il.append(D2I);
	    il.append(I2S);
	}
	else if (clazz == Integer.TYPE) {
	    il.append(D2I);
	}
	else if (clazz == Long.TYPE) {
	    il.append(D2L);
	}
	else if (clazz == Float.TYPE) {
	    il.append(D2F);
	}
	else if (clazz == Double.TYPE) {
	    il.append(NOP);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), clazz.getName());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Translates an external (primitive) Java type into a real. Expects a java 
     * object on the stack and pushes a real (i.e., a double).
     */
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, 
			      Class clazz) {
	InstructionList il = methodGen.getInstructionList();

	if (clazz == Character.TYPE || clazz == Byte.TYPE ||
	    clazz == Short.TYPE || clazz == Integer.TYPE) {
	    il.append(I2D);
	}
	else if (clazz == Long.TYPE) {
	    il.append(L2D);
	}
	else if (clazz == Float.TYPE) {
	    il.append(F2D);
	}
	else if (clazz == Double.TYPE) {
	    il.append(NOP);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), clazz.getName());
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
	il.append(new CHECKCAST(cpg.addClass(DOUBLE_CLASS)));
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(DOUBLE_CLASS,
						     DOUBLE_VALUE, 
						     DOUBLE_VALUE_SIG)));
    }

    public Instruction ADD() {
	return InstructionConstants.DADD;
    }

    public Instruction SUB() {
	return InstructionConstants.DSUB;
    }

    public Instruction MUL() {
	return InstructionConstants.DMUL;
    }

    public Instruction DIV() {
	return InstructionConstants.DDIV;
    }

    public Instruction REM() {
	return InstructionConstants.DREM;
    }

    public Instruction NEG() {
	return InstructionConstants.DNEG;
    }

    public Instruction LOAD(int slot) {
	return new DLOAD(slot);
    }
	
    public Instruction STORE(int slot) {
	return new DSTORE(slot);
    }

    public Instruction POP() {
	return POP2;
    }
    
    public Instruction CMP(boolean less) {
	return less ? InstructionConstants.DCMPG : InstructionConstants.DCMPL;
    }

    public Instruction DUP() {
	return DUP2;
    }
}

