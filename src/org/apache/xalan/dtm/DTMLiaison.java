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
package org.apache.xalan.dtm;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.*;
import org.apache.xerces.framework.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import org.apache.xpath.DOM2Helper;
import org.apache.xpath.XPathFactory;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * <meta name="usage" content="internal"/>
 * Liaison to Document Table Model (DTM) XML parser -- the default liaison and parser that XSLTProcessor
 * uses to perform transformations.
 * To enhance performance, DTM uses integer arrays to represent a DOM. If you are reading or writing a
 * DOM, use DOM2Helper.
 *
 * @see org.apache.xalan.xslt.XSLTProcessor
 * @see org.apache.xpath.DOM2Helper
 */
public class DTMLiaison extends DOM2Helper
{
  /**
   * Flag to tell whether or not the parse is done on a seperate thread,
   * so the transform can occur at the same time.  The default
   * is true.
   */
  private boolean m_doThreading = false;

  /**
   * Set whether or not the parse is done on a seperate thread,
   * so the transform can occur at the same time.  The default
   * is true.
   */
  boolean getDoThreading()
  {
    return m_doThreading;
  }

  /**
   * Set whether or not the parse is done on a seperate thread,
   * so the transform can occur at the same time.  The default
   * is true.
   */
  void setDoThreading(boolean b)
  {
    m_doThreading = b;
  }

  /**
   * Construct an instance.
   */
  public DTMLiaison()
  {
  }
  
  public String getUniqueID(Node node)
  {
    return "N"+Integer.toHexString(((DTMProxy)node).getDTMNodeNumber())
      +Integer.toHexString(((DTMProxy)node).getDTM().hashCode());
  }

