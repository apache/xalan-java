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

  /** NEEDSDOC Field m_parent          */
  Column m_parent;

  /** NEEDSDOC Field DEBUG          */
  private static final boolean DEBUG = false;

  /**
   * Constructor ColumnData
   *
   *
   * NEEDSDOC @param statement
   * NEEDSDOC @param parent
   */
  public ColumnData(XStatement statement, Column parent)
  {

    super(statement);

    m_parent = parent;
  }

  /**
   * Return Node.TEXT_NODE.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.TEXT_NODE;
  }

  /**
   * NEEDSDOC Method splitText 
   *
   *
   * NEEDSDOC @param offset
   *
   * NEEDSDOC (splitText) @return
   *
   * @throws DOMException
   */
  public Text splitText(int offset) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Return the value for this col element text node. I.e., return a String representation
   * of the data for this column in the current row.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public String getData() throws DOMException
  {

    try
    {
      ResultSet rs = this.getXStatement().getResultSet();
      int columnIndex = m_parent.m_columnIndex;

      if (DEBUG)
        System.out.println("In ColumnData.getData, columnIndex: "
                           + columnIndex);

      if (columnIndex < m_parent.m_parent.m_childCount)
      {
        return rs.getString(columnIndex + 1);
      }
      else
        return null;
    }
    catch (SQLException sqle)
    {
      return null;
    }
  }

  /**
   * Return the value for this col element text node. I.e., return a String representation
   * of the data for this column in the current row.
   * Calls @link #getNodeValue() getNodeValue()}.
   *
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC ($objectName$) @return
   */
  public int getLength()
  {

    String s = getData();

    return (null != s) ? s.length() : 0;
  }

  /**
   * Not supported.
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param count
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws DOMException
   */
  public String substringData(int offset, int count) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Not supported.
   *
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void appendData(String arg) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Not supported.
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void insertData(int offset, String arg) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Not supported.
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param count
   *
   * @throws DOMException
   */
  public void deleteData(int offset, int count) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Not supported.
   *
   * NEEDSDOC @param offset
   * NEEDSDOC @param count
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void replaceData(int offset, int count, String arg)
          throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Not supported.
   *
   * NEEDSDOC @param data
   *
   * @throws DOMException
   */
  public void setData(String data) throws DOMException
  {

    // TODO: It would be cool to make this callable, to set 
    // a value in the database.
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * The owner of a col text ode is the #Document (represented by XStatement).
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Document getOwnerDocument()
  {
    return this.getXStatement();
  }

  /**
   * Return "#Text".
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return "#Text";
  }

  /**
   * Return null.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In ColumnData.getNextSibling");

    return null;
  }

  /**
   * This always returns null.
   *
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC ($objectName$) @return
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In ColumnData.hasChildNodes");

    return false;
  }
}
