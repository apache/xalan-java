package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.Comment;

public class CommentImpl extends TextImpl implements Comment
{
  public CommentImpl (String data)
  {
    super(data);
  }

  public CommentImpl (char ch[], int start, int length)
  {
    super(ch, start, length);
  }

  /** 
   * A short integer indicating what type of node this is. The named
   * constants for this value are defined in the org.w3c.dom.Node interface.
   */
  public short getNodeType() 
  {
    return Node.COMMENT_NODE;
  }

  /** Returns the node name. */
  public String getNodeName() 
  {
    return "#comment";
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
    return "#comment";
  }
}
