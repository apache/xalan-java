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

import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;

/**
 * <meta name="usage" content="internal"/>
 * Class to hold information about a DocumentType node 
 */
public class DocumentTypeImpl extends Child implements DocumentType, NamedNodeMap
{

  /**
   * Constructor DocumentTypeImpl
   *
   *
   * @param doc Document Object
   * @param name Node name
   */
  DocumentTypeImpl(DocumentImpl doc, String name)
  {

    super(doc);

    m_name = name;
  }
  
  /**
   * Constructor DocumentTypeImpl
   *
   *
   * @param doc Document Object
   * @param name Node name
   */
  DocumentTypeImpl(DocumentImpl doc, String name, String publicId, String systemId)
  {

    super(doc);

    m_name = name;
    m_systemID = systemId;
    m_publicID = publicId;
  }

  /** DocumentType node name          */
  private String m_name;

  /** Document Type publicID          */
  private String m_publicID;

  /** Document Type systemID          */
  private String m_systemID;

  /** Document Type internalSubset          */
  private String m_internalSubset;

  /**
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   *
   * @return Document Type node type
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_TYPE_NODE;
  }

  /**
   * Returns the node name. 
   *
   * @return Document Type node name
   */
  public String getNodeName()
  {
    return m_name;  // I guess I need the name of the document type
  }

  /**
   * The name of DTD; i.e., the name immediately following the
   * <code>DOCTYPE</code> keyword.
   *
   * @return Document Type name
   */
  public String getName()
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
   * @return Document Type local name
   */
  public String getLocalName()
  {
    return m_name;
  }

  /**
   * A <code>NamedNodeMap</code> containing the general entities, both
   * external and internal, declared in the DTD. Parameter entities are not
   *  contained. Duplicates are discarded. For example in:
   * <pre>
   * &lt;!DOCTYPE ex SYSTEM "ex.dtd" [
   *   &lt;!ENTITY foo "foo"&gt;
   *   &lt;!ENTITY bar "bar"&gt;
   *   &lt;!ENTITY bar "bar2"&gt;
   *   &lt;!ENTITY % baz "baz"&gt;
   * ]&gt;
   * &lt;ex/&gt;</pre>
   *   the interface
   * provides access to <code>foo</code> and the first declaration of
   * <code>bar</code> but not the second declaration of  <code>bar</code>
   * or <code>baz</code>. Every node in this map also implements the
   * <code>Entity</code> interface.
   * <br>The DOM Level 2 does not support editing entities, therefore
   * <code>entities</code> cannot be altered in any way.
   *
   * @return both external and internal entities declared in the DTD
   */
  public NamedNodeMap getEntities()
  {
    return this;
  }

  /**
   * A <code>NamedNodeMap</code> containing  the notations declared in the
   * DTD. Duplicates are discarded. Every node in this map also implements
   * the <code>Notation</code> interface.
   * <br>The DOM Level 2 does not support editing notations, therefore
   * <code>notations</code> cannot be altered in any way.
   *
   * @return notations declared in the DTD
   */
  public NamedNodeMap getNotations()
  {
    return null;
  }

  /**
   * The public identifier of the external subset.
   * @since DOM Level 2
   *
   * @return public identifier
   */
  public String getPublicId()
  {
    return m_publicID;
  }

  /**
   * The system identifier of the external subset.
   * @since DOM Level 2
   *
   * @return system identifier
   */
  public String getSystemId()
  {
    return m_systemID;
  }

  /**
   * The internal subset as a string.
   * @since DOM Level 2
   *
   * @return internal subset as a string
   */
  public String getInternalSubset()
  {
    return m_internalSubset;
  }
  
  Hashtable m_entities = new Hashtable();
  
  /**
   * Retrieves a node specified by name.
   * @param nameThe <code>nodeName</code> of a node to retrieve.
   * @return A <code>Node</code> (of any type) with the specified 
   *   <code>nodeName</code>, or <code>null</code> if it does not identify 
   *   any node in this map.
   */
  public Node getNamedItem(String name)
  {
    return (Node)m_entities.get(name);
  }

