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

import org.apache.xml.utils.QName;

import org.w3c.dom.Node;

import org.xml.sax.Attributes;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

import org.xml.sax.ContentHandler;

/**
 * <meta name="usage" content="internal"/>
 * This class represents an element in an HTML or XML document.
 * Elements may have attributes associated with them as well as children nodes.
 */
public class ElementImpl extends Parent implements Attributes, NamedNodeMap
{

  /** Element name          */
  private String m_name;

  /** Number of attributes associated with this element          */
  private short m_attrsEnd = 0;

  /** First attribute associated with this element          */
  private AttrImpl m_firstAttr;

  /** Last attribute associated with this element          */
  private AttrImpl m_lastAttr;

  /**
   * Constructor ElementImpl
   *
   *
   * @param doc Document object
   * @param name Element name
   */
  ElementImpl(DocumentImpl doc, String name)
  {

    super(doc);

    m_name = name;
  }

  /**
   * Constructor ElementImpl
   *
   *
   * @param doc Document Object 
   * @param name Element name
   * @param atts List of attributes associated with this element
   */
  ElementImpl(DocumentImpl doc, String name, Attributes atts)
  {

    super(doc);

    m_name = name;

    setAttributes(atts);
  }

  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   *
   * @return This node's type
   */
  public short getNodeType()
  {
    return Node.ELEMENT_NODE;
  }

  /**
   * Returns the node name. 
   *
   * @return This node's name
   */
  public String getNodeName()
  {
    return m_name;
  }

  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return the local part of the qualified name of this node
   */
  public String getLocalName()
  {
    return m_name;
  }

  /**
   * Returns the tag name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return the tag name of this node
   */
  public String getTagName()
  {
    return m_name;
  }

  /**
   * Get the nth attribute child.
   * @param i the index of the child.
   *
   * @return the attribute child at the specified position
   * @throws ArrayIndexOutOfBoundsException if the index is out of bounds.
   * @throws NullPointerException if there are no children.
   */
  public AttrImpl getChildAttribute(int i)
          throws ArrayIndexOutOfBoundsException, NullPointerException
  {
    if(i < 0)  // bug fix attributed to Norman Walsh <ndw@nwalsh.com>
      return null; 

    synchronized (m_doc)
    {
      if (null != m_firstAttr)
      {
        Child next = m_firstAttr;

        for (int k = 0; k < i; k++)
        {
          if (null == next)
            return null;

          next = next.m_next;
        }

        return (AttrImpl) next;
      }
      else
        return null;
    }
  }

