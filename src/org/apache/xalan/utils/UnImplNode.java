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
package org.apache.xalan.utils;

import org.w3c.dom.*;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * <meta name="usage" content="internal"/>
 * To be subclassed by classes that wish to fake being nodes.
 */
public class UnImplNode implements Node, Element, NodeList, Document
{

  /**
   * Constructor UnImplNode
   *
   */
  public UnImplNode(){}

  /**
   * Throw an error.
   *
   * NEEDSDOC @param msg
   */
  public void error(int msg)
  {

    System.out.println("DOM ERROR! class: " + this.getClass().getName());

    throw new RuntimeException(XSLMessages.createMessage(msg, null));
  }

  /**
   * Throw an error.
   *
   * NEEDSDOC @param msg
   * NEEDSDOC @param args
   */
  public void error(int msg, Object[] args)
  {

    System.out.println("DOM ERROR! class: " + this.getClass().getName());

    throw new RuntimeException(XSLMessages.createMessage(msg, args));  //"UnImplNode error: "+msg);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param newChild
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"appendChild not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean hasChildNodes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasChildNodes not supported!");

    return false;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNodeType not supported!");

    return 0;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getParentNode()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getParentNode not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NodeList getChildNodes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getChildNodes not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getFirstChild()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getFirstChild not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getLastChild()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getLastChild not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getNextSibling()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNextSibling not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getLength()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getLength not supported!");

    return 0;
  }  // getLength():int

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param index
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node item(int index)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"item not supported!");

    return null;
  }  // item(int):Node

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Document getOwnerDocument()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getOwnerDocument not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getTagName()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getTagName not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNodeName not supported!");

    return null;
  }

  /** Unimplemented. */
  public void normalize()
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"normalize not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NodeList getElementsByTagName(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getElementsByTagName not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param oldAttr
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Attr removeAttributeNode(Attr oldAttr) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"removeAttributeNode not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param newAttr
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Attr setAttributeNode(Attr newAttr) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttributeNode not supported!");

    return null;
  }

  /**
   * NEEDSDOC Method hasAttribute 
   *
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC (hasAttribute) @return
   */
  public boolean hasAttribute(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasAttribute not supported!");

    return false;
  }

  /**
   * NEEDSDOC Method hasAttributeNS 
   *
   *
   * NEEDSDOC @param name
   * NEEDSDOC @param x
   *
   * NEEDSDOC (hasAttributeNS) @return
   */
  public boolean hasAttributeNS(String name, String x)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasAttributeNS not supported!");

    return false;
  }

  /**
   * NEEDSDOC Method getAttributeNode 
   *
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC (getAttributeNode) @return
   */
  public Attr getAttributeNode(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributeNode not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param name
   *
   * @throws DOMException
   */
  public void removeAttribute(String name) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"removeAttribute not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param name
   * NEEDSDOC @param value
   *
   * @throws DOMException
   */
  public void setAttribute(String name, String value) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttribute not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getAttribute(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttribute not supported!");

    return null;
  }

  /**
   * Introduced in DOM Level 2.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean hasAttributes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasAttributes not supported!");

    return false;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NodeList getElementsByTagNameNS(String namespaceURI,
                                         String localName)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getElementsByTagNameNS not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param newAttr
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Attr setAttributeNodeNS(Attr newAttr) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttributeNodeNS not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Attr getAttributeNodeNS(String namespaceURI, String localName)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributeNodeNS not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *
   * @throws DOMException
   */
  public void removeAttributeNS(String namespaceURI, String localName)
          throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"removeAttributeNS not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param qualifiedName
   * NEEDSDOC @param value
   *
   * @throws DOMException
   */
  public void setAttributeNS(
          String namespaceURI, String qualifiedName, String value)
            throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttributeNS not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getAttributeNS(String namespaceURI, String localName)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributeNS not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getPreviousSibling()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getPreviousSibling not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param deep
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node cloneNode(boolean deep)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"cloneNode not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public String getNodeValue() throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNodeValue not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param nodeValue
   *
   * @throws DOMException
   */
  public void setNodeValue(String nodeValue) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setNodeValue not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param value
   *
   * @throws DOMException
   */

  // public String getValue ()
  // {      
  //  error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"getValue not supported!");
  //  return null;
  // } 

  /** Unimplemented. */
  public void setValue(String value) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setValue not supported!");
  }

  /**
   *  Returns the name of this attribute.
   *
   * NEEDSDOC ($objectName$) @return
   */

  // public String getName()
  // {
  //  return this.getNodeName();
  // }
  public Element getOwnerElement()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getOwnerElement not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getSpecified()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setValue not supported!");

    return false;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NamedNodeMap getAttributes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributes not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param newChild
   * NEEDSDOC @param refChild
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node insertBefore(Node newChild, Node refChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"insertBefore not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param newChild
   * NEEDSDOC @param oldChild
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"replaceChild not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param oldChild
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node removeChild(Node oldChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"replaceChild not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param feature
   * NEEDSDOC @param version
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean supports(String feature, String version)
  {
    return false;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNamespaceURI()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNamespaceURI not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getPrefix()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getPrefix not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param prefix
   *
   * @throws DOMException
   */
  public void setPrefix(String prefix) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setPrefix not supported!");
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getLocalName()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getLocalName not supported!");

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public DocumentType getDoctype()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public DOMImplementation getImplementation()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Element getDocumentElement()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param tagName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Element createElement(String tagName) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC ($objectName$) @return
   */
  public DocumentFragment createDocumentFragment()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Text createTextNode(String data)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Comment createComment(String data)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public CDATASection createCDATASection(String data) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param target
   * NEEDSDOC @param data
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public ProcessingInstruction createProcessingInstruction(
          String target, String data) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Attr createAttribute(String name) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public EntityReference createEntityReference(String name)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param importedNode
   * NEEDSDOC @param deep
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Node importNode(Node importedNode, boolean deep) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param qualifiedName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Element createElementNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param qualifiedName
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Attr createAttributeNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param elementId
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Element getElementById(String elementId)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * NEEDSDOC Method setData 
   *
   *
   * NEEDSDOC @param data
   *
   * @throws DOMException
   */
  public void setData(String data) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param count
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public String substringData(int offset, int count) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void appendData(String arg) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void insertData(int offset, String arg) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param count
   *
   * @throws DOMException
   */
  public void deleteData(int offset, int count) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param count
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void replaceData(int offset, int count, String arg)
          throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented. 
   *
   * NEEDSDOC @param offset
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public Text splitText(int offset) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }
}
