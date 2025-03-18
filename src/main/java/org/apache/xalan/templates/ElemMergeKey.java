/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;

/**
 * Implementation of the XSLT 3.0 xsl:merge-key instruction.
 * 
 * One or more xsl:merge-key elements appear as child elements
 * of xsl:merge-source elements. 
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-merge-key
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemMergeKey extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  
  private static final long serialVersionUID = -2688842779485923042L;
  
  /**
   * The "select" expression.
   * 
   * The "select" attribute and the contained sequence constructor 
   * are mutually exclusive.
   */
  private Expression m_selectExpression = null;
  
  /**
   * The class constructor.
   */
  public ElemMergeKey() {}
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);
  }
  
  /**
   * This function is called after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {        
	  super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
      return Constants.ELEMNAME_MERGE_KEY;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_MERGE_KEY_STRING;
  }

  /**
   * Execute the xsl:merge-key transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
	  transformer.pushCurrentTemplateRuleIsNull(true);

	  if (transformer.getDebug()) {
		  transformer.getTraceManager().emitTraceEvent(this);
	  }

	  try {
		  transformSelectedNodes(transformer);
	  }
	  finally {
		  if (transformer.getDebug()) {
			  transformer.getTraceManager().emitTraceEndEvent(this);
		  }

		  transformer.popCurrentTemplateRuleIsNull();
	  }
  }
  
  /**
   * Add a child to the child list.
   * 
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {        
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {      	      		    
      super.callChildVisitors(visitor, callAttributes);
  }
  
  /**
   * @see ExpressionOwner#getExpression()
   */
  public Expression getExpression()
  {
      return m_selectExpression;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
      exp.exprSetParent(this);
      m_selectExpression = exp;
  }
  
  /**
   * This method performs the actual XSLT transformation logic, on XSL contents of 
   * xsl:merge-key element.
   *
   * @param transformer              non-null reference to the the current transform-time state.
   *
   * @throws TransformerException    thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  private void transformSelectedNodes(TransformerImpl transformer)
                                                               throws TransformerException {
	  // TO DO
  }

  /**
   * Set the "select" attribute.
   *
   * @param xpath The XPath expression for the "select" attribute.
   */
  public void setSelect(XPath xpath)
  {
      m_selectExpression = xpath.getExpression();  
  }

  /**
   * Get the "select" attribute.
   *
   * @return The XPath expression for the "select" attribute.
   */
  public Expression getSelect()
  {
      return m_selectExpression;
  }

}
