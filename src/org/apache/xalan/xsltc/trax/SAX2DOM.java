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

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Attr;
import java.util.Stack;


public class SAX2DOM implements ContentHandler {

    private Document _document = null;
    private DocumentBuilder _builder = null;
    private Stack _nodeStk = null;
 
    public SAX2DOM() throws ParserConfigurationException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	_builder = factory.newDocumentBuilder();
	_nodeStk = new Stack();
    }

    public Node getDOM() {
	return _document;
    }

    public void characters(char[] ch, int start, int length) {
	Text text = _document.createTextNode(new String(ch));
	Node last = (Node)_nodeStk.peek();
	last.appendChild(text);
    }

    public void startDocument() {
	_document = _builder.newDocument();
	Element root = (Element)_document.createElement("root");
	_document.appendChild(root);
	_nodeStk.push(root);
    }

    public void endDocument() {
	//printDOM();
    }

    public void startElement(String namespace, String localName, String qName,
	Attributes attrs ) 
    {
	// create new element
	Element tmp = (Element)_document.createElementNS(namespace, qName);
	int nattrs = attrs.getLength();
	for (int i=0; i<nattrs; i++ ) {
	    String namespaceuri = attrs.getURI(i);
	    String value = attrs.getValue(i);
	    String qname = attrs.getQName(i);
	    tmp.setAttributeNS(namespaceuri, qname, value);
	}
	// append this new node onto current stack node
	Node last = (Node)_nodeStk.peek();
	last.appendChild(tmp);
	// push this node onto stack
	_nodeStk.push(tmp);
    }

    public void endElement(String namespace, String localName, String qName) {
	Node lastActive = (Node)_nodeStk.pop();  
    }


    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    public void processingInstruction(String target, String data) {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) {
    }

    public void startPrefixMapping(String prefix, String uri) {
    }

    public void endPrefixMapping(String prefix) {
    }


    // for debugging - will be removed
    private void printDOM() {
        System.out.println("SAX2DOM.java:Printing DOM...");
        Node currNode = _document;
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
                    break;
                case Node.DOCUMENT_TYPE_NODE :
                    break;
                case Node.ELEMENT_NODE :
                    System.out.println("ELEMT NODE " + currNode.getLocalName() +":");
		     org.w3c.dom.NamedNodeMap map = currNode.getAttributes();
                    int length = map.getLength();
                    for (int i=0; i<length; i++ ){
                        Node attrNode = map.item(i);
                        short code = attrNode.getNodeType();
                        System.out.println("\tattr:"+attrNode.getNamespaceURI()+
                            "," + attrNode.getLocalName() +
                            "," + attrNode.getNodeName() +
                            "=" + attrNode.getNodeValue());
                    }
                    break;
                case Node.ENTITY_NODE :
                    org.w3c.dom.Entity edecl = (org.w3c.dom.Entity)currNode;
                    String name = edecl.getNotationName();
                    if ( name != null ) {
                        System.out.println("ENT NODE: "+currNode.getNodeName()+
                           ", "+ edecl.getSystemId()+ "," + name);
                    }
                    break;
                case Node.ENTITY_REFERENCE_NODE :
                    break;
                case Node.NOTATION_NODE :
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE :
                    break;
                case Node.TEXT_NODE :
                    String data = currNode.getNodeValue();
                    System.out.println("TEXT NODE:" + data);
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

}
