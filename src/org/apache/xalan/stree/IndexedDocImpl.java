package org.apache.xalan.stree;

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Comment;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;

public class IndexedDocImpl extends DocumentImpl implements IndexedElem
{
  IndexedDocImpl()
  {
	  super();
  }

  IndexedDocImpl(DocumentType doctype)
  {
    super(doctype);    
  }
  
  private int m_index;
  
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
