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
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.runtime;

import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xsltc.*;

public final class SAXAdapter implements TransletOutputHandler {

    private final ContentHandler _saxHandler;
    private final LexicalHandler _lexHandler;
    private final AttributeList  _attributes = new AttributeList();

    private String _openElementName;
    
    public SAXAdapter(ContentHandler saxHandler) {
	_saxHandler = saxHandler;
	_lexHandler = null;
    }

    public SAXAdapter(ContentHandler saxHandler, LexicalHandler lexHandler) {
	_saxHandler = saxHandler;
	_lexHandler = lexHandler;
    }

    private void maybeEmitStartElement() throws SAXException {
	if (_openElementName != null) {
	    _saxHandler.startElement(null, null, _openElementName, _attributes);
	    _openElementName = null;
	}
    }

    public void startDocument() throws TransletException {
	try {
	    _saxHandler.startDocument();
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    public void endDocument() throws TransletException {
	try {
	    _saxHandler.endDocument();
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    public void characters(char[] characters, int offset, int length)
	throws TransletException {
	try {
	    maybeEmitStartElement();
	    _saxHandler.characters(characters, offset, length);
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    public void startElement(String elementName) throws TransletException {
	try {
	    maybeEmitStartElement();
	    _openElementName = elementName;
	    _attributes.clear();
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    public void endElement(String elementName) throws TransletException {
	try {
	    maybeEmitStartElement();
	    _saxHandler.endElement(null, null, elementName);
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    public void attribute(String name, String value)
	throws TransletException {
	if (_openElementName != null) {
	    _attributes.add(name, value);
	}
	else {
	    BasisLibrary.runTimeError(BasisLibrary.STRAY_ATTRIBUTE_ERR, name);
	}
    }
    
    public void namespace(String prefix, String uri)
	throws TransletException {
	// ???
    }

    public void comment(String comment) throws TransletException {
	try {
	    maybeEmitStartElement();
	    if (_lexHandler != null) {
		char[] chars = comment.toCharArray();
		_lexHandler.comment(chars, 0, chars.length);
	    }
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }
    
    public void processingInstruction(String target, String data)
	throws TransletException {
	try {
	    maybeEmitStartElement();
	    _saxHandler.processingInstruction(target, data);
	}
	catch (SAXException e) {
	    throw new TransletException(e);
	}
    }

    // The SAX handler does not handle these:
    public void setType(int type) {}
    public void setHeader(String header) {}
    public void setIndent(boolean indent) {}
    public void omitHeader(boolean value) {}
    public void setCdataElements(Hashtable elements) { }
    public void close() {}
    public boolean setEscaping(boolean escape) throws TransletException {
        return(true);
    }
    public String getPrefix(String uri) { return(""); }
}
