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
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.Instruction;
import de.fub.bytecode.generic.*;
import de.fub.bytecode.classfile.Field;
import org.apache.xalan.xsltc.compiler.util.*;

final class Variable extends TopLevelElement {

    // The name of the variable.
    private QName _name;

    // The type of this variable.
    private Type _type;

    // True if the variable is local.
    private boolean _isLocal;
    
    private LocalVariableGen _local;
    private Instruction _loadInstruction;
    
    // A reference to the select expression.
    private Expression _select;
	
    // Index of this variable in the variable stack relative to base ptr
    private int _stackIndex = -1;

    private boolean _escapes;
    private boolean _usedLocally;

    /**
     * If the variable is of result tree type, and this result tree 
     * is implemented as a method, this is the name of the method.
     */
    private String _methodName;

    // references to this variable (when local)
    private Vector _refs = new Vector(2);

    // to make sure parameter field is not added twice
    private boolean    _compiled = false;

    public void addReference(VariableRef vref) {
	_refs.addElement(vref);
    }

    public void setEscapes() {
	_escapes = true;
	if (_stackIndex == -1) { // unassigned
	    Template template = getTemplate();
	    if (template != null) {
		_stackIndex = template.allocateIndex(_name);
	    }
	}
    }

    public Expression getExpression() {
	return(_select);
    }

    public String toString() {
	return("variable("+_name+")");
    }

    public void setUsedLocally() {
	_usedLocally = true;
    }

    public void removeReference(VariableRef vref, MethodGenerator methodGen) {
	_refs.remove(vref);
	if (_refs.isEmpty()) {
	    _local.setEnd(methodGen.getInstructionList().getEnd());
	    methodGen.removeLocalVariable(_local);
	    _refs = null;
	    _local = null;
	}
    }
    
    public Instruction loadInstruction() {
	final Instruction instr = _loadInstruction;
	return instr != null
	    ? instr : (_loadInstruction = _type.LOAD(_local.getIndex()));
    }
    
    public void display(int indent) {
	indent(indent);
	System.out.println("Variable " + _name);
	if (_select != null) { 
	    indent(indent + IndentIncrement);
	    System.out.println("select " + _select.toString());
	}
	displayContents(indent + IndentIncrement);
    }

    public String getMethodName() {
	return _methodName;
    }

    public Type getType() {
	return _type;
    }

    public boolean isLocal() {
	return _isLocal;
    }

    public QName getName() {
	return _name;
    }

    public int getStackIndex() {
	return _stackIndex;
    }

    /**
     * Parse the contents of the variable
     */
    public void parseContents(Parser parser) {
	// parse attributes name and select (if present)
	final String name = getAttribute("name");
	if (name.length() > 0) {
	    _name = parser.getQName(name);
	    _name.clearDefaultNamespace();
	}
        else {
	    reportError(this, parser, ErrorMsg.NREQATTR_ERR, "name");
	}

	// check whether variable/param of the same name is already in scope
	if (parser.lookupVariable(_name) != null) {
	    reportError(this, parser, ErrorMsg.VARREDEF_ERR, _name.toString());
	}

	final String select = getAttribute("select");
	if (select.length() > 0) {
	    _select = parser.parseExpression(this, "select", null);
	}

	// Children must be parsed first -> static scoping
	parseChildren(parser);

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
		    reportError(this, parser, ErrorMsg.VARREDEF_ERR,
				_name.toString());
		}
		// Ignore this if previous definition has higher precedence
		else if (them > us) {
		    return;
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
	else {
	    typeCheckContents(stable);
	    // Compile into a method if variable value is not context dependant
	    if (dependentContents() == false) {
		_methodName = "__rt_" + getXSLTC().nextVariableSerial();
		_type = new ResultTreeType(_methodName);
	    }
	    else {
		_type = Type.ResultTree;
	    }
	}
	return Type.Void;
    }

    /**
     * Compiles a method that generates the value of the variable
     */
    private void compileResultTreeMethod(ClassGenerator classGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	final String DOM_CLASS_SIG = classGen.getDOMClassSig();
	
	final RtMethodGenerator methodGen =
	    new RtMethodGenerator(ACC_PROTECTED,
				  de.fub.bytecode.generic.Type.VOID, 
				  new de.fub.bytecode.generic.Type[] {
				      Util.getJCRefType(DOM_CLASS_SIG),
				      Util.getJCRefType(TRANSLET_OUTPUT_SIG)
				  },
				  new String[] {
				      DOCUMENT_PNAME,
				      TRANSLET_OUTPUT_PNAME
				  },
				  _methodName,
				  classGen.getClassName(),
				  il, cpg);
	methodGen.addException("org.apache.xalan.xsltc.TransletException");

	translateContents(classGen, methodGen);
	il.append(RETURN);

	methodGen.stripAttributes(true);
	methodGen.setMaxLocals();
	methodGen.setMaxStack();
	methodGen.removeNOPs();
	classGen.addMethod(methodGen.getMethod());
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
		String name = _name.getLocalPart();
		name = name.replace('.', '_');
		name = name.replace('-', '_');
		_local = methodGen.addLocalVariable2(name,
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

    /**
     * Compile the value of the variable, which is either in an expression in
     * a 'select' attribute, or in the variable elements body
     */
    public void translateValue(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	// Compile expression is 'select' attribute if present
	if (_select != null) {
	    _select.translate(classGen, methodGen);
	    _select.startResetIterator(classGen, methodGen);
	}
	// If not, compile result tree from parameter body if present.
	else if (hasContents()) {
	    compileResultTree(classGen, methodGen);
	}
	// If neither are present then store empty string in variable
	else {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();
	    il.append(new PUSH(cpg, Constants.EMPTYSTRING));
	}
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	String name = _name.getLocalPart();
	name = name.replace('.', '_');
        name = name.replace('-', '_');

	// Make sure that a variable instance is only compiled once
	if (_compiled) return;
	_compiled = true;

	// If a result tree is implemented as method then compile and return
	if ((_select == null) && (_type != Type.ResultTree)) {
	    compileResultTreeMethod(classGen);
	    return;
	}

	if (isLocal()) {
	    // Push args to call addVariable()
	    if (_escapes) {
		il.append(classGen.loadTranslet());
		il.append(new PUSH(cpg, _stackIndex));
	    }

	    // Compile variable value computation
	    translateValue(classGen, methodGen);

	    // Dup value only when needed
	    if (_escapes) {
		il.append(_type.DUP());
	    }

	    // Add a new local variable and store value
	    if (_refs.isEmpty()) { // nobody uses the value
		il.append(_type.POP());
		_local = null;
	    }
	    else {		// normal case
		if (_local == null) {
		    _local = methodGen.addLocalVariable2(name,
							 _type.toJCType(),
							 il.getEnd());
		}
		il.append(_type.STORE(_local.getIndex()));
	    }

	    // Store boxed value into the template's variable stack
	    if (_escapes) {
		_type.translateBox(classGen, methodGen);
		il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
							     ADD_VARIABLE,
							     ADD_VARIABLE_SIG)));
	    }
	}
	else {
	    String signature = _type.toSignature();
	    if (signature.equals(DOM_IMPL_SIG))
		signature = classGen.getDOMClassSig();

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
