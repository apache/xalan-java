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
 * @author G. Todd Miller
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.*;

public abstract class SyntaxTreeNode implements Constants {
    private Parser _parser;
    private final int _line;			// line number in input
    private final Vector _contents = new Vector(2);

    private static final int NSpaces = 64;
    private static final char[] Spaces = new char[NSpaces];
    protected static final int IndentIncrement = 4;
    public static final SyntaxTreeNode Dummy = new AbsolutePathPattern(null);

    /**
     * A vector of references to all the parameters defined in
     * this node.
     */
    private Vector _params;

    /**
     * A vector of references to all the local variables defined
     * in this node.
     */
    private Vector _vars;

    private SyntaxTreeNode _parent;

    static {
	for (int i = 0; i < NSpaces; i++)
	    Spaces[i] = ' ';
    }

    public SyntaxTreeNode() {
	_line = 0;
    }

    public SyntaxTreeNode(int line) {
	_line = line;
    }

    public void setParser(Parser parser) {
	_parser = parser;
    }

    protected final void setParent(SyntaxTreeNode parent) {
	if (_parent == null)
	    _parent = parent;
    }
    
    public final SyntaxTreeNode getParent() {
	return _parent;
    }

    protected Stylesheet getStylesheet() {
	SyntaxTreeNode parent = this;
	while (parent != null) {
	    if (parent instanceof Stylesheet)
		return((Stylesheet)parent);
	    parent = parent.getParent();
	}
	return((Stylesheet)parent);
    }

    public int getImportPrecedence() {
	return getStylesheet().getImportPrecedence();
    }

    protected Template getTemplate() {
	SyntaxTreeNode parent = this;
	while ((parent != null) && (!(parent instanceof Template)))
	    parent = parent.getParent();
	return((Template)parent);
    }

    public final boolean isDummy() {
        return this == Dummy;
    }

    public final Parser getParser() {
	return _parser;
    }

    public final XSLTC getXSLTC() {
	return _parser.getXSLTC();
    }

    //!! are these needed?

    public int addParam(Param param) {
	if (_params == null) {
	    _params = new Vector(4);
	}
	_params.addElement(param);
	return _params.size() - 1;
    }

    public final boolean hasParams() {
	return _params != null;
    }

    /**
     * To be overridden in nodes implemented by code outside of applyTemplates
     * eg. in Predicates
     */
    protected boolean isClosureBoundary() {
	return false;
    }

    /**
     * This method is normally overriden by subclasses.
     * By default, it parses all the children of <tt>element</tt>.
     */
    public void parseContents(Element element, Parser parser) {
	parseChildren(element, parser);
    }

    /**
     * Parse all the children of <tt>element</tt>.
     * XSLT commands are recognized by the XSLT namespace
     */
    public final void parseChildren(Element element, Parser parser) {
	final NodeList nl = element.getChildNodes();
	final int n = nl != null ? nl.getLength() : 0;
	Vector locals = null;	// only create when needed

	for (int i = 0; i < n; i++) {
	    final Node node = nl.item(i);
	    switch (node.getNodeType()) {
	    case Node.ELEMENT_NODE:
		
		final Element child = (Element)node;
		// Add namespace declarations to symbol table
		parser.pushNamespaces(child);
		final SyntaxTreeNode instance = parser.makeInstance(child);
		addElement(instance);
		if (!(instance instanceof Fallback))
		    instance.parseContents(child, parser);

		// if variable or parameter, add it to scope
		final QName varOrParamName = updateScope(parser, instance);
		if (varOrParamName != null) {
		    if (locals == null) {
			locals = new Vector(2);
		    }
		    locals.addElement(varOrParamName);
		}
		// Remove namespace declarations from symbol table
		parser.popNamespaces(child);
		break;
		
	    case Node.TEXT_NODE:
		// !!! need to take a look at whitespace stripping
		final String temp = node.getNodeValue();
		if (temp.trim().length() > 0) {
		    addElement(new Text(temp));
		}
		break;
	    }
	}
	
	// after the last element, remove any locals from scope
	if (locals != null) {
	    final int nLocals = locals.size();
	    for (int i = 0; i < nLocals; i++) {
		parser.removeVariable((QName)locals.elementAt(i));
	    }
	}
    }
   