  /**
   * Get the number of children this node currently contains.
   * Factor in the number of attributes at beginning of list.
   * Note that this will only return the number of children
   * added so far.  If the isComplete property is false,
   * it is likely that more children will be added.
   * DON'T CALL THIS FUNCTION IF YOU CAN HELP IT!!!!!!
   *
   * @return the number of children this node currently contains
   */
  public int getChildCount()
  {

    if (!isComplete())
    {
      synchronized (m_doc)
      {
        try
        {

          // Here we have to wait until the element is complete
          while (!isComplete())
          {
            m_doc.wait(100);
            throwIfParseError();
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }

        //System.out.println("/// gotelcount " );
      }
    }

    return m_childCount;
  }

  /**
   * Get attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @return a NamedNodeMap containing this node's attributes
   */
  public NamedNodeMap getAttributes()
  {
    return this;
  }

  /**
   * Set attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @param name attribute name
   * @param value attribute value
   *
   * @throws DOMException
   */
  public void setAttribute(String name, String value) throws DOMException
  {

    AttrImpl attr = (AttrImpl) createAttribute(name);

    attr.setValue(value);
  }

  /**
   * Set attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @param namespaceURI Attribute name space
   * @param qualifiedName Attribute qualified name
   * @param value Attribute value
   *
   * @throws DOMException
   */
  public void setAttributeNS(
          String namespaceURI, String qualifiedName, String value)
            throws DOMException
  {

    AttrImplNS attr = (AttrImplNS) createAttributeNS(namespaceURI,
                        qualifiedName);

    attr.setValue(value);
  }

  /**
   * Set a list of attributes of this node.
   * <br>For nodes created with a DOM Level 1 method, such as
   * <code>createElement</code> from the <code>Document</code> interface,
   * it is <code>null</code>.
   * @since DOM Level 2
   *
   * @param atts List of attributes to set for this node
   *
   * @throws DOMException
   */
  public void setAttributes(Attributes atts) throws DOMException
  {

    for (int i = 0; i < atts.getLength(); i++)
    {
      String uri = atts.getURI(i);
      String name = atts.getQName(i);
      AttrImpl attr;

      if (null != uri || name.indexOf(':') > 0)
        attr = (AttrImplNS) createAttributeNS(uri, name);
      else
        attr = (AttrImpl) createAttribute(name);

      attr.setValue(atts.getValue(i));
    }
  }

  /**
   * Set the ID string to element association for this node.
   *
   * @param value The ID string, should not be null.
   */
  public void setIDAttribute(String value)
  {
    getDocumentImpl().setIDAttribute(value, this);
  }

  /**
   *  Create an attribute node.
   *
   * @param name Attribute name to create
   *
   * @return the created attribute object
   *
   * @throws DOMException
   */
  public Attr createAttribute(String name) throws DOMException
  {

    // System.out.println("name: "+name);
    AttrImpl attrImpl;

    if (QName.isXMLNSDecl(name))
    {
      attrImpl = new NameSpaceDecl(getDocumentImpl(),
                                   "http://www.w3.org/2000/xmlns/", name, "");
    }
    else
      attrImpl = new AttrImpl(getDocumentImpl(), name, "");

    boolean found = false;
    AttrImpl attr = m_firstAttr;

    while (null != attr)
    {
      if (attr.getNodeName().equals(name))
      {
        if (null != attr.m_prev)
          attr.m_prev.m_next = attr.m_next;

        if (null != m_next)
          attr.m_next.m_prev = attr.m_prev;

        attr.m_next = null;
        attr.m_prev = null;
        found = true;

        break;
      }

      attr = (AttrImpl) attr.m_next;
    }

    if (!found)
    {
      if (null == m_firstAttr)
      {
        m_firstAttr = attrImpl;
      }
      else
      {
        m_lastAttr.m_next = attrImpl;
        attrImpl.m_prev = m_lastAttr;
      }

      m_lastAttr = attrImpl;

      m_doc.incrementDocOrderCount();
      attrImpl.setUid(m_doc.getDocOrderCount());
      attrImpl.setParent(this);
      attrImpl.setLevel((short) (getLevel() + 1));

      m_attrsEnd++;
    }

    return (Attr) attrImpl;
  }

  /**
   * Create an attribute node with a namespace .
   *
   * @param namespaceURI name space of the attribute to create 
   * @param qualifiedName qualified name of the attribute to create
   *
   * @return the created attribute object
   *
   * @throws DOMException
   */
  public Attr createAttributeNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {

    // System.out.println("qualifiedName: "+qualifiedName);
    AttrImplNS attrImpl = new AttrImplNS(getDocumentImpl(), namespaceURI,
                                         qualifiedName, "");
    boolean found = false;
    AttrImpl attr = m_firstAttr;

    while (null != attr)
    {
      String attrURI = attr.getNamespaceURI();
      if(null == attrURI) // defensive, shouldn't have to do this.
        attrURI = "";
      if (attr.getLocalName().equals(attrImpl.getLocalName())
              && attrURI.equals(attrImpl.getNamespaceURI()))
      {
        if (null != attr.m_prev)
          attr.m_prev.m_next = attr.m_next;

        if (null != m_next)
          attr.m_next.m_prev = attr.m_prev;

        attr.m_next = null;
        attr.m_prev = null;
        found = true;

        break;
      }

      attr = (AttrImpl) attr.m_next;
    }

    if (!found)
    {
      if (null == m_firstAttr)
      {
        m_firstAttr = attrImpl;
      }
      else
      {
        m_lastAttr.m_next = attrImpl;
        attrImpl.m_prev = m_lastAttr;
      }

      m_lastAttr = attrImpl;

      m_doc.incrementDocOrderCount();
      attrImpl.setUid(m_doc.getDocOrderCount());
      attrImpl.setParent(this);
      attrImpl.setLevel((short) (getLevel() + 1));

      m_attrsEnd++;
    }

    return (Attr) attrImpl;
  }

  //
  //implement Attributes Interface
  //

  /**
   * Return the number of attributes in the list.
   *
   * @return The number of attributes in the list.
   */
  public int getAttrCount()
  {
    synchronized (m_doc)
    {
      return m_attrsEnd;
    }
  }

  /**
   * Look up an attribute's Namespace URI by index.
   *
   * @param index The attribute index (zero-based).
   * @return The Namespace URI, or the empty string if none
   *         is available, or null if the index is out of
   *         range.
   */
  public String getURI(int index)
  {

    AttrImpl attr = getChildAttribute(index);

    if (null != attr)
      return attr.getNamespaceURI();
    else
      return null;
  }

  /**
   * Look up an attribute's local name by index.
   *
   * @param index The attribute index (zero-based).
   * @return The local name, or the empty string if Namespace
   *         processing is not being performed, or null
   *         if the index is out of range.
   */
  public String getLocalName(int index)
  {

    AttrImpl attr = getChildAttribute(index);

    if (null != attr)
      return attr.getLocalName();
    else
      return null;
  }

  /**
   * Look up an attribute's raw XML 1.0 name by index.
   *
   * @param index The attribute index (zero-based).
   * @return The raw XML 1.0 name, or the empty string
   *         if none is available, or null if the index
   *         is out of range.
   */
  public String getQName(int index)
  {

    AttrImpl attr = getChildAttribute(index);

    if (null != attr)
      return attr.getNodeName();
    else
      return null;
  }

  /**
   * Look up an attribute's type by index.
   *
   * @param index The attribute index (zero-based).
   * @return The attribute's type as a string, or null if the
   *         index is out of range.
   */
  public String getType(int index)
  {

    AttrImpl attr = getChildAttribute(index);

    if (null != attr)
      return Integer.toString(attr.getNodeType());
    else
      return null;
  }

  /**
   * Look up an attribute's value by index.
   *
   * @param index The attribute index (zero-based).
   * @return The attribute's value as a string, or null if the
   *         index is out of range.
   */
  public String getValue(int index)
  {

    AttrImpl attr = getChildAttribute(index);

    if (null != attr)
      return attr.getValue();
    else
      return null;
  }

  /**
   * Look up an attribute's value by name.
   * 
   * @param name The attribute name to look up
   * @return The attribute's value as a string, or null if the
   *         index is out of range.
   */
  public String getAttribute(String name)
  {
    return getValue(name);
  }

  ////////////////////////////////////////////////////////////////////
  // Name-based query.
  ////////////////////////////////////////////////////////////////////

  /**
   * Look up the index of an attribute by Namespace name.
   *
   * @param uri The Namespace URI, or the empty string if
   *        the name has no Namespace URI.
   * @param localPart The attribute's local name.
   * 
   * @return The index of the attribute, or -1 if it does not
   *         appear in the list.
   */
  public int getIndex(String uri, String localPart)
  {

    for (int i = 0; i < getAttrCount(); i++)
    {
      AttrImpl attr = (AttrImpl) getChildAttribute(i);

      if (attr.getLocalName().equals(localPart)
              && attr.getNamespaceURI().equals(uri))
        return i;
    }

    return -1;
  }

  /**
   * Look up the index of an attribute by raw XML 1.0 name.
   *
   * @param rawName The raw (prefixed) name.
   * @return The index of the attribute, or -1 if it does not
   *         appear in the list.
   */
  public int getIndex(String rawName)
  {

    for (int i = 0; i < getAttrCount(); i++)
    {
      AttrImpl attr = getChildAttribute(i);

      if (attr.getNodeName().equals(rawName))
        return i;
    }

    return -1;
  }

  /**
   * Look up an attribute's type by Namespace name.
   *
   * @param uri The Namespace URI, or the empty String if the
   *        name has no Namespace URI.
   * @param localName The local name of the attribute.
   * @return The attribute type as a string, or null if the
   *         attribute is not in the list or if Namespace
   *         processing is not being performed.
   */
  public String getType(String uri, String localName)
  {

    for (int i = 0; i < getAttrCount(); i++)
    {
      AttrImpl attr = (AttrImpl) getChildAttribute(i);

      if (attr.getLocalName().equals(localName)
              && attr.getNamespaceURI().equals(uri))
        return Integer.toString(attr.getNodeType());
    }

    return null;
  }

  /**
   * Look up an attribute's type by raw XML 1.0 name.
   *
   * @param rawName The raw XML 1.0 name.
   * @return The attribute type as a string, or null if the
   *         attribute is not in the list or if raw names
   *         are not available.
   */
  public String getType(String rawName)
  {

    for (int i = 0; i < getAttrCount(); i++)
    {
      AttrImpl attr = getChildAttribute(i);

      if (attr.getNodeName().equals(rawName))
        return Integer.toString(attr.getNodeType());
    }

    return null;
  }

  /**
   * Look up an attribute's value by Namespace name.
   *
   * @param uri The Namespace URI, or the empty String if the
   *        name has no Namespace URI.
   * @param localName The local name of the attribute.
   * @return The attribute value as a string, or null if the
   *         attribute is not in the list.
   */
  public String getValue(String uri, String localName)
  {

    for (int i = 0; i < getAttrCount(); i++)
    {
      AttrImpl attr = (AttrImpl) getChildAttribute(i);

      if (attr.getLocalName().equals(localName)
              && attr.getNamespaceURI().equals(uri))
        return attr.getValue();
    }

    return null;
  }

  /**
   * Look up an attribute's value by raw XML 1.0 name.
   *
   * @param rawName The raw XML 1.0 name.
   * @return The attribute value as a string, or null if the
   *         attribute is not in the list or if raw names
   *         are not available.
   */
  public String getValue(String rawName)
  {

    for (int i = 0; i < getAttrCount(); i++)
    {
      AttrImpl attr = getChildAttribute(i);

      if (attr.getNodeName().equals(rawName))
        return attr.getValue();
    }

    return null;
  }

  ////////////////////////////  
  // Implement NamedNodeMap //
  ////////////////////////////

  /**
   * Get the child attribute with the specified attribute name 
   *
   *
   * @param name Attribute name to look up 
   *
   * @return The found attribute node or null if not found
   */
  public Node getNamedItem(String name)
  {
    return getChildAttribute(getIndex(name));
  }

  /**
   *  Adds a node using its <code>nodeName</code> attribute. If a node with
   * that name is already present in this map, it is replaced by the new
   * one.
   * <br> As the <code>nodeName</code> attribute is used to derive the name
   * which the node must be stored under, multiple nodes of certain types
   * (those that have a "special" string value) cannot be stored as the
   * names would clash. This is seen as preferable to allowing nodes to be
   * aliased.
   * @param arg  A node to store in this map. The node will later be
   *   accessible using the value of its <code>nodeName</code> attribute.
   * @return  If the new <code>Node</code> replaces an existing node the
   *   replaced <code>Node</code> is returned, otherwise <code>null</code>
   *   is returned.
   * @throws DOMException
   *    WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a
   *   different document than the one that created this map.
   *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   *   <br> INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an
   *   <code>Attr</code> that is already an attribute of another
   *   <code>Element</code> object. The DOM user must explicitly clone
   *   <code>Attr</code> nodes to re-use them in other elements.
   */
  public Node setNamedItem(Node arg) throws DOMException
  {

    setAttribute(((Attr) arg).getName(), ((Attr) arg).getValue());

    return getChildAttribute(getIndex(((Attr) arg).getName()));
  }

  /**
   *  Removes a node specified by name. A removed attribute may be known to
   * have a default value when this map contains the attributes attached to
   * an element, as returned by the attributes attribute of the
   * <code>Node</code> interface. If so, an attribute immediately appears
   * containing the default value as well as the corresponding namespace
   * URI, local name, and prefix when applicable.
   * @param name  The <code>nodeName</code> of the node to remove.
   * @return  The node removed from this map if a node with such a name
   *   exists.
   * @throws DOMException
   *    NOT_FOUND_ERR: Raised if there is no node named <code>name</code>
   *   in this map.
   *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   */
  public Node removeNamedItem(String name) throws DOMException
  {

    int index = getIndex(name);

    return removeItem(index);
  }

  /**
   * Remove the attribute at the specified index from the attribute list 
   *
   *
   * @param index Position of attribute to remove in attribute list 
   *
   * @return the removed attribute or null if not found  
   *
   * @throws DOMException
   */
  public Node removeItem(int index) throws DOMException
  {

    AttrImpl attr = m_firstAttr;
    int pos = 0;

    while (null != attr)
    {
      if (pos == index)
      {
        if (null != attr.m_prev)
          attr.m_prev.m_next = attr.m_next;

        if (null != m_next)
          attr.m_next.m_prev = attr.m_prev;

        attr.m_next = null;
        attr.m_prev = null;

        return attr;
      }

      attr = (AttrImpl) attr.m_next;

      pos++;
    }

    return null;
  }

  /**
   * The number of nodes (attributes) in this map.
   * The range of valid child node indices
   * is <code>0</code> to <code>length-1</code> inclusive.
   *
   * @return The nunber of attribute nodes associated with this element.
   */
  public int getLength()
  {
    return getAttrCount();
  }  // getLength():int

  /**
   *  Returns the <code>index</code> th item in the map. If
   * <code>index</code> is greater than or equal to the number of nodes in
   * this map, this returns <code>null</code> .
   * @param index  Index into this map.
   * @return  The node at the <code>index</code> th position in the map, or
   *   <code>null</code> if that is not a valid index.
   */
  public Node item(int index)
  {
    return getChildAttribute(index);
  }
  
  /**
   *  Retrieves a node specified by local name and namespace URI. HTML-only
   * DOM implementations do not need to implement this method.
   * @param namespaceURI  The  namespace URI of the node to retrieve.
   * @param localName  The  local name of the node to retrieve.
   * @return  A <code>Node</code> (of any type) with the specified local
   *   name and namespace URI, or <code>null</code> if they do not identify
   *   any node in this map.
   * @since DOM Level 2
   */
  public Node getNamedItemNS(String namespaceURI, String localName)
  {
    return getChildAttribute(getIndex(namespaceURI, localName));
  }

  /**
   *  Adds a node using its <code>namespaceURI</code> and
   * <code>localName</code> . If a node with that namespace URI and that
   * local name is already present in this map, it is replaced by the new
   * one.
   * <br> HTML-only DOM implementations do not need to implement this method.
   * @param arg  A node to store in this map. The node will later be
   *   accessible using the value of its <code>namespaceURI</code> and
   *   <code>localName</code> attributes.
   * @return  If the new <code>Node</code> replaces an existing node the
   *   replaced <code>Node</code> is returned, otherwise <code>null</code>
   *   is returned.
   * @throws DOMException
   *    WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a
   *   different document than the one that created this map.
   *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   *   <br> INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an
   *   <code>Attr</code> that is already an attribute of another
   *   <code>Element</code> object. The DOM user must explicitly clone
   *   <code>Attr</code> nodes to re-use them in other elements.
   * @since DOM Level 2
   */
  public Node setNamedItemNS(Node arg) throws DOMException
  {

    setAttributeNS(((Attr) arg).getNamespaceURI(), ((Attr) arg).getName(),
                   ((Attr) arg).getValue());

    return getChildAttribute(getIndex(((Attr) arg).getNamespaceURI(),
                                      ((Attr) arg).getName()));
  }

  /**
   *  Removes a node specified by local name and namespace URI. A removed
   * attribute may be known to have a default value when this map contains
   * the attributes attached to an element, as returned by the attributes
   * attribute of the <code>Node</code> interface. If so, an attribute
   * immediately appears containing the default value as well as the
   * corresponding namespace URI, local name, and prefix when applicable.
   * <br> HTML-only DOM implementations do not need to implement this method.
   * @param namespaceURI  The  namespace URI of the node to remove.
   * @param localName  The  local name of the node to remove.
   * @return  The node removed from this map if a node with such a local
   *   name and namespace URI exists.
   * @throws DOMException
   *    NOT_FOUND_ERR: Raised if there is no node with the specified
   *   <code>namespaceURI</code> and <code>localName</code> in this map.
   *   <br> NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   * @since DOM Level 2
   */
  public Node removeNamedItemNS(String namespaceURI, String localName)
          throws DOMException
  {

    int index = getIndex(namespaceURI, localName);

    return removeItem(index);
  }
  
}
