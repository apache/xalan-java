package org.apache.xpath.functions;

import org.apache.xpath.Expression;

public class Function3Args extends Function2Args
{
  Expression m_arg2;
  
  public Expression getArg2()
  {
    return m_arg2;
  }
  
  public void setArg(Expression arg, int argNum)
    throws WrongNumberArgsException
  {
    if(argNum < 2)
      super.setArg(arg, argNum);
    else if(2 == argNum)
      m_arg2 = arg;
    else
      throw new WrongNumberArgsException("3");
  }
  
  public void checkNumberArgs(int argNum)
    throws WrongNumberArgsException
  {
    if(argNum != 3)
      throw new WrongNumberArgsException("3");
  }

}
