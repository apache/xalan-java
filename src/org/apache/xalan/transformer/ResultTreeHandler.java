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

import org.w3c.dom.*;
import java.util.Stack;
import java.util.Enumeration;
import java.io.Writer;
import java.io.OutputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.NamespaceSupport;
import org.apache.xalan.utils.DOMBuilder;
import org.apache.xalan.utils.TreeWalker;
import org.apache.xalan.utils.MutableAttrListImpl;
import org.apache.xalan.utils.StringToStringTable;
import org.apache.xalan.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.OutputFormatExtended;
import serialize.SerializerFactory;
import serialize.OutputFormat;
import serialize.Method;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import serialize.SerializerHandler;
import serialize.Serializer;

/**
 * This class is a layer between the direct calls to the result 
 * tree content handler, and the transformer.  For one thing, 
 * we have to delay the call to
 * getContentHandler().startElement(name, atts) because of the
 * xsl:attribute and xsl:copy calls.  In other words,
 * the attributes have to be fully collected before you
 * can call startElement.
 */
public class ResultTreeHandler 
  implements ContentHandler, SerializerHandler, LexicalHandler
{
  /**
   * Create a new result tree handler.  The real content 
   * handler will be the ContentHandler of the transformer.
   */
  public ResultTreeHandler(TransformerImpl transformer)
  {
    m_transformer = transformer;
  }

  /**
   * Create a new result tree handler.  The real content 
   * handler will be the ContentHandler passed as an argument.
   */
  public ResultTreeHandler(TransformerImpl transformer,
                           ContentHandler realHandler)
  {
    m_transformer = transformer;
    m_contentHandler = realHandler;
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
      flushPending(); // I think.
      tw.traverse(nl.item(i));
    }
  }
  
  /**
   * Clone an element with or without children.
   * TODO: Fix or figure out node clone failure!
   * the error condition is severe enough to halt processing.
   */
  public void cloneToResultTree(Stylesheet stylesheetTree, Node node,
                                boolean shouldCloneWithChildren,
                                boolean overrideStrip,
                                boolean shouldCloneAttributes)
    throws SAXException
  {
    boolean stripWhiteSpace = false;
    XPathContext xctxt = this.m_transformer.getXPathContext();
    DOMHelper dhelper = xctxt.getDOMHelper();

    switch(node.getNodeType())
    {
    case Node.TEXT_NODE:
      {
        // If stripWhiteSpace is false, then take this as an override and
        // just preserve the space, otherwise use the XSL whitespace rules.
        if(!overrideStrip)
        {
          // stripWhiteSpace = isLiteral ? true : shouldStripSourceNode(node);
          stripWhiteSpace = false;
        }
        Text tx = (Text)node;
        String data = null;
        // System.out.println("stripWhiteSpace = "+stripWhiteSpace+", "+tx.getData());
        if(stripWhiteSpace)
        {
          if(!dhelper.isIgnorableWhitespace(tx))
          {
            data = tx.getData();
            if((null != data) && (0 == data.trim().length()))
            {
              data = null;
            }
          }
        }
        else
        {
          Node parent = node.getParentNode();
          if(null != parent)
          {
            if( Node.DOCUMENT_NODE != parent.getNodeType())
            {
              data = tx.getData();
              if((null != data) && (0 == data.length()))
              {
                data = null;
              }
            }
          }
          else
          {
            data = tx.getData();
            if((null != data) && (0 == data.length()))
            {
              data = null;
            }
          }
        }

        if(null != data)
        {
          // TODO: Hack around the issue of comments next to literals.
          // This would be, when a comment is present, the whitespace
          // after the comment must be added to the literal.  The
          // parser should do this, but XML4J doesn't seem to.
          // <foo>some lit text
          //     <!-- comment -->
          //     </foo>
          // Loop through next siblings while they are comments, then,
          // if the node after that is a ignorable text node, append
          // it to the text node just added.
          if(dhelper.isIgnorableWhitespace(tx))
          {
            ignorableWhitespace(data.toCharArray(), 0, data.length());
          }
          else
          {
            characters(data.toCharArray(), 0, data.length());
          }
        }
      }
      break;
    case Node.DOCUMENT_NODE:
      // Can't clone a document, but refrain from throwing an error
      // so that copy-of will work
      break;
    case Node.ELEMENT_NODE:
      {
        Attributes atts;
        if(shouldCloneAttributes)
        {
          addAttributes( node );
          processNSDecls(node);
        }
        String ns = dhelper.getNamespaceOfNode(node);
        String localName = dhelper.getLocalNameOfNode(node);
        startElement (ns, localName, node.getNodeName());
      }
      break;
    case Node.CDATA_SECTION_NODE:
      {
        String data = ((CDATASection)node).getData();
        cdata(data.toCharArray(), 0, data.length());
      }
      break;
    case Node.ATTRIBUTE_NODE:
      {
        String ns = dhelper.getNamespaceOfNode(node);
        String localName = dhelper.getLocalNameOfNode(node);
        addAttribute(ns, localName, node.getNodeName(), "CDATA", 
                     ((Attr)node).getValue());
      }
      break;
    case Node.COMMENT_NODE:
      {
        comment(((Comment)node).getData());
      }
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      {
        
        m_transformer.getMsgMgr().error(null, node, XSLTErrorResources.ER_NO_CLONE_OF_DOCUMENT_FRAG); //"No clone of a document fragment!");
      }
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference er = (EntityReference)node;
        entityReference(er.getNodeName());
      }
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      {
        ProcessingInstruction pi = (ProcessingInstruction)node;
        processingInstruction(pi.getTarget(), pi.getData());
      }
      break;
    default:
      m_transformer.getMsgMgr().error(XSLTErrorResources.ER_CANT_CREATE_ITEM, new Object[] {node.getNodeName()}); //"Can not create item in result tree: "+node.getNodeName());
    }

  } // end cloneToResultTree function
  


  /**
   * Check to see if the output prefix should be excluded.
   */
  private String excludePrefix(String name)
  {
    if(null != m_stylesheetRoot) // Just extra defensive
    {
      int indexOfNSSep = name.indexOf(':');
      if(indexOfNSSep > 0)
      {
        String prefix = name.substring(0, indexOfNSSep);
        if(m_stylesheetRoot.containsExcludeResultPrefix(prefix))
          name = name.substring(indexOfNSSep+1);
      }
    }
    return name;
  }


  /**
   * Flush the pending element.
   */
  public void flushPending()
    throws SAXException
  {
    // The stylesheet is set at a rather late stage, so I do 
    // this here, though it would probably be better done elsewhere.
    if((null == m_stylesheetRoot) && (null != m_transformer))
      m_stylesheetRoot = m_transformer.getStylesheet();
    
    if(!m_foundStartDoc && (null != m_pendingElementName))
    {
     if(null != m_stylesheetRoot && !m_stylesheetRoot.isOutputMethodSet())
      {
        if(m_pendingElementName.equalsIgnoreCase("html") 
           && (null == m_nsSupport.getURI("")))
        {
          OutputFormat oformat = m_stylesheetRoot.getOutputFormat();
          oformat.setMethod(Method.HTML);
          try
          {
            Serializer oldSerializer = m_transformer.getSerializer();
            Serializer serializer = SerializerFactory.getSerializer(oformat);
            Writer writer = oldSerializer.getWriter();
            if(null != writer)
              serializer.setWriter(writer);
            else
            {
              OutputStream os = serializer.getOutputStream();
              serializer.setOutputStream(os);
            }
            m_transformer.setSerializer(serializer);
            ContentHandler ch = serializer.asContentHandler();
            m_transformer.setContentHandler(ch);
            m_contentHandler = ch;
          }
          catch(java.io.IOException e)
          {
            throw new SAXException(e);
          }
        }
      }
    }
    // System.out.println("m_pendingStartDoc: "+m_pendingStartDoc);
    // System.out.println("m_haveDocContent: "+m_haveDocContent);
    if (!m_foundStartDoc && m_haveDocContent)
    {
      startDocument();
      m_haveDocContent = true;
    }

    if (m_pendingStartDoc && m_haveDocContent)
    {
      m_pendingStartDoc = false;
      getContentHandler().startDocument();
      m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                                          GenerateEvent.EVENTTYPE_STARTDOCUMENT));
    }

