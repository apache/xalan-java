package org.apache.xalan.utils;

public class StringBufferPool
{
  private static ObjectPool m_stringBufPool 
    = new ObjectPool(org.apache.xalan.utils.FastStringBuffer.class);
  
  public static FastStringBuffer get()
  {
    return (FastStringBuffer)m_stringBufPool.getInstance();
  }
  
  public static void free(FastStringBuffer sb)
  {
    m_stringBufPool.freeInstance(sb);
    sb.setLength(0);
  }
  
}
