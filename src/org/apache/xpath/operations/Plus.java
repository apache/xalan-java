package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;

public class Plus extends Operation
{
  public XObject operate(XObject left, XObject right)
    throws org.xml.sax.SAXException
  {
    return new XNumber(left.num() +  right.num());
  }
}
