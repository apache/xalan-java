package org.apache.xalan.stree;

import org.apache.xalan.utils.DOMBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class StreeDOMBuilder extends DOMBuilder
{
  
  /**
   * StreeDOMBuilder instance constructor... it will add the DOM nodes 
   * to the document fragment.
   */
  public StreeDOMBuilder(Document doc, Node node)
  {
    super(doc, node);
  }

  /**
   * StreeDOMBuilder instance constructor... it will add the DOM nodes 
   * to the document fragment.
   */
  public StreeDOMBuilder(Document doc, DocumentFragment docFrag)
  {
    super( doc, docFrag);
  }

  /**
   * StreeDOMBuilder instance constructor... it will add the DOM nodes 
   * to the document.
   */
  public StreeDOMBuilder(Document doc)
  {
    super(doc);
  }
  
  public void setIDAttribute(String namespaceURI,
                             String qualifiedName,
                             String value,
                             Element elem)
  {
    ((DocumentImpl)this.getRootNode()).setIDAttribute(namespaceURI,
                                                      qualifiedName,
                                                      value, elem);
  }

}
