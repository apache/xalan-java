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

import java.util.Stack;

import org.apache.xml.utils.ObjectPool;

import org.xml.sax.Attributes;

/**
 * This class acts as a base for ResultTreeHandler, and keeps
 * queud stack events.  In truth, we don't need a stack,
 * so I may change this down the line a bit.
 */
abstract class QueuedEvents
{

  /** The number of events queued          */
  int m_eventCount = 0;

  /** Queued start document          */
  QueuedStartDocument m_startDoc = new QueuedStartDocument();

  /** Queued start element          */
  QueuedStartElement m_startElement = new QueuedStartElement();

  /**
   * Get the queued document event.
   *
   * @return the queued document event or null if none.
   */
  QueuedStartDocument getQueuedDoc()
  {
    return (m_eventCount == 1) ? m_startDoc : null;
  }

  /**
   * Get the queued document event.
   *
   * @return the queued document event.
   */
  QueuedStartDocument getQueuedDocAtBottom()
  {
    return m_startDoc;
  }

  /**
   * Get the queued element.
   *
   * @return the queued element.
   */
  QueuedStartElement getQueuedElem()
  {
    return (m_eventCount > 1) ? m_startElement : null;
  }

//  /**
//   * This is for the derived class to init new events.
//   *
//   * @param qse Queud Sax event
//   */
//  protected abstract void initQSE(QueuedSAXEvent qse);

  /**
   * To re-initialize the document and element events 
   *
   */
  protected void reInitEvents()
  {
    // initQSE(m_startDoc);
    // initQSE(m_startElement);
  }

  /**
   * Push document event and re-initialize events  
   *
   */
  public void reset()
  {
    pushDocumentEvent();
    reInitEvents();
  }

  /**
   * Push the document event.  This never gets popped.
   */
  void pushDocumentEvent()
  {

    m_startDoc.setPending(true);
    // initQSE(m_startDoc);

    m_eventCount++;
  }

  /**
   * Push the element event
   *
   *
   * @param ns Namespace URI of the element
   * @param localName Local part of the qualified name of the element
   * @param name Name of the element
   * @param atts Attribute children of the element
   */
  void pushElementEvent(String ns, String localName, String name,
                        Attributes atts)
  {

    m_startElement.setPending(ns, localName, name, atts);
    // initQSE(m_startElement);

    m_eventCount++;
  }

  /**
   * Pop element event 
   *
   */
  void popEvent()
  {

    m_startElement.reset();

    m_eventCount--;
  }

  /** Instance of a serializer          */
  private org.apache.xalan.serialize.Serializer m_serializer;

  /**
   * This is only for use of object pooling, so that
   * it can be reset.
   *
   * @param s non-null instance of a serializer 
   */
  void setSerializer(org.apache.xalan.serialize.Serializer s)
  {
    m_serializer = s;
  }

  /**
   * This is only for use of object pooling, so the that
   * it can be reset.
   *
   * @return The serializer
   */
  org.apache.xalan.serialize.Serializer getSerializer()
  {
    return m_serializer;
  }
}
