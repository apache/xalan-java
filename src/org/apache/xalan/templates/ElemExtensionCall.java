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
package org.apache.xalan.templates;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.StringTokenizer;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.NameSpace;
import org.apache.xalan.utils.StringToStringTable;
import org.apache.xpath.XPathContext;
import org.apache.xalan.utils.MutableAttrListImpl;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.XPathContext;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.extensions.ExtensionNSHandler;
import org.apache.xalan.extensions.ExtensionsTable;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="advanced"/>
 * Implement an extension element.
 * @see <a href="http://www.w3.org/TR/xslt#extension-element">extension-element in XSLT Specification</a>
 */
public class ElemExtensionCall extends ElemLiteralResult
{
  // ExtensionNSHandler nsh;
  String m_extns;
  // String m_extHandlerLookup;
  transient boolean isAvailable = false;
  String m_lang;
  String m_srcURL;
  String m_scriptSrc;
  Class m_javaClass = null;
  ElemExtensionDecl m_decl = null;

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_EXTENSIONCALL;
  }
  
  /** 
   * Return the node name.
   */
  // public String getNodeName()
  // {
    // TODO: Need prefix.
    // return localPart;
  // }

  /**
   * Tell if this extension element is available for execution.
   */
  public boolean elementIsAvailable()
  {
    return isAvailable;
  }
  
  /**
   * This function is called after everything else has been 
   * recomposed, and allows the template to set remaining 
   * values that may be based on some other property that 
   * depends on recomposition.
   */
  public void compose()
  {
    m_extns = this.getNamespace();
    
    StylesheetRoot stylesheet = this.getStylesheetRoot();
    
    m_decl = getElemExtensionDecl(stylesheet, m_extns);
    
    if(null != m_decl)
    {
      for(ElemTemplateElement child = m_decl.getFirstChildElem();
          child != null; child = child.getNextSiblingElem())
      {
        if(Constants.ELEMNAME_EXTENSIONSCRIPT == child.getXSLToken())
        {
          ElemExtensionScript sdecl = (ElemExtensionScript)child;
          m_lang = sdecl.getLang();
          m_srcURL = sdecl.getSrc();
          ElemTemplateElement childOfSDecl = sdecl.getFirstChildElem();
          if(null != childOfSDecl)
          {
            if(Constants.ELEMNAME_TEXTLITERALRESULT == childOfSDecl.getXSLToken())
            {
              ElemTextLiteral tl = (ElemTextLiteral)childOfSDecl;
              char[] chars = tl.getChars();
              m_scriptSrc = new String(chars);
            }
          }
          break;
        }
      }
      
    }
    else
    {
      // stylesheet.error(xxx);
    }
  }
  
  private ElemExtensionDecl getElemExtensionDecl(StylesheetRoot stylesheet, 
                                                 String namespace)
  {
    ElemExtensionDecl decl = null;
        
    int n = stylesheet.getGlobalImportCount();
    for(int i = 0; i < n; i++)
    {
      Stylesheet imported = stylesheet.getGlobalImport(i);
      for(ElemTemplateElement child = imported.getFirstChildElem();
          child != null; child = child.getNextSiblingElem())
      {
        if(Constants.ELEMNAME_EXTENSIONDECL == child.getXSLToken())
        {
          decl = (ElemExtensionDecl)child;
          String prefix = decl.getPrefix();
          String declNamespace = child.getNamespaceForPrefix(prefix);
          if(namespace.equals(declNamespace))
          {
            return decl;
          }
        }
      }
    }
    return decl;
  }

  /**
   * Execute an extension.
   */
  public void execute(TransformerImpl transformer, 
                      Node sourceNode,
                      QName mode)
    throws SAXException
  {
    try
    {
      transformer.getResultTreeHandler().flushPending();
      
      XPathContext liaison = ((XPathContext)transformer.getXPathContext());
      ExtensionsTable etable = liaison.getExtensionsTable();
      ExtensionNSHandler nsh = etable.get(m_extns);

      nsh.processElement (this.getLocalName(), this,
                          transformer, 
                          getStylesheet(),
                          sourceNode.getOwnerDocument(), 
                          sourceNode, mode, m_javaClass, this);
    }
    catch(Exception e)
    {
      // System.out.println(e);
      // e.printStackTrace();
      String msg = e.getMessage();
      if(null != msg)
      {
        if(msg.startsWith("Stopping after fatal error:"))
        {
          msg = msg.substring("Stopping after fatal error:".length());
        }
        transformer.getMsgMgr().message(XSLMessages.createMessage(XSLTErrorResources.ER_CALL_TO_EXT_FAILED, new Object[]{msg}), false); //"Call to extension element failed: "+msg);
        // e.printStackTrace();
        // System.exit(-1);
      }
      // transformer.message(msg);
      isAvailable = false; 
      for (ElemTemplateElement child = m_firstChild; child != null; child = child.m_nextSibling) 
      {
        if(child.getXSLToken() == Constants.ELEMNAME_FALLBACK)
        {
          try
          {
            transformer.pushElemTemplateElement(child, sourceNode);
            child.execute(transformer, sourceNode, mode);
          }
          finally
          {
            transformer.popElemTemplateElement();
          }
        }
      }
    }
  }
  
  /**
   * Return the raw value of the attribute.
   */
  public String getAttribute(String rawName)
  {
    AVT avt = getLiteralResultAttribute(rawName);
    if((null != avt) && avt.getRawName().equals(rawName))
    {
      return avt.getSimpleString();        
    }
    return null;    
  }

  /**
   * Return the value of the attribute interpreted as an Attribute 
   * Value Template (in other words, you can use curly expressions 
   * such as href="http://{website}".
   */
  public String getAttribute(String rawName, Node sourceNode, TransformerImpl transformer)
    throws SAXException
  {
    AVT avt = getLiteralResultAttribute(rawName);
    if((null != avt) && avt.getRawName().equals(rawName))
    {
      XPathContext xctxt = transformer.getXPathContext();
      return avt.evaluate(xctxt, sourceNode, this,
                          new StringBuffer());        
    }
    return null;  
  }

}
