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
 * @author Santiago Pericas-Geertsen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

public abstract class SyntaxTreeNode implements Constants {

    /**
     * A reference to this node's parent or null it outermost.
     */
    protected SyntaxTreeNode _parent;

    /**
     * List of children of this node.
     */
    private final ArrayList _contents = new ArrayList(2);

    /**
     * The element's qualified name.
     */
    protected QName _qname;

    /**
     * The list of attributes defined in this element.
     */
    protected Attributes _attributes = null;

    /**
     * Namespace declarations of this element (as a mapping).
     */
    private HashMap _prefixMapping = null;

    /**
     * Source line where this element occurs in the input file.
     */
    private int _line;

    /**
     * A "sentinel" that is used to denote unrecognised syntaxt
     * tree nodes.
     */
    protected static final SyntaxTreeNode Dummy = new Text();

    /**
     * Creates a new SyntaxTreeNode with a 'null' QName and no source file
     * line number reference.
     */
    public SyntaxTreeNode() {
	_line = 0;
	_qname = null;
    }

    /**
     * Creates a new SyntaxTreeNode with a 'null' QName.
     * @param line Source file line number reference
     */
    public SyntaxTreeNode(int line) {
	_line = line;
	_qname = null;
    }

    /**
     * Creates a new SyntaxTreeNode with no source file line number reference.
     * @param uri The element's namespace URI
     * @param prefix The element's namespace prefix
     * @param local The element's local name
     */
    public SyntaxTreeNode(String uri, String prefix, String local) {
	_line = 0;
	setQName(uri, prefix, local);
    }

    /**
     * Returns 'true' if this syntax tree node is the Sentinal node.
     * @return 'true' if this syntax tree node is the Sentinal node.
     */
    protected final boolean isDummy() {
        return this == Dummy;
    }

    // -- Line number info  ----------------------------------------------

    /**
     * Set the source file line number for this element
     * @param line The source file line number.
     */
    protected final void setLineNumber(int line) {
	_line = line;
    }

    /**
     * Get the source file line number for this element
     * @return The source file line number.
     */
    public final int getLineNumber() {
	return _line;
    }

    // -- Node's qname  -----------------------------------------------

    /**
     * Set the QName for the syntax tree node.
     * @param qname The QName for the syntax tree node
     */
    protected void setQName(QName qname) {
	_qname = qname;
    }

    /**
     * Set the QName for the SyntaxTreeNode
     * @param uri The element's namespace URI
     * @param prefix The element's namespace prefix
     * @param local The element's local name
     */
    protected void setQName(String uri, String prefix, String localname) {
	_qname = new QName(uri, prefix, localname);
    }

    /**
     * Set the QName for the SyntaxTreeNode
     * @param qname The QName for the syntax tree node
     */
    protected QName getQName() {
	return _qname;
    }

    // -- Node's attributes  ------------------------------------------

    /**
     * Set the attributes for this SyntaxTreeNode.
     *
     * @param attributes Attributes for the element.
     */
    protected void setAttributes(Attributes attributes) {
	_attributes = attributes;
    }

    /**
     * Returns a value for an attribute from the source element.
     *
     * @param qname The QName of the attribute to return.
     * @return The value of the attribute of name 'qname' or the
     *         empty string if the attribute is not present.
     */
    protected String getAttribute(String qname) {
	if (_attributes == null) {
	    return EMPTYSTRING;
	}
	final String value = _attributes.getValue(qname);
	return (value == null || value.equals(EMPTYSTRING)) ?
	    EMPTYSTRING : value;
    }

    protected boolean hasAttribute(String qname) {
	return (_attributes != null && _attributes.getValue(qname) != null);
    }

    /**
     * Returns a list of all attributes declared for the element
     * represented by this syntax tree node.
     *
     * @return Attributes for this syntax tree node
     */
    protected Attributes getAttributes() {
	return _attributes;
    }

    // -- Node's NS declarations  -------------------------------------

    /**
     * Sets the prefix mapping for the namespaces that were declared in this
     * element. This does not include all prefix mappings in scope, so one
     * may have to check ancestor elements to get all mappings that are in
     * in scope. The prefixes must be passed in as a HashMap that maps
     * namespace prefixes (String objects) to namespace URIs (also String).
     *
     * @param mapping The HashMap containing the mappings.
     */
    protected void setPrefixMapping(HashMap mapping) {
	_prefixMapping = mapping;
    }

