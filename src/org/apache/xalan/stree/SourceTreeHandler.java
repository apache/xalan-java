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
package org.apache.xalan.stree;

import java.util.Stack;

import org.w3c.dom.Node;

import org.xml.sax.ContentHandler;

import org.apache.xalan.utils.DOMBuilder;
import org.apache.xalan.utils.XMLCharacterRecognizer;
import org.apache.xalan.utils.BoolStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.SourceTreeManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.WhiteSpaceInfo;

import org.w3c.dom.Document;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.Result;


/**
 * This class handles SAX2 parse events to create a source
 * tree for transformation.
 */
public class SourceTreeHandler implements TransformerHandler
{
  static int m_idCount = 0;
  int m_id;

  /**
   * Create a SourceTreeHandler that will start a transformation as
   * soon as a startDocument occurs.
   *
   * NEEDSDOC @param transformer
   */
  public SourceTreeHandler(TransformerImpl transformer)
  {
    m_id = m_idCount++;
    m_transformer = transformer;

    XPathContext xctxt = ((TransformerImpl) transformer).getXPathContext();

    xctxt.setDOMHelper(new StreeDOMHelper());

    // if (indexedLookup)
    //  m_root = new IndexedDocImpl();
    // else
    m_root = new DocumentImpl(this);

    m_initedRoot = false;
    m_shouldCheckWhitespace =
      transformer.getStylesheet().shouldCheckWhitespace();
  }

  /**
   * Create a SourceTreeHandler.
   */
  public SourceTreeHandler()
  {

    // if (indexedLookup)
    //  m_root = new IndexedDocImpl();
    // else
    m_root = new DocumentImpl(this);
    m_initedRoot = false;
  }

  /** NEEDSDOC Field m_transformer          */
  TransformerImpl m_transformer;

  /** NEEDSDOC Field m_sourceTreeHandler          */
  private DOMBuilder m_sourceTreeHandler;

  /** NEEDSDOC Field m_root          */
  private Document m_root;  // Normally a Document

  /** NEEDSDOC Field m_initedRoot          */
  private boolean m_initedRoot;

  /** NEEDSDOC Field m_shouldCheckWhitespace          */
  boolean m_shouldCheckWhitespace = false;

  /**
   * Get the root document of tree that is being or will be created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Node getRoot()
  {
    return m_root;
  }

  /**
   * Set the root document of tree will be created.
   *
   * NEEDSDOC @param root
   */
  public void setRoot(Document root)
  {
    m_root = root;
  }

  /**
   * NEEDSDOC Method setExceptionThrown 
   *
   *
   * NEEDSDOC @param e
   */
  public void setExceptionThrown(Exception e)
  {
    ((DocumentImpl) m_root).m_exceptionThrown = e;
  }

  /** NEEDSDOC Field m_inputSource          */
  Source m_inputSource;

  /**
   * NEEDSDOC Method setInputSource 
   *
   *
   * NEEDSDOC @param source
   */
  public void setInputSource(Source source)
  {
    m_inputSource = source;
  }

  /**
   * NEEDSDOC Method getInputSource 
   *
   *
   * NEEDSDOC (getInputSource) @return
   */
  public Source getInputSource()
  {
    return m_inputSource;
  }

  /**
   * Implement the setDocumentLocator event.
   *
   * NEEDSDOC @param locator
   */
  public void setDocumentLocator(Locator locator){}

  /** NEEDSDOC Field m_useMultiThreading          */
  private boolean m_useMultiThreading = false;

  /**
   * Set whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * NEEDSDOC @param b
   */
  public void setUseMultiThreading(boolean b)
  {
    m_useMultiThreading = b;
  }

  /**
   * Tell whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getUseMultiThreading()
  {
    return m_useMultiThreading;
  }

  /** NEEDSDOC Field indexedLookup          */
  private boolean indexedLookup = false;  // for now 

  /** NEEDSDOC Field m_eventsCount          */
  private int m_eventsCount = 0;

  /** NEEDSDOC Field m_maxEventsToNotify          */
  private int m_maxEventsToNotify = 18;