//    if((null != m_pendingElementName) && m_haveDocContent)
    if (null != m_pendingElementName)
    {
      /*
      if(null != m_stylesheetRoot.getCDataSectionElems())
      {
      if(isCDataResultElem(m_pendingElementName))
      {
      m_cdataStack.push(TRUE);
      }
      else
      {
      m_cdataStack.push(FALSE);
      }
      }
      */      
      if(!m_nsDeclsHaveBeenAdded)
        addNSDeclsToAttrs();

      // System.out.print("Calling start element: "+m_pendingElementName+"... ");
      // System.out.print(" on: "+getContentHandler()+"... ");
      // System.out.flush();
      getContentHandler().startElement(m_pendingElementNS, 
                                       m_pendingElementLName, 
                                       m_pendingElementName, 
                                       m_pendingAttributes);
      // System.out.println("Done calling start element: "+m_pendingElementLName);
      // System.out.flush();
      m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                                          GenerateEvent.EVENTTYPE_STARTELEMENT,
                                                                          m_pendingElementName, m_pendingAttributes));
      clearPendingElement();
    }
  }
  
  /**
   * To fullfill the FormatterListener interface... not action
   * for the moment.
   */
  public void setDocumentLocator (Locator locator)
  {
  }
  
  public boolean getFoundStartDoc() { return m_foundStartDoc; }
  /**
   * Flag to indicate whether or not our startDocument() method has been called.
   */
  private boolean m_foundStartDoc = false;

  public boolean getFoundEndDoc() { return m_foundEndDoc; }
  private boolean m_foundEndDoc = false;

  /**
   * Bottleneck the startDocument event.
   */
  public void startDocument ()
    throws SAXException
  {
    // m_uniqueNSValue = 0;
    m_foundStartDoc = true;
    m_pendingStartDoc = true;
    m_haveDocContent = false;
    // m_flistener.startDocument();
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_STARTDOCUMENT));
    ContentHandler chandler = getContentHandler();
    if((null != chandler) 
       && (chandler instanceof TransformerClient))
    {
      ((TransformerClient)chandler).setTransformState(m_transformer);
    }
  }

  /**
   * Bottleneck the endDocument event.
   */
  public void endDocument ()
    throws SAXException
  {
    m_foundEndDoc = true;
    m_haveDocContent = true;
    flushPending();
    getContentHandler().endDocument();
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_ENDDOCUMENT));
    // m_variableStacks.popCurrentContext();
  }

  /**
   * Bottleneck the startElement event.  This is used to "pend" an
   * element, so that attributes can still be added to it before 
   * the real "startElement" is called on the result tree listener.
   */
  public void startElement (String ns, String localName, String name)
    throws SAXException
  {
    flushPending();
    m_nsSupport.pushContext();
    setPendingElementName (ns, localName, name);
    m_haveDocContent = true;
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
    flushPending();
    m_nsSupport.pushContext();
    m_pendingAttributes.clear(); // Is this needed?? -sb
    m_pendingAttributes.addAttributes(atts);
    setPendingElementName (ns, localName, name);
    m_haveDocContent = true;
  }

  /**
   * Bottleneck the endElement event.
   */
  public void endElement (String ns, String localName, String name)
    throws SAXException
  {
    // name = excludePrefix(name);
    flushPending();
    getContentHandler().endElement(ns, localName, name);
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_ENDELEMENT,
                                                      name, (Attributes)null));
    // if(null != m_stylesheetRoot.getCDataSectionElems())
    //  m_cdataStack.pop();
    m_nsSupport.popContext();
  }

  /**
   * Bottleneck the characters event.
   */
  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    if(!m_haveDocContent)
    {
      int n = ch.length;
      for(int i = 0; i < n; i++)
      {
        // todo: isSpaceChar doesn't seem to be recognizing 0x0A.  (??) -sb
        if(!Character.isSpaceChar(ch[i]))
        {
          m_haveDocContent = true;
          break;
        }
      }
    }
    if(m_haveDocContent)
    {
      flushPending();
    } 
    
    getContentHandler().characters(ch, start, length);
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                                        GenerateEvent.EVENTTYPE_CHARACTERS,
                                                                        ch, start, length));
  }

  /**
   * Bottleneck the ignorableWhitespace event.
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    if(m_haveDocContent)
    {
      flushPending();
      getContentHandler().ignorableWhitespace(ch, start, length);
      m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                        GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE,
                                                        ch, start, length));
    }
  }

  /**
   * Bottleneck the processingInstruction event.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    m_haveDocContent = true;
    flushPending();
    getContentHandler().processingInstruction(target, data);
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_PI,
                                                      target, data));
  }

  /**
   * Bottleneck the comment event.
   */
  public void comment(String data) throws SAXException
  {
    m_haveDocContent = true;
    flushPending();
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).comment(data.toCharArray(), 0, data.length());
    }
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_COMMENT,
                                                      data));
  }

  /**
   * Bottleneck the comment event.
   */
  public void comment(char ch[], int start, int length) throws SAXException
  {
    m_haveDocContent = true;
    flushPending();
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).comment(ch, start, length);
    }
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_COMMENT,
                                                      new String(ch, start, length)));
  }


  /**
   * Bottleneck the comment event.
   */
  public void entityReference(String name) throws SAXException
  {
    m_haveDocContent = true;
    flushPending();
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).startEntity(name);
      ((LexicalHandler)getContentHandler()).endEntity(name);
    }
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_ENTITYREF,
                                                      name));
  }

  /**
   * Start an entity.
   */
  public void startEntity(String name) throws SAXException
  {
    m_haveDocContent = true;
    flushPending();
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
    m_haveDocContent = true;
    flushPending();
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).endEntity(name);
    }
    m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                      GenerateEvent.EVENTTYPE_ENTITYREF,
                                                      name));
  }

  /**
   * Start the DTD.
   */
  public void startDTD(String s1, String s2, String s3) throws SAXException
  {
    flushPending();
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
    flushPending();
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
    flushPending();
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
    flushPending();
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
    flushPending();
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
    flushPending();
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
    flushPending();
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
    flushPending();
    if(getContentHandler() instanceof LexicalHandler)
    {
      ((LexicalHandler)getContentHandler()).endCDATA();
    }
  }

  /**
   * Bottleneck the cdata event.
   */
  public void cdata (char ch[], int start, int length)
    throws SAXException
  {
    m_haveDocContent = true;
    flushPending();
    /*
    if((null != m_stylesheetRoot.getCDataSectionElems()) &&
    !m_cdataStack.isEmpty() && (m_cdataStack.peek() == TRUE))
    {
    // boolean isLexH = (getContentHandler() instanceof LexicalHandler);

    if(getContentHandler() instanceof LexicalHandler)
    ((LexicalHandler)getContentHandler()).startCDATA();

    getContentHandler().characters(ch, start, length);

    if(getContentHandler() instanceof LexicalHandler)
    ((LexicalHandler)getContentHandler()).endCDATA();

    if(null != m_traceListeners)
    fireGenerateEvent(new GenerateEvent(m_transformer,
    GenerateEvent.EVENTTYPE_CDATA,
    ch, start, length));
    }
    else
    */
    {
      getContentHandler().characters(ch, start, length);
      m_transformer.getTraceManager().fireGenerateEvent(new GenerateEvent(m_transformer,
                                                        GenerateEvent.EVENTTYPE_CHARACTERS,
                                                        ch, start, length));
    }

    /*
    if(getContentHandler() instanceof FormatterListener)
    {
    ((FormatterListener)getContentHandler()).cdata(ch, start, length);
    }
    else
    {
    // Bad but I think it's better than dropping it.
    getContentHandler().characters(ch, start, length);
    }
    ((FormatterListener)getContentHandler()).cdata(ch, start, length);
    if(null != m_traceListeners)
    fireGenerateEvent(new GenerateEvent(this,
    GenerateEvent.EVENTTYPE_CDATA,
    ch, start, length));
    }
    */
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
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   */
  protected void addNSDeclsToAttrs()
  {
    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    while (prefixes.hasMoreElements()) 
    {
      String prefix = (String)prefixes.nextElement();
      boolean isDefault = (prefix.length() == 0);
      String name;
      if(isDefault)
      {
        prefix = "xml";
        name = "xmlns";
      }
      else
        name="xmlns:"+prefix;
      
      // System.out.println("Adding xmlns: "+name);
        
      // System.out.println("calling addAttribute(null, null, "+name+", 'CDATA', "+
      //                   m_nsSupport.getURI(prefix)+");");
      m_pendingAttributes.addAttribute("http://www.w3.org/2000/xmlns/", 
                                       prefix, 
                                       name, "CDATA", m_nsSupport.getURI(prefix));
    }
    m_nsDeclsHaveBeenAdded = true;
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
    Node parent = src;
    while (parent != null
           && ((type = parent.getNodeType()) == Node.ELEMENT_NODE
               || (type == Node.ENTITY_REFERENCE_NODE)))
    {
      if (type == Node.ELEMENT_NODE)
      {
        NamedNodeMap nnm = parent.getAttributes();
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
      parent = parent.getParentNode();
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
    // System.out.println("startPrefixMapping("+prefix+", "+uri+")");
    String existingURI = m_nsSupport.getURI(prefix);
    if((null == existingURI) || !existingURI.equals(uri))
    {
      m_nsSupport.declarePrefix(prefix, uri);
      getContentHandler().startPrefixMapping(prefix, uri);
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
    getContentHandler().endPrefixMapping(prefix);
  }
  
  /**
   * Use the SAX2 helper class to track result namespaces.
   */
  private NamespaceSupport m_nsSupport = new NamespaceSupport();
  
  /**
   * Get the NamespaceSupport object.
   */
  public NamespaceSupport getNamespaceSupport()
  {
    return m_nsSupport;
  }
  
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
   * Return the current content handler, which may be a content
   * handler stored in this object, or it may delegate to the 
   * content handler in the transformer.
   *
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #setContentHandler
   */
  public ContentHandler getContentHandler()
  {
    return (null == m_contentHandler) 
           ? m_transformer.getContentHandler() : m_contentHandler;
  }

  
  /**
   * The root of a linked set of stylesheets.
   */
  private StylesheetRoot m_stylesheetRoot = null;
      
  /**
   * This is used whenever a unique namespace is needed.
   */
  private int m_uniqueNSValue = 0;
  
  /**
   * Get a unique namespace value.
   */
  public int getUniqueNSValue()
  {
    return m_uniqueNSValue++;
  }
  
  private static final String S_NAMESPACEPREFIX = "ns";
  
  /**
   * Get new unique namespace prefix.
   */
  public String getNewUniqueNSPrefix()
  {
    return S_NAMESPACEPREFIX+String.valueOf(getUniqueNSValue());
  }

  /**
   * Flag to indicate that we have some document content since the last
   * call to startDocument()
   */
  private boolean m_haveDocContent = false;
  
  /**
   * The pending element, namespace, and local name.
   */
  private String m_pendingElementName;
  private String m_pendingElementNS; 
  private String m_pendingElementLName;
  
  /**
   * Set the pending element name.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  private void setPendingElementName (String ns, String localName, String name)
  {
    m_pendingElementName = name;
    m_pendingElementNS = ns; 
    m_pendingElementLName = localName;
  }  
  
  /**
   * Get the pending element name.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  public String getPendingElementName()
  {
    return m_pendingElementName;
  }
  
  /**
   * Clear the pending element values.  This needs to be called 
   * after the real startElement event is sent to the listener.
   */
  private void clearPendingElement()
  {
    m_pendingAttributes.clear();
    m_nsDeclsHaveBeenAdded = false;
    m_pendingElementName = null;
    m_pendingElementNS = null; 
    m_pendingElementLName = null;
  }

  /**
   * Flag to tell if a call to getContentHandler().startDocument is pending.
   */
  private boolean m_pendingStartDoc = false;

  /**
   * The pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  private MutableAttrListImpl m_pendingAttributes = new MutableAttrListImpl();
  
  /**
   * Flag to try and get the xmlns decls to the attribute list 
   * before other attributes are added.
   */
  private boolean m_nsDeclsHaveBeenAdded = false;
  
  
  /**
   * Get the pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  public MutableAttrListImpl getPendingAttributes()
  {
    return m_pendingAttributes;
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
  {
    if(!m_nsDeclsHaveBeenAdded)
      addNSDeclsToAttrs();
    m_pendingAttributes.addAttribute(uri, localName, rawName, type, value);
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

}
