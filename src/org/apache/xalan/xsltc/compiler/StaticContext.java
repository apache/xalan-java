/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import java.text.Collator;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.xalan.xsltc.compiler.util.*;

public final class StaticContext {

    /**
     * A reference to the current node. This is needed to search for
     * scoped info such as namespace declarations.
     */
    private SyntaxTreeNode _currentNode = null;

    /**
     * A thread local variable that holds the static context. Multiple
     * calls to getInstance() will return the same variable.
     */
    static private ThreadLocal _staticContext = new ThreadLocal();

    /**
     * The method getInstance() should be used instead.
     */
    private StaticContext() {
    }

    /**
     * Clear cached values every time a new node is set as current.
     */
    private void clearCache() {
	_cachedTemplate = null;
    }

    /**
     * An instance of the this class can be obtained only by calling
     * this static method.
     */
    public static StaticContext getInstance(SyntaxTreeNode current) {
        StaticContext result = (StaticContext) _staticContext.get();
        if (result == null) {
            _staticContext.set(result = new StaticContext());
        }
        result.setCurrentNode(current);
	result.clearCache();
        return result;
    }

    /**
     * Set current node.
     */
    public void setCurrentNode(SyntaxTreeNode currentNode) {
	_currentNode = currentNode;
    }

    // -- Decimal Formats ------------------------------------------------

    private HashMap _decimalFormats = null;

    public DecimalFormatting getDecimalFormatting(QName name) {
	if (_decimalFormats == null) return null;
	return (DecimalFormatting)_decimalFormats.get(name);
    }

    public void addDecimalFormatting(QName name, DecimalFormatting symbols) {
	if (_decimalFormats == null) _decimalFormats = new HashMap();
	_decimalFormats.put(name, symbols);
    }

    // -- Stylesheet -----------------------------------------------------

    /**
     * A reference to the stylesheet object.
     */
    private Stylesheet _stylesheet = null;

    /**
     * Returns a reference to the current stylesheet. If not set, then
     * search for an ancestor of the current node.
     */
    public Stylesheet getCurrentStylesheet() {
	if (_stylesheet == null) {
	    SyntaxTreeNode parent = _currentNode;
	    while (parent != null && parent instanceof Stylesheet == false) {
		parent = parent.getParent();
	    }
	    _stylesheet = (Stylesheet) parent;
	}
	return _stylesheet;
    }

    /**
     * Sets the stylesheet object.
     */
    public void setCurrentStylesheet(Stylesheet stylesheet) {
	_stylesheet = stylesheet;
    }

    // -- Templates ------------------------------------------------------

    /**
     * A mapping between qnames and template objects.
     */
    private HashMap _templates = null;

    /**
     * A cached reference to the current template.
     */
    private Template _cachedTemplate = null;

    /**
     * Adds a named template to the static context.
     */
    public Template addTemplate(Template template) {
	final QName name = template.getName();
	if (_templates == null) _templates = new HashMap();
	return (Template)_templates.put(name, template);
    }

    /**
     * Returns the template object associated to 'name'. These names
     * are used by xsl:call-template.
     */
    public Template getTemplate(QName name) {
	if (_templates == null) return null;
	return (Template)_templates.get(name);
    }

    /**
     * Returns a reference to the "current" template or null if
     * the current node is top-level and it is not a template.
     */
    public Template getCurrentTemplate() {
	if (_cachedTemplate == null) {
	    SyntaxTreeNode parent = _currentNode;
	    while (parent != null && parent instanceof Template == false) {
		parent = parent.getParent();
	    }
	    _cachedTemplate = (Template) parent;
	}
	return _cachedTemplate;
    }

    // -- Variables/Parameters -------------------------------------------

    private HashMap _variables = null;

    /**
     * Add a variable or a parameter to the static context. A stack
     * is used for scoping.
     */
    public void addVariable(VariableBase var) {
	if (_variables == null) {
            _variables = new HashMap();
	}
	final QName qname = var.getName();
	Object oldvar = _variables.put(qname, var);
        if (oldvar instanceof Stack) {
            ((Stack) oldvar).push(var);
        }
        else if (oldvar instanceof VariableBase) {
            Stack scopes = new Stack();
            scopes.push(oldvar);
            scopes.push(var);
            _variables.put(qname, scopes);
        }
        else {
            // assert(var == null);
        }
    }

