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
import java.util.Properties;
import java.util.Enumeration;

import org.w3c.dom.Node;

import org.xml.sax.ContentHandler;

import org.apache.xml.utils.DOMBuilder;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xml.utils.BoolStack;
import org.apache.xpath.XPathContext;
import org.apache.xpath.SourceTreeManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.WhiteSpaceInfo;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import org.xml.sax.Attributes;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.Locator;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.DTDHandler;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.Result;
import javax.xml.transform.ErrorListener;


/**
 * This class handles SAX2 parse events to create a source
 * tree for transformation.
 */
public class SourceTreeHandler extends org.xml.sax.helpers.DefaultHandler implements TransformerHandler, DeclHandler, DTDHandler
{
//  static int m_idCount = 0;
//  int m_id;
  
  /**
   * Create a SourceTreeHandler that will start a transformation as
   * soon as a startDocument occurs.
   *
   * @param transformer The transformer this will use to transform a
   * source tree into a result tree.
   */
  public SourceTreeHandler(TransformerImpl transformer)
  {
    this(transformer, false);
  }

  /**
   * Create a SourceTreeHandler that will start a transformation as
   * soon as a startDocument occurs.
   *
   * @param transformer The transformer this will use to transform a
   * source tree into a result tree.
   */
  public SourceTreeHandler(TransformerImpl transformer, boolean doFragment)
  {
//    m_id = m_idCount++;
    m_transformer = transformer;

    XPathContext xctxt = ((TransformerImpl) transformer).getXPathContext();

    xctxt.setDOMHelper(new StreeDOMHelper());
    
    if(doFragment)
    {
      m_root = new DocumentFragmentImpl(1024);
      m_docFrag = (DocumentFragmentImpl)m_root;
    }
    else
    {
      m_root = new DocumentImpl(this);
    }

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

  /** 
   * The transformer this will use to transform a
   * source tree into a result tree.          
   */
  TransformerImpl m_transformer;

  /** DOMBuilder object this will use          */
  private DOMBuilder m_sourceTreeHandler;

  /** The root of the source document          */
  private DocImpl m_root;

  /** If this is non-null, the fragment where the nodes will be added. */
  private DocumentFragment m_docFrag;

  /** No longer used??          */
  private boolean m_initedRoot;

  /** Flag indicating whether we should check whitespaces          */
  boolean m_shouldCheckWhitespace = false;
  
  boolean m_shouldTransformAtEnd = true;
  
  public void setShouldTransformAtEnd(boolean b)
  {
    m_shouldTransformAtEnd = b;
  }

  /**
   * Get the root document of tree that is being or will be created.
   *
   * @return Root document
   */
  public Node getRoot()
  {
    return m_root;
  }

  /**
   * Set the root document of tree will be created.
   *
   * @param root root document of tree that will be created
   */
  public void setRoot(DocImpl root)
  {
    m_root = root;
  }

  /**
   * If an exception was thrown, keep track of it
   *
   *
   * @param e Exception that was thrown
   */
  public void setExceptionThrown(Exception e)
  {
    m_root.m_exceptionThrown = e;
  }

  /** Source Document          */
  private Source m_inputSource;

  /**
   * Set the Source document 
   *
   *
   * @param source source document
   */
  public void setInputSource(Source source)
  {
    m_inputSource = source;
  }

  /**
   * Get Source Document.
   *
   *
   * @return source document
   */
  public Source getInputSource()
  {
    return m_inputSource;
  }

  /**
   * Implement the setDocumentLocator event.
   *
   * @param locator Document locator
   */
  public void setDocumentLocator(Locator locator){}

  /** 
   * Flag to indicate whether to use multiple threads for the transformation
   * and the parse.          
   */
  private boolean m_useMultiThreading = false;

  /**
   * Set whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * @param b Flag to indicate whether to use multiple threads
   */
  public void setUseMultiThreading(boolean b)
  {
    m_useMultiThreading = b;
  }

  /**
   * Tell whether or not the tree being built should handle
   * transformation while the parse is still going on.
   *
   * @return Flag to indicate whether to use multiple threads
   */
  public boolean getUseMultiThreading()
  {
    return m_useMultiThreading;
  }
  
  /**
   * Simple count incremented in startDocument and decremented in 
   * endDocument, to make sure this contentHandler isn't being double 
   * entered.
   */
  private int m_entryCount = 0;
  
  /** Indicate whether running in Debug mode        */
  private static final boolean DEBUG = false;

  /** Flag indicating whether indexed lookup is being used to search the source tree          */
  private boolean indexedLookup = false;  // for now 

  /** 
   * Field to hold the number of tasks on the transform thread 
   * so far waiting for a notify() from the parse thread.          
   */
  private int m_eventsCount = 0;

  /** 
   * Minimum number of waiting tasks before the transform thread
   * gets a notify() event.            
   */
  private int m_maxEventsToNotify = 18;

  /**
   * Notify all waiting threads that some events have occured.
   * Note that we only notify when the predefined number of  
   * have been hit.
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
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException
  {
    // System.out.println("startDocument: "+m_id);
    
    if(m_entryCount != 0)
      throw new org.xml.sax.SAXException(
    "startDocument can not be called while within startDocument/endDocument! "+
    "Threading problem?");
    
    m_entryCount++; // decremented at the end of endDocument
    
    synchronized (m_root)
    {
      m_inDTD = false;
      m_root.setSourceTreeHandler(this);
      m_root.setUid(1);
      m_root.setLevel(new Integer(1).shortValue());
      m_root.setUseMultiThreading(getUseMultiThreading());

      if(null != m_docFrag)
      {
        m_sourceTreeHandler =
                new StreeDOMBuilder(m_root, m_docFrag);
      }
      else if (m_root.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
        m_sourceTreeHandler =
                new StreeDOMBuilder(m_root.getOwnerDocument(), (DocumentFragment) m_root);
      else
        m_sourceTreeHandler = new StreeDOMBuilder((Document) m_root);

      pushShouldStripWhitespace(false);
      m_sourceTreeHandler.startDocument();
    }

    // Do the transformation in parallel with source reading
    if (m_useMultiThreading && (null != m_transformer))
    {
      if (m_transformer.isParserEventsOnMain())
      {
        // We may need to pass our output properties to the next 
        // transformer.  There is a question as to whether or not this should 
        // be done.
        ContentHandler resultContentHandler = m_transformer.getContentHandler();
        if(null != resultContentHandler)
        {
          if(resultContentHandler instanceof SourceTreeHandler)
          {
            // myProps is a clone of the transformer's output properties.
            Properties myProps = m_transformer.getOutputProperties();
            
            // Now copy the result content handler keys on top of our keys.
            SourceTreeHandler resultHandler = (SourceTreeHandler) resultContentHandler;
            Transformer resultTransformer = resultHandler.getTransformer();
            Properties resultProps = resultTransformer.getOutputProperties();
            Enumeration myKeys = myProps.keys();
            while(myKeys.hasMoreElements())
            {
              Object key = myKeys.nextElement();
                            
              // Only add it if it has not been explicitly set.
              if(null == resultProps.get(key))
              {
                // System.out.println("key: "+key+", value: "+myProps.get(key));
                // System.out.println("resultProps.get(key): "+resultProps.get(key));
                resultProps.put(key, myProps.get(key));
              }
            }
            
            resultTransformer.setOutputProperties(resultProps);
            
          }
        }
        
        if(null != m_docFrag)
          m_transformer.setSourceTreeDocForThread(m_docFrag);
        else
          m_transformer.setSourceTreeDocForThread(m_root);

	int cpriority = Thread.currentThread().getPriority();
	    
	// runTransformThread is equivalent with the 2.0.1 code,
	// except that the Thread may come from a pool.
	m_transformer.runTransformThread( cpriority );
	
      }
    }

    notifyWaiters();
  }
  
 
  /**
   * Implement the endDocument event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {
    m_eventsCount = m_maxEventsToNotify;

    // notifyWaiters();

    Object synchObj = m_root;

    synchronized (synchObj)
    {
      m_sourceTreeHandler.endDocument();
      
      // System.out.println("endDocument: "+m_id);
      m_root.setComplete(true);

      popShouldStripWhitespace();

      if (!m_useMultiThreading && (null != m_transformer) && m_shouldTransformAtEnd)
      {
        try
        {
          m_transformer.transformNode(m_root);
        }
        catch(TransformerException te)
        {
          // te.printStackTrace();
          throw new org.xml.sax.SAXException(te);
        }
      }
    }

    m_eventsCount = m_maxEventsToNotify;

    notifyWaiters();

    // printTree(m_root);
    if (m_useMultiThreading && (null != m_transformer))
    {
      // may throw SAXException ( if error reading transform )
      m_transformer.waitTransformThread();
    }
    m_entryCount--; // incremented at the start of startDocument
  }

  /**
   * Print the tree starting at the specified node. 
   *
   *
   * @param n A node in a Document.
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
   * @param ns Namespace of the element
   * @param localName Local part of the qualified name of the element
   * @param name Name of the element
   * @param atts List of attributes associated with the element
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String ns, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {
    if(DEBUG)
    {
      System.out.println("SourceTreeHandler - startElement: "+ns+", "+localName+", "+m_root);
      int n = atts.getLength();
      for (int i = 0; i < n; i++) 
      {
        System.out.println("atts["+i+"]: "+atts.getQName(i)+" = "+atts.getValue(i));
      }
      if(null == ns)
      {
        (new RuntimeException(localName+" has a null namespace!")).printStackTrace();
      }
    }
    
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
   * @param ns Namespace of the element
   * @param localName Local part of the qualified name of the element
   * @param name Name of the element
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String ns, String localName, String name)
          throws org.xml.sax.SAXException
  {
    if(DEBUG)
    {
      System.out.println("SourceTreeHandler - endElement: "+ns+", "+localName);
    }

    synchronized (m_root)
    {
      Parent myElement=(Parent)(m_sourceTreeHandler.getCurrentNode());
      
      m_sourceTreeHandler.endElement(ns, localName, name);     

      // Mark as complete only after endElement has had a chance to flush
      // any pending work (Text nodes in particular)
      myElement.setComplete(true);

      m_shouldStripWS = m_shouldStripWhitespaceStack.popAndTop();
      
    }

    notifyWaiters();
  }

  /** Flag indicating whether we got a CDATA event          */
  private boolean m_isCData = false;

  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event.</p>
   *
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA() throws org.xml.sax.SAXException
  {
    m_isCData = true;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA() throws org.xml.sax.SAXException
  {
    m_isCData = false;
  }

  /**
   * Implement the characters event.
   *
   * @param ch Character array from the characters event
   * @param start Start index of characters to process in the array
   * @param length Number of characters to process in the array 
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char ch[], int start, int length) throws org.xml.sax.SAXException
  {
    if(m_inDTD)
      return;
      
    if(DEBUG)
    {
      System.out.print("SourceTreeHandler#characters: ");
      int n = start+length;
      for (int i = start; i < n; i++) 
      {
        if(Character.isWhitespace(ch[i]))
          System.out.print("\\"+((int)ch[i]));
        else
          System.out.print(ch[i]);
      }    
      System.out.println("");  
    }

    synchronized (m_root)
    {
//      if (m_isCData)
//        m_sourceTreeHandler.cdata(ch, start, length);
//      else
      m_sourceTreeHandler.characters(ch, start, length);
    }

    notifyWaiters();
  }

  /**
   * Implement the characters event.
   *
   * @param ch Character array from the characters event
   * @param start Start index of characters to process in the array
   * @param length Number of characters to process in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
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
   * @param ch Character array from the characters event
   * @param start Start index of characters to process in the array
   * @param length Number of characters to process in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {
    if(m_inDTD)
      return;

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
   * @param target Target of PI node
   * @param data Content of PI node
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
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
   * @throws org.xml.sax.SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length) throws org.xml.sax.SAXException
  {
    if(m_inDTD)
      return;

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
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity(String name) throws org.xml.sax.SAXException
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
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity(String name) throws org.xml.sax.SAXException
  {

    synchronized (m_root)
    {
      m_sourceTreeHandler.endEntity(name);
    }

    notifyWaiters();
  }
  
  private boolean m_inDTD = false;

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
   * @throws org.xml.sax.SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD(String name, String publicId, String systemId)
          throws org.xml.sax.SAXException
  {
    m_inDTD = true; 
    if (m_root instanceof DocumentImpl)
    {
      DocumentImpl doc = ((DocumentImpl)m_root);
      DocumentTypeImpl dtd = new DocumentTypeImpl(doc, name, publicId, systemId);
      ((DocumentImpl)m_root).setDoctype(dtd);
    }
  }

  /**
   * Report the end of DTD declarations.
   *
   * @throws org.xml.sax.SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD() throws org.xml.sax.SAXException
  {
    m_inDTD = false; 
  }

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
   * @see #endPrefixMapping
   * @see #startElement
   *
   * @throws org.xml.sax.SAXException
   */
  public void startPrefixMapping(String prefix, String uri)
          throws org.xml.sax.SAXException
  {
    if(DEBUG)
      System.out.println("SourceTreeHandler - startPrefixMapping("+prefix+", "+uri+");");

    synchronized (m_root)
    {
      m_sourceTreeHandler.startPrefixMapping(prefix, uri);
    }
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
   * @see #startPrefixMapping
   * @see #endElement
   *
   * @throws org.xml.sax.SAXException
   */
  public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException
  {
    if(DEBUG)
      System.out.println("SourceTreeHandler - endPrefixMapping("+prefix+");");

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
   *
   * @throws org.xml.sax.SAXException
   */
  public void skippedEntity(String name) throws org.xml.sax.SAXException
  {
  }

  /** Flag indicating whether to strip whitespace nodes          */
  private boolean m_shouldStripWS = false;

  /** Stack of flags indicating whether to strip whitespace nodes          */
  private BoolStack m_shouldStripWhitespaceStack = new BoolStack();

  /**
   * Find out whether or not to strip whispace nodes.  
   *
   *
   * @return whether or not to strip whispace nodes.
   */
  boolean getShouldStripWhitespace()
  {
    return m_shouldStripWS;
  }

  /**
   * Set whether to strip whitespaces and push in current value of   
   * m_shouldStripWS in m_shouldStripWhitespaceStack.
   *
   * @param shouldStrip Flag indicating whether to strip whitespace nodes
   */
  void pushShouldStripWhitespace(boolean shouldStrip)
  {

    m_shouldStripWS = shouldStrip;

    m_shouldStripWhitespaceStack.push(shouldStrip);
  }

  /**
   * Set whether to strip whitespaces at this point by popping out  
   * m_shouldStripWhitespaceStack. 
   *
   */
  void popShouldStripWhitespace()
  {
    m_shouldStripWS = m_shouldStripWhitespaceStack.popAndTop();
  }

  /**
   * Set whether to strip whitespaces and set the top of the stack to 
   * the current value of m_shouldStripWS.  
   *
   *
   * @param shouldStrip Flag indicating whether to strip whitespace nodes
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
   * @throws IllegalArgumentException if result is invalid for some reason.
   */
  public void setResult(Result result)
    throws IllegalArgumentException
  {
    if(null == result)
      throw new IllegalArgumentException("result should not be null");
    try
    {
      ContentHandler handler = m_transformer.createResultContentHandler(result);
      m_transformer.setContentHandler(handler); 
    }
    catch(TransformerException te)
    {
      throw new IllegalArgumentException("result could not be set");
    }
  }
  
  /**
   * Set the base ID (URL or system ID) from where relative 
   * URLs will be resolved.
   * @param baseID Base URL for the source tree.
   */
  public void setSystemId(String baseID)
  {
    m_transformer.setBaseURLOfSource(baseID);
    
    XPathContext xctxt = m_transformer.getXPathContext();
    SourceTreeManager stm = xctxt.getSourceTreeManager();
    
    m_inputSource = new StreamSource(baseID);
    
    stm.putDocumentInCache(m_root, m_inputSource);
  }
  
  /**
   * Get the base ID (URI or system ID) from where relative 
   * URLs will be resolved.
   * @return The systemID that was set with {@link #setSystemId}.
   */
  public String getSystemId()
  {
    return m_transformer.getBaseURLOfSource();
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
  
  /**
   * Report an element type declaration.
   *
   * <p>The content model will consist of the string "EMPTY", the
   * string "ANY", or a parenthesised group, optionally followed
   * by an occurrence indicator.  The model will be normalized so
   * that all whitespace is removed,and will include the enclosing
   * parentheses.</p>
   *
   * @param name The element type name.
   * @param model The content model as a normalized string.
   * @throws SAXException The application may raise an exception.
   */
  public void elementDecl (String name, String model)
    throws org.xml.sax.SAXException
  {
  }


  /**
   * Report an attribute type declaration.
   *
   * <p>Only the effective (first) declaration for an attribute will
   * be reported.  The type will be one of the strings "CDATA",
   * "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY",
   * "ENTITIES", or "NOTATION", or a parenthesized token group with 
   * the separator "|" and all whitespace removed.</p>
   *
   * @param eName The name of the associated element.
   * @param aName The name of the attribute.
   * @param type A string representing the attribute type.
   * @param valueDefault A string representing the attribute default
   *        ("#IMPLIED", "#REQUIRED", or "#FIXED") or null if
   *        none of these applies.
   * @param value A string representing the attribute's default value,
   *        or null if there is none.
   * @throws SAXException The application may raise an exception.
   */
  public void attributeDecl (String eName,
                             String aName,
                             String type,
                             String valueDefault,
                             String value)
    throws org.xml.sax.SAXException
  {
  }


  /**
   * Report an internal entity declaration.
   *
   * <p>Only the effective (first) declaration for each entity
   * will be reported.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @param value The replacement text of the entity.
   * @throws SAXException The application may raise an exception.
   * @see #externalEntityDecl
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void internalEntityDecl (String name, String value)
    throws org.xml.sax.SAXException
  {
  }


  /**
   * Report a parsed external entity declaration.
   *
   * <p>Only the effective (first) declaration for each entity
   * will be reported.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @param publicId The declared public identifier of the entity, or
   *        null if none was declared.
   * @param systemId The declared system identifier of the entity.
   * @throws SAXException The application may raise an exception.
   * @see #internalEntityDecl
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void externalEntityDecl (String name, String publicId,
                                  String systemId)
    throws org.xml.sax.SAXException
  {
  }

  /**
   * Receive notification of a notation declaration event.
   *
   * <p>It is up to the application to record the notation for later
   * reference, if necessary.</p>
   *
   * <p>At least one of publicId and systemId must be non-null.
   * If a system identifier is present, and it is a URL, the SAX
   * parser must resolve it fully before passing it to the
   * application through this event.</p>
   *
   * <p>There is no guarantee that the notation declaration will be
   * reported before any unparsed entities that use it.</p>
   *
   * @param name The notation name.
   * @param publicId The notation's public identifier, or null if
   *        none was given.
   * @param systemId The notation's system identifier, or null if
   *        none was given.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #unparsedEntityDecl
   * @see org.xml.sax.AttributeList
   */
  public void notationDecl (String name,
                            String publicId,
                            String systemId)
    throws org.xml.sax.SAXException
  {
  }
  
  
  /**
   * Receive notification of an unparsed entity declaration event.
   *
   * <p>Note that the notation name corresponds to a notation
   * reported by the {@link #notationDecl notationDecl} event.  
   * It is up to the application to record the entity for later 
   * reference, if necessary.</p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before passing it to the application.</p>
   *
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @param name The unparsed entity's name.
   * @param publicId The entity's public identifier, or null if none
   *        was given.
   * @param systemId The entity's system identifier.
   * @param notation name The name of the associated notation.
   * @see #notationDecl
   * @see org.xml.sax.AttributeList
   */
  public void unparsedEntityDecl (String name,
                                  String publicId,
                                  String systemId,
                                  String notationName)
    throws org.xml.sax.SAXException
  {
    try
    {
      if(null != m_inputSource)
        systemId = org.apache.xml.utils.SystemIDResolver.getAbsoluteURI(systemId, m_inputSource.getSystemId());
    }
    catch(Exception e)
    {
      throw new org.xml.sax.SAXException(e);
    }
    EntityImpl entity = new EntityImpl(name, notationName, publicId, systemId);
    m_root.getDoctype().getEntities().setNamedItem(entity);
  }

}
