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

import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemElement;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemFuncResult;
import org.apache.xalan.templates.ElemFallback;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.templates.ElemMessage;
import org.apache.xalan.templates.ElemComment;
import org.apache.xalan.templates.Constants;
import org.apache.xpath.XPath;
import org.apache.xalan.templates.StylesheetRoot;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xalan.res.XSLTErrorResources;


/**
 * <meta name="usage" content="internal"/>
 * This class processes parse events for an exslt func:function element.
 */
public class ProcessorFunction extends ProcessorTemplateElem
{

  /**
   * Start an ElemFunction. Verify that it is top level and that it has a name attribute with a
   * namespace.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws SAXException
  {
    //System.out.println("ProcessorFunction.startElement()");
    String msg = "";
    if (!(handler.getElemTemplateElement() instanceof StylesheetRoot))
    {
      msg = "func:function element must be top level.";
      handler.error(msg, new SAXException(msg));
    }
    super.startElement(handler, uri, localName, rawName, attributes);
       
    String val = attributes.getValue("name");
    int indexOfColon = val.indexOf(":");
    if (indexOfColon > 0)
    {
      String prefix = val.substring(0, indexOfColon);
      String localVal = val.substring(indexOfColon + 1);
      String ns = handler.getNamespaceSupport().getURI(prefix);
      //if (ns.length() > 0)
      //  System.out.println("fullfuncname " + ns + localVal);
    }
    else
    {
      msg = "xsl:function name must have namespace";
      handler.error(msg, new SAXException(msg));
    }
  }
  
  /**
   * Must include; super doesn't suffice!
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws SAXException
  {
    //System.out.println("ProcessorFunction appendAndPush()" + elem);
    super.appendAndPush(handler, elem);
    //System.out.println("originating node " + handler.getOriginatingNode());
    elem.setDOMBackPointer(handler.getOriginatingNode());
    handler.getStyleshe%t().setTemplate((ElemTemplate) elem);
  }
    
  /**
   * End an ElemFunction, and verify its validity.
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws SAXException
  {
   ElemTemplateElement function = handler.getElemTemplateElement();
   SourceLocator locator = handler.getLocator();

   // Validate
   validate(function, handler);// may throw exception 
            
   super.endElement(handler, uri, localName, rawName);   
  }
  
  /**
   * Validate that the xsl:function contains children in the following order:
   *  xsl:param*, xsl:variable* | xsl:message*, xsl:result. The only required 
   * element is xsl:result, which must be the last child.
   * Note: I assume xsl:comment is allowed anywhere.
   */
  public void validate(ElemTemplateElement elem, StylesheetHandler handler)
    throws SAXException
  {
    String msg = "";
    int result = 0; // Number of xsl:result elements.
    int elemOrder = 0;
    int lastElemOrder = 0;
    boolean invalid = false;
    elem = elem.getFirstChildElem();
    while (elem != null)
    {
      if (elem.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE)
        elemOrder = 0;
      else if (elem.getXSLToken() == Constants.ELEMNAME_VARIABLE || 
               elem.getXSLToken() == Constants.ELEMNAME_MESSAGE)
        elemOrder = 1;
      else if (elem.getXSLToken() == Constants.ELEMNAME_FUNCRESULT) 
      {
        elemOrder = 2;
        if (++result > 1)
        {
          msg = "xsl:function cannot contain more than one xsl:result element.";
          invalid = true;
        }        
      }
      else if (elem.getXSLToken() != Constants.ELEMNAME_COMMENT)
      {       
        msg = "xsl:function contains invalid content.";
        invalid = true;
      }
      if (elemOrder < lastElemOrder)
      {
        msg = "xsl:function elements do not appear in the proper order.";
        invalid = true;
      }      
      elem = elem.getNextSiblingElem();
      lastElemOrder = elemOrder;
    }
    if (result != 1)
    {
      msg = "The last element in an xsl:function must be an xsl:result.";
      invalid = true;
    }
    if (invalid)
      handler.error(msg, new SAXException(msg));    
  }
  
}