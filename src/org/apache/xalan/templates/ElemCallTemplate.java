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

import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xpath.*;
import org.apache.xalan.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xpath.VariableStack;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:call-template.
 * <pre>
 * &amp;!ELEMENT xsl:call-template (xsl:with-param)*>
 * &amp;!ATTLIST xsl:call-template
 *   name %qname; #REQUIRED
 * &amp;
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#named-templates">named-templates in XSLT Specification</a>
 */
public class ElemCallTemplate extends ElemTemplateElement
{
  /**
   * An xsl:call-template element invokes a template by name; 
   * it has a required name attribute that identifies the template to be invoked. 
   */
  public QName m_templateName = null;
  
  /**
   * Set the "name" attribute. 
   * An xsl:call-template element invokes a template by name; 
   * it has a required name attribute that identifies the template to be invoked. 
   */
  public void setName(QName name)
  {
    m_templateName = name;
  }

  /**
   * Get the "name" attribute.
   * An xsl:call-template element invokes a template by name; 
   * it has a required name attribute that identifies the template to be invoked. 
   */
  public QName getName()
  {
    return m_templateName;
  }

  /**
   * The template which is named by QName.
   */
  private ElemTemplateElement m_template = null;
    
  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_CALLTEMPLATE;
  }
  
  /** 
   * Return the node name.
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_CALLTEMPLATE_STRING;
  }

  /**
   * Invoke a named template.
   * @see <a href="http://www.w3.org/TR/xslt#named-templates">named-templates in XSLT Specification</a>
   */
  public void execute(TransformerImpl transformer, 
                      Node sourceNode,
                      QName mode)
    throws SAXException
  {
    if(TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);
    
    // XPathContext xctxt = transformer.getXPathContext();
    if(null == m_template)
    {
      m_template 
        = this.getStylesheetRoot().getTemplateComposed(m_templateName);
    }
    
    if(null != m_template)
    {
      XPathContext xctxt = transformer.getXPathContext();
      VariableStack vars = xctxt.getVarStack();
      int selectStackFrameIndex = vars.getCurrentStackFrameIndex();
      
      vars.pushContextMarker();
      vars.setCurrentStackFrameIndex(selectStackFrameIndex);
      transformer.pushParams(getStylesheet(), 
                             this, sourceNode, mode);
      vars.setCurrentStackFrameIndex(vars.size());
      Locator savedLocator = xctxt.getSAXLocator();
      try
      {        
        xctxt.setSAXLocator(m_template);
        // template.executeChildTemplates(transformer, sourceNode, mode);
        transformer.pushElemTemplateElement(m_template, sourceNode);
        m_template.execute(transformer, sourceNode, mode);
      }
      finally
      {
        transformer.popElemTemplateElement();
        xctxt.setSAXLocator(savedLocator);
        vars.popCurrentContext();
        vars.setCurrentStackFrameIndex(selectStackFrameIndex);
      }
    }
    else
    {
      transformer.getMsgMgr().error(XSLTErrorResources.ER_TEMPLATE_NOT_FOUND, new Object[] {m_templateName}); //"Could not find template named: '"+templateName+"'");
    }
  }
  
  /**
   * Add a child to the child list.
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    int type = ((ElemTemplateElement)newChild).getXSLToken();
    switch(type)
    {
    case Constants.ELEMNAME_WITHPARAM:
      break;
      
    default:
      error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {newChild.getNodeName(), this.getNodeName()}); //"Can not add " +((ElemTemplateElement)newChild).m_elemName +
            //" to " + this.m_elemName);
    }
    return super.appendChild(newChild);
  }

}
