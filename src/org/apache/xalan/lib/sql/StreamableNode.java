package org.apache.xalan.lib.sql;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.apache.xalan.utils.UnImplNode;
import org.apache.xpath.patterns.NodeTestFilter;
import org.apache.xpath.patterns.NodeTest;

/**
 * This is the superclass for all nodes in the Xalan sql package.
 */
public class StreamableNode extends UnImplNode implements NodeTestFilter
{
  private XStatement m_statement;
  
  public XStatement getXStatement()
  {
    return m_statement;
  }
  
  private NodeTest m_nodetest;
  
  public NodeTest getNodeTest()
  {
    return m_nodetest;
  }
  
  public StreamableNode(XStatement statement)
  {
    m_statement = statement;
  }
  
  public void setNodeTest(NodeTest nodeTest)
  {
    m_nodetest = nodeTest;
  }
  
  public Document getOwnerDocument()
  {
    return m_statement;
  }

  /**
   * Streamable nodes default to being elements.
   */
  public short getNodeType()
  {
    return Node.ELEMENT_NODE;
  }
  
  /**
   * Return "#Document".
   */
  public String       getLocalName()
  {
    return getNodeName();
  }

  /**
   * Returns null.
   */
  public String             getNamespaceURI()
  {
    return null;
  }

  /** Returns null. */
  public String             getPrefix()
  {
    return null;
  }


}
