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
package org.apache.xalan.transformer;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.GenerateEvent;

/**
 * Acts as a base class for queued SAX events.
 */
public abstract class QueuedSAXEvent
{

  /**
   * Constructor QueuedSAXEvent
   *
   *
   * NEEDSDOC @param type
   */
  public QueuedSAXEvent(int type)
  {
    m_type = type;
  }

  /** NEEDSDOC Field DOC          */
  static final int DOC = 1;

  /** NEEDSDOC Field ELEM          */
  static final int ELEM = 2;

  /** NEEDSDOC Field m_traceManager          */
  protected TraceManager m_traceManager;

  /** NEEDSDOC Field m_transformer          */
  protected TransformerImpl m_transformer;

  /** NEEDSDOC Field m_contentHandler          */
  protected ContentHandler m_contentHandler;

  /** NEEDSDOC Field isPending          */
  public boolean isPending = false;

  /** NEEDSDOC Field isEnded          */
  public boolean isEnded = false;

  /** NEEDSDOC Field m_type          */
  private int m_type;

  /**
   * NEEDSDOC Method getType 
   *
   *
   * NEEDSDOC (getType) @return
   */
  int getType()
  {
    return m_type;
  }

  /**
   * NEEDSDOC Method setTraceManager 
   *
   *
   * NEEDSDOC @param traceManager
   */
  void setTraceManager(TraceManager traceManager)
  {
    m_traceManager = traceManager;
  }

  /**
   * NEEDSDOC Method setTransformer 
   *
   *
   * NEEDSDOC @param transformer
   */
  void setTransformer(TransformerImpl transformer)
  {
    m_transformer = transformer;
  }

  /**
   * NEEDSDOC Method fireGenerateEvent 
   *
   *
   * NEEDSDOC @param type
   * NEEDSDOC @param name
   * NEEDSDOC @param attrs
   */
  protected void fireGenerateEvent(int type, String name, Attributes attrs)
  {

    GenerateEvent ge = new GenerateEvent(m_transformer, type, name, attrs);

    if (null != m_traceManager)
      m_traceManager.fireGenerateEvent(ge);
  }

  /**
   * NEEDSDOC Method getContentHandler 
   *
   *
   * NEEDSDOC (getContentHandler) @return
   */
  ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }

  /**
   * NEEDSDOC Method setContentHandler 
   *
   *
   * NEEDSDOC @param ch
   */
  void setContentHandler(ContentHandler ch)
  {
    m_contentHandler = ch;
  }

  /**
   * Clear the pending event.
   */
  void clearPending()
  {
    isPending = false;
  }

  /**
   * NEEDSDOC Method setPending 
   *
   *
   * NEEDSDOC @param b
   */
  void setPending(boolean b)
  {
    isPending = b;
    this.isEnded = !isPending;
  }

  /**
   * Flush the event.
   *
   * @throws SAXException
   */
  void flush() throws SAXException
  {
    clearPending();
  }

  /**
   * NEEDSDOC Method reset 
   *
   */
  void reset()
  {
    isPending = false;
  }
}
