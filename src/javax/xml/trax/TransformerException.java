package javax.xml.trax;

import javax.xml.trax.SourceLocator;

/**
 * This simply subclasses the TransformerException for the purposes
 * of being able to be caught in a catch clause.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class TransformerException extends Exception
{
  SourceLocator locator;
  Exception containedException;

  /**
   * Create a new TransformerException.
   *
   * @param message The error or warning message.
   */
  public TransformerException(String message)
  {
    super(message);
    this.containedException = null;
    this.locator = null;
  }

  /**
   * Create a new TransformerException wrapping an existing exception.
   *
   * @param e The exception to be wrapped.
   */
  public TransformerException(Exception e)
  {
    super("TRaX Transform Exception");
    this.containedException = e;
    this.locator = null;
  }

  /**
   * Wrap an existing exception in a TransformerException.
   *
   * <p>This is used for throwing processor exceptions before
   * the processing has started.</p>
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param e Any exception
   */
  public TransformerException(String message, Exception e)
  {
    super("TRaX Transform Exception");
    this.containedException = e;
    this.locator = null;
  }

  /**
   * Create a new TransformerException from a message and a Locator.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback.</p>
   *
   * @param message The error or warning message.
   * @param locator The locator object for the error or warning.
   */
  public TransformerException(String message, SourceLocator locator)
  {
    super(message);
    this.containedException = null;
    this.locator = locator;
  }

  /**
   * Wrap an existing exception in a TransformerException.
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param locator The locator object for the error or warning.
   * @param e Any exception
   */
  public TransformerException(String message, SourceLocator locator,
                            Exception e)
  {
    super(message);
    this.containedException = e;
    this.locator = locator;
  }

}
