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
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author John Howard <JohnH@schemasoft.com>
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.Field;
import org.apache.xalan.xsltc.compiler.util.*;
import org.apache.xalan.xsltc.dom.Axis;

final class Variable extends VariableBase {

    public int getIndex() {
	return (_local != null) ? _local.getIndex() : -1;
    }

    /**
     * Parse the contents of the variable
     */
    public void parseContents(Parser parser) {
	// Parse 'name' and 'select' attributes plus parameter contents
	super.parseContents(parser);

	// Add a ref to this var to its enclosing construct
	SyntaxTreeNode parent = getParent();
	if (parent instanceof Stylesheet) {
	    // Mark this as a global variable
	    _isLocal = false;
	    // Check if a global variable with this name already exists...
	    Variable var = parser.getSymbolTable().lookupVariable(_name);
	    // ...and if it does we need to check import precedence
	    if (var != null) {
		final int us = this.getImportPrecedence();
		final int them = var.getImportPrecedence();
		// It is an error if the two have the same import precedence
		if (us == them) {
		    final String name = _name.toString();
		    reportError(this, parser, ErrorMsg.VARIABLE_REDEF_ERR,name);
		}
		// Ignore this if previous definition has higher precedence
		else if (them > us) {
		    _ignore = true;
		    return;
		}
		else {
		    var.disable();
		}
		// Add this variable if we have higher precedence
	    }
	    ((Stylesheet)parent).addVariable(this);
	    parser.getSymbolTable().addVariable(this);
	}
	else {
	    _isLocal = true;
	}
    }

    /**
     * Runs a type check on either the variable element body or the
     * expression in the 'select' attribute
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

	// Type check the 'select' expression if present
	if (_select != null) {
	    _type = _select.typeCheck(stable);
	}
	// Type check the element contents otherwise
	else if (hasContents()) {
	    typeCheckContents(stable);
	    _type = Type.ResultTree;
	}
	else {
	    _type = Type.Reference;
	}
	// The return type is void as the variable element does not leave
	// anything on the JVM's stack. The '_type' global will be returned
	// by the references to this variable, and not by the variable itself.
	return Type.Void;
    }

    /**
     * This method is part of a little trick that is needed to use local
     * variables inside nested for-each loops. See the initializeVariables()
     * method in the ForEach class for an explanation
     */
    public void initialize(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// This is only done for local variables that are actually used
	if (isLocal() && !_refs.isEmpty()) {
	    // Create a variable slot if none is allocated
	    if (_local == null) {
		_local = methodGen.addLocalVariable2(_name.getLocalPart(),
						     _type.toJCType(),
						     il.getEnd());
	    }
	    // Push the default value on the JVM's stack
	    if ((_type instanceof IntType) ||
		(_type instanceof NodeType) ||
		(_type instanceof BooleanType))
		il.append(new ICONST(0)); // 0 for node-id, integer and boolean
	    else if (_type instanceof RealType)
		il.append(new DCONST(0)); // 0.0 for floating point numbers
	    else
		il.append(new ACONST_NULL()); // and 'null' for anything else
	    il.append(_type.STORE(_local.getIndex()));
	}
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	final String name = getVariable();

	// Make sure that a variable instance is only compiled once
	if (_ignore) return;
	_ignore = true;

	if (isLocal()) {
	    // Compile variable value computation
	    translateValue(classGen, methodGen);

	    // Add a new local variable and store value
	    if (_refs.isEmpty()) { // Remove it if nobody uses the value
		il.append(_type.POP());
		_local = null;
	    }
	    else {		   // Store in local var slot if referenced
		if (_local == null) mapRegister(methodGen);
		il.append(_type.STORE(_local.getIndex()));
	    }
	}
	else {
	    String signature = _type.toSignature();

	    // Global variables are store in class fields
	    if (classGen.containsField(name) == null) {
		classGen.addField(new Field(ACC_PUBLIC, 
					    cpg.addUtf8(name),
					    cpg.addUtf8(signature),
					    null, cpg.getConstantPool()));

		// Push a reference to "this" for putfield
		il.append(classGen.loadTranslet());
		// Compile variable value computation
		translateValue(classGen, methodGen);
		// Store the variable in the allocated field
		il.append(new PUTFIELD(cpg.addFieldref(classGen.getClassName(),
						       name, signature)));
	    }
	}
    }
}
