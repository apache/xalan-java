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

import org.apache.xalan.utils.DOMBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;

/**
 * <meta name="usage" content="internal"/>
 * This class takes SAX events (in addition to some extra events
 * that SAX doesn't handle yet) and adds the result to a document
 * or document fragment.
 */
public class StreeDOMBuilder extends DOMBuilder
{

  /** Source document node          */
  protected DocumentImpl m_docImpl;

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
   * Receive notification of character data.
   *
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws TransformerException
   */
  public void characters(char ch[], int start, int length) throws org.xml.sax.SAXException
  {

    if (m_inCData)
      append(new CDATASectionImpl(m_docImpl, ch, start, length));
    else
      append(new TextImpl(m_docImpl, ch, start, length));
  }

  /**
   * Receive notification of ignorable whitespace in element content. 
   *
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws TransformerException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {
    append(new TextImpl(m_docImpl, ch, start, length));
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
   * @throws TransformerException
   */
  public void charactersRaw(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

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
   * @throws TransformerException
   */
  public void comment(char ch[], int start, int length) throws org.xml.sax.SAXException
  {
    append(new CommentImpl(m_docImpl, ch, start, length));
  }

  /**
   * Receive notification of cdata. 
   *
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   *
   * @throws TransformerException
   */
  public void cdata(char ch[], int start, int length) throws org.xml.sax.SAXException
  {
    append(new CDATASectionImpl(m_docImpl, ch, start, length));
  }
}
