package org.apache.xpath.functions;

import org.apache.xpath.Expression;

public class Function2Args extends FunctionOneArg
{
  Expression m_arg1;
  
  public Expression getArg1()
  {
    return m_arg1;
  }
  
  public void setArg(Expression arg, int argNum)
    throws WrongNumberArgsException
  {
    // System.out.println("argNum: "+argNum);
    if(argNum == 0)
      super.setArg(arg, argNum);
    else if(1 == argNum)
      m_arg1 = arg;
    else
      throw new WrongNumberArgsException("2");

  }

  public void checkNumberArgs(int argNum)
    throws WrongNumberArgsException
  {
    if(argNum != 2)
      throw new WrongNumberArgsException("2");
  }
}
