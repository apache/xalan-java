package org.apache.xalan.stree;

// DOM imports
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;

/**
 * The responsibility of this class is to hide the internal workings 
 * of the LevelIndexer from the LocPathIterator, and to return nodes 
 * that:
 * <ol>
 * <li>Belongs to the given parent;</li>
 * <li>match the given node type;</li>
 * <li>match the given namespace;</li>
 * <li>match the given local name;</li>
 * </ol>
 */
public class LevelIndexIterator
{
  /**
   */
  public LevelIndexIterator(Node parent, int type, String url, String name)
  {
  }
  
  /**
   *  Returns the next node in the set and advances the position of the 
   * iterator in the set. After a NodeIterator is created, the first call 
   * to nextNode() returns the first node in the set.
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   * @exception DOMException
   *    INVALID_STATE_ERR: Raised if this method is called after the
   *   <code>detach</code> method was invoked.
   */
  public Node nextNode()
    throws DOMException
  {
    return null;
  }  
}
