package org.apache.xpath.parser;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class NonExecutableExpression extends Expression
{
  protected XPath m_parser; // I'm going to leave this for right now only.
  
  public NonExecutableExpression(XPath parser, String value)
  {
  	m_parser = parser;
  	m_value = value;
  }
  
  protected NonExecutableExpression(XPath parser)
  {
  	m_parser = parser;
  }
  
  public XPath getParser()
  {
  	return m_parser;
  }
	
  public Vector m_exprs = new Vector();
	
  public void jjtAddChild(Node n, int i) 
  {
  	n = fixupPrimarys(n);
    if(null == m_exprs)
    	m_exprs  = new Vector();
  	if(i >= m_exprs.size())
  	{
  		m_exprs.setSize(i+1);
  	}
    m_exprs.setElementAt(n, i);
  }
  
  public Node jjtGetChild(int i) 
  {
    if(null == m_exprs)
    	return null;
    else
    	return (Node)m_exprs.elementAt(i);
  }

  public int jjtGetNumChildren() 
  {
    if(null == m_exprs)
    	return 0;
    else
    	return m_exprs.size();
  }
  
  String m_value;
  public void processToken(Token t) { m_value = t.image; }
  
  public String toString() 
  { 
  	return this.getClass().getName()+((null == m_value) ? "" : (" "+m_value)); 
  }



  /**
   * @see Expression#deepEquals(Expression)
   * Dummy stub.
   */
  public boolean deepEquals(Expression expr)
  {
    return false;
  }


  /**
   * @see Expression#fixupVariables(Vector, int)
   * Dummy stub.
   */
  public void fixupVariables(VariableComposeState vcs)
  {
  }


  /**
   * @see Expression#execute(XPathContext)
   * Dummy stub.
   */
  public XObject execute(XPathContext xctxt) throws TransformerException
  {
  	throw new RuntimeException("Can't execute a NonExecutableExpression!");
    // return null;
  }
  
  public XObject execute(XPathContext xctxt, int currentNode)
          throws javax.xml.transform.TransformerException
  {
  	throw new RuntimeException("Can't execute a NonExecutableExpression!");
  }
  
  public XObject execute(
          XPathContext xctxt, int currentNode, DTM dtm, int expType)
            throws javax.xml.transform.TransformerException
  {
  	throw new RuntimeException("Can't execute a NonExecutableExpression!");
  }


  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   * Dummy stub.
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  }


}

