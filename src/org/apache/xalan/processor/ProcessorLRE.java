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
package org.apache.xalan.processor;

import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xpath.XPath;
import org.apache.xalan.templates.StylesheetRoot;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

public class ProcessorLRE extends ProcessorTemplateElem
{
  /**
   * Receive notification of the start of an element.
   *
   * @param name The element type name.
   * @param attributes The specified or defaulted attributes.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startElement
   */
  public void startElement (StylesheetHandler handler, 
                            String uri, String localName,
                            String rawName, Attributes attributes)
    throws SAXException
  {
    ElemTemplateElement p = handler.getElemTemplateElement();
    if(null == p)
    {
      // Literal Result Template as stylesheet.
      Stylesheet stylesheet = new StylesheetRoot();
      // stylesheet.setDOMBackPointer(handler.getOriginatingNode());
      stylesheet.setLocaterInfo(new org.xml.sax.helpers.LocatorImpl());
      stylesheet.setPrefixes(handler.getNamespaceSupport());

      handler.pushStylesheet(stylesheet);
      
      setPropertiesFromAttributes(handler, "stylesheet", attributes, stylesheet);

      handler.pushElemTemplateElement(stylesheet);
      
      ElemTemplate template = new ElemTemplate();
      appendAndPush(handler, template);
      
      XPath rootMatch = new XPath("/", stylesheet, stylesheet, XPath.MATCH);
      template.setMatch(rootMatch); 
      // template.setDOMBackPointer(handler.getOriginatingNode());
      stylesheet.setTemplate(template);
      p = handler.getElemTemplateElement();
    }

    XSLTElementDef def = getElemDef();
    Class classObject = def.getClassObject();
    
    boolean isExtension = false;
    while(null != p)
    {
      // System.out.println("Checking: "+p);
      if(p instanceof ElemLiteralResult)
      {
        ElemLiteralResult parentElem = (ElemLiteralResult)p;
        isExtension = parentElem.containsExtensionElementURI(uri);
      }
      else if(p instanceof Stylesheet)
      {
        Stylesheet parentElem = (Stylesheet)p;
        isExtension = parentElem.containsExtensionElementURI(uri);
      }
      if(isExtension)
        break;
      p = p.getParentElem();
    }
    
    ElemLiteralResult elem = null;
    try
    {
      if(isExtension)
      {
        // System.out.println("Creating extension(1): "+uri);
        elem = new ElemExtensionCall();
      }
      else
        elem = (ElemLiteralResult)classObject.newInstance();
      elem.setDOMBackPointer(handler.getOriginatingNode());
      elem.setLocaterInfo(handler.getLocator());
      elem.setPrefixes(handler.getNamespaceSupport());
      elem.setNamespace(uri);
      elem.setLocalName(localName);
      elem.setRawName(rawName);
    }
    catch(InstantiationException ie)
    {
      handler.error("Failed creating ElemLiteralResult instance!", ie);
    }
    catch(IllegalAccessException iae)
    {
      handler.error("Failed creating ElemLiteralResult instance!", iae);
    }
    
    setPropertiesFromAttributes(handler, rawName, attributes, elem);
    
    // bit of a hack here...
    if(!isExtension)
    {
      isExtension = elem.containsExtensionElementURI(uri);
      if(isExtension)
      {
        // System.out.println("Creating extension(2): "+uri);
        elem = new ElemExtensionCall();
        elem.setLocaterInfo(handler.getLocator());
        elem.setPrefixes(handler.getNamespaceSupport());
        elem.setNamespace(uri);
        elem.setLocalName(localName);
        elem.setRawName(rawName);
        setPropertiesFromAttributes(handler, rawName, attributes, elem);
      }
    }
    
    appendAndPush(handler, elem);
  }

}
