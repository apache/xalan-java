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

import org.xml.sax.helpers.DefaultHandler;
import javax.xml.transform.TransformerException;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;

import java.util.Vector;
import java.util.StringTokenizer;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.xalan.utils.SystemIDResolver;

/**
 * Search for the xml-stylesheet processing instructions in an XML document.
 * @see <a href="http://www.w3.org/TR/xml-stylesheet/">Associating Style Sheets with XML documents, Version 1.0</a>
 */
public class StylesheetPIHandler extends DefaultHandler
{
  /** The baseID of the document being processed.  */
  String m_baseID;

  /** The desired media criteria. */
  String m_media;

  /** The desired title criteria.  */
  String m_title;

  /** The desired character set criteria.   */
  String m_charset;

  /** A list of SAXSource objects that match the criteria.  */
  Vector m_stylesheets = new Vector();

  /**
   * Construct a StylesheetPIHandler instance that will search 
   * for xml-stylesheet PIs based on the given criteria.
   *
   * @param baseID The base ID of the XML document, needed to resolve 
   *               relative IDs.
   * @param media The desired media criteria.
   * @param title The desired title criteria.
   * @param charset The desired character set criteria.
   */
  public StylesheetPIHandler(String baseID, String media, String title,
                             String charset)
  {

    m_baseID = baseID;
    m_media = media;
    m_title = title;
    m_charset = charset;
  }

  /**
   * Return the last stylesheet found that match the constraints.
   *
   * @return Source object that references the last stylesheet reference 
   *         that matches the constraints.
   */
  public Source getAssociatedStylesheet()
  {

    int sz = m_stylesheets.size();

    if (sz > 0)
    {
      SAXSource ssource 
        = new SAXSource((InputSource) m_stylesheets.elementAt(sz-1));
      return ssource;
    }
    else
      return null;
  }

  /**
   * Handle the xml-stylesheet processing instruction.
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *             none is supplied.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   * @see <a href="http://www.w3.org/TR/xml-stylesheet/">Associating Style Sheets with XML documents, Version 1.0</a>
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {

    if (target.equals("xml-stylesheet"))
    {
      String href = null;  // CDATA #REQUIRED
      String type = null;  // CDATA #REQUIRED
      String title = null;  // CDATA #IMPLIED
      String media = null;  // CDATA #IMPLIED
      String charset = null;  // CDATA #IMPLIED
      boolean alternate = false;  // (yes|no) "no"
      StringTokenizer tokenizer = new StringTokenizer(data, " \t=");

      while (tokenizer.hasMoreTokens())
      {
        String name = tokenizer.nextToken();

        if (name.equals("type"))
        {
          String typeVal = tokenizer.nextToken();

          type = typeVal.substring(1, typeVal.length() - 1);
        }
        else if (name.equals("href"))
        {
          href = tokenizer.nextToken();
          href = href.substring(1, href.length() - 1);
          try
          {
            href = SystemIDResolver.getAbsoluteURI(href, m_baseID);
          }
          catch(TransformerException te)
          {
            throw new org.xml.sax.SAXException(te);
          }
        }
        else if (name.equals("title"))
        {
          title = tokenizer.nextToken();
          title = title.substring(1, title.length() - 1);
        }
        else if (name.equals("media"))
        {
          media = tokenizer.nextToken();
          media = media.substring(1, media.length() - 1);
        }
        else if (name.equals("charset"))
        {
          charset = tokenizer.nextToken();
          charset = charset.substring(1, charset.length() - 1);
        }
        else if (name.equals("alternate"))
        {
          String alternateStr = tokenizer.nextToken();

          alternate = alternateStr.substring(1, alternateStr.length()
                                             - 1).equals("yes");
        }
      }

      if ((null != type) && type.equals("text/xsl") && (null != href))
      {
        if (null != m_media)
        {
          if (null != media)
          {
            if (!media.equals(m_media))
              return;
          }
          else
            return;
        }

        if (null != m_charset)
        {
          if (null != charset)
          {
            if (!charset.equals(m_charset))
              return;
          }
          else
            return;
        }

        if (null != m_title)
        {
          if (null != title)
          {
            if (!title.equals(m_title))
              return;
          }
          else
            return;
        }

        m_stylesheets.addElement(new InputSource(href));
      }
    }
  }

  /**
   * The spec notes that "The xml-stylesheet processing instruction is allowed only in the prolog of an XML document.",
   * so, at least for right now, I'm going to go ahead an throw a TransformerException
   * in order to stop the parse.
   *
   * @param uri The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param rawName The qualified name (with prefix).
   * @param attributes The specified or defaulted attributes.
   *
   * @throws StopParseException since there can be no valid xml-stylesheet processing 
   *                            instructions past the first element.
   */
  public void startElement(
          String namespaceURI, String localName, String qName, Attributes atts)
            throws org.xml.sax.SAXException
  {
    throw new StopParseException();
  }
}
