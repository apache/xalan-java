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

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

public class StreamXMLOutput extends StreamOutput implements Constants {

    /**
     * Holds the current tree depth.
     */
    private int _depth = 0;

    /**
     * Contains all elements that should be output as CDATA sections.
     */
    private Hashtable _cdata = null;

    /**
     * The top of this stack contains the element id of the last element whose
     * contents should be output as CDATA sections.
     */
    private Stack _cdataStack;

    /** 
     * Each entry (prefix) in this hashtable points to a Stack of URIs.
     */
    private Hashtable _namespaces;
    
    /** 
     * The top of this stack contains an id of the element that last declared
     * a namespace. Used to ensure prefix/uri map scopes are closed correctly.
     */
    private Stack _nodeStack;

    /** 
     * The top of this stack is the prefix that was last mapped to an URI.
     */
    private Stack _prefixStack;

    public StreamXMLOutput(Writer writer, String encoding) {
	_writer = writer;
	_encoding = encoding;
	_is8859Encoded = encoding.equalsIgnoreCase("iso-8859-1");
	init();
    }

    public StreamXMLOutput(OutputStream out, String encoding) 
	throws IOException
    {
	try {
	    _writer = new OutputStreamWriter(out, _encoding = encoding);
	    _is8859Encoded = encoding.equalsIgnoreCase("iso-8859-1");
	    init();
	}
	catch (UnsupportedEncodingException e) {
	    _writer = new OutputStreamWriter(out, _encoding = "utf-8");
	}
    }

    /**
     * Initialize global variables
     */
    private void init() {
	// CDATA stack
	_cdataStack = new Stack();
	_cdataStack.push(new Integer(-1)); 	// push dummy value

	// Initialize namespaces
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

    public void startDocument() throws TransletException { 
	// empty
    }

    public void endDocument() throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append("/>");
	}

	try {
	    int n = 0;
	    final int length = _buffer.length();
	    final String output = _buffer.toString();

	    // Output buffer in chunks of OUTPUT_BUFFER_SIZE 
	    if (length > OUTPUT_BUFFER_SIZE) {
		do {
		    _writer.write(output, n, OUTPUT_BUFFER_SIZE);
		    n += OUTPUT_BUFFER_SIZE;
		} while (n + OUTPUT_BUFFER_SIZE < length);
	    }
	    _writer.write(output, n, length - n);
	    _writer.flush();
	}
	catch (IOException e) {
	    // ignore
	}
    }

    public void startElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append('>');
	}

	// Handle document type declaration (for first element only)
	if (_firstElement) {
	    if (_doctypeSystem != null || _doctypePublic != null) {
		appendDTD(elementName);
	    }
	    _firstElement = false;
	}

	if (_indent) {
	    indent(_lineFeedNextStartTag);
	    _lineFeedNextStartTag = true;
	    _indentNextEndTag = false;
	    _indentLevel++;
	}

	_buffer.append('<').append(elementName);
	_startTagOpen = true;
	_indentNextEndTag = false;
    }

    public void endElement(String elementName) throws TransletException { 
	if (_startTagOpen) {
	    _startTagOpen = false;
	    _buffer.append(">");
	}

	if (_indent) {
	    _indentLevel --;
	    if (_indentNextEndTag) {
		indent(_indentNextEndTag);
		_indentNextEndTag = true;
	    }
	}
    }

    public void characters(String characters)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}

	if (_escaping) {
	    escapeCharacters(characters.toCharArray(), 0, characters.length());
	}
	else {
	    _buffer.append(characters);
	}
    }

    public void characters(char[] characters, int offset, int length)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}

	if (_escaping) {
	    escapeCharacters(characters, offset, length);
	}
	else {
	    _buffer.append(characters, offset, length);
	}
    }

    public void attribute(String attributeName, String attributeValue)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append(' ').append(attributeName)
		   .append("=\"").append(attributeValue)
		   .append('"');
	}
    }

    public void comment(String comment) throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}
	_buffer.append("<!--").append(comment).append("-->");
    }

    public void processingInstruction(String target, String data)
	throws TransletException 
    { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}
	_buffer.append("<?").append(target).append(data).append("?>");
    }

    public boolean setEscaping(boolean escape) throws TransletException 
    { 
	final boolean temp = _escaping;
	_escaping = escape;
	return temp; 
    }

    public void close() { 
	try {
	    _writer.close();
	}
	catch (Exception e) {
	    // ignore
	}
    }

    public void setCdataElements(Hashtable elements) { 
	_cdata = elements;
    }

    public void namespace(final String prefix, final String uri)
	throws TransletException 
    {
	if (_startTagOpen) {
	    pushNamespace(prefix, uri);
	}
	else if (prefix != EMPTYSTRING || uri != EMPTYSTRING) {
	    BasisLibrary.runTimeError(BasisLibrary.STRAY_NAMESPACE_ERR,
				      prefix, uri);
	}
    }

    /**
     * Declare a prefix to point to a namespace URI
     */
    private void pushNamespace(String prefix, String uri) {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.equals(XML_PREFIX)) {
	    return;
	}
	
	Stack stack;
	// Get the stack that contains URIs for the specified prefix
	if ((stack = (Stack)_namespaces.get(prefix)) == null) {
	    _namespaces.put(prefix, stack = new Stack());
	}
	else if (uri.equals(stack.peek())) {
	    return;	// Ignore
	}

	stack.push(uri);
	_prefixStack.push(prefix);
	_nodeStack.push(new Integer(_depth));
    }

    /**
     * Undeclare the namespace that is currently pointed to by a given prefix
     */
    private void popNamespace(String prefix) {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.equals(XML_PREFIX)) return;

	Stack stack;
	if ((stack = (Stack)_namespaces.get(prefix)) != null) {
	    stack.pop();
	}
    }

    /**
     * Pop all namespace definitions that were delcared by the current element
     */
    private void popNamespaces() throws TransletException {
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
    private String lookupNamespace(String prefix) {
	final Stack stack = (Stack)_namespaces.get(prefix);
	return stack != null && !stack.isEmpty() ? (String)stack.peek() : null;
    }

}
