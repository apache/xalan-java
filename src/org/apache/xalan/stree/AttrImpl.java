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
package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;

import org.w3c.dom.DOMException;

import org.xml.sax.ContentHandler;

/**
 * <meta name="usage" content="internal"/>
 * Class to hold information about an attribute node.
 */
public class AttrImpl extends Child implements Attr
{

  /** Attribute name         */
  private String m_name;

  /** Attribute value          */
  private String m_value;

  /** 
   * Flag to determine if this attribute was explicitly given a 
   * value in the original document          
   */
  private boolean m_specified = true;

  /**
   * Constructor AttrImpl
   *
   *
   * @param doc Document object
   * @param name Attribute name
   * @param value Attribute value
   */
  AttrImpl(DocumentImpl doc, String name, String value)
  {

    super(doc);

    m_name = name;
    m_value = value;
  }

  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   *
   * @return node type of attribute
   */
  public short getNodeType()
  {
    return Node.ATTRIBUTE_NODE;
  }

  /**
   * Returns the node name. 
   *
   * @return node name
   */
  public String getNodeName()
  {
    return m_name;
  }

  /**
   * The namespace URI of this node, or <code>null</code> if it is
   * unspecified.
   * <br>This is not a computed value that is the result of a namespace
   * lookup based on an examination of the namespace declarations in scope.
   * It is merely the namespace URI given at creation time.
   * <br>For nodes of any type other than <code>ELEMENT_NODE</code> and
   * <code>ATTRIBUTE_NODE</code> and nodes created with a DOM Level 1
   * method, such as <code>createElement</code> from the
   * <code>Document</code> interface, this is always <code>null</code>.Per
   * the Namespaces in XML Specification  an attribute does not inherit its
   * namespace from the element it is attached to. If an attribute is not
   * explicitly given a namespace, it simply has no namespace.
   *
   * @return namespace URI
   */
  public String getNamespaceURI()
  {
    return null;
  }

  /**
   * The namespace prefix of this node, or <code>null</code> if it is
   * unspecified.
   * @since DOM Level 2
   *
   * @return namespace prefix of this node
   */
  public String getPrefix()
  {
    return null;
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return local part of the qualified name of this node
   */
  public String getLocalName()
  {
    return m_name;
  }

  /**
   * Returns the value of this attribute node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return the value of this attribute node
   */
  public String getValue()
  {
    return m_value;
  }

  /**
   * Same as getValue(). 
   *
   * @return the value of this attribute node
   *
   * @throws DOMException
   */
  public String getNodeValue() throws DOMException
  {
    return m_value;
  }

  /**
   * Sets the value of this attribute node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @param value Attribute value to be set
   *
   * @throws DOMException
   */
  public void setValue(String value) throws DOMException
  {
    m_value = value;
  }

  /**
   *    If this attribute was explicitly given a value in the original
   *   document, this is <code>true</code> ; otherwise, it is
   *   <code>false</code> . Note that the implementation is in charge of this
   *   attribute, not the user. If the user changes the value of the
   *   attribute (even if it ends up having the same value as the default
   *   value) then the <code>specified</code> flag is automatically flipped
   *   to <code>true</code> .  To re-specify the attribute as the default
   *   value from the DTD, the user must delete the attribute. The
   *   implementation will then make a new attribute available with
   *   <code>specified</code> set to <code>false</code> and the default value
   *   (if one exists).
   *   <br> In summary: If the attribute has an assigned value in the document
   *   then  <code>specified</code> is <code>true</code> , and the value is
   *   the  assigned value. If the attribute has no assigned value in the
   *   document and has  a default value in the DTD, then
   *   <code>specified</code> is <code>false</code> ,  and the value is the
   *   default value in the DTD. If the attribute has no assigned value in
   *   the document and has  a value of #IMPLIED in the DTD, then the
   *   attribute does not appear  in the structure model of the document.
   *
   * @return whether this attribute was explicitly given a value in the original
   *         document
   */
  public boolean getSpecified()
  {
    return m_specified;
  }

  /**
   *   The <code>Element</code> node this attribute is attached to or
   *  <code>null</code> if this attribute is not in use.
   *  @since DOM Level 2
   *
   * @return node this attribute is attached to
   */
  public Element getOwnerElement()
  {
    return (Element) getParentNode();
  }

  /**
   * Get this attribute's name
   *
   *
   * @return attribute name
   */
  public String getName()
  {
    return m_name;
  }

  /**
   * Set this attribute's name
   *
   *
   * @param name to set attribute name to 
   */
  void setName(String name)
  {
    m_name = name;
  }

  /**
   * The node immediately preceding this node. If there is no such node,
   * this returns <code>null</code>.
   *
   * @return node immediately preceding this node
   */
  public Node getPreviousSibling()
  {
    return null;
  }

  /**
   * The node immediately following this node. If there is no such node,
   * this returns <code>null</code>.
   *
   * @return node immediately following this node
   */
  public Node getNextSibling()
  {
    // return m_parent.m_first;
    return null;
  }
  
  /**
   * Handle a Characters event 
   *
   *
   * @param ch Content handler to handle SAX events
   *
   * @throws SAXException if the content handler characters event throws a SAXException.
   */
  public void dispatchCharactersEvent(ContentHandler ch) 
    throws org.xml.sax.SAXException
  {
    ch.characters(m_value.toCharArray(), 0, m_value.length());
  }
}
