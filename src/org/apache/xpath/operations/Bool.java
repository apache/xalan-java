package org.apache.xpath.operations;

import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;

public class Bool extends UnaryOperation
{
  public XObject operate(XObject right)
    throws org.xml.sax.SAXException
  {
    if(XObject.CLASS_BOOLEAN == right.getType())
      return right;
    else
      return right.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
