package org.apache.xalan.stree;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;


public class IndexedElemImpl extends ElementImpl implements IndexedElem
  
{
  private String m_name;
  private short attrsEnd;
  private int m_index;
  
  IndexedElemImpl (DocumentImpl doc, String name)
  {
    super(doc, name);    
  }

  IndexedElemImpl (DocumentImpl doc, String name, Attributes atts)
  {
    super(doc, name, atts);    
  }

  /** 
   * An integer indicating where this node's children can
   * be found in the indexed nodes list.
   */
  public void setIndex(int anIndex) 
  {
    this.m_index = anIndex;
  }
  
  /** 
   * An integer indicating where this node's children can
   * be found in the indexed nodes list.
   */
  public int getIndex() 
  {
    return m_index;
  }

}  