// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/**
 * This exception serves as a root exception of TRaX exception, and 
 * is thrown in raw form when an exceptional condition occurs in the 
 * Processor object.
 *
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>Abstract exception root?</h4></dt>
 *    <dd>Should the root TRaX exception be abstract?</dd>
 *    <dt><h4>Derive from SAXException?</h4></dt>
 *    <dd>Keith Visco writes: I don't think these exceptions should extend  
 *        SAXException, but could nest a SAXException if necessary.</dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class ProcessorException extends SAXParseException
{
  //////////////////////////////////////////////////////////////////////
  // Constructors.
  //////////////////////////////////////////////////////////////////////
    
  /**
   * Create a new ProcessorException from a message and a Locator.
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
  public ProcessorException (String message, Locator locator) 
  {
    super(message, locator);
  }
  
  
  /**
   * Wrap an existing exception in a ProcessorException.
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
  public ProcessorException (String message, Locator locator,
                             Exception e) 
  {
    super( message, locator, e);
  }
  
  /**
   * Wrap an existing exception in a ProcessorException.
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
  public ProcessorException (String message, Exception e) 
  {
    super( "TRaX Processor Exception", new LocatorImpl(), e);
  }
  
  /**
   * Create a new ProcessorException.
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
  public ProcessorException (String message, String publicId, String systemId,
                             int lineNumber, int columnNumber)
  {
    super(message, publicId, systemId, lineNumber, columnNumber);
  }
  
  
  /**
   * Create a new ProcessorException with an embedded exception.
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
  public ProcessorException (String message, String publicId, String systemId,
                             int lineNumber, int columnNumber, Exception e)
  {
    super(message, publicId, systemId, lineNumber, columnNumber, e);
  }
}
