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
 *     the documentation and/or other materials provided with the
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

import org.apache.trax.URIResolver;
import org.apache.trax.TransformException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.stree.SourceTreeHandler;
import org.apache.xalan.utils.SystemIDResolver;

import org.apache.xpath.res.XPATHErrorResources;

/**
 * This class bottlenecks all management of source trees.  The methods
 * in this class should allow easy garbage collection of source 
 * trees, and should centralize parsing for those source trees.
 */
public class SourceTreeManager
{
  private Vector m_sourceTree = new Vector();
  
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

  EntityResolver m_entityResolver;
    
  /*
  * Allow an application to register an entity resolver.
  */
  public void setEntityResolver (EntityResolver resolver)
  {
    m_entityResolver = resolver;
  }
    
  /**
   * Given a document, find the URL associated with that document.
   * @param owner Document that was previously processed by this liaison.
   */
  public String findURIFromDoc(Document owner)
  {
    Node root = owner.getOwnerDocument();
    if(null == root)
      root = owner;
    String url = null;
    int n = m_sourceTree.size();
    for(int i = 0; i < n; i++)
    {
      SourceTree sTree = (SourceTree)m_sourceTree.elementAt(i);
      if(root == sTree.m_root)
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
   */
  public InputSource resolveURI (String base, String urlString, Locator locator)
    throws TransformException, IOException
  {
    String uri;
    try
    {
      uri = SystemIDResolver.getAbsoluteURI(urlString, base);
    }
    catch(SAXException se)
    {
      // Try and see if the entity resolver can do the job. 
      // If not, throw an exception.
      if(null != m_entityResolver)
      {
        try
        {
          return m_entityResolver.resolveEntity(null, urlString);
        }
        catch(SAXException se2)
        {
          throw new TransformException("URL of base: "+base+
          " and url: "+urlString+" can't be resolved", locator, se2);
        }
      }
      else
        throw new TransformException("URL of base: "+base+
          " and url: "+urlString+" can't be resolved", locator, se);
    }
    
    InputSource source;
    try
    {
      if(null != m_entityResolver)
        source = m_entityResolver.resolveEntity(null, urlString);
      else
        source = new InputSource(uri);
    }
    catch(SAXException se2)
    {
      throw new TransformException("URL: "+urlString
        +" can't be resolved", locator, se2);
    }

    return source;
  }
  
  /**
   * Put the source tree root node in the document cache.
   * TODO: This function needs to be a LOT more sophisticated.
   */
  public void putDocumentInCache(Node n, InputSource source)
  {
    if(null != source.getSystemId())
    {
      m_sourceTree.addElement(new SourceTree(n, source.getSystemId()));        
    }      
  }
  
  
  
  /**
   * Given a URL, find the node associated with that document.
   * @param url 
   */
  public Node findNodeFromURL(String base, String url, Locator locator)
    throws TransformException
  {
    try
    {
      InputSource source = this.resolveURI(base, url, locator);
      if(null != source.getSystemId())
        url = source.getSystemId();
      Node node = null;
      int n = m_sourceTree.size();;
      for(int i = 0; i < n; i++)
      {
        SourceTree sTree = (SourceTree)m_sourceTree.elementAt(i);
        if(url.equals(sTree.m_url))
        {
          node = sTree.m_root;
          break;
        }
      }
      return node;
    }
    catch(IOException ioe)
    {
      throw new TransformException(ioe, locator);
    }
  }
  
  /**
   * Get the source tree from the a base URL and a URL string.
   */
  public Node getSourceTree (String base, String urlString, Locator locator) 
    throws TransformException
  {
    try
    {
      InputSource source = this.resolveURI(base, urlString, locator);
      // System.out.println("base: "+base+", urlString: "+urlString+", source: "+source.getSystemId());
      return getSourceTree(source, locator);
    }
    catch(IOException ioe)
    {
      throw new TransformException(ioe, locator);
    }
  }

  /**
   * Get the source tree from the input source.
   */
  public Node getSourceTree (InputSource source, Locator locator) 
    throws TransformException
  {
    // Try first to see if we have a node cached that matches this 
    // systemID.
    if(null != source.getSystemId())
    {
      Node n = findNodeFromURL(null, source.getSystemId(), locator);
      if(null != n)
        return n;
    }
    
    Node root = null;
        
    if(null != m_uriResolver)
      root = m_uriResolver.getDOMNode(source);
    
    if(null == root)
      root = getDOMNode(source, locator);
    
    if(null != root)
      putDocumentInCache(root, source);
    return root;
  }
  
  /**
   * Try to create a DOM source tree from the input source.
   */
  public Node getDOMNode (InputSource source, Locator locator) 
    throws TransformException
  {
    Node doc = null;
    String liaisonClassName = System.getProperty("org.apache.xalan.source.liaison");

    if(null != liaisonClassName)
    {
      try 
      {
        DOM2Helper liaison =  (DOM2Helper)(Class.forName(liaisonClassName).newInstance());
        liaison.parse(source);
        doc = liaison.getDocument();
      } 
      catch (SAXException se) 
      {
        throw new TransformException(se, locator);
      } 
      catch (ClassNotFoundException e1) 
      {
        throw new TransformException("XML Liaison class " + liaisonClassName +
          " specified but not found", locator, e1);
      } 
      catch (IllegalAccessException e2) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " found but cannot be loaded", locator, e2);
      } 
      catch (InstantiationException e3) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " loaded but cannot be instantiated (no empty public constructor?)",
            locator, e3);
      } 
      catch (ClassCastException e4) 
      {
          throw new TransformException("XML Liaison class " + liaisonClassName +
            " does not implement DOM2Helper", locator, e4);
      }
    }
    else
    {
    try
    {
      XMLReader reader = getXMLReader(source, locator) ;
      
      // TODO: Need to use factory of some kind to create the ContentHandler
      // (Also, try using JAXP if need be...)
      ContentHandler handler = new SourceTreeHandler();
      if(handler instanceof org.apache.xalan.stree.SourceTreeHandler)
      {
        // temp hack
        ((org.apache.xalan.stree.SourceTreeHandler)handler).setUseMultiThreading(false);
      }
      reader.setContentHandler(handler);

      try
      {
        reader.setProperty("http://xml.org/sax/properties/lexical-handler", 
                           handler);
      }
      catch(SAXException se){}
      
      reader.parse(source);

      if(handler instanceof org.apache.xalan.stree.SourceTreeHandler)
      {
        doc = ((org.apache.xalan.stree.SourceTreeHandler)handler).getRoot();
      }
    }
    catch(IOException ioe)
    {
      throw new TransformException(ioe, locator);
    }
    catch(SAXException se)
    {
      throw new TransformException(se, locator);
    }
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
   */
  public XMLReader getXMLReader(InputSource inputSource, Locator locator) 
    throws TransformException
  {
    try
    {
      XMLReader reader = (null != m_uriResolver) 
                         ? m_uriResolver.getXMLReader(inputSource) : null;
      if(null == reader)
        reader = XMLReaderFactory.createXMLReader();
      
      try
      {
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        reader.setFeature("http://apache.org/xml/features/validation/dynamic", true);
      }
      catch(SAXException se)
      {
        // What can we do?
        // TODO: User diagnostics.
      }

      return reader;
    }
    catch(SAXException se)
    {
      throw new TransformException(se, locator);
    }
  }
}