  /**
   * Check node to see if it matches this liaison.
   */
  public void checkNode(Node node)
    throws SAXException
  {
    if(!(node instanceof DTMProxy))
      throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_DTM_CANNOT_HANDLE_NODES, new Object[]{((Object)node).getClass()})); //"DTMLiaison can not handle nodes of type"
        //+((Object)node).getClass());
  }
  
  /**
   * Figure out if node2 should be placed after node1 when 
   * placing nodes in a list that is to be sorted in 
   * document order.  Assumes that node1 and node2 are not
   * equal.
   * NOTE: Make sure this does the right thing with attribute nodes!!!
   * @return true if node2 should be placed 
   * after node1, and false if node2 should be placed 
   * before node1.
   */
  public boolean isNodeAfter(Node node1, Node node2)
  {
    // Assume first that the nodes are DTM nodes, since discovering node 
    // order is massivly faster for the DTM.
    try
    {
      int index1 = ((DTMProxy)node1).getDTMNodeNumber();
      int index2 = ((DTMProxy)node2).getDTMNodeNumber();
      return index1 <= index2;
    }
    catch(ClassCastException cce)
    {
      // isNodeAfter will return true if node is after countedNode 
      // in document order. isDOMNodeAfter is sloooow (relativly).
      return super.isNodeAfter(node1, node2);
    }
  }    

  /**
   * Parse an XML document.
   *
   * <p>The application can use this method to instruct the SAX parser
   * to begin parsing an XML document from any valid input
   * source (a character stream, a byte stream, or a URI).</p>
   *
   * <p>Applications may not invoke this method while a parse is in
   * progress (they should create a new Parser instead for each
   * additional XML document).  Once a parse is complete, an
   * application may reuse the same Parser object, possibly with a
   * different input source.</p>
   *
   * @param source The input source for the top-level of the
   *        XML document.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.InputSource
   * @see #parse(java.lang.String)
   * @see #setEntityResolver
   * @see #setDTDHandler
   * @see #setContentHandler
   * @see #setErrorHandler
   */
  public void parse (InputSource source)
    throws SAXException
  {
    if(true)
    {
      DTM parser = new DTM();
      Thread parseThread = null;
      {
        String ident = (null == source.getSystemId())
                       ? "Input XSL" : source.getSystemId();
        parser.setErrorHandler(new org.apache.xalan.utils.DefaultErrorHandler(ident));

        // if(null != m_entityResolver)
        // {
          // System.out.println("Setting the entity resolver.");
        //  parser.setEntityResolver(m_entityResolver);
        // }

        // if(getUseValidation())
        //  parser.setFeature("http://xml.org/sax/features/validation", true);

        // Set whether or not to create entity ref nodes
        // parser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", getShouldExpandEntityRefs());

        if(m_doThreading)
        {
          try
          {
            parser.parse(source);
          }
          catch(IOException ioe)
          {
            throw new SAXException(ioe);
          }
        }
        else
        {
          parser.setInputSource(source);
          parseThread = new Thread(parser);
          try
          {
            parseThread.start();
          }
          catch(RuntimeException re)
          {
            throw new SAXException(re.getMessage());
          }
        }

        setDocument(parser.getDocument());
      }
    }
    else
    {
      super.parse(source);
    }
  }

  /**
   * Returns true if the liaison supports the SAX ContentHandler
   * interface.  The default is that the parser does not support
   * the SAX interface.
   */
  public boolean supportsSAX()
  {
    return true;
  }

  /**
   * Returns the namespace of the given node.
   */
  public String getNamespaceOfNode(Node n)
  {
    return ((org.apache.xalan.dtm.DTMProxy)n).getNamespaceURI();
  }

  /**
   * Returns the local name of the given node.
   */
  public String getLocalNameOfNode(Node n)
  {
    return ((org.apache.xalan.dtm.DTMProxy)n).getLocalName();
  }

  /**
   * Get the parent of a node.
   */
  public Node getParentOfNode(Node n)
    throws RuntimeException
  {
    return ((org.apache.xalan.dtm.DTMProxy)n).getOwnerNode();
  }

  /**
   * Given an ID, return the element.
   */
  public Element getElementByID(String id, Document doc)
  {
    return ((DTMProxy)doc).getDTM().getIdentifier(id);
  }
  
  public void setIDAttribute(String namespaceURI,
                             String qualifiedName,
                             String value,
                             Element elem)
  {
    ((org.apache.xalan.stree.DocumentImpl)this.getDocument()).setIDAttribute(namespaceURI, qualifiedName, value, elem);
  }

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   * Since it states in the DOM draft: "An XML processor may choose to
   * completely expand entities before the structure model is passed
   * to the DOM; in this case, there will be no EntityReferences in the DOM tree."
   * So I'm not sure how well this is going to work.
   */
  public String getUnparsedEntityURI(String name, Document doc)
  {
    String url = null;
    DTMProxy docp = (DTMProxy)doc;
    DTM dtm = docp.dtm;
    int nameindex = dtm.getStringPool().addSymbol(name);
    int entityRefIndex = dtm.m_entities.get(nameindex);
    int entityRef[] = {0, 0, 0, 0};
    dtm.m_entityNodes.readSlot(entityRefIndex, entityRef);
    if((entityRef[3] >> 16) != 0)
    {
      url = dtm.getStringPool().toString(entityRef[3] & 0xFF);
      if(null == url)
      {
        url = dtm.getStringPool().toString(entityRef[2]);
      }
      else
      {
        // This should be resolved to an absolute URL, but that's hard
        // to do from here.
      }
    }

    return url;
    
    /*
    String url = "";
    DocumentType doctype = doc.getDoctype();
    if(null != doctype)
    {
      NamedNodeMap entities = doctype.getEntities();
      Entity entity = (Entity)entities.getNamedItem(name);
      String notationName = entity.getNotationName();
      if(null != notationName) // then it's unparsed
      {
        // The draft says: "The XSLT processor may use the public
        // identifier to generate a URI for the entity instead of the URI
        // specified in the system identifier. If the XSLT processor does
        // not use the public identifier to generate the URI, it must use
        // the system identifier; if the system identifier is a relative
        // URI, it must be resolved into an absolute URI using the URI of
        // the resource containing the entity declaration as the base
        // URI [RFC2396]."
        // So I'm falling a bit short here.
        url = entity.getSystemId();
        if(null == url)
        {
          url = entity.getPublicId();
        }
        else
        {
          // This should be resolved to an absolute URL, but that's hard
          // to do from here.
        }
      }
    }
    return url;
    */
  }


}
