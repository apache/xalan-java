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
 * @author Morten Jorensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.*;

public abstract class SyntaxTreeNode implements Constants {

    // Reference to the AST parser
    private Parser _parser;

    // This node's line number in the input file (not obtainable!!!!)
    private final int _line;

    // Reference to this node's parent node
    private SyntaxTreeNode _parent;
    // Contains all child nodes of this node
    private final Vector   _contents = new Vector(2);

    // All parameters defined in this AST node
    private Vector _params;
    // All variables defined in this AST node
    private Vector _vars;

    // The QName of this element (contains uri, prefix and localname)
    protected QName      _qname;
    // The attributes (if any) of this element
    protected Attributes _attributes = null;
    // The namespace declarations (if any) of this element
    private   Hashtable _prefixMapping = null;

    // Sentinel
    public static final SyntaxTreeNode Dummy = new AbsolutePathPattern(null);

    // These two are used for indenting nodes in the AST (debug output)
    private static final char[] _spaces = 
	"                                                       ".toCharArray();
    protected static final int IndentIncrement = 4;

    public SyntaxTreeNode() {
	_line = 0;
	_qname = null;
    }

    public SyntaxTreeNode(int line) {
	_line = line;
	_qname = null;
    }

    public SyntaxTreeNode(String uri, String prefix, String localname) {
	_line = 0;
	setQName(uri, prefix, localname);
    }

    public void setQName(QName qname) {
	_qname = qname;
    }

    public void setQName(String uri, String prefix, String localname) {
	_qname = new QName(uri, prefix, localname);
    }

    public QName getQName() {
	return(_qname);
    }

    public void setAttributes(Attributes attributes) {
	_attributes = attributes;
    }

    public String getAttribute(String qname) {
	if (_attributes == null)
	    return(Constants.EMPTYSTRING);
	final String value = _attributes.getValue(qname);
	if (value == null)
	    return(Constants.EMPTYSTRING);
	else
	    return(value);
    }

    public Attributes getAttributes() {
	return(_attributes);
    }

    public void setPrefixMapping(Hashtable mapping) {
	_prefixMapping = mapping;
    }

    public Hashtable getPrefixMapping() {
	return _prefixMapping;
    }

    public void addPrefixMapping(String prefix, String uri) {
	if (_prefixMapping == null)
	    _prefixMapping = new Hashtable();
	_prefixMapping.put(prefix, uri);
    }

    public String lookupNamespace(String prefix) {
	// Initialise the output (default is 'null' for undefined)
	String uri = null;

	// First look up the prefix/uri mapping in our own hashtable...
	if (_prefixMapping != null)
	    uri = (String)_prefixMapping.get(prefix);
	// ... but if we can't find it there we ask our parent for the mapping
	if ((uri == null) && (_parent != null)) {
	    uri = _parent.lookupNamespace(prefix);
	    if ((prefix == Constants.EMPTYSTRING) && (uri == null))
		uri = Constants.EMPTYSTRING;
	}
	// ... and then we return whatever URI we've got.
	return(uri);
    }

    public String lookupPrefix(String uri) {
	// Initialise the output (default is 'null' for undefined)
	String prefix = null;

	// First look up the prefix/uri mapping in our own hashtable...
	if ((_prefixMapping != null) &&
	    (_prefixMapping.contains(uri))) {
	    Enumeration prefixes = _prefixMapping.keys();
	    while (prefixes.hasMoreElements()) {
		prefix = (String)prefixes.nextElement();
		String mapsTo = (String)_prefixMapping.get(prefix);
		if (mapsTo.equals(uri)) return(prefix);
	    }
	}
	// ... but if we can't find it there we ask our parent for the mapping
	else if (_parent != null) {
	    prefix = _parent.lookupPrefix(uri);
	    if ((uri == Constants.EMPTYSTRING) && (prefix == null))
		prefix = Constants.EMPTYSTRING;
	}
	return(prefix);
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
    public void parseContents(Parser parser) {
	parseChildren(parser);
    }

    /**
     * Parse all the children of <tt>element</tt>.
     * XSLT commands are recognized by the XSLT namespace
     */
    public final void parseChildren(Parser parser) {

	Vector locals = null;	// only create when needed
	
	final int count = _contents.size();
	for (int i=0; i<count; i++) {
	    SyntaxTreeNode child = (SyntaxTreeNode)_contents.elementAt(i);
	    parser.getSymbolTable().setCurrentNode(child);
	    child.parseContents(parser);
	    // if variable or parameter, add it to scope
	    final QName varOrParamName = updateScope(parser, child);
	    if (varOrParamName != null) {
		if (locals == null) {
		    locals = new Vector(2);
		}
		locals.addElement(varOrParamName);
	    }
	}

	parser.getSymbolTable().setCurrentNode(this);

	// after the last element, remove any locals from scope
	if (locals != null) {
	    final int nLocals = locals.size();
	    for (int i = 0; i < nLocals; i++) {
		parser.removeVariable((QName)locals.elementAt(i));
	    }
	}
    }
   
    /**
     * Add a node to the current scope and return name of a variable or
     * parameter if the node represents a variable or a parameter.
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

    public final void setFirstElement(SyntaxTreeNode element) {
	_contents.insertElementAt(element,0);
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
	System.out.print(new String(_spaces, 0, indent));
    }

    public final int getLineNumber() {
	return _line;
    }

    protected static void reportError(SyntaxTreeNode element, Parser parser,
				      int errorCode, String msg) {
	final ErrorMsg error = new ErrorMsg(errorCode, 0 /*lineNumber*/, msg);
        parser.reportError(Constants.ERROR, error);
    }

    protected static void reportWarning(SyntaxTreeNode element, Parser parser,
					int errorCode, String msg) {
	final ErrorMsg error = new ErrorMsg(errorCode, 0 /*lineNumber*/, msg);
        parser.reportError(Constants.WARNING, error);
    }

}
