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


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.xml.utils.*;

/**
 * <p>
 * This class represents a row from a query result set.
 * The row object can be accessed in one of two modes.
 * Streaming or cached.
 * </p>
 *
 * <p>
 * In <b>streaming mode</b>, the same Row object is used to represent sequential
 * database rows. Streaming mode only supports traversing the Document once.
 * </p>
 * <p>
 * In <b>cached mode</b>, as the Document is traversed a new Row objec is created
 * for each database row and a full Document is built. Cached mode allows
 * the Document to be traversed many times.
 * </p>
 * <p>
 * If you are only traversing the Document once transfering the contents
 * to the result tree, the streaming mode is the better option. The
 * memory footprint is much less. Also streaming mode may be required
 * for database queries that return a large number of rows because the memory
 * footprint is limited to one row.
 * </p>
 * <p>
 * Caching is controlled by two methods on the XConnection object called
 * enableCacheNodes() or disableCacheNodes().
 * </p>
 *
 */
public class Row extends StreamableNode
{

  /** Number of children for this row         */
  int m_childCount;

  /** Number of columns in this row         */
  Column[] m_columns;

  /** Meta data           */
  ResultSetMetaData m_metadata;

  /** Next row          */
  Row m_nextNode = null;  // normally null, if streamable.

  /** Previous Node */
  Row m_prevNode = null;  // normally null, if streamable.

  /** Flag for DEBUG mode        */
  private static final boolean DEBUG = false;

  /** Parent node, a row-set          */
  RowSet m_parent;

  /**
   * Constructor Row
   *
   *
   * @param statement Owning document
   * @param parent parent node, a row-set
   * @param prev, the previous node in the row-set
   *
   */
  public Row(XStatement statement, RowSet parent, Row prev)
  {
    super(statement);

    if (DEBUG) System.out.println("In Row.constructor");

    m_parent = parent;
    m_prevNode = prev;

    populateColumnData();

  }

  // ===== Element implementation =====

  /**
   * The parent of a row is #Document represented by XStatement).
   *
   * @return The owning document
   */
  public Document getOwnerDocument()
  {
    return this.getXStatement();
  }

  /**
   * Return node name: "row".
   *
   * @return "row".
   */
  public String getNodeName()
  {
    return XStatement.S_ROWNAME;
  }

  /**
   * Return the first col element for the current row.
   *
   * @return the first col element for the current row or
   * null if none
   */
  public Node getFirstChild()
  {
    if (DEBUG) System.out.println("In Row.getFirstChild");

    if (hasChildNodes()) return m_columns[0];
    else return null;
  }

  /**
   * Return next row in the row-set.
   *
   * Use the same Row object over and over if the row-set is streamable.
   *
   * @return next row in the row-set or null if none
   * @throws Exception, Force the XConnection to handle all errors
   */
  public Node getNextSibling()
    throws DOMException
  {

    if (DEBUG)
    {
      System.out.println("In Row.getNextSibling");
    }

    try
    {
      XStatement xstatement = this.getXStatement();
      ResultSet resultSet = xstatement.getResultSet();

      if (xstatement.getShouldCacheNodes() == false)
      {
        if (resultSet.next())
        {

          // Freshen our place in line
          incermentOrderIndex();
          // Get new data, even though we are streamable
          // we need to be up-to-date
          populateColumnData();

          return this;

        }
        else
        {
          // We are at the end of the streamable line
         return null;
        }
      }
      else
      {
        // We are not in streamable mode any more, if we have already
        // been here, then lets just return what we already have
        if (null == m_nextNode)
        {
          if (resultSet.next())
          {
            m_nextNode = new Row(getXStatement(), m_parent, this);
          }
        }

        if (DEBUG) System.out.println(": m_next: " + m_nextNode);
        return m_nextNode;
      }
    }
    catch(SQLException e)
    {
      throw new DOMException(DOMException.SYNTAX_ERR, e.getLocalizedMessage());
    }
  }

  /**
   * Allow us to walk back up the tree
   */
  public Node getPreviousSibling()
  {
    return m_prevNode;
  }

  /**
   * Return the RowSet parent.
   *
   * @return The parent node (RowSet) of this row.
   */
  public Node getParentNode()
  {

    if (DEBUG)
      System.out.println("In Row.getParentNode");

    return m_parent;
  }

  /**
   * Tell if the row Node has any children (col Nodes),
   * which should always be true.
   *
   * @return True if the row has any children
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In Row.hasChildNodes " + m_childCount);

    return (m_childCount > 0);
  }


  private void populateColumnData()
  {
    try
    {
      XStatement xstatement = getXStatement();
      ResultSet resultSet = xstatement.getResultSet();

      ResultSetMetaData metadata = resultSet.getMetaData();
      m_metadata = metadata;

      m_childCount = metadata.getColumnCount();
      m_columns = new Column[m_childCount];

      // Use the same columns array, from Row to Row the columns
      // count should not change.

      for (int i = 0; i < m_childCount; i++)
      {
        m_columns[i] = new Column(xstatement, this, i, metadata, resultSet);
      }
    }
    catch(SQLException e)
    {
      if (DEBUG) System.out.println("Error Populating Column Data");
    }
  }

}
