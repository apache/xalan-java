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
import org.apache.xalan.xsltc.compiler.util.ReferenceType;

import de.fub.bytecode.generic.Instruction;
import de.fub.bytecode.generic.*;
import de.fub.bytecode.classfile.Field;

import org.apache.xalan.xsltc.compiler.util.*;

final class Param extends TopLevelElement {

    private QName      _name;
    private boolean    _isLocal;	// true if the param is local
    private Expression _select;
    private Type       _type;

    // a JavaClass construct to refer to a JVM var
    private LocalVariableGen _local;
    // cached JavaClass instruction to push the contents of this var
    private Instruction _loadInstruction;
    // references to this variable (when local)
    private Vector     _refs = new Vector(2);
    // to make sure parameter field is not added twice
    private boolean    _compiled = false;

    public void display(int indent) {
	indent(indent);
	System.out.println("param " + _name);
	if (_select != null) {
	    indent(indent + IndentIncrement);
	    System.out.println("select " + _select.toString());
	}
	displayContents(indent + IndentIncrement);
    }

    public String toString() {
	return("param("+_name+")");
    }

    public void addReference(ParameterRef pref) {
	_refs.addElement(pref);
    }

    public void removeReference(ParameterRef pref) {
	_refs.remove(pref);
    }
    
    public void removeReference(ParameterRef pref, MethodGenerator methodGen) {
	_refs.remove(pref);
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
    
    public Type getType() {
	return _type;
    }

    public boolean isLocal() {
	return _isLocal;
    }

    public QName getName() {
	return _name;
    }

    public void parseContents(Parser parser) {
	// Parse attributes name and select (if present)
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
	    ErrorMsg msg = new ErrorMsg(ErrorMsg.VARREDEF_ERR, _name, this);
	    parser.reportError(Constants.ERROR, msg);
	}
	
	final String select = getAttribute("select");
	if (select.length() > 0) {
	    _select = parser.parseExpression(this, "select", null);
	}

	// Children must be parsed first -> static scoping
	parseChildren(parser);

	// Add a ref to this param to its enclosing construct
	final SyntaxTreeNode parent = getParent();
	if (parent instanceof Stylesheet) {
	    _isLocal = false;
	    ((Stylesheet)parent).addParam(this);
	    //!! check for redef
	    parser.getSymbolTable().addParam(this);
	}
	else if (parent instanceof Template) {
	    _isLocal = true;
	    ((Template)parent).hasParams(true);
	}
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_select != null) {
	    final Type tselect = _select.typeCheck(stable); 
	    if (tselect instanceof ReferenceType == false) {
		_select = new CastExpr(_select, Type.Reference);
	    }
	}
	else {
	    typeCheckContents(stable);
	}
	_type = Type.Reference;
	return Type.Void;
    }

    /**
     * Compile the value of the parameter, which is either in an expression in
     * a 'select' attribute, or in the parameter element's body
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
	// If neither are present then store empty string in parameter slot
	else {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();
	    il.append(new PUSH(cpg, Constants.EMPTYSTRING));
	}
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_compiled) return;
	_compiled = true;

	// Make name acceptable for use as field name in class
	// TODO: convert to escape sequence like $dot$ and $dash$
	String name = _name.getLocalPart(); // TODO: namespace ?
	name = name.replace('.', '_');
	name = name.replace('-', '_');

	if (isLocal()) {

	    il.append(classGen.loadTranslet());
	    il.append(new PUSH(cpg, name));
	    translateValue(classGen, methodGen);
	    il.append(new PUSH(cpg, true));

	    // Call addParameter() from this class
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
							 ADD_PARAMETER,
							 ADD_PARAMETER_SIG)));

	    if (_refs.isEmpty()) { // nobody uses the value
		il.append(_type.POP());
		_local = null;
	    }
	    else {		// normal case
		_local = methodGen.addLocalVariable2(name,
						     _type.toJCType(),
						     il.getEnd());
		// Cache the result of addParameter() in a local variable
		il.append(_type.STORE(_local.getIndex()));
	    }
	}
	else {
	    String signature = _type.toSignature();
	    if (signature.equals(DOM_IMPL_SIG))
		signature = classGen.getDOMClassSig();
	    classGen.addField(new Field(ACC_PUBLIC, cpg.addUtf8(name),
					cpg.addUtf8(signature),
					null, cpg.getConstantPool()));
	    il.append(classGen.loadTranslet());
	    il.append(DUP);
	    il.append(new PUSH(cpg, name));
	    translateValue(classGen, methodGen);
	    il.append(new PUSH(cpg, true));

	    // Call addParameter() from this class
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
							 ADD_PARAMETER,
							 ADD_PARAMETER_SIG)));
	    // Cache the result of addParameter() in a field
	    il.append(new PUTFIELD(cpg.addFieldref(classGen.getClassName(),
						   name, signature)));
	}
    }
}
