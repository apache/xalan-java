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
public class NodeTest extends NonExecutableExpression
{
  public NodeTest(XPath parser)
  {
  	super(parser);
  }
  
  NodeTest(int whatToShow, XPath parser)
  {
  	super(parser);
  	m_whatToShow = whatToShow;
  }

  public void jjtAddChild(Node n, int i) 
  {
    if(n instanceof NameTest) // includes KindTest
    {
    	NameTest ntest = (NameTest)n;
    	m_isTotallyWild = ntest.isTotallyWild();
    	m_namespace = ntest.getNamespaceURI();
    	m_name = ntest.getLocalName();
    	m_whatToShow = ntest.getWhatToShow();
    }
    else
    {
    	// Assertion, should never happen.
    	throw new RuntimeException("Child of NodeTest can only be a NameTest or KindTest: "+
    		n+" "+m_parser.m_prefixResolver);
    }
  }
  
  int m_whatToShow;
  
  public int getWhatToShow()
  {
  	return m_whatToShow;
  }

  public void setWhatToShow(int wts)
  {
  	m_whatToShow = wts;
  }

  
  private boolean m_isTotallyWild = false;
  
  public boolean isTotallyWild()
  {
  	return m_isTotallyWild;
  }

  public void setTotallyWild(boolean b)
  {
  	m_isTotallyWild = b;
  }
  
  /**
   * The namespace to be tested for, which may be null.
   */
  String m_namespace;

  /**
   * The local name to be tested for.
   */
  String m_name;
  
  public String getNamespaceURI()
  {
  	return m_namespace;
  }

  public String getLocalName()
  {
  	return m_name;
  }
  
  public void setNamespaceURI(String n)
  {
  	m_namespace = n;
  }

  public void setLocalName(String n)
  {
  	m_name = n;
  }

}