  /**
   * NEEDSDOC Method notifyWaiters 
   *
   */
  private void notifyWaiters()
  {

    if (m_useMultiThreading && (m_eventsCount >= m_maxEventsToNotify))
    {
      Object synchObj = m_root;

      synchronized (synchObj)
      {
        synchObj.notify();
      }

      m_eventsCount = 0;
    }
    else
      m_eventsCount++;
  }

  /**
   * Implement the startDocument event.
   *
   * @throws SAXException
   */
  public void startDocument() throws SAXException
  {
    // System.out.println("startDocument: "+m_id);
    synchronized (m_root)
    {

      /*
      if(false == m_initedRoot)
      {
        if(null != m_transformer)
        {
          String urlOfSource = m_transformer.getBaseURLOfSource();
          if(null == m_inputSource)
          {
            m_inputSource = new InputSource(urlOfSource);
          }
          m_transformer.getXPathContext().getSourceTreeManager().putDocumentInCache(m_root, m_inputSource);
        }
      }
      */
      ((DocumentImpl) m_root).setSourceTreeHandler(this);
      ((DocumentImpl) m_root).setUid(1);
      ((DocumentImpl) m_root).setLevel(new Integer(1).shortValue());
      ((DocumentImpl) m_root).setUseMultiThreading(getUseMultiThreading());

      m_sourceTreeHandler = new StreeDOMBuilder(m_root);

      pushShouldStripWhitespace(false);
      m_sourceTreeHandler.startDocument();
    }

    if (m_useMultiThreading && (null != m_transformer))
    {
      if (m_transformer.isParserEventsOnMain())
      {
        m_transformer.setSourceTreeDocForThread(m_root);

        Thread t = new Thread(m_transformer);

        m_transformer.setTransformThread(t);

        int cpriority = Thread.currentThread().getPriority();

        // t.setPriority(cpriority-1);
        t.setPriority(cpriority);
        t.start();
      }
    }

    notifyWaiters();
  }
  

  /**
   * Implement the endDocument event.
   *
   * @throws SAXException
   */
  public void endDocument() throws SAXException
  {
    // System.out.println("endDocument: "+m_id);
    ((Parent) m_root).setComplete(true);

    m_eventsCount = m_maxEventsToNotify;

    notifyWaiters();

    Object synchObj = m_root;

    synchronized (synchObj)
    {
      m_sourceTreeHandler.endDocument();
      popShouldStripWhitespace();

      if (!m_useMultiThreading && (null != m_transformer))
      {
        try
        {
          m_transformer.transformNode(m_root);
        }
        catch(TransformerException te)
        {
          throw new SAXException(te);
        }
      }
    }

    m_eventsCount = m_maxEventsToNotify;

    notifyWaiters();

    // printTree(m_root);
    if (m_useMultiThreading && (null != m_transformer))
    {
      Thread transformThread = m_transformer.getTransformThread();

      if (null != transformThread)
      {
        try
        {

          // This should wait until the transformThread is considered not alive.
          transformThread.join();
          m_transformer.setTransformThread(null);
        }
        catch (InterruptedException ie){}
      }
    }
  }

  /**
   * NEEDSDOC Method printTree 
   *
   *
   * NEEDSDOC @param n
   */
  private void printTree(Node n)
  {

    System.out.println("node: " + n.getNodeName());

    Node child;

    for (child = n.getFirstChild(); child != null;
            child = child.getNextSibling())
    {
      printTree(child);
    }
  }

  /**
   * Implement the startElement event.
   *
   * NEEDSDOC @param ns
   * NEEDSDOC @param localName
   * NEEDSDOC @param name
   * NEEDSDOC @param atts
   *
   * @throws SAXException
   */
  public void startElement(
          String ns, String localName, String name, Attributes atts)
            throws SAXException
  {

    synchronized (m_root)
    {
      m_shouldStripWhitespaceStack.push(m_shouldStripWS);
      m_sourceTreeHandler.startElement(ns, localName, name, atts);
    }

    notifyWaiters();
  }

