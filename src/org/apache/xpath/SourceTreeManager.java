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
package org.apache.xpath;

import java.net.MalformedURLException;

import java.io.File;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.Document;

import javax.xml.transform.URIResolver;
import javax.xml.transform.TransformerException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
// import org.xml.sax.Locator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.stree.SourceTreeHandler;
import org.apache.xalan.utils.SystemIDResolver;
import org.apache.xpath.res.XPATHErrorResources;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.utils.SAXSourceLocator;

/**
 * This class bottlenecks all management of source trees.  The methods
 * in this class should allow easy garbage collection of source
 * trees, and should centralize parsing for those source trees.
 */
public class SourceTreeManager
{

  /** NEEDSDOC Field m_sourceTree          */
  private Vector m_sourceTree = new Vector();

  /** NEEDSDOC Field m_uriResolver          */
  URIResolver m_uriResolver;

  /**
   * Set an object that will be used to resolve URIs used in
   * document(), etc.
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }

  /** NEEDSDOC Field m_entityResolver          */
  EntityResolver m_entityResolver;

  /*
  * Allow an application to register an entity resolver.
  */

  /**
   * NEEDSDOC Method setEntityResolver 
   *
   *
   * NEEDSDOC @param resolver
   */
  public void setEntityResolver(EntityResolver resolver)
  {
    m_entityResolver = resolver;
  }

  /**
   * Given a document, find the URL associated with that document.
   * @param owner Document that was previously processed by this liaison.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String findURIFromDoc(Document owner)
  {

    Node root = owner.getOwnerDocument();

    if (null == root)
      root = owner;

    String url = null;
    int n = m_sourceTree.size();

    for (int i = 0; i < n; i++)
    {
      SourceTree sTree = (SourceTree) m_sourceTree.elementAt(i);

      if (root == sTree.m_root)
      {
        url = sTree.m_url;

        break;
      }
    }

    return url;
  }

  /**
   * This will be called by the processor when it encounters
   * an xsl:include, xsl:import, or document() function.
   *
   * @param base The base URI that should be used.
   * @param uri Value from an xsl:import or xsl:include's href attribute,
   * or a URI specified in the document() function.
   * @returns a InputSource that can be used to process the resource.
   * NEEDSDOC @param urlString
   * NEEDSDOC @param locator
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws IOException
   * @throws TransformerException
   */
  public Source resolveURI(
          String base, String urlString, SourceLocator locator)
            throws TransformerException, IOException, SAXException
  {
    Source source = null;
    
    if(null != m_uriResolver)
    {
      source = m_uriResolver.resolve(urlString, base);
    }
    
    if(null == source)
    {
      String uri = SystemIDResolver.getAbsoluteURI(urlString, base);

      source = new StreamSource(uri);
    }

    return source;
  }

  /**
   * Put the source tree root node in the document cache.
   * TODO: This function needs to be a LOT more sophisticated.
   *
   * NEEDSDOC @param n
   * NEEDSDOC @param source
   */
  public void putDocumentInCache(Node n, Source source)
  {

    try
    {
      Node cachedNode = getNode(source);

      if (null != cachedNode)
      {
        if (!cachedNode.equals(n))
          throw new RuntimeException(
            "Programmer's Error!  "
            + "putDocumentInCache found reparse of doc: "
            + source.getBaseID());

        return;
      }

      if (null != source.getBaseID())
      {
        m_sourceTree.addElement(new SourceTree(n, source.getBaseID()));
      }
    }
    catch (TransformerException te)
    {
      throw new org.apache.xalan.utils.WrappedRuntimeException(te);
    }
  }

