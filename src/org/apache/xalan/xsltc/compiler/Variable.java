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

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import com.sun.xml.tree.ElementEx;
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

    public void parseContents(ElementEx element, Parser parser) {
	// parse attributes name and select (if present)
	final String name = element.getAttribute("name");
	if (name.length() > 0) {
	    _name = parser.getQName(name);
	}
        else {
	    reportError(element, parser, ErrorMsg.NREQATTR_ERR, "name");
	}

	// check whether variable/param of the same name is already in scope
	if (parser.lookupVariable(_name) != null) {
	    ErrorMsg error = new ErrorMsg(ErrorMsg.VARREDEF_ERR, _name, this);
	    parser.addError(error);
	}
	
	final String select = element.getAttribute("select");
	if (select.length() > 0) {
	    _select = parser.parseExpression(this, element, "select");
	}

	// Children must be parsed first -> static scoping
	parseChildren(element, parser);

	// Add a ref to this var to its enclosing construct
	final SyntaxTreeNode parent = getParent();
	if (parent instanceof Stylesheet) {
	    _isLocal = false;
	    ((Stylesheet)parent).addVariable(this);
	    //!! check for redef
	    parser.getSymbolTable().addVariable(this);
	}
	else {
	    _isLocal = true;
	}
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_select == null) {
	    typeCheckContents(stable);
	    if (dependentContents() == false) {
		_methodName = "%rt%" + getXSLTC().nextVariableSerial();
		_type = new ResultTreeType(_methodName);
	    }
	    else {
		_type = Type.ResultTree;
	    }
	}
	else {
	    _type = _select.typeCheck(stable);
	}
	return Type.Void;
    }

    private void compileRtMethod(ClassGenerator classGen,
				 MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();
	final String DOM_CLASS_SIG = classGen.getDOMClassSig();
	
	final RtMethodGenerator rtMethodGen =
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
	rtMethodGen.addException("org.apache.xalan.xsltc.TransletException");

	translateContents(classGen, rtMethodGen);
	il.append(RETURN);

	rtMethodGen.stripAttributes(true);
	rtMethodGen.setMaxLocals();
	rtMethodGen.setMaxStack();
	rtMethodGen.removeNOPs();
	classGen.addMethod(rtMethodGen.getMethod());
    }

    public void compileRtDom(ClassGenerator classGen,
			     MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Save the current handler base on the stack
	il.append(methodGen.loadHandler());

	final String DOM_CLASS = classGen.getDOMClass();

	// Create new instance of DOM class (with 64 nodes)
	int index = cpg.addMethodref(DOM_IMPL, "<init>", "(I)V");
	il.append(new NEW(cpg.addClass(DOM_IMPL)));
	il.append(DUP);
	il.append(DUP);
	il.append(new PUSH(cpg, 64));
	il.append(new INVOKESPECIAL(index));

	// Overwrite old handler with DOM handler
	index = cpg.addMethodref(DOM_IMPL,
				 "getOutputDomBuilder",
				 "()" + TRANSLET_OUTPUT_SIG);
	il.append(new INVOKEVIRTUAL(index));
	il.append(DUP);
	il.append(methodGen.storeHandler());

	// Call startDocument on the new handler
	il.append(methodGen.startDocument());

	// Instantiate result tree fragment
	translateContents(classGen, methodGen);

	// Call endDocument on the new handler
	il.append(methodGen.loadHandler());
	il.append(methodGen.endDocument());

	// Check if we need to wrap the DOMImpl object in a DOMAdapter object
	if (!DOM_CLASS.equals(DOM_IMPL_CLASS)) {
	    // new org.apache.xalan.xsltc.dom.DOMAdapter(DOMImpl,String[]);
	    index = cpg.addMethodref(DOM_ADAPTER_CLASS, "<init>",
				     "("+DOM_IMPL_SIG+
				     "["+STRING_SIG+
				     "["+STRING_SIG+")V");
	    il.append(new NEW(cpg.addClass(DOM_ADAPTER_CLASS)));
	    il.append(new DUP_X1());
	    il.append(SWAP);
	    il.append(new ICONST(0));
	    il.append(new ANEWARRAY(cpg.addClass(STRING)));
	    il.append(DUP);
	    il.append(new INVOKESPECIAL(index)); // leave DOMAdapter on stack
	    
	    // Must we wrap the DOMAdapter object in an MultiDOM object?
	    if (DOM_CLASS.equals("org.apache.xalan.xsltc.dom.MultiDOM")) {
		// new org.apache.xalan.xsltc.dom.MultiDOM(DOMAdapter);
		index = cpg.addMethodref(MULTI_DOM_CLASS, "<init>",
					 "("+DOM_ADAPTER_SIG+")V");
		il.append(new NEW(cpg.addClass(MULTI_DOM_CLASS)));
		il.append(new DUP_X1());
		il.append(SWAP);
		il.append(new INVOKESPECIAL(index)); // leave MultiDOM on stack
	    }
	}

	// Restore old handler base from stack
	il.append(SWAP);
	il.append(methodGen.storeHandler());
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final String name = _name.getLocalPart();

	if (_compiled) return;
	_compiled = true;

	if (isLocal()) {
	    // If rt implemented as method then compile and return
	    if (_select == null && _type != Type.ResultTree) {
		compileRtMethod(classGen, methodGen);
		return;
	    }

	    // Push args to call addVariable()
	    if (_escapes) {
		il.append(classGen.loadTranslet());
		il.append(new PUSH(cpg, _stackIndex));
	    }

	    // Compile rt or expression and store in local
	    if (_select == null) {
		if (hasContents()) {
		    compileRtDom(classGen, methodGen);
		}
		else {
		    // If no select and no contents push the empty string
		    il.append(new PUSH(cpg, ""));
		}
	    }
	    else {
		_select.translate(classGen, methodGen);
		_select.startResetIterator(classGen, methodGen);
	    }

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
		_local = methodGen.addLocalVariable2(name,
						     _type.toJCType(),
						     il.getEnd());
		il.append(_type.STORE(_local.getIndex()));
	    }

	    // Store boxed value into the template's variable stack
	    if (_escapes) {
		_type.translateBox(classGen, methodGen);
		il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
							     ADD_VARIABLE,
							     ADD_VARIABLE_SIG)
					    ));
	    }
	}
	else {
	    // If rt implemented as method then compile and return
	    if (_select == null && _type != Type.ResultTree) {
		compileRtMethod(classGen, methodGen);
		return;
	    }

	    String signature = _type.toSignature();
	    if (signature.equals(DOM_IMPL_SIG))
		signature = classGen.getDOMClassSig();

	    // Add a new field to this class
	    if (classGen.containsField(name) == null) {
		classGen.addField(new Field(ACC_PUBLIC, 
					    cpg.addUtf8(name),
					    cpg.addUtf8(signature),
					    null, cpg.getConstantPool()));

		// Push a reference to "this" for putfield
		il.append(classGen.loadTranslet());

		// Compile rt or expression and store in field
		if (_select == null) {
		    if (hasContents()) {
			compileRtDom(classGen, methodGen);
		    }
		    else {
			// If no select and no contents push the empty string
			il.append(new PUSH(cpg, ""));
		    }
		}
		else {
		    _select.translate(classGen, methodGen);
		    _select.startResetIterator(classGen, methodGen);
		}
		il.append(new PUTFIELD(cpg.addFieldref(classGen.getClassName(),
						       name, signature)));
	    }
	}
    }
}
