/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.DOMHelper;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Attr;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;

/**
 * <meta name="usage" content="general"/>
 * Provides XSLTProcessor an interface to the Xerces XML parser.  This 
 * liaison should be used if Xerces DOM nodes are being process as 
 * the source tree or as the result tree.
 * @see org.apache.xalan.xslt.XSLTProcessor
 * @see org.apache.xml.parsers
 */
public class DOM2Helper extends DOMHelper
{
  /**
   * Construct an instance.
   */
  public DOM2Helper()
  {
  }

  /**
   * <meta name="usage" content="internal"/>
   * Check node to see if it matches this liaison.
   */
  public void checkNode(Node node)
    throws SAXException
  {
    if(!(node instanceof org.apache.xerces.dom.NodeImpl))
      throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XERCES_CANNOT_HANDLE_NODES, new Object[]{((Object)node).getClass()})); //"DOM2Helper can not handle nodes of type"
        //+((Object)node).getClass());
  }

  /**
   * Returns true that this implementation does support
   * the SAX ContentHandler interface.
   */
  public boolean supportsSAX()
  {
    return true;
  }
  
  private Document m_doc;
  
  public void setDocument(Document doc) {m_doc = doc;}
  public Document getDocument() {return m_doc;}

  /**
   * <meta name="usage" content="internal"/>
   * Parse an XML document.
   * 
   * <p>Right now the Xerces DOMParser class is used.  This needs 
   * fixing, either via jaxp, or via some other, standard method.</p>
   *
   * <p>The application can use this method to instruct the SAX parser
   * to begin parsing an XML document from any valid input
   * source (a character stream, a byte stream, or a URI).</p>
   *
   * <p>Applications may not invoke this method while a parse is in
   * progress (they should create a new Parser instead for each
   * additional XML document).  Once a parse is complete, an
   * application may reuse the same Parser object, possibly with a
   * different input source.</p>
   *
   * @param source The input source for the top-level of the
   *        XML document.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.InputSource
   * @see #parse(java.lang.String)
   * @see #setEntityResolver
   * @see #setDTDHandler
   * @see #setContentHandler
   * @see #setErrorHandler
   */
  public void parse (InputSource source)
    throws SAXException
  {
    // I guess I should use JAXP factory here... when it's legal.
    org.apache.xerces.parsers.DOMParser parser 
      = new org.apache.xerces.parsers.DOMParser();
    
    // domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", getShouldExpandEntityRefs()? false : true);
    if(m_useDOM2getNamespaceURI)
    {
      parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
      parser.setFeature("http://xml.org/sax/features/namespaces", true);
    }
    else
    {
      parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
    }
    
    parser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);

    String ident = (null == source.getSystemId())
                   ? "Input XSL" : source.getSystemId();
    parser.setErrorHandler(new org.apache.xalan.utils.DefaultErrorHandler(ident));

    // if(null != m_entityResolver)
    // {
    // System.out.println("Setting the entity resolver.");
    //  parser.setEntityResolver(m_entityResolver);
    // }

    try
    {
      parser.parse(source);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
    setDocument(((org.apache.xerces.parsers.DOMParser)parser).getDocument());
  }

  /**
   * Given an ID, return the element.
   */
  public Element getElementByID(String id, Document doc)
  {
    return doc.getElementById(id);
  }
  
  /**
   * Figure out if node2 should be placed after node1 when 
   * placing nodes in a list that is to be sorted in 
   * document order.
   * NOTE: Make sure this does the right thing with attribute nodes!!!
   * @return true if node2 should be placed 
   * after node1, and false if node2 should be placed 
   * before node1.
   */
  public boolean isNodeAfter(Node node1, Node node2)
  {
    // Assume first that the nodes are DTM nodes, since discovering node 
    // order is massivly faster for the DTM.
    try
    {
      int index1 = ((DOMOrder)node1).getUid();
      int index2 = ((DOMOrder)node2).getUid();
      return index1 <= index2;
    }
    catch(ClassCastException cce)
    {
      // isNodeAfter will return true if node is after countedNode 
      // in document order. isDOMNodeAfter is sloooow (relativly).
      return super.isNodeAfter(node1, node2);
    }
  }   

  /**
   * Get the parent of a node.
   */
  public Node getParentOfNode(Node node)
    throws RuntimeException
  {
    return (Node.ATTRIBUTE_NODE == node.getNodeType())
           ? ((Attr)node).getOwnerElement() : node.getParentNode();
  }
  
  /**
   * Returns the local name of the given node.
   */
  public String getLocalNameOfNode(Node n)
  {
    return n.getLocalName();
  }

  /**
   * Returns the namespace of the given node.
   */
  public String getNamespaceOfNode(Node n)
  {
    return n.getNamespaceURI();
  }

  private boolean m_useDOM2getNamespaceURI = false;

}