  /**
   * Implement the endElement event.
   *
   * NEEDSDOC @param ns
   * NEEDSDOC @param localName
   * NEEDSDOC @param name
   *
   * @throws SAXException
   */
  public void endElement(String ns, String localName, String name)
          throws SAXException
  {

    synchronized (m_root)
    {
      ((Parent) m_sourceTreeHandler.getCurrentNode()).setComplete(true);
      m_sourceTreeHandler.endElement(ns, localName, name);

      m_shouldStripWS = m_shouldStripWhitespaceStack.popAndTop();
    }

    notifyWaiters();
  }

  /** NEEDSDOC Field m_isCData          */
  private boolean m_isCData = false;

  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA() throws SAXException
  {
    m_isCData = true;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA() throws SAXException
  {
    m_isCData = false;
  }

  /**
   * Implement the characters event.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param start
   * NEEDSDOC @param length
   *
   * @throws SAXException
   */
  public void characters(char ch[], int start, int length) throws SAXException
  {

    synchronized (m_root)
    {
      if (m_shouldStripWS
              && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
        return;

      if (m_isCData)
        m_sourceTreeHandler.cdata(ch, start, length);
      else
        m_sourceTreeHandler.characters(ch, start, length);
    }

    notifyWaiters();
  }

  /**
   * Implement the characters event.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param start
   * NEEDSDOC @param length
   *
   * @throws SAXException
   */
  public void charactersRaw(char ch[], int start, int length)
          throws SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.charactersRaw(ch, start, length);
    }

    notifyWaiters();
  }

