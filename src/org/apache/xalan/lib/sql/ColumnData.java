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
import org.w3c.dom.Text;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.xalan.res.XSLTErrorResources;

/**
  * Represents the col element text node, i.e., the column value.
 */
public class ColumnData extends StreamableNode implements Text
{

  /** The column for which this is the data          */
  Column  m_parent;

  /**
   * The value of this Column, this value is set inside the
   * constructor
   */
  String  m_Data;


  /** Flag for debug mode         */
  private static final boolean DEBUG = false;

  /**
   * Constructor ColumnData
   *
   *
   * @param statement Owning document
   * @param parent Owning column
   * @param ResultSet {@link java.sql.ResultSet}
   * @throws SQLException, make the Column Class deal with the problem
   *
   */
  public ColumnData(XStatement statement, Column parent, ResultSet resultSet)
  {

    super(statement);
    m_parent = parent;

    try
    {
      int columnIndex = m_parent.m_columnIndex;

      if (DEBUG)
          System.out.println("In ColumnData.constructor, columnIndex: "
                           + columnIndex);


      if (columnIndex < m_parent.m_parent.m_childCount)
      {
        m_Data = resultSet.getString(columnIndex + 1);
      }
    }
    catch(SQLException e)
    {
      m_Data = null;
    }

    // Always make the column Data equal a valid value
    if (null == m_Data) m_Data = "";
  }

  /**
   * Return node type, Node.TEXT_NODE.
   *
   * @return Node.TEXT_NODE.
   */
  public short getNodeType()
  {
    return Node.TEXT_NODE;
  }


  /**
   * Return the value for this col element text node.
   * I.e., return a String representation
   * of the data for this column in the current row.
   *
   * @return the data for this column
   *
   */
  public String getData()
  {
    return m_Data;
  }

  /**
   * Return the value for this col element text node.
   * I.e., return a String representation
   * of the data for this column in the current row.
   * Calls @link #getNodeValue() getNodeValue()}.
   *
   * @return the value for this column
   *
   * @throws DOMException
   */
  public String getNodeValue() throws DOMException
  {
    return getData();
  }

  /**
   *  The number of  16-bit units that are available through
   * <code>data</code> and the <code>substringData</code> method below.
   * This may have the value zero, i.e., <code>CharacterData</code> nodes
   * may be empty.
   *
   * @return Number of characters in data
   */

  public int getLength()
  {
    return m_Data.length();
  }

  /**
   *
   * substringData
   *
   * @param offset Starting offset of substring
   * @param count Number of characters in substring
   *
   * @return String
   *
   */
  public String substringData(int offset, int count)
  {
    return m_Data.substring(offset, offset+count);
  }

  /**
   * Append Data to the Column Data
   *
   * @param arg String data to append
   *
   */
  public void appendData(String arg)
  {
    StringBuffer s = new StringBuffer(m_Data);
    s.append(arg);
    m_Data = s.toString();
  }

  /**
   *
   * Insert data into the Column Data
   *
   * @param offset
   * @param arg
   *
   */
  public void insertData(int offset, String arg)
  {
    StringBuffer s = new StringBuffer(m_Data);
    s.insert(offset, arg.getBytes());
    m_Data = s.toString();
  }

  /**
   * Delete Content from the Column Data
   *
   * @param offset
   * @param count
   *
   */
  public void deleteData(int offset, int count)
  {
  }

  /**
   * @param offset
   * @param count
   * @param arg
   *
   */
  public void replaceData(int start, int rSize, String str)
  {
  }

  /**
   * @param data
   *
   */
  public void setData(String data)
  {
    m_Data = data;
  }

  /**
   * The owner of a col text node is the #Document (represented by XStatement).
   *
   * @return The owning document
   */
  public Document getOwnerDocument()
  {
    return this.getXStatement();
  }

  /**
   * Return node name, "#Text".
   *
   * @return "#Text".
   */
  public String getNodeName()
  {
    return "#Text";
  }

  /**
   * Return First child. This always returns null.
   *
   * @return null
   */
  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In ColumnData.getNextSibling");

    return null;
  }

  /**
   * Return next sibling. This always returns null.
   *
   * @return null
   */
  public Node getNextSibling()
  {

    if (DEBUG)
      System.out.println("In ColumnData.getNextSibling");

    return null;
  }

  /**
   * The parent node of the col text node is the col node.
   *
   * @return The parent node i.e the column node
   */
  public Node getParentNode()
  {

    if (DEBUG)
      System.out.println("In ColumnData.getParentNode");

    return m_parent;
  }

  /**
   * Tell if there are any children of the col node,
   * which is always false.
   *
   * @return false
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In ColumnData.hasChildNodes");

    return false;
  }
}
