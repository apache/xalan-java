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
 *     the documentation and/or other materials provided with the
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
 
import java.util.Enumeration;

import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;

import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.GenerateEvent;

import org.apache.xalan.utils.MutableAttrListImpl;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.TreeWalker;
import org.apache.xalan.utils.ObjectPool;
import org.apache.xalan.utils.XMLCharacterRecognizer;

import org.apache.xpath.DOMHelper;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import org.apache.serialize.SerializerHandler;

/**
 * This class is a layer between the direct calls to the result 
 * tree content handler, and the transformer.  For one thing, 
 * we have to delay the call to
 * getContentHandler().startElement(name, atts) because of the
 * xsl:attribute and xsl:copy calls.  In other words,
 * the attributes have to be fully collected before you
 * can call startElement.
 */
public class ResultTreeHandler extends QueuedEvents
  implements ContentHandler, SerializerHandler, LexicalHandler
{
  /**
   * Create a new result tree handler.  The real content 
   * handler will be the ContentHandler passed as an argument.
   */
  public ResultTreeHandler(TransformerImpl transformer,
                           ContentHandler realHandler)
  {
    m_transformer = transformer;
    m_tracer = transformer.getTraceManager();
    m_contentHandler = realHandler;
    m_cloner = new ClonerToResultTree(transformer, this);
    
    // The stylesheet is set at a rather late stage, so I do 
    // this here, though it would probably be better done elsewhere.
    if(null != m_transformer)
      m_stylesheetRoot = m_transformer.getStylesheet();

    pushDocumentEvent(); // not pending yet.
  }
  
  /**
   * Bottleneck the startDocument event.
   */
  public void startDocument ()
    throws SAXException
  {
  }

  /**
   * Bottleneck the endDocument event.  This may be called 
   * more than once in order to make sure the pending start 
   * document is called.
   */
  public void endDocument ()
    throws SAXException
  {
    flushPending(EVT_ENDDOCUMENT);
    getQueuedDocAtBottom().flushEnd();
  }

  /**
   * Bottleneck the startElement event.  This is used to "pend" an
   * element, so that attributes can still be added to it before 
   * the real "startElement" is called on the result tree listener.
   */
  public void startElement (String ns, String localName, String name)
    throws SAXException
  {
    startElement (ns, localName, name, null);
  }
    
  /**
   * Bottleneck the startElement event.  This is used to "pend" an
   * element, so that attributes can still be added to it before 
   * the real "startElement" is called on the result tree listener.
   */
  public void startElement (String ns, String localName, String name, 
                            Attributes atts)
    throws SAXException
  {
    checkForSerializerSwitch(ns, localName);
    flushPending(EVT_STARTELEMENT);
    if(!m_nsContextPushed)
      m_nsSupport.pushContext();

    ensurePrefixIsDeclared(ns, localName);
        
    // getQueuedElem().setPending(ns, localName, name, atts);
    this.pushElementEvent(ns, localName, name, atts);
  }

  /**
   * Bottleneck the endElement event.
   */
  public void endElement (String ns, String localName, String name)
    throws SAXException
  {
    flushPending(EVT_ENDELEMENT);
    QueuedStartElement qse = getQueuedElem();
    qse.flushEnd();
    sendEndPrefixMappings();
    popEvent();
    
    m_nsSupport.popContext();
  }
  
  boolean m_nsContextPushed = false;
  
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
   */
  public void startPrefixMapping (String prefix, String uri)
    throws SAXException
  {    
    startPrefixMapping (prefix, uri, true);
  }
  
  public void startPrefixMapping (String prefix, String uri, boolean shouldFlush)
    throws SAXException
  {    
    if(shouldFlush)
      flushPending(EVT_STARTPREFIXMAPPING);
    if(!m_nsContextPushed)
    {
      m_nsSupport.pushContext();
      m_nsContextPushed = true;
    }
    
    String existingURI = m_nsSupport.getURI(prefix);
    if((null == existingURI) || !existingURI.equals(uri))
    {
      m_nsSupport.declarePrefix(prefix, uri);
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
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #startPrefixMapping
   * @see #endElement
   */
  public void endPrefixMapping (String prefix)
    throws SAXException
  {
  }
  
  /**
   * Bottleneck the characters event.
   */
  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    QueuedStartDocument qsd = getQueuedDoc();
    if((null != qsd) && qsd.isPending() 
       && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
      return;
    
    flushPending(EVT_CHARACTERS);    
    getContentHandler().characters(ch, start, length);
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_CHARACTERS,
                                                 ch, start, length));
  }

  /**
   * Bottleneck the ignorableWhitespace event.
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    QueuedStartDocument qsd = getQueuedDoc();
    if((null != qsd) && qsd.isPending() 
       && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
      return;

    flushPending(EVT_IGNORABLEWHITESPACE);
    getContentHandler().ignorableWhitespace(ch, start, length);
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE,
                                                 ch, start, length));
  }

  /**
   * Bottleneck the processingInstruction event.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    flushPending(EVT_PROCESSINGINSTRUCTION);
    getContentHandler().processingInstruction(target, data);
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_PI,
                                                 target, data));
  }

  /**
   * Bottleneck the comment event.
   */
  public void comment(String data) throws SAXException
  {
    flushPending(EVT_COMMENT);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).comment(data.toCharArray(), 0, data.length());
    }
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_COMMENT,
                                                 data));
  }

  /**
   * Bottleneck the comment event.
   */
  public void comment(char ch[], int start, int length) throws SAXException
  {
    flushPending(EVT_COMMENT);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).comment(ch, start, length);
    }
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_COMMENT,
                                                 new String(ch, start, length)));
  }


  /**
   * Bottleneck the comment event.
   */
  public void entityReference(String name) throws SAXException
  {
    flushPending(EVT_ENTITYREF);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).startEntity(name);
      ((LexicalHandler)getContentHandler()).endEntity(name);
    }
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_ENTITYREF,
                                                 name));
  }

  /**
   * Start an entity.
   */
  public void startEntity(String name) throws SAXException
  {
    flushPending(EVT_STARTENTITY);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).startEntity(name);
    }
  }

  /**
   * End an entity.
   */
  public void endEntity(String name) throws SAXException
  {
    flushPending(EVT_ENDENTITY);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).endEntity(name);
    }
    m_tracer.fireGenerateEvent(new GenerateEvent(m_transformer,
                                                 GenerateEvent.EVENTTYPE_ENTITYREF,
                                                 name));
  }

  /**
   * Start the DTD.
   */
  public void startDTD(String s1, String s2, String s3) throws SAXException
  {
    flushPending(EVT_STARTDTD);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).startDTD(s1, s2, s3);
    }
  }

  /**
   * End the DTD.
   */
  public void endDTD() throws SAXException
  {
    flushPending(EVT_ENDDTD);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).endDTD();
    }
  }
  
  /**
   * Starts an un-escaping section. All characters printed within an
   * un-escaping section are printed as is, without escaping special
   * characters into entity references. Only XML and HTML serializers
   * need to support this method.
   * <p>
   * The contents of the un-escaping section will be delivered through
   * the regular <tt>characters</tt> event.
   */
  public void startNonEscaping()
    throws SAXException
  {
    flushPending(EVT_STARTNONESCAPING);
    if(getContentHandler() instanceof SerializerHandler)
    {
      ((SerializerHandler)getContentHandler()).startNonEscaping();
    }
  }

  /**
   * Ends an un-escaping section.
   *
   * @see #startNonEscaping
   */
  public void endNonEscaping()
    throws SAXException
  {
    flushPending(EVT_ENDNONESCAPING);
    if(getContentHandler() instanceof SerializerHandler)
    {
      ((SerializerHandler)getContentHandler()).endNonEscaping();
    }
  }

  /**
   * Starts a whitespace preserving section. All characters printed
   * within a preserving section are printed without indentation and
   * without consolidating multiple spaces. This is equivalent to
   * the <tt>xml:space=&quot;preserve&quot;</tt> attribute. Only XML
   * and HTML serializers need to support this method.
   * <p>
   * The contents of the whitespace preserving section will be delivered
   * through the regular <tt>characters</tt> event.
   */
  public void startPreserving()
    throws SAXException
  {
    flushPending(EVT_STARTPRESERVING);
    if(getContentHandler() instanceof SerializerHandler)
    {
      ((SerializerHandler)getContentHandler()).startPreserving();
    }
  }


  /**
   * Ends a whitespace preserving section.
   *
   * @see #startPreserving
   */
  public void endPreserving()
    throws SAXException
  {
    flushPending(EVT_ENDENDPRESERVING);
    if(getContentHandler() instanceof SerializerHandler)
    {
      ((SerializerHandler)getContentHandler()).endPreserving();
    }
  }

  /**
   * Start the CDATACharacters.
   */
  public void startCDATA() throws SAXException
  {
    flushPending(EVT_STARTCDATA);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).startCDATA();
    }
  }

  /**
   * End the CDATA characters.
   */
  public void endCDATA() throws SAXException
  {
    flushPending(EVT_ENDCDATA);
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).endCDATA();
    }
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
   */
  public void skippedEntity (String name)
    throws SAXException
  {
  }
  
  /**
   * Flush the pending element.
   */
  public void flushPending()
    throws SAXException
  {    
    flushPending(EVT_NODE);
  }
      
  /**
   * Flush the pending element.
   */
  public void flushPending(int type)
    throws SAXException
  {    
    QueuedStartElement qe = getQueuedElem();
    QueuedStartDocument qdab = getQueuedDocAtBottom();
    
    if ((type != EVT_STARTPREFIXMAPPING) && qdab.isPending())
    {
      qdab.flush();
    }

    if ((null != qe) && qe.isPending())
    {
      if(!qe.nsDeclsHaveBeenAdded())
        addNSDeclsToAttrs();

      sendStartPrefixMappings();
      qe.flush();
      m_nsContextPushed = false;
    }
  }
  
  /**
   * Given a result tree fragment, walk the tree and
   * output it to the result stream.
   */
  public void outputResultTreeFragment(XObject obj, 
                                       XPathContext support)
    throws SAXException
  {
    DocumentFragment docFrag = obj.rtree(support);
    NodeList nl = docFrag.getChildNodes();
    int nChildren = nl.getLength();
    TreeWalker tw = new TreeWalker(this);
    for(int i = 0; i < nChildren; i++)
    {
      flushPending(EVT_NODE); // I think.
      tw.traverse(nl.item(i));
    }
  }
  
  /**
   * Clone an element with or without children.
   */
  public void cloneToResultTree(Stylesheet stylesheetTree, Node node,
                                boolean shouldCloneWithChildren,
                                boolean overrideStrip,
                                boolean shouldCloneAttributes)
    throws SAXException
  {
    m_cloner.cloneToResultTree(stylesheetTree, node,
                               shouldCloneWithChildren,
                               overrideStrip,
                               shouldCloneAttributes);
  } 
  
  /**
   * To fullfill the FormatterListener interface... no action
   * for the moment.
   */
  public void setDocumentLocator (Locator locator)
  {
  }
  
  /**
   * This function checks to make sure a given prefix is really 
   * declared.  It might not be, because it may be an excluded prefix.
   * If it's not, it still needs to be declared at this point.
   * TODO: This needs to be done at an earlier stage in the game... -sb
   */
  void ensurePrefixIsDeclared(String ns, String localName)
    throws SAXException
  {
    if (ns != null && ns.length() > 0)
    { 
      int index;
      String prefix = (index = localName.indexOf(":"))< 0 ? null : localName.substring(0, index);
      if(null != prefix)
      {
        String foundURI = m_nsSupport.getURI(prefix);
        if((null == foundURI) || !foundURI.equals(ns))
          startPrefixMapping ( prefix, ns, false);
      }
    }
  }
  
  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   */
  protected void sendStartPrefixMappings()
    throws SAXException
  {
    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = getContentHandler();
    while (prefixes.hasMoreElements())
    {
      String prefix = (String)prefixes.nextElement();      
      handler.startPrefixMapping(prefix, m_nsSupport.getURI(prefix));
    }
  }
  
  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   */
  protected void sendEndPrefixMappings()
    throws SAXException
  {
    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = getContentHandler();
    
    while (prefixes.hasMoreElements()) 
    {
      String prefix = (String)prefixes.nextElement();
      handler.endPrefixMapping(prefix);
    }
  }

  /**
   * Check to see if we should switch serializers based on the 
   * first output element being an HTML element.
   */
  private void checkForSerializerSwitch(String ns, String localName)
    throws SAXException
  {
    QueuedStartDocument qdab = getQueuedDocAtBottom();
    if(qdab.isPending())
    {
      SerializerSwitcher.switchSerializerIfHTML(m_transformer, ns, localName);
    }
  }
  
  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   */
  protected void addNSDeclsToAttrs()
  {
    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    QueuedStartElement qe = getQueuedElem();
    while (prefixes.hasMoreElements()) 
    {
      String prefix = (String)prefixes.nextElement();
      boolean isDefault = (prefix.length() == 0);
      String name;
      if(isDefault)
      {
        //prefix = "xml";
        name = "xmlns";
      }
      else
        name="xmlns:"+prefix;
      
      qe.addAttribute("http://www.w3.org/2000/xmlns/", 
                      prefix, 
                      name, "CDATA", m_nsSupport.getURI(prefix));
    }
    qe.setNSDeclsHaveBeenAdded(true);
  }

  /**
   * Copy <KBD>xmlns:</KBD> attributes in if not already in scope.
   */
  public void processNSDecls(Node src)
    throws SAXException
  {
    int type;
    // Vector nameValues = null;
    // Vector alreadyProcessedPrefixes = null;
    Node parent;
    if(((type = src.getNodeType()) == Node.ELEMENT_NODE
        || (type == Node.ENTITY_REFERENCE_NODE))
       && (parent = src.getParentNode()) != null)
    {
      processNSDecls(parent);
    }  
    if (type == Node.ELEMENT_NODE)
    {
      NamedNodeMap nnm = src.getAttributes();
      int nAttrs = nnm.getLength();
      for (int i = 0;  i < nAttrs;  i++)
      {
        Node attr = nnm.item(i);
        String aname = attr.getNodeName();
        if (QName.isXMLNSDecl(aname))
        {
          String prefix = QName.getPrefixFromXMLNSDecl(aname);
          String desturi = getURI(prefix);
          String srcURI = attr.getNodeValue();
          if(!srcURI.equalsIgnoreCase(desturi))
          {
            this.startPrefixMapping(prefix, srcURI);
          }
        }
      }
    }      
  }
  
  /**
   * Given a prefix, return the namespace,
   */
  public String getURI(String prefix)
  {
    return m_nsSupport.getURI(prefix);
  }

  /**
   * Given a namespace, try and find a prefix.
   */
  public String getPrefix(String namespace)
  {
    // This Enumeration business may be too slow for our purposes...
    Enumeration enum = m_nsSupport.getPrefixes();
    while(enum.hasMoreElements())
    {
      String prefix = (String)enum.nextElement();
      if(m_nsSupport.getURI(prefix).equals(namespace))
        return prefix;
    }
    return null;
  }  
  
  /**
   * Get the NamespaceSupport object.
   */
  public NamespaceSupport getNamespaceSupport()
  {
    return m_nsSupport;
  }
  
  /**
   * Override QueuedEvents#initQSE.
   */
  protected void initQSE(QueuedSAXEvent qse)
  {
    qse.setContentHandler(m_contentHandler);
    qse.setTransformer(m_transformer);
    qse.setTraceManager(m_tracer);
  }
  
  /**
   * Return the current content handler.
   *
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #setContentHandler
   */
  public ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }
  
  /**
   * Set the current content handler.
   *
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #getContentHandler
   */
  public void setContentHandler(ContentHandler ch)
  {
    m_contentHandler = ch;
    reInitEvents();
  }

  /**
   * Get a unique namespace value.
   */
  public int getUniqueNSValue()
  {
    return m_uniqueNSValue++;
  }

  /**
   * Get new unique namespace prefix.
   */
  public String getNewUniqueNSPrefix()
  {
    return S_NAMESPACEPREFIX+String.valueOf(getUniqueNSValue());
  }
  
  /**
   * Get the pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  public MutableAttrListImpl getPendingAttributes()
  {
    return getQueuedElem().getAttrs();
  }
  
  /**
   * Add an attribute to the end of the list.
   * 
   * <p>Do not pass in xmlns decls to this function!
   *
   * <p>For the sake of speed, this method does no checking
   * to see if the attribute is already in the list: that is
   * the responsibility of the application.</p>
   *
   * @param uri The Namespace URI, or the empty string if
   *        none is available or Namespace processing is not
   *        being performed.
   * @param localName The local name, or the empty string if
   *        Namespace processing is not being performed.
   * @param rawName The raw XML 1.0 name, or the empty string
   *        if raw names are not available.
   * @param type The attribute type as a string.
   * @param value The attribute value.
   */
  public void addAttribute (String uri, String localName, String rawName,
                            String type, String value)
    throws SAXException
  {
    QueuedStartElement qe = getQueuedElem();
    if(!qe.nsDeclsHaveBeenAdded())
      addNSDeclsToAttrs();
    
    ensurePrefixIsDeclared(uri, localName);
    qe.addAttribute(uri, localName, rawName, type, value);
  }
  
  /**
   * Copy an DOM attribute to the created output element, executing
   * attribute templates as need be, and processing the xsl:use
   * attribute.
   */
  public void addAttribute( Attr attr )
    throws SAXException
  {
    DOMHelper helper = m_transformer.getXPathContext().getDOMHelper();
    addAttribute (helper.getNamespaceOfNode(attr), 
                  helper.getLocalNameOfNode(attr), 
                  attr.getNodeName(),
                  "CDATA", 
                  attr.getValue());
  } // end copyAttributeToTarget method
  
  /**
   * Copy DOM attributes to the result element.
   */
  public void addAttributes( Node src )
    throws SAXException
  {
    NamedNodeMap nnm = src.getAttributes();
    int nAttrs = nnm.getLength();
    for (int i = 0;  i < nAttrs;  i++)
    {
      addAttribute((Attr)nnm.item(i));
    }
  }
  
  /**
   * Tell if an element is pending, to be output to the result tree.
   */
  public boolean isElementPending()
  {
    QueuedStartElement qse = getQueuedElem();
    return (null != qse) ? qse.isPending() : false;
  }

  /**
   * Use the SAX2 helper class to track result namespaces.
   */
  private NamespaceSupport m_nsSupport = new NamespaceSupport();
  
  /**
   * The transformer object.
   */
  private TransformerImpl m_transformer;
  
  /**
   * The content handler.  May be null, in which 
   * case, we'll defer to the content handler in the 
   * transformer.
   */
  private ContentHandler m_contentHandler;
  
  /**
   * The root of a linked set of stylesheets.
   */
  private StylesheetRoot m_stylesheetRoot = null;
  
  /**
   * This is used whenever a unique namespace is needed.
   */
  private int m_uniqueNSValue = 0;
  
  private static final String S_NAMESPACEPREFIX = "ns";
  
  /**
   * This class clones nodes to the result tree.
   */
  private ClonerToResultTree m_cloner;
  
  /**
   * Trace manager for debug support.
   */
  private TraceManager m_tracer;
  
  
  // These are passed to flushPending, to help it decide if it 
  // should really flush.
  private static final int EVT_SETDOCUMENTLOCATOR = 1;
  private static final int EVT_STARTDOCUMENT = 2;
  private static final int EVT_ENDDOCUMENT = 3;
  private static final int EVT_STARTPREFIXMAPPING = 4;
  private static final int EVT_ENDPREFIXMAPPING = 5;
  private static final int EVT_STARTELEMENT = 6;
  private static final int EVT_ENDELEMENT = 7;
  private static final int EVT_CHARACTERS = 8;
  private static final int EVT_IGNORABLEWHITESPACE = 9;
  private static final int EVT_PROCESSINGINSTRUCTION = 10;
  private static final int EVT_SKIPPEDENTITY = 11;
  private static final int EVT_COMMENT = 12;
  private static final int EVT_ENTITYREF = 13;
  private static final int EVT_STARTENTITY = 14;
  private static final int EVT_ENDENTITY = 15;
  private static final int EVT_STARTDTD = 16;
  private static final int EVT_ENDDTD = 17;
  private static final int EVT_STARTNONESCAPING = 18;
  private static final int EVT_ENDNONESCAPING = 19;
  private static final int EVT_STARTPRESERVING = 20;
  private static final int EVT_ENDENDPRESERVING = 21;
  private static final int EVT_STARTCDATA = 22;
  private static final int EVT_ENDCDATA = 23;
  private static final int EVT_NODE = 24;

}
