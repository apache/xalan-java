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
 * This is the superclass for all nodes in the org.apache.xalan.lib.sql package.
 */
public class StreamableNode extends UnImplNode
        implements NodeTestFilter, NamedNodeMap, DOMOrder
{

  /** NEEDSDOC Field m_statement          */
  private XStatement m_statement;

  /**
   * NEEDSDOC Method getXStatement 
   *
   *
   * NEEDSDOC (getXStatement) @return
   */
  public XStatement getXStatement()
  {
    return m_statement;
  }

  /**
   * NEEDSDOC Method isSupported 
   *
   *
   * NEEDSDOC @param feature
   * NEEDSDOC @param version
   *
   * NEEDSDOC (supports) @return
   */
  public boolean isSupported(String feature, String version)
  {
    return (AxesWalker.FEATURE_NODETESTFILTER == feature)
           || feature.equals(AxesWalker.FEATURE_NODETESTFILTER);
  }

  /** NEEDSDOC Field m_nodetest          */
  private NodeTest m_nodetest;

  /**
   * NEEDSDOC Method getNodeTest 
   *
   *
   * NEEDSDOC (getNodeTest) @return
   */
  public NodeTest getNodeTest()
  {
    return m_nodetest;
  }

  /**
   * Constructor StreamableNode
   *
   *
   * NEEDSDOC @param statement
   */
  public StreamableNode(XStatement statement)
  {

    m_statement = statement;

    if (null != statement)
    {
      m_orderIndex = m_statement.getAndIncrementNodeCounter();
    }
  }

  /**
   * NEEDSDOC Method setNodeTest 
   *
   *
   * NEEDSDOC @param nodeTest
   */
  public void setNodeTest(NodeTest nodeTest)
  {
    m_nodetest = nodeTest;
  }

  /**
   * NEEDSDOC Method getOwnerDocument 
   *
   *
   * NEEDSDOC (getOwnerDocument) @return
   */
  public Document getOwnerDocument()
  {
    return m_statement;
  }

  /**
   * Streamable nodes default to being elements.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.ELEMENT_NODE;
  }

  /**
   * Return NodeName.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getLocalName()
  {
    return getNodeName();
  }

  /**
   * Returns null.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNamespaceURI()
  {
    return null;
  }

  /**
   * Returns null.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getPrefix()
  {
    return null;
  }

  /**
   * NEEDSDOC Method getAttributes 
   *
   *
   * NEEDSDOC (getAttributes) @return
   */
  public NamedNodeMap getAttributes()
  {
    return this;
  }

  /**
   * NEEDSDOC Method getNamedItem 
   *
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC (getNamedItem) @return
   */
  public Node getNamedItem(String name)
  {
    return null;
  }

  /**
   * NEEDSDOC Method item 
   *
   *
   * NEEDSDOC @param index
   *
   * NEEDSDOC (item) @return
   */
  public Node item(int index)
  {
    return null;
  }

  /**
   * NEEDSDOC Method getLength 
   *
   *
   * NEEDSDOC (getLength) @return
   */
  public int getLength()
  {
    return 0;
  }

  /**
   * NEEDSDOC Method getNamedItemNS 
   *
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *
   * NEEDSDOC (getNamedItemNS) @return
   */
  public Node getNamedItemNS(String namespaceURI, String localName)
  {
    return null;
  }

  /**
   * NEEDSDOC Method setNamedItem 
   *
   *
   * NEEDSDOC @param arg
   *
   * NEEDSDOC (setNamedItem) @return
   *
   * @throws DOMException
   */
  public Node setNamedItem(Node arg) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * NEEDSDOC Method removeNamedItem 
   *
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC (removeNamedItem) @return
   *
   * @throws DOMException
   */
  public Node removeNamedItem(String name) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * NEEDSDOC Method setNamedItemNS 
   *
   *
   * NEEDSDOC @param arg
   *
   * NEEDSDOC (setNamedItemNS) @return
   *
   * @throws DOMException
   */
  public Node setNamedItemNS(Node arg) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * NEEDSDOC Method removeNamedItemNS 
   *
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *
   * NEEDSDOC (removeNamedItemNS) @return
   *
   * @throws DOMException
   */
  public Node removeNamedItemNS(String namespaceURI, String localName)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /** NEEDSDOC Field m_orderIndex          */
  int m_orderIndex = -1;

  /**
   * Get the UID (document order index).
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getUid()
  {

    if (-1 == m_orderIndex)
    {
      if (null != m_statement)
      {
        m_orderIndex = m_statement.getAndIncrementNodeCounter();
      }
      else
        m_orderIndex = 0;  // ?
    }

    // System.out.println(" Returning UID: "+m_orderIndex);
    // System.out.flush();
    return m_orderIndex;
  }
}
