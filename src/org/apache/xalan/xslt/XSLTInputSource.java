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
import org.w3c.dom.Node;
import java.io.InputStream;
import java.io.Reader;

/**
 * <meta name="usage" content="general"/>
 * Represents an XML source document or XSL stylesheet.
 * Use XSLTInputSource objects to provide input to the XSLTProcessor process() method
 * for a transformation. This class extends the SAX input source to handle
 * DOM nodes as input as well as files, character streams, byte streams and SAX DocumentHandlers.
 *
 * @see XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)
 */
public class XSLTInputSource extends InputSource
{
  /**
    * Zero-argument default constructor -- Before you can use the new XSLTInputSource object in a transformation,
    * you must define the document source with setSystemId(), setNode(), setByteStream(), or setCharacterStream().
    *
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setByteStream
    * @see #setCharacterStream
    * @see #setEncoding(String)
    * @see #setPublicId(String)
    */
  public XSLTInputSource ()
  {
  }

  /**
    * Create a new input source with a system identifier (for a URL or file name) --
    * the equivalent of creating an input source with the zero-argument
    * constructor and setting the new object's SystemId property.

    * If the system identifier is a URL, it must be fully resolved.
    *
    * @param systemId The system identifier (URI).
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setByteStream
    * @see #setEncoding(String)
    * @see #setCharacterStream
    * @see #setPublicId(String)
    */
  public XSLTInputSource (String systemId)
  {
    setSystemId(systemId);
  }


  /**
    * Create a new input source with a byte stream -- the equivalent of creating an input source
    * with the zero-argument constructor and setting the new object's ByteStream property.
    *
    * @param byteStream The raw byte stream containing the document.
    * @see #setByteStream
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setEncoding(String)
    * @see #setCharacterStream
    * @see #setPublicId(String)
    */
  public XSLTInputSource (InputStream byteStream)
  {
    setByteStream(byteStream);
  }

  /**
    * Create a new input source with a character stream -- the equivalent of creating an input source
    * with the zero-argument constructor and setting the new object's CharacterStream property.
    * <p>The character stream shall not include a byte order mark.</p>
    *
    * @param characterStream The character stream containing the document.
    * @see #setCharacterStream
    * @see #setByteStream
    * @see #setSystemId(String)
    * @see #setNode(Node)
    * @see #setEncoding(String)
    * @see #setPublicId(String)
    */
  public XSLTInputSource (Reader characterStream)
  {
    setCharacterStream(characterStream);
  }

  /**
    * Create a new input source with a DOM Node -- the equivalent of creating an input source
    * with the zero-argument constructor and setting the new object's Node property.
    *
    * @param node The DOM Node containing the document.
    * @see #setNode(Node)
    * @see #setCharacterStream
    * @see #setByteStream
    * @see #setSystemId(String)
    * @see #setEncoding(String)
    * @see #setPublicId(String)
    */
  public XSLTInputSource (Node node)
  {
    setNode(node);
  }

  /**
    * Create a new XSLTInputSource source from a SAX input source.
    * This operation sets the ByteStream, CharacterStream, SystemId, PublicID, and Encoding properties.
    *
    * @param isource The SAX input source.
    * @see #setCharacterStream
    * @see #setByteStream
    * @see #setSystemId(String)
    * @see #setEncoding(String)
    * @see #setPublicId(String)
    * @see #setNode(Node)
    */
  public XSLTInputSource (InputSource isource)
  {
    this.setByteStream(isource.getByteStream());
    this.setCharacterStream(isource.getCharacterStream());
    this.setEncoding(isource.getEncoding());
    this.setPublicId(isource.getPublicId());
    this.setSystemId(isource.getSystemId());
  }

  /**
    * Set the DOM Node for this input source.
    *
    * @param characterStream The character stream containing the
    *        XML document or XSL stylesheet.
    * @see #XSLTInputSource(Node)
    * @see #getCharacterStream()
    * @see java.io.Reader
    */
  public void setNode (Node node)
  {
    this.node = node;
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
    return node;
  }

  private Node node = null;

}