  /**
   * Adds a node using its <code>nodeName</code> attribute. If a node with 
   * that name is already present in this map, it is replaced by the new 
   * one.
   * <br>As the <code>nodeName</code> attribute is used to derive the name 
   * which the node must be stored under, multiple nodes of certain types 
   * (those that have a "special" string value) cannot be stored as the 
   * names would clash. This is seen as preferable to allowing nodes to be 
   * aliased.
   * @param argA node to store in this map. The node will later be 
   *   accessible using the value of its <code>nodeName</code> attribute.
   * @return If the new <code>Node</code> replaces an existing node the 
   *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
   *   is returned.
   * @throws DOMException
   *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
   *   different document than the one that created this map.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
   *   <code>Attr</code> that is already an attribute of another 
   *   <code>Element</code> object. The DOM user must explicitly clone 
   *   <code>Attr</code> nodes to re-use them in other elements.
   */
  public Node setNamedItem(Node arg)
    throws DOMException
  {
    m_entities.put(arg.getNodeName(), arg);
    return null;
  }

  /**
   * Removes a node specified by name. When this map contains the attributes 
   * attached to an element, if the removed attribute is known to have a 
   * default value, an attribute immediately appears containing the 
   * default value as well as the corresponding namespace URI, local name, 
   * and prefix when applicable.
   * @param nameThe <code>nodeName</code> of the node to remove.
   * @return The node removed from this map if a node with such a name 
   *   exists.
   * @throws DOMException
   *   NOT_FOUND_ERR: Raised if there is no node named <code>name</code> in 
   *   this map.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   */
  public Node removeNamedItem(String name)
    throws DOMException
  {
    return null;
  }

  /**
   * Returns the <code>index</code>th item in the map. If <code>index</code> 
   * is greater than or equal to the number of nodes in this map, this 
   * returns <code>null</code>.
   * @param indexIndex into this map.
   * @return The node at the <code>index</code>th position in the map, or 
   *   <code>null</code> if that is not a valid index.
   */
  public Node item(int index)
  {
    return null;
  }

  /**
   * The number of nodes in this map. The range of valid child node indices 
   * is <code>0</code> to <code>length-1</code> inclusive. 
   */
  public int getLength()
  {
    return 0;
  }

  /**
   * Retrieves a node specified by local name and namespace URI. HTML-only 
   * DOM implementations do not need to implement this method.
   * @param namespaceURIThe namespace URI of the node to retrieve.
   * @param localNameThe local name of the node to retrieve.
   * @return A <code>Node</code> (of any type) with the specified local 
   *   name and namespace URI, or <code>null</code> if they do not 
   *   identify any node in this map.
   * @since DOM Level 2
   */
  public Node getNamedItemNS(String namespaceURI, 
                             String localName)
  {
    return null;
  }

  /**
   * Adds a node using its <code>namespaceURI</code> and 
   * <code>localName</code>. If a node with that namespace URI and that 
   * local name is already present in this map, it is replaced by the new 
   * one.
   * <br>HTML-only DOM implementations do not need to implement this method.
   * @param argA node to store in this map. The node will later be 
   *   accessible using the value of its <code>namespaceURI</code> and 
   *   <code>localName</code> attributes.
   * @return If the new <code>Node</code> replaces an existing node the 
   *   replaced <code>Node</code> is returned, otherwise <code>null</code> 
   *   is returned.
   * @throws DOMException
   *   WRONG_DOCUMENT_ERR: Raised if <code>arg</code> was created from a 
   *   different document than the one that created this map.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   *   <br>INUSE_ATTRIBUTE_ERR: Raised if <code>arg</code> is an 
   *   <code>Attr</code> that is already an attribute of another 
   *   <code>Element</code> object. The DOM user must explicitly clone 
   *   <code>Attr</code> nodes to re-use them in other elements.
   * @since DOM Level 2
   */
  public Node setNamedItemNS(Node arg)
    throws DOMException
  {
    return null;
  }

  /**
   * Removes a node specified by local name and namespace URI. A removed 
   * attribute may be known to have a default value when this map contains 
   * the attributes attached to an element, as returned by the attributes 
   * attribute of the <code>Node</code> interface. If so, an attribute 
   * immediately appears containing the default value as well as the 
   * corresponding namespace URI, local name, and prefix when applicable.
   * <br>HTML-only DOM implementations do not need to implement this method.
   * @param namespaceURIThe namespace URI of the node to remove.
   * @param localNameThe local name of the node to remove.
   * @return The node removed from this map if a node with such a local 
   *   name and namespace URI exists.
   * @throws DOMException
   *   NOT_FOUND_ERR: Raised if there is no node with the specified 
   *   <code>namespaceURI</code> and <code>localName</code> in this map.
   *   <br>NO_MODIFICATION_ALLOWED_ERR: Raised if this map is readonly.
   * @since DOM Level 2
   */
  public Node removeNamedItemNS(String namespaceURI, 
                                String localName)
    throws DOMException
  {
    return null;
  }

}
