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
public class Star extends NonExecutableExpression
{
  public Star(XPath parser)
  {
	  super(parser);
  }
}

