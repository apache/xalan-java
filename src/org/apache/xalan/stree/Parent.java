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
   * DON'T CALL THIS FUNCTION IF YOU CAN HELP IT!!!
   */
  public int getChildCount()
  {    
    if (!isComplete())
    {
      Object synchObj = getSynchObject();
      synchronized (synchObj)
      {
        try
        {
          // Here we have to wait until the element is complete
          while (!isComplete())
          {
            synchObj.wait();
            throwIfParseError();
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
        //System.out.println("... getcount " );
        
      }
    }
    //System.out.println("Waiting... Done "+ this.getNodeName() );          
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
      Object synchObj = getSynchObject();
      synchronized (synchObj)
      {
        try
        {
          // Only wait until the first child comes, or we are complete.
          while (!isComplete())
          {
            synchObj.wait();
            throwIfParseError();
            if(null != m_children)
              break;
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
        //System.out.println("... getcount " );        
      }
    }
    //System.out.println("Waiting(haschildnodes)... Done "+ this.getNodeName() );
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
      Object synchObj = getSynchObject();
      synchronized (synchObj)
      {
        try
        {
          // System.out.println("Waiting... getChild " + i + " " + getNodeName());
          while (!isComplete())
          {
            synchObj.wait();
            throwIfParseError();
            // System.out.println("... gotChild " + i);
            child = ((null != m_children) && (i >= 0) && i < m_children.length) ?
                    m_children[i] : null;
            if(null != child)
              break;
          }
        }
        catch (InterruptedException e)
        {
          throwIfParseError();
        }
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
    if (!hasChildNodes())
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
      throw new org.apache.xalan.utils.WrappedRuntimeException(e);
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
      
      // I think this was put in to handle Result tree fragments.
      // Maybe endDocument is not being called??    
      if(!doc.getUseMultiThreading() && (child instanceof Parent))
        ((Parent)child).setComplete(true);

    }
    catch(ClassCastException cce)
    {
      // TODO: Make ResultTreeFrag be an Stree DocumentFragment, or some such.
      // No owner doc, so we can't set the document order count, 
      // which will be a problem when result tree fragments need to 
      // be treated like node-sets.
      doc = null;
      if(child instanceof Parent) // for now DocumentFragments can't be built on another thread.
        ((Parent)child).setComplete(true);
    }
    child.setParent(this);
    child.setLevel((short)(getLevel() + 1));
	  // getDocumentImpl().getLevelIndexer().insertNode(child);
    
    if((null != doc) && (Node.ELEMENT_NODE == child.getNodeType()))
    {
      SourceTreeHandler sh = doc.getSourceTreeHandler();
      if(null != sh)
      {
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

    }
    if (newChild.getNodeType() != Node.ATTRIBUTE_NODE)
    {  
      // Notify anyone waiting for a child...
      synchronized (getSynchObject())
      {
        getSynchObject().notifyAll();
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
    if(!m_isComplete)
      throwIfParseError();
    return m_isComplete;
  }
  
  /**
   * Set that this node's child list is complete, i.e. 
   * an endElement event has occured.
   */
  public void setComplete(boolean isComplete)
  {
    m_isComplete = isComplete;
  }
  
  protected void throwParseError(Exception e)
  {
    m_isComplete = true;
    super.throwParseError(e);
  }


}
