package org.apache.xpath.parser;

import java.util.Vector;

import org.apache.xpath.patterns.UnionPattern;

public class RootOfRootPattern extends RootOfRoot
{
  public RootOfRootPattern(XPath parser)
  {
	  super(parser);
  }

  public void jjtAddChild(Node n, int i) 
  {
    if(null == m_exprs)
    	m_exprs  = new Vector();
  	if(i >= m_exprs.size())
  	{
  		m_exprs.setSize(i+1);
  	}
  	if(n instanceof UnionPattern)
  	{
  		UnionPattern up = (UnionPattern)n;
  		if(1 == up.jjtGetNumChildren())
  		{
  			n = up.jjtGetChild(0);
  		}
  	}
    m_exprs.setElementAt(n, i);
  }

  public void jjtClose() 
  {
  	Node n = jjtGetChild(0);
  	if(n instanceof Pattern)
  	{
  		n.jjtSetParent(null);
  		m_exprs.setElementAt(n.jjtGetChild(0), 0);
  	}
  	super.jjtClose();
  }

}

