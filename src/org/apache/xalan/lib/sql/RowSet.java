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

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.apache.xpath.patterns.NodeTest;

/**
 * <p>
 * The row-set is the controlling element in the Document that determines
 * if the JDB ResultSet is being traversed or if the ResultSet MetaData
 * is being interogated.
 * <p>
 *
 * <pre>
 * <p>
 * The DTD for the Document is a follows.
 * &lt;row-set&gt;
 *    &lt;column-header {@link org.apache.xalan.lib.sql.ColumnAttributes} &gt;
 *    .
 *    . One for each column in the Query
 *    .
 *    &lt;column-header /&gt;
 *    &lt;row&gt;
 *    .
 *    . One for each Row in the query
 *    .
 *      &lt;col {@link ColumnAttribute} &gt; The Data for the column &lt;/col&gt;
 *      .
 *      .
 *      . One for each column in the Row
 *      .
 *      .
 *    &lt;/row&gt;
 * &lt;row-set&gt;
 * </p>
 * </pre>
 */

public class RowSet extends StreamableNode
{
  /** Let's keep track of the branch we are on */
  private static final int  ROWSET_POS_NONE = 0;
  private static final int  ROWSET_POS_COLHDR = 1;
  private static final int  ROWSET_POS_ROW = 2;
  private int               m_RowSetPos;

  /** Array of column headers in this row-set          */
  ColumnHeader[] m_columnHeaders = null;

  /** First row in this row-set          */
  Row m_firstrow;

  /** Flag for debug mode         */
  private static final boolean DEBUG = false;

  /**
   * Constructor RowSet
   *
   *
   * @param statement Owning document
   */
  public RowSet(XStatement statement)
  {
    super(statement);

    m_RowSetPos = ROWSET_POS_NONE;

    // Make sure we always populate the column headers
    // If we do not iterate over the COlumn Headers, and ask
    // for a column attribute, we will fail. So we need to
    // populate this array now.
    // Changed 3/15/01 JCG
    try
    {
      if (null == m_columnHeaders)
      {
        ResultSetMetaData metaData = statement.m_resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // If we have actuall requested a set of columns and we
        // have not been here before, then get all the column
        // headers to build our list of attributes
        if (columnCount > 0)
        {
          m_columnHeaders = new ColumnHeader[columnCount];

          // Populate the column Header Array
          for (int x =0; x< columnCount; x++)
          {
            m_columnHeaders[x] =
              new ColumnHeader(getXStatement(), this, x,  metaData);
          }
        }
      }
    }
    catch(SQLException e)
    {
      m_columnHeaders = null;
    }
  }

  // ===== Element implementation =====

  /**
   * Return node name: "row-set".
   *
   * @return "row-set".
   */
  public String getNodeName()
  {
    return XStatement.S_DOCELEMENTNAME;
  }

  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In RowSet.getFirstChild");

    NodeTest nt = this.getNodeTest();

      // If we are asking for the Column Header branch of the Node Tree
      // or if we are asking for any branch (nt == null) then we will
      // return the First Column Header Node
    if ((null == nt) ||
      nt.getLocalName().equals(XStatement.S_COLUMNHEADERNAME))
    {
      m_RowSetPos = ROWSET_POS_COLHDR;
      return getFirstColHdr();
    }
    //
    // If we ask for the Row Branch directly then return the
    // first row. If we have been here before and we are in
    // streamable mode then return null, we can re-traverse
    // the Result Set in Streamable Mode.
    //
    // Suppporting JDBC scrollable cursors may change that
    // though.
    //
    else if (nt.getLocalName().equals(XStatement.S_ROWNAME))
    {
      m_RowSetPos = ROWSET_POS_ROW;
      return getFirstRow();
    }

    return null;
  }

  private Node getFirstColHdr()
  {
    if (null != m_columnHeaders) return m_columnHeaders[0];
    else return null;
  }

  private Node getFirstRow()
  {
    XStatement statement = getXStatement();

    try
    {
      if (null == m_firstrow)
      {
        // If there was no data, then return null for the query
        ResultSet resultSet = getXStatement().getResultSet();
        if (resultSet.next())
        {
          m_firstrow = new Row(getXStatement(), this, null);
        }
        else
        {
          m_firstrow = null;
        }
      }
      else
      {
        // We have been here before, then don't allow
        // use to re-traverse the query.
        //
        // We could just re-issue the query ??
        if (statement.getShouldCacheNodes() == false)
        {
          // Streaming is off, so let's prevent another walk through
          return null;
        }
      }
    }
    catch(SQLException e)
    {
      // Something went wrong, just return null
      return null;
    }

    return m_firstrow;
  }

  /**
   * getNextSibling
   *
   * @return null
   */
  public Node getNextSibling()
  {

    if (DEBUG)
      System.out.println("In RowSet.getNextSibling");

    switch (m_RowSetPos)
    {
      case ROWSET_POS_NONE:
        return getFirstColHdr();
      case ROWSET_POS_COLHDR:
        return getFirstRow();
      default:
        return null;
    }
  }

  /**
   * The parent node of row-set is #Document (represented by XStatement).
   *
   * @return Owner document
   */
  public Node getParentNode()
  {

    if (DEBUG)
      System.out.println("In RowSet.getParentNode");

    return getXStatement();
  }

  /**
   * Tell if there are any children of the document,
   * which is always true.
   *
   * @return True
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In RowSet.hasChildNodes");

    return true;
  }
}
