package org.apache.xpath.axes;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public interface ContextNodeList
{
  public Node getCurrentNode();
  
  /**
   * Get the current position, which is one less than 
   * the next nextNode() call will retreave.  i.e. if 
   * you call getCurrentPos() and the return is 0, the next 
   * fetch will take place at index 1.
   */
  public int getCurrentPos();
  
  /**
   * Reset the iterator.
   */
  public void reset();
  
  /**
   * If setShouldCacheNodes(true) is called, then nodes will 
   * be cached.  They are not cached by default.
   */
  public void setShouldCacheNodes(boolean b);
  
  /**
   * If an index is requested, NodeSet will call this method 
   * to run the iterator to the index.  By default this sets 
   * m_next to the index.  If the index argument is -1, this 
   * signals that the iterator should be run to the end.
   */
  public void runTo(int index);
  
  /**
   * Set the current position in the node set.
   * @param i Must be a valid index.
   */
  public void setCurrentPos(int i);
  
  /**
   * Get the length of the list.
   */
  public int size();
  
  /**
   * Tells if this NodeSet is "fresh", in other words, if 
   * the first nextNode() that is called will return the 
   * first node in the set.
   */
  public boolean isFresh();
  
  /**
   * Get a cloned Iterator.
   */
  public NodeIterator cloneWithReset()
    throws CloneNotSupportedException;

  public Object clone()
    throws CloneNotSupportedException;
}
