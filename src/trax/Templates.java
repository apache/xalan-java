// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import org.apache.xml.serialize.OutputFormat;

/**
 * The Templates object is the runtime representation of compiled 
 * transformation instructions.  Templatess must be threadsafe for a given instance 
 * over multiple threads concurrently, and are generally meant to 
 * be used multiple times in a given session.
 *
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>newTransformer</h4></dt>
 *    <dd>Is newTransformer the right way to create a transformer?  The alternative might 
 *        be to have a factory method in the Transformer class that takes as an argument 
 *        a Templates object.</dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public interface Templates
{
  /**
   * Create a new transformation context for this Templates object.
   */
  Transformer newTransformer();
  
  /**
   * Get the properties for xsl:output.  The object returned will 
   * be a clone of the internal values, and thus it can be mutated 
   * without mutating the Templates object, and then handed in to 
   * the process method.
   * @return A OutputProperties object that may be mutated.
   * 
   * @see org.xml.serialize.OutputFormat
   */
  OutputFormat getOutputFormat();
}
