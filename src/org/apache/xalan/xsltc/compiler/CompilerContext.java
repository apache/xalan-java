/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.util.HashMap;
import java.util.ArrayList;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.compiler.codemodel.CmClassDecl;
import org.apache.xalan.xsltc.compiler.codemodel.CmMethodDecl;

public class CompilerContext {

    /**
     * A thread local variable that holds the compiler context. Multiple
     * calls to getInstance() will return the same variable.
     */
    static private ThreadLocal _compilerContext = new ThreadLocal();

    /**
     * A reference to the XSLTC object.
     */
    private XSLTC _xsltc;

    /**
     * A reference to the parser object.
     */
    private Parser _parser;

    /**
     * The method getInstance() should be used instead.
     */
    private CompilerContext() {
        init();
    }

    /**
     * Resets the state of the compiler context.
     */
    public void init() {
        _nextGType = DOM.NTYPES;
        _elements = new HashMap();
        _attributes = new HashMap();
        _namespaces = new HashMap();
        _namespaces.put("", new Integer(_nextNSType));
        _namesIndex = new ArrayList(128);
        _namespaceIndex = new ArrayList(32);
    }

    /**
     * This method must be called the first time an instance of the
     * compiler context is created to ensure that a reference to the
     * parser is provided.
     */
    static CompilerContext getInstance(XSLTC xsltc) {
        CompilerContext result = (CompilerContext) _compilerContext.get();
        if (result == null) {
            _compilerContext.set(result = new CompilerContext());
        }
        result._xsltc = xsltc;
        result._parser = xsltc.getParser();
        return result;
    }

    /**
     * This method can be called to obtain a instance to the compiler
     * context after getInstance(XSLTC) has been invoked (otherwise
     * it will throw an exception).
     */
    static CompilerContext getInstance() {
        CompilerContext result = (CompilerContext) _compilerContext.get();
        if (result == null) {
            throw new IllegalStateException(
                "CompilerContext.getInstance(XSLTC) must be called first.");
        }
        return (CompilerContext) _compilerContext.get();
    }

    /**
     * Returns a reference to the XSLTC object.
     */
    public XSLTC getXSLTC() {
        return _xsltc;
    }

    /**
     * Returns a reference to the parser object.
     */
    public Parser getParser() {
        return _parser;
    }

    // -- Current CM Class  ----------------------------------------------

    /**
     * A reference to the "current" codemodel class object.
     */
    private CmClassDecl _currentClass;

    /**
     * Returns a reference to the "current" class object from the
     * codemodel package.
     */
    public CmClassDecl getCurrentClass() {
        return _currentClass;
    }

    /**
     * Set the current codemodel class object. This method must be
     * called whenever a new class is generated.
     */
    public void setCurrentClass(CmClassDecl currentClass) {
        _currentClass = currentClass;
    }

    // -- Current CM Method  ----------------------------------------------

    /**
     * A refernece to the "current" codemodel method object.
     */
    private CmMethodDecl _currentMethod;

    /**
     * Returns a reference to the "current" method object from the
     * codemodel package.
     */
    public CmMethodDecl getCurrentMethod() {
        return _currentMethod;
    }

    /**
     * Set the current codemodel method object. This method must be
     * called whenever a new method is generated.
     */
    public void setCurrentMethod(CmMethodDecl currentMethod) {
        _currentMethod = currentMethod;
    }

    // -- Fresh NS prefixes -----------------------------------------------

    /**
     * This is used for xsl:attribute elements that have a "namespace"
     * attribute that is currently not defined using xmlns:
     */
    private int _nsCounter = 0;

    public String getFreshNsPrefix() {
        return new String("ns" + _nsCounter++);
    }

    // -- Template inlining -----------------------------------------------

    private boolean _templateInlining = false;

    public boolean getTemplateInlining() {
	return _templateInlining;
    }

    public void setTemplateInlining(boolean value) {
	_templateInlining = value;
    }

