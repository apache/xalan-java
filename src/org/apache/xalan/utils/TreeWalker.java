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
package org.apache.xalan.utils;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xpath.DOM2Helper;
import org.apache.xpath.DOMHelper;
import org.apache.xalan.utils.NodeConsumer;

import serialize.SerializerHandler;

/**
 * <meta name="usage" content="advanced"/>
 * This class does a pre-order walk of the DOM tree, calling a ContentHandler
 * interface as it goes.
 */
public class TreeWalker
{
  private ContentHandler m_contentHandler =   null;
  
  // ARGHH!!  JAXP Uses Xerces without setting the namespace processing to ON!
  // DOM2Helper m_dh = new DOM2Helper();
  protected DOMHelper m_dh = new DOM2Helper();
  
  /**
   * Get the ContentHandler used for the tree walk.
   */
  public ContentHandler getFormatterListener()
  {
    return m_contentHandler;
  }
  
  /**
   * Constructor.
   * @param   formatterListener The implemention of the 
   * FormatterListener operation (toXMLString, digest, ...)
   */
  public TreeWalker(ContentHandler formatterListener) 
  {
    this.m_contentHandler = formatterListener;
  }

  /**
   * Perform a pre-order traversal non-recursive style.
   */
  public void traverse(Node pos) throws SAXException 
  {
    Node top = pos;
    while(null != pos)
    {     
      startNode(pos);
      
      Node nextNode = pos.getFirstChild();
      while(null == nextNode)
      {
        endNode(pos);
        if(top.equals( pos ))
          break;
        nextNode = pos.getNextSibling();
        if(null == nextNode)
        {
          pos = pos.getParentNode();
          if((null == pos) || (top.equals( pos )))
          {
            if(null != pos)
              endNode(pos);
            nextNode = null;
            break;
          }
        }
      }
      pos = nextNode;
    }
  }

  /**
   * Perform a pre-order traversal non-recursive style.
   */
  public void traverse(Node pos, Node top) throws SAXException 
  {
    while(null != pos)
    {     
      startNode(pos);
      
      Node nextNode = pos.getFirstChild();
      while(null == nextNode)
      {
        endNode(pos);
        if((null != top) && top.equals( pos ))
          break;
        nextNode = pos.getNextSibling();
        if(null == nextNode)
        {
          pos = pos.getParentNode();
          if((null == pos) || ((null != top) && top.equals( pos )))
          {
            nextNode = null;
            break;
          }
        }
      }
      pos = nextNode;
    }
  }
  
  /*
  public void traverse(Node pos) throws SAXException 
  {
    startNode(pos);
    NodeList children = pos.getChildNodes();
    if(null != children)
    {
      int nChildren = children.getLength();
      for(int i = 0; i < nChildren; i++)
      {
        traverse(children.item(i));
      }
    }
    endNode(pos);
  }
  */
  
  boolean nextIsRaw = false;
  
