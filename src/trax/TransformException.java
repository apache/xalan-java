// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/**
 * This simply subclasses the TransformException for the purposes 
 * of being able to be caught in a catch clause.
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
public class TransformException extends SAXParseException
{
  /**
   * Create a new TransformException.
   *
   * @param message The error or warning message.
   * @see org.xml.sax.SAXException
   */
  public TransformException (String message) 
  {
    super(message, new LocatorImpl());
  }

  /**
   * Create a new TransformException wrapping an existing exception.
   *
   * @param e The exception to be wrapped in a SAXException.
   * @see org.xml.sax.SAXException
   */
  public TransformException (Exception e)
  {
    super("TRaX Transform Exception", new LocatorImpl(), e);
  }

  /**
   * Wrap an existing exception in a TransformException.
   *
   * <p>This is used for throwing processor exceptions before 
   * the processing has started.</p>
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param e Any exception
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, Exception e) 
  {
    super( "TRaX Transform Exception", new LocatorImpl(), e);
  }
  
  /**
   * Create a new TransformException from a message and a Locator.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback.</p>
   *
   * @param message The error or warning message.
   * @param locator The locator object for the error or warning.
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale 
   */
  public TransformException (String message, Locator locator) 
  {
    super(message, locator);
  }
  
  
  /**
   * Wrap an existing exception in a TransformException.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback, and needs to wrap an existing exception that is not a
   * subclass of SAXException.</p>
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param locator The locator object for the error or warning.
   * @param e Any exception
   * @see org.xml.sax.Locator
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, Locator locator,
                             Exception e) 
  {
    super( message, locator, e);
  }
    
  /**
   * Create a new TransformException.
   *
   * <p>This constructor is most useful for parser writers.</p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before creating the exception.</p>
   *
   * @param message The error or warning message.
   * @param publicId The public identifer of the entity that generated
   *                 the error or warning.
   * @param systemId The system identifer of the entity that generated
   *                 the error or warning.
   * @param lineNumber The line number of the end of the text that
   *                   caused the error or warning.
   * @param columnNumber The column number of the end of the text that
   *                     cause the error or warning.
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, 
                             String publicId, String systemId,
                             int lineNumber, int columnNumber)
  {
    super(message, publicId, systemId, lineNumber, columnNumber);
  }
  
  
  /**
   * Create a new TransformException with an embedded exception.
   *
   * <p>This constructor is most useful for parser writers who
   * need to wrap an exception that is not a subclass of
   * SAXException.</p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before creating the exception.</p>
   *
   * @param message The error or warning message, or null to use
   *                the message from the embedded exception.
   * @param publicId The public identifer of the entity that generated
   *                 the error or warning.
   * @param systemId The system identifer of the entity that generated
   *                 the error or warning.
   * @param lineNumber The line number of the end of the text that
   *                   caused the error or warning.
   * @param columnNumber The column number of the end of the text that
   *                     cause the error or warning.
   * @param e Another exception to embed in this one.
   * @see org.xml.sax.Parser#setLocale
   */
  public TransformException (String message, String publicId, String systemId,
                             int lineNumber, int columnNumber, Exception e)
  {
    super(message, publicId, systemId, lineNumber, columnNumber, e);
  }

}
