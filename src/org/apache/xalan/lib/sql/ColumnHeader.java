package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * This class represents a ColumnHeader, which represents the metadata 
 * for a column.
 */
public class ColumnHeader extends StreamableNode implements NamedNodeMap
{
  private static final boolean DEBUG = false;
  int m_columnIndex;
  ResultSetMetaData m_metaData;
  RowSet m_parent;
  
  public ColumnHeader(XStatement statement, RowSet parent, int columnIndex, 
                      ResultSetMetaData metaData)
  {
    super(statement);
    m_columnIndex = columnIndex;
    m_metaData = metaData;
    m_parent = parent;
  }

  /**
   * Return "row-set".
   */
  public String getNodeName()
  {
    return XStatement.S_COLUMNHEADERNAME;
  }
  
  /**
   * Return the document element node.
   */
  public Node               getFirstChild()
  {
    if(DEBUG)
      System.out.println("In ColumnHeader.getFirstChild");
    
    return null;
  }
  
  /**
   * This always returns null.
   */
  public Node               getNextSibling()
  {
    if(DEBUG)
      System.out.println("In ColumnHeader.getNextSibling");
    // try
    {
      if(this.getNodeTest().getNamespace() == null)
      {
        if(this.getNodeTest().getLocalName().equals(XStatement.S_COLUMNHEADERNAME))
        {
          int nextIndex = m_columnIndex+1;
          if(nextIndex < m_parent.m_columnHeaders.length)
          {
            if(null == m_parent.m_columnHeaders[nextIndex])
              m_parent.m_columnHeaders[nextIndex] 
                = new ColumnHeader(getXStatement(), m_parent, nextIndex, m_metaData);
            return m_parent.m_columnHeaders[nextIndex];
          }
          else
            return null;
        }
      }
    }
    // catch(SQLException sqle)
    {
      // diagnostics?
    }
    return null;
  }
  
  /**
   * The parent node of document is always null.
   */
  public Node               getParentNode()
  {
    if(DEBUG)
      System.out.println("In ColumnHeader.getParentNode");
    return m_parent;
  }
  
  /**
   * Tell if there are any children of the document, 
   * which is always true.
   */
  public boolean            hasChildNodes()
  {
    if(DEBUG)
      System.out.println("In ColumnHeader.hasChildNodes");
    return false;
  }
  
  public NamedNodeMap       getAttributes()
  {
    return this;
  }
  
  // ============= NamedNodeMap ===============
  
  ColumnAttribute[] m_attributes = null;
  
  private void allocAttrs()
  {
    // try
    {
      m_attributes = new ColumnAttribute[ColumnAttribute.NUMBER_ATTRIBUTES];
    }
    // catch(SQLException sqle)
    {
      // diagnostics?
    }
  }
  
  private ColumnAttribute allocAttr(int pos, String name)
  {
    if(null == m_attributes[pos])
    {
      ColumnAttribute attr
        = new ColumnAttribute(this.getXStatement(),
                              this,
                              m_columnIndex, pos, m_metaData);
      attr.m_name = name;
      m_attributes[pos] = attr;
    }
    return m_attributes[pos];
  }
  
  public Node getNamedItem(String name)
  {
    if(null == m_attributes)
      allocAttrs();
    int pos = ColumnAttribute.getAttrPosFromName(name);
    if(pos >= 0)
    {
      return allocAttr(pos, name);
    }
    else
      return null;
  }
    
  public Node item(int index)
  {
    if(null == m_attributes)
      allocAttrs();
    
    String name = ColumnAttribute.getAttrNameFromPos(index);
    if(null != name)
    {
      return allocAttr(index, name);
    }
    else
      return null;
  }

  public int getLength()
  {
    return ColumnAttribute.NUMBER_ATTRIBUTES;
  }

  public Node getNamedItemNS(String namespaceURI, 
                             String localName)
  {
    if(null == namespaceURI)
      return getNamedItem(localName);
    return null;
  }
  
  public Node setNamedItem(Node arg)
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); 
    return null;
  }
  
  public Node removeNamedItem(String name)
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); 
    return null;
  }
  
  public Node setNamedItemNS(Node arg)
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); 
    return null;
  }
  
  public Node removeNamedItemNS(String namespaceURI, 
                                String localName)
    throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); 
    return null;
  }



}
