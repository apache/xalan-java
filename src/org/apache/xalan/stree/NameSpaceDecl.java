package org.apache.xalan.stree;

public class NameSpaceDecl extends AttrImplNS
{
  NameSpaceDecl(DocumentImpl doc, String uri, String name, String value)
  {
    super(doc, uri, name, value);
  }
  
  /**
   * Tell if the given node is a namespace decl node.
   */
  public boolean isNamespaceNode()
  {
    return true;
  }

}
