package javax.xml.trax;

import java.io.IOException;

import java.util.Properties;
import java.util.Enumeration;

/**
 * A TransformerFactory instance creates Transformer and Template 
 * objects.
 * 
 * A particular TransformerFactory is "plugged" into the platform via 
 * Processor in one of two ways: 1) as a platform default, 
 * and 2) through external specification by a system property named 
 * "javax.xml.trax.xslt" obtained using java.lang.System.getProperty().  
 * Or, a given application may set a platform default factory name, which
 * will be used if no system property is found. 
 * A derived class with the specified name shall implement a 
 * public no-args constructor used by the base abstract class to 
 * create a concrete instance of this class.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public abstract class TransformerFactory
{
  /**
   * The name of the default concrete class to be used.
   */
  private static String platformDefaultFactoryName = null;

  /**
   * Set the name of the default concrete class to be used.
   */
  private static String PLATFORMDEFAULTFACTORYPROP 
    = "javax.xml.trax.processor.xslt";

  /**
   * Set the name of the default concrete class to be used.
   * 
   * @param classname Full classname of concrete implementation
   * of the abstract TransformerFactory class.
   */
  public static void setPlatformDefault(String classname)
  {
    platformDefaultFactoryName = classname;
  }

  /**
   * Obtain a new instance of a TransformerFactory object.
   *
   * A particular TransformerFactory is "plugged" into the platform via 
   * Processor in one of two ways: 1) as a platform default, 
   * and 2) through external specification by a system property named 
   * "javax.xml.trax.xslt" obtained using java.lang.System.getProperty().  
   * Or, a given application may set a platform default factory name, which
   * will be used if no system property is found. 
   * A derived class with the specified name shall implement a 
   * public no-args constructor used by the base abstract class to 
   * create a concrete instance of this class.
   * 
   * @return Concrete instance of an Processor object.
   *
   * @throws TransformerFactoryException if, for any reason, an
   * instance can't be created.
   */
  public static TransformerFactory newInstance()
          throws TransformerFactoryException
  {
    TransformerFactory factory = null;

    try
    {
      String factoryKey = PLATFORMDEFAULTFACTORYPROP;
      String factoryName = System.getProperty(factoryKey);

      if (null == factoryName)
      {
        factoryName = platformDefaultFactoryName;
      }

      if (null == factoryName)
      {
        throw new TransformerFactoryException("Can't find system property: "
                                            + factoryKey);
      }

      Class factoryClass = Class.forName(factoryName);

      factory = (TransformerFactory) factoryClass.newInstance();
    }
    catch (java.lang.IllegalAccessException iae)
    {
      throw new TransformerFactoryException(
        "Transformation Processor can not be accessed!", iae);
    }
    catch (java.lang.InstantiationException ie)
    {
      throw new TransformerFactoryException(
        "Not able to create Transformation Processor!", ie);
    }
    catch (java.lang.ClassNotFoundException cnfe)
    {
      throw new TransformerFactoryException(
        "Transformation Processor not found!", cnfe);
    }

    return factory;
  }

  /**
   * Process the source into a Transformer object.  Care must 
   * be given to know that this object can not be used concurrently 
   * in multiple threads.
   *
   * @param source An object that holds a URL, input stream, etc.
   * 
   * @return A Transformer object capable of 
   * being used for transformation purposes in a single thread.
   *
   * @exception TransformerFactoryException May throw this during the parse when it
   *            is constructing the Templates object and fails.
   */
  public abstract Transformer newTransformer(Source source) 
    throws TransformerFactoryException;

  /**
   * Create a new Transformer object that performs a copy 
   * of the source to the result.
   *
   * @param source An object that holds a URL, input stream, etc.
   * 
   * @return A Transformer object capable of 
   * being used for transformation purposes in a single thread.
   *
   * @return
   * @exception TransformerFactoryException May throw this during 
   *            the parse when it is constructing the 
   *            Templates object and it fails.
   */
  public abstract Transformer newTransformer() 
    throws TransformerFactoryException;
  
  /**
   * Process the source into a Templates object, which is likely 
   * a compiled representation of the source. This Templates object 
   * may then be used concurrently across multiple threads.  Creating 
   * a Templates object allows the TransformerFactory to do detailed 
   * performance optimization of transformation instructions, without 
   * penalizing runtime transformation.
   *
   * @param source An object that holds a URL, input stream, etc.
   * @returns A Templates object capable of being used for transformation purposes.
   *
   * @return
   * @exception TransformerFactoryException May throw this during the parse when it
   *            is constructing the Templates object and fails.
   */
  public abstract Templates newTemplates(Source source) 
    throws TransformerFactoryException;

  //======= CONFIGURATION METHODS =======

  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.</p>
   * @param name The feature name, which is a fully-qualified
   *        URI.
   * @return The current state of the feature (true or false).
   */
  public abstract boolean getFeature(String name);

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

  /**
   * Set an object that will be used to resolve URIs used in
   * xsl:import, etc.  This will be used as the default for the
   * transformation.
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public abstract void setURIResolver(URIResolver resolver);

  /**
   * Set an object that will be used to resolve URIs used in
   * xsl:import, etc.  This will be used as the default for the
   * transformation.
   *
   * @return
   */
  public abstract URIResolver getURIResolver();
}
