package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.CDATASection;

public class CDATASectionImpl extends TextImpl implements CDATASection
{
  public CDATASectionImpl (String data)
  {
    super(data);
  }

  public CDATASectionImpl (char ch[], int start, int length)
  {
    super(ch, start, length);
  }

  /** Returns the node type. */
  public short getNodeType() 
  {
    return Node.CDATA_SECTION_NODE;
  }

  /** Returns the node name. */
  public String getNodeName() 
  {
    return "#cdata-section";
  }
  
  /**
   * Returns the local part of the qualified name of this node.
   * <br>For nodes created with a DOM Level 1 method, such as 
   * <code>createElement</code> from the <code>Document</code> interface, 
   * it is <code>null</code>.
   * @since DOM Level 2
   */
  public String       getLocalName()
  {
    return "#cdata-section";
  }
}
