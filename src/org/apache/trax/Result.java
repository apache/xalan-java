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

// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package org.apache.trax;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.w3c.dom.Node;

/**
 * Acts as an holder for a transformation result tree.
 * <p>This class is modeled after the SAX InputSource class, except that it
 * is for the Result target, and in addition to streams, and writers,
 * it also can specify a DOM node to which nodes will be appended.</p>
 *
 * <h3>Open issues:</h3>
 * <dl> *    <dt><h4>Should this be an interface?</h4></dt>
 *    <dd>Should this be an interface instead of a concrete class?  The justification
 *        for it being a class is that it is just a bag of data, and contains no
 *        behavior of its own.</dd>
 * </dl> 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class Result
{

  /**
   * Zero-argument default constructor.
   */
  public Result(){}

  /**
   * Create a new output target with a byte stream.
   *
   * @param byteStream The raw byte stream that will contain the document.
   */
  public Result(OutputStream byteStream)
  {
    setByteStream(byteStream);
  }

  /**
   * Create a new output target with a character stream.
   *
   * @param characterStream The character stream where the result will be written.
   */
  public Result(Writer characterStream)
  {
    setCharacterStream(characterStream);
  }

  /**
   * Create a new output target with a DOM node.
   *
   * @param n The DOM node that will contain the result tree.
   */
  public Result(Node n)
  {
    setNode(n);
  }

  /**
   * Set the byte stream for this output target.
   *
   * @param byteStream A byte stream that will contain the result document.
   */
  public void setByteStream(OutputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  /**
   * Get the byte stream for this output target.
   *
   * @return The byte stream, or null if none was supplied.
   */
  public OutputStream getByteStream()
  {
    return byteStream;
  }

  /**
   * Set the character stream for this output target.
   *
   * @param characterStream The character stream that will contain
   *                     the result document.
   */
  public void setCharacterStream(Writer characterStream)
  {
    this.characterStream = characterStream;
  }

  /**
   * Get the character stream for this output target.
   *
   * @return The character stream, or null if none was supplied.
   */
  public Writer getCharacterStream()
  {
    return characterStream;
  }

  /**
   * Set the node that will contain the result DOM tree.
   *
   * NEEDSDOC @param node
   */
  public void setNode(Node node)
  {
    this.node = node;
  }

  /**
   * Get the node that will contain the result tree.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getNode()
  {
    return node;
  }

  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  /** NEEDSDOC Field byteStream          */
  protected OutputStream byteStream;

  /** NEEDSDOC Field characterStream          */
  protected Writer characterStream;

  /** NEEDSDOC Field node          */
  protected Node node;
}
