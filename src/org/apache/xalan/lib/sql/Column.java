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
 * <meta name="usage" content="experimental"/>
 * Represents a col node from a row node.
 */
public class Column extends StreamableNode
{

  /** column Index          */
  int m_columnIndex;

  /** Parent row node          */
  Row m_parent;

  /** Flag indicating if in DEBUG mode         */
  private static final boolean DEBUG = false;

  /** Column data          */
  ColumnData m_text;

  /**
   * Constructor Column
   *
   *
   * @param statement Current Document
   * @param parent Parent row node of this column
   * @param columnIndex Index for this column
   * @param metadata Meta data (column header).
   */
  public Column(XStatement statement, Row parent, int columnIndex,
                ResultSetMetaData metadata)
  {

    super(statement);

    m_columnIndex = columnIndex;
    m_parent = parent;
    m_text = null;
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
   */
  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In Column.getFirstChild");

    if (null == m_text)
      m_text = new ColumnData(this.getXStatement(), this);

    return m_text;
  }

  /**
   * Return the next col element for the current row.
   * @return a Column node or null.
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
   * Return the metadata for this column.
   *
   * @return the metadata for this column(column header).
   */
  public NamedNodeMap getAttributes()
  {
    return m_parent.m_parent.m_columnHeaders[m_columnIndex];
  }
}
