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

/**
 * <meta name="usage" content="experimental"/>
 * This class represents the row-set StreamableNode, a "streamable" holder
 * for the JDBC query result set.
 */
public class RowSet extends StreamableNode
{

  /** Array of column headers in this row-set          */
  ColumnHeader[] m_columnHeaders;

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

  /**
   * The first time the client asks for a column-header element, instantiate an array of ColumnHeaders
   * (1 per column), and return the ColumnHeader for the first row.
   * @return ColumnHeader Node for first row or null.
   */
  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In RowSet.getFirstChild");

    try
    {
      org.apache.xpath.patterns.NodeTest nt = this.getNodeTest();
      if ((null == nt) || nt.getNamespace() == null)
      {
        if ((null == nt) || nt.getLocalName().equals(
                XStatement.S_COLUMNHEADERNAME))
        {
          if (null == m_columnHeaders)
          {
            ResultSetMetaData metaData =
              getXStatement().m_resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (columnCount > 0)
            {
              m_columnHeaders = new ColumnHeader[columnCount];
              m_columnHeaders[0] = new ColumnHeader(getXStatement(), this, 0,
                                                    metaData);

              return m_columnHeaders[0];
            }
            else
              return null;
          }
          else
            return m_columnHeaders[0];
        }
        else if (nt.getLocalName().equals(
                XStatement.S_ROWNAME))
        {
          if (null == m_firstrow)
          {
            m_firstrow = new Row(getXStatement(), this);
          }

          return m_firstrow;
        }
        else
          return null;
      }
      else
        return null;
    }
    catch (SQLException sqle)
    {

      // diagnostics?
      return null;
    }
  }


  /**
   * getNextSibling - This always returns null.
   *
   * @return null
   */
  public Node getNextSibling()
  {

    if (DEBUG)
      System.out.println("In RowSet.getNextSibling");

    return null;
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

    return this.getXStatement();
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
