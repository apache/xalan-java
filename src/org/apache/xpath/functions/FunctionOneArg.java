package org.apache.xpath.functions;

import org.apache.xpath.Expression;

public class FunctionOneArg extends Function
{
  Expression m_arg0;

  public Expression getArg0()
  {
    return m_arg0;
  }

  public void setArg(Expression arg, int argNum)
    throws WrongNumberArgsException
  {
    if(0 == argNum)
      m_arg0 = arg;
    else
      throw new WrongNumberArgsException("1");
  }
  
  public void checkNumberArgs(int argNum)
    throws WrongNumberArgsException
  {
    if(argNum != 1)
      throw new WrongNumberArgsException("1");
  }

}
