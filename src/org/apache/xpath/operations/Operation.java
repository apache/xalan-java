package org.apache.xpath.operations;

import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Node;

public class Operation extends Expression
{
  protected Expression m_left;
  protected Expression m_right;
  
  public void setLeftRight(Expression l, Expression r)
  {
    m_left = l;
    m_right = r;
  }
  
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    XObject left = m_left.execute(xctxt);
    XObject right = m_right.execute(xctxt);
    
    return operate(left, right);
  }
  
  public XObject operate(XObject left, XObject right)
    throws org.xml.sax.SAXException
  {
    return null; // no-op
  }
}
