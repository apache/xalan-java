package org.apache.xalan.stree;

import org.xml.sax.Attributes;

public class IndexedElemWithNS extends ElementImplWithNS implements IndexedElem
{
  private String m_localName;
  private String m_uri;
  private int m_index;
  
  IndexedElemWithNS (DocumentImpl doc, String ns, String name)
  {
    super(doc, ns,name);    
  }
  
  IndexedElemWithNS (DocumentImpl doc, String ns, String localName,
                     String name, Attributes atts)
  {
    super(doc, ns, localName, name, atts);    
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
