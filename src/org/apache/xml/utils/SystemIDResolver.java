/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xml.utils;

import javax.xml.transform.TransformerException;
import org.apache.xml.utils.URI;
import org.apache.xml.utils.URI.MalformedURIException;

import java.io.*;

import java.lang.StringBuffer;

/**
 * <meta name="usage" content="internal"/>
 * This class is used to resolve relative URIs and SystemID 
 * strings into absolute URIs.
 *
 * <p>This is a generic utility for resolving URIs, other than the 
 * fact that it's declared to throw TransformerException.  Please 
 * see code comments for details on how resolution is performed.</p>
 */
public class SystemIDResolver
{

  /**
   * Get an absolute URI from a given relative URI (local path). 
   * 
   * <p>The relative URI is a local filesystem path. The path can be
   * absolute or relative. If it is a relative path, it is resolved relative 
   * to the system property "user.dir" if it is available; if not (i.e. in an 
   * Applet perhaps which throws SecurityException) then we just return the
   * relative path. The space and backslash characters are also replaced to
   * generate a good absolute URI.</p>
   *
   * @param localPath The relative URI to resolve
   *
   * @return Resolved absolute URI
   */
  public static String getAbsoluteURIFromRelative(String localPath)
  {
    if (localPath == null || localPath.length() == 0)
      return "";
      
    // If the local path is a relative path, then it is resolved against
    // the "user.dir" system property.
    String absolutePath = localPath;
    if (!isAbsolutePath(localPath))
    {
      try 
      {
        absolutePath = getAbsolutePathFromRelativePath(localPath);
      }
      // user.dir not accessible from applet
      catch (SecurityException se) 
      {
        return "file:" + localPath;
      }
    }

    String urlString;
    if (null != absolutePath)
    {
      if (absolutePath.startsWith(File.separator))
        urlString = "file://" + absolutePath;
      else
        urlString = "file:///" + absolutePath;        
    }
    else
      urlString = "file:" + localPath;
    
    return replaceChars(urlString);
  }
  
  /**
   * Return an absolute path from a relative path.
   *
   * @param relativePath A relative path
   * @return The absolute path
   */
  private static String getAbsolutePathFromRelativePath(String relativePath)
  {
    return new File(relativePath).getAbsolutePath();
  }
  
  /**
   * Return true if the systemId denotes an absolute URI (contains the scheme part).
   *
   * @param systemId The systemId string
   * @return true if the systemId contains a scheme part
   */
  public static boolean isAbsoluteURI(String systemId)
  {
    // If there is more than one character before the ':' character,
    // then it is considered to be an absolute URI; otherwise it is a local path.
    int colonIndex = systemId.indexOf(':');
    if (colonIndex > 1)
      return true;
    else
      return false;
  }
  
  /**
   * Return true if the local path is an absolute path.
   *
   * @param systemId The path string
   * @return true if the path is absolute
   */
  public static boolean isAbsolutePath(String systemId)
  {
    // On Unix, an absolute path starts with '/'.
    if (systemId.startsWith(File.separator))
      return true;
    
    // On Windows, an absolute path starts with "[drive_letter]:\".
    if (systemId.length() > 2 
        && systemId.charAt(1) == ':'
        && Character.isLetter(systemId.charAt(0))
        && (systemId.charAt(2) == '\\' || systemId.charAt(2) == '/'))
      return true;
    else
      return false;
  }
  
  /**
   * Replace spaces with "%20" and backslashes with forward slashes in 
   * the input string to generate a well-formed URI string.
   *
   * @param str The input string
   * @return The string after conversion
   */
  private static String replaceChars(String str)
  {
    StringBuffer buf = new StringBuffer(str);
    int length = buf.length();
    for (int i = 0; i < length; i++)
    {
      char currentChar = buf.charAt(i);
      // Replace space with "%20"
      if (currentChar == ' ')
      {
        buf.setCharAt(i, '%');
        buf.insert(i+1, "20");
        length = length + 2;
        i = i + 2;
      }
      // Replace backslash with forward slash
      else if (currentChar == '\\')
      {
        buf.setCharAt(i, '/');
      }
    }
    
    return buf.toString();
  }
  
  /**
   * Take a SystemID string and try to turn it into a good absolute URI.
   *
   * @param systemId A URI string, which may be absolute or relative.
   *
   * @return The resolved absolute URI
   */
  public static String getAbsoluteURI(String systemId)
  {
    String absoluteURI = systemId;
    if (isAbsoluteURI(systemId))
    {
      // Only process the systemId if it starts with "file:".
      if (systemId.startsWith("file:"))
      {
        String str = systemId.substring(5);
        
        // Resolve the absolute path if the systemId starts with "file:///"
        // or "file:/". Don't do anything if it only starts with "file://".
        if (str != null && str.startsWith("/"))
        {
          if (str.startsWith("///") || !str.startsWith("//"))
          {
            // A Windows path containing a drive letter can be relative.
            // A Unix path starting with "file:/" is always absolute.
            int secondColonIndex = systemId.indexOf(':', 5);
            if (secondColonIndex > 0)
            {
              String localPath = systemId.substring(secondColonIndex-1);
              try {
                if (!isAbsolutePath(localPath))
                  absoluteURI = systemId.substring(0, secondColonIndex-1) + 
                                getAbsolutePathFromRelativePath(localPath);
              }
              catch (SecurityException se) {
                return systemId;
              }
            }
          }          
        }
        else
        {
          return getAbsoluteURIFromRelative(systemId.substring(5));
        }
                
        return replaceChars(absoluteURI);
      }
      else
        return systemId;
    }
    else
      return getAbsoluteURIFromRelative(systemId);
    
  }


  /**
   * Take a SystemID string and try to turn it into a good absolute URI.
   *
   * @param urlString SystemID string
   * @param base The URI string used as the base for resolving the systemID
   *
   * @return The resolved absolute URI
   * @throws TransformerException thrown if the string can't be turned into a URI.
   */
  public static String getAbsoluteURI(String urlString, String base)
          throws TransformerException
  {    
    if (base == null)
      return getAbsoluteURI(urlString);
    
    String absoluteBase = getAbsoluteURI(base);
    URI uri = null;
    try 
    {
      URI baseURI = new URI(absoluteBase);
      uri = new URI(baseURI, urlString);
    }
    catch (MalformedURIException mue)
    {
      throw new TransformerException(mue);
    }
    
    return replaceChars(uri.toString());
  }
  
}
