package org.apache.xpath.parser;

import org.apache.xpath.patterns.FunctionPattern;

/**
 * Temporarily holds a function while it's being built.
 */
public class TempFunctionHolder extends FunctionPattern
{

  /**
   * Constructor for TempFunctionHolder
   */
  public TempFunctionHolder()
  {
    super();
  }
  
  /**
   * Tell if this node should have it's parent reduced.
   */
  public boolean shouldReduceIfOneChild()
  {
  	return true;
  }

  public Node jjtGetChild(int i) 
  {
    return m_functionExpr;
  }

  public int jjtGetNumChildren() {
    return 1;
  }


}

