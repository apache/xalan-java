package org.apache.xpath.patterns;

import org.apache.xpath.Expression;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xpath.axes.LocPathIterator;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public class AncestorStepPattern extends StepPattern
{
  public AncestorStepPattern(int whatToShow, String namespace, String name)
  {
    super(whatToShow, namespace, name);
  }
  
  public AncestorStepPattern(int whatToShow)
  {
    super(whatToShow);
  }
  
  /**
   * Overide the super method so that we can handle
   * match patterns starting with a function such as id()// 
   */  
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    int whatToShow = getWhatToShow();
    if(whatToShow == NodeTest.SHOW_BYFUNCTION)
    {
      XObject score = NodeTest.SCORE_NONE;
      if(null != m_relativePathPattern)
      {
        score = m_relativePathPattern.execute(xctxt);
      }
      return score;
    }
    else 
      return super.execute(xctxt);
  }

  
  public XObject executeRelativePathPattern(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    XObject score = NodeTest.SCORE_NONE;
    Node parent = xctxt.getCurrentNode();
    
    while(null != (parent = xctxt.getDOMHelper().getParentOfNode(parent)))
    {
      try
      {
        xctxt.pushCurrentNode(parent);
        score = execute(xctxt);
        if(score != NodeTest.SCORE_NONE)
        {
          score = SCORE_OTHER;
          break;
        }
      }
      finally
      {
        xctxt.popCurrentNode();
      }
    }
    return score;
  }


}
