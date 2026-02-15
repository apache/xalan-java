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

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

/**
  * Implementation of XSLT 3.0 instruction xsl:matching-substring.
  *    
  * @author Mukul Gandhi <mukulg@apache.org>
  *   
  * @xsl.usage advanced
 */
public class ElemMatchingSubstring extends ElemTemplateElement implements ExpressionOwner {
  
  private static final long serialVersionUID = -5675647188821169056L;
  
  private String m_strValue = null;
  
  private String m_regex = null;
  
  private String m_flags = null;
  
  static final String STR_VALUE = "STR_VALUE";
  
  static final String REGEX = "REGEX";
  
  static final String REGEX_FLAGS = "REGEX_FLAGS";

  /**
   * Constructor of this class.
   */
  public ElemMatchingSubstring(){}
  
  /**
   * Class field, that represents the value of "xpath-default-namespace" 
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
   * is declared on xsl:matching-substring instruction.
   */
  private boolean m_expand_text_declared;
  
  /**
   * Class field, that represents the value of "expand-text" 
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
   * An XPath expression for 'use-when' attribute. 
   */
  private XPath m_useWhen = null;

  /**
   * Method definition, to set the value of XSL attribute 
   * "use-when".
   * 
   * @param xpath            XPath expression for attribute "use-when"
   */
  public void setUseWhen(XPath xpath)
  {
	  m_useWhen = xpath;  
  }

  /**
   * Method definition, to get the value of XSL attribute 
   * "use-when".
   * 
   * @return			XPath expression for attribute "use-when"
   */
  public XPath getUseWhen()
  {
	  return m_useWhen;
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
   * @return           The token id for this element
   */
  public int getXSLToken() {
     return Constants.ELEMNAME_MATCHING_SUBSTRING;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName() {
     return Constants.ELEMNAME_MATCHINGSUBSTRING_STRING;
  }

  /**
   * Execute the xsl:matching-substring transformation
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException {
      
	  XPathContext xctxt = transformer.getXPathContext();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator(); 

	  final int sourceNode = xctxt.getCurrentNode();
	    
	  if (m_useWhen != null) {
		  boolean result1 = isXPathExpressionStatic(m_useWhen.getExpression());
		  if (result1) {
			  XObject useWhenResult = m_useWhen.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			  if (useWhenResult.bool()) {
				  transformSelectedNodes(transformer);
			  }
		  }
		  else {
			  throw new TransformerException("XPST0008 : XSL variables other than XSLT static variables/parameters, cannot be "
                      																										+ "used within XPath static expression.", srcLocator);
		  }
	  }
	  else {
         transformSelectedNodes(transformer);
	  }
  }

  /**
   * Get template element associated with this
   *
   * @return template element associated with this (itself)
   */
  protected ElemTemplateElement getTemplateMatch() {
      return this;
  }

  /**
   * @param transformer             non-null reference to the the current transform-time state.
   *
   * @throws TransformerException   Thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException {

      final XPathContext xctxt = transformer.getXPathContext();
      
      String strValueTobeEvaluated = getStrValue();
      
      Map<String, String> customDataMap = new HashMap<String, String>();
      customDataMap.put(STR_VALUE, strValueTobeEvaluated);
      customDataMap.put(REGEX, m_regex);
      customDataMap.put(REGEX_FLAGS, m_flags);
      
      XPathContext xctxtNew = new XPathContext(false);
      
      xctxtNew.setVarStack(xctxt.getVarStack());
      xctxtNew.setPos(xctxt.getPos());
      xctxtNew.setLast(xctxt.getLast());

      DTMManager dtmMgr = xctxtNew.getDTMManager();      
      DTM docFragDtm = dtmMgr.getXmlShallowDTMTree(strValueTobeEvaluated, null, null);
      
      int contextNode = docFragDtm.getFirstChild(docFragDtm.getDocument());
       
      for (ElemTemplateElement elem = this.m_firstChild; elem != null; 
                                                    elem = elem.m_nextSibling) {          
          xctxtNew.pushCurrentNode(contextNode);
          xctxtNew.setSAXLocator(elem);
          xctxtNew.setCustomDataMap(customDataMap);
          transformer.setXPathContext(xctxtNew);          
          transformer.setCurrentElement(elem);
          elem.execute(transformer);
      }
      
      // restore the XPath context to where that was, before this method was called
      transformer.setXPathContext(xctxt);
  }

  /**

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

  @Override
  public Expression getExpression() {
      return null;
  }

  @Override
  public void setExpression(Expression exp) {
      // no op 
  }

  public String getStrValue() {
      return m_strValue;
  }

  public void setStrValue(String strValue) {
      this.m_strValue = strValue;
  }

  public String getRegex() {
      return m_regex;
  }

  public void setRegex(String regex) {
      this.m_regex = regex;
  }

  public String getFlags() {
      return m_flags;
  }

  public void setFlags(String flags) {
      this.m_flags = flags;
  }

}
