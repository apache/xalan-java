package javax.xml.trax.sax;

import javax.xml.trax.*;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLFilter;

/**
 * Interface SAXTransformerFactory
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public abstract class SAXTransformerFactory extends TransformerFactory
{
  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result.
   * 
   * @param src The source of the transformation instructions.
   *
   * @throws TransformerFactoryException
   */
  public abstract TransformerHandler newTransformerHandler(Source src) throws TransformerFactoryException;

  /**
   * Create an XMLFilter that uses the given source as the 
   * transformation instructions.
   * 
   * @param src The source of the transformation instructions.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   */
  public abstract XMLFilter newXMLFilter(Source src);

  /**
   * Get InputSource specification(s) that are associated with the
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches
   * the given criteria.  Note that it is possible to return several stylesheets
   * that match the criteria, in which case they are applied as if they were
   * a list of imports or cascades.
   *
   * @param source
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns An array of InputSources that can be passed to processMultiple method.
   *
   * @return A Source object suitable for passing to the TransformerFactory.
   *
   * @throws TransformerFactoryException
   */
  public abstract Source getAssociatedStylesheet(
    Source source, String media, String title, String charset)
      throws TransformerFactoryException;

}
