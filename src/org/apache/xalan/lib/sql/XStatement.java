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
  private static final boolean DEBUG = false;
  private Statement m_statement;
  
  private int m_nodeCounter = 0;
  
  int getAndIncrementNodeCounter()
  {
    int c = m_nodeCounter;
    m_nodeCounter++;
    return c;
  }
  
  public Statement getStatement()
  {
    return m_statement;
  }
  
  ResultSet m_resultSet;
  
  public ResultSet getResultSet()
  {
    return m_resultSet;
  }
  
  private XConnection m_xconnection;
  private String m_queryString; 
  RowSet m_rowset;
  boolean m_nextHasBeenCalled = false;
  
  static final String S_DOCELEMENTNAME = "row-set";
  static final String S_COLUMNHEADERNAME = "column-header";
  static final String S_ROWNAME = "row";
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
  */
  public XStatement(XConnection connection, String queryString)
    throws SQLException
  {
    super(null);
    if(DEBUG)
      System.out.println("In XStatement constructor");
    // The SQL statement which lets us execute commands against the connection.
    m_xconnection = connection;
    m_statement = m_xconnection.m_connection.createStatement();
    m_queryString = queryString;
    m_resultSet = m_statement.executeQuery(m_queryString);
    m_rowset = new RowSet(this);

    if(DEBUG)
      System.out.println("Exiting XStatement constructor");
  }
  
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
    if(DEBUG)
      System.out.println("In XStatement.getRoot");
    return this;
  }

  /**
   *  This attribute determines which node types are presented via the 
   * iterator. The available set of constants is defined in the 
   * <code>NodeFilter</code> interface.
   */
  public int getWhatToShow()
  {
    if(DEBUG)
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
    if(DEBUG)
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
    if(DEBUG)
      System.out.println("In XStatement.getExpandEntityReferences");
    return true;
  }
    
  /**
   * Return the #Document node (one role the XStatement plays) the first time called; 
   * return null thereafter.
   * @return this or null.
   */
  public Node nextNode()
    throws DOMException
  {
    if(DEBUG)
      System.out.println("In XStatement.nextNode");
    if(!m_nextHasBeenCalled)
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
   */
  public Node previousNode()
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"getParentNode not supported!");
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
    if(DEBUG)
      System.out.println("XStatement.detach");
    try
    {
      m_statement.close();
      m_statement = null;
      m_resultSet = null;
    }
    catch(SQLException sqle)
    { 
      // diagnostics?
    }
  }
  
  public String toString()
  {
    return "XStatement: "+m_queryString;
  }


  // ===== Document implementation ====
  
  /**
   * The parent of a document is null.
   */
  public Document getOwnerDocument()
  {
    return null;
  }

  /**
   * Return Node.DOCUMENT_NODE.
   */
  public short getNodeType()
  {
    return Node.DOCUMENT_NODE;
  }
  
  /**
   * Return "#Document".
   */
  public String getNodeName()
  {
    return "#Document";
  }
  
  /**
   * Return the row-set node.
   */
  public Node               getFirstChild()
  {
    if(DEBUG)
      System.out.println("In XStatement.getFirstChild: "+this.getNodeTest());
    
    try
    {
      if((this.getNodeTest().getNamespace() == null) && 
         (this.getNodeTest().getLocalName().equals(S_DOCELEMENTNAME)))
        return m_rowset;
      else
        return null;
    }
    catch(NullPointerException e)
    {
      e.printStackTrace();
      throw e;
    }
  }
  
  /**
   * This always returns null.
   */
  public Node               getNextSibling()
  {
    if(DEBUG)
      System.out.println("In XStatement.getNextSibling");
    return null;
  }
  
  /**
   * The parent node of document is always null.
   */
  public Node               getParentNode()
  {
    if(DEBUG)
      System.out.println("In XStatement.getParentNode");
    return null;
  }
  
  /**
   * Tell if there are any children of the document, 
   * which is always true.
   */
  public boolean            hasChildNodes()
  {
    if(DEBUG)
      System.out.println("In XStatement.hasChildNodes");
    return true;
  }
  
  // ===== ContextNodeList implementation =====
  
  public Node getCurrentNode()
  {
    return this.m_rowset;
  }
  
  public int getCurrentPos()
  {
    return 0; // Not totally sure...
  }
  
  public void reset()
  {
    this.m_nextHasBeenCalled = false;
  }

  public void setShouldCacheNodes(boolean b)
  {
    // Set streamable?
  }

  public void runTo(int index)
  {
    // Throw exception if not streamable!
  }

  public void setCurrentPos(int i)
  {
    // Throw exception if not streamable!
  }
  
  public int size()
  {
    return 1;
  }
  
  public boolean isFresh()
  {
    return (!this.m_nextHasBeenCalled);
  }
  
  public NodeIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    XStatement clone = (XStatement)super.clone();
    clone.reset();
    return clone;
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    XStatement clone = (XStatement)super.clone();
    return clone;
  }
}
