package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;

public class Gte extends Operation
{
  public XObject operate(XObject left, XObject right)
    throws org.xml.sax.SAXException
  {
    return left.greaterThanOrEqual(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
