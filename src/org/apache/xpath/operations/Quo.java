package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNumber;

public class Quo extends Operation
{
  // Actually, this is no longer supported by xpath...
  public XObject operate(XObject left, XObject right)
    throws org.xml.sax.SAXException
  {
    return new XNumber((int)(left.num() /  right.num()));
  }
}
