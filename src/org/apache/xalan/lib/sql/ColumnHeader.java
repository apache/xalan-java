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
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * This class represents a column-header Node, which contains the metadata
 * for a column.
 */
public class ColumnHeader extends StreamableNode implements NamedNodeMap
{

  /** NEEDSDOC Field DEBUG          */
  private static final boolean DEBUG = false;

  /** NEEDSDOC Field m_columnIndex          */
  int m_columnIndex;

  /** NEEDSDOC Field m_metaData          */
  ResultSetMetaData m_metaData;

  /** NEEDSDOC Field m_parent          */
  RowSet m_parent;

  /**
   * Constructor ColumnHeader
   *
   *
   * NEEDSDOC @param statement
   * NEEDSDOC @param parent
   * NEEDSDOC @param columnIndex
   * NEEDSDOC @param metaData
   */
  public ColumnHeader(XStatement statement, RowSet parent, int columnIndex,
                      ResultSetMetaData metaData)
  {

    super(statement);

    m_columnIndex = columnIndex;
    m_metaData = metaData;
    m_parent = parent;
  }

  /**
   * Return "column-header".
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return XStatement.S_COLUMNHEADERNAME;
  }

  /**
   * Always returns null.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In ColumnHeader.getFirstChild");

    return null;
  }

  /**
   * Returns column-header Node for the next column.
   * @return a ColumnHeader Node or null.
   */
  public Node getNextSibling()
  {

    if (DEBUG)
      System.out.println("In ColumnHeader.getNextSibling");

    // try
    {
      if (this.getNodeTest().getNamespace() == null)
      {
        if (this.getNodeTest().getLocalName().equals(
                XStatement.S_COLUMNHEADERNAME))
        {
          int nextIndex = m_columnIndex + 1;

          if (nextIndex < m_parent.m_columnHeaders.length)
          {
            if (null == m_parent.m_columnHeaders[nextIndex])
              m_parent.m_columnHeaders[nextIndex] =
                new ColumnHeader(getXStatement(), m_parent, nextIndex,
                                 m_metaData);

            return m_parent.m_columnHeaders[nextIndex];
          }
          else
            return null;
        }
      }
    }

    // catch(SQLException sqle)
    {

      // diagnostics?
    }

    return null;
  }

  /**
   * The parent node of a column-header Node is the row-set Node.
   * @returns a RowSet.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getParentNode()
  {

    if (DEBUG)
      System.out.println("In ColumnHeader.getParentNode");

    return m_parent;
  }

  /**
   * Tell if there are any children of the column-header Node,
   * which is always false.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In ColumnHeader.hasChildNodes");

    return false;
  }

  /**
   * Return the metadata for this column.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public NamedNodeMap getAttributes()
  {
    return this;
  }

  // ============= NamedNodeMap ===============

  /** NEEDSDOC Field m_attributes          */
  ColumnAttribute[] m_attributes = null;

  /**
   * NEEDSDOC Method allocAttrs 
   *
   */
  private void allocAttrs()
  {

    // try
    {
      m_attributes = new ColumnAttribute[ColumnAttribute.NUMBER_ATTRIBUTES];
    }

    // catch(SQLException sqle)
    {

      // diagnostics?
    }
  }

  /**
   * NEEDSDOC Method allocAttr 
   *
   *
   * NEEDSDOC @param pos
   * NEEDSDOC @param name
   *
   * NEEDSDOC (allocAttr) @return
   */
  private ColumnAttribute allocAttr(int pos, String name)
  {

    if (null == m_attributes[pos])
    {
      ColumnAttribute attr = new ColumnAttribute(this.getXStatement(), this,
                                                 m_columnIndex, pos,
                                                 m_metaData);

      attr.m_name = name;
      m_attributes[pos] = attr;
    }

    return m_attributes[pos];
  }

  /**
   * Get an attribute by name from the metadata for this column.
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getNamedItem(String name)
  {

    if (null == m_attributes)
      allocAttrs();

    int pos = ColumnAttribute.getAttrPosFromName(name);

    if (pos >= 0)
    {
      return allocAttr(pos, name);
    }
    else
      return null;
  }

  /**
   * Get an attribute by index from the metadata for this column.
   *
   * NEEDSDOC @param index
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node item(int index)
  {

    if (null == m_attributes)
      allocAttrs();

    String name = ColumnAttribute.getAttrNameFromPos(index);

    if (null != name)
    {
      return allocAttr(index, name);
    }
    else
      return null;
  }

  /**
   * Get the number of attributes of column metadata attributes.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public int getLength()
  {
    return ColumnAttribute.NUMBER_ATTRIBUTES;
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

    if (null == namespaceURI)
      return getNamedItem(localName);

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
}
