package org.apache.xpath.parser;
public class QuantifiedExpr extends NonExecutableExpression
{

  /**
   * Constructor for QuantifiedExpr
   */
  public QuantifiedExpr(XPath parser)
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