    /**
     * Returns a HashMap containing the prefix mappings that were declared
     * for this element. This does not include all prefix mappings in scope,
     * so one may have to check ancestor elements to get all mappings that are
     * in in scope.
     *
     * @return Prefix mappings (for this element only).
     */
    protected HashMap getPrefixMapping() {
	return _prefixMapping;
    }

    /**
     * Adds a single prefix mapping to this syntax tree node.
     *
     * @param prefix Namespace prefix.
     * @param uri Namespace URI.
     */
    protected void addPrefixMapping(String prefix, String uri) {
	if (_prefixMapping == null) {
	    _prefixMapping = new HashMap();
	}
	_prefixMapping.put(prefix, uri);
    }

    // -- Node's parent  ----------------------------------------------

    /**
     * Set this syntax tree node's parent node.
     *
     * @param parent The parent node.
     */
    protected void setParent(SyntaxTreeNode parent) {
        _parent = parent;
    }

    /**
     * Returns this syntax tree node's parent node.
     *
     * @return The parent syntax tree node.
     */
    protected final SyntaxTreeNode getParent() {
	return _parent;
    }

    // -- parse()  ----------------------------------------------

    /**
     * Parse the contents of this syntax tree nodes (child nodes, XPath
     * expressions, patterns and functions). The default behaviour is to
     * parser the syntax tree node's children.
     */
    public void parse(CompilerContext ccontext) {
	parseContents(ccontext);
    }

    /**
     * Parse all children of this syntax tree node. This method is normally
     * called by the parse() method.
     */
    protected final void parseContents(CompilerContext ccontext) {
	ArrayList locals = null;
        StaticContextImpl scontext = getStaticContext();

	final int count = _contents.size();
	for (int i = 0; i < count; i++) {
	    SyntaxTreeNode child = (SyntaxTreeNode) _contents.get(i);
	    child.parse(ccontext);

            // Is variable or parameter?
            if (child instanceof VariableBase) {
                final VariableBase var = (VariableBase) child;
                scontext.addVariable(var);
                if (locals == null) {
                    locals = new ArrayList(2);
                }
                // Collect all var names to be removed
                locals.add(var.getName());
            }
	}

	// After the last node, remove any locals from scope
	if (locals != null) {
	    final int nLocals = locals.size();
	    for (int i = 0; i < nLocals; i++) {
		scontext.removeVariable((QName) locals.get(i));
	    }
	}
    }

    // -- typeCheck()  ---------------------------------------------------

    /**
     * Type check the children of this node. The type check phase may
     * add coercions (CastExpr) to the AST.
     *
     * @param stable The compiler/parser's symbol table
     */
    public abstract Type typeCheck(CompilerContext ccontext)
        throws TypeCheckError;

