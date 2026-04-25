/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;

/**
 * Implementation of XSLT 3.0 xsl:next-match instruction.
 * 
 * @see <a href="https://www.w3.org/TR/xslt-30/#element-next-match">xsl:next-match XSLT 3.0 specification</a>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemNextMatch extends ElemTemplateElement
{

  private static final long serialVersionUID = -8730317226202081634L;
  
  /**
   * Class field, that represents the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;

  /**
   * Class field, that represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;
  
  /**
   * Set the value of "xpath-default-namespace" attribute.
   *
   * @param v   Value of the "xpath-default-namespace" attribute
   */
  public void setXpathDefaultNamespace(String v)
  {
	  m_xpath_default_namespace = v; 
  }

  /**
   * Get the value of "xpath-default-namespace" attribute.
   *  
   * @return		  The value of "xpath-default-namespace" attribute 
   */
  public String getXpathDefaultNamespace() {
	  return m_xpath_default_namespace;
  }

  /**
   * Variable to indicate whether, an attribute 'expand-text'
   * is declared on xsl:next-match instruction.
   */
  private boolean m_expand_text_declared;

  /**
   * Set the value of "expand-text" attribute.
   *
   * @param v   Value of the "expand-text" attribute
   */
  public void setExpandText(boolean v)
  {
	  m_expand_text = v;
	  m_expand_text_declared = true;
  }

  /**
   * Get the value of "expand-text" attribute.
   *  
   * @return		  The value of "expand-text" attribute 
   */
  public boolean getExpandText() {
	  return m_expand_text;
  }

  /**
   * Get a boolean value indicating whether, an "expand-text" 
   * attribute has been declared. 
   */
  public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return Token ID for xsl:next-match element types
   */
  public int getXSLToken()
  {
      return Constants.ELEMNAME_NEXT_MATCH;
  }

  /**
   * Return the node name.
   *
   * @return Element name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_NEXT_MATCH_STRING;
  }

  /**
   * Execute the xsl:next-match transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
     // no op
  }

  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:next-match EMPTY>
   *
   * @param newChild New element to append to this element's children list
   *
   * @return null, xsl:next-match cannot have children 
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
	  super.appendChild(newChild);
    
	  String lineNo = String.valueOf(newChild.getLineNumber());
	  String columnNo = String.valueOf(newChild.getColumnNumber());

	  if (!(newChild instanceof ElemWithParam)) {
	     error(XSLTErrorResources.ER_CANNOT_ADD,
										  new Object[]{ newChild.getNodeName(),
												  this.getNodeName(), lineNo, columnNo });
	  }

	  return null;
  }
}
