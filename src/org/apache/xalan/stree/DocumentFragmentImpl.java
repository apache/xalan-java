package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;

public class DocumentFragmentImpl extends Parent implements DocumentFragment
{
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
}
