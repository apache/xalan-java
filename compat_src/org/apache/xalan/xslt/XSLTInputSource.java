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
package org.apache.xalan.xslt;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

/**
 * <meta name="usage" content="general"/>
 * Represents an XML source document or XSL stylesheet.
 * Use XSLTInputSource objects to provide input to the XSLTProcessor process() method
 * for a transformation. This class extends the SAX input source to handle
 * DOM nodes as input as well as files, character streams, byte streams and SAX DocumentHandlers.
 *
 * @see XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class XSLTInputSource //extends SAXSource 
{
  
  private StreamSource streamSource = null;
  private SAXSource saxSource = null;
  private DOMSource domSource = null;
  
  /**
    * Zero-argument default constructor -- Before you can use the new XSLTInputSource object in a transformation,
    * you must define the document source with setSystemId(), setNode(), setInputStream(), or setCharacterStream().
    *
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setInputStream
    * @see #setCharacterStream
    * @see #setEncoding(String)
    */
  public XSLTInputSource ()
  {
    saxSource = new SAXSource();
  }

  /**
    * Create a new input source with a system identifier (for a URL or file name) --
    * the equivalent of creating an input source with the zero-argument
    * constructor and setting the new object's SystemId property.

    * If the system identifier is a URL, it must be fully resolved.
    *
    * If the system identifier is a URL, it must be fully resolved.
    *
    * @param systemId The system identifier (URI).
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setInputStream
    * @see #setEncoding(String)
    * @see #setCharacterStream
    */
  public XSLTInputSource (String systemId)
  {
    saxSource = new SAXSource();
    this.setSystemId(systemId);
  }


  /**
    * Create a new input source with a byte stream -- the equivalent of creating an input source
    * with the zero-argument constructor and setting the new object's ByteStream property.
    *
    * @param byteStream The raw byte stream containing the document.
    * @see #setInputStream
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setEncoding(String)
    * @see #setCharacterStream
    */
  public XSLTInputSource (InputStream byteStream)
  {
    streamSource = new StreamSource();
    streamSource.setInputStream(byteStream);
  }

  /**
    * Create a new input source with a character stream -- the equivalent of creating an input source
    * with the zero-argument constructor and setting the new object's CharacterStream property.
    * <p>The character stream shall not include a byte order mark.</p>
    *
    * @param characterStream The character stream containing the document.
    * @see #setCharacterStream
    * @see #setInputStream
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setEncoding(String)
    */
  public XSLTInputSource (Reader characterStream)
  {
    streamSource = new StreamSource();
    streamSource.setReader(characterStream);
  }

  /**
    * Create a new input source with a DOM Node -- the equivalent of creating an input source
    * with the zero-argument constructor and setting the new object's Node property.
    *
    * @param node The DOM Node containing the document.
    * @see #setNode(Node)
    * @see #setCharacterStream
    * @see #setInputStream
    * @see #setSystemId(String)
    * @see #setEncoding(String)
    */
  public XSLTInputSource (Node node)
  {
    domSource = new DOMSource();
    domSource.setNode(node);
  }

  /**
    * Create a new XSLTInputSource source from a SAX input source.
    * This operation sets the ByteStream, CharacterStream, SystemId, PublicID, and Encoding properties.
    *
   * @param isource The SAX input source.
    * @see #setCharacterStream
    * @see #setInputStream
    * @see #setSystemId(String)
    * @see #setEncoding(String)
    * @see #setNode(Node)
    */
  public XSLTInputSource (InputSource isource)
  {
    saxSource = new SAXSource();
    saxSource.setInputSource(isource);
    //saxSource.setXMLReader(isource.getCharacterStream());
    this.setEncoding(isource.getEncoding());
    this.setSystemId(isource.getSystemId());
  }

  /**
    * Set the DOM Node for this input source.
    *
    * @param node The DOM node containing the
    *        XML document or XSL stylesheet.
    * @see #XSLTInputSource(Node)
    * @see #getCharacterStream()    * 
    * @see java.io.Reader
    */
  public void setNode (Node node)
  {
    if (domSource == null)
      domSource = new DOMSource();
    domSource.setNode(node);
  }

  /**
    * Get the DOM Node for this input source.
    *
    * @return The DOM node containing the document, or null if none was supplied.
    * @see #XSLTInputSource(Node)
    * @see #setNode(Node)
    */
  public Node getNode ()
  {
    if (domSource != null)
      return domSource.getNode();
    else
      return null;
  }

  
  
   /** 
     * Set the character encoding, if known.
     *
     * <p>The encoding must be a string acceptable for an
     * XML encoding declaration (see section 4.3.3 of the XML 1.0
     * recommendation).</p>
     *
     * <p>This method has no effect when the application provides a
     * character stream.</p>
     *
     * @param encoding A string describing the character encoding.
     * @see #setSystemId
     * @see #getEncoding
     */
    public void setEncoding (String encoding)
    {
	this.encoding = encoding;
    }
    
    
    /**
     * Get the character encoding for a byte stream or URI.
     *
     * @return The encoding, or null if none was supplied.
     * @see #getSystemId
     */
    public String getEncoding ()
    {
	return encoding;
    }
    
    private String encoding = null;
    
    /**
     * Set the character stream for this input source.
     *
     * <p>If there is a character stream specified, the SAX parser
     * will ignore any byte stream and will not attempt to open
     * a URI connection to the system identifier.</p>
     *
     * @param characterStream The character stream containing the
     *        XML document or other entity.
     * @see java.io.Reader
     */
    public void setCharacterStream (Reader characterStream)
    {
      if (streamSource == null)
	      streamSource = new StreamSource();
      streamSource.setReader(characterStream);
    }
    
    
    /**
     * Get the character stream for this input source.
     *
     * @return The character stream, or null if none was supplied.
     */
    public Reader getCharacterStream ()
    {
      if (streamSource != null)
        return streamSource.getReader();
      else
        return null;
    }
    
    
    
    /**
     * Set the base ID (URL or system ID) from where URLs
     * will be resolved.
     *
     * @param baseID Base URL for this.
     */
    public void setSystemId(String baseID) 
    {
      this.baseID = baseID;
      
      if (saxSource != null)
        saxSource.setSystemId(baseID);
      else if (domSource != null)
        domSource.setSystemId(baseID);
      else if (streamSource != null)
        streamSource.setSystemId(baseID);      
    }

    /**
     * Get the base ID (URL or system ID) from where URLs
     * will be resolved.
     *
     * @return Base URL for this.
     */
    public String getSystemId() {
        return this.baseID;
    }
    
    private String baseID = null;

    
    /**
     * Set the byte stream to be used as input.  Normally,
     * a stream should be used rather than a reader, so that
     * the XML parser can resolve character encoding specified
     * by the XML declaration.
     *
     * <p>If this Source object is used to process a stylesheet, normally
     * setSystemId should also be called, so that relative URL references
     * can be resolved.</p>
     *
     * @param inputStream A valid InputStream reference to an XML stream.
     */
    public void setInputStream(InputStream inputStream) 
    {
      if (streamSource == null)
        streamSource = new StreamSource();
      streamSource.setInputStream(inputStream);
    }

    /**
     * Get the byte stream that was set with setInputStream.
     *
     * @return The byte stream that was set with setInputStream, or null
     * if setByteStream or the ByteStream constructor was not called.
     */
    public InputStream getInputStream() {
      if (streamSource != null)
        return streamSource.getInputStream();
      else
        return null;
    }
    
    
     /**
     * Get the Result object associated with this XSLTResultTarget object .
     *
     * @return The Result object associated with this XSLTResultTarget object
     * 
     */
    public Source getSourceObject() 
    {
      if (streamSource != null)        
        return streamSource;
      else if (domSource != null)
        return domSource;
      else 
        return saxSource;
    } 
}
