package org.apache.xpath.parser;

import java.util.Vector;

import org.apache.xpath.functions.Function;
import org.apache.xpath.operations.Variable;

public class PathExpr extends NonExecutableExpression
{
  public PathExpr(XPath parser)
  {
  	super(parser);
  }
    
  /**
   * Tell if this node is part of a PathExpr chain.  For instance:
   * <pre>
   * 	|UnaryExpr
   * 	|   PathExpr
   * 	|      StepExpr
   * 	|         AxisChild child::
   * 	|         NodeTest
   * 	|            NameTest
   * 	|               QName foo
   * 	|         Predicates   * 
   * </pre><br/>
   * In this example, UnaryExpr, PathExpr, and StepExpr should all return true.
   */
  public boolean isPathExpr()
  {
  	return true;
  }

	
//  public void jjtAddChild(Node n, int i) 
//  {
//  	n = fixupPrimarys(n);
//    if(null == m_exprs)
//    	m_exprs  = new Vector();
//  	if(i >= m_exprs.size())
//  	{
//  		m_exprs.setSize(i+1);
//  	}
//    m_exprs.setElementAt(n, i);
//  }
  
  public void jjtClose() 
  {
  	int size = m_exprs.size();
  	for(int i = size-1; i >= 0; i--)
  	{
  		SimpleNode node = (SimpleNode)m_exprs.elementAt(i);
  		if(node instanceof SlashOrSlashSlash)
  		{
  			boolean isSlashSlash = ((SlashOrSlashSlash)node).getisSlashSlash();
  			if(isSlashSlash)
  			{
  				node = new StepExpr(m_parser);
  				PatternAxis patAxis = new PatternAxis(org.apache.xml.dtm.Axis.DESCENDANTORSELF, m_parser);
  				patAxis.m_value = "descendant-or-self::"; // for diagnostics
  				NodeTest nt = new NodeTest(org.apache.xml.dtm.DTMFilter.SHOW_ALL, m_parser);
  				Predicates preds = new Predicates(m_parser);
  				node.jjtAddChild(patAxis, 0);
  				node.jjtAddChild(nt, 1);
  				node.jjtAddChild(preds, 2);
  				m_exprs.setElementAt(node, i);
  			}
  			else
  				m_exprs.removeElementAt(i);
  			// i--;
  		}
  		
  	}
  	Node child;
  	if((jjtGetNumChildren() == 1) 
  		&& (child = jjtGetChild(0)) instanceof StepExpr)
  	{
  		if(child.jjtGetNumChildren() == 2)
  		{
	  		StepExpr stepExpr = (StepExpr)child;
	  		if(stepExpr.getPredicates().jjtGetNumChildren() == 0)
	  		{
	  			Node varOrFunc = stepExpr.jjtGetChild(0);
	  			if(varOrFunc instanceof Variable 
	  			|| varOrFunc instanceof Function
	  			|| varOrFunc instanceof org.apache.xpath.patterns.FunctionPattern)
	  			{
	  				m_exprs.setElementAt(varOrFunc, 0);
	  				varOrFunc.jjtSetParent(this); // parent will continue to reduce.
	  			}
	  		}
  		}
  	}
  }
  

  /**
   * @see org.apache.xpath.parser.Node#jjtAddChild(Node, int)
   */
  public void jjtAddChild(Node n, int i)
  {
    if (n instanceof StepExpr && n.jjtGetChild(0) instanceof Variable)
    {
      if (null == m_exprs)
        m_exprs = new Vector();
      if (i >= m_exprs.size())
      {
        m_exprs.setSize(i + 1);
      }
      m_exprs.setElementAt(n, i);
    }
    else
      super.jjtAddChild(n, i);
  }

}

