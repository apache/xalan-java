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
package javax.xml.transform.sax;

import javax.xml.transform.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * Acts as an holder for a transformation result tree.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class SAXResult implements Result
{

  /**
   * Zero-argument default constructor.
   */
  public SAXResult(){}

  /**
   * Create a new output target with a DOM node.
   *
   *
   * @param handler
   */
  public SAXResult(ContentHandler handler)
  {
    setHandler(handler);
  }

  /**
   * Set the node that will contain the result DOM tree.
   *
   * @param handler
   */
  public void setHandler(ContentHandler handler)
  {
    this.handler = handler;
  }

  /**
   * Get the node that will contain the result tree.
   *
   * @return
   */
  public ContentHandler getHandler()
  {
    return handler;
  }

  /**
   * Set the SAX2 DeclHandler for the output.
   *
   * @param handler
   */
  void setDeclHandler(DeclHandler handler)
  {
    this.declhandler = declhandler;
  }

  /**
   * Get the SAX2 DeclHandler for the output.
   * @return A DeclHandler, or null.
   */
  DeclHandler getDeclHandler()
  {
    return declhandler;
  }

  /**
   * Set the SAX2 LexicalHandler for the output.
   *
   * @param handler
   */
  void setLexicalHandler(LexicalHandler handler)
  {
    this.lexhandler = lexhandler;
  }

  /**
   * Get a SAX2 LexicalHandler for the output.
   * @return A LexicalHandler, or null.
   */
  LexicalHandler getLexicalHandler()
  {
    return lexhandler;
  }

  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  /**
   * Field handler
   */
  private ContentHandler handler;

  /**
   * Field declhandler
   */
  private DeclHandler declhandler;

  /**
   * Field lexhandler
   */
  private LexicalHandler lexhandler;
}
