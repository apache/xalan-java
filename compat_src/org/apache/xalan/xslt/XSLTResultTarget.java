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
import org.xml.sax.DocumentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;


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
 * @deprecated This compatibility layer will be removed in later releases. 
 */
public class XSLTResultTarget //implements Result //extends StreamResult
{
  
  private StreamResult sr = null;
  private SAXResult saxResult = null;
  private DOMResult dr = null;
  
  
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
     sr = new StreamResult();
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
    sr = new StreamResult();
    sr.setSystemId(fileName);
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
    sr = new StreamResult();
    sr.setOutputStream(byteStream);
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
    sr = new StreamResult();
    sr.setWriter(characterStream);
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
    dr = new DOMResult();
    dr.setNode(n);
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
    saxResult = new SAXResult();    
    setDocumentHandler(handler);
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
    if (sr == null)
      sr = new StreamResult();
    sr.setSystemId(fileName);
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
    if (sr != null)
      return sr.getSystemId();
    else
      return null;
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
  
  private String encoding;


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
		if (handler instanceof XSLTEngineImpl)
			saxResult.setHandler(((XSLTEngineImpl)handler).getTransformer().getContentHandler());
    if (handler instanceof ParserAdapter)
    {
      if (saxResult == null)
        saxResult = new SAXResult();
      saxResult.setHandler(((ParserAdapter)handler).getContentHandler());
    }  
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
  
  private DocumentHandler formatterListener = null;
  
  /**
     * Set the node that will contain the result DOM tree.  In practice,
     * the node should be a {@link org.w3c.dom.Document} node,
     * a {@link org.w3c.dom.DocumentFragment} node, or a
     * {@link org.w3c.dom.Element} node.  In other words, a node
     * that accepts children.
     *
     * @param node The node to which the transformation
     * will be appended.
     */
    public void setNode(Node node) {
      if (dr == null)
        dr = new DOMResult();
      dr.setNode(node);
    }

    /**
     * Get the node that will contain the result DOM tree.
     * If no node was set via setNode, the node will be
     * set by the transformation, and may be obtained from
     * this method once the transformation is complete.
     *
     * @return The node to which the transformation
     * will be appended.
     */
    public Node getNode() {
      if (dr != null)
        return dr.getNode();
      else
        return null;
    }
    
 /**
   * Set the byte stream to contain the transformation result.
   *
   * @param byteStream A byte stream that will contain the transformation result.
   *
   * @see #XSLTResultTarget(OutputStream)
   * @see #setByteStream(OutputStream)
   */
    public void setByteStream(OutputStream byteStrm) {
        if (sr == null)
          sr = new StreamResult();
        sr.setOutputStream(byteStrm);
    }

 /**
   * Get the byte stream that contains or will contain the transformation result.
   *
   * @return The byte stream, or null if none was supplied.
   *
   * @see #XSLTResultTarget(OutputStream)
   * @see #setByteStream(OutputStream)
   */
    public OutputStream getByteStream() {
      if (sr != null)
        return sr.getOutputStream();
      else
        return null;
    }
    
    /**
     * Set the system identifier for this Result.
     *
     * <p>If the Result is not to be written to a file, the system identifier is optional.
     * The application may still want to provide one, however, for use in error messages
     * and warnings, or to resolve relative output identifiers.</p>
     *
     * @param systemId The system identifier as a URI string.
     */
    public void setSystemId(String systemID)
    {
      if (sr != null)
        sr.setSystemId(systemID);
      else if (dr != null)
        dr.setSystemId(systemID);
      else if (saxResult != null)
        saxResult.setSystemId(systemID);
    }

    /**
     * Get the system identifier that was set with setSystemId.
     *
     * @return The system identifier that was set with setSystemId,
     * or null if setSystemId was not called.
     */
    public String getSystemId()
    {
      if (sr != null)
        return sr.getSystemId();
      else if (dr != null)
        dr.getSystemId();
      else if (saxResult != null)
        saxResult.getSystemId();
      
      return null;
    }
    
     /**
     * Set the writer that is to receive the result.  Normally,
     * a stream should be used rather than a writer, so that
     * the transformer may use instructions contained in the
     * transformation instructions to control the encoding.  However,
     * there are times when it is useful to write to a writer,
     * such as when using a StringWriter.
     *
     * @param writer  A valid Writer reference.
     */
    public void setCharacterStream(Writer writer) {      
      if (sr == null)
        sr = new StreamResult();
      sr.setWriter(writer);
    }

    /**
     * Get the character stream that was set with setWriter.
     *
     * @return The character stream that was set with setWriter, or null
     * if setWriter or the Writer constructor was not called.
     */
    public Writer getCharacterStream() {
      if (sr != null)
        return sr.getWriter();
      else
        return null;
    }
  
  
    /**
     * Get the Result object associated with this XSLTResultTarget object .
     *
     * @return The Result object associated with this XSLTResultTarget object
     * 
     */
    public Result getResultObject() 
    {
      if (sr != null)        
        return sr;
      else if (dr != null)
        return dr;
      else 
        return saxResult;
    } 
    
}
