package org.apache.xpath.objects;

import org.apache.xml.dtm.XType;

public class XDecimal extends XDouble  // TBD: For now, just use this as a double.
{
  /**
   * Constructor for Decimal
   * 
   * This one's actually being used, unlike most XObject empty ctors,
   * because it may be created and _then_ set (by processToken)
   * during stylesheet parsing.
   */
  public XDecimal()
  {
    super();
  }

}

