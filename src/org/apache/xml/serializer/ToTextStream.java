/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES{} LOSS OF
 * USE, DATA, OR PROFITS{} OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Santiago Pericas-Geertsen
 *
 */
package org.apache.xml.serializer;

import java.io.IOException;

import org.apache.xml.serializer.CharInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ToTextStream extends ToStream 
{ 

       
  /**
   * Default constructor.
   */
  public ToTextStream()
  {
    super();
  }

 
 
  /**
   * Receive notification of the beginning of a document.
   *
   * <p>The SAX parser will invoke this method only once, before any
   * other methods in this interface or in DTDHandler (except for
   * setDocumentLocator).</p>
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  protected void startDocumentInternal() throws org.xml.sax.SAXException
  {
    super.startDocumentInternal();

    m_needToCallStartDocument = false;

    // No action for the moment.
  }

  /**
   * Receive notification of the end of a document.
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {
    flushPending();
    flushWriter();
    if (m_tracer != null)
        super.fireEndDoc();
  }

  /**
   * Receive notification of the beginning of an element.
   *
   * <p>The Parser will invoke this method at the beginning of every
   * element in the XML document; there will be a corresponding
   * endElement() event for every startElement() event (even when the
   * element is empty). All of the element's content will be
   * reported, in order, before the corresponding endElement()
   * event.</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached.  Note that the attribute list provided will
   * contain only attributes with explicit values (specified or
   * defaulted): #IMPLIED attributes will be omitted.</p>
   *
   *
   * @param namespaceURI The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param name The qualified name (with prefix), or the
   *        empty string if qualified names are not available.
   * @param atts The attributes attached to the element, if any.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
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
    // time to fire off startElement event
    if (m_tracer != null) {
        super.fireStartElem(name);
        this.firePseudoAttributes();
    }
    return;
  }

  /**
   * Receive notification of the end of an element.
   *
   * <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding
   * startElement() event for every endElement() event (even when the
   * element is empty).</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached to the name.</p>
   *
   *
   * @param namespaceURI The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param name The qualified name (with prefix), or the
   *        empty string if qualified names are not available.
   * @param name The element type name
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String namespaceURI, String localName, String name)
          throws org.xml.sax.SAXException
  {
        if (m_tracer != null)
            super.fireEndElem(name);           
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
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   */
  public void characters(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    // this.accum(ch, start, length);
    flushPending();    
    
    try
    {
        writeNormalizedChars(ch, start, length, false);
        if (m_tracer != null)
            super.fireCharEvent(ch, start, length);      
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * If available, when the disable-output-escaping attribute is used,
   * output raw text without escaping.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    try
    {
      writeNormalizedChars(ch, start, length, false);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }
  
/**
 * Normalize the characters, but don't escape.  Different from 
 * SerializerToXML#writeNormalizedChars because it does not attempt to do 
 * XML escaping at all.
 *
 * @param ch The characters from the XML document.
 * @param start The start position in the array.
 * @param length The number of characters to read from the array.
 * @param isCData true if a CDATA block should be built around the characters.
 *
 * @throws IOException
 * @throws org.xml.sax.SAXException
 */
void writeNormalizedChars(
    final char ch[],
    final int start,
    final int length,
    final boolean isCData)
    throws IOException, org.xml.sax.SAXException
{
    final java.io.Writer writer = m_writer;
    final int end = start + length;

    /* copy a few "constants" before the loop for performance */
    final char S_LINEFEED = CharInfo.S_LINEFEED;
    final int M_MAXCHARACTER = this.m_maxCharacter;

    if (isCData)
    {
        for (int i = start; i < end; i++)
        {
            final char c = ch[i];

            if (S_LINEFEED == c)
            {
                writer.write(m_lineSep, 0, m_lineSepLen);
            }
            else if (c > M_MAXCHARACTER)
            {
                if (i != 0)
                    closeCDATA();

                // This needs to go into a function...
                if (isUTF16Surrogate(c))
                {
                    i = writeUTF16Surrogate(c, ch, i, end);
                }
                else
                {
                    writer.write(c);
                }

                if ((i != 0) && (i < (end - 1)))
                {
                    writer.write(CDATA_DELIMITER_OPEN);
                    m_cdataTagOpen = true;
                }
            }
            else if (
                ((i < (end - 2))
                    && (']' == c)
                    && (']' == ch[i + 1])
                    && ('>' == ch[i + 2])))
            {
                writer.write(CDATA_CONTINUE);
                i += 2;
            }
            else
            {
                if (c <= M_MAXCHARACTER)
                {
                    writer.write(c);
                }

                else if (isUTF16Surrogate(c))
                {
                    i = writeUTF16Surrogate(c, ch, i, end);
                }
                else
                {
                    writer.write(c);
                }
            }
        }
    }
    else
    {
        // not in CDATA section
        for (int i = start; i < end; i++)
        {
            final char c = ch[i];

            if (S_LINEFEED == c)
            {
                writer.write(m_lineSep, 0, m_lineSepLen);
            }
            else if (c <= M_MAXCHARACTER)
            {
                writer.write(c);
            }
            else if (isUTF16Surrogate(c))
            {
                i = writeUTF16Surrogate(c, ch, i, end);
            }
            else
            {
                writer.write(c);
            }
        }
    }
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
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   */
  public void cdata(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {
    try
    {
        writeNormalizedChars(ch, start, length, false);
        if (m_tracer != null)
            super.fireCDATAEvent(ch, start, length);              
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>Validating Parsers must use this method to report each chunk
   * of ignorable whitespace (see the W3C XML 1.0 recommendation,
   * section 2.10): non-validating parsers may also use this method
   * if they are capable of parsing and using content models.</p>
   *
   * <p>SAX parsers may return all contiguous whitespace in a single
   * chunk, or they may split it into several chunks; however, all of
   * the characters in any single event must come from the same
   * external entity, so that the Locator provides useful
   * information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    try
    {
      writeNormalizedChars(ch, start, length, false);
    }
    catch(IOException ioe)
    {
      throw new SAXException(ioe);
    }
  }

  /**
   * Receive notification of a processing instruction.
   *
   * <p>The Parser will invoke this method once for each processing
   * instruction found: note that processing instructions may occur
   * before or after the main document element.</p>
   *
   * <p>A SAX parser should never report an XML declaration (XML 1.0,
   * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
   * using this method.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {
    // flush anything pending first
    flushPending();  
    
    if (m_tracer != null)
        super.fireEscapingEvent(target, data);  
  }

  /**
   * Called when a Comment is to be constructed.
   * Note that Xalan will normally invoke the other version of this method.
   * %REVIEW% In fact, is this one ever needed, or was it a mistake?
   *
   * @param   data  The comment data.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void comment(String data) throws org.xml.sax.SAXException
  {
    comment(data.toCharArray(), 0, data.length());
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
   * @throws org.xml.sax.SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    flushPending();
    if (m_tracer != null)
        super.fireCommentEvent(ch, start, length);
  }

  /**
   * Receive notivication of a entityReference.
   *
   * @param name non-null reference to the name of the entity.
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {
        if (m_tracer != null)
            super.fireEntityReference(name);    
  }
  
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#addAttribute(String, String, String, String, String)
     */
    public void addAttribute(
        String uri,
        String localName,
        String rawName,
        String type,
        String value)
    {
        // do nothing, just forget all about the attribute
    }
 
    /**
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException
    {
        // do nothing
    }

    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#endElement(String)
     */
    public void endElement(String elemName) throws SAXException
    {
        if (m_tracer != null)
            super.fireEndElem(elemName);                       
    }
 
    /**
     * From XSLTC
     */
    public void startElement(
    String elementNamespaceURI,
    String elementLocalName,
    String elementName) 
    throws SAXException 
    {
        if (m_needToCallStartDocument)
            startDocumentInternal();        
        // time to fire off startlement event.
        if (m_tracer != null) {
            super.fireStartElem(elementName);
            this.firePseudoAttributes();
        }
        
        return;
    }


    /**
     * From XSLTC
     */
    public void characters(String characters) 
    throws SAXException 
    { 
        characters(characters.toCharArray(), 0, characters.length());
    }


    /**
     * From XSLTC
     */
    public void addAttribute(String name, String value)
    {
        // do nothing, forget about the attribute
    }
    
    /**
     * Add a unique attribute
     */
    public void addUniqueAttribute(String qName, String value, int flags)
        throws SAXException
    {
        // do nothing, forget about the attribute 
    }

    public boolean startPrefixMapping(
        String prefix,
        String uri,
        boolean shouldFlush)
        throws SAXException
    {
        // no namespace support for HTML
        return false;
    }


    public void startPrefixMapping(String prefix, String uri)
        throws org.xml.sax.SAXException
    {
        // no namespace support for HTML
    }


    public void namespaceAfterStartElement(
        final String prefix,
        final String uri)
        throws SAXException
    {
        // no namespace support for HTML
    }    

    public void flushPending()
    {
        try
        {
            if (m_needToCallStartDocument)
            {
                startDocumentInternal();
                m_needToCallStartDocument = false;
            }
        }
        catch (SAXException e)
        { // can we do anything useful here,
            // or should this method throw a SAXException?
        }
    }
}
