package javax.xml.trax;

/**
 * The TransformerFactoryException is a type of ProcessorException that
 * is thrown when a configurable factory object can not
 * be created.
 *
 * <h3>Open issues:</h3>
 * <dl> *    <dt><h4>No open issues are known for this class</h4></dt>
 *    <dd></dd>
 * </dl>
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class TransformerFactoryException extends Exception
{
  SourceLocator locator;
  Exception containedException;

  /**
   * Create a new TransformerFactoryException.
   *
   * @param message The error or warning message.
   */
  public TransformerFactoryException(String message)
  {
    super(message);
    this.containedException = null;
    this.locator = null;
  }

  /**
   * Create a new TransformerFactoryException wrapping an existing exception.
   *
   * @param e The exception to be wrapped.
   */
  public TransformerFactoryException(Exception e)
  {
    super("TRaX Transform Exception");
    this.containedException = e;
    this.locator = null;
  }

  /**
   * Wrap an existing exception in a TransformerFactoryException.
   *
   * <p>This is used for throwing processor exceptions before
   * the processing has started.</p>
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param e Any exception
   */
  public TransformerFactoryException(String message, Exception e)
  {
    super("TRaX Transform Exception");
    this.containedException = e;
    this.locator = null;
  }

  /**
   * Create a new TransformerFactoryException from a message and a Locator.
   *
   * <p>This constructor is especially useful when an application is
   * creating its own exception from within a DocumentHandler
   * callback.</p>
   *
   * @param message The error or warning message.
   * @param locator The locator object for the error or warning.
   */
  public TransformerFactoryException(String message, SourceLocator locator)
  {
    super(message);
    this.containedException = null;
    this.locator = locator;
  }

  /**
   * Wrap an existing exception in a TransformerFactoryException.
   *
   * @param message The error or warning message, or null to
   *                use the message from the embedded exception.
   * @param locator The locator object for the error or warning.
   * @param e Any exception
   */
  public TransformerFactoryException(String message, SourceLocator locator,
                            Exception e)
  {
    super(message);
    this.containedException = e;
    this.locator = locator;
  }
}
