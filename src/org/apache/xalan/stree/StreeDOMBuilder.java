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

import org.apache.xml.utils.DOMBuilder;
import org.apache.xml.utils.XMLCharacterRecognizer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * This class takes SAX events (in addition to some extra events
 * that SAX doesn't handle yet) and adds the result to a document
 * or document fragment.
 */
public class StreeDOMBuilder extends DOMBuilder
{

  /** This is the current text node that we don't append until the first 
   *  non-text event occurs following a characters event, so the threaded 
   *  transformer doesn't try and use it before it's time!   
   *  WARNING: Do NOT do a getNodeValue() or the like on this node while 
   *  it is accumulating text, as that will cause a string to be made, and 
   *  no more text will be obtained by the node!  */
  TextImpl m_text_buffer = null;

  /** Source document node */
  protected DocumentImpl m_docImpl;

  /**
   * State of the source tree indicating whether the last event
   * was a characters event.   
   */
  private boolean m_previousIsText = false;

  /** Indicate whether running in Debug mode        */
  private static final boolean DEBUG = false;

  /**
   * StreeDOMBuilder instance constructor... it will add the DOM nodes
   * to the document fragment.
   *
   * @param doc Root node of DOM being created
   * @param node Node currently being processed
   */
  public StreeDOMBuilder(Document doc, Node node)
  {

    super(doc, node);

    m_docImpl = (DocumentImpl) doc;
  }

  /**
   * StreeDOMBuilder instance constructor... it will add the DOM nodes
   * to the document fragment.
   *
   * @param doc Root node of DOM being created
   * @param docFrag Document fragment node of DOM being created
   */
  public StreeDOMBuilder(Document doc, DocumentFragment docFrag)
  {

    super(doc, docFrag);

    m_docImpl = (DocumentImpl) doc;
  }

  /**
   * StreeDOMBuilder instance constructor... it will add the DOM nodes
   * to the document.
   *
   * @param doc Root node of DOM being created
   */
  public StreeDOMBuilder(Document doc)
  {

    super(doc);

    m_docImpl = (DocumentImpl) doc;
  }

  /**
   * Set an ID string to node association in the ID table.
   *
   * @param id The ID string.
   * @param elem The associated ID.
   */
  public void setIDAttribute(String id, Element elem)
  {
    m_docImpl.setIDAttribute(id, elem);
  }

  /**
   * Receive notification of the beginning of an element.
   *
   *
   * @param ns namespace URL of the element
   * @param localName local part of qualified name of the element
   * @param name The element type name.
   * @param atts The attributes attached to the element, if any.
   * @see org.apache.xml.utils.DOMBuilder#startElement(String, String, String, Attributes)
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String ns, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {
    setPreviousIsText(false);
    ElementImpl elem;

    if ((null == ns) || (ns.length() == 0))
      elem = (ElementImpl)m_doc.createElement(name);
    else
      elem = (ElementImpl)m_doc.createElementNS(ns, name);

    // if you do the append here, the element might be accessed before
    // the attributes are added.
    // append(elem);
    
    // But, in order for the document order stuff to be done correctly, we 
    // have to set the uid here, before the attributes are counted.
    elem.m_uid = ++((DocImpl)m_doc).m_docOrderCount;
    elem.m_level = (short) (((DocImpl)m_doc).m_level + 1);

    int nAtts = atts.getLength();

    if (0 != nAtts)
    {
      for (int i = 0; i < nAtts; i++)
      {

        //System.out.println("type " + atts.getType(i) + " name " + atts.getLocalName(i) );
        // First handle a possible ID attribute
        if (atts.getType(i).equalsIgnoreCase("ID"))
          setIDAttribute(atts.getValue(i), elem);

        String attrNS = atts.getURI(i);
        
        if(attrNS == null)
          attrNS = ""; // defensive, shouldn't have to do this.

        // System.out.println("attrNS: "+attrNS+", localName: "+atts.getQName(i)
        //                   +", qname: "+atts.getQName(i)+", value: "+atts.getValue(i));
        // Crimson won't let us set an xmlns: attribute on the DOM.
        if ((attrNS.length() == 0) || atts.getQName(i).startsWith("xmlns:"))
          elem.setAttribute(atts.getQName(i), atts.getValue(i));
        else
        {

          // elem.setAttributeNS(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
          elem.setAttributeNS(attrNS, atts.getQName(i), atts.getValue(i));
        }
      }
    }
    
    append(elem);

    m_elemStack.push(elem);

    m_currentNode = elem;
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param ns namespace URL of the element
   * @param localName local part of qualified name of the element
   * @param name The element type name
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String ns, String localName, String name)
          throws org.xml.sax.SAXException
  {
    setPreviousIsText(false);
    // ((Parent)getCurrentNode()).setComplete(true);
    super.endElement(ns, localName, name);
  }

  /**
   * Receive notification of character data.
   *
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws org.xml.sax.SAXException
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {
    if(DEBUG)
    {
      System.out.print("SourceTreeDOMBuilder#characters: ");
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

    if (getPreviousIsText())
      appendAccumulatedText(m_text_buffer, ch, start, length);
    else
    {
        //      if (m_inCData)
        //      {
        // CDATA SECTIONS DON'T REALLY WORK IN THE STREE.  I WOULD 
        // LEAVE THIS, BUT THE APPEND MODE MAKES IT STRANGE...
        //        m_text_buffer = (new CDATASectionImpl(m_docImpl, ch, start,
        //                                                  length));
        //      }
        //      else
        m_text_buffer = (new TextImpl(m_docImpl, ch, start, length));

      setPreviousIsText(true);
    }

  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws org.xml.sax.SAXException
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    if (getPreviousIsText())
      appendAccumulatedText(m_text_buffer, ch, start, length);
    else
      m_text_buffer = new TextImpl(m_docImpl, ch, start, length);

    setPreviousIsText(true);
  }

  /**
   * If available, when the disable-output-escaping attribute is used,
   * output raw text without escaping.  A PI will be inserted in front
   * of the node with the name "lotusxsl-next-is-raw" and a value of
   * "formatter-to-dom".
   *
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws org.xml.sax.SAXException
   *
   * @throws org.xml.sax.SAXException
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    setPreviousIsText(false);
    append(m_doc.createProcessingInstruction("xslt-next-is-raw",
                                             "formatter-to-dom"));
    append(new TextImpl(m_docImpl, ch, start, length));
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   *
   * @throws org.xml.sax.SAXException
   *
   * @throws org.xml.sax.SAXException
   */
  public void comment(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {
    setPreviousIsText(false);
    append(new CommentImpl(m_docImpl, ch, start, length));
  }


  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @see #characters
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {
    setPreviousIsText(false);
    super.processingInstruction(target, data);
  }

  /**
   * Set the state of the source tree to indicate whether the last event
   * was a characters event.
   *
   *
   * @param isText True if last event was a characters event
   *
   *
   * @throws org.xml.sax.SAXException
   */
  public void setPreviousIsText(boolean isText) throws org.xml.sax.SAXException
  {

    if (m_previousIsText && !isText)
    {
      if (!( m_docImpl.m_sourceTreeHandler.getShouldStripWhitespace()
        && m_text_buffer.isWhitespace() ))
      {
        append(m_text_buffer);
      }

      m_text_buffer = null;
    }

    m_previousIsText = isText;
  }
  
  /**
   * Receive notification of the end of a document.
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   */
  public void endDocument() throws org.xml.sax.SAXException
  {
    if(DEBUG)
    {
      System.out.println("SourceTreeDOMBuilder#endDocument");
    }
    super.endDocument();
    setPreviousIsText(false);
  }


  /**
   * Get the state of the source tree indicating whether the last event
   * was a characters event.
   *
   *
   * @return True if last event was a characters event
   *
   */
  boolean getPreviousIsText()
  {
    return m_previousIsText;
  }

  /**
   * Append the text from this characters event to the previous text.
   *
   *
   * @param textNode Text node created by previous characters event.
   *
   * @param currentNode The current node.
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   *
   * @throws org.xml.sax.SAXException
   */
  void appendAccumulatedText(
          Node currentNode, char ch[], int start, int length)
            throws org.xml.sax.SAXException
  {

    // Currently, we don't call this unless there _is_ a text
    // outstanding... I think... but let's be paranoid.
    if (m_text_buffer == null)
    {
      m_text_buffer = new TextImpl(m_docImpl, ch, start, length);

      setPreviousIsText(true);
    }
    else
      ((TextImpl) m_text_buffer).appendText(ch, start, length);
      
    if(DEBUG)
    {
      System.out.print("SourceTreeDOMBuilder#appendAccumulatedText: ");
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


  }
}
