/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.xdm;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Properties;
import java.util.Enumeration;

import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

/**
 * A XDMManager instance can be used to create and manage XML
 * Data Model (XDM) instances.
 *
 * <p>The system property that determines which Factory implementation
 * to create is named "org.apache.xml.utils.XDMFactory". This
 * property names a concrete subclass of the XDMFactory abstract
 *  class. If the property is not defined, a platform default is be used.</p>
 *
 * <p>An instance of this class <emph>must</emph> be safe to use across
 * thread instances.  It is expected that a client will create a single instance
 * of a XDMManager to use across multiple threads.  This will allow sharing
 * of XDMs across multiple processes.</p>
 *
 * <p>Note: this class is incomplete right now.  It will be pretty much
 * modeled after javax.xml.transform.TransformerFactory in terms of its
 * factory support.</p>
 *
 * <p>State: In progress!!</p>
 */
public abstract class XDMManager
{

  /** The default property name to load the manager. 
   * %REVIEW% Are we ever really going to plug in other managers?
   * */
  private static final String defaultPropName =
    "org.apache.xml.xdm.XDMManager";

  /**
   * Factory for creating XMLString objects.
   *  %TBD% Make this set by the caller.
   */
  protected XMLStringFactory m_xsf = null;

  /**
   * Default constructor is protected on purpose.
   */
  protected XDMManager(){}

  /**
   * Get the XMLStringFactory used for the XDMs.
   *
   * @return a valid XMLStringFactory object, or null if it hasn't been set yet.
   */
  public XMLStringFactory getXMLStringFactory()
  {
    return m_xsf;
  }

  /**
   * Set the XMLStringFactory used for the XDMs. 
   * Used to be public, but I don't think we really
   * want folks mucking with it.
   * */
  protected void setXMLStringFactory(XMLStringFactory xsf)
  {
    m_xsf = xsf;
  }

