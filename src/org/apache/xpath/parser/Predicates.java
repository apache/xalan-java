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
public class Predicates extends NonExecutableExpression
{
  public Predicates(XPath parser)
  {
	  super(parser);
  }

  public Vector getPreds()
  {
  	return m_exprs;
  }
  
  public void jjtClose() 
  {
  	if(null == m_exprs)
  		return;
  	int count = m_exprs.size();
  	for(int i = count-1; i >= 0; i--)
  	{
  		Object n = m_exprs.elementAt(i);
  		if(n instanceof LbrackOrRbrack)
  			m_exprs.removeElementAt(i);
  	}
  	super.jjtClose();
  }

	
}

