package org.apache.xalan.utils;

public class StringBufferPool
{
  private static ObjectPool m_stringBufPool 
    = new ObjectPool(java.lang.StringBuffer.class);
  
  public static StringBuffer get()
  {
    return (StringBuffer)m_stringBufPool.getInstance();
  }
  
  public static void free(StringBuffer sb)
  {
    m_stringBufPool.freeInstance(sb);
    sb.setLength(0);
  }
  
}
