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

public class DOM2SAX implements XMLReader , Locator {
    private Document _dom = null;
    private ContentHandler _contentHdlr = null;
 
    public DOM2SAX(Node root) {
	_dom = (Document)root;
    }

    public ContentHandler getContentHandler() { 
	return _contentHdlr;
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
    public void parse(InputSource input) throws IOException, SAXException {
	Node currNode = _dom; 
	while (currNode != null) {
	    // start of node processing
	    switch (currNode.getNodeType()) {
		case Node.ATTRIBUTE_NODE : 
		    break;
		case Node.CDATA_SECTION_NODE : 
		    break;
		case Node.COMMENT_NODE : 
		    break;
		case Node.DOCUMENT_FRAGMENT_NODE : 
		    break;
		case Node.DOCUMENT_NODE : 
		    _contentHdlr.setDocumentLocator(this);
		    _contentHdlr.startDocument(); 	    
		    break;
		case Node.DOCUMENT_TYPE_NODE : 
		    break;
		case Node.ELEMENT_NODE : 
		    AttributesImpl attrList = new AttributesImpl();
		    NamedNodeMap map = currNode.getAttributes();
		    int length = map.getLength();
		    for (int i=0; i<length; i++ ){
			Node attrNode = map.item(i);
			short code = attrNode.getNodeType();
			attrList.addAttribute(attrNode.getNamespaceURI(),
			    attrNode.getLocalName(),
			    attrNode.getNodeName(),
			    getNodeTypeFromCode(code),  // must be better way
			    attrNode.getNodeValue());
		    }
		    _contentHdlr.startElement(currNode.getNamespaceURI(),
		        currNode.getLocalName(), currNode.getNodeName(),
			attrList); 
		    break;
		case Node.ENTITY_NODE : 
		   /***
		    Entity edecl = (Entity)currNode;
		    String name = edecl.getNotationName();
		    if ( name != null ) {
			_contentHdlr.unparsedEntityDecl(currNode.getNodeName(),
			    edecl.getPublicId(), edecl.getSystemId(), name);
		    } 
		    **/
		    break;
		case Node.ENTITY_REFERENCE_NODE : 
		    break;
		case Node.NOTATION_NODE :
		    /***
		    Notation ndecl = (Notation)currNode;
		    _contentHdlr.notationDecl(currNode.getNodeName(),
			ndecl.getPublicId(), ndecl.getSystemId());
		    **/
		    break;
		case Node.PROCESSING_INSTRUCTION_NODE : 
		    _contentHdlr.processingInstruction(currNode.getNodeName(),
			currNode.getNodeValue());
		    break;
		case Node.TEXT_NODE : 
		    String data = currNode.getNodeValue();
		    length = data.length();
		    char[] array = new char[length];
		    data.getChars(0, length, array, 0);
		    _contentHdlr.characters(array, 0, length); 
		    break;
	    }

	    // move to first child
	    Node next = currNode.getFirstChild();
	    if (next != null) {
		currNode = next;
		continue;
	    }

	    // no child nodes, walk the tree
	    while (currNode != null) {
		switch (currNode.getNodeType()) {
		    case Node.DOCUMENT_NODE: 
			break;
		    case Node.ELEMENT_NODE: 
			break;
		}
		next = currNode.getNextSibling();
		if (next != null ) {
		    currNode = next;
		    break;
		}
		// move up a level
		currNode = currNode.getParentNode();
	    }
	}
    }
    public void parse(String sysId) throws IOException, SAXException {
    }
    public void setContentHandler(ContentHandler handler) throws 
	NullPointerException 
    {
	if (handler == null ) throw new NullPointerException();
	_contentHdlr = handler;
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
