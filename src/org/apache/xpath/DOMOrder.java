package org.apache.xpath;

/**
 * Nodes that implement this index can return a document order index.
 * Eventually, this will be replaced by DOM 3 methods.
 */
public interface DOMOrder
{
  /**
   * Get the UID (document order index).
   */
  public int getUid();

}
