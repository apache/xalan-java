package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.WhiteSpaceInfo;

import org.xml.sax.SAXException;

public class Parent extends Child
{
  /**
   * The list of children.  For space conservation reasons, 
   * this list is resized everytime a child is added, and is 
   * always exactly the size of the child count.  This is 
   * certainly subject to review, but I thought I'd give it 
   * a try and see how it works.  The alternative is to 
   * keep an extra int around which tells us the first free 
   * member in the list, etc. 
   */
  protected Child[] m_children;   
  
  /**
   * Get the number of children this node currently contains.
   * Note that this will only return the number of children 
   * added so far.  If the isComplete property is false, 
   * it is likely that more children will be added.
   */
  public int getChildCount()
  {    
    if (!isComplete())
    {
      synchronized (this)
      {
        try
        {
          //System.out.println("Waiting... getCount " );
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
        //System.out.println("... getcount " );
        
      }
    }
    return (null == m_children) ? 0 : m_children.length;
  }
  
  /**
   *  This is a convenience method to allow easy determination of whether a 
   * node has any children.
   * @return  <code>true</code> if the node has any children, 
   *   <code>false</code> if the node has no children.
   */
  public boolean      hasChildNodes()
  {
    if (null == m_children && !isComplete())
    {
      synchronized (this)
      {
        try
        {
          //System.out.println("Waiting... getCount " );
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
        //System.out.println("... getcount " );        
      }
    }
    return (null == m_children || m_children.length == 0) ? false : true;
  }
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the position of the child of an element in the document.
   * Note that this is assuming an index starting at 1
   */
  public int getChildUID(int pos)
  {        
    Child child = getChild(pos);
    return (null != child) ? child.getUid() : -1;
  }
  
  /**
   * Get the nth child.
   * @param i the index of the child.
   * @exception ArrayIndexOutOfBoundsException if the index is out of bounds.
   * @exception NullPointerException if there are no children.
   */
  public Child getChild(int i)
    throws ArrayIndexOutOfBoundsException, NullPointerException
  {
    // wait?
    
    Child child = ((null != m_children) && (i >= 0) && i < m_children.length) ?
           m_children[i] : null;
    if (child == null && !isComplete())
    {
      synchronized (this)
      {
        try
        {
          // System.out.println("Waiting... getChild " + i + " " + getNodeName());
          wait();
        }
        catch (InterruptedException e)
        {
          // That's OK, it's as good a time as any to check again
        }
        // System.out.println("... gotChild " + i);
        child = ((null != m_children) && (i >= 0) && i < m_children.length) ?
           m_children[i] : null;
        }
    }     
    return child;
      
  }
  
  /**
   * The first child of this node. If there is no such node, this returns 
   * <code>null</code>.
   */
  public Node         getFirstChild()
  {
    if (getChildCount() == 0)
      return null;
    else        
      return getChild(0);
  }
  
  /**
   * The last child of this node. If there is no such node, this returns 
   * <code>null</code>.
   */
  public Node         getLastChild()
  {
    try
    {
      return getChild(getChildCount()-1);
    }
    catch(Exception e)
    {
      return null;
    }  
  }
  
  /**
   * Append a child to the child list.
   * @param newChild Must be a org.apache.xalan.stree.Child.
   * @exception ClassCastException if the newChild isn't a org.apache.xalan.stree.Child.
   */
  public Node appendChild(Node newChild)
    throws DOMException
  {
    int childCount;
    if(null == m_children)
    {
      m_children = new Child[1];
      childCount = 0;
    }
    else
    {
      childCount = m_children.length;
      Child[] newChildren = new Child[childCount+1];
      System.arraycopy(m_children, 0, newChildren, 0, childCount);
      // Child prevChild = m_children[childCount-1];
      m_children = newChildren;
    }
    
    Child child = (Child)newChild;
    m_children[childCount] = child;
    child.SetChildPosition(childCount);

    DocumentImpl doc;
    try
    {
      doc = (DocumentImpl)this.getOwnerDocument();
      doc.incrementDocOrderCount();
      child.setUid(doc.getDocOrderCount());
    }
    catch(ClassCastException cce)
    {
      // TODO: Make ResultTreeFrag be an Stree DocumentFragment, or some such.
      // No owner doc, so we can't set the document order count, 
      // which will be a problem when result tree fragments need to 
      // be treated like node-sets.
      doc = null;
    }
    child.setParent(this);
    child.setLevel((short)(getLevel() + 1));
	  // getDocumentImpl().getLevelIndexer().insertNode(child);
    
    if((null != doc) && (Node.ELEMENT_NODE == child.getNodeType()))
    {
      SourceTreeHandler sh = doc.getSourceTreeHandler();
      TransformerImpl transformer = sh.getTransformer();
      if(null != transformer)
      {
        StylesheetRoot stylesheet= transformer.getStylesheet();
        try
        {
          ElementImpl elem = (ElementImpl)child;
          WhiteSpaceInfo info 
            = stylesheet.getWhiteSpaceInfo(transformer.getXPathContext(), elem);
          boolean shouldStrip;
          if(null == info)
          {
            shouldStrip = sh.getShouldStripWhitespace();
          }
          else
          {
            shouldStrip = info.getShouldStripSpace();
          }
          sh.setShouldStripWhitespace(shouldStrip);
        }
        catch(SAXException se)
        {
          // TODO: Diagnostics
        }
      }

    }
    if (newChild.getNodeType() != Node.ATTRIBUTE_NODE)
    {  
      // Notify anyone waiting for a child...
      synchronized (this)
      {
        notifyAll();
      }
    }
    
    return newChild;
  }  

  
  /**
   * Flag that tells if this node is complete.
   */
  private boolean m_isComplete = false;
  
  /**
   * Return if this node has had all it's children added, i.e. 
   * if a endElement event has occured.
   */
  public boolean isComplete()
  {
    return m_isComplete;
  }

  /**
   * Set that this node's child list is complete, i.e. 
   * an endElement event has occured.
   */
  public void setComplete(boolean isComplete)
  {
    m_isComplete = isComplete;
    if (isComplete())
    {
      // Notify anyone waiting for a child...
      synchronized (this)
      {
        notifyAll();
      }
    }
  }

}
