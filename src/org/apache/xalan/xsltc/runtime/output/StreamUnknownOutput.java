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

import java.util.ArrayList;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.xalan.xsltc.*;
import org.apache.xalan.xsltc.runtime.*;
import org.apache.xalan.xsltc.runtime.Hashtable;

public class StreamUnknownOutput extends StreamOutput {

    private StreamOutput _handler;

    private boolean      _isHtmlOutput = false;
    private boolean      _firstTagOpen = false;
    private boolean      _firstElement = true;
    private String       _firstTagPrefix, _firstTag;

    private ArrayList    _attributes = null;
    private ArrayList    _namespaces = null;

    // Cache calls to output properties events
    private String       _mediaType          = null;
    private boolean      _callStartDocument  = false;
    private boolean      _callSetVersion     = false;
    private boolean      _callSetDoctype     = false;

    static class Pair {
	public String name, value;

	public Pair(String name, String value) {
	    this.name = name;
	    this.value = value;
	}
    }

    public StreamUnknownOutput(Writer writer, String encoding) {
	super(writer, encoding);
	_handler = new StreamXMLOutput(writer, encoding);
// System.out.println("StreamUnknownOutput.<init>");
    }

    public StreamUnknownOutput(OutputStream out, String encoding) 
	throws IOException
    {
	super(out, encoding);
	_handler = new StreamXMLOutput(out, encoding);
// System.out.println("StreamUnknownOutput.<init>");
    }

    public void startDocument() 
	throws TransletException 
    { 
	_callStartDocument = true;
    }

    public void endDocument() 
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    initStreamOutput();
	}
	else if (_callStartDocument) {
	    _handler.startDocument();
	}
	_handler.endDocument();
    }

    public void startElement(String elementName) 
	throws TransletException 
    { 
// System.out.println("startElement() = " + elementName);
	if (_firstElement) {
	    _firstElement = false;

	    _firstTag = elementName;
	    _firstTagPrefix = BasisLibrary.getPrefix(elementName);
	    if (_firstTagPrefix == null) {
		_firstTagPrefix = EMPTYSTRING;
	    }

	    _firstTagOpen = true;
	    _isHtmlOutput = BasisLibrary.getLocalName(elementName)
				        .equalsIgnoreCase("html");
	}
	else {
	    if (_firstTagOpen) {
		initStreamOutput();
	    }
	    _handler.startElement(elementName);
	}
    }

    public void endElement(String elementName) 
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    initStreamOutput();
	}
	_handler.endElement(elementName);
    }

    public void characters(String characters) 
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    initStreamOutput();
	}
	_handler.characters(characters);
    }

    public void characters(char[] characters, int offset, int length)
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    initStreamOutput();
	}
	_handler.characters(characters, offset, length);
    }

    public void attribute(String name, String value)
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    if (_attributes == null) {
		_attributes = new ArrayList();
	    }
	    _attributes.add(new Pair(name, value));
	}
	else {
	    _handler.attribute(name, value);
	}
    }

    public void namespace(String prefix, String uri)
	throws TransletException 
    {
// System.out.println("namespace() = " + prefix + " " + uri);
	if (_firstTagOpen) {
	    if (_namespaces == null) {
		_namespaces = new ArrayList();
	    }
	    _namespaces.add(new Pair(prefix, uri));

	    // Check if output is XHTML instead of HTML
	    if (_firstTagPrefix.equals(prefix) && !uri.equals(EMPTYSTRING)) {
		_isHtmlOutput = false;
	    }
	}
	else {
	    _handler.namespace(prefix, uri);
	}
    }

    public void comment(String comment) 
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    initStreamOutput();
	}
	_handler.comment(comment);
    }

    public void processingInstruction(String target, String data)
	throws TransletException 
    { 
	if (_firstTagOpen) {
	    initStreamOutput();
	}
	_handler.processingInstruction(target, data);
    }

    public void setDoctype(String system, String pub) {
	_handler.setDoctype(system, pub);

	// Cache call to setDoctype()
	super.setDoctype(system, pub);
	_callSetDoctype = true;
    }

    /**
     * This method cannot be cached because default is different in 
     * HTML and XML (we need more than a boolean).
     */
    public void setIndent(boolean indent) { 
	_handler.setIndent(indent);
    }

    public void setVersion(String version) { 
	_handler.setVersion(version);

	// Cache call to setVersion()
	super.setVersion(version);
	_callSetVersion = true;
    }

    public void omitHeader(boolean value) {
	_handler.omitHeader(value);
    }

    public void setStandalone(String standalone) {
	_handler.setStandalone(standalone);
    }

    public void setMediaType(String mediaType) { 
	_handler.setMediaType(mediaType);
	_mediaType = mediaType;
    }

    public boolean setEscaping(boolean escape) 
	throws TransletException 
    { 
	return _handler.setEscaping(escape);
    }

    public void setCdataElements(Hashtable elements) { 
	_handler.setCdataElements(elements);
    }

    public void setIndentNumber(int value) {
	_handler.setIndentNumber(value);
    }

    private void initStreamOutput() 
	throws TransletException 
    {
// System.out.println("initStreamOutput() _isHtmlOutput = " + _isHtmlOutput);
	// Create a new handler if output is HTML
	if (_isHtmlOutput) {
	    _handler = new StreamHTMLOutput(_handler);

	    if (_callSetVersion) {
		_handler.setVersion(_version);
	    }
	    if (_callSetDoctype) {
		_handler.setDoctype(_doctypeSystem, _doctypePublic);
	    }
	    if (_mediaType != null) {
		_handler.setMediaType(_mediaType);
	    }
	}

	// Call startDocument() if necessary
	if (_callStartDocument) {
	    _handler.startDocument();
	    _callStartDocument = false;
	}

	// Output first tag
	_handler.startElement(_firstTag);

	// Output namespaces of first tag
	if (_namespaces != null) {
	    final int n = _namespaces.size();
	    for (int i = 0; i < n; i++) {
		final Pair pair = (Pair) _namespaces.get(i);
		_handler.namespace(pair.name, pair.value);
	    }
	}

	// Output attributes of first tag
	if (_attributes != null) {
	    final int n = _attributes.size();
	    for (int i = 0; i < n; i++) {
		final Pair pair = (Pair) _attributes.get(i);
		_handler.attribute(pair.name, pair.value);
	    }
	}

	// Close first tag
	_firstTagOpen = false;
    }
}