    /**
     * Call typeCheck() on all child syntax tree nodes.
     *
     * @param stable The compiler/parser's symbol table
     */
    protected Type typeCheckContents(CompilerContext ccontext)
        throws TypeCheckError
    {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    SyntaxTreeNode item = (SyntaxTreeNode)_contents.get(i);
	    item.typeCheck(ccontext);
	}
	return Type.Void;
    }

    // -- translate()  ---------------------------------------------------

    /**
     * Translate this abstract syntax tree node into JVM bytecodes.
     *
     * @param classGen BCEL Java class generator
     * @param methodGen BCEL Java method generator
     */
    public abstract void translate(ClassGenerator classGen,
				   MethodGenerator methodGen);

    /**
     * Call translate() on all child syntax tree nodes.
     *
     * @param classGen BCEL Java class generator
     * @param methodGen BCEL Java method generator
     */
    protected void translateContents(ClassGenerator classGen,
				     MethodGenerator methodGen) {
	// Call translate() on all child nodes
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    final SyntaxTreeNode item = (SyntaxTreeNode)_contents.get(i);
	    item.translate(classGen, methodGen);
	}

	// After translation, unmap any registers for any variables/parameters
	// that were declared in this scope. Performing this unmapping in the
	// same AST scope as the declaration deals with the problems of
	// references falling out-of-scope inside the for-each element.
	// (the cause of which being 'lazy' register allocation for references)
	for (int i = 0; i < n; i++) {
	    if( _contents.get(i) instanceof VariableBase) {
		final VariableBase var = (VariableBase)_contents.get(i);
		var.unmapRegister(methodGen);
	    }
	}
    }

    // -- Node's import precedence  -----------------------------------

    /**
     * Get the import precedence of this element. The import precedence equals
     * the import precedence of the stylesheet in which this element occured.
     * @return The import precedence of this syntax tree node.
     */
    protected int getImportPrecedence() {
        Stylesheet stylesheet = getStylesheet();
        if (stylesheet == null) return Integer.MIN_VALUE;
        return stylesheet.getImportPrecedence();
    }

    // -- Dependency on evaluation context  ------------------------------

    /**
     * Returns true if this expression/instruction depends on the context.
     * By default, every expression/instruction depends on the context
     * unless it overrides this method. Currently used to determine if
     * result trees are compiled using procedures or little DOMs (result
     * tree fragments).
     *
     * @return 'true' if this node depends on the context.
     */
    protected boolean contextDependent() {
	return true;
    }

    /**
     * Return true if any of the expressions/instructions in the contents
     * of this node is context dependent.
     *
     * @return 'true' if the contents of this node is context dependent.
     */
    protected boolean dependentContents() {
	final int n = elementCount();
	for (int i = 0; i < n; i++) {
	    final SyntaxTreeNode item = (SyntaxTreeNode)_contents.get(i);
	    if (item.contextDependent()) {
		return true;
	    }
	}
	return false;
    }

    // -- Adding/removing children  --------------------------------------

    /**
     * Adds a child node to this syntax tree node. The node will be
     * appended to the contents list.
     *
     * @param element is the new child node.
     */
    protected final void add(SyntaxTreeNode node) {
	_contents.add(node);
	node.setParent(this);
    }

    /**
     * Inserts a node at a given position by shifting zero or more
     * nodes.
     *
     * @param element is the new child node.
     */
    protected final void add(int index, SyntaxTreeNode node) {
	_contents.add(index, node);
	node.setParent(this);
    }

    /**
     * Removed a child node of this syntax tree node.
     *
     * @param element is the child node to remove.
     */
    protected final void remove(SyntaxTreeNode node) {
	_contents.remove(node);
	node.setParent(null);
    }

    /**
     * Returns a ArrayList containing all the children nodes.
     */
    protected final ArrayList getContents() {
	return _contents;
    }

    /**
     * Queries a node to see if it has any children.
     */
    protected final boolean hasContents() {
	return (elementCount() > 0);
    }

    /**
     * Returns the number of children this node has.
     */
    protected final int elementCount() {
	return _contents.size();
    }

    /**
     * Returns an Iterator containing all the children nodes.
     */
    protected final Iterator iterator() {
	return _contents.iterator();
    }

    /**
     * Returns a child node at a given position.
     *
     * @param pos The child node's position.
     */
    protected final SyntaxTreeNode get(int n) {
	return (SyntaxTreeNode) _contents.get(n);
    }

    // -- Convenient methods to get compiler/static contexts  ------------

    /**
     * Returns a reference to the static context using this node as
     * current.
     */
    public StaticContextImpl getStaticContext() {
        return StaticContextImpl.getInstance(this);
    }

    /**
     * Returns a reference to the compiler context.
     */
    public CompilerContextImpl getCompilerContext() {
        return CompilerContextImpl.getInstance();
    }

    // -------------------------------------------------------------------
    // -- The following are just TEMPS - should go away  -----------------
    // -------------------------------------------------------------------

    // TEMP - this should go away !
    protected void reportError(SyntaxTreeNode element, Parser parser,
			       int errorCode, String message) {
	final ErrorMsg error = new ErrorMsg(errorCode, message, element);
        parser.reportError(Constants.ERROR, error);
    }

    // TEMP - this should go away !
    protected  void reportWarning(SyntaxTreeNode element, Parser parser,
				  int errorCode, String message) {
	final ErrorMsg error = new ErrorMsg(errorCode, message, element);
        parser.reportError(Constants.WARNING, error);
    }


    // TEMP - this should go away !
    public final Parser getParser() {
        return CompilerContextImpl.getInstance().getParser();
    }

    // TEMP - this should go away !
    protected final XSLTC getXSLTC() {
        return getCompilerContext().getXSLTC();
    }

    // TEMP - this should go away !
    protected Template getTemplate() {
        return getStaticContext().getCurrentTemplate();
    }

    // TEMP - this should go away !
    public Stylesheet getStylesheet() {
        return getStaticContext().getStylesheet();
    }

    // TEMP - this should go away !
    protected String lookupNamespace(String prefix) {
        return getStaticContext().getNamespace(prefix);
    }

    // TEMP - this should go away !
    protected String lookupPrefix(String uri) {
        return getStaticContext().getPrefix(uri);
    }

    // TEMP - this should go away !
    protected void compileResultTree(ClassGenerator classGen,
                                     MethodGenerator methodGen) {
    }
}