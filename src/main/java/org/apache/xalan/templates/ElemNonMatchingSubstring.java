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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;

/**
 * Implementation of the XSLT 3.0 non-matching-substring instruction.
 *    
 * @author Mukul Gandhi <mukulg@apache.org>
 *   
 * @xsl.usage advanced
*/
public class ElemNonMatchingSubstring extends ElemTemplateElement implements ExpressionOwner {

  private static final long serialVersionUID = -3993288598365914097L;
  
  private String m_strValue = null;

  /**
    * Constructor of this class.
  */
  public ElemNonMatchingSubstring(){}
  
  /**
   * This class field, represents the value of "xpath-default-namespace" 
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
  
  /**
   * Variable to indicate whether, an attribute 'expand-text'
   * is declared on xsl:non-matching-substring instruction.
   */
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

  /**
   * Get a boolean value indicating whether, an "expand-text" 
   * attribute has been declared. 
   */
  public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException {
      super.compose(sroot);
  }
  
  /**
   * This after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException {
      super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken() {
      return Constants.ELEMNAME_NON_MATCHING_SUBSTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName() {
      return Constants.ELEMNAME_NONMATCHINGSUBSTRING_STRING;
  }

  /**
   * Execute the non-matching-substring transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {
      transformSelectedNodes(transformer);
  }

  /**
   * Get template element associated with this.
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch() {
      return this;
  }

  /**
   *
   * @param transformer                non-null reference to the the current transform-time state.
   *
   * @throws TransformerException      Thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException {

      final XPathContext xctxt = transformer.getXPathContext();
      
      String strValueTobeEvaluated = getStrValue();
      
      XPathContext xctxtNew = new XPathContext(false);
      
      xctxtNew.setVarStack(xctxt.getVarStack());
      xctxtNew.setPos(xctxt.getPos());
      xctxtNew.setLast(xctxt.getLast());

      DTMManager dtmMgr = xctxtNew.getDTMManager();      
      DTM docFragDtm = dtmMgr.constructShallowXmlDTMTree(strValueTobeEvaluated, null, null);
      
      int contextNode = docFragDtm.getFirstChild(docFragDtm.getDocument());
      
      for (ElemTemplateElement elem = this.m_firstChild; elem != null;
                                                           elem = elem.m_nextSibling) {
          xctxtNew.pushCurrentNode(contextNode);
          xctxtNew.setSAXLocator(elem);
          transformer.setXPathContext(xctxtNew);          
          transformer.setCurrentElement(elem);
          elem.execute(transformer);
      }
      
      // restore the XPath context to where that was, before this method was called
      transformer.setXPathContext(xctxt);
  }

  /**
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild) {
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes) {
      super.callChildVisitors(visitor, callAttributes);
  }

  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression() {
      return null;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp) {
  	  // NO OP
  }
  
  public String getStrValue() {
      return m_strValue;
  }

  public void setStrValue(String m_strValue) {
      this.m_strValue = m_strValue;
  }
  
}
