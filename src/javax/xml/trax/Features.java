package javax.xml.trax;

/**
 * This class defines feature URLs that are supported by 
 * the TrAX subpackages, such as sax, dom, etc.  Using these 
 * values allows the transformer to do an identity comparison 
 * instead of a lexical comparison, which can greatly improve 
 * the performance of TransformerFactory#getFeature.
 */
public class Features
{
  /**
   * If TransformerFactory#getFeature returns true with this value 
   * passed as a parameter, the transformer supports the interfaces 
   * found in the javax.xml.trax.sax package.
   */
  public static final String SAX="http://xml.org/trax/features/sax";
  
  /**
   * If TransformerFactory#getFeature returns true with this value 
   * passed as a parameter, the transformer supports the interfaces 
   * found in the javax.xml.trax.dom package.
   */
  public static final String DOM="http://xml.org/trax/features/dom";
  
  /**
   * If TransformerFactory#getFeature returns true with this value 
   * passed as a parameter, the transformer supports the interfaces 
   * found in the javax.xml.trax.stream package.
   */
  public static final String STREAM="http://xml.org/trax/features/stream";
  
}
