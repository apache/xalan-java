package org.apache.xpath.functions;

public class WrongNumberArgsException extends Exception
{
  public String m_argsExpected;
  
  public WrongNumberArgsException(String argsExpected)
  {
    super();
    m_argsExpected = argsExpected;
  }
}
