package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * This class represents the row-set StreamableNode, a "streamable" holder  
 * for the JDBC query result set.
 */
public class RowSet extends StreamableNode
{
  ColumnHeader[] m_columnHeaders;
  Row m_firstrow;
  private static final boolean DEBUG = false;

  public RowSet(XStatement statement)
  {
    super(statement);
  }
  
  // ===== Element implementation =====
    
  /**
   * Return "row-set".
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
  public Node               getFirstChild()
  {
    if(DEBUG)
      System.out.println("In RowSet.getFirstChild");

    try
    {
      if(this.getNodeTest().getNamespace() == null)
      {
        if(this.getNodeTest().getLocalName().equals(XStatement.S_COLUMNHEADERNAME))
        {
          if(null == m_columnHeaders)
          {
            ResultSetMetaData metaData = getXStatement().m_resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            if(columnCount > 0)
            {
              m_columnHeaders = new ColumnHeader[columnCount];
              m_columnHeaders[0] = new ColumnHeader(getXStatement(), this, 0, metaData);
              return m_columnHeaders[0];
            }
            else 
              return null;
          }
          else
            return m_columnHeaders[0];
        }
        else if(this.getNodeTest().getLocalName().equals(XStatement.S_ROWNAME))
        {
          if(null == m_firstrow)
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
    catch(SQLException sqle)
    {
      // diagnostics?
      return null;
    }
  }
  
  /**
   * This always returns null.
   */
  public Node               getNextSibling()
  {
    if(DEBUG)
      System.out.println("In RowSet.getNextSibling");
    return null;
  }
  
  /**
   * The parent node of row-set is #Document (represented by XStatement).
   */
  public Node               getParentNode()
  {
    if(DEBUG)
      System.out.println("In RowSet.getParentNode");
    return this.getXStatement();
  }
  
  /**
   * Tell if there are any children of the document, 
   * which is always true.
   */
  public boolean            hasChildNodes()
  {
    if(DEBUG)
      System.out.println("In RowSet.hasChildNodes");
    return true;
  }

}
