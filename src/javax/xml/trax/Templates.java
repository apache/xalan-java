package javax.xml.trax;

import java.util.Properties;

import javax.xml.trax.TransformerException;

/**
 * The Templates object is the runtime representation of compiled
 * transformation instructions.
 *
 * <p>Templates must be threadsafe for a given instance
 * over multiple threads concurrently, and are generally meant to
 * be used multiple times in a given session.</p>
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public interface Templates
{

  /**
   * Create a new transformation context for this Templates object.
   *
   * @return A valid non-null instance of a Transformer.
   *
   * @throws TransformerException if a Transformer can not be created.
   */
  Transformer newTransformer() throws TransformerException;

  /**
   * Get the static properties for xsl:output.  The object returned will
   * be a clone of the internal values, and thus it can be mutated
   * without mutating the Templates object, and then handed in to
   * the process method.
   *
   * <p>For XSLT, Attribute Value Templates attribute values will
   * be returned unexpanded (since there is no context at this point).</p>
   *
   * @return A OutputProperties object that may be mutated.
   */
  Properties getOutputProperties();
}
