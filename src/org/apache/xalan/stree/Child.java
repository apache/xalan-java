package org.apache.xalan.stree;

import org.apache.xalan.utils.UnImplNode;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

public class Child extends UnImplNode
{
  private Parent m_parent;
  private short m_level;
  
  /**
   * Set the parent of the node.
   */
  protected void setParent(Parent parent)
  {
    m_parent = parent;
  }
  
  /**
   * Return if this node has had all it's children added, i.e. 
   * if a endElement event has occured.  An atomic node always 
   * returns true.
   */
  public boolean isComplete()
  {
    // Atomic nodes are always complete.
    return true;
  }
  
  /**
   * The position in the parent's list.
   */
  private int m_childPos;
  
  /**
   * <meta name="usage" content="internal"/>
   * Set the position of the child of an element in the parent 
   * array.
   */
  protected void SetChildPosition(int pos)
  {
    m_childPos = (short)pos;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the position of the child of an element in the parent 
   * array.
   */
  public int getChildPosition()
  {
    return m_childPos;
  }
  
  /**
   * The UID (document order index).
   */
  private int m_uid;
  
  /**
   * Set the UID (document order index).
   */
  protected void setUid(int kIndex)
  {
    m_uid = kIndex;
  }
  
  /**
   * Get the UID (document order index).
   */
  public int getUid()
  {
    return m_uid;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree.
   */
  public short getLevel()
  {
    return m_level;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree.
   */
  public void setLevel(short level)
  {
    m_level = level;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the value of K.  K is the maximum width of the tree.
   *
  public int getK()
  {
    return getDocumentImpl().getK();
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the value of Y.  Y is the maximum depth of the tree.
   * Needed to calculate depth-first (document order) numbering.
   *
  public int getY()
  {
    return getDocumentImpl().getY();
  }

  /**
   * Get the root Document Implementation.
   */
  DocumentImpl getDocumentImpl()
  {
    Child n = this;
    while(n.getUid() > 1)
      n = (Child)n.getParentNode();
    // if((n == null) || !(n instanceof DocumentImpl))
    //    return null;
    return (DocumentImpl)n;
  }

  
  // ================ Node interface implementation ==============
      
  /**
   * The parent of this node. All nodes, except <code>Attr</code>, 
   * <code>Document</code>, <code>DocumentFragment</code>, 
   * <code>Entity</code>, and <code>Notation</code> may have a parent. 
   * However, if a	node has just been created and not yet added to the 
   * tree, or if it has been removed from the tree, this is 
   * <code>null</code>.
   */
  public Node         getParentNode()
  {
    return this.m_parent;
  }

  /**
   * The first child of this node. If there is no such node, this returns 
   * <code>null</code>.
   */
  public Node         getFirstChild()
  {
    return null;
  }
  
  /**
   * The last child of this node. If there is no such node, this returns 
   * <code>null</code>.
   */
  public Node         getLastChild()
  {
    return null;
  }
  
  /**
   * The node immediately preceding this node. If there is no such node, 
   * this returns <code>null</code>.
   */
  public Node         getPreviousSibling()
  {
    if (m_parent!= null)
    {
      try{
        return m_parent.getChild(getChildPosition()-1);
      }
      catch(Exception e)
      {}
    }  
    return null;
  }
  
  /**
   * The node immediately following this node. If there is no such node, 
   * this returns <code>null</code>.
   */
  public Node         getNextSibling()
  {
    if (m_parent!= null)
    {
      try
      {
        return m_parent.getChild(getChildPosition()+1);
      }
      catch(Exception e)
      {}
    }  
    return null;
  }
      
  /**
   * The <code>Document</code> object associated with this node. This is 
   * also the <code>Document</code> object used to create new nodes. When 
   * this node is a <code>Document</code> or a <code>DocumentType</code> 
   * which is not used with any <code>Document</code> yet, this is 
   * <code>null</code>.
   * @version DOM Level 2
   */
  public Document     getOwnerDocument()
  {
    return getDocumentImpl();
  }
  
  /**
   *  This is a convenience method to allow easy determination of whether a 
   * node has any children.
   * @return  <code>true</code> if the node has any children, 
   *   <code>false</code> if the node has no children.
   */
  public boolean      hasChildNodes()
  {
    return false;
  }

  /**
   * Tests whether the DOM implementation implements a specific feature and 
   * that feature is supported by this node.
   * @since DOM Level 2
   * @param feature The string of the feature to test. This is the same name 
   *   that which can be passed to the method <code>hasFeature</code> on 
   *   <code>DOMImplementation</code>.
   * @param version This is the version number of the feature to test. In 
   *   Level 2, version 1, this is the string "2.0". If the version is not 
   *   specified, supporting any version of the feature will cause the 
   *   method to return <code>true</code>.
   * @return Returns <code>true</code> if the specified feature is supported 
   *   on this node, <code>false</code> otherwise.
   */
  public boolean      supports(String feature, 
                               String version)
  {
    return false;
  }

  /**
   * The namespace URI of this node, or <code>null</code> if it is 
   * unspecified.
    */
  public String       getNamespaceURI()
  {
    return null;
  }
    
  /**
   * The namespace prefix of this node, or <code>null</code> if it is 
   * unspecified.
   * @since DOM Level 2
   */
  public String       getPrefix()
  {
    return null;
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
    return null;
  }
  
  /** UnImplemented. */
  public String getTagName()
  {
    return null;
  }
  
  /** Unimplemented. */
  public NamedNodeMap       getAttributes()
  {
    return null;
  }
  
  /** Unimplemented. */
  public void               setAttribute(String name,
                                         String value)
    throws DOMException
  {    
  }
  
  /**
   * Tell if the given node is a namespace decl node.
   */
  public boolean isNamespaceNode()
  {
    return false;
  }

}
