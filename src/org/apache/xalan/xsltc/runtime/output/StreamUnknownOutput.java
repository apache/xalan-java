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

public class StreamUnknownOutput extends StreamOutput {

    private StreamOutput _handler;
    private boolean      _startDocumentCalled = false;

    public StreamUnknownOutput(Writer writer, String encoding) {
	super(writer, encoding);
	_handler = new StreamXMLOutput(writer, encoding);
    }

    public StreamUnknownOutput(OutputStream out, String encoding) 
	throws IOException
    {
	super(out, encoding);
	_handler = new StreamXMLOutput(out, encoding);
    }

    public void startDocument() throws TransletException { 
	_startDocumentCalled = true;
    }

    public void endDocument() throws TransletException { 
	_handler.endDocument();
    }

    public void startElement(String elementName) throws TransletException { 
	if (_firstElement) {
	    // If first element is HTML, create a new handler
	    if (elementName.equalsIgnoreCase("html")) {
		_handler = new StreamHTMLOutput(_handler);
	    }
	    if (_startDocumentCalled) {
		_handler.startDocument();
	    }
	    _firstElement = false;
	}
	_handler.startElement(elementName);
    }

    public void endElement(String elementName) 
	throws TransletException 
    { 
	_handler.endElement(elementName);
    }

    public void characters(String characters) 
	throws TransletException 
    { 
	_handler.characters(characters);
    }

    public void characters(char[] characters, int offset, int length)
	throws TransletException 
    { 
	_handler.characters(characters, offset, length);
    }

    public void attribute(String name, String value)
	throws TransletException 
    { 
	_handler.attribute(name, value);
    }

    public void comment(String comment) 
	throws TransletException 
    { 
	_handler.comment(comment);
    }

    public void processingInstruction(String target, String data)
	throws TransletException 
    { 
	_handler.processingInstruction(target, data);
    }

    public boolean setEscaping(boolean escape) 
	throws TransletException 
    { 
	return _handler.setEscaping(escape);
    }

    public void setCdataElements(Hashtable elements) { 
	_handler.setCdataElements(elements);
    }

    public void namespace(String prefix, String uri)
	throws TransletException 
    {
	_handler.namespace(prefix, uri);
    }

    public void setDoctype(String system, String pub) {
	_handler.setDoctype(system, pub);
    }

    public void setIndent(boolean indent) { 
	_handler.setIndent(indent);
    }

    public void omitHeader(boolean value) {
	_handler.omitHeader(value);
    }

    public void setStandalone(String standalone) {
	_handler.setStandalone(standalone);
    }

}
