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

import org.apache.xpath.patterns.NodeTest;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * <p>
 * The ColumnHeader is a special branch of the document that provides
 * a mechinsim to interogate columns that are returned from the query
 * without accessing the data or moving the row counter.
 * </p>
 *
 * <pre>
 * The DTD is as follows.
 *
 * <p><HR>
 * &lt;row-set&gt;
 *    &lt;column-header &lt;attribute list&gt; /&gt;
 * &lt;/row-set&gt;
 *<HR>
 *</p>
 *</pre>
 *
 * <p>
 * To retrive a list of column labels that are available, walk the
 * column-header elements and access the @column-label attribute.
 *
 * The column arrributes are actually store in an array of the RowSet
 * object.
 *
 * This object will only give you the attributes for the current column
 * header element (a.k.a. the current column)  Access to the other columns
 * is through the parent (row-set) array.
 * </p>
 */
public class ColumnHeader extends StreamableNode implements NamedNodeMap
{

  /** Flag for DEBUG mode         */
  private static final boolean DEBUG = false;

  /** Column index         */
  int m_columnIndex;

  /** Meta data          */
  ResultSetMetaData m_metaData;

  /** Parent node, a row-set          */
  RowSet m_parent;

  /**
   * Constructor ColumnHeader
   *
   *
   * @param statement Owning document
   * @param parent Parent node, a row-set
   * @param columnIndex Index of column this header is for
   * @param metaData Meta data
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
   * Return Node name, "column-header".
   *
   * @return "column-header".
   */
  public String getNodeName()
  {
    return XStatement.S_COLUMNHEADERNAME;
  }

  /**
   * getFirstChild - Always returns null.
   *
   * @return null
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

    NodeTest nt = this.getNodeTest();
    if ((null == nt) || nt.getNamespace() == null)
    {
      if (
        (null == nt) ||
        nt.getLocalName().equals(XStatement.S_COLUMNHEADERNAME))
      {
        int nextIndex = m_columnIndex + 1;

        if (nextIndex < m_parent.m_columnHeaders.length)
        {
          if (null == m_parent.m_columnHeaders[nextIndex])
          {
            m_parent.m_columnHeaders[nextIndex] =
              new ColumnHeader(getXStatement(),m_parent,nextIndex,m_metaData);
          }
          return m_parent.m_columnHeaders[nextIndex];
        }
        else if(nt == null)
        {
          return new Row(getXStatement(), m_parent, null);
        }
        else return null;
      }
    }

    return null;
  }

  /**
   * The parent node of a column-header Node is the row-set Node.
   *
   * @return a RowSet.
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
   * @return false
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
   * @return This node.
   */
  public NamedNodeMap getAttributes()
  {
    return this;
  }

  // ============= NamedNodeMap ===============

  /** Array of attributes for column          */
  ColumnAttribute[] m_attributes = null;

  /**
   * Allocate an array of attributes for this column
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
   * Create an attribute
   *
   *
   * @param pos Index of attribute in array
   * @param name Attribut name
   *
   * @return The attribute at the given index
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
   * @param name Attribute name
   *
   * @return Attribute with given name or null if not found
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
   * @param index Index of attribut to get
   *
   * @return Attribute node at given index or null if not found
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
   * @return the number of attributes of column
   */
  public int getLength()
  {
    return ColumnAttribute.NUMBER_ATTRIBUTES;
  }

  /**
   * Get an attribute by namespaced name from the metadata for this column.
   *
   *
   * @param namespaceURI Namespace URI of attribute
   * @param localName Local name of attribute
   *
   * @return the attribute with the given local name and a null
   * namespace, or null.
   */
  public Node getNamedItemNS(String namespaceURI, String localName)
  {

    if (null == namespaceURI)
      return getNamedItem(localName);

    return null;
  }

  /**
   * Set an attribute from the metadata for this column. Not supported
   *
   *
   * @param arg
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
   * Remove an attribute - Not supported
   *
   *
   * @param name
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
   * Set namespaced attribute - Not supported
   *
   *
   * @param arg
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
   * Removed namespaced attribute - Not supported
   *
   *
   * @param namespaceURI
   * @param localName
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
}
