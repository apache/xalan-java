package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;

public class Number extends UnaryOperation
{
  public XObject operate(XObject right)
    throws org.xml.sax.SAXException
  {
    if(XObject.CLASS_NUMBER == right.getType())
      return right;
    else
      return new XNumber(right.num());
  }
}
