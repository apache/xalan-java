package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;

public class Neg extends UnaryOperation
{
  public XObject operate(XObject right)
    throws org.xml.sax.SAXException
  {
    return new XNumber(-right.num());
  }
}
