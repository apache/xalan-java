package org.apache.xpath.functions;

import org.w3c.dom.Node;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;

public class FunctionDef1Arg extends FunctionOneArg
{
  protected Node getArg0AsNode(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    return (null == m_arg0) ? xctxt.getCurrentNode() 
                              : m_arg0.execute(xctxt).nodeset().nextNode();
  }

  protected String getArg0AsString(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    return (null == m_arg0) ? XNodeSet.getStringFromNode(xctxt.getCurrentNode() )
                              : m_arg0.execute(xctxt).str();
  }

  protected double getArg0AsNumber(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    return (null == m_arg0) ? XNodeSet.getNumberFromNode(xctxt.getCurrentNode())
                              : m_arg0.execute(xctxt).num();
  }

  public void checkNumberArgs(int argNum)
    throws WrongNumberArgsException
  {
    if(argNum > 1)
      throw new WrongNumberArgsException("0 or 1");
  }

}