  /**
   * Given a URL, find the node associated with that document.
   * @param url
   *
   * NEEDSDOC @param source
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public Node getNode(Source source) throws TransformerException
  {
    if(source instanceof DOMSource)
      return ((DOMSource)source).getNode();

    // TODO: Not sure if the BaseID is really the same thing as the ID.
    String url = source.getBaseID();

    if (null == url)
      return null;

    Node node = null;
    int n = m_sourceTree.size();
    ;

    // System.out.println("getNode: "+n);
    for (int i = 0; i < n; i++)
    {
      SourceTree sTree = (SourceTree) m_sourceTree.elementAt(i);

      // System.out.println("getNode -         url: "+url);
      // System.out.println("getNode - sTree.m_url: "+sTree.m_url);
      if (url.equals(sTree.m_url))
      {
        node = sTree.m_root;

        break;
      }
    }

    // System.out.println("getNode - returning: "+node);
    return node;
  }

  /**
   * Get the source tree from the a base URL and a URL string.
   *
   * NEEDSDOC @param base
   * NEEDSDOC @param urlString
   * NEEDSDOC @param locator
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public Node getSourceTree(String base, String urlString, SourceLocator locator)
          throws SAXException
  {

    // System.out.println("getSourceTree");
    try
    {
      Source source = this.resolveURI(base, urlString, locator);

      // System.out.println("getSourceTree - base: "+base+", urlString: "+urlString+", source: "+source.getSystemId());
      return getSourceTree(source, locator);
    }
    catch (IOException ioe)
    {
      throw new SAXParseException(ioe.getMessage(), (SAXSourceLocator)locator, ioe);
    }
    catch (TransformerException te)
    {
      throw new SAXParseException(te.getMessage(), (SAXSourceLocator)locator, te);
    }
  }

  /**
   * Get the source tree from the input source.
   *
   * NEEDSDOC @param source
   * NEEDSDOC @param locator
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public Node getSourceTree(Source source, SourceLocator locator)
          throws TransformerException
  {
    Node n = getNode(source);

    if (null != n)
      return n;

    n = getDOMNode(source, locator);

    if (null != n)
      putDocumentInCache(n, source);

    return n;
  }

  /**
   * Try to create a DOM source tree from the input source.
   *
   * NEEDSDOC @param source
   * NEEDSDOC @param locator
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public Node getDOMNode(Source source, SourceLocator locator)
          throws TransformerException
  {
    
    if(source instanceof DOMSource)
    {
      return ((DOMSource)source).getNode();
    }

    Node doc = null;

    try
    {

      // System.out.println("reading: "+source.getSystemId());
      XMLReader reader = getXMLReader(source, locator);

      // TODO: Need to use factory of some kind to create the ContentHandler
      // (Also, try using JAXP if need be...)
      ContentHandler handler = new SourceTreeHandler();

      if (handler instanceof org.apache.xalan.stree.SourceTreeHandler)
      {

        // temp hack
        ((org.apache.xalan.stree.SourceTreeHandler) handler).setUseMultiThreading(
          false);
      }

      reader.setContentHandler(handler);

      try
      {
        reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                           handler);
      }
      catch (SAXException se){}

      InputSource isource = SAXSource.sourceToInputSource(source);
      reader.parse(isource);

      if (handler instanceof org.apache.xalan.stree.SourceTreeHandler)
      {
        doc = ((org.apache.xalan.stree.SourceTreeHandler) handler).getRoot();
      }
    }
    catch (IOException ioe)
    {
      throw new TransformerException(ioe.getMessage(), locator, ioe);
    }
    catch (SAXException se)
    {
      throw new TransformerException(se.getMessage(), locator, se);
    }

    return doc;
  }

  /**
   * This method returns the SAX2 parser to use with the InputSource
   * obtained from this URI.
   * It may return null if any SAX2-conformant XML parser can be used,
   * or if getInputSource() will also return null. The parser must
   * be free for use (i.e.
   * not currently in use for another parse().
   *
   * @param inputSource The value returned from the EntityResolver.
   * @returns a SAX2 parser to use with the InputSource.
   * NEEDSDOC @param locator
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws TransformerException
   */
  public XMLReader getXMLReader(Source inputSource, SourceLocator locator)
          throws TransformerException
  {

    try
    {
      XMLReader reader = (inputSource instanceof SAXSource)
                         ? ((SAXSource)inputSource).getXMLReader() : null;

      if (null == reader)
        reader = XMLReaderFactory.createXMLReader();

      try
      {
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);
        reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                          true);
      }
      catch (SAXException se)
      {

        // What can we do?
        // TODO: User diagnostics.
      }

      return reader;
    }
    catch (SAXException se)
    {
      throw new TransformerException(se.getMessage(), locator, se);
    }
  }
}
