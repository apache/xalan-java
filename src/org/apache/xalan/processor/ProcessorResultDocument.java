/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
import org.apache.xalan.res.XSLTErrorResources;

import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.NoSuchMethodException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

import java.util.Vector;

import org.apache.xml.utils.QName;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.*;

/**
 * This class processes parse events for an XSLT template element.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#section-Creating-the-Result-Tree">section-Creating-the-Result-Tree in XSLT Specification</a>
 */
public class ProcessorResultDocument extends ProcessorTemplateElem
{
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {
    super.startElement(handler, uri, localName, rawName, attributes);
    // validate? Cannot be owned by variable, parameter, message, what else?
    if (localName.equals(Constants.ELEMNAME_PRINCIPALRESULTDOCUMENT_STRING))
      validatePrincipal(handler);
  }
  
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws org.xml.sax.SAXException
  {
    super.endElement(handler, uri, localName, rawName);
    // validate??
  }
  
  /**
   * This element must be a child of the Stylesheet.
   */
  private void validatePrincipal(StylesheetHandler handler)
      throws org.xml.sax.SAXException    
  {
    ElemTemplateElement parent = handler.getElemTemplateElement().getParentElem();
    //System.out.println("validatePrincipal parent " + parent);
    if (!(parent instanceof StylesheetRoot))
    {
      String msg = "xsl:principal-result-document must be a top-level element.";
      handler.error(msg, new org.xml.sax.SAXException(msg));
    }
  }

}