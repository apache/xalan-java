package org.apache.xalan.stree;

import org.apache.xpath.DOM2Helper;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

public class StreeDOMHelper extends DOM2Helper
{
  /**
   * Create an empty DOM Document.  Mainly used for RTFs.
   */
  public Document createDocument()
  {
    return new DocumentImpl();
  }
  
  public String getUniqueID(Node node)
  {
    try
    {
      int index = ((Child)node).getUid();
      return "N"+Integer.toHexString(index);
    }
    catch(ClassCastException cce)
    {
      return super.getUniqueID(node);
    }
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree.
   */
  public short getLevel(Node node1)
  {
    try
    {
      return ((Child)node1).getLevel();
    }
    catch(ClassCastException cce)
    {
      return super.getLevel(node1);
    }
  }
  
  /**
   * Tell if the given node is a namespace decl node.
   */
  public boolean isNamespaceNode(Node n)
  {
    try
    {
      return ((Child)n).isNamespaceNode();
    }
    catch(ClassCastException cce)
    {
      return super.isNamespaceNode(n);
    }
  }


}
