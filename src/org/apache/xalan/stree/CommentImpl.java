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
}
