// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;

/**
 * An interface that can be called by the processor to for turning the
 * URIs used in document() and xsl:import etc into an InputSource or a 
 * Node if the processor supports the "http://xml.org/trax/features/dom/input" feature.
 * 
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>resolveURItoDOMTree</h4></dt>
 *    <dd>Surely it's the URIResolver that
 *    knows it wants to supply a DOM node to satisfy the URI, how is the processor
 *    supposed to know this? Perhaps it would be better to have
 *    URIResolver.getURItype() which returns "inputSource" or "DOM Node", and the
 *    processor then calls resolveURI() or resolveURItoNode() as appropriate?
 *    (It's still not a very pretty design, it doesn't extend nicely to return
 *    another source of SAX events, e.g. an SQL query).</dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public interface URIResolver
{
  /**
   * This will be called by the processor when it encounters 
   * an xsl:include, xsl:import, or document() function.
   * 
   * @param base The base URI that should be used.
   * @param uri Value from an xsl:import or xsl:include's href attribute, 
   * or a URI specified in the document() function.
   * @returns a InputSource that can be used to process the resource.
   */
  public InputSource resolveURI (String base, String uri)
    throws TransformException, IOException;

  /**
   * This will be called by the processor when it encounters 
   * an xsl:include, xsl:import, or document() function, if it needs 
   * a DOM tree.
   * 
   * @param base The base URI that should be used.
   * @param uri Value from an xsl:import or xsl:include's href attribute, 
   * or a URI specified in the document() function.
   * @returns a DOM node that represents the resolution of the URI to a tree.
   */
  public Node resolveURIToDOMTree (String base, String uri)
    throws TransformException, IOException;
}
