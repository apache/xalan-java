// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.Properties;
import java.util.Enumeration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.w3c.dom.Node;

/**
 * A particular transformation Processor is "plugged" into the platform via 
 * Processor in one of two ways: 1) as a platform default, 
 * and 2) through external specification by a system property named 
 * "org.xml.trax.Processor.[type]" obtained using 
 * java.lang.System.getProperty().  The [type] part of the property specifies 
 * the language to be used, for instance, "trax.processor.xslt" would 
 * specify an XSLT processor.  This property (or platform default) 
 * names a class that is a concrete subclass of org.xml.trax.Processor.
 * The subclass shall implement a public no-args constructor used by 
 * the base abstract class to create an instance of the factory using 
 * the newInstance() method.
 * 
 * <p>The platform default is only used if no external implementation is 
 * available.</p>
 * 
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>Separate Factory?</h4></dt>
 *    <dd>Should there be a separate ProcessorFactory class, to be 
 *        more consistent with javax.xml.parsers.SAXParserFactory? Doing this 
 *        would allow this class to be an interface instead of an abstract class.</dd>
 *    <dt><h4>Separate DOM Interface?</h4></dt>
 *    <dd>Should there be a separate DOMProcessor class, instead of 
 *        having the processFromNode method?</dd>
 *    <dt><h4>XMLReader vs. Parser vs. SAXParser/DocumentBuilder</h4></dt>
 *    <dd>Currently the interfaces support XMLReader.  Should this be 
 *        javax.xml.parsers.SAXParser/javax.xml.parsers.DocumentBuilder?
 *        Or, perhaps just org.xml.sax.Parser?</dd>
 *    <dt><h4>XMLReader derivation?</h4></dt>
 *    <dd>Should this derive from XMLReader (in a similar way that Transformer 
 *        derives from XMLFilter)?</dd>
 * </dl>
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public abstract class Processor
{ 
  /**
   * Set the name of the default concrete class to be used.
   */
  private static String platformDefaultFactoryName = null;
  
  /**
   * Set the name of the default concrete class to be used.
   * @param classname Full classname of concrete implementation 
   * of org.xml.trax.Processor.
   */
  public static void setPlatformDefaultProcessor(String classname)
  {
    platformDefaultFactoryName = classname;
  }
    
  /**
   * Obtain a new instance of a Processor object.
   * @return Concrete instance of an Processor object.
   */
  public static Processor newInstance(String type)
    throws ProcessorFactoryException
  {
    Processor factory = null;
    try
    {
      String factoryKey = "trax.processor."+type;
      String factoryName = System.getProperty(factoryKey);
      
      if(null == factoryName)
      {
        loadPropertyFileToSystem(XSLT_PROPERTIES) ;
        factoryName = System.getProperty(factoryKey);
      }

      if(null == factoryName)
        factoryName = platformDefaultFactoryName;
      
      if(null == factoryName)
        throw new ProcessorFactoryException("Can't find system property: "+factoryKey, null);
      
      Class factoryClass = Class.forName(factoryName);
      factory = (Processor)factoryClass.newInstance();
    }
    catch(java.lang.IllegalAccessException iae)
    {
      throw new ProcessorFactoryException("Transformation Processor can not be accessed!", iae);
    }
    catch(java.lang.InstantiationException ie)
    {
      throw new ProcessorFactoryException("Not able to create Transformation Processor!", ie);
    }
    catch(java.lang.ClassNotFoundException cnfe)
    {
      throw new ProcessorFactoryException("Transformation Processor not found!", cnfe);
    }
    
    return factory;
  }
  
  // TODO: This needs changing to some vendor neutral location.
  public static String XSLT_PROPERTIES = "/org/apache/xalan/res/XSLTInfo.properties";
  
  /*
  * Retrieve a propery bundle from a specified file and load it 
  * int the System properties.
  * @param file The string name of the property file.  
  */
  private static void loadPropertyFileToSystem(String file) 
  {
    InputStream is;
    try
    {   		   
      Properties props = new Properties();
      is = Process.class.getResourceAsStream(file);    
      // get a buffered version
      BufferedInputStream bis = new BufferedInputStream (is);
      props.load (bis);                                     // and load up the property bag from this
      bis.close ();                                          // close out after reading
      // OK, now we only want to set system properties that 
      // are not already set.
      Properties systemProps = System.getProperties();
      Enumeration propEnum = props.propertyNames();
      while(propEnum.hasMoreElements())
      {
        String prop = (String)propEnum.nextElement();
        if(!systemProps.containsKey(prop))
          systemProps.put(prop, props.getProperty(prop));
      }
      System.setProperties(systemProps);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  /**
   * Process the source into a templates object.
   * 
   * @param source An object that holds a URL, input stream, etc.
   * @returns A Templates object capable of being used for transformation purposes.
   * @exception SAXException May throw this if it needs to create a XMLReader, 
   *            and XMLReaderFactory.createXMLReader() fails.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @exception ProcessorException May throw this during the parse when it 
   *            is constructing the Templates object and fails.
   */
  public abstract Templates process(InputSource source)
    throws ProcessorException, SAXException, IOException;

  /**
   * Process the stylesheet from a DOM tree, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.    
   * 
   * @param node A DOM tree which must contain 
   * valid transform instructions that this processor understands.
   * @returns A Templates object capable of being used for transformation purposes.
   */
  public abstract Templates processFromNode(Node node)
    throws ProcessorException;

  /**
   * Process the stylesheet from a DOM tree, if the 
   * processor supports the "http://xml.org/trax/features/dom/input" 
   * feature.    
   * 
   * @param node A DOM tree which must contain 
   * valid transform instructions that this processor understands.
   * @param systemID The systemID from where xsl:includes and xsl:imports 
   * should be resolved from.
   * @returns A Templates object capable of being used for transformation purposes.
   */
  public abstract Templates processFromNode(Node node, String systemID)
    throws ProcessorException;

  /**
   * Process a series of inputs, treating them in import or cascade 
   * order.  This is mainly for support of the getAssociatedStylesheets
   * method, but may be useful for other purposes.
   * 
   * @param sources An array of SAX InputSource objects.
   * @returns A Templates object capable of being used for transformation purposes.
   */
  public abstract Templates processMultiple(InputSource[] source)
    throws ProcessorException;

  /**
   * Get InputSource specification(s) that are associated with the 
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction 
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches 
   * the given criteria.  Note that it is possible to return several stylesheets 
   * that match the criteria, in which case they are applied as if they were 
   * a list of imports or cascades.
   * <p>Note that DOM2 has it's own mechanism for discovering stylesheets. 
   * Therefore, there isn't a DOM version of this method.</p>
   * 
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>Does the xml-stylesheet recommendation really support multiple stylesheets?</h4></dt>
   *    <dd>Mike Kay wrote:  I don't see any support in the
   *        xml-stylesheet recommendation for this interpretation of what you should do
   *        if there's more than one match. Scott Boag replies: It's in the HTML references.  
   *        But it's a bit subtle.  We talked about this at the last XSL WG F2F, and people 
   *        agreed to the multiple stylesheet stuff.  I'll try and work out the specific 
   *        references.  Probably the xml-stylesheet recommendation needs to have a note 
   *        added to it.</dd>
   * </dl>
   * 
   * @param media The media attribute to be matched.  May be null, in which 
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns An array of InputSources that can be passed to processMultiple method.
   */
  public abstract InputSource[] getAssociatedStylesheets(InputSource source,
                                                      String media, 
                                                      String title,
                                                      String charset)
    throws ProcessorException;
  
  /**
   * Get a TemplatesBuilder object that can process SAX 
   * events into a Templates object, if the processor supports the 
   * "http://xml.org/trax/features/sax/input" feature.
   * 
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>Should Processor derive from org.xml.sax.ContentHandler?</h4></dt>
   *    <dd>Instead of requesting an object from the Processor class, should 
   *        the Processor class simply derive from org.xml.sax.ContentHandler?</dd>
   * </dl>
   * @return A TemplatesBuilder object, or null if not supported.
   * @exception May throw a ProcessorException if a TemplatesBuilder 
   * can not be constructed for some reason.
   */
  public abstract TemplatesBuilder getTemplatesBuilder()
    throws ProcessorException;
  
  
  //======= CONFIGURATION METHODS =======
  
  /**
   * The XML reader to be used for the templates, 
   * and for the source documents if it is not set.
   */
  private XMLReader reader;
  
  /**
   * Set an XML parser for the templates.  This may also 
   * be used for the XML input for the source tree, if 
   * the setXMLReader method on the Transformation 
   * method is not set.
   */
  public void setXMLReader(XMLReader reader)
  {
    this.reader = reader;
  }

  /**
   * Get the XML parser used for the templates.  This may also 
   * be used for the XML input for the source tree, if 
   * the setXMLReader method on the Transformation 
   * method is not set.
   * @return Valid XMLReader object, or null if none has been set.
   */
  public XMLReader getXMLReader()
  {
    return reader;
  }

  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an Processor to recognize a feature name but
   * to be unable to return its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of knowing whether the underlying parser is
   * validating, for example.</p>
   * 
   * <h3>Open issues:</h3>
   * <dl>
   *    <dt><h4>Should getFeature be changed to hasFeature?</h4></dt>
   *    <dd>Keith Visco writes: Should getFeature be changed to hasFeature? 
   *        It returns a boolean which indicated whether the "state" 
   *        of feature is "true or false". I assume this means whether 
   *        or not a feature is supported? I know SAX is using "getFeature", 
   *        but to me "hasFeature" is cleaner.</dd>
   * </dl>
   *
   * @param name The feature name, which is a fully-qualified
   *        URI.
   * @return The current state of the feature (true or false).
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            Processor does not recognize the feature name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            Processor recognizes the feature name but 
   *            cannot determine its value at this time.
   */
  public boolean getFeature (String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotRecognizedException(name);
  }
  
  /**
   * Set the state of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an Processor to recognize a feature name but
   * to be unable to set its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of affecting whether the underlying parser is
   * validating, for example.</p>
   *
   * @param name The feature name, which is a fully-qualified
   *        URI.
   * @param state The requested state of the feature (true or false).
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            Processor does not recognize the feature name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            Processor recognizes the feature name but 
   *            cannot set the requested value.
   */
  public void setFeature (String name, boolean value)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotRecognizedException(name);
  }
  
  private URIResolver resolver;
  
  /**
   * Set an object that will be used to resolve URIs used in 
   * xsl:import, etc.  This will be used as the default for the 
   * transformation.
   * @param resolver An object that implements the URIResolver interface, 
   * or null.
   */
  public void setURIResolver(URIResolver resolver)
  {
    this.resolver = resolver;
  }

  /**
   * Set an object that will be used to resolve URIs used in 
   * xsl:import, etc.  This will be used as the default for the 
   * transformation.
   * @param resolver An object that implements the URIResolver interface, 
   * or null.
   */
  public URIResolver getURIResolver()
  {
    return resolver;
  }
  
  ErrorHandler errorHandler;
  
  /**
   * Allow an application to register an error event handler.
   *
   * <p>If the application does not register an error handler, all
   * error events reported by the SAX parser will be silently
   * ignored; however, normal processing may not continue.  It is
   * highly recommended that all SAX applications implement an
   * error handler to avoid unexpected bugs.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the SAX parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The error handler.
   * @exception java.lang.NullPointerException If the handler 
   *            argument is null.
   * @see #getErrorHandler
   */
  public void setErrorHandler (ErrorHandler handler)
  {
    if (handler == null) {
      throw new NullPointerException("Null error handler");
    }
    errorHandler = handler;
  }

  /**
   * Return the current error handler.
   *
   * @return The current error handler, or null if none
   *         has been registered.
   * @see #setErrorHandler
   */
  public ErrorHandler getErrorHandler ()
  {
    return errorHandler;
  }
  
  private EntityResolver entityResolver;
  
  /**
   * Allow an application to register an entity resolver.
   *
   * <p>If the application does not register an entity resolver,
   * the XMLReader will perform its own default resolution.</p>
   *
   * <p>Applications may register a new or different resolver in the
   * middle of a parse, and the SAX parser must begin using the new
   * resolver immediately.</p>
   *
   * @param resolver The entity resolver.
   * @exception java.lang.NullPointerException If the resolver 
   *            argument is null.
   * @see #getEntityResolver
   */
  public void setEntityResolver (EntityResolver resolver)
  {
    entityResolver = resolver;
  }


  /**
   * Return the current entity resolver.
   *
   * @return The current entity resolver, or null if none
   *         has been registered.
   * @see #setEntityResolver
   */
  public EntityResolver getEntityResolver ()
  {
    return entityResolver;
  }


}
