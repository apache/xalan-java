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
 */


package org.apache.xalan.xsltc.trax;

import java.util.ArrayList;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;

import org.apache.xalan.xsltc.runtime.Constants;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.TransletOutputHandler;

public class SAX2TO implements ContentHandler, LexicalHandler, Constants {

    static private class Pair {
	String left;
	String right;

	public Pair(String ll, String rr) {
	    left = ll; right = rr;
	}
    }

    TransletOutputHandler _handler;
    ArrayList _nsDeclarations = new ArrayList();

    public SAX2TO(TransletOutputHandler handler) {
	_handler = handler;
    }

    public void startDocument() throws TransletException {
	_handler.startDocument();
    }

    public void endDocument() throws TransletException {
	_handler.endDocument();
	_handler.close();
    }

    public void startElement(String namespace, String localName, String qName,
	Attributes attrs) throws TransletException
    {
	_handler.startElement(qName);

	int n = _nsDeclarations.size();
	for (int i = 0; i < n; i++) {
	    final Pair pair = (Pair) _nsDeclarations.get(i);
	    _handler.namespace(pair.left, pair.right);
	}
	_nsDeclarations.clear();

	n = attrs.getLength();
	for (int i = 0; i < n; i++) {
	    _handler.attribute(attrs.getQName(i), attrs.getValue(i));
	}
    }

    public void endElement(String namespace, String localName, String qName) 
	throws TransletException
    {
	_handler.endElement(qName);
    }

    public void startPrefixMapping(String prefix, String uri)
	throws TransletException
    {
	_nsDeclarations.add(new Pair(prefix, uri));
    }

    public void endPrefixMapping(String prefix) {
	// Empty
    }

    public void characters(char[] ch, int start, int length)
	throws TransletException
    {
	_handler.characters(ch, start, length);
    }

    public void processingInstruction(String target, String data)
	throws TransletException
    {
	_handler.processingInstruction(target, data);
    }

    public void comment(char[] ch, int start, int length) 
	throws TransletException
    {
	_handler.comment(new String(ch, start, length));
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
	throws TransletException
    {
	_handler.characters(ch, start, length);
    }

    public void startCDATA() throws TransletException { 
	_handler.startCDATA();
    }

    public void endCDATA() throws TransletException { 
	_handler.endCDATA();
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) {
    }

    public void startEntity(java.lang.String name) { 
    }

    public void endDTD() { 
    }

    public void endEntity(String name) { 
    }

    public void startDTD(String name, String publicId, String systemId)
        throws SAXException 
    { 
    }
}