  /**
   * Obtain a new instance of a <code>XDMManager</code>.
   * This static method creates a new factory instance
   * This method uses the following ordered lookup procedure to determine
   * the <code>XDMManager</code> implementation class to
   * load:
   * <ul>
   * <li>
   * Use the <code>org.apache.xml.xdm.XDMManager</code> system
   * property.
   * </li>
   * <li>
   * Use the JAVA_HOME(the parent directory where jdk is
   * installed)/lib/jaxp.properties for a property file that contains the
   * name of the implementation class keyed on the same value as the
   * system property defined above.
   * </li>
   * <li>
   * Use the Services API (as detailed in the JAR specification), if
   * available, to determine the classname. The Services API will look
   * for a classname in the file
   * <code>META-INF/services/javax.xml.parsers.XDMManager</code>
   * in jars available to the runtime.
   * </li>
   * <li>
   * Use the default <code>XDMManager</code> classname, which is
   * <code>org.apache.xml.xdm.ref.XDMManagerDTM</code>.
   * </li>
   * </ul>
   *
   * Once an application has obtained a reference to a <code>
   * XDMManager</code> it can use the factory to configure
   * and obtain parser instances.
   *
   * @return new XDMManager instance, never null.
   *
   * @throws XDMConfigurationException
   * if the implementation is not available or cannot be instantiated.
   */
  public static XDMManager newInstance(XMLStringFactory xsf) 
           throws XDMConfigurationException
  {

    String classname = findFactory(defaultPropName,
                                   "org.apache.xml.xdm.ref.XDMManagerDTM");

    if (classname == null)
    {
      throw new XDMConfigurationException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_DEFAULT_IMPL, null)); //"No default implementation found");
    }

    XDMManager factoryImpl;

    try
    {
      Class clazz = Class.forName(classname);

      factoryImpl = (XDMManager) clazz.newInstance();
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new XDMConfigurationException(cnfe);
    }
    catch (IllegalAccessException iae)
    {
      throw new XDMConfigurationException(iae);
    }
    catch (InstantiationException ie)
    {
      throw new XDMConfigurationException(ie);
    }
    
    factoryImpl.setXMLStringFactory(xsf);

    return factoryImpl;
  }

  /**
   * Get an instance of a XDM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the XDMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * 
   * (More parameters may eventually need to be added for error handling
   * and entity resolution, and to better control selection of implementations.)
   *
   * @param source the specification of the source object, which may be null,
   *               in which case it is assumed that node construction will take
   *               by some other means.
   * @param unique true if the returned XDM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   * @param incremental true if the XDM should be built incrementally, if
   *                    possible.
   * @param doIndexing true if the caller considers it worth it to use 
   *                   indexing schemes.
   *
   * @return a non-null XDMCursor pointing to the document's root
   * node.
   */
  public abstract XDMCursor getXDM(javax.xml.transform.Source source,
                             boolean unique, XDMWSFilter whiteSpaceFilter,
                             boolean incremental, boolean doIndexing);

  /**
   * Given a W3C DOM node, try and return a XDM Cursor. This will
   * attempt to return an already-loaded XDM.
   * 
   * %REVIEW% Should this wrap an XDM around the Node if one does
   * not already exit?
   * 
   * %REVIEW% Should we just tell folks to call getXDM() with
   * a DOMSource and unique=false? Or does that set a different
   * root node...?
   * 
   * Note: Performance of this call may be suboptimal.
   *
   * @param node Non-null reference to a DOM node.
   *
   * @return a valid XDMCursor pointing to the XPath Data Model
   * view of the specified node.
   */
  public abstract XDMCursor getXDMCursorFromNode(org.w3c.dom.Node node);

  /**
   * Creates an XDM representing a <code>DocumentFragment</code>
   * containing a single <code>Text</code> node.
   * 
   * Rationalle: DTMManager.createDocumentFragment was being
   * used only to create fragments that contain
   * a single text node (by pairing it with addTextNode).
   * Since XDMs/DTMs aren't really supposed to be mutable,
   * I felt it was better to encapsulate that concept.
   * 
   * @return an XDM "SELF" Cursor pointing to the new document
   * fragment node.
   */
  public abstract XDMCursor createTextFragment(String text);

  /**
   * Release an XDM either to a lru pool, or completely remove reference.
   * XDMs without system IDs are always hard deleted.
   * 
   * %REVIEW% Do we need to actively delete,
   * or should XDM just use finalizer and instance counting?
   * DTM version was called from
   * XRTreeFrag, sql.XConnction, XPathContext... 
   * 
   * State: experimental.
   *
   * @param xdm Cursor pointing to any node within the XDM to be released.
   * @param shouldHardDelete True if the XDM should be removed no matter what.
   * @return true if the XDM was removed, false if it was put back in a lru pool.
   */
  public abstract boolean release(XDMCursor xdm, boolean shouldHardDelete);

  /* Flag indicating whether an incremental transform is desired */
  public static boolean m_incremental = false;  
  
  /**
   * Set a flag indicating whether an incremental transform is desired 
   * @param incremental boolean to use to set m_incremental.
   *
   */
  public synchronized static boolean getIncremental()
  {
    return m_incremental;  
  }
  
  /**
   * Set a flag indicating whether an incremental transform is desired 
   * @param incremental boolean to use to set m_incremental.
   *
   */
  public synchronized static void setIncremental(boolean incremental)
  {
    m_incremental = incremental;  
  }
  
  

  // -------------------- private methods --------------------

  /**
   * Avoid reading all the files when the findFactory
   * method is called the second time (cache the result of
   * finding the default impl).
   */
  private static String foundFactory = null;

  /**
   * Temp debug code - this will be removed after we test everything
   */
  private static boolean debug;

  static
  {
    try
    {
      debug = System.getProperty("xdm.debug") != null;
    }
    catch (SecurityException ex){}
  }

  /**
   * Private implementation method - will find the implementation
   * class in the specified order.
   *
   * @param factoryId   Name of the factory interface.
   * @param xmlProperties Name of the properties file based on JAVA/lib.
   * @param defaultFactory Default implementation, if nothing else is found.
   *
   * @return The factory class name.
   */
  private static String findFactory(String factoryId, String defaultFactory)
  {

    // Use the system property first
    try
    {
      String systemProp = null;

      try
      {
        systemProp = System.getProperty(factoryId);
      }
      catch (SecurityException se){}

      if (systemProp != null)
      {
        if (debug)
        {
          System.err.println("XDM: found system property" + systemProp);
        }

        return systemProp;
      }
    }
    catch (SecurityException se){}

    if (foundFactory != null)
    {
      return foundFactory;
    }

    // try to read from $java.home/lib/jaxp.properties
    try
    {
      String javah = System.getProperty("java.home");
      String configFile = javah + File.separator + "lib" + File.separator
                          + "jaxp.properties";
      File f = new File(configFile);

      if (f.exists())
      {
        Properties props = new Properties();

        props.load(new FileInputStream(f));

        foundFactory = props.getProperty(factoryId);

        if (debug)
        {
          System.err.println("XDM: found java.home property " + foundFactory);
        }

        if (foundFactory != null)
        {
          return foundFactory;
        }
      }
    }
    catch (Exception ex)
    {
      if (debug)
      {
        ex.printStackTrace();
      }
    }

    String serviceId = "META-INF/services/" + factoryId;

    // try to find services in CLASSPATH
    try
    {
      ClassLoader cl = XDMManager.class.getClassLoader();
      InputStream is = null;

      if (cl == null)
      {
        is = ClassLoader.getSystemResourceAsStream(serviceId);
      }
      else
      {
        is = cl.getResourceAsStream(serviceId);
      }

      if (is != null)
      {
        if (debug)
        {
          System.err.println("XDM: found  " + serviceId);
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));

        foundFactory = rd.readLine();

        rd.close();

        if (debug)
        {
          System.err.println("XDM: loaded from services: " + foundFactory);
        }

        if ((foundFactory != null) &&!"".equals(foundFactory))
        {
          return foundFactory;
        }
      }
    }
    catch (Exception ex)
    {
      if (debug)
      {
        ex.printStackTrace();
      }
    }

    return defaultFactory;
  }
}