    /** if node represents a variable or a parameter,
	add it to the current scope and return name of the var/par
    */
    protected QName updateScope(Parser parser, SyntaxTreeNode node) {
	if (node instanceof Variable) {
	    final Variable var = (Variable)node;
	    parser.addVariable(var);
	    return var.getName();
	}
	else if (node instanceof Param) {
	    final Param param = (Param)node;
	    parser.addParameter(param);
	    return param.getName();
	}
	else {
	    return null;
	}
    }

    /**
     * Type check the children of this node. The type check phase may add
     * coercions (CastExpr) to the AST.
     */
    public abstract Type typeCheck(SymbolTable stable) throws TypeCheckError;

    /**
     * Call typeCheck() on every children of this node.
     */
    public Type typeCheckContents(SymbolTable stable) throws TypeCheckError {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    SyntaxTreeNode item = (SyntaxTreeNode)_contents.elementAt(i);
	    item.typeCheck(stable);
	}
	return Type.Void;
    }

    /**
     * Translate this node into JVM bytecodes.
     */
    public abstract void translate(ClassGenerator classGen,
				   MethodGenerator methodGen);

    /**
     * Call translate() on every children of this node.
     */
    public void translateContents(ClassGenerator classGen,
				  MethodGenerator methodGen) {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    final SyntaxTreeNode item = (SyntaxTreeNode)_contents.elementAt(i);
	    item.translate(classGen, methodGen);
	}
    }

    /**
     * Returns true if this expression/instruction depends on the context. By 
     * default, every expression/instruction depends on the context unless it 
     * overrides this method. Currently used to determine if result trees are 
     * compiled using procedures or little DOMs.
     */
    public boolean contextDependent() {
	return true;
    }

    /**
     * Return true if any of the expressions/instructions in the contents of
     * this node is context dependent.
     */
    public boolean dependentContents() {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    final SyntaxTreeNode item = (SyntaxTreeNode)_contents.elementAt(i);
	    if (item.contextDependent()) {
		return true;
	    }
	}
	return false;
    }

    public final void addElement(SyntaxTreeNode element) {
	_contents.addElement(element);
	element.setParent(this);
    }

    public final void removeElement(SyntaxTreeNode element) {
	_contents.remove(element);
	element.setParent(null);
    }

    public final Vector getContents() {
	return _contents;
    }

    public final boolean hasContents() {
	return elementCount() > 0;
    }

    public final int elementCount() {
	return _contents.size();
    }
	
    public final Enumeration elements() {
	return _contents.elements();
    }

    public final Object elementAt(int i) {
	return _contents.elementAt(i);
    }

    public void display(int indent) {
	displayContents(indent);
    }

    protected void displayContents(int indent) {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    SyntaxTreeNode item = (SyntaxTreeNode)_contents.elementAt(i);
	    item.display(indent);
	}
    }

    protected final void indent(int indent) {
	System.out.print(new String(Spaces, 0, indent));
    }

    public final int getLineNumber() {
	return _line;
    }

    protected static void reportError(Element element, Parser parser,
				      int errorCode, String errMsg) {
	//final int lineNumber = ((Integer)element.getUserObject()).intValue();
	final ErrorMsg error = new ErrorMsg(errorCode, -1 /*lineNumber*/, errMsg);
        parser.addError(error);
    }

    protected static void reportWarning(Element element, Parser parser,
				      int errorCode, String errMsg) {
	//final int lineNumber = ((Integer)element.getUserObject()).intValue();
	final ErrorMsg error = new ErrorMsg(errorCode, -1 /*lineNumber*/, errMsg);
        parser.addWarning(error);
    }


}
