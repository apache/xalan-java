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
import org.apache.xpath.XPathContext;

/**
 * Implementation of the XSLT 3.0 xsl:merge-action instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-merge-action
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemMergeAction extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  private static final long serialVersionUID = -2829604543551683903L;
  
  /**
   * The class constructor.
   */
  public ElemMergeAction() {}
  
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
      return Constants.ELEMNAME_MERGE_ACTION;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_MERGE_ACTION_STRING;
  }

  /**
   * Execute the xsl:merge-action transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
	  transformer.pushCurrentTemplateRuleIsNull(true);

	  if (transformer.getDebug()) {
		  transformer.getTraceManager().fireTraceEvent(this);
	  }

	  try {
		  transformSelectedNodes(transformer);
	  }
	  finally {
		  if (transformer.getDebug()) {
			  transformer.getTraceManager().fireTraceEndEvent(this);
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
      return null;
  }

  /**
   * @see ExpressionOwner#setExpression(Expression)
   */
  public void setExpression(Expression exp)
  {
      // NO OP
  }
  
  /**
   * This method performs the actual XSLT transformation logic, on XSL contents of 
   * xsl:merge-action element.
   *
   * @param transformer              non-null reference to the the current transform-time state.
   *
   * @throws TransformerException    thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  private void transformSelectedNodes(TransformerImpl transformer)
                                                               throws TransformerException {
      
	  XPathContext xctxt = transformer.getXPathContext();

	  // TO DO
  
  }

}
