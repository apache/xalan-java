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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


/**
 * <p>
 * A JDBC Query returns a resultSet that contains rows of
 * Data. In each row are entried for each column. The Column
 * Object is used to represent the information about an
 * individual column. i.e. It represents the &lt;col&gt; element.
 * From this object, the attributes of that colum can be interrogated
 * {@link org.apache.xalan.lib.sql.ColumnAttribute} or the value of
 * the data that the current column rpresents
 * {@link org.apache.xalan.lib.sql.ColumnData}
 *
 * </p>
 * <p>
 * This object only represents the current column but a full array of
 * all the columns are held in the Parent Row object. The array is
 * zero based and the position of the current column is held in the
 * m_columnIndex class field, the parent row information is held
 * in the m_parentRow and the extent of the array is available in
 * the m_childCount from the m_parentRow object.
 *</p>
 */
public class Column extends StreamableNode
{

  /**
   * column Index, our position in the column line up
   * The first position being zero.
   */
  int m_columnIndex;

  /** Parent row node  */
  Row m_parent;

  /** Flag indicating if in DEBUG mode         */
  private static final boolean DEBUG = false;

  /** Column data   */
  ColumnData m_text = null;

  /**
   * Build up an instance of the Column object.
   * To support cached nodes, the column data will be
   * fetched in the constructor to assure that a valid
   * copy exists.
   *
   * @param statement Current Document
   * @param parent Parent row node of this column
   * @param columnIndex Index for this column
   * @param metadata Meta data (column header).
   * @param ResultSet {@link java.sql.ResultSet}
   */
  public Column(XStatement statement, Row parent, int columnIndex,
                ResultSetMetaData metadata, ResultSet resultSet)
  {

    super(statement);

    m_columnIndex = columnIndex;
    m_parent = parent;

    // Get the column data right away so it is not lost when
    // streamable mode is turned off.
    // JCG 4/1/2001
    m_text = new ColumnData(statement, this, resultSet);
  }

  /**
   * The parent of col is #Document (represented by XStatement).
   *
   * @return The parent of this column
   */
  public Document getOwnerDocument()
  {
    return this.getXStatement();
  }

  /**
   * Return "col".
   *
   * @return the deault column name "col".
   */
  public String getNodeName()
  {
    return XStatement.S_COLUMNNAME;
  }

  /**
   * Return the col text node (the column value).
   *
   * @return the column value
   * @throws Exception, Let all errors be handled by the XConnection
   */
  public Node getFirstChild()
    throws DOMException
  {

    if (DEBUG)
      System.out.println("In Column.getFirstChild");

    return m_text;
  }

  /**
   * Return the next col element for the current row.
   * @return a Column node or null if we are at the end
   * of the column array.
   *
   * The column array list is built and maintained in the
   * parent row object.
   *
   */
  public Node getNextSibling()
  {

    if (DEBUG)
      System.out.println("In Column.getNextSibling");

    int nextIndex = m_columnIndex + 1;

    return (nextIndex < m_parent.m_childCount)
           ? m_parent.m_columns[nextIndex] : null;
  }

  /**
   * The parent node of col is a row.
   *
   * @return The parent node of this column
   */
  public Node getParentNode()
  {

    if (DEBUG)
      System.out.println("In Column.getParentNode");

    return m_parent;
  }

  /**
   * Tell if there are any children of col,
   * which is always true. I.e., col contains a text node
   * with a textual representation of the column value.
   *
   * @return True
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In Column.hasChildNodes");

    return true;
  }

  /**
   * From the XConnection (the root of the variable), retrive
   * the ColumnAttributes object.
   *
   * @return the metadata for this column(column header).
   */
  public NamedNodeMap getAttributes()
  {
    return m_parent.m_parent.m_columnHeaders[m_columnIndex];
  }
}
