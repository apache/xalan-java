package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * This class represents a column node from a row in a JDBC-accessed 
 * database.
 */
public class Column extends StreamableNode
{
  int m_columnIndex;
  Row m_parent;
  private static final boolean DEBUG = false;
  ColumnData m_text;
  
  public Column(XStatement statement, Row parent, int columnIndex, 
                ResultSetMetaData metadata)
  {
    super(statement);
    m_columnIndex = columnIndex;
    m_parent = parent;
    m_text = null;
  }
  
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
    return XStatement.S_COLUMNNAME;
  }
      
  /**
   * Return the document element node.
   */
  public Node getFirstChild()
  {
    if(DEBUG)
      System.out.println("In Column.getFirstChild");
    
    if(null == m_text)
      m_text = new ColumnData(this.getXStatement(), this);
    
    return m_text;
  }
  
  /**
   * This always returns null.
   */
  public Node getNextSibling()
  {
    if(DEBUG)
      System.out.println("In Column.getNextSibling");
    int nextIndex = m_columnIndex+1;
    return (nextIndex < m_parent.m_childCount) 
           ? m_parent.m_columns[nextIndex] : null;
  }
  
  /**
   * The parent node of document is always null.
   */
  public Node getParentNode()
  {
    if(DEBUG)
      System.out.println("In Column.getParentNode");
    return m_parent;
  }
  
  /**
   * Tell if there are any children of the document, 
   * which is always true.
   */
  public boolean            hasChildNodes()
  {
    if(DEBUG)
      System.out.println("In Column.hasChildNodes");
    return true;
  }
  
  public NamedNodeMap       getAttributes()
  {
    return m_parent.m_parent.m_columnHeaders[m_columnIndex];
  }


  
}
