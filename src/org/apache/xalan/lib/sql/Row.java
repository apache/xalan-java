package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.apache.xalan.utils.UnImplNode;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * This class represents a row from a database.  It is used 
 * over and over, and so is certainly not fully DOM complient, 
 * and will result in strange results in the stylesheet if the 
 * user is not carefull.
 */
public class Row extends StreamableNode
{
  int m_childCount;
  Column[] m_columns;
  ResultSetMetaData m_metadata;
  boolean m_isStreamable = false;
  Row m_next; // normally null, if streamable.
  private static final boolean DEBUG = false;
  RowSet m_parent;
  
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
      for(int i = 0; i < m_childCount; i++)
      {
        m_columns[i] = new Column(xstatement, this, i, metadata);
      }
    }
    catch(SQLException sqle)
    {
      // diagnostics?
    }
  }
    
  // ===== Element implementation =====
    
  /**
   * The parent of a document is null.
   */
  public Document getOwnerDocument()
  {
    return this.getXStatement();
  }
  
  /**
   * Return "#Document".
   */
  public String getNodeName()
  {
    return XStatement.S_ROWNAME;
  }
      
  /**
   * Return the document element node.
   */
  public Node               getFirstChild()
  {
    if(DEBUG)
      System.out.println("In Row.getFirstChild");
    
    if(this.hasChildNodes())
      return m_columns[0];
    else
      return null;
  }
  
  /**
   * This always returns null.
   */
  public Node               getNextSibling()
  {
    if(DEBUG)
    {
      System.out.print("In Row.getNextSibling");
      System.out.flush();
    }
    XStatement xstatement = this.getXStatement();
    ResultSet resultSet = xstatement.getResultSet();
    try
    {
      if(m_isStreamable)
      {
        if(resultSet.next())
          return this;
        else
          return null;
      }
      else
      {
        if(null == m_next)
        {
          try
          {
            if(resultSet.next())
              m_next = new Row(getXStatement(), m_parent);
          }
          catch(SQLException sqle)
          {
            // Diagnostics?
          }
        }
        if(DEBUG)
        {
          System.out.println(": m_next: "+m_next);
          System.out.flush();
          // Exception e = new Exception();
          // e.printStackTrace();
        }
        return m_next;
      }
    }
    catch(SQLException sqle)
    {
      // Diagnostics?
      return null;
    }
  }
    
  /**
   * Return the RowSet parent.
   */
  public Node               getParentNode()
  {
    if(DEBUG)
      System.out.println("In Row.getParentNode");
    return m_parent;
  }
  
  /**
   * Tell if there are any children of the document, 
   * which is always true.
   */
  public boolean            hasChildNodes()
  {
    if(DEBUG)
      System.out.println("In Row.hasChildNodes");
    return (m_childCount > 0);
  }

}
