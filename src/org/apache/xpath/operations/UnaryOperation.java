package org.apache.xpath.operations;

import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.w3c.dom.Node;

public abstract class UnaryOperation extends Expression
{
  protected Expression m_right;
  
  public void setRight(Expression r)
  {
    m_right = r;
  }
  
  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    XObject right = m_right.execute(xctxt);
    
    return operate(right);
  }
  
  public abstract XObject operate(XObject right)
    throws org.xml.sax.SAXException;
}
