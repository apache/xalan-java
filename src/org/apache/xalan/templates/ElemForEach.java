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
import org.apache.xalan.xpath.*;
import java.util.Vector;
import org.apache.xalan.utils.QName;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:for-each.
 * <pre>
 * <!ELEMENT xsl:for-each
 *  (#PCDATA
 *   %instructions;
 *   %result-elements;
 *   | xsl:sort)*
 * >
 * 
 * <!ATTLIST xsl:for-each
 *   select %expr; #REQUIRED
 *   %space-att;
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#for-each">for-each in XSLT Specification</a>
 */
public class ElemForEach extends ElemTemplateElement
{
  /**
   * Construct a element representing xsl:for-each.
   */
  public ElemForEach()
  {
  }

  /**
   * The "select" expression.
   */
  private XPath m_selectExpression = null;
  
  /**
   * Set the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
    m_selectExpression = xpath;
  }

  /**
   * Set the "select" attribute.
   */
  public XPath getSelect()
  {
    return m_selectExpression;
  }

  private Vector m_sortElems = null;
  
  /**
   * Get the count xsl:sort elements associated with this element.
   * @return The number of xsl:sort elements.
   */
  public int getSortElemCount()
  {
    return (m_sortElems == null) ? 0 : m_sortElems.size();
  }
  
  /**
   * Get a xsl:sort element associated with this element.
   */
  public ElemSort getSortElem(int i)
  {
    return (ElemSort)m_sortElems.elementAt(i);
  }
  
  /**
   * Set a xsl:sort element associated with this element.
   */
  public void setSortElem(ElemSort sortElem)
  {
    if(null == m_sortElems)
      m_sortElems = new Vector();
    m_sortElems.addElement(sortElem);
  }

  
  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_FOREACH;
  }
  
  /** 
   * Return the node name.
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_FOREACH_STRING;
  }

  public void execute(TransformerImpl transformer, 
                      Node sourceNode,
                      QName mode)
    throws SAXException
  {    
    transformer.pushCurrentTemplateRuleIsNull(true);

    try
    {
      if(TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);
      if(null != sourceNode)
      {      
        transformer.transformSelectedNodes(getStylesheetComposed(), 
                                              this,
                                              this,
                                              sourceNode, mode,
                                              m_selectExpression, 
                                              transformer.getXPathContext().getVarStack().getCurrentStackFrameIndex());
      }
      else // if(null == sourceNode)
      {
        transformer.getMsgMgr().error(this, sourceNode, XSLTErrorResources.ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES);
        //"sourceNode is null in handleApplyTemplatesInstruction!");
      }
    }

    finally
    {
      transformer.popCurrentTemplateRuleIsNull();
    }
  }
  
  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    int type = ((ElemTemplateElement)newChild).getXSLToken();
    if(Constants.ELEMNAME_SORT == type)
    {
      setSortElem((ElemSort)newChild);
    }

    return super.appendChild(newChild);
  }

}
