package org.apache.xpath.parser;
public class UnaryExpr extends NonExecutableExpression
{
  public UnaryExpr(XPath parser)
  {
	  super(parser);
  }

  public void jjtClose() 
  {
  	int n = jjtGetNumChildren();
  	if(n > 1)
  	{
  		Node head = jjtGetChild(0);
  		for(int i = 1; i < n; i++)
  		{
  			Node next = jjtGetChild(i);
  			head.jjtAddChild(next, 0);
  			head = next;
  		}
  		m_exprs.setSize(1);
  	}
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
 
  /**
   * @see SimpleNode#shouldReduceIfOneChild()
   */
  public boolean shouldReduceIfOneChild()
  {
    return (jjtGetNumChildren() == 1) ? true : false;
  }


}

