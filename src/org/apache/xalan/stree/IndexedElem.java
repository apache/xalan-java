package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;


public interface IndexedElem 
  
{
  

  /** 
   * An integer indicating where this node's children can
   * be found in the indexed nodes list.
   */
  public void setIndex(int anIndex);
  
  
  /** 
   * An integer indicating where this node's children can
   * be found in the indexed nodes list.
   */
  public int getIndex();
  

}  