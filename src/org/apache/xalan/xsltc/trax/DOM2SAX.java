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
 * @author G. Todd Miller 
 *
 */


package org.apache.xalan.xsltc.trax;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import java.io.IOException;
import org.w3c.dom.Entity;
import org.w3c.dom.Notation;

import org.apache.xalan.xsltc.runtime.AttributeList;

class DOM2SAX implements XMLReader , Locator {

    private Document _dom = null;
    private ContentHandler _sax = null;
 
    public DOM2SAX(Node root) {
	_dom = (Document)root;
    }

    public ContentHandler getContentHandler() { 
	return _sax;
    }

    public DTDHandler getDTDHandler() { 
	return null;
    }

    public ErrorHandler getErrorHandler() {
	return null;
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
	SAXNotSupportedException
    {
	return false;
    }

    public void setFeature(String name, boolean value) throws 
	SAXNotRecognizedException, SAXNotSupportedException 
    {
	
    }

    public void parse(InputSource unused) throws IOException, SAXException {
        Node currNode = _dom;
        parse(currNode);
    }

    private void parse(Node node) throws IOException, SAXException {
        Node first = null;
 	if (node == null ) return;

        switch (node.getNodeType()) {
	case Node.ATTRIBUTE_NODE:         // handled by ELEMENT_NODE
	case Node.COMMENT_NODE:           // should be handled!!!
	case Node.CDATA_SECTION_NODE:
	case Node.DOCUMENT_FRAGMENT_NODE:
	case Node.DOCUMENT_TYPE_NODE :
	case Node.ENTITY_NODE :
	case Node.ENTITY_REFERENCE_NODE:
	case Node.NOTATION_NODE :
	    // These node types are ignored!!!
	    break;

	case Node.DOCUMENT_NODE:
	    _sax.setDocumentLocator(this);
	    _sax.startDocument();
	    Node next = node.getFirstChild();
	    while (next != null) {
		parse(next);
		next = next.getNextSibling();
	    }
	    _sax.endDocument();
	    break;

	case Node.ELEMENT_NODE:
	    // Gather all attribute node of the element
	    AttributeList attrs = new AttributeList();
	    NamedNodeMap map = node.getAttributes();
	    int length = map.getLength();
	    for (int i=0; i<length; i++ ) {
		Node attr = map.item(i);
		attrs.add(attr.getNodeName(), attr.getNodeValue());
	    }

	    // Generate SAX event to start element
	    _sax.startElement(node.getNamespaceURI(), node.getLocalName(),
			      node.getNodeName(), attrs);

	    // Traverse all child nodes of the element (if any)
	    next = node.getFirstChild();
	    while ( next != null ) {
		parse(next);
		next = next.getNextSibling();
	    }

	    // Generate SAX event to close element
	    _sax.endElement(node.getNamespaceURI(),
				    node.getLocalName(), node.getNodeName());
	    break;

	case Node.PROCESSING_INSTRUCTION_NODE:
	    _sax.processingInstruction(node.getNodeName(),
				       node.getNodeValue());
	    break;

	case Node.TEXT_NODE:
	    final String data = node.getNodeValue();
	    _sax.characters(data.toCharArray(), 0, data.length());
	    break;
	}
    }

    public void parse(String sysId) throws IOException, SAXException {
	throw new IOException("This method is not yet implemented.");
    }
    public void setContentHandler(ContentHandler handler) throws 
	NullPointerException 
    {
	if (handler == null ) throw new NullPointerException();
	_sax = handler;
    }
    public void setDTDHandler(DTDHandler handler) throws NullPointerException {
	if (handler == null )  throw new NullPointerException();
    }
    public void setEntityResolver(EntityResolver resolver) throws 
	NullPointerException 
    {
	if (resolver == null )  throw new NullPointerException();
    }
    public EntityResolver getEntityResolver() {
	return null;
    }
    public void setErrorHandler(ErrorHandler handler) throws 
	NullPointerException
    {
	if (handler == null )  throw new NullPointerException();
    }
    public void setProperty(String name, Object value) throws
	SAXNotRecognizedException, SAXNotSupportedException {
    }
    public Object getProperty(String name) throws SAXNotRecognizedException,
	SAXNotSupportedException
    {
	return null;
    }

    // Locator methods
    public int getColumnNumber() { return 0; }
    public int getLineNumber() { return 0; }
    public String getPublicId() { return null; }
    public String getSystemId() { return null; }


    // private 
    private String getNodeTypeFromCode(short code) {
	String retval = null;
	switch (code) {
	case Node.ATTRIBUTE_NODE : 
	    retval = "ATTRIBUTE_NODE"; break; 
	case Node.CDATA_SECTION_NODE :
	    retval = "CDATA_SECTION_NODE"; break; 
	case Node.COMMENT_NODE :
	    retval = "COMMENT_NODE"; break; 
	case Node.DOCUMENT_FRAGMENT_NODE :
	    retval = "DOCUMENT_FRAGMENT_NODE"; break; 
	case Node.DOCUMENT_NODE :
	    retval = "DOCUMENT_NODE"; break; 
	case Node.DOCUMENT_TYPE_NODE :
	    retval = "DOCUMENT_TYPE_NODE"; break; 
	case Node.ELEMENT_NODE :
	    retval = "ELEMENT_NODE"; break; 
	case Node.ENTITY_NODE :
	    retval = "ENTITY_NODE"; break; 
	case Node.ENTITY_REFERENCE_NODE :
	    retval = "ENTITY_REFERENCE_NODE"; break; 
	case Node.NOTATION_NODE :
	    retval = "NOTATION_NODE"; break; 
	case Node.PROCESSING_INSTRUCTION_NODE :
	    retval = "PROCESSING_INSTRUCTION_NODE"; break; 
	case Node.TEXT_NODE:
	    retval = "TEXT_NODE"; break; 
        }
	return retval;
    }
}
