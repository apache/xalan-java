package org.apache.xpath.functions;

import org.apache.xpath.Expression;

public class FunctionMultiArgs extends Function3Args
{
  Expression[] m_args;
  
  public void setArg(Expression arg, int argNum)
    throws WrongNumberArgsException
  {
    if(argNum < 3)
      super.setArg(arg, argNum);
    else if(null == m_args)
      m_args = new Expression[1];
    else
    {
      // Slow but space conservative.
      Expression[] args = new Expression[m_args.length+1];
      System.arraycopy(m_args, 0, args, 0, m_args.length);
      args[m_args.length] = arg;
      m_args = args;
    }
  }
  
  public void checkNumberArgs(int argNum)
    throws WrongNumberArgsException
  {
  }

}

