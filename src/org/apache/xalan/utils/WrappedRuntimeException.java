package org.apache.xalan.utils;

/**
 * This class is for throwing important checked exceptions 
 * over non-checked methods.  It should be used with care, 
 * and in limited circumstances.
 */
public class WrappedRuntimeException extends RuntimeException
{
  private Exception m_exception;
  
  /**
   * Construct a WrappedRuntimeException from a 
   * checked exception.
   */
  public WrappedRuntimeException(Exception e)
  {
    super(e.getMessage());
    m_exception = e;
  }
  
  /**
   * Get the checked exception that this runtime exception wraps.
   */
  public Exception getException()
  {
    return m_exception;
  }
}
