package org.apache.xpath.parser;



public class IfExpr extends NonExecutableExpression
{

  /**
   * Constructor for IfExpr
   */
  public IfExpr(XPath parser)
  {
    super(parser);
  }

  /**
   * @see SimpleNode#shouldReduceIfOneChild()
   */
  public boolean shouldReduceIfOneChild()
  {
    return (jjtGetNumChildren() == 1) ? true : false;
  }

}

