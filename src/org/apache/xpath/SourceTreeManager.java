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
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import trax.URIResolver;
import trax.TransformException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.stree.SourceTreeHandler;
import org.apache.xalan.utils.SystemIDResolver;

/**
 * This class bottlenecks all management of source trees.  The methods
 * in this class should allow easy garbage collection of source 
 * trees, and should centralize parsing for those source trees.
 */
public class SourceTreeManager implements URIResolver
{
  private int m_size = 0;
  private SourceTree m_sourceTree[] = new SourceTree[20];
    
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
    int n = m_size;
    for(int i = 0; i < n; i++)
    {
      SourceTree ass = m_sourceTree[i];
      if(root == ass.m_root)
      {
        url = ass.m_url;
        break;
      }
    }
    return url;
  }
  
  /**
   * Given a document, find the URL associated with that document.
   * @param owner Document that was previously processed by this liaison.
   */
  public Node findNodeFromURL(String url)
  {
    Node node = null;
    int n = m_size;
    for(int i = 0; i < n; i++)
    {
      SourceTree ass = m_sourceTree[i];
      if(url == ass.m_url)
      {
        node = ass.m_root;
        break;
      }
    }
    return node;
  }
  
  /**
   * Return a string suitible for telling the user what parser is being used.
   */
  public String getParserDescription()
  {
    return "(No parser - generic DOM)";
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
  public InputSource resolveURI (String base, String urlString)
    throws TransformException, IOException
  {
    String uri;
    try
    {
      uri = SystemIDResolver.getAbsoluteURI(base, urlString);
    }
    catch(SAXException se)
    {
      throw new TransformException(se);
    }
    InputSource source = new InputSource();
    
    source.setSystemId(uri);
    return source;
  }

  /**
   * This will be called by the processor when it encounters 
   * an xsl:include, xsl:import, or document() function, if it needs 
   * a DOM tree.
   * 
   * @param base The base URI that should be used.
   * @param uri Value from an xsl:import or xsl:include's href attribute, 
   * or a URI specified in the document() function.
   * @returns a DOM node that represents the resolution of the URI to a tree.
   */
  public Node resolveURIToDOMTree (String base, String uri)
    throws TransformException, IOException
  {
    InputSource source = resolveURI (base, uri);
    try
    {
      XMLReader reader = XMLReaderFactory.createXMLReader();
      SourceTreeHandler handler = new SourceTreeHandler();
      reader.setContentHandler(handler);
      reader.parse(source);
      return handler.getRoot();
    }
    catch(SAXException se)
    {
      throw new TransformException(se);
    }
  }



}
