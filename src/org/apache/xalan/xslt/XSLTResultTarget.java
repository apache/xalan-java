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

import org.w3c.dom.Node;
import java.io.OutputStream;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xml.sax.DocumentHandler;
import org.apache.trax.Result;

/**
 * <meta name="usage" content="general"/>
 * Contains the result of a transformation that you perform with the
 * XSLTProcessor process() method or one of the StylesheetRoot process() methods.
 * Create an instance of this class to provide the process() method a container
 * for the transformation result tree.
 * You can use a file name or URL, character stream, byte stream, DOM Node, or SAX DocumentHandler
 * to instantiate an XSLTResultTarget object.
 *
 * @see XSLTProcessor#process(XSLTInputSource, XSLTInputSource, XSLTResultTarget)
 * @see StylesheetRoot
 */
public class XSLTResultTarget extends Result
{
  /**
    * Zero-argument default constructor  -- Before you can use the new XSLTResultTarget object in a transformation,
    * you must define the output container by setting its FileName, CharacterStrea, ByteStream, or Node property.
    *
    * @see #setFileName(String)
    * @see #setCharacterStream(Writer)
    * @see #setByteStream(OutputStream)
    * @see #setNode(Node)
    * @see #setDocumentHandler(DocumentHandler)
    * @see #setEncoding(String)
    */
  public XSLTResultTarget ()
  {
    super();
  }

  /**
   * Create a new output target with a file name -- the equivalent of creating an output target
   * with the zero-argument constructor and setting the new object's FileName property.
   *
   * @param fileName Identifies the file that will contain the transformation result (must be a valid system file name).
   *
   * @see #setFileName(String)
   * @see #setCharacterStream(Writer)
   * @see #setByteStream(OutputStream)
   * @see #setNode(Node)
   * @see #setDocumentHandler(DocumentHandler)
   * @see #setEncoding(String)
   */
  public XSLTResultTarget (String fileName) // File?
  {
    super();
    setFileName(fileName);    
  }


  /**
   * Create a new output target with a byte stream -- the equivalent of creating an output target
   * with the zero-argument constructor and setting the new object's ByteStream property.
   *
   * @param byteStream The raw byte stream that will contain the transformation result.
   *
   * @see #setByteStream(OutputStream)
   * @see #setFileName(String)
   * @see #setCharacterStream(Writer)
   * @see #setNode(Node)
   * @see #setDocumentHandler(DocumentHandler)
   * @see #setEncoding(String)
   */

  public XSLTResultTarget (OutputStream byteStream)
  {
    super(byteStream);
    //setByteStream(byteStream);    
  }


  /**
   * Create a new output target with a character stream -- the equivalent of creating an output target
   * with the zero-argument constructor and setting the new object's CharacterStream property.
   *
   * @param characterStream The character stream where the transformation result is written.
   *
   * @see #setCharacterStream(Writer)
   * @see #setByteStream(OutputStream)
   * @see #setFileName(String)
   * @see #setNode(Node)
   * @see #setDocumentHandler(DocumentHandler)
   * @see #setEncoding(String)
   */
  public XSLTResultTarget (Writer characterStream)
  {
    super(characterStream);
    //setCharacterStream(characterStream);
  }

  /**
   * Create a new output target with a DOM Node -- the equivalent of creating an output target
   * with the zero-argument constructor and setting the new object's Node property.
   *
   * @param node The DOM Node that will contain the transformation result.
   *
   * @see #setNode(Node)
   * @see #setCharacterStream(Writer)
   * @see #setByteStream(OutputStream)
   * @see #setFileName(String)
   * @see #setDocumentHandler(DocumentHandler)
   * @see #setEncoding(String)
   */
  public XSLTResultTarget (Node n)
  {
    super(n);    
  }

  /**
   * Create a new output target with a SAX Document handler, which
   * will handle result events -- the equivalent of creating an output target with the
   * zero-argument constructor and setting the new object's DocyumentHandler property.
   *
   * @param handler The SAX Document handler to which the result is written.
   *
   * @see #setDocumentHandler(DocumentHandler)
   * @see #setNode(Node)
   * @see #setCharacterStream(Writer)
   * @see #setByteStream(OutputStream)
   * @see #setFileName(String)
   * @see #setEncoding(String)
   */
  public XSLTResultTarget(DocumentHandler handler)
  {
    super();
    formatterListener = handler;
  }

  /**
   * Set the file name or URL where the transformation result will be written.
   *
   * @param fileName The system identifier as a string.
   *
   * @see #XSLTResultTarget(String)
   * @see #getFileName
   */
  public void setFileName (String fileName) // File?
  {
    this.fileName = fileName;
  }


  /**
   * Get the file name where the results are or will be written, or null if none was supplied.
   *
   * @return The file name or URL.
   *
   * @see #XSLTResultTarget(String)
   * @see #setFileName(String)
   */
  public String getFileName ()
  {
    return fileName;
  } 



  /**
   * Set the character encoding, if known.
   *
   * @param encoding The character encoding.
   */
  public void setEncoding (String encoding)
  {
    this.encoding = encoding;
  }


  /**
   * Get the character encoding that was used.
   *
   * @return The encoding, or null if none was supplied.
   */
  public String getEncoding ()
  {
    return encoding;
  }


  /**
   * Set a SAX DocumentHandler to process the result tree events.
   * You can process events as they occur
   * rather than waiting for the transformation to be completed.
   *
   * @param handler The SAX DocumentHandler to process result tree events.
   *
   * @see #XSLTResultTarget(DocumentHandler)
   * @see #getDocumentHandler()
   */
  public void setDocumentHandler (DocumentHandler handler)
  {
    this.formatterListener = handler;
  }

  /**
   * Get the SAX DocumentHandler that processes the result tree events.
   * You can use the DocumentHandler to process events as they occur
   * rather than waiting for the transformation to be completed.
   *
   * @return The SAX DocumentHandler that processes result tree events.
   *
   * @see #XSLTResultTarget(DocumentHandler)
   * @see #setDocumentHandler(DocumentHandler)
   */
  public DocumentHandler getDocumentHandler ()
  {
    return formatterListener;
  }

  public Writer getCharacterStream() {
    if( characterStream != null ) return characterStream;
    if( fileName != null ) {
      try {
	if( encoding==null ) {
	  return new FileWriter( fileName );
	} else {
	  return new OutputStreamWriter( new FileOutputStream( fileName ), encoding);
	}
      } catch( IOException ex ) {
	return null;
      }
    }
    return null;
  }
    
  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  private String fileName;
  private String encoding;
  private DocumentHandler formatterListener;
}
