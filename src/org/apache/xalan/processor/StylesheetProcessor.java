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
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.w3c.dom.Node;

import org.apache.xalan.utils.TreeWalker;
import org.apache.xalan.utils.SystemIDResolver;
import org.apache.trax.Processor;
import org.apache.trax.ProcessorException;
import org.apache.trax.Templates;
import org.apache.trax.TemplatesBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import java.util.Properties;
import java.util.Enumeration;

/**
 * The StylesheetProcessor, which implements the TRaX Processor
 * interface, processes XSLT stylesheets into a Templates object
 * (a StylesheetRoot).
 */
public class StylesheetProcessor extends Processor
{

  /** NEEDSDOC Field XSLT_PROPERTIES          */
  public static String XSLT_PROPERTIES =
    "/org/apache/xalan/res/XSLTInfo.properties";

  /** NEEDSDOC Field isInited          */
  private static boolean isInited = false;

  /**
   * Constructor StylesheetProcessor
   *
   */
  public StylesheetProcessor()
  {
    loadPropertyFileToSystem(XSLT_PROPERTIES);
  }

  /*
  * Retrieve a propery bundle from a specified file and load it
  * int the System properties.
  * @param file The string name of the property file.
  */

  /**
   * NEEDSDOC Method loadPropertyFileToSystem 
   *
   *
   * NEEDSDOC @param file
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

  /**
   * Process the source into a templates object.
   *
   * @param source An object that holds a URL, input stream, etc.
   * @returns A Templates object capable of being used for transformation purposes.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws IOException
   * @throws ProcessorException
   * @throws SAXException
   */
  public Templates process(InputSource source)
          throws ProcessorException, SAXException, IOException
  {

    TemplatesBuilder builder = getTemplatesBuilder();

    builder.setBaseID(source.getSystemId());

    XMLReader reader = this.getXMLReader();

    if (null == reader)
    {
      reader = XMLReaderFactory.createXMLReader();
    }

    // If you set the namespaces to true, we'll end up getting double 
    // xmlns attributes.  Needs to be fixed.  -sb
    // reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
    try
    {
      reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                        true);
    }
    catch (SAXException ex)
    {

      // feature not recognized
    }

    reader.setContentHandler(builder);
    reader.parse(source);

    return builder.getTemplates();
  }

  /**
   * Process the stylesheet from a DOM tree, if the
   * processor supports the "http://xml.org/trax/features/dom/input"
   * feature.
   *
   * @param node A DOM tree which must contain
   * valid transform instructions that this processor understands.
   * @returns A Templates object capable of being used for transformation purposes.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ProcessorException
   */
  public Templates processFromNode(Node node) throws ProcessorException
  {

    try
    {
      TemplatesBuilder builder = getTemplatesBuilder();
      TreeWalker walker = new TreeWalker(builder);

      walker.traverse(node);

      return builder.getTemplates();
    }
    catch (SAXException se)
    {
      throw new ProcessorException("processFromNode failed", se);
    }
  }

  /**
   * The systemID that was specified in
   * processFromNode(Node node, String systemID).
   */
  private String m_DOMsystemID = "";

  /**
   * The systemID that was specified in
   * processFromNode(Node node, String systemID).
   *
   * NEEDSDOC ($objectName$) @return
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
   * @returns A Templates object capable of being used for transformation purposes.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ProcessorException
   */
  public Templates processFromNode(Node node, String systemID)
          throws ProcessorException
  {

    m_DOMsystemID = systemID;

    return processFromNode(node);
  }

  /**
   * Process a series of inputs, treating them in import or cascade
   * order.  This is mainly for support of the getAssociatedStylesheets
   * method, but may be useful for other purposes.
   *
   * @param sources An array of SAX InputSource objects.
   * @returns A Templates object capable of being used for transformation purposes.
   *
   * NEEDSDOC @param source
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ProcessorException
   */
  public Templates processMultiple(InputSource[] source)
          throws ProcessorException
  {

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.println("<?xml version='1.0'?>");
    pw.println(
      "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">");

    int n = source.length;

    for (int i = 0; i < n; i++)
    {
      pw.print("<xsl:import href=\"");
      pw.print(source[i].getSystemId());
      pw.println("\"/>");
    }

    pw.println("</xsl:stylesheet>");
    pw.close();

    String stylesheetString = sw.toString();
    StringReader reader = new StringReader(stylesheetString);

    try
    {
      return this.process(new InputSource(reader));
    }
    catch (ProcessorException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new ProcessorException("processMultiple failed", e);
    }
  }

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
   *
   * NEEDSDOC @param source
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   * @returns An array of InputSources that can be passed to processMultiple method.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws ProcessorException
   */
  public InputSource[] getAssociatedStylesheets(
          InputSource source, String media, String title, String charset)
            throws ProcessorException
  {

    // What I try to do here is parse until the first startElement
    // is found, then throw a special exception in order to terminate 
    // the parse.
    StylesheetPIHandler handler = new StylesheetPIHandler(source, media,
                                    title, charset);

    try
    {
      XMLReader reader = this.getXMLReader();

      if (null == reader)
      {
        reader = XMLReaderFactory.createXMLReader();
      }

      // Need to set options!
      reader.setContentHandler(handler);
      reader.parse(source);
    }
    catch (StopParseException spe)
    {

      // OK, good.
    }
    catch (SAXException se)
    {
      throw new ProcessorException("getAssociatedStylesheets failed", se);
    }
    catch (IOException ioe)
    {
      throw new ProcessorException("getAssociatedStylesheets failed", ioe);
    }

    return handler.getAssociatedStylesheets();
  }

  /**
   * Get a TemplatesBuilder object that can process SAX
   * events into a Templates object, if the processor supports the
   * "http://xml.org/trax/features/sax/input" feature.
   *
   * <h3>Open issues:</h3>
   * <dl>   *    <dt><h4>Should Processor derive from org.xml.sax.ContentHandler?</h4></dt>
   *    <dd>Instead of requesting an object from the Processor class, should
   *        the Processor class simply derive from org.xml.sax.ContentHandler?</dd>
   * </dl>   * @return A TemplatesBuilder object, or null if not supported.
   * @exception May throw a ProcessorException if a StylesheetHandler can
   * not be constructed for some reason.
   *
   * @throws ProcessorException
   */
  public TemplatesBuilder getTemplatesBuilder() throws ProcessorException
  {
    return new StylesheetHandler(this);
  }

  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an Processor to recognize a feature name but
   * to be unable to return its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of knowing whether the underlying parser is
   * validating, for example.</p>   *    * <h3>Open issues:</h3>
   * <dl>   *    <dt><h4>Should getFeature be changed to hasFeature?</h4></dt>
   *    <dd>Keith Visco writes: Should getFeature be changed to hasFeature?
   *        It returns a boolean which indicated whether the "state"
   *        of feature is "true or false". I assume this means whether
   *        or not a feature is supported? I know SAX is using "getFeature",
   *        but to me "hasFeature" is cleaner.</dd>
   * </dl>   
   * @param name The feature name, which is a fully-qualified
   *        URI.
   * @return The current state of the feature (true or false).
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            Processor does not recognize the feature name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            Processor recognizes the feature name but
   *            cannot determine its value at this time.
   *
   * @throws SAXNotRecognizedException
   * @throws SAXNotSupportedException
   */
  public boolean getFeature(String name)
          throws SAXNotRecognizedException, SAXNotSupportedException
  {

    if ("http://xml.org/trax/features/sax/input".equals(name))
      return true;
    else if ("http://xml.org/trax/features/dom/input".equals(name))
      return true;

    throw new SAXNotRecognizedException(name);
  }
}
