package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * Represents a col node from a row node.
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
   * The parent of col is #Document (represented by XStatement).
   */
  public Document getOwnerDocument()
  {
    return this.getXStatement();
  }
  
  /**
   * Return "col".
   */
  public String getNodeName()
  {
    return XStatement.S_COLUMNNAME;
  }
      
  /**
   * Return the col text node (the column value).
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
   * Return the next col element for the current row.
   * @return a Column node or null.
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
   * The parent node of col is a row.
   */
  public Node getParentNode()
  {
    if(DEBUG)
      System.out.println("In Column.getParentNode");
    return m_parent;
  }
  
  /**
   * Tell if there are any children of col, 
   * which is always true. I.e., col contains a text node
   * with a textual representation of the column value.
   */
  public boolean            hasChildNodes()
  {
    if(DEBUG)
      System.out.println("In Column.hasChildNodes");
    return true;
  }
  /**
   * Return the metadata for this column.
   */
  public NamedNodeMap       getAttributes()
  {
    return m_parent.m_parent.m_columnHeaders[m_columnIndex];
  }  
}
