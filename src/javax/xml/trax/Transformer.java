package javax.xml.trax;

import java.util.Properties;

/**
 * This object represents a Transformer, which can transform a 
 * source tree into a result tree.
 * 
 * <p>An object of this class can not be used concurrently over 
 * multiple threads.</p>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public abstract class Transformer
{
  /**
   * Process the source tree to the output result.
   * @param xmlSource  The input for the source tree.
   * @param outputTarget The output source target.
   *
   * @throws TransformerException
   */
  public abstract void transform(Source xmlSource, Result outputTarget)
    throws TransformerException;

  /**
   * Set the output properties for the transformation.  These
   * properties will override properties set in the templates
   * with xsl:output.
   * 
   * <p>If argument to this function is null, any properties 
   * previously set will be removed.</p>
   *
   * @param oformat A set of output properties that will be 
   * used to override any of the same properties in effect 
   * for the transformation.
   * 
   * @see org.xml.serialize.OutputFormat
   */
  public abstract void setOutputProperties(Properties oformat);

  /**
   * Set a parameter for the templates.
   * 
   * @param name The name of the parameter, 
   *             which may have a namespace URI.
   * @param value The value object.  This can be any valid Java object
   * -- it's up to the processor to provide the proper
   * coersion to the object, or simply pass it on for use
   * in extensions.
   */
  public abstract void setParameter(String name, Object value);

  /**
   * Set an object that will be used to resolve URIs used in
   * document(), etc.
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public abstract void setURIResolver(URIResolver resolver);

  /**
   * Method setProperty
   *
   * @param name
   * @param value
   *
   * @throws TransformerFactoryException
   */
  public abstract void setProperty(String name, Object value)
    throws TransformerFactoryException;

  /**
   * Method getProperty
   *
   * @param name
   *
   * @return
   *
   * @throws TransformerFactoryException
   */
  public abstract Object getProperty(String name) 
    throws TransformerFactoryException;
}
