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

import org.apache.xml.utils.UnImplNode;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * <meta name="usage" content="experimental"/>
 * This class represents a row from a query result set.  It is used
 * over and over, and so is certainly not fully DOM complient,
 * and will result in strange results in the stylesheet if the
 * user is not carefull.
 */
public class Row extends StreamableNode
{

  /** Number of children for this row         */
  int m_childCount;

  /** Number of columns in this row         */
  Column[] m_columns;

  /** Meta data           */
  ResultSetMetaData m_metadata;

  /** Flag for whether this is streamable           */
  boolean m_isStreamable = false;

  /** Next row          */
  Row m_next;  // normally null, if streamable.

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
   */
  public Row(XStatement statement, RowSet parent)
  {

    super(statement);

    try
    {
      m_parent = parent;

      XStatement xstatement = this.getXStatement();
      ResultSet resultSet = xstatement.getResultSet();
      ResultSetMetaData metadata = resultSet.getMetaData();

      m_metadata = metadata;
      m_childCount = metadata.getColumnCount();
      m_columns = new Column[m_childCount];

      for (int i = 0; i < m_childCount; i++)
      {
        m_columns[i] = new Column(xstatement, this, i, metadata);
      }
    }
    catch (SQLException sqle)
    {

      // diagnostics?
    }
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

    if (DEBUG)
      System.out.println("In Row.getFirstChild");

    if (this.hasChildNodes())
      return m_columns[0];
    else
      return null;
  }

  /**
   * Return next row in the row-set. Use the same Row object over and over
   * if the row-set is streamable.
   *
   * @return next row in the row-set or null if none
   */
  public Node getNextSibling()
  {

    if (DEBUG)
    {
      System.out.print("In Row.getNextSibling");
      System.out.flush();
    }

    XStatement xstatement = this.getXStatement();
    ResultSet resultSet = xstatement.getResultSet();

    try
    {
      if (m_isStreamable)
      {
        if (resultSet.next())
          return this;
        else
          return null;
      }
      else
      {
        if (null == m_next)
        {
          try
          {
            if (resultSet.next())
              m_next = new Row(getXStatement(), m_parent);
          }
          catch (SQLException sqle)
          {

            // Diagnostics?
          }
        }

        if (DEBUG)
        {
          System.out.println(": m_next: " + m_next);
          System.out.flush();

          // Exception e = new Exception();
          // e.printStackTrace();
        }

        return m_next;
      }
    }
    catch (SQLException sqle)
    {

      // Diagnostics?
      return null;
    }
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
      System.out.println("In Row.hasChildNodes");

    return (m_childCount > 0);
  }
}
