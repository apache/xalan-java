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
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xalan.utils.QName;
import org.apache.xalan.trace.SelectionEvent;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:variable.
 * <pre>
 * <!ELEMENT xsl:variable %template;>
 * <!ATTLIST xsl:variable
 *   name %qname; #REQUIRED
 *   select %expr; #IMPLIED
 * >
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#variables">variables in XSLT Specification</a>
 */
public class ElemVariable extends ElemTemplateElement
{

  /**
   * Constructor ElemVariable
   *
   */
  public ElemVariable(){}

  /**
   * The value of the "select" attribute.
   */
  private XPath m_selectPattern;

  /**
   * Set the "select" attribute.
   * If the variable-binding element has a select attribute,
   * then the value of the attribute must be an expression and
   * the value of the variable is the object that results from
   * evaluating the expression. In this case, the content
   * of the variable must be empty.
   *
   * NEEDSDOC @param v
   */
  public void setSelect(XPath v)
  {
    m_selectPattern = v;
  }

  /**
   * Get the "select" attribute.
   * If the variable-binding element has a select attribute,
   * then the value of the attribute must be an expression and
   * the value of the variable is the object that results from
   * evaluating the expression. In this case, the content
   * of the variable must be empty.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public XPath getSelect()
  {
    return m_selectPattern;
  }

  /**
   * The value of the "name" attribute.
   */
  private QName m_qname;

  /**
   * Set the "name" attribute.
   * Both xsl:variable and xsl:param have a required name
   * attribute, which specifies the name of the variable. The
   * value of the name attribute is a QName, which is expanded
   * as described in [2.4 Qualified Names].
   * @see <a href="http://www.w3.org/TR/xslt#qname">qname in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setName(QName v)
  {
    m_qname = v;
  }

  /**
   * Get the "name" attribute.
   * Both xsl:variable and xsl:param have a required name
   * attribute, which specifies the name of the variable. The
   * value of the name attribute is a QName, which is expanded
   * as described in [2.4 Qualified Names].
   * @see <a href="http://www.w3.org/TR/xslt#qname">qname in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public QName getName()
  {
    return m_qname;
  }

  /**
   * Tells if this is a top-level variable or param, or not.
   */
  private boolean m_isTopLevel = false;

  /**
   * Set if this is a top-level variable or param, or not.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC @param v
   */
  public void setIsTopLevel(boolean v)
  {
    m_isTopLevel = v;
  }

  /**
   * Get if this is a top-level variable or param, or not.
   * @see <a href="http://www.w3.org/TR/xslt#top-level-variables">top-level-variables in XSLT Specification</a>
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getIsTopLevel()
  {
    return m_isTopLevel;
  }

  /**
   * Get an integer representation of the element type.
   *
   * @return An integer representation of the element, defined in the
   *     Constants class.
   * @see org.apache.xalan.templates.Constants
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_VARIABLE;
  }

  /**
   * Return the node name.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_VARIABLE_STRING;
  }

  /**
   * Copy constructor.
   *
   * NEEDSDOC @param param
   *
   * @throws SAXException
   */
  public ElemVariable(ElemVariable param) throws SAXException
  {

    m_selectPattern = param.m_selectPattern;
    m_qname = param.m_qname;
    m_isTopLevel = param.m_isTopLevel;

    // m_value = param.m_value;
    // m_varContext = param.m_varContext;
  }

  /**
   * Execute a variable declaration and push it onto the variable stack.
   * @see <a href="http://www.w3.org/TR/xslt#variables">variables in XSLT Specification</a>
   *
   * NEEDSDOC @param transformer
   * NEEDSDOC @param sourceNode
   * NEEDSDOC @param mode
   *
   * @throws SAXException
   */
  public void execute(
          TransformerImpl transformer, Node sourceNode, QName mode)
            throws SAXException
  {

    if (TransformerImpl.S_DEBUG)
      transformer.getTraceManager().fireTraceEvent(sourceNode, mode, this);

    XObject var = getValue(transformer, sourceNode);

    transformer.getXPathContext().getVarStack().pushVariable(m_qname, var);
  }

  /**
   * Get the XObject representation of the variable.
   *
   * NEEDSDOC @param transformer
   * NEEDSDOC @param sourceNode
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws SAXException
   */
  public XObject getValue(TransformerImpl transformer, Node sourceNode)
          throws SAXException
  {

    XObject var;

    if (null != m_selectPattern)
    {
      XPathContext xctxt = transformer.getXPathContext();

      var = m_selectPattern.execute(xctxt, sourceNode, this);
      if(TransformerImpl.S_DEBUG)
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this, 
                                      "select", m_selectPattern, var);
    }
    else if (null == getFirstChild())
    {
      var = XString.EMPTYSTRING;
    }
    else
    {

      // Use result tree fragment
      DocumentFragment df = transformer.transformToRTF(this, sourceNode,
                              null);

      var = new XRTreeFrag(df);
    }

    return var;
  }
}