    // -- Nodeset call ----------------------------------------------------

    private boolean _callsNodeset = false;

    public boolean getCallsNodeset() {
	return _callsNodeset;
    }

    public void setCallsNodeset(boolean value) {
	_callsNodeset = value;
    }

    // -- Multi-document --------------------------------------------------

    private boolean _multiDocument = false;

    public boolean getMultiDocument() {
	return _multiDocument;
    }

    public void setMultiDocument(boolean value) {
	_multiDocument = value;
    }

    // -- Fresh helper class names ---------------------------------------

    private int _helperClassSerial  = 0;

    public String getFreshClassName() {
        return new String(_xsltc.getClassName() + _helperClassSerial++);
    }

    // -- Name mapping ---------------------------------------------------

    /**
     * Next available attribute/element type.
     */
    private int _nextGType;

    /**
     * Mapping between expanded element qnames (Strings) and generalized
     * types (Integers).
     */
    private HashMap _elements;

    /**
     * Mapping between expanded attribute qnames (Strings) and generalized
     * types (Integers).
     */
    private HashMap _attributes;

    /**
     * List of all elements that are known at compile. Can be regarded
     * as a reverse mapping between generalized types and expanded
     * qnames (by adding DOM.NTYPES to the index).
     */
    private ArrayList _namesIndex;

    /**
     * Next available namespace type.
     */
    private int _nextNSType;

    /**
     * Mapping between namespace URIs (Strings) and generalized
     * NS types (Integers).
     */
    private HashMap _namespaces;

    /**
     * List of all namespace URIs that are known at compile. Can be
     * regarded as a reverse mapping between generalized types and
     * expanded qnames.
     */
    private ArrayList _namespaceIndex;

    /**
     * Registers an attribute and gives it a type so that it can be
     * mapped to DOM attribute types at run-time.
     */
    public int registerAttribute(QName name) {
        String expandedQName = name.toString();
        Integer code = (Integer)_attributes.get(expandedQName);

        if (code == null) {
            code = new Integer(_nextGType++);
            _attributes.put(name.toString(), code);

            // Insert a '@' between ':' and local part
            final String uri = name.getNamespace();
            final String local = "@" + name.getLocalPart();
            if (uri != null && uri.length() > 0) {
                _namesIndex.add(uri + ":" + local);
            }
            else {
                _namesIndex.add(local);
            }

            if (name.getLocalPart().equals("*")) {
                registerNamespace(name.getNamespace());
            }
        }
        return code.intValue();
    }

    /**
     * Registers an element and gives it a type so that it can be
     * mapped to DOM element types at run-time.
     */
    public int registerElement(QName name) {
        // Register element (full QName)
        Integer code = (Integer)_elements.get(name.toString());
        if (code == null) {
            _elements.put(name.toString(), code = new Integer(_nextGType++));
            _namesIndex.add(name.toString());
        }
        if (name.getLocalPart().equals("*")) {
            registerNamespace(name.getNamespace());
        }
        return code.intValue();
    }

    /**
     * Registers a namespace and gives it a type so that it can be mapped to
     * DOM namespace types at run-time.
     */
    public int registerNamespace(String namespaceURI) {
        Integer code = (Integer)_namespaces.get(namespaceURI);
        if (code == null) {
            code = new Integer(_nextNSType++);
            _namespaces.put(namespaceURI,code);
            _namespaceIndex.add(namespaceURI);
        }
        return code.intValue();
    }

    /**
     * Registers a namespace prefix. This is needed to handle an XPath
     * expression of the form "namespace::prefix".
     */
    public int registerPrefix(String prefix) {
        return 0;    // TODO
    }

    /**
     * Returns the list of statically known element/attribute names.
     */
    public ArrayList getNamesIndex() {
        return _namesIndex;
    }

    /**
     * Returns the list of statically known namespace URIs.
     */
    public ArrayList getNamespaceIndex() {
        return _namespaceIndex;
    }
}
