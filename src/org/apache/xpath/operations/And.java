package org.apache.xpath.operations;

import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;

import org.w3c.dom.Node;

public class And extends Operation
{
  /**
   * AND two expressions and return the boolean result. Override 
   * superclass method for optimization purposes.
   */
  public XObject execute(XPathContext xctxt) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = m_left.execute(xctxt);
    if(expr1.bool())
    {
      XObject expr2 = m_right.execute(xctxt);
      return expr2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    else
      return XBoolean.S_FALSE;
  }
}
