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
package org.apache.xalan.processor;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemParam;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * This class processes parse events for an xsl:param element.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
 */
class ProcessorGlobalParamDecl extends ProcessorTemplateElem
{

  /**
   * Append the current template element to the current
   * template element, and then push it onto the current template
   * element stack.
   *
   * NEEDSDOC @param handler
   * NEEDSDOC @param elem
   *
   * @throws SAXException
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws SAXException
  {

    // Just push, but don't append.
    handler.pushElemTemplateElement(elem);
  }

  /**
   * Receive notification of the end of an element.
   *
   * @param name The element type name.
   * @param attributes The specified or defaulted attributes.
   *
   * NEEDSDOC @param handler
   * NEEDSDOC @param uri
   * NEEDSDOC @param localName
   * NEEDSDOC @param rawName
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endElement
   *
   * @throws SAXException
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws SAXException
  {

    ElemParam v = (ElemParam) handler.getElemTemplateElement();

    handler.getStylesheet().appendChild(v);
    handler.getStylesheet().setParam(v);
    super.endElement(handler, uri, localName, rawName);
  }
}
