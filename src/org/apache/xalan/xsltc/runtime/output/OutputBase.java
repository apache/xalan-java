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
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES{} LOSS OF
 * USE, DATA, OR PROFITS{} OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
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
 * @author G. Todd Miller 
 *
 */

package org.apache.xalan.xsltc.runtime.output;

import java.util.Stack;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.TransletOutputHandler;
import org.apache.xalan.xsltc.runtime.Hashtable;

public abstract class OutputBase implements TransletOutputHandler, Constants {

    /**
     * Document system identifier
     */
    protected String  _doctypeSystem = null;

    /**
     * Document public identifier
     */
    protected String  _doctypePublic = null;

    /**
     * Holds the current tree depth.
     */
    protected int _depth = 0;

    /**
     * Each entry (prefix) in this hashtable points to a Stack of URIs
     */
    protected Hashtable _namespaces;

    /** 
     * The top of this stack contains an id of the element that last declared
     * a namespace. Used to ensure prefix/uri map scopes are closed correctly
     */
    protected Stack _nodeStack;

    /** 
     * The top of this stack is the prefix that was last mapped to an URI
     */
    protected Stack _prefixStack;

    /**
     * Contains all elements that should be output as CDATA sections.
     */
    protected Hashtable _cdata = null;

    /**
     * The top of this stack contains the element id of the last element whose
     * contents should be output as CDATA sections.
     */
    protected Stack _cdataStack;

    /**
     * Set to true when a CDATA section is being output.
     */
    protected boolean _cdataTagOpen = false;

    /**
     * Set to true when a start tag is being output.
     */
    protected boolean _startTagOpen  = false;
 
    /**
     * Set to false after processing first element.
     */
    protected boolean _firstElement = true;

    /**
     * Initialize global variables
     */
    protected void initCDATA() {
	// CDATA stack
	_cdataStack = new Stack();
	_cdataStack.push(new Integer(-1)); 	// push dummy value
    }

    protected void initNamespaces() {
	// Namespaces
	_namespaces = new Hashtable();
	_nodeStack = new Stack();
	_prefixStack = new Stack();

	// Define the default namespace (initially maps to "" uri)
	Stack stack;
	_namespaces.put(EMPTYSTRING, stack = new Stack());
	stack.push(EMPTYSTRING);
	_prefixStack.push(EMPTYSTRING);

	_namespaces.put(XML_PREFIX, stack = new Stack());
	stack.push("http://www.w3.org/XML/1998/namespace");
	_prefixStack.push(XML_PREFIX);

	_nodeStack.push(new Integer(-1));
	_depth = 0;
    }

    /**
     * Set the output document system/public identifiers
     */
    public void setDoctype(String system, String pub) {
        _doctypeSystem = system;
        _doctypePublic = pub;

    }

    public void setCdataElements(Hashtable elements) { 
	_cdata = elements;
    }

 
   /**
     * TODO: This method is a HACK! Since XSLTC does not have access to the
     * XML file, it sometimes generates a NS prefix of the form "ns?" for
     * an attribute. If at runtime, when the qname of the attribute is
     * known, another prefix is specified for the attribute, then we can get
     * a qname of the form "ns?:otherprefix:name". This function patches the
     * qname by simply ignoring "otherprefix".
     */ 
    protected static String patchName(String qname) throws TransletException {
        final int lastColon = qname.lastIndexOf(':');
        if (lastColon > 0) {
            final int firstColon = qname.indexOf(':');
            if (firstColon != lastColon) {
                return qname.substring(0, firstColon) + 
		    qname.substring(lastColon);
            }
        }
        return qname;
    }

    /**
     * Declare a prefix to point to a namespace URI
     */
    protected boolean pushNamespace(String prefix, String uri) {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.startsWith(XML_PREFIX)) {
	    return false;
	}
	
	Stack stack;
	// Get the stack that contains URIs for the specified prefix
	if ((stack = (Stack)_namespaces.get(prefix)) == null) {
	    _namespaces.put(prefix, stack = new Stack());
	}

	if (!stack.empty() && uri.equals(stack.peek())) {
	    return false;
	}

	stack.push(uri);
	_prefixStack.push(prefix);
	_nodeStack.push(new Integer(_depth));
	return true;
    }

    /**
     * Undeclare the namespace that is currently pointed to by a given prefix
     */
    protected boolean popNamespace(String prefix) {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.startsWith(XML_PREFIX)) {
	    return false;
	}

	Stack stack;
	if ((stack = (Stack)_namespaces.get(prefix)) != null) {
	    stack.pop();
	    return true;
	}
	return false;
    }

    /**
     * Pop all namespace definitions that were delcared by the current element
     */
    protected void popNamespaces() {
	while (true) {
	    if (_nodeStack.isEmpty()) return;
	    Integer i = (Integer)(_nodeStack.peek());
	    if (i.intValue() != _depth) return;
	    _nodeStack.pop();
	    popNamespace((String)_prefixStack.pop());
	}
    }

    /**
     * Use a namespace prefix to lookup a namespace URI
     */
    protected String lookupNamespace(String prefix) {
        final Stack stack = (Stack)_namespaces.get(prefix);
        return stack != null && !stack.isEmpty() ? (String)stack.peek() : null;
    }

    /**
     * Returns the local name of a qualified name. If the name has 
     * no prefix, then it works as the identity (SAX2).
     */
    protected static String getLocalName(String qname) {
        final int col = qname.lastIndexOf(':');
        return (col > 0) ? qname.substring(col + 1) : qname;
    }

    /**
     * Returns the URI of an element or attribute. Note that default namespaces
     * do not apply directly to attributes.
     */
    protected String getNamespaceURI(String qname, boolean isElement)
        throws TransletException
    {
        String uri = EMPTYSTRING;
        int col = qname.lastIndexOf(':');
        final String prefix = (col > 0) ? qname.substring(0, col) : EMPTYSTRING;

        if (prefix != EMPTYSTRING || isElement) {
            uri = lookupNamespace(prefix);
            if (uri == null && !prefix.equals(XMLNS_PREFIX)) {
                BasisLibrary.runTimeError(BasisLibrary.NAMESPACE_PREFIX_ERR,
                                          qname.substring(0, col));
            }
        }
        return uri;
    }

    public void startCDATA() throws TransletException { }
    public void endCDATA() throws TransletException { }
    public void namespace(String prefix, String uri) throws TransletException { }
    public void setType(int type) { }
    public void setIndent(boolean indent) { }
    public void omitHeader(boolean value) { }
    public boolean setEscaping(boolean escape) throws TransletException { return true; }
    public void setMediaType(String mediaType) { }
    public void setStandalone(String standalone) { }
    public void setVersion(String version) { }
    public void close() { }

}
