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
import java.util.HashSet;
import java.util.Iterator;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

public class StreamXMLOutput extends StreamOutput implements Constants {

    private static final String BEGCDATA = "<![CDATA[";
    private static final String ENDCDATA = "]]>";
    private static final String CNTCDATA = "]]]]><![CDATA[>";
    private static final String BEGCOMM  = "<!--";
    private static final String ENDCOMM  = "-->";
    private static final String CDATA_ESC_START = "]]>&#";
    private static final String CDATA_ESC_END   = ";<![CDATA[";

    /**
     * Holds the current tree depth.
     */
    private int _depth = 0;

    /**
     * Each entry (prefix) in this hashtable points to a Stack of URIs
     */
    private Hashtable _namespaces;

    /** 
     * The top of this stack contains an id of the element that last declared
     * a namespace. Used to ensure prefix/uri map scopes are closed correctly
     */
    private Stack _nodeStack;

    /** 
     * The top of this stack is the prefix that was last mapped to an URI
     */
    private Stack _prefixStack;

    /**
     * Contains all elements that should be output as CDATA sections.
     */
    private Hashtable _cdata = null;

    /**
     * The top of this stack contains the element id of the last element whose
     * contents should be output as CDATA sections.
     */
    private Stack _cdataStack;

    private boolean _cdataTagOpen = false;

    private HashSet _attributes = new HashSet();

    static class Attribute {
	public String name, value;

	Attribute(String name, String value) {
	    this.name = name; 
	    this.value = value;
	}

	public int hashCode() {
	    return name.hashCode();
	}

	public boolean equals(Object obj) {
	    try {
		return name.equalsIgnoreCase(((Attribute) obj).name);
	    }
	    catch (ClassCastException e) {
		return false;
	    }
	}
    }

    public StreamXMLOutput(Writer writer, String encoding) {
	super(writer, encoding);
	init();
    }

    public StreamXMLOutput(OutputStream out, String encoding) 
	throws IOException
    {
	super(out, encoding);
	init();
    }

