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
import org.xml.sax.Attributes;

import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.GenerateEvent;

import javax.xml.transform.TransformerException;

/**
 * Acts as a base class for queued SAX events.
 */
public abstract class QueuedSAXEvent
{

  /**
   * Constructor QueuedSAXEvent
   *
   *
   * @param type Type of SAX event
   */
  public QueuedSAXEvent(int type)
  {
    m_type = type;
  }

  /** Document SAX event          */
  static final int DOC = 1;

  /** Element SAX event          */
  static final int ELEM = 2;

  /** Instance of TraceManager           */
  protected TraceManager m_traceManager;

  /** Instance of Transformer           */
  protected TransformerImpl m_transformer;

  /** Instance of ContentHandler          */
  protected ContentHandler m_contentHandler;

  /** Flag indicating that the ContentHandler is a TransformerClient, 
   *  so we need to save the transform state of the queue. */
  protected boolean m_isTransformClient = false;
  
  /** Flag indicating that an event is pending          */
  public boolean isPending = false;

  /** Flag indicating that an event is ended          */
  public boolean isEnded = false;

  /** Type of SAX event          */
  private int m_type;
  
  /**
   * Get the type of this SAX event 
   *
   *
   * @return The type of this SAX event
   */
  int getType()
  {
    return m_type;
  }

  /**
   * Set the Trace Manager 
   *
   *
   * @param traceManager Trace Manager to set
   */
  void setTraceManager(TraceManager traceManager)
  {
    m_traceManager = traceManager;
  }

  /**
   * Set the Transformer 
   *
   *
   * @param transformer Transformer to set
   */
  void setTransformer(TransformerImpl transformer)
  {
    m_transformer = transformer;
  }

  /**
   * Fire a Generate Event 
   *
   *
   * @param type Event type
   * @param name Element name
   * @param attrs Attributes for the element
   */
  protected void fireGenerateEvent(int type, String name, Attributes attrs)
  {
    if (null != m_traceManager)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer, type, name, attrs);
      m_traceManager.fireGenerateEvent(ge);
    }
  }

  /**
   * Get the ContentHandler used by this event
   *
   *
   * @return The content Handler 
   */
  ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }

  /**
   * Set the ContentHandler this event will use
   *
   *
   * @param ch Content Handler to set
   */
  void setContentHandler(ContentHandler ch)
  {
    m_contentHandler = ch;
  }
  
  /**
   * Tell this queued element if the content handler is a TransformerClient.
   */
  void setIsTransformClient(boolean b)
  {
    m_isTransformClient = b;
  }

  /**
   * Clear the pending event.
   */
  void clearPending()
  {
    isPending = false;
  }

  /**
   * Set whether this event is pending
   *
   *
   * @param b Flag indicating whether this event is pending 
   */
  void setPending(boolean b)
  {
    isPending = b;
    this.isEnded = !isPending;
        
  }

  /**
   * Flush the event.
   *
   * @throws TransformerException
   */
  void flush() throws org.xml.sax.SAXException
  {
    clearPending();
  }

  /**
   * Reset Pending flag to indicate that this event is not pending 
   *
   */
  void reset()
  {
    isPending = false;
  }
  
}
