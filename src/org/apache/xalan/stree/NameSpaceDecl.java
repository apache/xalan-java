package org.apache.xalan.stree;

public class NameSpaceDecl extends AttrImplNS
{
  NameSpaceDecl(String uri, String name, String value)
  {
    super(uri, name, value);
  }
  
  /**
   * Tell if the given node is a namespace decl node.
   */
  public boolean isNamespaceNode()
  {
    return true;
  }

}
