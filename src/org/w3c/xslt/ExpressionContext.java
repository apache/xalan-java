package org.w3c.xslt;

import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Node;

/**
 * An object that implements this interface can supply 
 * information about the current XPath expression context.
 */
public interface ExpressionContext
{
  /**
   * Get the current context node.
   * @return The current context node.
   */
  public Node getContextNode();
  
  /**
   * Get the current context node list.
   * @return An iterator for the current context list, as 
   * defined in XSLT.
   */
  public NodeIterator getContextNodes();

  /**
   * Get the value of a node as a number.
   * @param n Node to be converted to a number.  May be null.
   * @return value of n as a number.
   */
  public double toNumber(Node n);

  /**
   * Get the value of a node as a string.
   * @param n Node to be converted to a string.  May be null.
   * @return value of n as a string, or an empty string if n is null.
   */
  public String toString(Node n);

}
