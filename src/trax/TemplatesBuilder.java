// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

/**
 * This is a SAX ContentHandler that may be used to process SAX 
 * events into an Templates objects.  This is an abstract class 
 * instead of an interface, so it can be a ContentHandler object, 
 * for passing into the JAXP SAXParser interface.
 * 
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>Should Processor derive from org.xml.sax.ContentHandler?</h4></dt>
 *    <dd>Instead of requesting an object from the Processor class, should 
 *        the Processor class simply derive from org.xml.sax.ContentHandler?</dd>
 *    <dt><h4>ContentHandler vs. ContentHandler</h4></dt>
 *    <dd>I don't think I would use ContentHandler at all, except that JAXP uses it.  
 *        Maybe we should go back to using ContentHandler?</dd>
 * </dl>
 */
public interface TemplatesBuilder extends org.xml.sax.ContentHandler
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
   */
  public Templates getTemplates() 
    throws TransformException;
  
  /**
   * Set the base ID (URL or system ID) for the stylesheet 
   * created by this builder.  This must be set in order to 
   * resolve relative URLs in the stylesheet.
   * @param baseID Base URL for this stylesheet.
   */
  public void setBaseID(String baseID);
}
