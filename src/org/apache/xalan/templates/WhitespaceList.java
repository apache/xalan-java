package org.apache.xalan.templates;

import org.apache.xalan.xpath.XPathContext;
import org.apache.xalan.utils.QName;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class WhitespaceList extends TemplateList
{
  /**
   * Construct a TemplateList object.
   */
  WhitespaceList(Stylesheet stylesheet)
  {
    super(stylesheet);
  }

  /**
   * For derived classes to override which method gets accesed to 
   * get the imported template.
   */
  protected ElemTemplate getTemplate(StylesheetComposed imported,
                                     XPathContext support,
                                     Node targetNode,
                                     QName mode,
                                     boolean quietConflictWarnings)
    throws SAXException
  {
    return imported.getWhiteSpaceInfo(support, 
                                      (Element)targetNode);
  }

}
