package javax.xml.trax.sax;

import javax.xml.trax.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * This is a SAX ContentHandler that may be used to process SAX
 * events into an Templates objects.  This is an abstract class
 * instead of an interface, so it can be a ContentHandler object,
 * for passing into the JAXP SAXParser interface.
 */
public interface TransformerHandler 
  extends  ContentHandler, LexicalHandler
{
  /**
   * When this object is used as a ContentHandler or DocumentHandler, it
   * creates a Templates object, which the caller can get once
   * the SAX events have been completed.
   * @return The stylesheet object that was created during
   * the SAX event process, or null if no stylesheet has
   * been created.
   *
   * @version Alpha
   * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
   *
   * @throws TransformerException
   */
  public Templates getTemplates() throws TransformerException;
  
  public void setResult(Result result);
}
