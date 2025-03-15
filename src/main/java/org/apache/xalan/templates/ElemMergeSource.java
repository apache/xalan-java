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

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;

/**
 * Implementation of the XSLT 3.0 xsl:merge-source instruction.
 * 
 * One or more xsl:merge-source elements appear as child elements
 * of xsl:merge elements. 
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-merge-source
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemMergeSource extends ElemTemplateElement 
                                                implements ExpressionOwner
{

  private static final long serialVersionUID = 195818366260075984L;
  
  /**
   * The "name" attribute value.
   */
  private String m_name = null;
  
  /**
   * The "select" expression.
   */
  private Expression m_selectExpression = null;
  
  /**
   * The "sort-before-merge" attribute value;
   */
  private boolean m_sortBeforeMerge;

  /**
   * Vector containing the xsl:merge-key elements associated with this element.
   */
  private Vector m_mergeKeyElems = null;
  
  /**
   * The class constructor.
   */
  public ElemMergeSource() {}
  
  /**
   * Get the count of xsl:merge-key elements associated with this element.
   * 
   * @return The number of xsl:merge-key elements.
   */
  public int getMergeKeyElemCount()
  {
      return (m_mergeKeyElems == null) ? 0 : m_mergeKeyElems.size();
  }

  /**
   * Get a xsl:merge-key element associated with this element.
   *
   * @param i index of xsl:merge-key element to get
   *
   * @return xsl:merge-key element at given index
   */
  public ElemMergeKey getMergeKeyElem(int i)
  {
      return (ElemMergeKey)m_mergeKeyElems.elementAt(i);
  }

  /**
   * Set a xsl:merge-key element associated with this element.
   *
   * @param mergeKeyElem xsl:merge-key element to set
   */
  public void setMergeKeyElem(ElemMergeKey mergeKeyElem)
  {
      if (m_mergeKeyElems == null)
    	  m_mergeKeyElems = new Vector();
      
      mergeKeyElem.setParentElem(this);    
      m_mergeKeyElems.addElement(mergeKeyElem);
  }
  
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

	  int length = getMergeKeyElemCount();

	  for (int i = 0; i < length; i++)
	  {
		  getMergeKeyElem(i).compose(sroot);
	  }

	  java.util.Vector vnames = sroot.getComposeState().getVariableNames();

	  if (m_selectExpression != null) {
		  m_selectExpression.fixupVariables(vnames, sroot.getComposeState().getGlobalsSize());
	  } else {
		  m_selectExpression = getStylesheetRoot().m_selectDefault.getExpression();
	  }
  }
  
  /**
   * This function is called after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	  int length = getMergeKeyElemCount();

	  for (int i = 0; i < length; i++)
	  {
		  getMergeKeyElem(i).endCompose(sroot);
	  }

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
      return Constants.ELEMNAME_MERGE_SOURCE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_MERGE_SOURCE_STRING;
  }

  /**
   * Execute the xsl:merge-source transformation.
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
	  int type = ((ElemTemplateElement)newChild).getXSLToken();

	  if (type == Constants.ELEMNAME_MERGE_KEY)
	  {
		  setMergeKeyElem((ElemMergeKey)newChild);

		  return newChild;
	  }
	  else {
		  return super.appendChild(newChild);
	  }
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {      	      		
	  int length = getMergeKeyElemCount();

	  for (int i = 0; i < length; i++)
	  {
		  getMergeKeyElem(i).callVisitors(visitor);
	  }

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
   * xsl:merge-source element.
   *
   * @param transformer              non-null reference to the the current transform-time state.
   *
   * @throws TransformerException    thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  private void transformSelectedNodes(TransformerImpl transformer)
                                                               throws TransformerException {
	  // NO OP
  }
  
  /**
   * Set the "name" attribute.
   */
  public void setName(String name) {
	  this.m_name = name;
  }
  
  /**
   * Get the "name" attribute.
   */
  public String getName() {
	  return m_name;
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
  
  /**
   * Set the "sort-before-merge" attribute.
   */
  public void setSortBeforeMerge(boolean sortBeforeMerge) {
	  this.m_sortBeforeMerge = sortBeforeMerge;
  }
  
  /**
   * Get the "sort-before-merge" attribute.
   */
  public boolean getSortBeforeMerge() {
	  return m_sortBeforeMerge;
  }

}
