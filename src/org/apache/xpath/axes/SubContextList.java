package org.apache.xpath.axes;

import org.apache.xpath.XPathContext;

import org.w3c.dom.Node;

public interface SubContextList
{  
  public int getLastPos(XPathContext xctxt);
  
  /**
   * Get the current sub-context position.
   */
  public int getProximityPosition(XPathContext xctxt);
}