    /**
     * Get the variable or parameter bound to 'qname'. Returns null
     * if the name is unbound.
     */
    public VariableBase getVariable(QName qname) {
	if (_variables == null) return null;
        final Object var = _variables.get(qname);
        if (var instanceof Stack) {
            return (VariableBase) ((Stack) var).peek();
        }
        return (VariableBase) var;
    }

    /**
     * Remove a variable from the static context (if present).
     */
    public void removeVariable(QName qname) {
        if (_variables != null) {
            Object var = _variables.get(qname);
            if (var instanceof Stack) {
                final Stack stack = (Stack) var;
                stack.pop();
                if (stack.isEmpty()) {
                    _variables.remove(qname);
                }
            }
            else if (var instanceof VariableBase) {
                _variables.remove(qname);
            }
            else {
                // assert(var == null);
            }
        }
    }

    // -- Attribute Sets  ------------------------------------------------

    private HashMap _attributeSets = null;

    public AttributeSet addAttributeSet(AttributeSet atts) {
	if (_attributeSets == null) _attributeSets = new HashMap();
	return (AttributeSet)_attributeSets.put(atts.getName(), atts);
    }

    public AttributeSet getAttributeSet(QName name) {
	if (_attributeSets == null) return null;
	return (AttributeSet)_attributeSets.get(name);
    }

    // -- Primops  -------------------------------------------------------

    private final HashMap _primops = new HashMap();

    /**
     * Add a primitive operator or function to the symbol table. To avoid
     * name clashes with user-defined names, the prefix <tt>PrimopPrefix</tt>
     * is prepended.
     */
    public void addPrimop(String name, MethodType mtype) {
	ArrayList methods = (ArrayList)_primops.get(name);
	if (methods == null) {
	    _primops.put(name, methods = new ArrayList());
	}
	methods.add(mtype);
    }

    /**
     * Lookup a primitive operator or function in the symbol table by
     * prepending the prefix <tt>PrimopPrefix</tt>.
     */
    public ArrayList getPrimop(String name) {
	return (ArrayList)_primops.get(name);
    }

    // -- Namespaces  ----------------------------------------------------

    /**
     * Found the URI bound to 'prefix' starting at the current node
     * and going upwards. Returns null if 'prefix' is undefined.
     */
    public String getNamespace(String prefix) {
        String result = null;

        SyntaxTreeNode parent = _currentNode;
        while (parent != null && result == null) {
            HashMap map = _currentNode.getPrefixMapping();
            if (map != null) {
                result = (String) map.get(prefix);
            }
            parent = parent.getParent();
        }

        // If not bound, check default namespace
        if (result == null && prefix.length() == 0) {
            return Constants.EMPTYSTRING;
        }
        return result;
    }

    /**
     * Returns one of the prefixes mapped to 'uri' or null if no prefix
     * is found.
     */
    public String getPrefix(String uri) {
        SyntaxTreeNode parent = _currentNode;

        while (parent != null) {
            HashMap prefixMapping = _currentNode.getPrefixMapping();

            if (prefixMapping != null) {
                Iterator prefixes = prefixMapping.keySet().iterator();
                while (prefixes.hasNext()) {
                    final String prefix = (String) prefixes.next();
                    final String mapsTo = (String) prefixMapping.get(prefix);
                    if (mapsTo.equals(uri)) return prefix;
                }
            }
            parent = parent.getParent();
        }
        return null;
    }

    // -- Prefix aliases  ------------------------------------------------

    private HashMap _aliases = null;

    /**
     * Adds an alias for a namespace prefix
     */
    public void addPrefixAlias(String prefix, String alias) {
	if (_aliases == null) _aliases = new HashMap();
	_aliases.put(prefix,alias);
    }

    /**
     * Retrieves any alias for a given namespace prefix
     */
    public String getPrefixAlias(String prefix) {
	if (_aliases == null) return null;
	return (String)_aliases.get(prefix);
    }

