package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

public class String extends UnaryOperation
{
  public XObject operate(XObject right)
    throws org.xml.sax.SAXException
  {
    if(XObject.CLASS_STRING == right.getType())
      return right;
    else
      return new XString(right.str());
  }
}
