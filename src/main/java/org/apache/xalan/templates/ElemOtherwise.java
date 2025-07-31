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
/*
 * $Id$
 */
package org.apache.xalan.templates;


/**
 * Implement xsl:otherwise.
 * <pre>
 * <!ELEMENT xsl:otherwise %template;>
 * <!ATTLIST xsl:otherwise %space-att;>
 * </pre>
 * @see <a href="http://www.w3.org/TR/xslt#section-Conditional-Processing-with-xsl:choose">XXX in XSLT Specification</a>
 * @xsl.usage advanced
 */
public class ElemOtherwise extends ElemTemplateElement
{
   static final long serialVersionUID = 1863944560970181395L;
   
   /**
    * Class field to, represent the value of "xpath-default-namespace" 
    * attribute.
    */
   private String m_xpath_default_namespace = null;
   
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
   
   // Variable to indicate whether, an attribute 'expand-text' 
   // is there on xsl:otherwise instruction.
   private boolean m_expand_text_declared;
   
   /**
    * This class field, represents the value of "expand-text" 
    * attribute.
    */
   private boolean m_expand_text;

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
   
   public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
   }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_OTHERWISE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_OTHERWISE_STRING;
  }
}