    // -- Collations  ----------------------------------------------------

    public Collator getCollator(String uri) {
        return null;    // TODO
    }

    public Collator getDefaultCollator() {
        return null;    // TODO
    }

    // -- XPath 1.0 compatible flag --------------------------------------

    private boolean _xpath10CompatibleFlag = true;

    public boolean getXPath10CompatibleFlag() {
        return _xpath10CompatibleFlag;
    }

    public void setXPath10CompatibleFlag(boolean flag) {
        _xpath10CompatibleFlag = flag;
    }

    // -- Base URI -------------------------------------------------------

    private String _baseURI;

    public String getBaseURI() {
        return _baseURI;
    }

    public void setBaseURI(String uri) {
        _baseURI = uri;
    }

    // -- Default NS for elements and types ------------------------------

    private String _defaultElementNamespace;

    public String getDefaultElementNamespace() {
        return _defaultElementNamespace;
    }

    public void setDefaultElementNamespace(String uri) {
        _defaultElementNamespace = uri;
    }

    public String getDefaultTypeNamespace() {
        return getDefaultElementNamespace();
    }

    public void setDefaultTypeNamespace(String uri) {
        setDefaultElementNamespace(uri);
    }

    // -- Default NS for functions  --------------------------------------

    private String _defaultFunctionNamespace;

    public String getDefaultFunctionNamespace() {
        return _defaultFunctionNamespace;
    }

    public void setDefaultFunctionNamespace(String uri) {
        _defaultFunctionNamespace = uri;
    }

    // -- Schema type definitions  ---------------------------------------

    public Type getSchemaType(QName name) {
        return null;    // TODO
    }

    public void setSchemaType(QName name, Type type) {
        // TODO;
    }

    // -- Namespace exclusions  ------------------------------------------

    private HashMap _excludedURI = null;

    /**
     * Register a namespace URI so that it will not be declared in the
     * output unless it is actually referenced in the output.
     */
    public void setExcludeURI(String uri) {
        // The null-namespace cannot be excluded
        if (uri == null) return;

        // Create new HashMap of exlcuded URIs if none exists
        if (_excludedURI == null) _excludedURI = new HashMap();

        // Register the namespace URI
        Integer refcnt = (Integer)_excludedURI.get(uri);
        if (refcnt == null)
            refcnt = new Integer(1);
        else
            refcnt = new Integer(refcnt.intValue() + 1);
        _excludedURI.put(uri, refcnt);
    }

    /**
     * Exclude a series of namespaces given by a list of whitespace
     * separated namespace prefixes.
     */
    public void setExcludePrefixes(String prefixes) {
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                final String uri;
                if (prefix.equals("#default"))
                    uri = getNamespace(Constants.EMPTYSTRING);
                else
                    uri = getNamespace(prefix);
                if (uri != null) setExcludeURI(uri);
            }
        }
    }

    /**
     * Check if a namespace should not be declared in the output
     * (unless used).
     */
    public boolean getExcludeUri(String uri) {
        if (uri != null && _excludedURI != null) {
            final Integer refcnt = (Integer)_excludedURI.get(uri);
            return (refcnt != null && refcnt.intValue() > 0);
        }
        return false;
    }

    /**
     * Turn off namespace declaration exclusion.
     */
    public void setUnexcludePrefixes(String prefixes) {
        if (_excludedURI == null) return;
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                final String uri;
                if (prefix.equals("#default"))
                    uri = getNamespace(Constants.EMPTYSTRING);
                else
                    uri = getNamespace(prefix);
                Integer refcnt = (Integer)_excludedURI.get(uri);
                if (refcnt != null)
                    _excludedURI.put(uri, new Integer(refcnt.intValue() - 1));
            }
        }
    }

    // -- SHOULD BE MOVED OUT OF THIS CLASS !!!! -------------------------

    private final HashMap _stylesheets = new HashMap();

    public Stylesheet addStylesheet(QName name, Stylesheet node) {
        return (Stylesheet)_stylesheets.put(name, node);
    }

    public Stylesheet getStylesheet(QName name) {
        return (Stylesheet)_stylesheets.get(name);
    }
}
