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
package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;

import org.apache.xml.utils.UnImplNode;
import org.apache.xpath.patterns.NodeTestFilter;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.DOMOrder;
import org.apache.xpath.axes.AxesWalker;

/**
 * <p>
 * The StreamableNode really just provides a base implemtation
 * for the other SQL Node based classes. It support keeping track
 * of the Document Order Index where it is just incermented to assure
 * that the node are considered seperate and assign a distance in the
 * Document. It also provides a common reference to the Document Root
 * or the XStatement object.
 * </p>
 *
 */
public class StreamableNode extends UnImplNode
        implements NodeTestFilter, NamedNodeMap, DOMOrder
{

  /** Owning document         */
  private XStatement m_statement;

  /**
   * Get XStatement (owning document)
   *
   *
   * @return owning document
   */
  public XStatement getXStatement()
  {
    return m_statement;
  }

  /**
   * Check if a given feature is supported
   *
   *
   * @param feature Feature to check
   * @param version Version to check
   *
   * @return True if NodeTest feature is supported
   */
  public boolean isSupported(String feature, String version)
  {
    return (AxesWalker.FEATURE_NODETESTFILTER == feature)
           || feature.equals(AxesWalker.FEATURE_NODETESTFILTER);
  }

  /** Instance of a NodeTest          */
  private NodeTest m_nodetest;

  /**
   * Return the current NodeTest instance
   *
   *
   * @return the current NodeTest instance
   */
  public NodeTest getNodeTest()
  {
    return m_nodetest;
  }

  /**
   * Constructor StreamableNode
   *
   *
   * @param statement Owning document
   */
  public StreamableNode(XStatement statement)
  {
    m_statement = statement;
    incermentOrderIndex();
  }

  /**
   * Set NodeTest instance
   *
   *
   * @param nodeTest The NodeTest to use
   */
  public void setNodeTest(NodeTest nodeTest)
  {
    m_nodetest = nodeTest;
  }

  /**
   * Get Owner Document
   *
   *
   * @return owner document
   */
  public Document getOwnerDocument()
  {
    return m_statement;
  }

  /**
   * Streamable nodes default to being elements.
   *
   * @return Node.ELEMENT_NODE;
   */
  public short getNodeType()
  {
    return Node.ELEMENT_NODE;
  }

  /**
   * Return NodeName.
   *
   * @return the node name
   */
  public String getLocalName()
  {
    return getNodeName();
  }

  /**
   * getNamespaceURI - Always Returns null.
   *
   * @return null
   */
  public String getNamespaceURI()
  {
    return null;
  }

  /**
   * getPrefix - Always Returns null.
   *
   * @return null
   */
  public String getPrefix()
  {
    return null;
  }

  /**
   * Get list of attributes
   *
   *
   * @return the list of attributes for this node, itself
   */
  public NamedNodeMap getAttributes()
  {
    return this;
  }

  /**
   * Get the attribute with the given name - Not implemented
   *
   *
   * @param name attribute name to get
   *
   * @return null
   */
  public Node getNamedItem(String name)
  {
    return null;
  }

  /**
   * Return the attribute at the given index - Not implemented
   *
   *
   * @param index Index of attribute to get
   *
   * @return null
   */
  public Node item(int index)
  {
    return null;
  }

  /**
   * The number of attributes for this node - Not implemented
   *
   *
   * @return 0
   */
  public int getLength()
  {
    return 0;
  }

  /**
   * Get the attribute with the given namespaced name - Not implemented
   *
   *
   * @param namespaceURI Namespace URI of the attribute to get
   * @param localName Local name of the attribute to get
   *
   * @return null
   */
  public Node getNamedItemNS(String namespaceURI, String localName)
  {
    return null;
  }

  /**
   * Set the given attribute - Not supported
   *
   *
   * @param arg attribute node
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node setNamedItem(Node arg) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Remove the attribute with the given name - Not supported
   *
   *
   * @param name Attribute name
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node removeNamedItem(String name) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Set the attribute with the given namespaced name - Not supported
   *
   *
   * @param arg Attriute node
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node setNamedItemNS(Node arg) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Remove the attribute with the given namespaced name - Not supported
   *
   *
   * @param namespaceURI Namespace URI of the attribute to remove
   * @param localName Local name of the attribute to remove
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node removeNamedItemNS(String namespaceURI, String localName)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /** Document Order index of this node         */
  int m_orderIndex = -1;

  /**
   * Get the UID (document order index).
   *
   * @return document order index for this node
   */
  public int getUid()
  {

    if (-1 == m_orderIndex)
    {
      incermentOrderIndex();
    }

//    if (DEBUG)
//      System.out.println(" Returning UID: "+m_orderIndex);

    return m_orderIndex;
  }

  public void incermentOrderIndex()
  {
    if (null != m_statement)
    {
      m_orderIndex = m_statement.getAndIncrementNodeCounter();
    }
    else m_orderIndex = 0;
  }
}
