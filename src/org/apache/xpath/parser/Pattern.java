package org.apache.xpath.parser;

import java.util.Vector;

import org.apache.xerces.impl.xpath.XPath.Axis;
import org.apache.xpath.Expression;
import org.apache.xpath.patterns.StepPattern;

/**
 * This is an expression node that only exists for construction 
 * purposes. 
 */
public class Pattern extends NonExecutableExpression
{
  public Pattern(XPath parser)
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
  	int invertedPos = (m_exprs.size()-1)-i;
  	if(0 == invertedPos)
  	{
//  		if(n instanceof StepPattern)
//  		{
//  		 	StepPattern spat = (StepPattern)n;
//  		 	spat.setAxis(org.apache.xml.dtm.Axis.SELF);
//  		}
    	m_exprs.setElementAt(n, invertedPos);
  	}
  	else if(n instanceof SlashOrSlashSlash)
  	{
  		m_exprs.setElementAt(n, invertedPos);
  	}
  	else
  	{
  		int prevNodePos = invertedPos-1;
  		Node prevNode = (Node)m_exprs.elementAt(prevNodePos);
  		int whichAxis;
  		if(prevNode instanceof SlashOrSlashSlash)
  		{
  			m_exprs.removeElementAt(prevNodePos);
  			whichAxis = ((SlashOrSlashSlash)prevNode).getisSlashSlash() ? 
  					org.apache.xml.dtm.Axis.ANCESTOR : org.apache.xml.dtm.Axis.PARENT;
  					
	  		if(n instanceof StepPattern)
	  		{
	  			StepPattern spat = (StepPattern)n;
	  			spat.setAxis(whichAxis);
	  		}
  		}
  			
  		Node head = (Node)m_exprs.elementAt(0);
  		if(head instanceof StepPattern)
  		{
  			StepPattern headPat = (StepPattern)head;
  			StepPattern tail = headPat.getRelativePathPattern();
  			while(null != tail)
  			{
  					headPat = tail;
  					tail = tail.getRelativePathPattern();
  			}
  			headPat.setExpression((Expression)n);
  		}
  			
  	}
  }

  public void jjtClose() 
  {
  	if(jjtGetNumChildren() > 0)
  	{
  		((StepPattern)jjtGetChild(0)).calcScore();
  		int i = 4; // debugger breakpoint
  	}
  }

}

