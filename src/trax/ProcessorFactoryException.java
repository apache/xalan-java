// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import org.xml.sax.SAXException;

/**
 * The ProcessorFactoryException is a type of ProcessorException that 
 * is thrown when a configurable factory object can not 
 * be created.
 *
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>No open issues are known for this class</h4></dt>
 *    <dd></dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class ProcessorFactoryException extends SAXException
{
  /**
   * Create a new ProcessorFactoryException from an existing exception.
   *
   * @param message The detail message.
   * @param e The exception to be wrapped in a SAXException.
   * @see org.xml.sax.SAXException
   *
   * @version Alpha
   * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
   */
  public ProcessorFactoryException (String message, Exception e)
  {
    super(message, e);
  }
}
