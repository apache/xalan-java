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

package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.NodeTest;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.Constants;

public abstract class Type implements Constants {
    public static final Type Int        = new IntType();
    public static final Type Real       = new RealType();
    public static final Type Boolean    = new BooleanType();
    public static final Type NodeSet    = new NodeSetType();
    public static final Type String     = new StringType();
    public static final Type ResultTree = new ResultTreeType();
    public static final Type Reference  = new ReferenceType();
    public static final Type Void       = new VoidType();
    public static final Type Object     = new ObjectType();

    public static final Type Node       = new NodeType(NodeTest.ANODE);
    public static final Type Root       = new NodeType(NodeTest.ROOT);
    public static final Type Element    = new NodeType(NodeTest.ELEMENT);
    public static final Type Attribute  = new NodeType(NodeTest.ATTRIBUTE);
    public static final Type Text       = new NodeType(NodeTest.TEXT);
    public static final Type Comment    = new NodeType(NodeTest.COMMENT);
    public static final Type Processing_Instruction = new NodeType(NodeTest.PI);

    /**
     * Returns a string representation of this type.	
     */
    public abstract String toString();

    /**
     * Returns true if this and other are identical types.
     */
    public abstract boolean identicalTo(Type other);

    /**
     * Returns true if this type is a numeric type. Redefined in NumberType.
     */
    public boolean isNumber() {
	return false;
    }

    /**
     * Returns true if this type has no object representaion. Redefined in
     * ResultTreeType.
     */
    public boolean implementedAsMethod() {
	return false;
    }

    /**
     * Returns true if this type is a simple type. Redefined in NumberType,
     * BooleanType and StringType.
     */
    public boolean isSimple() {
	return false;
    }

    public abstract org.apache.bcel.generic.Type toJCType();

    /**
     * Returns the distance between two types. This measure is used to select
     * overloaded functions/operators. This method is typically redefined by
     * the subclasses.
     */
    public int distanceTo(Type type) {
	return type == this ? 0 : Integer.MAX_VALUE;
    }

    /**
     * Returns the signature of an internal type's external representation.
     */
    public abstract String toSignature();

    /**
     * Translates an object of this type to an object of type
     * <code>type</code>. 
     * Expects an object of the former type and pushes an object of the latter.
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Type type) {
	ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				    toString(), type.toString());
	classGen.getParser().reportError(Constants.FATAL, err);
    }

    /**
     * Translates object of this type to an object of type <code>type</code>. 
     * Expects an object of the former type and pushes an object of the latter
     * if not boolean. If type <code>type</code> is boolean then a branchhandle
     * list (to be appended to the false list) is returned.
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     Type type) {
	FlowList fl = null;
	if (type == Type.Boolean) {
	    fl = translateToDesynthesized(classGen, methodGen,
					  (BooleanType)type);
	}
	else {
	    translateTo(classGen, methodGen, type);
	}
	return fl;
    }

    /**
     * Translates an object of this type to an non-synthesized boolean. It
     * does not push a 0 or a 1 but instead returns branchhandle list to be
     * appended to the false list.
     */ 
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				    toString(), type.toString());
	classGen.getParser().reportError(Constants.FATAL, err);
	return null;
    }

    /**
     * Translates an object of this type to the external (Java) type denoted
     * by <code>clazz</code>. This method is used to translate parameters 
     * when external functions are called.
     */ 
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Class clazz) {
	ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				    toString(), clazz.getClass().toString());
	classGen.getParser().reportError(Constants.FATAL, err);
    }

    /**
     * Translates an external (Java) type denoted by <code>clazz</code> to 
     * an object of this type. This method is used to translate return values 
     * when external functions are called.
     */ 
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen,
			      Class clazz) {
	ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				    clazz.getClass().toString(), toString());
	classGen.getParser().reportError(Constants.FATAL, err);
    }

    /**
     * Translates an object of this type to its boxed representation.
     */ 
    public void translateBox(ClassGenerator classGen,
			     MethodGenerator methodGen) {
	ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				    toString(), "["+toString()+"]");
	classGen.getParser().reportError(Constants.FATAL, err);
    }

    /**
     * Translates an object of this type to its unboxed representation.
     */ 
    public void translateUnBox(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
				    "["+toString()+"]", toString());
	classGen.getParser().reportError(Constants.FATAL, err);
    }

    /**
     * Returns the class name of an internal type's external representation.
     */
    public String getClassName() {
	return(EMPTYSTRING);
    }

    public Instruction ADD() {
	return null;		// should never be called
    }

    public Instruction SUB() {
	return null;		// should never be called
    }

    public Instruction MUL() {
	return null;		// should never be called
    }

    public Instruction DIV() {
	return null;		// should never be called
    }

    public Instruction REM() {
	return null;		// should never be called
    }

    public Instruction NEG() {
	return null;		// should never be called
    }

    public Instruction LOAD(int slot) {
	return null;		// should never be called
    }
	
    public Instruction STORE(int slot) {
	return null;		// should never be called
    }

    public Instruction POP() {
	return POP;
    }

    public BranchInstruction GT(boolean tozero) {
	return null;		// should never be called
    }

    public BranchInstruction GE(boolean tozero) {
	return null;		// should never be called
    }

    public BranchInstruction LT(boolean tozero) {
	return null;		// should never be called
    }

    public BranchInstruction LE(boolean tozero) {
	return null;		// should never be called
    }

    public Instruction CMP(boolean less) {
	return null;		// should never be called
    }
	
    public Instruction DUP() {
	return DUP;	// default
    }
}
