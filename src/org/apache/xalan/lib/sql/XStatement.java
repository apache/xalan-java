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
import java.sql.SQLException;
import java.sql.ResultSet;

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

  /** NEEDSDOC Field DEBUG          */
  private static final boolean DEBUG = false;

  /** NEEDSDOC Field m_statement          */
  private Statement m_statement;

  /** NEEDSDOC Field m_nodeCounter          */
  private int m_nodeCounter = 0;

  /**
   * NEEDSDOC Method getAndIncrementNodeCounter 
   *
   *
   * NEEDSDOC (getAndIncrementNodeCounter) @return
   */
  int getAndIncrementNodeCounter()
  {

    int c = m_nodeCounter;

    m_nodeCounter++;

    return c;
  }

  /**
   * NEEDSDOC Method getStatement 
   *
   *
   * NEEDSDOC (getStatement) @return
   */
  public Statement getStatement()
  {
    return m_statement;
  }

  /** NEEDSDOC Field m_resultSet          */
  ResultSet m_resultSet;

  /**
   * NEEDSDOC Method getResultSet 
   *
   *
   * NEEDSDOC (getResultSet) @return
   */
  public ResultSet getResultSet()
  {
    return m_resultSet;
  }

  /** NEEDSDOC Field m_xconnection          */
  private XConnection m_xconnection;

  /** NEEDSDOC Field m_queryString          */
  private String m_queryString;

  /** NEEDSDOC Field m_rowset          */
  RowSet m_rowset;

  /** NEEDSDOC Field m_nextHasBeenCalled          */
  boolean m_nextHasBeenCalled = false;

  /** NEEDSDOC Field S_DOCELEMENTNAME          */
  static final String S_DOCELEMENTNAME = "row-set";

  /** NEEDSDOC Field S_COLUMNHEADERNAME          */
  static final String S_COLUMNHEADERNAME = "column-header";

  /** NEEDSDOC Field S_ROWNAME          */
  static final String S_ROWNAME = "row";

  /** NEEDSDOC Field S_COLUMNNAME          */
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

  /**
   * NEEDSDOC Method getXStatement 
   *
   *
   * NEEDSDOC (getXStatement) @return
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
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC Method toString 
   *
   *
   * NEEDSDOC (toString) @return
   */
  public String toString()
  {
    return "XStatement: " + m_queryString;
  }

  // ===== Document implementation ====

  /**
   * The parent of a document is null.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Document getOwnerDocument()
  {
    return null;
  }

  /**
   * Return Node.DOCUMENT_NODE.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }

  /**
   * Return "#Document".
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return "#Document";
  }

  /**
   * Return the row-set node.
   *
   * NEEDSDOC ($objectName$) @return
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
   * This always returns null.
   *
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC ($objectName$) @return
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
   * NEEDSDOC ($objectName$) @return
   */
  public boolean hasChildNodes()
  {

    if (DEBUG)
      System.out.println("In XStatement.hasChildNodes");

    return true;
  }

  // ===== ContextNodeList implementation =====

  /**
   * NEEDSDOC Method getCurrentNode 
   *
   *
   * NEEDSDOC (getCurrentNode) @return
   */
  public Node getCurrentNode()
  {
    return this.m_rowset;
  }

  /**
   * NEEDSDOC Method getCurrentPos 
   *
   *
   * NEEDSDOC (getCurrentPos) @return
   */
  public int getCurrentPos()
  {
    return 0;  // Not totally sure...
  }

  /**
   * NEEDSDOC Method reset 
   *
   */
  public void reset()
  {
    this.m_nextHasBeenCalled = false;
  }

  /**
   * NEEDSDOC Method setShouldCacheNodes 
   *
   *
   * NEEDSDOC @param b
   */
  public void setShouldCacheNodes(boolean b)
  {

    // Set streamable?
  }

  /**
   * NEEDSDOC Method runTo 
   *
   *
   * NEEDSDOC @param index
   */
  public void runTo(int index)
  {

    // Throw exception if not streamable!
  }

  /**
   * NEEDSDOC Method setCurrentPos 
   *
   *
   * NEEDSDOC @param i
   */
  public void setCurrentPos(int i)
  {

    // Throw exception if not streamable!
  }

  /**
   * NEEDSDOC Method size 
   *
   *
   * NEEDSDOC (size) @return
   */
  public int size()
  {
    return 1;
  }

  /**
   * NEEDSDOC Method isFresh 
   *
   *
   * NEEDSDOC (isFresh) @return
   */
  public boolean isFresh()
  {
    return (!this.m_nextHasBeenCalled);
  }

  /**
   * NEEDSDOC Method cloneWithReset 
   *
   *
   * NEEDSDOC (cloneWithReset) @return
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
   * NEEDSDOC Method clone 
   *
   *
   * NEEDSDOC (clone) @return
   *
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException
  {

    XStatement clone = (XStatement) super.clone();

    return clone;
  }
}
