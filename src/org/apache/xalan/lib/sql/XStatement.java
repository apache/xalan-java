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

import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.Vector;
import java.util.Enumeration;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;

import java.lang.Integer;
import java.lang.Double;

import org.apache.xpath.axes.ContextNodeList;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * Represents a JDBC query statement. Also acts as both a
 * NodeIterator and the Document node for the row-set representation
 * of the query result set.
 */
public class XStatement extends StreamableNode
        implements NodeIterator, ContextNodeList, Cloneable
{

  /** Flag for DEBUG mode          */
  private static final boolean DEBUG = false; 

  /**
   * Ths JDBC Statement that is used for the query.
   * It is allocated as a prepared statement but may
   * only be use as a regular statemwnt.
   *
   */
  private Statement         m_statement;
  private PreparedStatement m_pstatement;

  /** Node counter          */
  private int m_nodeCounter = 0;

  /**
   * Get And Increment Node Counter
   *
   *
   * @return Node counter
   */
  int getAndIncrementNodeCounter()
  {

    int c = m_nodeCounter;

    m_nodeCounter++;

    return c;
  }

  /**
   * Get the JDBC Query statement
   *
   *
   * @return the JDBC Query statement
   */
  public Statement getStatement()
  {
    return m_statement;
  }

  /** ResultSet instance from executing the query string          */
  ResultSet m_resultSet;

  /**
   * Get the ResultSet from executing the query string
   *
   *
   * @return ResultSet instance
   */
  public ResultSet getResultSet()
  {
    return m_resultSet;
  }

  /** XConnection instance (for access to JDBC data).          */
  private XConnection m_xconnection;

  /** The SQL Query string         */
  private String m_queryString;

  /** Rowset instance (for the JDBC query result set).          */
  RowSet m_rowset;

  /** Flag          */
  boolean m_nextHasBeenCalled = false;

  /** Constant for DOCELEMENTNAME          */
  static final String S_DOCELEMENTNAME = "row-set";

  /** Constant for COLUMNHEADERNAME          */
  static final String S_COLUMNHEADERNAME = "column-header";

  /** Constant for ROWNAME          */
  static final String S_ROWNAME = "row";

  /** Constant for COLUMNNAME          */
  static final String S_COLUMNNAME = "col";

  /**
   * The {@link org.apache.xalan.lib.sql.XConnection#query(java.lang.String) XConnection query()}
   * method uses this constructor to execute a SQL query statement. When instantiated,
   * XStatement executes the query and creates a
   * {@link org.apache.xalan.lib.sql.RowSet RowSet}, a row-set element associated with the query
   * result set.
   * @param connection the XConnection object that calls this constructor.
   * @param queryString the SQL query.
   *
   *
   * @throws SQLException
   */
  public XStatement(XConnection connection, String queryString)
          throws SQLException
  {

    super(null);

    if (DEBUG)
      System.out.println("In XStatement constructor");

    // The SQL statement which lets us execute commands against the connection.
    m_xconnection = connection;

    m_statement = m_xconnection.m_connection.createStatement();

    m_queryString = queryString;

    m_resultSet = m_statement.executeQuery(m_queryString);
    m_rowset = new RowSet(this);

    if (DEBUG)
      System.out.println("Exiting XStatement constructor");
  }

  public XStatement(XConnection connection, String queryString, Vector pList)
          throws SQLException
  {

    super(null);

    if (DEBUG)
      System.out.println("In XStatement constructor for pquery");

    // The SQL statement which lets us execute commands against the connection.
    m_xconnection = connection;
    m_queryString = queryString;

    if (DEBUG)
    {
      System.out.println("Executing PQuery: " + m_queryString);
    }
    m_pstatement = m_xconnection.m_connection.prepareStatement(m_queryString);
    Enumeration enum = pList.elements();
    int indx = 1;
    while (enum.hasMoreElements())
    {
      QueryParameter param = (QueryParameter) enum.nextElement();
      setParameter(indx, m_pstatement, param);
      indx++;
    }

    m_resultSet = m_pstatement.executeQuery();
    m_rowset = new RowSet(this);

    //
    // Make a copy of the statement for external access
    m_statement = m_pstatement;

    if (DEBUG)
      System.out.println("Exiting XStatement constructor");
  }


  /**
   * Set the parameter for a Prepared Statement
   *
   */
  public void setParameter(int pos, PreparedStatement stmt, QueryParameter p)
    throws SQLException
  {
    String type = p.getType();
    if (type.equalsIgnoreCase("string"))
    {
      stmt.setString(pos, p.getValue());
    }

    if (type.equalsIgnoreCase("bigdecimal"))
    {
      stmt.setBigDecimal(pos, new BigDecimal(p.getValue()));
    }

    if (type.equalsIgnoreCase("boolean"))
    {
      Integer i = new Integer( p.getValue() );
      boolean b = ((i.intValue() != 0) ? false : true);
      stmt.setBoolean(pos, b);
    }

    if (type.equalsIgnoreCase("bytes"))
    {
      stmt.setBytes(pos, p.getValue().getBytes());
    }

    if (type.equalsIgnoreCase("date"))
    {
      stmt.setDate(pos, Date.valueOf(p.getValue()));
    }

    if (type.equalsIgnoreCase("double"))
    {
      Double d = new Double(p.getValue());
      stmt.setDouble(pos, d.doubleValue() );
    }

    if (type.equalsIgnoreCase("float"))
    {
      Float f = new Float(p.getValue());
      stmt.setFloat(pos, f.floatValue());
    }

    if (type.equalsIgnoreCase("long"))
    {
      Long l = new Long(p.getValue());
      stmt.setLong(pos, l.longValue());
    }

    if (type.equalsIgnoreCase("short"))
    {
      Short s = new Short(p.getValue());
      stmt.setShort(pos, s.shortValue());
    }

    if (type.equalsIgnoreCase("time"))
    {
      stmt.setTime(pos, Time.valueOf(p.getValue()) );
    }

    if (type.equalsIgnoreCase("timestamp"))
    {

      stmt.setTimestamp(pos, Timestamp.valueOf(p.getValue()) );
    }

  }


  /**
   * Get the representation of the JDBC Query statement
   *
   *
   * @return the representation of the JDBC Query statement, this
   */
  public XStatement getXStatement()
  {
    return this;
  }

  // ============== NodeIterator interface =============

  /**
   *  The XStatement object is the NodeIterator root.
   * @return itself.
   */
  public Node getRoot()
  {

    if (DEBUG)
      System.out.println("In XStatement.getRoot");

    return this;
  }

  /**
   *  This attribute determines which node types are presented via the
   * iterator. The available set of constants is defined in the
   * <code>NodeFilter</code> interface.
   *
   * @return which node types are to be presented
   */
  public int getWhatToShow()
  {

    if (DEBUG)
      System.out.println("In XStatement.getWhatToShow");

    // TODO: ??
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   *  The filter used to screen nodes.
   * @return null.
   */
  public NodeFilter getFilter()
  {

    if (DEBUG)
      System.out.println("In XStatement.getFilter");

    return null;
  }

  /**
   *  The value of this flag determines whether the children of entity
   * reference nodes are visible to the iterator. If false, they will be
   * skipped over.
   * <br> To produce a view of the document that has entity references
   * expanded and does not expose the entity reference node itself, use the
   * whatToShow flags to hide the entity reference node and set
   * expandEntityReferences to true when creating the iterator. To produce
   * a view of the document that has entity reference nodes but no entity
   * expansion, use the whatToShow flags to show the entity reference node
   * and set expandEntityReferences to false.
   * @return true.
   */
  public boolean getExpandEntityReferences()
  {

    if (DEBUG)
      System.out.println("In XStatement.getExpandEntityReferences");

    return true;
  }

  /**
   * Return the #Document node (one role the XStatement plays) the first time called;
   * return null thereafter.
   * @return this or null.
   *
   * @throws DOMException
   */
  public Node nextNode() throws DOMException
  {

    if (DEBUG)
      System.out.println("In XStatement.nextNode");

    if (!m_nextHasBeenCalled)
    {
      m_nextHasBeenCalled = true;

      return this;
    }
    else
      return null;
  }

  /**
   * Throw an exception, since streaming nodes and iterators can not
   * go backwards.
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node previousNode() throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getParentNode not supported!");

    return null;
  }

  /**
   *  Detaches the iterator from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state. After<code>detach</code> has been invoked, calls to
   * <code>nextNode</code> or<code>previousNode</code> will raise the
   * exception INVALID_STATE_ERR.
   */
  public void detach()
  {

    if (DEBUG)
      System.out.println("XStatement.detach");

    try
    {
      m_statement.close();

      m_statement = null;
      m_resultSet = null;
    }
    catch (SQLException sqle)
    {

      // diagnostics?
    }
  }

  /**
   * Return the String value of this object
   *
   *
   * @return String value of the JDBC query string
   */
  public String toString()
  {
    return "XStatement: " + m_queryString;
  }

  // ===== Document implementation ====

  /**
   * The parent of a document is null.
   *
   * @return null
   */
  public Document getOwnerDocument()
  {
    return null;
  }

  /**
   * Return node type: Node.DOCUMENT_NODE.
   *
   * @return Node.DOCUMENT_NODE.
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }

  /**
   * Return node name: "#Document".
   *
   * @return "#Document".
   */
  public String getNodeName()
  {
    return "#Document";
  }

  /**
   * Return the row-set node.
   *
   * @return the row-set node or null if not found.
   */
  public Node getFirstChild()
  {

    if (DEBUG)
      System.out.println("In XStatement.getFirstChild: "
                         + this.getNodeTest());

    try
    {
      if ((this.getNodeTest().getNamespace() == null)
              && (this.getNodeTest().getLocalName().equals(S_DOCELEMENTNAME)))
        return m_rowset;
      else
        return null;
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();

      throw e;
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
      System.out.println("In XStatement.getNextSibling");

    return null;
  }

  /**
   * The parent node of document is always null.
   *
   * @return null
   */
  public Node getParentNode()
  {

    if (DEBUG)
      System.out.println("In XStatement.getParentNode");

    return null;
  }

  /**
   * Tell if there are any children of the document,
   * which is always true.
   *
   * @return true
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In XStatement.hasChildNodes");

    return true;
  }

  // ===== ContextNodeList implementation =====

  /**
   * The current node is the RowSet
   *
   *
   * @return The row-set
   */
  public Node getCurrentNode()
  {
    return this.m_rowset;
  }

  /**
   * Get Current Position
   *
   *
   * @return 0
   */
  public int getCurrentPos()
  {
    return 0;  // Not totally sure...
  }

  /**
   * Reset this object
   *
   */
  public void reset()
  {
    this.m_nextHasBeenCalled = false;
  }

  /**
   * Set whether nodes should be cached - not implemented
   *
   *
   * @param b Flag indicating whether nodes should be cached
   */
  public void setShouldCacheNodes(boolean b)
  {

    // Set streamable?
  }

  /**
   * Not implemented
   *
   *
   * @param index
   */
  public void runTo(int index)
  {

    // Throw exception if not streamable!
  }

  /**
   * Not implemented
   *
   *
   * @param i
   */
  public void setCurrentPos(int i)
  {

    // Throw exception if not streamable!
  }

  /**
   * Return size
   *
   *
   * @return 1
   */
  public int size()
  {
    return 1;
  }

  /**
   * Return whether this iterator is fresh
   *
   *
   * @return True if this has not been called
   */
  public boolean isFresh()
  {
    return (!this.m_nextHasBeenCalled);
  }

  /**
   * Overide cloneWithReset method
   *
   *
   * @return A clone of this which has been reset
   *
   * @throws CloneNotSupportedException
   */
  public NodeIterator cloneWithReset() throws CloneNotSupportedException
  {

    XStatement clone = (XStatement) super.clone();

    clone.reset();

    return clone;
  }

  /**
   * Clone this object
   *
   *
   * @return A clone of this object
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    XStatement clone = (XStatement) super.clone();

    return clone;
  }

  /** Index of Last node found by this iterator   */
  private int m_last = 0;

  /**
   * Get index of the last found node
   *
   *
   * @return index of last found node
   */
  public int getLast()
  {
    return m_last;
  }

  /**
   * Set the index of the last found node
   *
   *
   * @aram index of last found node
   */
  public void setLast(int last)
  {
    m_last = last;
  }
}
