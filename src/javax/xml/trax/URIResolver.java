package javax.xml.trax;

/**
 * <p>An interface that can be called by the processor to for turning the
 * URIs used in document() and xsl:import etc into an Source object.
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
   * @param href An href attribute, which may be relative or absolute.
   * @param base The base URI in effect when the href attribute was encountered.
   *
   * @return A non-null Source object.
   *
   * @throws TransformerException
   */
  public Source resolve(String href, String base) throws TransformerException;
}
