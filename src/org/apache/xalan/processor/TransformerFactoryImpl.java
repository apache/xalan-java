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
package org.apache.xalan.processor;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;

import org.w3c.dom.Node;

import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.TransformerIdentityImpl;
import org.apache.xalan.transformer.TrAXFilter;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.ErrorListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import java.util.Properties;
import java.util.Enumeration;

/**
 * The TransformerFactoryImpl, which implements the TRaX TransformerFactory
 * interface, processes XSLT stylesheets into a Templates object
 * (a StylesheetRoot).
 */
public class TransformerFactoryImpl extends SAXTransformerFactory
{

  /** The Xalan properties file. */
  public static String XSLT_PROPERTIES =
    "/org/apache/xalan/res/XSLTInfo.properties";

  /** Flag tells if the properties file has been loaded to the system */
  private static boolean isInited = false;

  /**
   * Constructor TransformerFactoryImpl
   *
   */
  public TransformerFactoryImpl()
  {
    loadPropertyFileToSystem(XSLT_PROPERTIES);
  }

  /**
   * Retrieve a propery bundle from a specified file and load it
   * int the System properties.
   *
   * @param file The properties file to be processed.
   */
  private static void loadPropertyFileToSystem(String file)
  {

    if (false == isInited)
    {
      try
      {
        InputStream is;

        try
        {
          Properties props = new Properties();

          is = Process.class.getResourceAsStream(file);

          // get a buffered version
          BufferedInputStream bis = new BufferedInputStream(is);

          props.load(bis);  // and load up the property bag from this
          bis.close();  // close out after reading

          // OK, now we only want to set system properties that 
          // are not already set.
          Properties systemProps = System.getProperties();
          Enumeration propEnum = props.propertyNames();

          while (propEnum.hasMoreElements())
          {
            String prop = (String) propEnum.nextElement();

            if (!systemProps.containsKey(prop))
              systemProps.put(prop, props.getProperty(prop));
          }

          System.setProperties(systemProps);

          isInited = true;
        }
        catch (Exception ex){}
      }
      catch (SecurityException se)
      {

        // In this case the caller is required to have 
        // the needed attributes already defined.
      }
    }
  }

public javax.xml.transform.Templates processFromNode(Node node)
          throws TransformerConfigurationException
  {

    try
    {
      TemplatesHandler builder = newTemplatesHandler();
      TreeWalker walker = new TreeWalker(builder, new org.apache.xpath.DOM2Helper());

      walker.traverse(node);

      return builder.getTemplates();
    }
    catch (org.xml.sax.SAXException se)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(se));
        }
        catch (TransformerException ex)
        {
          throw new TransformerConfigurationException(ex);
        }

        return null;
      }
      else

        // Should remove this later... but right now diagnostics from 
        // TransformerConfigurationException are not good.
        // se.printStackTrace();
        throw new TransformerConfigurationException("processFromNode failed",
                                                    se);
    }
    catch (TransformerConfigurationException tce)
    {
      // Assume it's already been reported to the error listener.
      throw tce;
    }
    catch (TransformerException tce)
    {
      // Assume it's already been reported to the error listener.
      throw new TransformerConfigurationException(tce.getMessage(), tce);
    }
    catch (Exception e)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(e));
        }
        catch (TransformerException ex)
        {
          throw new TransformerConfigurationException(ex);
        }

        return null;
      }
      else

        // Should remove this later... but right now diagnostics from 
        // TransformerConfigurationException are not good.
        // se.printStackTrace();
        throw new TransformerConfigurationException("processFromNode failed",
                                                    e);
    }
  }

  /**
   * The systemID that was specified in
   * processFromNode(Node node, String systemID).
   */
  private String m_DOMsystemID = null;

  /**
   * The systemID that was specified in
   * processFromNode(Node node, String systemID).
   *
   * @return The systemID, or null.
   */
  String getDOMsystemID()
  {
    return m_DOMsystemID;
  }

  /**
   * Process the stylesheet from a DOM tree, if the
   * processor supports the "http://xml.org/trax/features/dom/input"
   * feature.
   *
   * @param node A DOM tree which must contain
   * valid transform instructions that this processor understands.
   * @param systemID The systemID from where xsl:includes and xsl:imports
   * should be resolved from.
   *
   * @return A Templates object capable of being used for transformation purposes.
   *
   * @throws TransformerConfigurationException
   */
  javax.xml.transform.Templates processFromNode(Node node, String systemID)
          throws TransformerConfigurationException
  {

    m_DOMsystemID = systemID;

    return processFromNode(node);
  }

  /**
   * Get InputSource specification(s) that are associated with the
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches
   * the given criteria.  Note that it is possible to return several stylesheets
   * that match the criteria, in which case they are applied as if they were
   * a list of imports or cascades.
   *
   * <p>Note that DOM2 has it's own mechanism for discovering stylesheets.
   * Therefore, there isn't a DOM version of this method.</p>
   *
   *
   * @param source The XML source that is to be searched.
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   *
   * @return A Source object capable of being used to create a Templates object.
   *
   * @throws TransformerConfigurationException
   */
  public Source getAssociatedStylesheet(
          Source source, String media, String title, String charset)
            throws TransformerConfigurationException
  {

    String baseID;
    InputSource isource = null;
    Node node = null;
    XMLReader reader = null;

    if (source instanceof DOMSource)
    {
      DOMSource dsource = (DOMSource) source;

      node = dsource.getNode();
      baseID = dsource.getSystemId();
    }
    else
    {
      isource = SAXSource.sourceToInputSource(source);
      baseID = isource.getSystemId();
    }

    // What I try to do here is parse until the first startElement
    // is found, then throw a special exception in order to terminate 
    // the parse.
    StylesheetPIHandler handler = new StylesheetPIHandler(baseID, media,
                                    title, charset);
    
    // Use URIResolver. Patch from Dmitri Ilyin 
    if (m_uriResolver != null) 
    {
      handler.setURIResolver(m_uriResolver); 
    }

    try
    {
      if (null != node)
      {
        TreeWalker walker = new TreeWalker(handler, new org.apache.xpath.DOM2Helper());

        walker.traverse(node);
      }
      else
      {

        // Use JAXP1.1 ( if possible )
        try
        {
          javax.xml.parsers.SAXParserFactory factory =
            javax.xml.parsers.SAXParserFactory.newInstance();

          factory.setNamespaceAware(true);

          javax.xml.parsers.SAXParser jaxpParser = factory.newSAXParser();

          reader = jaxpParser.getXMLReader();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex)
        {
          throw new org.xml.sax.SAXException(ex);
        }
        catch (javax.xml.parsers.FactoryConfigurationError ex1)
        {
          throw new org.xml.sax.SAXException(ex1.toString());
        }
        catch (NoSuchMethodError ex2){}
        catch (AbstractMethodError ame){}

        if (null == reader)
        {
          reader = XMLReaderFactory.createXMLReader();
        }

        // Need to set options!
        reader.setContentHandler(handler);
        reader.parse(isource);
      }
    }
    catch (StopParseException spe)
    {

      // OK, good.
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new TransformerConfigurationException(
        "getAssociatedStylesheets failed", se);
    }
    catch (IOException ioe)
    {
      throw new TransformerConfigurationException(
        "getAssociatedStylesheets failed", ioe);
    }

    return handler.getAssociatedStylesheet();
  }

  /**
   * Create a new Transformer object that performs a copy
   * of the source to the result.
   *
   * @param source An object that holds a URI, input stream, etc.
   *
   * @return A Transformer object that may be used to perform a transformation
   * in a single thread, never null.
   *
   * @throws TransformerConfigurationException May throw this during
   *            the parse when it is constructing the
   *            Templates object and fails.
   */
  public TemplatesHandler newTemplatesHandler()
          throws TransformerConfigurationException
  {
    return new StylesheetHandler(this);
  }

  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an TransformerFactory to recognize a feature name but
   * to be unable to return its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of knowing whether the underlying parser is
   * validating, for example.</p>
   *
   * @param name The feature name, which is a fully-qualified URI.
   * @return The current state of the feature (true or false).
   */
  public boolean getFeature(String name)
  {

    // Try first with identity comparison, which 
    // will be faster.
    if ((DOMResult.FEATURE == name) || (DOMSource.FEATURE == name)
            || (SAXResult.FEATURE == name) || (SAXSource.FEATURE == name)
            || (StreamResult.FEATURE == name)
            || (StreamSource.FEATURE == name)
            || (SAXTransformerFactory.FEATURE == name)
            || (SAXTransformerFactory.FEATURE_XMLFILTER == name))
      return true;
    else if ((DOMResult.FEATURE.equals(name))
             || (DOMSource.FEATURE.equals(name))
             || (SAXResult.FEATURE.equals(name))
             || (SAXSource.FEATURE.equals(name))
             || (StreamResult.FEATURE.equals(name))
             || (StreamSource.FEATURE.equals(name))
             || (SAXTransformerFactory.FEATURE.equals(name))
             || (SAXTransformerFactory.FEATURE_XMLFILTER.equals(name)))
      return true;
    else
      return false;
  }

  /**
   * Allows the user to set specific attributes on the underlying
   * implementation.
   *
   * @param name The name of the attribute.
   * @param value The value of the attribute.
   *
   * @throws IllegalArgumentException thrown if the underlying
   * implementation doesn't recognize the attribute.
   */
  public void setAttribute(String name, Object value)
          throws IllegalArgumentException
  {
    throw new IllegalArgumentException(name);
  }

  /**
   * Allows the user to retrieve specific attributes on the underlying
   * implementation.
   *
   * @param name The name of the attribute.
   * @return value The value of the attribute.
   *
   * @throws IllegalArgumentException thrown if the underlying
   * implementation doesn't recognize the attribute.
   */
  public Object getAttribute(String name) throws IllegalArgumentException
  {
    throw new IllegalArgumentException(name);
  }

  /**
   * Create an XMLFilter that uses the given source as the
   * transformation instructions.
   *
   * @param src The source of the transformation instructions.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   *
   * @throws TransformerConfigurationException
   */
  public XMLFilter newXMLFilter(Source src)
          throws TransformerConfigurationException
  {

    Templates templates = newTemplates(src);
    if( templates==null ) return null;
    
    return newXMLFilter(templates);
  }

  /**
   * Create an XMLFilter that uses the given source as the
   * transformation instructions.
   *
   * @param src The source of the transformation instructions.
   *
   * @param templates non-null reference to Templates object.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   *
   * @throws TransformerConfigurationException
   */
  public XMLFilter newXMLFilter(Templates templates)
          throws TransformerConfigurationException
  {
    try {
      return new TrAXFilter(templates);
    } catch( TransformerConfigurationException ex ) {
      if( m_errorListener != null) {
        try {
          m_errorListener.fatalError( ex );
          return null;
        } catch( TransformerException ex1 ) {
          new TransformerConfigurationException(ex1);
        }
      }
      throw ex;
    }
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the transformation
   * instructions specified by the argument.
   *
   * @param src The source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   *
   * @throws TransformerConfigurationException
   */
  public TransformerHandler newTransformerHandler(Source src)
          throws TransformerConfigurationException
  {

    Templates templates = newTemplates(src);
    if( templates==null ) return null;
    
    return newTransformerHandler(templates);
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the Templates argument.
   *
   * @param templates The source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   * @throws TransformerConfigurationException
   */
  public TransformerHandler newTransformerHandler(Templates templates)
          throws TransformerConfigurationException
  {
    try {
      TransformerImpl transformer =
        (TransformerImpl) templates.newTransformer();
      TransformerHandler th =
        (TransformerHandler) transformer.getInputContentHandler(true);

      return th;
    } catch( TransformerConfigurationException ex ) {
      if( m_errorListener != null ) {
        try {
          m_errorListener.fatalError( ex );
          return null;
        } catch (TransformerException ex1 ) {
          ex=new TransformerConfigurationException(ex1);
        }
      }
      throw ex;
    }
    
  }

//  /** The identity transform string, for support of newTransformerHandler()
//   *  and newTransformer().  */
//  private static final String identityTransform =
//    "<xsl:stylesheet " + "xmlns:xsl='http://www.w3.org/1999/XSL/Transform' "
//    + "version='1.0'>" + "<xsl:template match='/|node()'>"
//    + "<xsl:copy-of select='.'/>" + "</xsl:template>" + "</xsl:stylesheet>";
//
//  /** The identity transform Templates, built from identityTransform, 
//   *  for support of newTransformerHandler() and newTransformer().  */
//  private static Templates m_identityTemplate = null;

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result.
   *
   * @param src The source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   *
   * @throws TransformerConfigurationException
   */
  public TransformerHandler newTransformerHandler()
          throws TransformerConfigurationException
  {

//    if (null == m_identityTemplate)
//    {
//      synchronized (identityTransform)
//      {
//        if (null == m_identityTemplate)
//        {
//          StringReader reader = new StringReader(identityTransform);
//
//          m_identityTemplate = newTemplates(new StreamSource(reader));
//        }
//      }
//    }
//
//    return newTransformerHandler(m_identityTemplate);
    return new TransformerIdentityImpl();
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
   * @throws TransformerConfigurationException May throw this during the parse when it
   *            is constructing the Templates object and fails.
   */
  public Transformer newTransformer(Source source)
          throws TransformerConfigurationException
  {
    try {
      Templates tmpl=newTemplates( source );
      /* this can happen if an ErrorListener is present and it doesn't
         throw any exception in fatalError. 
         The spec says: "a Transformer must use this interface
         instead of throwing an exception" - the newTemplates() does
         that, and returns null.
      */
      if( tmpl==null ) return null;
      return tmpl.newTransformer();
    } catch( TransformerConfigurationException ex ) {
      if( m_errorListener != null ) {
        try {
          m_errorListener.fatalError( ex );
          return null;
        } catch( TransformerException ex1 ) {
          ex=new TransformerConfigurationException( ex1 );
        }
      }
      throw ex;
    }
  }

  /**
   * Create a new Transformer object that performs a copy
   * of the source to the result.
   *
   * @param source An object that holds a URL, input stream, etc.
   *
   * @return A Transformer object capable of
   * being used for transformation purposes in a single thread.
   *
   * @throws TransformerConfigurationException May throw this during
   *            the parse when it is constructing the
   *            Templates object and it fails.
   */
  public Transformer newTransformer() throws TransformerConfigurationException
  {

//    if (null == m_identityTemplate)
//    {
//      synchronized (identityTransform)
//      {
//        if (null == m_identityTemplate)
//        {
//          StringReader reader = new StringReader(identityTransform);
//
//          m_identityTemplate = newTemplates(new StreamSource(reader));
//        }
//      }
//    }
//
//    return m_identityTemplate.newTransformer();
      return new TransformerIdentityImpl();
  }

  /**
   * Process the source into a Templates object, which is likely
   * a compiled representation of the source. This Templates object
   * may then be used concurrently across multiple threads.  Creating
   * a Templates object allows the TransformerFactory to do detailed
   * performance optimization of transformation instructions, without
   * penalizing runtime transformation.
   *
   * @param source An object that holds a URL, input stream, etc.
   * @return A Templates object capable of being used for transformation purposes.
   *
   * @throws TransformerConfigurationException May throw this during the parse when it
   *            is constructing the Templates object and fails.
   */
  public Templates newTemplates(Source source)
          throws TransformerConfigurationException
  {

    TemplatesHandler builder = newTemplatesHandler();
    String baseID = source.getSystemId();

    if (null == baseID)
    {
      try
      {
        String currentDir = System.getProperty("user.dir");

        baseID = "file:///" + currentDir + java.io.File.separatorChar
                 + source.getClass().getName();
      }
      catch (SecurityException se)
      {

        // For untrusted applet case, user.dir is outside the sandbox 
        //  and not accessible: just leave baseID as null (-sb & -sc)
      }
    }
    else
    {
      try
      {
        baseID = SystemIDResolver.getAbsoluteURI(baseID);
      }
      catch (TransformerException te)
      {
        throw new TransformerConfigurationException(te);
      }
    }

    builder.setSystemId(baseID);

    if (source instanceof DOMSource)
    {
      DOMSource dsource = (DOMSource) source;
      Node node = dsource.getNode();

      if (null != node)
        return processFromNode(node, baseID);
      else
      {
        String messageStr = XSLMessages.createMessage(
          XSLTErrorResources.ER_ILLEGAL_DOMSOURCE_INPUT, null);

        throw new IllegalArgumentException(messageStr);
      }
    }

    try
    {
      InputSource isource = SAXSource.sourceToInputSource(source);
      XMLReader reader = null;

      if (source instanceof SAXSource)
        reader = ((SAXSource) source).getXMLReader();

      if (null == reader)
      {

        // Use JAXP1.1 ( if possible )
        try
        {
          javax.xml.parsers.SAXParserFactory factory =
            javax.xml.parsers.SAXParserFactory.newInstance();

          factory.setNamespaceAware(true);

          javax.xml.parsers.SAXParser jaxpParser = factory.newSAXParser();

          reader = jaxpParser.getXMLReader();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex)
        {
          throw new org.xml.sax.SAXException(ex);
        }
        catch (javax.xml.parsers.FactoryConfigurationError ex1)
        {
          throw new org.xml.sax.SAXException(ex1.toString());
        }
        catch (NoSuchMethodError ex2){}
        catch (AbstractMethodError ame){}
      }

      if (null == reader)
        reader = XMLReaderFactory.createXMLReader();

      // If you set the namespaces to true, we'll end up getting double 
      // xmlns attributes.  Needs to be fixed.  -sb
      // reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      try
      {
        reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                          true);
      }
      catch (org.xml.sax.SAXException ex)
      {

        // feature not recognized
      }

      reader.setContentHandler(builder);
      reader.parse(isource);
    }
    catch (org.xml.sax.SAXException se)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(se));
        }
        catch (TransformerException ex1)
        {
          throw new TransformerConfigurationException(ex1);
        }
      }
      else
        throw new TransformerConfigurationException(se.getMessage(), se);
    }
    catch (Exception e)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(e));

          return null;
        }
        catch (TransformerException ex1)
        {
          throw new TransformerConfigurationException(ex1);
        }
      }
      else
        throw new TransformerConfigurationException(e.getMessage(), e);
    }

    return builder.getTemplates();
  }

  /**
   * The object that implements the URIResolver interface,
   * or null.
   */
  URIResolver m_uriResolver;

  /**
   * Set an object that will be used to resolve URIs used in
   * xsl:import, etc.  This will be used as the default for the
   * transformation.
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }

  /**
   * Get the object that will be used to resolve URIs used in
   * xsl:import, etc.  This will be used as the default for the
   * transformation.
   *
   * @return The URIResolver that was set with setURIResolver.
   */
  public URIResolver getURIResolver()
  {
    return m_uriResolver;
  }

  /** The error listener.   */
  private ErrorListener m_errorListener = new DefaultErrorHandler();

  /**
   * Get the error listener in effect for the TransformerFactory.
   *
   * @return A non-null reference to an error listener.
   */
  public ErrorListener getErrorListener()
  {
    return m_errorListener;
  }

  /**
   * Set an error listener for the TransformerFactory.
   *
   * @param listener Must be a non-null reference to an ErrorListener.
   *
   * @throws IllegalArgumentException if the listener argument is null.
   */
  public void setErrorListener(ErrorListener listener)
          throws IllegalArgumentException
  {

    if (null == listener)
      throw new IllegalArgumentException("ErrorListener");

    m_errorListener = listener;
  }
}
