package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;

public class DocumentFragmentImpl extends DocumentImpl implements DocumentFragment
{
  public DocumentFragmentImpl()
  {
    setComplete(true);
  }
  
  /** 
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() 
  {
    return Node.DOCUMENT_FRAGMENT_NODE;
  }

  /** Returns the node name. */
  public String getNodeName() 
  {
    return "#document-fragment";
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
    return "#document-fragment";
  }
}
