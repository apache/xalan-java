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

class VariableBase extends TopLevelElement {

    protected QName       _name;            // The name of the variable.
    protected String      _variable;        // The real name of the variable.
    protected Type        _type;            // The type of this variable.
    protected boolean     _isLocal;         // True if the variable is local.
    protected LocalVariableGen _local;      // Reference to JVM variable
    protected Instruction _loadInstruction; // Instruction to load JVM variable
    protected Expression  _select;          // Reference to variable expression
    protected String      select;           // Textual repr. of variable expr.

    // References to this variable (when local)
    protected Vector      _refs = new Vector(2); 

    // Dependencies to other variables/parameters (for globals only)
    protected Vector      _dependencies = null;

    // Used to make sure parameter field is not added twice
    protected boolean    _ignore = false;

    // Used to order top-level variables so that there are no forward references
    protected int        _weight = 0;

    /**
     * Disable this variable/parameter
     */
    public void disable() {
	_ignore = true;
    }

    /**
     * Add a reference to this variable. Called by VariableRef when an
     * expression contains a reference to this variable.
     */
    public void addReference(VariableRefBase vref) {
	_refs.addElement(vref);
    }

    /**
     * Remove a reference to this variable. Called by VariableRef when this
     * variable goes out of scope.
     */
    public void removeReference(VariableRefBase vref) {
	_refs.remove(vref);
    }

    /**
     *
     */
    public void addDependency(VariableBase other) {
	if (_dependencies == null) {
	    _dependencies = new Vector();
	}
	if (!_dependencies.contains(other)) {
	    _dependencies.addElement(other);
	}
    }

    /**
     *
     */
    public Vector getDependencies() {
	return _dependencies;
    }

    /**
     * Map this variable to a register
     */
    public void mapRegister(MethodGenerator methodGen) {
        if (_local == null) {
            final InstructionList il = methodGen.getInstructionList();
	    final String name = _name.getLocalPart(); // TODO: namespace ?
	    final org.apache.bcel.generic.Type varType = _type.toJCType();
            _local = methodGen.addLocalVariable2(name, varType, il.getEnd());
        }
    }

    /**
     * Remove the mapping of this variable to a register.
     * Called when we leave the AST scope of the variable's declaration
     */
    public void unmapRegister(MethodGenerator methodGen) {
	if (_refs.isEmpty() && (_local != null)) {
	    _local.setEnd(methodGen.getInstructionList().getEnd());
	    methodGen.removeLocalVariable(_local);
	    _refs = null;
	    _local = null;
	}
    }

    /**
     * Returns a handle to the instruction for loading the value of this
     * variable onto the JVM stack.
     */
    public Instruction loadInstruction() {
	final Instruction instr = _loadInstruction;
	if (_loadInstruction == null) 
	    _loadInstruction = _type.LOAD(_local.getIndex());
	return _loadInstruction;
    }

    /**
     * Returns the expression from this variable's select attribute (if any)
     */
    public Expression getExpression() {
	return(_select);
    }

    /**
     * Display variable as single string
     */
    public String toString() {
	return("variable("+_name+")");
    }

    /**
     * Display variable in a full AST dump
     */
    public void display(int indent) {
	indent(indent);
	System.out.println("Variable " + _name);
	if (_select != null) { 
	    indent(indent + IndentIncrement);
	    System.out.println("select " + _select.toString());
	}
	displayContents(indent + IndentIncrement);
    }

    /**
     * Returns the type of the variable
     */
    public Type getType() {
	return _type;
    }

    /**
     * Returns the name of the variable or parameter as it will occur in the
     * compiled translet.
     */
    public QName getName() {
	return _name;
    }

    /**
     * Returns the name of the variable or parameter as it occured in the
     * stylesheet.
     */
    public String getVariable() {
	return _variable;
    }

    /**
     * Set the name of the variable or paremeter. Escape all special chars.
     */
    public void setName(QName name) {
	_name = name;
	_variable = Util.escape(name.getLocalPart());
    }

    /**
     * Returns the true if the variable is local
     */
    public boolean isLocal() {
	return _isLocal;
    }

    /**
     * Parse the contents of the <xsl:decimal-format> element.
     */
    public void parseContents(Parser parser) {
	// Get the 'name attribute
	String name = getAttribute("name");
	if (name == null) name = EMPTYSTRING;

	if (name.length() > 0)
	    setName(parser.getQNameIgnoreDefaultNs(name));
        else
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");

	// Check whether variable/param of the same name is already in scope
	VariableBase other = parser.lookupVariable(_name);
	if ((other != null) && (other.getParent() == getParent())) {
	    reportError(this, parser, ErrorMsg.VARIABLE_REDEF_ERR, name);
	}
	
	select = getAttribute("select");
	if (select.length() > 0) {
	    _select = getParser().parseExpression(this, "select", null);
	    if (_select.isDummy()) {
		reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "select");
		return;
	    }
	}

	// Children must be parsed first -> static scoping
	parseChildren(parser);
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

}
