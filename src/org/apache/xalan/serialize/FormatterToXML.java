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
package org.apache.xalan.serialize;

import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;

import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;

import org.w3c.dom.Node;

import org.apache.xalan.serialize.OutputFormat;
import org.apache.xalan.serialize.helpers.XMLOutputFormat;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.OutputFormat;
import org.apache.xalan.serialize.DOMSerializer;
import org.apache.xalan.serialize.QName;

import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.WrappedRuntimeException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="general"/>
 * FormatterToXML formats SAX-style events into XML.
 * Warning: this class will be replaced by the Xerces Serializer classes.
 */
public class FormatterToXML
        implements ContentHandler, LexicalHandler, 
                   Serializer, DOMSerializer
{

  /**
   * The writer where the XML will be written.
   */
  protected Writer m_writer = null;

  /** NEEDSDOC Field m_shouldFlush          */
  boolean m_shouldFlush = true;

  /** NEEDSDOC Field m_outputStream          */
  protected OutputStream m_outputStream = System.out;

  /** NEEDSDOC Field m_bytesEqualChars          */
  private boolean m_bytesEqualChars = false;

  /**
   * The character encoding.  Must match the encoding used for the printWriter.
   */
  protected String m_encoding = null;

  /**
   * Assume java encoding names are the same as the ISO encoding names if this is true.
   */
  static boolean javaEncodingIsISO = false;

  /**
   * Tells if we should write the XML declaration.
   */
  public boolean m_shouldNotWriteXMLHeader = false;

  /**
   * Tells the XML version, for writing out to the XML decl.
   */
  public String m_version = null;

  /**
   * A stack of Boolean objects that tell if the given element
   * has children.
   */
  protected BoolStack m_elemStack = new BoolStack();

  /** NEEDSDOC Field m_disableOutputEscapingStates          */
  protected BoolStack m_disableOutputEscapingStates = new BoolStack();

  /** NEEDSDOC Field m_cdataSectionStates          */
  protected BoolStack m_cdataSectionStates = new BoolStack();

  /**
   * NEEDSDOC Method isEscapingDisabled 
   *
   *
   * NEEDSDOC (isEscapingDisabled) @return
   */
  protected boolean isEscapingDisabled()
  {
    return m_disableOutputEscapingStates.peekOrFalse();
  }

  /**
   * NEEDSDOC Method isCDataSection 
   *
   *
   * NEEDSDOC (isCDataSection) @return
   */
  protected boolean isCDataSection()
  {
    return m_cdataSectionStates.peekOrFalse();
  }

  /**
   * Use the system line seperator to write line breaks.
   */
  protected final String m_lineSep = System.getProperty("line.separator");

  /**
   * The length of the line seperator, since the write is done
   * one character at a time.
   */
  protected final int m_lineSepLen = m_lineSep.length();

  /**
   * Output a system-dependent line break.
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void outputLineSep() throws org.xml.sax.SAXException
  {

    for (int z = 0; z < m_lineSepLen; z++)
    {
      accum(m_lineSep.charAt(z));
    }
  }

  /**
   * State flag to tell if preservation of whitespace
   * is important.
   */
  protected boolean m_ispreserve = false;

  /**
   * Stack to keep track of whether or not we need to
   * preserve whitespace.
   */
  protected BoolStack m_preserves = new BoolStack();

  /**
   * State flag that tells if the previous node processed
   * was text, so we can tell if we should preserve whitespace.
   */
  protected boolean m_isprevtext = false;

  /**
   * Flag to tell if indenting (pretty-printing) is on.
   */
  protected boolean m_doIndent = false;

  /**
   * Flag to keep track of the indent amount.
   */
  protected int m_currentIndent = 0;

  /**
   * Amount to indent.
   */
  public int indent = 0;

  /**
   * Current level of indent.
   */
  protected int level = 0;

  /**
   * Flag to signal that a newline should be added.
   */
  boolean m_startNewLine;

  /**
   * Flag to tell that we need to add the doctype decl,
   * which we can't do until the first element is
   * encountered.
   */
  boolean m_needToOutputDocTypeDecl = true;

  /**
   * The System ID for the doc type.
   */
  String m_doctypeSystem;

  /**
   * The public ID for the doc type.
   */
  String m_doctypePublic;

  /**
   * The standalone value for the doctype.
   */
  boolean m_standalone = false;

  /**
   * The mediatype.  Not used right now.
   */
  String m_mediatype;

  /**
   * Tells if we're in an EntityRef event.
   */
  protected boolean m_inEntityRef = false;

  /**
   * These are characters that will be escaped in the output.
   */

  // public char[] m_attrSpecialChars = {'<', '>', '&', '\"', '\r', '\n'};
  public char[] m_attrSpecialChars = { '<', '>', '&', '\"' };

  /** NEEDSDOC Field SPECIALSSIZE          */
  static final int SPECIALSSIZE = 256;

  /** NEEDSDOC Field m_attrCharsMap          */
  public char[] m_attrCharsMap = new char[SPECIALSSIZE];

  /** NEEDSDOC Field m_charsMap          */
  public char[] m_charsMap = new char[SPECIALSSIZE];

  /**
   * Set the attribute characters what will require special mapping.
   */
  protected void initAttrCharsMap()
  {

    int n = (m_maxCharacter > SPECIALSSIZE) ? SPECIALSSIZE : m_maxCharacter;

    for (int i = 0; i < n; i++)
    {
      m_attrCharsMap[i] = '\0';
    }

    int nSpecials = m_attrSpecialChars.length;

    for (int i = 0; i < nSpecials; i++)
    {
      m_attrCharsMap[(int) m_attrSpecialChars[i]] = 'S';
    }

    m_attrCharsMap[0x0A] = 'S';
    m_attrCharsMap[0x0D] = 'S';

    for (int i = m_maxCharacter; i < SPECIALSSIZE; i++)
    {
      m_attrCharsMap[i] = 'S';
    }
  }

  /**
   * Set the characters what will require special mapping.
   */
  protected void initCharsMap()
  {

    initAttrCharsMap();

    int n = (m_maxCharacter > SPECIALSSIZE) ? SPECIALSSIZE : m_maxCharacter;

    for (int i = 0; i < n; i++)
    {
      m_charsMap[i] = '\0';
    }

    m_charsMap[(int) '\n'] = 'S';
    m_charsMap[(int) '<'] = 'S';
    m_charsMap[(int) '>'] = 'S';
    m_charsMap[(int) '&'] = 'S';

    for (int i = 0; i < 20; i++)
    {
      m_charsMap[i] = 'S';
    }

    m_charsMap[0x0A] = 'S';
    m_charsMap[0x0D] = 'S';
    m_charsMap[9] = '\0';

    for (int i = m_maxCharacter; i < SPECIALSSIZE; i++)
    {
      m_charsMap[i] = 'S';
    }
  }

  /**
   * Flag to quickly tell if the encoding is UTF8.
   */
  boolean m_isUTF8;

  /**
   * The maximum character size before we have to resort
   * to escaping.
   */
  int m_maxCharacter = Encodings.getLastPrintable();

  /**
   * Add space before '/>' for XHTML.
   */
  public boolean m_spaceBeforeClose = false;

  /** NEEDSDOC Field DEFAULT_MIME_ENCODING          */
  static final String DEFAULT_MIME_ENCODING = "UTF-8";

  /** NEEDSDOC Field m_format          */
  protected OutputFormat m_format;

  /**
   * Default constructor.
   */
  public FormatterToXML(){}

  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   */
  public FormatterToXML(Writer writer)
  {
    m_shouldFlush = false;
    m_writer = writer;
  }

  /**
   * Constructor using an output stream, and a simple OutputFormat.
   * @param writer        The character output stream to use.
   *
   * NEEDSDOC @param os
   *
   * @throws UnsupportedEncodingException
   */
  public FormatterToXML(java.io.OutputStream os)
          throws UnsupportedEncodingException
  {

    OutputFormat of = new XMLOutputFormat();

    init(os, of);
  }

  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   *
   * NEEDSDOC @param xmlListener
   */
  public FormatterToXML(FormatterToXML xmlListener)
  {

    m_writer = xmlListener.m_writer;
    m_outputStream = xmlListener.m_outputStream;
    m_bytesEqualChars = xmlListener.m_bytesEqualChars;
    m_encoding = xmlListener.m_encoding;
    javaEncodingIsISO = xmlListener.javaEncodingIsISO;
    m_shouldNotWriteXMLHeader = xmlListener.m_shouldNotWriteXMLHeader;
    m_shouldNotWriteXMLHeader = xmlListener.m_shouldNotWriteXMLHeader;
    m_elemStack = xmlListener.m_elemStack;

    // m_lineSep = xmlListener.m_lineSep;
    // m_lineSepLen = xmlListener.m_lineSepLen;
    m_ispreserve = xmlListener.m_ispreserve;
    m_preserves = xmlListener.m_preserves;
    m_isprevtext = xmlListener.m_isprevtext;
    m_doIndent = xmlListener.m_doIndent;
    m_currentIndent = xmlListener.m_currentIndent;
    indent = xmlListener.indent;
    level = xmlListener.level;
    m_startNewLine = xmlListener.m_startNewLine;
    m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
    m_doctypeSystem = xmlListener.m_doctypeSystem;
    m_doctypePublic = xmlListener.m_doctypePublic;
    m_standalone = xmlListener.m_standalone;
    m_mediatype = xmlListener.m_mediatype;
    m_attrSpecialChars = xmlListener.m_attrSpecialChars;
    m_attrCharsMap = xmlListener.m_attrCharsMap;
    m_charsMap = xmlListener.m_charsMap;
    m_maxCharacter = xmlListener.m_maxCharacter;
    m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
    m_inCData = xmlListener.m_inCData;
    m_charBuf = xmlListener.m_charBuf;
    m_byteBuf = xmlListener.m_byteBuf;

    // m_pos = xmlListener.m_pos;
    m_pos = 0;

    initCharsMap();
  }

  /**
   * Initialize the serializer with the specified writer and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param writer The writer to use
   * @param format The output format
   */
  public synchronized void init(Writer writer, OutputFormat format)
  {
    init(writer, format, false);
  }

  /**
   * Initialize the serializer with the specified writer and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param writer The writer to use
   * @param format The output format
   * NEEDSDOC @param shouldFlush
   */
  private synchronized void init(Writer writer, OutputFormat format,
                                 boolean shouldFlush)
  {

    m_shouldFlush = shouldFlush;
    m_writer = writer;
    m_format = format;

    // This is to get around differences between Xalan and Xerces.
    // Xalan uses -1 as default for no indenting, Xerces uses 0.
    // So we just adjust the indent value here because we bumped it
    // up previously ( in StylesheetRoot);
    indent = format.getIndentAmount() - 1;
    m_doIndent = format.getIndent();
    m_shouldNotWriteXMLHeader = format.getOmitXMLDeclaration();
    m_doctypeSystem = format.getDoctypeSystemId();
    m_doctypePublic = format.getDoctypePublicId();
    m_standalone = format.getStandalone();
    m_mediatype = format.getMediaType();

    if (null != m_doctypePublic)
    {
      if (m_doctypePublic.startsWith("-//W3C//DTD XHTML"))
        m_spaceBeforeClose = true;
    }

    // initCharsMap();
    if (null == m_encoding)
      m_encoding = Encodings.getMimeEncoding(format.getEncoding());

    m_isUTF8 = m_encoding.equals(DEFAULT_MIME_ENCODING);
    m_maxCharacter = Encodings.getLastPrintable(m_encoding);

    initCharsMap();
  }

  /**
   * Initialize the serializer with the specified output stream and output format.
   * Must be called before calling any of the serialize methods.
   *
   * @param output The output stream to use
   * @param format The output format
   * @throws UnsupportedEncodingException The encoding specified
   *   in the output format is not supported
   */
  public synchronized void init(OutputStream output, OutputFormat format)
          throws UnsupportedEncodingException
  {
    if(null == format)
    {
      format = new org.apache.xalan.serialize.helpers.XMLOutputFormat();
    }
    m_encoding = Encodings.getMimeEncoding(format.getEncoding());

    if (m_encoding.equals("WINDOWS-1250") || m_encoding.equals("US-ASCII")
            || m_encoding.equals("ASCII"))
    {
      m_bytesEqualChars = true;
      m_outputStream = output;

      init((Writer) null, format, true);
    }
    else
    {
      Writer osw;

      try
      {
        osw = Encodings.getWriter(output, m_encoding);
      }
      catch (UnsupportedEncodingException uee)
      {
        System.out.println("Warning: encoding \"" + m_encoding
                           + "\" not supported" + ", using "
                           + Encodings.DEFAULT_MIME_ENCODING);

        m_encoding = Encodings.DEFAULT_MIME_ENCODING;
        osw = Encodings.getWriter(output, m_encoding);
      }

      m_maxCharacter = Encodings.getLastPrintable(m_encoding);

      init(osw, format, true);
    }
  }

  /**
   * Receive an object for locating the origin of SAX document events.
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator)
  {

    // I don't do anything with this yet.
  }

  /**
   * Output the doc type declaration.
   *
   * NEEDSDOC @param name
   *
   * @throws org.xml.sax.SAXException
   */
  void outputDocTypeDecl(String name) throws org.xml.sax.SAXException
  {

    accum("<!DOCTYPE ");
    accum(name);

    if (null != m_doctypePublic)
    {
      accum(" PUBLIC \"");
      accum(m_doctypePublic);
      accum("\"");
    }

    if (null == m_doctypePublic)
      accum(" SYSTEM \"");
    else
      accum(" \"");

    accum(m_doctypeSystem);
    accum("\">");
    outputLineSep();
  }

  /**
   * Receive notification of the beginning of a document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    m_needToOutputDocTypeDecl = true;
    m_startNewLine = false;

    if (m_shouldNotWriteXMLHeader == false)
    {
      String encoding = Encodings.getMimeEncoding(m_encoding);
      String version = (null == m_version) ? "1.0" : m_version;
      String standalone = (m_standalone) ? " standalone=\"yes\"" : "";

      accum("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\""
            + standalone + "?>");
      outputLineSep();
    }
  }

  /**
   * Receive notification of the end of a document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {

    if (m_doIndent &&!m_isprevtext)
    {
      outputLineSep();
    }

    flush();
    flushWriter();
  }

  /**
   * Report the start of DTD declarations, if any.
   *
   * Any declarations are assumed to be in the internal subset
   * unless otherwise indicated.
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception org.xml.sax.SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD(String name, String publicId, String systemId)
          throws org.xml.sax.SAXException
  {

    // Do nothing for now.
  }

  /**
   * Report the end of DTD declarations.
   *
   * @exception org.xml.sax.SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD() throws org.xml.sax.SAXException
  {

    // Do nothing for now.
  }

  /**
   * NEEDSDOC Method startPrefixMapping 
   *
   *
   * NEEDSDOC @param prefix
   * NEEDSDOC @param uri
   *
   * @throws org.xml.sax.SAXException
   */
  public void startPrefixMapping(String prefix, String uri)
          throws org.xml.sax.SAXException{}

  /**
   * NEEDSDOC Method endPrefixMapping 
   *
   *
   * NEEDSDOC @param prefix
   *
   * @throws org.xml.sax.SAXException
   */
  public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException{}

  /**
   * NEEDSDOC Method subPartMatch 
   *
   *
   * NEEDSDOC @param p
   * NEEDSDOC @param t
   *
   * NEEDSDOC (subPartMatch) @return
   */
  protected static final boolean subPartMatch(String p, String t)
  {
    return (p == t) || ((null != p) && (p.equals(t)));
  }

  /**
   * Push a boolean state based on if the name of the element
   * is found in the list of qnames.  A state is always pushed,
   * one way or the other.
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   * NEEDSDOC @param qnames
   * NEEDSDOC @param state
   */
  protected void pushState(String namespaceURI, String localName,
                           QName[] qnames, BoolStack state)
  {

    boolean b;

    if (null != qnames)
    {
      b = false;

      if ((null != namespaceURI) && namespaceURI.length() == 0)
        namespaceURI = null;

      int nElems = qnames.length;

      for (int i = 0; i < nElems; i++)
      {
        QName q = qnames[i];

        if (q.getLocalName().equals(localName)
                && subPartMatch(namespaceURI, q.getNamespaceURI()))
        {
          b = true;
          ;

          break;
        }
      }
    }
    else
    {
      b = state.peekOrFalse();
    }

    state.push(b);
  }

  /**
   * Receive notification of the beginning of an element.
   *
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   * @param name The element type name.
   * @param atts The attributes attached to the element, if any.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endElement
   * @see org.xml.sax.AttributeList
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String namespaceURI, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    if ((true == m_needToOutputDocTypeDecl) && (null != m_doctypeSystem))
    {
      outputDocTypeDecl(name);
    }

    m_needToOutputDocTypeDecl = false;

    writeParentTagEnd();
    pushState(namespaceURI, localName, m_format.getCDataElements(),
              m_cdataSectionStates);
    pushState(namespaceURI, localName, m_format.getNonEscapingElements(),
              m_disableOutputEscapingStates);

    m_ispreserve = false;

    //  System.out.println(name+": m_doIndent = "+m_doIndent+", m_ispreserve = "+m_ispreserve+", m_isprevtext = "+m_isprevtext);
    if (shouldIndent() && m_startNewLine)
    {
      indent(m_currentIndent);
    }

    m_startNewLine = true;

    accum('<');
    accum(name);

    int nAttrs = atts.getLength();

    for (int i = 0; i < nAttrs; i++)
    {
      processAttribute(atts.getQName(i), atts.getValue(i));
    }

    // Flag the current element as not yet having any children.
    openElementForChildren();

    m_currentIndent += indent;
    m_isprevtext = false;
  }

  /**
   * Check to see if a parent's ">" has been written, and, if
   * it has not, write it.
   *
   * @throws org.xml.sax.SAXException
   */
  protected void writeParentTagEnd() throws org.xml.sax.SAXException
  {

    if (!m_elemStack.isEmpty())
    {

      // See if the parent element has already been flagged as having children.
      if ((false == m_elemStack.peek()))
      {
        accum('>');

        m_isprevtext = false;

        m_elemStack.pop();
        m_elemStack.push(true);
        m_preserves.push(m_ispreserve);
      }
    }
  }

  /**
   * Flag the current element as not yet having any
   * children.
   */
  protected void openElementForChildren()
  {

    // Flag the current element as not yet having any children.
    m_elemStack.push(false);
  }

  /**
   * Tell if child nodes have been added to the current
   * element.  Must be called in balance with openElementForChildren().
   *
   * NEEDSDOC ($objectName$) @return
   */
  protected boolean childNodesWereAdded()
  {
    return m_elemStack.isEmpty() ? false : m_elemStack.pop();
  }

  /**
   * Receive notification of the end of an element.
   *
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   * @param name The element type name
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String namespaceURI, String localName, String name)
          throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    m_currentIndent -= indent;

    boolean hasChildNodes = childNodesWereAdded();

    if (hasChildNodes)
    {
      if (shouldIndent())
        indent(m_currentIndent);

      accum('<');
      accum('/');
      accum(name);
      accum('>');
    }
    else
    {
      if (m_spaceBeforeClose)
        accum(" />");
      else
        accum("/>");
    }

    if (hasChildNodes)
    {
      m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
    }

    m_isprevtext = false;

    m_disableOutputEscapingStates.pop();
    m_cdataSectionStates.pop();
  }

  /**
   * Process an attribute.
   * @param   name   The name of the attribute.
   * @param   value   The value of the attribute.
   *
   * @throws org.xml.sax.SAXException
   */
  protected void processAttribute(String name, String value)
          throws org.xml.sax.SAXException
  {

    accum(' ');
    accum(name);
    accum("=\"");
    writeAttrString(value, m_encoding);
    accum('\"');
  }

  /**
   * Starts an un-escaping section. All characters printed within an
   * un-escaping section are printed as is, without escaping special
   * characters into entity references. Only XML and HTML serializers
   * need to support this method.
   * <p>
   * The contents of the un-escaping section will be delivered through
   * the regular <tt>characters</tt> event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startNonEscaping() throws org.xml.sax.SAXException
  {
    m_disableOutputEscapingStates.push(true);
  }

  /**
   * Ends an un-escaping section.
   *
   * @see #startNonEscaping
   *
   * @throws org.xml.sax.SAXException
   */
  public void endNonEscaping() throws org.xml.sax.SAXException
  {
    m_disableOutputEscapingStates.pop();
  }

  /**
   * Starts a whitespace preserving section. All characters printed
   * within a preserving section are printed without indentation and
   * without consolidating multiple spaces. This is equivalent to
   * the <tt>xml:space=&quot;preserve&quot;</tt> attribute. Only XML
   * and HTML serializers need to support this method.
   * <p>
   * The contents of the whitespace preserving section will be delivered
   * through the regular <tt>characters</tt> event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startPreserving() throws org.xml.sax.SAXException
  {

    // Not sure this is really what we want.  -sb
    m_preserves.push(true);

    m_ispreserve = true;
  }

  /**
   * Ends a whitespace preserving section.
   *
   * @see #startPreserving
   *
   * @throws org.xml.sax.SAXException
   */
  public void endPreserving() throws org.xml.sax.SAXException
  {

    // Not sure this is really what we want.  -sb
    m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
  }

  /**
   * Receive notification of a processing instruction.
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    if (target.equals(javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING))
    {
      startNonEscaping();
    }
    else if (target.equals(javax.xml.transform.Result.PI_ENABLE_OUTPUT_ESCAPING))
    {
      endNonEscaping();
    }
    else
    {
      writeParentTagEnd();

      if (shouldIndent())
        indent(m_currentIndent);

      accum('<');
      accum('?');
      accum(target);

      if (data.length() > 0 &&!Character.isSpaceChar(data.charAt(0)))
        accum(' ');

      accum(data);
      accum('?');
      accum('>');

      m_startNewLine = true;
    }
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception org.xml.sax.SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length) throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    writeParentTagEnd();

    if (shouldIndent())
      indent(m_currentIndent);

    accum("<!--");
    accum(ch, start, length);
    accum("-->");

    m_startNewLine = true;
  }

  /** NEEDSDOC Field m_inCData          */
  protected boolean m_inCData = false;

  /**
   * Report the start of a CDATA section.
   *
   * @exception org.xml.sax.SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA() throws org.xml.sax.SAXException
  {
    m_inCData = true;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @exception org.xml.sax.SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA() throws org.xml.sax.SAXException
  {
    m_inCData = false;
  }

  /**
   * Receive notification of cdata.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   *
   * @throws org.xml.sax.SAXException
   */
  public void cdata(char ch[], int start, int length) throws org.xml.sax.SAXException
  {

    try
    {
      writeParentTagEnd();

      m_ispreserve = true;

      if (shouldIndent())
        indent(m_currentIndent);

      boolean writeCDataBrackets = (((length >= 1)
                                     && (ch[start] <= m_maxCharacter)));

      if (writeCDataBrackets)
      {
        accum("<![CDATA[");
      }

      // accum(ch, start, length);
      if (isEscapingDisabled())
      {
        charactersRaw(ch, start, length);
      }
      else
        writeNormalizedChars(ch, start, length, true);

      if (writeCDataBrackets)
      {
        accum("]]>");
      }
    }
    catch (IOException ioe)
    {
      throw new org.xml.sax.SAXException(
        XSLMessages.createXPATHMessage(XPATHErrorResources.ER_OIERROR, null),
        ioe);  //"IO error", ioe);
    }
  }

  /** NEEDSDOC Field MAXCHARBUF          */
  static final int MAXCHARBUF = (4 * 1024);

  /** NEEDSDOC Field NUMBERBYTESTOWRITEDIRECT          */
  static final int NUMBERBYTESTOWRITEDIRECT = (1024);

  /** NEEDSDOC Field m_charBuf          */
  protected char[] m_charBuf = new char[MAXCHARBUF];

  /** NEEDSDOC Field m_byteBuf          */
  protected byte[] m_byteBuf = new byte[MAXCHARBUF];

  /** NEEDSDOC Field m_pos          */
  protected int m_pos = 0;

  /**
   * Append a byte to the buffer.
   *
   * NEEDSDOC @param b
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(byte b) throws org.xml.sax.SAXException
  {

    if (m_bytesEqualChars)
    {
      m_byteBuf[m_pos++] = b;

      if (m_pos >= MAXCHARBUF)
        flushBytes();
    }
    else
    {
      m_charBuf[m_pos++] = (char) b;

      if (m_pos >= MAXCHARBUF)
        flushChars();
    }
  }

  /**
   * Append a character to the buffer.
   *
   * NEEDSDOC @param b
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(char b) throws org.xml.sax.SAXException
  {

    if (m_bytesEqualChars)
    {
      m_byteBuf[m_pos++] = (byte) b;

      if (m_pos >= MAXCHARBUF)
        flushBytes();
    }
    else
    {
      m_charBuf[m_pos++] = b;

      if (m_pos >= MAXCHARBUF)
        flushChars();
    }
  }

  /**
   * Append a character to the buffer.
   *
   * NEEDSDOC @param chars
   * NEEDSDOC @param start
   * NEEDSDOC @param length
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(char chars[], int start, int length)
          throws org.xml.sax.SAXException
  {

    int n = start + length;

    if (m_bytesEqualChars)
    {
      for (int i = start; i < n; i++)
      {
        m_byteBuf[m_pos++] = (byte) chars[i];

        if (m_pos >= MAXCHARBUF)
          flushBytes();
      }
    }
    else
    {
      if (length >= NUMBERBYTESTOWRITEDIRECT)
      {
        if (m_pos != 0)
          flushChars();

        try
        {
          m_writer.write(chars, start, length);
        }
        catch (IOException ioe)
        {
          throw new org.xml.sax.SAXException(ioe);
        }
      }
      else
      {
        if ((m_pos + length) >= MAXCHARBUF)
          flushChars();

        // if(1 == length)
        //   m_charBuf[m_pos] = chars[start];
        // else
        System.arraycopy(chars, start, m_charBuf, m_pos, length);

        m_pos += length;
      }
    }
  }

  /**
   * Append a character to the buffer.
   *
   * NEEDSDOC @param s
   *
   * @throws org.xml.sax.SAXException
   */
  protected final void accum(String s) throws org.xml.sax.SAXException
  {

    int n = s.length();

    if (m_bytesEqualChars)
    {
      char[] chars = s.toCharArray();

      for (int i = 0; i < n; i++)
      {
        m_byteBuf[m_pos++] = (byte) chars[i];
        ;

        if (m_pos >= MAXCHARBUF)
          flushBytes();
      }
    }
    else
    {
      if (n >= NUMBERBYTESTOWRITEDIRECT)
      {
        if (m_pos != 0)
          flushChars();

        try
        {
          m_writer.write(s);
        }
        catch (IOException ioe)
        {
          throw new org.xml.sax.SAXException(ioe);
        }
      }
      else
      {
        for (int i = 0; i < n; i++)
        {
          m_charBuf[m_pos++] = s.charAt(i);
          ;

          if (m_pos >= MAXCHARBUF)
            flushChars();
        }
      }
    }
  }

  /**
   * NEEDSDOC Method flushBytes 
   *
   *
   * @throws org.xml.sax.SAXException
   */
  private final void flushBytes() throws org.xml.sax.SAXException
  {

    try
    {
      m_outputStream.write(m_byteBuf, 0, m_pos);

      m_pos = 0;
    }
    catch (IOException ioe)
    {
      throw new org.xml.sax.SAXException(ioe);
    }
  }

  /**
   * Flush the formatter's result stream.
   *
   * @throws org.xml.sax.SAXException
   */
  public final void flushWriter() throws org.xml.sax.SAXException
  {

    if (m_shouldFlush && (null != m_writer))
    {
      try
      {
        m_writer.flush();
      }
      catch (IOException ioe)
      {
        throw new org.xml.sax.SAXException(ioe);
      }
    }
  }

  /**
   * NEEDSDOC Method flushChars 
   *
   *
   * @throws org.xml.sax.SAXException
   */
  private final void flushChars() throws org.xml.sax.SAXException
  {

    try
    {
      m_writer.write(m_charBuf, 0, m_pos);

      m_pos = 0;
    }
    catch (IOException ioe)
    {
      throw new org.xml.sax.SAXException(ioe);
    }
  }

  /**
   * NEEDSDOC Method flush 
   *
   *
   * @throws org.xml.sax.SAXException
   */
  public final void flush() throws org.xml.sax.SAXException
  {

    if (m_bytesEqualChars)
    {
      flushBytes();
    }
    else
    {
      flushChars();
    }
  }

  /**
   * Receive notification of character data.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param chars The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char chars[], int start, int length)
          throws org.xml.sax.SAXException
  {
    if (m_inEntityRef)
      return;

    if (0 == length)
      return;

    if (isCDataSection())
    {
      cdata(chars, start, length);

      return;
    }

    if (isEscapingDisabled())
    {
      charactersRaw(chars, start, length);

      return;
    }

    writeParentTagEnd();

    int startClean = start;
    int lengthClean = 0;

    // int pos = 0;
    int end = start + length;
    boolean checkWhite = true;

    for (int i = start; i < end; i++)
    {
      char ch = chars[i];
      
      if(checkWhite)
      {
        if(!Character.isWhitespace(ch))
        {
          m_ispreserve = true;
          checkWhite = false;
        }
      }

      if ((ch < SPECIALSSIZE) && (m_charsMap[ch] != 'S'))
      {

        // accum(ch);
        lengthClean++;
      }
      else
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = accumDefaultEscape(ch, i, chars, end, false);
        i = startClean - 1;
      }
    }

    if (lengthClean > 0)
    {
      accum(chars, startClean, lengthClean);
    }
    m_isprevtext = true;
  }

  /**
   * If available, when the disable-output-escaping attribute is used,
   * output raw text without escaping.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param start
   * NEEDSDOC @param length
   *
   * @throws org.xml.sax.SAXException
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    if (m_inEntityRef)
      return;

    writeParentTagEnd();

    m_ispreserve = true;

    accum(ch, start, length);
  }

  /**
   * Normalize the characters, but don't escape.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param start
   * NEEDSDOC @param length
   * NEEDSDOC @param isCData
   *
   * @throws IOException
   * @throws org.xml.sax.SAXException
   */
  void writeNormalizedChars(char ch[], int start, int length, boolean isCData)
          throws IOException, org.xml.sax.SAXException
  {

    int end = start + length;

    for (int i = start; i < end; i++)
    {
      char c = ch[i];

      if ((0x0D == c) && ((i + 1) < end) && (0x0A == ch[i + 1]))
      {
        outputLineSep();

        i++;
      }
      else if ((0x0A == c) && ((i + 1) < end) && (0x0D == ch[i + 1]))
      {
        outputLineSep();

        i++;
      }
      else if ('\n' == c)
      {
        outputLineSep();
      }
      else if (isCData && (c > m_maxCharacter))
      {
        if (i != 0)
          accum("]]>");

        // This needs to go into a function... 
        if (0xd800 <= ((int) c) && ((int) c) < 0xdc00)
        {

          // UTF-16 surrogate
          int next;

          if (i + 1 >= end)
          {
            throw new org.xml.sax.SAXException(
              XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                new Object[]{ Integer.toHexString((int) c) }));  //"Invalid UTF-16 surrogate detected: "

            //+Integer.toHexString((int)c)+ " ?");
          }
          else
          {
            next = ch[++i];

            if (!(0xdc00 <= next && next < 0xe000))
              throw new org.xml.sax.SAXException(
                XSLMessages.createXPATHMessage(
                  XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                  new Object[]{
                    Integer.toHexString((int) c) + " "
                    + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "

            //+Integer.toHexString((int)c)+" "+Integer.toHexString(next));
            next = ((c - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
          }

          accum('&');
          accum('#');

          // accum('x');
          accum(Integer.toString(next));
          accum(';');
        }
        else
        {
          accum("&#");

          String intStr = Integer.toString((int) c);

          accum(intStr);
          accum(';');
        }

        if ((i != 0) && (i < (end - 1)))
          accum("<![CDATA[");
      }
      else if (isCData
               && ((i < (end - 2)) && (']' == c) && (']' == ch[i + 1])
                   && ('>' == ch[i + 2])))
      {
        accum("]]]]><![CDATA[>");

        i += 2;
      }
      else
      {
        if (c <= m_maxCharacter)
        {
          accum(c);
        }

        // This needs to go into a function... 
        else if (0xd800 <= ((int) c) && ((int) c) < 0xdc00)
        {

          // UTF-16 surrogate
          int next;

          if (i + 1 >= end)
          {
            throw new org.xml.sax.SAXException(
              XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                new Object[]{ Integer.toHexString((int) c) }));  //"Invalid UTF-16 surrogate detected: "

            //+Integer.toHexString((int)c)+ " ?");
          }
          else
          {
            next = ch[++i];

            if (!(0xdc00 <= next && next < 0xe000))
              throw new org.xml.sax.SAXException(
                XSLMessages.createXPATHMessage(
                  XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                  new Object[]{
                    Integer.toHexString((int) c) + " "
                    + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "

            //+Integer.toHexString((int)c)+" "+Integer.toHexString(next));
            next = ((c - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
          }

          accum("&#");
          accum(Integer.toString(next));
          accum(";");
        }
        else
        {
          accum("&#");

          String intStr = Integer.toString((int) c);

          accum(intStr);
          accum(';');
        }
      }
    }
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * Not sure how to get this invoked quite yet.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    if (0 == length)
      return;

    characters(ch, start, length);
  }

  /**
   * NEEDSDOC Method skippedEntity 
   *
   *
   * NEEDSDOC @param name
   *
   * @throws org.xml.sax.SAXException
   */
  public void skippedEntity(String name) throws org.xml.sax.SAXException
  {

    // TODO: Should handle
  }

  /**
   * Report the beginning of an entity.
   *
   * The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @exception org.xml.sax.SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.misc.DeclHandler#internalEntityDecl
   * @see org.xml.sax.misc.DeclHandler#externalEntityDecl
   */
  public void startEntity(String name) throws org.xml.sax.SAXException
  {

    entityReference(name);

    m_inEntityRef = true;
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception org.xml.sax.SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity(String name) throws org.xml.sax.SAXException
  {
    m_inEntityRef = false;
  }

  /**
   * Receive notivication of a entityReference.
   *
   * NEEDSDOC @param name
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {

    writeParentTagEnd();

    if (shouldIndent())
      indent(m_currentIndent);

    accum("&");
    accum(name);
    accum(";");
  }

  /**
   * Handle one of the default entities, return false if it
   * is not a default entity.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param i
   * NEEDSDOC @param chars
   * NEEDSDOC @param len
   * NEEDSDOC @param escLF
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  final int accumDefaultEntity(
          char ch, int i, char[] chars, int len, boolean escLF)
            throws org.xml.sax.SAXException
  {

    if (!escLF && (0x0D == ch) && ((i + 1) < len) && (0x0A == chars[i + 1]))
    {
      outputLineSep();

      i++;
    }
    else if (!escLF && (0x0A == ch) && ((i + 1) < len)
             && (0x0D == chars[i + 1]))
    {
      outputLineSep();

      i++;
    }
    else if (!escLF && 0x0D == ch)
    {
      outputLineSep();

      i++;
    }
    else if (!escLF && '\n' == ch)
    {
      outputLineSep();
    }
    else if ('<' == ch)
    {
      accum('&');
      accum('l');
      accum('t');
      accum(';');
    }
    else if ('>' == ch)
    {
      accum('&');
      accum('g');
      accum('t');
      accum(';');
    }
    else if ('&' == ch)
    {
      accum('&');
      accum('a');
      accum('m');
      accum('p');
      accum(';');
    }
    else if ('"' == ch)
    {
      accum('&');
      accum('q');
      accum('u');
      accum('o');
      accum('t');
      accum(';');
    }
    else if ('\'' == ch)
    {
      accum('&');
      accum('a');
      accum('p');
      accum('o');
      accum('s');
      accum(';');
    }
    else
    {
      return i;
    }

    return i + 1;
  }

  /**
   * Escape and accum a character.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param i
   * NEEDSDOC @param chars
   * NEEDSDOC @param len
   * NEEDSDOC @param escLF
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws org.xml.sax.SAXException
   */
  final int accumDefaultEscape(
          char ch, int i, char[] chars, int len, boolean escLF)
            throws org.xml.sax.SAXException
  {

    int pos = accumDefaultEntity(ch, i, chars, len, escLF);

    if (i == pos)
    {
      pos++;

      if (0xd800 <= ch && ch < 0xdc00)
      {

        // UTF-16 surrogate
        int next;

        if (i + 1 >= len)
        {
          throw new org.xml.sax.SAXException(
            XSLMessages.createXPATHMessage(
              XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
              new Object[]{ Integer.toHexString(ch) }));  //"Invalid UTF-16 surrogate detected: "

          //+Integer.toHexString(ch)+ " ?");
        }
        else
        {
          next = chars[++i];

          if (!(0xdc00 <= next && next < 0xe000))
            throw new org.xml.sax.SAXException(
              XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                new Object[]{
                  Integer.toHexString(ch) + " "
                  + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "

          //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
          next = ((ch - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
        }

        accum("&#");
        accum(Integer.toString(next));
        accum(";");

        /*} else if (null != ctbc && !ctbc.canConvert(ch)) {
        sb.append("&#x");
        sb.append(Integer.toString((int)ch, 16));
        sb.append(";");*/
      }
      else
      {
        if (ch > m_maxCharacter
                || ((ch < SPECIALSSIZE) && (m_attrCharsMap[ch] == 'S')))
        {
          accum("&#");
          accum(Integer.toString(ch));
          accum(";");
        }
        else
        {
          accum(ch);
        }
      }
    }

    return pos;
  }

  /**
   * Returns the specified <var>string</var> after substituting <VAR>specials</VAR>,
   * and UTF-16 surrogates for chracter references <CODE>&amp;#xnn</CODE>.
   *
   * @param   string      String to convert to XML format.
   * @param   specials    Chracters, should be represeted in chracter referenfces.
   * @param   encoding    CURRENTLY NOT IMPLEMENTED.
   * @return              XML-formatted string.
   * @see #backReference
   *
   * @throws org.xml.sax.SAXException
   */
  public void writeAttrString(String string, String encoding)
          throws org.xml.sax.SAXException
  {

    char[] stringChars = string.toCharArray();
    int len = stringChars.length;

    for (int i = 0; i < len; i++)
    {
      char ch = stringChars[i];

      if ((ch < SPECIALSSIZE) && (m_attrCharsMap[ch] != 'S'))
        accum(ch);
      else
        accumDefaultEscape(ch, i, stringChars, len, true);
    }
  }

  /**
   * NEEDSDOC Method shouldIndent 
   *
   *
   * NEEDSDOC (shouldIndent) @return
   */
  protected boolean shouldIndent()
  {
    return m_doIndent && (!m_ispreserve &&!m_isprevtext);
  }

  /**
   * Prints <var>n</var> spaces.
   * @param pw        The character output stream to use.
   * @param n         Number of spaces to print.
   * @exception IOException   Thrown if <var>pw</var> is invalid.
   *
   * @throws org.xml.sax.SAXException
   */
  public void printSpace(int n) throws org.xml.sax.SAXException
  {

    for (int i = 0; i < n; i++)
    {
      accum(' ');
    }
  }

  /**
   * Prints a newline character and <var>n</var> spaces.
   * @param pw        The character output stream to use.
   * @param n         Number of spaces to print.
   * @exception IOException   Thrown if <var>pw</var> is invalid.
   *
   * @throws org.xml.sax.SAXException
   */
  public void indent(int n) throws org.xml.sax.SAXException
  {

    if (m_startNewLine)
      outputLineSep();

    if (m_doIndent)
    {
      printSpace(n);
    }
  }

  /**
   * Specifies an output stream to which the document should be
   * serialized. This method should not be called while the
   * serializer is in the process of serializing a document.
   * <p>
   * The encoding specified in the {@link OutputFormat} is used, or
   * if no encoding was specified, the default for the selected
   * output method.
   *
   * @param output The output stream
   */
  public void setOutputStream(OutputStream output)
  {

    try
    {
      init(output, m_format);
    }
    catch (UnsupportedEncodingException uee)
    {

      // Should have been warned in init, I guess...
    }
  }

  /**
   * NEEDSDOC Method getOutputStream 
   *
   *
   * NEEDSDOC (getOutputStream) @return
   */
  public OutputStream getOutputStream()
  {
    return m_outputStream;
  }

  /**
   * Specifies a writer to which the document should be serialized.
   * This method should not be called while the serializer is in
   * the process of serializing a document.
   * <p>
   * The encoding specified for the {@link OutputFormat} must be
   * identical to the output format used with the writer.
   *
   * @param writer The output writer stream
   */
  public void setWriter(Writer writer)
  {
    m_writer = writer;
  }

  /**
   * NEEDSDOC Method getWriter 
   *
   *
   * NEEDSDOC (getWriter) @return
   */
  public Writer getWriter()
  {
    return m_writer;
  }

  /**
   * Specifies an output format for this serializer. It the
   * serializer has already been associated with an output format,
   * it will switch to the new format. This method should not be
   * called while the serializer is in the process of serializing
   * a document.
   *
   * @param format The output format to use
   */
  public void setOutputFormat(OutputFormat format)
  {

    boolean shouldFlush = m_shouldFlush;

    init(m_writer, format, false);

    m_shouldFlush = shouldFlush;
  }

  /**
   * Returns the output format for this serializer.
   *
   * @return The output format in use
   */
  public OutputFormat getOutputFormat()
  {
    return m_format;
  }

  /**
   * Return a {@link DocumentHandler} interface into this serializer.
   * If the serializer does not support the {@link DocumentHandler}
   * interface, it should return null.
   *
   * @return A {@link DocumentHandler} interface into this serializer,
   *  or null if the serializer is not SAX 1 capable
   * @throws IOException An I/O exception occured
   */
  public DocumentHandler asDocumentHandler() throws IOException
  {
    return null;  // at least for now
  }

  /**
   * Return a {@link ContentHandler} interface into this serializer.
   * If the serializer does not support the {@link ContentHandler}
   * interface, it should return null.
   *
   * @return A {@link ContentHandler} interface into this serializer,
   *  or null if the serializer is not SAX 2 capable
   * @throws IOException An I/O exception occured
   */
  public ContentHandler asContentHandler() throws IOException
  {
    return this;
  }

  /**
   * Return a {@link DOMSerializer} interface into this serializer.
   * If the serializer does not support the {@link DOMSerializer}
   * interface, it should return null.
   *
   * @return A {@link DOMSerializer} interface into this serializer,
   *  or null if the serializer is not DOM capable
   * @throws IOException An I/O exception occured
   */
  public DOMSerializer asDOMSerializer() throws IOException
  {
    return this;  // for now
  }

  /**
   * Resets the serializer. If this method returns true, the
   * serializer may be used for subsequent serialization of new
   * documents. It is possible to change the output format and
   * output stream prior to serializing, or to use the existing
   * output format and output stream.
   *
   * @return True if serializer has been reset and can be reused
   */
  public boolean reset()
  {
    return false;
  }
  
  /**
   * Serializes the DOM node. Throws an exception only if an I/O
   * exception occured while serializing.
   *
   * @param elem The element to serialize
   * @throws IOException An I/O exception occured while serializing
   */
  public void serialize(Node node) throws IOException
  {
    try
    {
      TreeWalker walker = new TreeWalker(this);
      walker.traverse(node);
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new WrappedRuntimeException(se);
    }
  }

}  //ToXMLStringVisitor





