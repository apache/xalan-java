package org.apache.xpath.parser;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class KindTest extends NameTest
{
  public KindTest(XPath parser)
  {
  	super(parser);
  }

// Why on earth did I have this commented?
//  public int getWhatToShow()
//  {
//  	return 0;
//  }  
}

