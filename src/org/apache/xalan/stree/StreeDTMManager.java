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
package org.apache.xalan.stree;

import org.apache.xml.dtm.DTMManagerDefault;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.dom2dtm.DOM2DTM;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.SourceTreeManager;
import org.apache.xpath.XPathContext;

import java.util.Properties;
import java.util.Enumeration;

import org.apache.xml.utils.PrefixResolver;

import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerException;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class StreeDTMManager <needs-comment/>
 */
public class StreeDTMManager extends DTMManagerDefault
{

  /**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * (I think more parameters will need to be added for error handling, and entity
   * resolution).
   *
   * @param source the specification of the source object.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(Source source, boolean unique,
                    DTMWSFilter whiteSpaceFilter)
  {

    // System.out.println("In StreeDTMManager");
    if (source instanceof DOMSource)
    {
      return super.getDTM(source, unique, whiteSpaceFilter, false);
    }
    else if (whiteSpaceFilter instanceof TransformerImpl)
    {
      TransformerImpl transformer = (TransformerImpl) whiteSpaceFilter;
      XPathContext xctxt = transformer.getXPathContext();
      XMLReader reader = getXMLReader(source, xctxt.getSAXLocator());

      // Get the input content handler, which will handle the 
      // parse events and create the source tree. 
      // ContentHandler inputHandler = transformer.getInputContentHandler();
      // SourceTreeHandler sth = new SourceTreeHandler(transformer, this, false);
      // sth.setUseMultiThreading(true);
      SourceTreeHandler sth = new SourceTreeHandler();
      sth.setUseMultiThreading(false);
      
      // transformer.setIsTransformDone(false);
      InputSource xmlSource = SAXSource.sourceToInputSource(source);

      String urlOfSource = xmlSource.getSystemId();

      if (null != urlOfSource)
      {
        try
        {
          urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
        }
        catch (Exception e)
        {

          // %REVIEW% Is there a better way to send a warning?
          System.err.println("Can not absolutize URL: " + urlOfSource);
        }

        xmlSource.setSystemId(urlOfSource);
      }

      int documentID = m_dtms.size() << 20;
      DOMSource ds = new DOMSource(sth.getRoot(), xmlSource.getSystemId());
      DTM dtm = new DOM2DTM(this, ds, documentID, whiteSpaceFilter, 
                      org.apache.xpath.objects.XMLStringFactoryImpl.getFactory());
      int doc = sth.getDTMRoot();
      m_dtms.add(dtm);
      reader.setContentHandler(sth);

      if (sth instanceof org.xml.sax.DTDHandler)
        reader.setDTDHandler(sth);

      try
      {
        reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                           sth);
      }
      catch (org.xml.sax.SAXException snre){}

      try
      {
        reader.setProperty(
          "http://xml.org/sax/properties/declaration-handler", sth);
      }
      catch (org.xml.sax.SAXException se){}

      try
      {
        reader.setProperty("http://xml.org/sax/handlers/LexicalHandler", sth);
      }
      catch (org.xml.sax.SAXException se){}

      try
      {
        reader.setProperty("http://xml.org/sax/handlers/DeclHandler", sth);
      }
      catch (org.xml.sax.SAXException se){}

      // Set the reader for cloning purposes.
      // transformer.getXPathContext().setPrimaryReader(reader);
      // transformer.setExceptionThrown(null);
      
      sth.setInputSource(source);

      if (DTM.NULL != doc)
      {
        SourceTreeManager stm =
          transformer.getXPathContext().getSourceTreeManager();

        // stm.putDocumentInCache(doc, source);
        // transformer.setXMLSource(source);

        if (null == xmlSource)
        {
          throw new DTMException("Not supported: " + source);
        }


        try
        {
          reader.parse(xmlSource);

          return dtm;
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    else
    {
      throw new DTMException("Not supported: " + source);
    }

    return null;

  }

  /**
   * This method returns the SAX2 parser to use with the InputSource
   * obtained from this URI.
   * It may return null if any SAX2-conformant XML parser can be used,
   * or if getInputSource() will also return null. The parser must
   * be free for use (i.e.
   * not currently in use for another parse().
   *
   * @param inputSource The value returned from the URIResolver.
   * @returns a SAX2 XMLReader to use to resolve the inputSource argument.
   * @param locator The location of the original caller, for diagnostic purposes.
   *
   * @return non-null XMLReader reference ready to parse.
   */
  public XMLReader getXMLReader(Source inputSource, SourceLocator locator)
  {

    try
    {
      XMLReader reader = (inputSource instanceof SAXSource)
                         ? ((SAXSource) inputSource).getXMLReader() : null;

      if (null == reader)
      {
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
          reader = XMLReaderFactory.createXMLReader();
      }

      try
      {
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);
        reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                          true);
      }
      catch (org.xml.sax.SAXException se)
      {

        // What can we do?
        // TODO: User diagnostics.
      }

      return reader;
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new DTMException(se.getMessage(), locator, se);
    }
  }
}