  protected void startNode(Node node)
    throws SAXException 
  {
    if(m_contentHandler instanceof NodeConsumer)
    {
      ((NodeConsumer)m_contentHandler).setOriginatingNode(node);
    }
    switch(node.getNodeType())
    {
    case Node.COMMENT_NODE:
      {
        String data = ((Comment)node).getData();
        if(m_contentHandler instanceof LexicalHandler)
        {
          LexicalHandler lh = ((LexicalHandler)this.m_contentHandler);
          lh.comment(data.toCharArray(), 0, data.length());
        }
      }
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      // ??;
      break;
    case Node.DOCUMENT_NODE:
      this.m_contentHandler.startDocument();
      break;
    case Node.ELEMENT_NODE:
      NamedNodeMap atts = ((Element)node).getAttributes();
      int nAttrs = atts.getLength();
      for(int i = 0; i < nAttrs; i++)
      {
        Node attr = atts.item(i);
        String attrName = attr.getNodeName();
        if(attrName.equals("xmlns") || attrName.startsWith("xmlns:"))
        {
          int index;
          String prefix 
            = (index = attrName.indexOf(":"))< 0 
              ? null : attrName.substring(index+1);
          this.m_contentHandler.startPrefixMapping ( prefix, 
                                                     attr.getNodeValue());
        }
      }
      // System.out.println("m_dh.getNamespaceOfNode(node): "+m_dh.getNamespaceOfNode(node));
      // System.out.println("m_dh.getLocalNameOfNode(node): "+m_dh.getLocalNameOfNode(node));
      this.m_contentHandler.startElement (m_dh.getNamespaceOfNode(node), 
                                          m_dh.getLocalNameOfNode(node), 
                                          node.getNodeName(), 
                                          new AttList(atts, m_dh));
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      {
        ProcessingInstruction pi = (ProcessingInstruction)node;
        String name = pi.getNodeName();
        // String data = pi.getData();
        if(name.equals("xslt-next-is-raw"))
        {
          nextIsRaw = true;
        }
        else
        {
          this.m_contentHandler.processingInstruction(pi.getNodeName(), pi.getData());
        }
      }
      break;
    case Node.CDATA_SECTION_NODE:
      {
        String data = ((Text)node).getData();
        boolean isLexH = (m_contentHandler instanceof LexicalHandler);
        LexicalHandler lh = isLexH ? ((LexicalHandler)this.m_contentHandler) : null;
        if(isLexH)
        {
          lh.startCDATA();
        }
        this.m_contentHandler.characters(data.toCharArray(), 0, data.length());
        {
          if(isLexH)
          {
            lh.endCDATA();
          }
        }
      }
      break;
    case Node.TEXT_NODE:
      {
        String data = ((Text)node).getData();
        if(nextIsRaw)
        {
          nextIsRaw = false;
          boolean isSerH = (m_contentHandler instanceof SerializerHandler);
          SerializerHandler serH = isSerH ? ((SerializerHandler)this.m_contentHandler) : null;
          if(isSerH)
          {
            serH.startNonEscaping();
          }
          this.m_contentHandler.characters(data.toCharArray(), 0, data.length());
          {
            if(isSerH)
            {
              serH.endNonEscaping();
            }
          }
        }
        else
        {
          this.m_contentHandler.characters(data.toCharArray(), 0, data.length());
        }
      }
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference eref = (EntityReference)node;
        if(m_contentHandler instanceof LexicalHandler)
        {
          ((LexicalHandler)this.m_contentHandler).startEntity(eref.getNodeName());
        }
        else
        {
          // warning("Can not output entity to a pure SAX ContentHandler");
        }
      }
      break;
    default:
    }
  }

  protected void endNode(Node node)
    throws SAXException 
  {
    switch(node.getNodeType())
    {
    case Node.DOCUMENT_NODE:
      this.m_contentHandler.endDocument();
      break;
    case Node.ELEMENT_NODE:
      this.m_contentHandler.endElement("", "", node.getNodeName());
       NamedNodeMap atts = ((Element)node).getAttributes();
      int nAttrs = atts.getLength();
      for(int i = 0; i < nAttrs; i++)
      {
        Node attr = atts.item(i);
        String attrName = attr.getNodeName();
        if(attrName.equals("xmlns") || attrName.startsWith("xmlns:"))
        {
          int index;
          String prefix 
            = (index = attrName.indexOf(":"))< 0 
              ? null : attrName.substring(index+1);
          this.m_contentHandler.endPrefixMapping(prefix);
        }
      }
     break;
    case Node.CDATA_SECTION_NODE:
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference eref = (EntityReference)node;
        if(m_contentHandler instanceof LexicalHandler)
        {
          LexicalHandler lh = ((LexicalHandler)this.m_contentHandler);
          lh.endEntity(eref.getNodeName());
        }
      }
      break;
    default:
    }
  }
  
}  //TreeWalker
