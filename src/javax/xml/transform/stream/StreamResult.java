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
package javax.xml.transform.stream;

import javax.xml.transform.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Acts as an holder for a transformation result, 
 * which may be XML, plain Text, HTML, or some other form of markup.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class StreamResult implements Result
{

  /**
   * Zero-argument default constructor.
   */
  public StreamResult(){}

  /**
   * Construct a StreamResult from a byte stream.  Normally, 
   * a stream should be used rather than a reader, so that 
   * the transformer may use instructions contained in the 
   * transformation instructions to control the encoding.
   *
   * @param byteStream A valid OutputStream reference.
   */
  public StreamResult(OutputStream byteStream)
  {
    setByteStream(byteStream);
  }

  /**
   * Construct a StreamResult from a character stream.  Normally, 
   * a stream should be used rather than a reader, so that 
   * the transformer may use instructions contained in the 
   * transformation instructions to control the encoding.  However, 
   * there are times when it is useful to write to a character 
   * stream, such as when using a StringWriter.
   *
   * @param characterStream  A valid Writer reference.
   */
  public StreamResult(Writer characterStream)
  {
    setCharacterStream(characterStream);
  }

  /**
   * Set the byte stream that is to be written to.  Normally, 
   * a stream should be used rather than a reader, so that 
   * the transformer may use instructions contained in the 
   * transformation instructions to control the encoding.
   *
   * @param byteStream A valid OutputStream reference.
   */
  public void setByteStream(OutputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  /**
   * Get the byte stream that was set with setByteStream.
   *
   * @return The byte stream that was set with setByteStream, or null
   * if setByteStream or the byte stream constructor was not called.
   */
  public OutputStream getByteStream()
  {
    return byteStream;
  }

  /**
   * Set the character stream that is to be written to.  Normally, 
   * a stream should be used rather than a reader, so that 
   * the transformer may use instructions contained in the 
   * transformation instructions to control the encoding.  However, 
   * there are times when it is useful to write to a character 
   * stream, such as when using a StringWriter.
   *
   * @param characterStream  A valid Writer reference.
   */
  public void setCharacterStream(Writer characterStream)
  {
    this.characterStream = characterStream;
  }

  /**
   * Get the character stream that was set with setCharacterStream.
   *
   * @return The character stream that was set with setCharacterStream, or null
   * if setCharacterStream or the character stream constructor was not called.
   */
  public Writer getCharacterStream()
  {
    return characterStream;
  }

  /**
   * Method setSystemId Set the systemID that may be used in association
   * with the byte or character stream, or, if neither is set, use 
   * this value as a writeable URL (probably a file name).
   *
   * @param systemId The system identifier as a URL string.
   */
  public void setSystemId(String systemId)
  {
    this.systemId = systemId;
  }

  /**
   * Get the system identifier that was set with setSystemId.
   *
   * @return The system identifier that was set with setSystemId, or null
   * if setSystemId was not called.
   */
  public String getSystemId()
  {
    return systemId;
  }
  
  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  /**
   * The systemID that may be used in association
   * with the byte or character stream, or, if neither is set, use 
   * this value as a writeable URL (probably a file name).
   */
  private String systemId;

  /**
   * The byte stream that is to be written to.
   */
  private OutputStream byteStream;

  /**
   * The character stream that is to be written to.
   */
  private Writer characterStream;
}
