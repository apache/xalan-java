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
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.xalan.xsltc.compiler.util.Type;

import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.JavaClass;

import org.apache.xalan.xsltc.compiler.util.*;

public final class Template extends TopLevelElement {

    private QName   _name;     // The name of the template (if any)
    private QName   _mode;     // Mode in which this template is instantiated.
    private Pattern _pattern;  // Matching pattern defined for this template.
    private double  _priority; // Matching priority of this template.
    private int     _position; // Position within stylesheet (prio. resolution)
    private boolean _disabled = false;
    private boolean _compiled = false;//make sure it is compiled only once
    private boolean _hasParams = false;
    private boolean _simplified = false;

    public boolean hasParams() {
	return _hasParams;
    }

    public void hasParams(boolean hasParams) {
	_hasParams = hasParams;
    }

    public boolean isSimplified() {
	return(_simplified);
    }

    public void setSimplified() {
	_simplified = true;
    }

    public void disable() {
	_disabled = true;
    }

    public boolean disabled() {
	return(_disabled);
    }

    public double getPriority() {
	return _priority;
    }

    public int getPosition() {
	return(_position);
    }

    public boolean isNamed() {
	return _name != null;
    }

    public Pattern getPattern() {
	return _pattern;
    }

    public QName getName() {
	return _name;
    }

    public void setName(QName qname) {
	if (_name == null) _name = qname;
    }

    public QName getModeName() {
	return _mode;
    }

    /**
     * Compare this template to another. First checks priority, then position.
     */
    public int compareTo(Object template) {
	Template other = (Template)template;
	if (_priority > other._priority)
	    return 1;
	else if (_priority < other._priority)
	    return -1;
	else if (_position > other._position)
	    return 1;
	else if (_position < other._position)
	    return -1;
	else
	    return 0;
    }

    public void display(int indent) {
	Util.println('\n');
	indent(indent);
	if (_name != null) {
	    indent(indent);
	    Util.println("name = " + _name);
	}
	else if (_pattern != null) {
	    indent(indent);
	    Util.println("match = " + _pattern.toString());
	}
	if (_mode != null) {
	    indent(indent);
	    Util.println("mode = " + _mode);
	}
	displayContents(indent + IndentIncrement);
    }

    private boolean resolveNamedTemplates(Template other, Parser parser) {

	if (other == null) return true;

	SymbolTable stable = parser.getSymbolTable();

	final int us = this.getImportPrecedence();
	final int them = other.getImportPrecedence();

	if (us > them) {
	    other.disable();
	    return true;
	}
	else if (us < them) {
	    stable.addTemplate(other);
	    this.disable();
	    return true;
	}
	else {
	    return false;
	}
    }

    private Stylesheet _stylesheet = null;

    public Stylesheet getStylesheet() {
	return _stylesheet;
    }

    public void parseContents(Parser parser) {

	final String name     = getAttribute("name");
	final String mode     = getAttribute("mode");
	final String match    = getAttribute("match");
	final String priority = getAttribute("priority");

	_stylesheet = super.getStylesheet();

	if (name.length() > 0) {
	    _name = parser.getQNameIgnoreDefaultNs(name);
	}
	
	if (mode.length() > 0) {
	    _mode = parser.getQNameIgnoreDefaultNs(mode);
	}
	
	if (match.length() > 0) {
	    _pattern = parser.parsePattern(this, "match", null);
	}

	if (priority.length() > 0) {
	    _priority = Double.parseDouble(priority);
	}
	else {
	    if (_pattern != null)
		_priority = _pattern.getPriority();
	    else
		_priority = Double.NaN;
	}

	_position = parser.getTemplateIndex();

	// Add the (named) template to the symbol table
	if (_name != null) {
	    Template other = parser.getSymbolTable().addTemplate(this);
	    if (!resolveNamedTemplates(other, parser)) {
		ErrorMsg err =
		    new ErrorMsg(ErrorMsg.TEMPLATE_REDEF_ERR, _name, this);
		parser.reportError(Constants.ERROR, err);
	    }
	}

	parser.setTemplate(this);	// set current template
	parseChildren(parser);
	parser.setTemplate(null);	// clear template
    }

    /**
     * When the parser realises that it is dealign with a simplified stylesheet
     * it will create an empty Stylesheet object with the root element of the
     * stylesheet (a LiteralElement object) as its only child. The Stylesheet
     * object will then create this Template object and invoke this method to
     * force some specific behaviour. What we need to do is:
     *  o) create a pattern matching on the root node
     *  o) add the LRE root node (the only child of the Stylesheet) as our
     *     only child node
     *  o) set the empty Stylesheet as our parent
     *  o) set this template as the Stylesheet's only child
     */
    public void parseSimplified(Stylesheet stylesheet, Parser parser) {

	_stylesheet = stylesheet;
	setParent(stylesheet);

	_name = null;
	_mode = null;
	_priority = Double.NaN;
	_pattern = parser.parsePattern(this, "/");

	final Vector contents = _stylesheet.getContents();
	final SyntaxTreeNode root = (SyntaxTreeNode)contents.elementAt(0);

	if (root instanceof LiteralElement) {
	    addElement(root);
	    root.setParent(this);
	    contents.set(0, this);
	    parser.setTemplate(this);
	    root.parseContents(parser);
	    parser.setTemplate(null);
	}
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_pattern != null) {
	    _pattern.typeCheck(stable);
	}

	return typeCheckContents(stable);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_disabled) return;
	// bug fix #4433133, add a call to named template from applyTemplates 
	String className = classGen.getClassName();

	if (_compiled && isNamed()){
	    String methodName = Util.escape(_name.toString());
	    il.append(classGen.loadTranslet());
	    il.append(methodGen.loadDOM());
	    il.append(methodGen.loadIterator());
	    il.append(methodGen.loadHandler()); 
	    il.append(methodGen.loadCurrentNode()); 
	    il.append(new INVOKEVIRTUAL(cpg.addMethodref(className,
							 methodName,
							 "("
							 + DOM_INTF_SIG
							 + NODE_ITERATOR_SIG
							 + TRANSLET_OUTPUT_SIG
							 + "I)V")));
	    return;
	}

	if (_compiled) return;
	_compiled = true; 
	
	final InstructionHandle start = il.getEnd();
	translateContents(classGen, methodGen);
	final InstructionHandle end = il.getEnd();
	il.setPositions(true);
    }
}