    /**
     * Initialize global variables
     */
    private void init() {
	// CDATA stack
	_cdataStack = new Stack();
	_cdataStack.push(new Integer(-1)); 	// push dummy value

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

    public void startDocument() throws TransletException { 
// System.out.println("startDocument");
	if (!_omitHeader) {
	    final StringBuffer header = new StringBuffer("<?xml version=\"");
	    header.append(_version).append("\" encoding=\"").append(_encoding);
	    if (_standalone != null) {
		header.append("\" standalone=\"").append(_standalone);
	    }
	    header.append("\"?>\n");

	    // Always insert header at the beginning 
	    _buffer.insert(0, header.toString());
	}
    }

    public void endDocument() throws TransletException { 
// System.out.println("endDocument");
	if (_startTagOpen) {
	    _buffer.append("/>");
	}
	else if (_cdataTagOpen) {
	    closeCDATA();
	}

	// Finally, output buffer to writer
	outputBuffer();
    }

    public void startElement(String elementName) throws TransletException { 
// System.out.println("startElement = " + elementName);
	if (_startTagOpen) {
	    _buffer.append('>');
	}
	else if (_cdataTagOpen) {
	    closeCDATA();
	}

	// Handle document type declaration (for first element only)
	if (_firstElement) {
	    if (_doctypeSystem != null || _doctypePublic != null) {
		appendDTD(elementName);
	    }
	    _firstElement = false;
	}

	if (_cdata != null && _cdata.containsKey(elementName)) {
	    _cdataStack.push(new Integer(_depth));
	}

	if (_indent) {
	    indent(_lineFeedNextStartTag);
	    _lineFeedNextStartTag = true;
	    _indentNextEndTag = false;
	    _indentLevel++;
	}

	_buffer.append('<').append(elementName);

	_depth++;
	_startTagOpen = true;
	_attributes.clear();
    }

    public void endElement(String elementName) throws TransletException { 
// System.out.println("endElement = " + elementName);
	if (_cdataTagOpen) {
	    closeCDATA();
	}

	if (_startTagOpen) {
	    _startTagOpen = false;
	    _buffer.append("/>");
	    _indentLevel--;
	    _indentNextEndTag = true;
	}
	else {
	    if (_indent) {
		_indentLevel--;

		if (_indentNextEndTag) {
		    indent(_indentNextEndTag);
		    _indentNextEndTag = true;
		    _lineFeedNextStartTag = true;
		}
	    }
	    _buffer.append("</").append(elementName).append('>');
	}

	popNamespaces();
	_depth--;
    }

    public void characters(String characters) throws TransletException { 
// System.out.println("characters() '" + characters + "'");
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}

	final Integer I = (Integer) _cdataStack.peek();
	if (I.intValue() == _depth && !_cdataTagOpen) {
	    startCDATA(characters.toCharArray(), 0, characters.length());
	} 
	else if (_escaping) {
	    if (_cdataTagOpen) {
		escapeCDATA(characters.toCharArray(), 0, 
			    characters.length());
	    } 
	    else {
		escapeCharacters(characters.toCharArray(), 0, 
			         characters.length());
	    }
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

    public void attribute(String name, String value)
	throws TransletException 
    { 
// System.out.println("attribute = " + name);
	if (_startTagOpen) {
	    final Attribute attr = new Attribute(name, value);

	    if (!_attributes.contains(attr)) {
		_buffer.append(' ').append(name).append("=\"")
		       .append(value).append('"');
		_attributes.add(attr);
	    }
	}
    }

    public void comment(String comment) throws TransletException { 
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}
	else if (_cdataTagOpen) {
	    closeCDATA();
	}

	_buffer.append("<!--").append(comment).append("-->");
    }

    public void processingInstruction(String target, String data)
	throws TransletException 
    { 
// System.out.println("PI target = " + target + " data = " + data);
	if (_startTagOpen) {
	    _buffer.append('>');
	    _startTagOpen = false;
	}
	else if (_cdataTagOpen) {
	    closeCDATA();
	}

	_buffer.append("<?").append(target).append(' ')
	       .append(data).append("?>");
    }

    public boolean setEscaping(boolean escape) throws TransletException 
    { 
	final boolean temp = _escaping;
	_escaping = escape;
	return temp; 
    }

    public void setCdataElements(Hashtable elements) { 
	_cdata = elements;
    }

    public void namespace(final String prefix, final String uri)
	throws TransletException 
    {
// System.out.println("namespace prefix = " + prefix + " uri = " + uri);

	if (_startTagOpen) {
	    if (pushNamespace(prefix, uri)) {
		_buffer.append(' ').append(XMLNS_PREFIX);
		if (prefix != null && prefix != EMPTYSTRING) {
		    _buffer.append(':').append(prefix);
		}
		_buffer.append("=\"").append(uri).append('"');
	    }
	}
	else if (prefix != EMPTYSTRING || uri != EMPTYSTRING) {
	    BasisLibrary.runTimeError(BasisLibrary.STRAY_NAMESPACE_ERR,
				      prefix, uri);
	}
    }

    /**
     * Declare a prefix to point to a namespace URI
     */
    private boolean pushNamespace(String prefix, String uri) {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.startsWith(XML_PREFIX)) {
	    return false;
	}
	
	Stack stack;
	// Get the stack that contains URIs for the specified prefix
	if ((stack = (Stack)_namespaces.get(prefix)) == null) {
	    _namespaces.put(prefix, stack = new Stack());
	}

	// Quit now if the URI the prefix currently maps to is the same as this
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
    private void popNamespace(String prefix) {
	// Prefixes "xml" and "xmlns" cannot be redefined
	if (prefix.startsWith(XML_PREFIX)) {
	    return;
	}

	Stack stack;
	if ((stack = (Stack)_namespaces.get(prefix)) != null) {
	    stack.pop();
	}
    }

    /**
     * Pop all namespace definitions that were delcared by the current element
     */
    private void popNamespaces() {
	while (true) {
	    if (_nodeStack.isEmpty()) return;
	    Integer i = (Integer)(_nodeStack.peek());
	    if (i.intValue() != _depth) return;
	    _nodeStack.pop();
	    popNamespace((String)_prefixStack.pop());
	}
    }

    /**
     * Utility method - pass a whole charactes as CDATA to SAX handler
     */
    private void startCDATA(char[] ch, int off, int len) {
	final int limit = off + len;
	int offset = off;

	// Output start bracket - "<![CDATA["
	_buffer.append(BEGCDATA);

	// Detect any occurence of "]]>" in the character array
	for (int i = offset; i < limit - 2; i++) {
	    if (ch[i] == ']' && ch[i+1] == ']' && ch[i+2] == '>') {
		_buffer.append(ch, offset, i - offset);
		_buffer.append(CNTCDATA);
		offset = i + 3;
		i = i + 2; 	// Skip next chars ']' and '>'.
	    }
	}

	// Output the remaining characters
	if (offset < limit) {
	    _buffer.append(ch, offset, limit - offset);
	}
	_cdataTagOpen = true;
    }

    private void closeCDATA() {
	_buffer.append(ENDCDATA);
	_cdataTagOpen = false;
    }

    /**
     * Utility method - escape special characters and pass to SAX handler
     */
    private void escapeCDATA(char[] ch, int off, int len) {
	int limit = off + len;
	int offset = off;

	if (limit > ch.length) {
	    limit = ch.length;
	}

	// Step through characters and escape all special characters
	for (int i = off; i < limit; i++) {
	    final char current = ch[i];

	    if ((current >= '\u007F' && current < '\u00A0') ||
		(_is8859Encoded && current > '\u00FF'))
	    {
		_buffer.append(ch, offset, i - offset)
		       .append(CDATA_ESC_START)
		       .append(Integer.toString((int) ch[i]))
		       .append(CDATA_ESC_END);
		offset = i + 1;
	    }
	}
	// Output remaining characters 
	if (offset < limit) {
	    _buffer.append(ch, offset, limit - offset);
	}
    }
}
