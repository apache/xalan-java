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
public class PatternAxis extends NonExecutableExpression
{
	public PatternAxis(XPath parser)
	{
	  super(parser);
	}

	int m_axis;
	
	/**
	 * Return what axis this node represents.
	 * @return one of org.apache.xml.dtm.Axis.DESCENDANT, etc.
	 */
	public int getAxis()
	{
		return m_axis;
	}
	
	/**
	 * Return what axis this node represents.
	 * @return one of org.apache.xml.dtm.Axis.DESCENDANT, etc.
	 */
	public void setAxis(int axis)
	{
		m_axis = axis;
	}

	
	/**
	 * Construct a PatternAxis.
	 * 
	 * @param axis one of org.apache.xml.dtm.Axis.DESCENDANT, etc.
	 */
	PatternAxis(int axis, XPath parser)
	{
	  super(parser);
	  m_axis = axis;
	}
}

