// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

//import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.w3c.dom.Node;



/**
 * <p><i>This version of URIResolver reflects the proposal made by Michael Kay to revise
 * the interface as defined in TRAX 0.6.</i></p>
 *
 * <p>An interface that can be called by the processor to for turning the
 * URIs used in document() and xsl:import etc into an InputSource or a 
 * Node if the processor supports the "http://xml.org/trax/features/dom/input" feature.</p>
 *
 * Node that the URIResolver is stateful (it remembers the most recent URI) so separate
 * instances must be used in each thread.
 * 
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
 
public interface URIResolver
{
  /**
   * This will be called by the processor when it encounters 
   * an xsl:include, xsl:import, or document() function, if it needs 
   * a DOM tree. The URIResolver must be prepared to return either a
   * DOM tree, or a SAX InputSource, or both. This method must not be called
   * unless setURI() has been called first.
   * 
   * @param inputSource The value returned from the EntityResolver.
   * @returns a DOM node that represents the resolution of the URI 
   * to a tree, if the
   * URI resolver is capable of returning a DOM Node; or null otherwise.
   */
  public Node getDOMNode (InputSource inputSource) 
    throws TransformException;

  /**
   * This method returns the SAX2 parser to use with the InputSource 
   * obtained from this URI.
   * It may return null if any SAX2-conformant XML parser can be used,
   * or if getInputSource() will also return null. The parser must 
   * be free for use (i.e.
   * not currently in use for another parse().
   * 
   * @param inputSource The value returned from the EntityResolver.
   * @returns a SAX2 parser to use with the InputSource.
   */
  public XMLReader getXMLReader(InputSource inputSource) 
    throws TransformException;
}
