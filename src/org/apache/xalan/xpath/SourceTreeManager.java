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
package org.apache.xalan.xpath;

import java.net.URL;
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
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.stree.SourceTreeHandler;

/**
 * This class bottlenecks all management of source trees.  The methods
 * in this class should allow easy garbage collection of source 
 * trees, and should centralize parsing for those source trees.
 * The SourceTreeManager is also the default 
 */
public class SourceTreeManager implements URIResolver
{
  private int m_size = 0;
  private SourceTree m_sourceTree[] = new SourceTree[20];
  
  /**
   * <meta name="usage" content="advanced"/>
   * Associate an XLocator provider to a node.  This makes
   * the association based on the root of the tree that the 
   * node is parented by.
   */
  public void associateXLocatorToNode(Node node, URL url, XLocator xlocator)
  {
    Node root = node.getOwnerDocument();
    if(null == root)
      root = node;
    XLocator found = null;
    int n = m_size;
    for(int i = 0; i < n; i++)
    {
      SourceTree ass = m_sourceTree[i];
      if(root == ass.m_root)
      {
        found = ass.m_locator;
        break;
      }
    }
    if(null == found)
    {
      m_sourceTree[m_size] = new SourceTree(root, url, xlocator);
      m_size++;
    }
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Get an XLocator provider keyed by node.  This get's
   * the association based on the root of the tree that the 
   * node is parented by.
   */
  public XLocator getXLocatorFromNode(Node node)
  {
    Node root = node.getOwnerDocument();
    if(null == root)
      root = node;
    XLocator xlocator = null;
    int n = m_size;
    for(int i = 0; i < n; i++)
    {
      SourceTree ass = m_sourceTree[i];
      if(root == ass.m_root)
      {
        xlocator = ass.m_locator;
        break;
      }
    }
    return xlocator;
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
    URL url = null;
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
    return url.toExternalForm();
  }
  
  /**
   * Given a document, find the URL associated with that document.
   * @param owner Document that was previously processed by this liaison.
   */
  public Node findNodeFromURL(URL url)
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
      if(url == ass.m_url.toExternalForm())
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
    String origURLString = urlString;
    String origBase = base;
    
    // System.out.println("getURLFromString - urlString: "+urlString+", base: "+base);
    Object doc;
    URL url = null;
    int fileStartType = 0;
    try
    {
      
      if(null != base)
      {
        if(base.toLowerCase().startsWith("file:/"))
        {
          fileStartType = 1;
        }
        else if(base.toLowerCase().startsWith("file:"))
        {
          fileStartType = 2;
        }
      }
      
      boolean isAbsoluteURL;
      
      // From http://www.ics.uci.edu/pub/ietf/uri/rfc1630.txt
      // A partial form can be distinguished from an absolute form in that the
      // latter must have a colon and that colon must occur before any slash
      // characters. Systems not requiring partial forms should not use any
      // unencoded slashes in their naming schemes.  If they do, absolute URIs
      // will still work, but confusion may result.
      int indexOfColon = urlString.indexOf(':');
      int indexOfSlash = urlString.indexOf('/');
      if((indexOfColon != -1) && (indexOfSlash != -1) && (indexOfColon < indexOfSlash))
      {
        // The url (or filename, for that matter) is absolute.
        isAbsoluteURL = true;
      }
      else
      {
        isAbsoluteURL = false;
      }
      
      if(isAbsoluteURL || (null == base) || (base.length() == 0))
      {
        try 
        {
          url = new URL(urlString);
        }
        catch (MalformedURLException e) {}
      }
      // The Java URL handling doesn't seem to handle relative file names.
      else if(!((urlString.charAt(0) == '.') || (fileStartType > 0)))
      {
        try 
        {
          URL baseUrl = new URL(base);
          url = new URL(baseUrl, urlString);
        }
        catch (MalformedURLException e) 
        {
        }
      }
      
      if(null == url)
      {
        // Then we're going to try and make a file URL below, so strip 
        // off the protocol header.
        if(urlString.toLowerCase().startsWith("file:/"))
        {
          urlString = urlString.substring(6);
        }
        else if(urlString.toLowerCase().startsWith("file:"))
        {
          urlString = urlString.substring(5);
        }
      }
      
      if((null == url) && ((null == base) || (fileStartType > 0)))
      {
        if(1 == fileStartType)
        {
          if(null != base)
            base = base.substring(6);
          fileStartType = 1;
        }
        else if(2 == fileStartType)
        {
          if(null != base)
            base = base.substring(5);
          fileStartType = 2;
        }
        
        File f = new File(urlString);
        
        if(!f.isAbsolute() && (null != base))
        {
          // String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
          // System.out.println("prebuiltUrlString (1): "+base);
          StringTokenizer tokenizer = new StringTokenizer(base, "\\/");
          String fixedBase = null;
          while(tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            if (null == fixedBase) 
            {
              // Thanks to Rick Maddy for the bug fix for UNIX here.
              if (base.charAt(0) == '\\' || base.charAt(0) == '/') 
              {
                fixedBase = File.separator + token;
              }
              else 
              {
                fixedBase = token;
              }
            }
            else 
            {
              fixedBase+= File.separator + token;
            }
          }
          // System.out.println("rebuiltUrlString (1): "+fixedBase);
          f = new File(fixedBase);
          String dir = f.isDirectory() ? f.getAbsolutePath() : f.getParent();
          // System.out.println("dir: "+dir);
          // System.out.println("urlString: "+urlString);
          // f = new File(dir, urlString);
          // System.out.println("f (1): "+f.toString());
          // urlString = f.getAbsolutePath();
          f = new File(urlString); 
          boolean isAbsolute =  f.isAbsolute() 
                                || (urlString.charAt( 0 ) == '\\')
                                || (urlString.charAt( 0 ) == '/');
          if(!isAbsolute)
          {
            // Getting more and more ugly...
            if(dir.charAt( dir.length()-1 ) != File.separator.charAt(0) && 
               urlString.charAt( 0 ) != File.separator.charAt(0))
            {
              urlString = dir + File.separator + urlString;
            }
            else
            {
              urlString = dir + urlString;
            }

            // System.out.println("prebuiltUrlString (2): "+urlString);
            tokenizer = new StringTokenizer(urlString, "\\/");
            String rebuiltUrlString = null;
            while(tokenizer.hasMoreTokens())
            {
              String token = tokenizer.nextToken();
              if (null == rebuiltUrlString) 
              {
                // Thanks to Rick Maddy for the bug fix for UNIX here.
                if (urlString.charAt(0) == '\\' || urlString.charAt(0) == '/') 
                {
                  rebuiltUrlString = File.separator + token;
                }
                else 
                {
                  rebuiltUrlString = token;
                }
              }
              else 
              {
                rebuiltUrlString+= File.separator + token;
              }
            }
            // System.out.println("rebuiltUrlString (2): "+rebuiltUrlString);
            if(null != rebuiltUrlString)
              urlString = rebuiltUrlString;
          }
          // System.out.println("fileStartType: "+fileStartType);
          if(1 == fileStartType)
          {
            if (urlString.charAt(0) == '/') 
            {
              urlString = "file://"+urlString;
            }
            else
            {
              urlString = "file:/"+urlString;
            }
          }
          else if(2 == fileStartType)
          {
            urlString = "file:"+urlString;
          }
          try 
          {
            // System.out.println("Final before try: "+urlString);
            url = new URL(urlString);
          }
          catch (MalformedURLException e) 
          {
            // System.out.println("Error trying to make URL from "+urlString);
          }
        }
      }
      if(null == url)
      {
        // The sun java VM doesn't do this correctly, but I'll 
        // try it here as a second-to-last resort.
        if((null != origBase) && (origBase.length() > 0))
        {
          try 
          {
            URL baseURL = new URL(origBase);
            // System.out.println("Trying to make URL from "+origBase+" and "+origURLString);
            url = new URL(baseURL, origURLString);
            // System.out.println("Success! New URL is: "+url.toString());
          }
          catch (MalformedURLException e) 
          {
            // System.out.println("Error trying to make URL from "+origBase+" and "+origURLString);
          }
        }
        
        if(null == url)
        {
          try 
          {
            String lastPart;
            if(null != origBase)
            {
              File baseFile = new File(origBase);
              if(baseFile.isDirectory())
              {
                lastPart = new File(baseFile, urlString).getAbsolutePath ();
              }
              else
              {
                String parentDir = baseFile.getParent();
                lastPart = new File(parentDir, urlString).getAbsolutePath ();
              }
            }
            else
            {
              lastPart = new File (urlString).getAbsolutePath ();
            }
            // Hack
            // if((lastPart.charAt(0) == '/') && (lastPart.charAt(2) == ':'))
            //   lastPart = lastPart.substring(1, lastPart.length() - 1);
            
            String fullpath;
            if (lastPart.charAt(0) == '\\' || lastPart.charAt(0) == '/') 
            {
              fullpath = "file://" + lastPart;
            }
            else
            {
              fullpath = "file:" + lastPart;
            }
            url = new URL(fullpath);
          }
          catch (MalformedURLException e2)
          {
            throw new TransformException( XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_CREATE_URL, new Object[]{urlString}),e2); //"Cannot create url for: " + urlString, e2 );
          }
        }
      }
    }
    catch(SecurityException se)
    {
      try
      {
        url = new URL("http://xml.apache.org/xslt/"+java.lang.Math.random()); // dummy
      }
      catch (MalformedURLException e2)
      {
        // I give up
      }
    }

    InputSource source = new InputSource();
    
    source.setSystemId(url.toExternalForm());
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