  /**
   * Implement the ignorableWhitespace event.
   *
   * NEEDSDOC @param ch
   * NEEDSDOC @param start
   * NEEDSDOC @param length
   *
   * @throws SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws SAXException
  {

    synchronized (m_root)
    {
      if (m_shouldStripWS)
        return;

      m_sourceTreeHandler.characters(ch, start, length);
    }

    notifyWaiters();
  }

  /**
   * Implement the processingInstruction event.
   *
   * NEEDSDOC @param target
   * NEEDSDOC @param data
   *
   * @throws SAXException
   */
  public void processingInstruction(String target, String data)
          throws SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.processingInstruction(target, data);
    }

    notifyWaiters();
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * <p>This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).</p>
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length) throws SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.comment(ch, start, length);
    }

    notifyWaiters();
  }

  /**
   * Report the beginning of an entity.
   *
   * <p>The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.</p>
   *
   * <p>Note that skipped entities will be reported through the
   * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
   * event, which is part of the ContentHandler interface.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity(String name) throws SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.startEntity(name);
    }

    notifyWaiters();
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity(String name) throws SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.endEntity(name);
    }

    notifyWaiters();
  }

  /**
   * Report the start of DTD declarations, if any.
   *
   * <p>Any declarations are assumed to be in the internal subset
   * unless otherwise indicated by a {@link #startEntity startEntity}
   * event.</p>
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD(String name, String publicId, String systemId)
          throws SAXException{}

  /**
   * Report the end of DTD declarations.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD() throws SAXException{}

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   * <p>The information from this event is not necessary for
   * normal Namespace processing: the SAX XML reader will
   * automatically replace prefixes for element and attribute
   * names when the http://xml.org/sax/features/namespaces
   * feature is true (the default).</p>
   *
   * <p>There are cases, however, when applications need to
   * use prefixes in character data or in attribute values,
   * where they cannot safely be expanded automatically; the
   * start/endPrefixMapping event supplies the information
   * to the application to expand prefixes in those contexts
   * itself, if necessary.</p>
   *
   * <p>Note that start/endPrefixMapping events are not
   * guaranteed to be properly nested relative to each-other:
   * all startPrefixMapping events will occur before the
   * corresponding startElement event, and all endPrefixMapping
   * events will occur after the corresponding endElement event,
   * but their order is not guaranteed.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #endPrefixMapping
   * @see #startElement
   *
   * @throws SAXException
   */
  public void startPrefixMapping(String prefix, String uri)
          throws SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.startPrefixMapping(prefix, uri);
    }

    // System.out.println("DOMBuilder.startPrefixMapping("+prefix+", "+uri+");");
  }

  /**
   * End the scope of a prefix-URI mapping.
   *
   * <p>See startPrefixMapping for details.  This event will
   * always occur after the corresponding endElement event,
   * but the order of endPrefixMapping events is not otherwise
   * guaranteed.</p>
   *
   * @param prefix The prefix that was being mapping.
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #startPrefixMapping
   * @see #endElement
   *
   * @throws SAXException
   */
  public void endPrefixMapping(String prefix) throws SAXException
  {
    m_sourceTreeHandler.endPrefixMapping(prefix);
  }

  /**
   * Receive notification of a skipped entity.
   *
   * <p>The Parser will invoke this method once for each entity
   * skipped.  Non-validating processors may skip entities if they
   * have not seen the declarations (because, for example, the
   * entity was declared in an external DTD subset).  All processors
   * may skip external entities, depending on the values of the
   * http://xml.org/sax/features/external-general-entities and the
   * http://xml.org/sax/features/external-parameter-entities
   * properties.</p>
   *
   * @param name The name of the skipped entity.  If it is a
   *        parameter entity, the name will begin with '%'.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws SAXException
   */
  public void skippedEntity(String name) throws SAXException{}

  /** NEEDSDOC Field m_shouldStripWS          */
  private boolean m_shouldStripWS = false;

  /** NEEDSDOC Field m_shouldStripWhitespaceStack          */
  private BoolStack m_shouldStripWhitespaceStack = new BoolStack();

  /**
   * NEEDSDOC Method getShouldStripWhitespace 
   *
   *
   * NEEDSDOC (getShouldStripWhitespace) @return
   */
  boolean getShouldStripWhitespace()
  {
    return m_shouldStripWS;
  }

  /**
   * NEEDSDOC Method pushShouldStripWhitespace 
   *
   *
   * NEEDSDOC @param shouldStrip
   */
  void pushShouldStripWhitespace(boolean shouldStrip)
  {

    m_shouldStripWS = shouldStrip;

    m_shouldStripWhitespaceStack.push(shouldStrip);
  }

  /**
   * NEEDSDOC Method popShouldStripWhitespace 
   *
   */
  void popShouldStripWhitespace()
  {
    m_shouldStripWS = m_shouldStripWhitespaceStack.popAndTop();
  }

  /**
   * NEEDSDOC Method setShouldStripWhitespace 
   *
   *
   * NEEDSDOC @param shouldStrip
   */
  void setShouldStripWhitespace(boolean shouldStrip)
  {

    m_shouldStripWS = shouldStrip;

    m_shouldStripWhitespaceStack.setTop(shouldStrip);
  }
  
  /**
   * Method setResult allows the user of the TransformerHandler
   * to set the result of the transform.
   *
   * @param result A Result instance, should not be null.
   * 
   * @throws TransformerException if result is invalid for some reason.
   */
  public void setResult(Result result)
    throws TransformerException
  {
    ContentHandler handler = m_transformer.createResultContentHandler(result);
    m_transformer.setContentHandler(handler);    
  }
  
  /**
   * Set the base ID (URL or system ID) from where relative 
   * URLs will be resolved.
   * @param baseID Base URL for the source tree.
   */
  public void setBaseID(String baseID)
  {
    m_transformer.setBaseURLOfSource(baseID);
    
    XPathContext xctxt = m_transformer.getXPathContext();
    SourceTreeManager stm = xctxt.getSourceTreeManager();
    
    stm.putDocumentInCache(m_root, new StreamSource(baseID));
  }
  
  /**
   * Get the Transformer associated with this handler, which 
   * is needed in order to set parameters and output properties.
   */
  public Transformer getTransformer()
  {
    return m_transformer;
  }
  
  /**
   * Get the Transformer associated with this handler, which 
   * is needed in order to set parameters and output properties.
   */
  TransformerImpl getTransformerImpl()
  {
    return m_transformer;
  }


}
