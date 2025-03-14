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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;

/**
 * Implementation of the XSLT 3.0 xsl:merge instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#merge-instruction
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemMerge extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  
  private static final long serialVersionUID = 5751562599003014575L;
  
  /**
   * A class field vector object, referring to value of xsl:merge-source 
   * elements within xsl:merge element.
   */
  private Vector m_mergeSourceElems = null;
  
  /**
   * A Class field variable referring to value of xsl:merge-action 
   * element within xsl:merge element.
   */
  private ElemMergeAction m_mergeAction = null;
  
  /**
   * The class constructor.
   */
  public ElemMerge() {}
  
  /**
   * Get the count of xsl:merge-source elements associated with this element.
   * 
   * @return The number of xsl:merge-source elements.
   */
  public int getMergeSourceElemCount()
  {
      return (m_mergeSourceElems == null) ? 0 : m_mergeSourceElems.size();
  }

  /**
   * Get a xsl:merge-source element associated with this element.
   *
   * @param i index of xsl:merge-source element to get
   *
   * @return xsl:merge-source element at given index
   */
  public ElemMergeSource getMergeSourceElem(int i)
  {
      return (ElemMergeSource)m_mergeSourceElems.elementAt(i);
  }

  /**
   * Set a xsl:merge-source element associated with this element.
   *
   * @param mergeSourceElem xsl:merge-source element to set
   */
  public void setMergeSourceElem(ElemMergeSource mergeSourceElem)
  {
      if (m_mergeSourceElems == null)
    	  m_mergeSourceElems = new Vector();
    
      m_mergeSourceElems.addElement(mergeSourceElem);
  }
  
  /**
   * Set a xsl:merge-action element associated with this element.
   *
   * @param mergeActionElem xsl:merge-action element to set
   */
  public void setMergeActionElem(ElemMergeAction mergeActionElem)
  {    
      m_mergeAction = mergeActionElem;
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

	  int length = getMergeSourceElemCount();

	  for (int i = 0; i < length; i++)
	  {
		  getMergeSourceElem(i).compose(sroot);
	  }
  }
  
  /**
   * This function is called after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {
	  int length = getMergeSourceElemCount();

	  for (int i = 0; i < length; i++)
	  {
		  getMergeSourceElem(i).endCompose(sroot);
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
      return Constants.ELEMNAME_MERGE;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_MERGE_STRING;
  }

  /**
   * Execute the xsl:merge transformation.
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

	  if (type == Constants.ELEMNAME_MERGE_SOURCE) 
	  {
		  setMergeSourceElem((ElemMergeSource)newChild);

		  return newChild;
	  }
	  else if (type == Constants.ELEMNAME_MERGE_ACTION) 
	  {
		  setMergeActionElem((ElemMergeAction)newChild);
		  
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
	  int length = getMergeSourceElemCount();

	  for (int i = 0; i < length; i++)
	  {
		  getMergeSourceElem(i).callVisitors(visitor);
	  }
	  
	  m_mergeAction.callVisitors(visitor);

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
   * xsl:merge element.
   *
   * @param transformer              non-null reference to the the current transform-time state.
   *
   * @throws TransformerException    thrown in a variety of circumstances.
   * 
   * @xsl.usage advanced
   */
  private void transformSelectedNodes(TransformerImpl transformer)
                                                               throws TransformerException {
      
	  /**
	   * Within this method definition, we determine finally the
	   * typed value of xsl:merge-action instruction and serialize 
	   * that to XSL transform's output. 
	   */

	  XPathContext xctxt = transformer.getXPathContext();	  
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  Map<Object, ResultSequence> xslMergeResultMap = new HashMap<Object, ResultSequence>();

	  int mergeSourceCount = getMergeSourceElemCount();

	  List<String> mergeSrcNameList = new ArrayList<String>();
	  List<Boolean> sortBeforeMergeList = new ArrayList<Boolean>();
	  
	  Boolean isSortBeforeMergeRequired = null;
	  
	  for (int idx = 0; idx < mergeSourceCount; idx++) {
		  ElemMergeSource mergeSourceElem = getMergeSourceElem(idx);
		  
		  String mergeSrcName = mergeSourceElem.getName();
		  if (mergeSrcNameList.contains(mergeSrcName)) {
			  throw new TransformerException("XTSE1505 : Within an xsl:merge element, there are more than "
			  		                                + "one xsl:merge-source elements with name '" + mergeSrcName + "'.", srcLocator);  
		  }
		  else {
			  mergeSrcNameList.add(mergeSrcName); 
		  }
		  
		  isSortBeforeMergeRequired = mergeSourceElem.getSortBeforeMerge();
		  if ((sortBeforeMergeList.size() > 0) && !sortBeforeMergeList.contains(isSortBeforeMergeRequired)) {
			  throw new TransformerException("XTSE1505 : Within an xsl:merge element, all xsl:merge-source elements must "
			  		                                + "have same boolean value (yes/no) for attribute 'sort-before-merge'.", srcLocator); 
		  }
		  else {			   
			  sortBeforeMergeList.add(isSortBeforeMergeRequired);
		  }
		  
		  Expression mergeSourceSelectExpr = mergeSourceElem.getSelect();
		  
		  int contextNode = xctxt.getContextNode();
		  
		  XObject mergeSourceValue = mergeSourceSelectExpr.execute(xctxt);

		  /**
		   * We consider only one merge key at the moment (otherwise, it'll be 
		   * composite merge key). We also assume just now that, merge key 
		   * value is specified by xsl:merge-key's 'select' attribute and 
		   * not by xsl:merge-key element's contained sequence constructor 
		   * both of which are mutually exclusive.
		   */

		  int mergeKeyCount = mergeSourceElem.getMergeKeyElemCount();
		  ElemMergeKey mergeKey = mergeSourceElem.getMergeKeyElem(0);		 
		  Expression mergeKeySelectExpr = mergeKey.getSelect();

		  if (mergeSourceValue instanceof ResultSequence) {
			  ResultSequence rSeq = (ResultSequence)mergeSourceValue;
			  for (int idx2 = 0; idx2 < rSeq.size(); idx2++) {
				  XObject xObj = rSeq.item(idx2);			   
				  XObject mergeKeyValue = null;
				  if (mergeKeySelectExpr instanceof SelfIteratorNoPredicate) {			       
					  mergeKeyValue = xObj; 
				  }
				  else {
					  xctxt.setXPath3ContextItem(xObj);
					  mergeKeyValue = mergeKeySelectExpr.execute(xctxt); 
				  }

				  Object mergeKeyNormalizedValue = getXPathEvaluationRawResult(mergeKeyValue);

				  if (xslMergeResultMap.get(mergeKeyNormalizedValue) != null) {
					  ResultSequence mergeGroup = xslMergeResultMap.get(mergeKeyNormalizedValue);
					  mergeGroup.add(mergeKeyValue);
				  } else {
					  ResultSequence mergeGroup = new ResultSequence();
					  mergeGroup.add(mergeKeyValue);
					  xslMergeResultMap.put(mergeKeyNormalizedValue, mergeGroup);
				  } 
			  }
		  }
		  else if (mergeSourceValue instanceof XNodeSet) {
			  // TO DO 
		  }		  
	  }
	  
	  if (isSortBeforeMergeRequired.booleanValue()) {		 
		 // Sort the overall xsl:merge result according to 
		 // available merge key values.
		  
		 List<Object> mergeKeyList = new ArrayList<Object>();
		 
		 Set<Object> mapKeySet = xslMergeResultMap.keySet();	  
		 Iterator<Object> mapKeyIter = mapKeySet.iterator();
		 while (mapKeyIter.hasNext()) {
		    Object currentMergeKey = mapKeyIter.next();
		    mergeKeyList.add(currentMergeKey);
		 }
		 
		 mergeKeyList.sort(null);
		 
		 for (int idx = 0; idx < mergeKeyList.size(); idx++) {
			Object currentMergeKey = mergeKeyList.get(idx);
			ResultSequence currentMergeGroup = xslMergeResultMap.get(currentMergeKey);
			for (ElemTemplateElement templateElem = m_mergeAction.m_firstChild; templateElem != null;
																			 templateElem = templateElem.m_nextSibling) {
				templateElem.setMergeKey(currentMergeKey);
				templateElem.setMergeGroup(currentMergeGroup);
				xctxt.setSAXLocator(templateElem);
				transformer.setCurrentElement(templateElem);                   
				templateElem.execute(transformer);
			}
		 }
	  }
	  else {
		  Set<Object> mapKeySet = xslMergeResultMap.keySet();	  
		  Iterator<Object> mapKeyIter = mapKeySet.iterator();
		  while (mapKeyIter.hasNext()) {
			  Object currentMergeKey = mapKeyIter.next();
			  ResultSequence currentMergeGroup = xslMergeResultMap.get(currentMergeKey);
			  for (ElemTemplateElement templateElem = m_mergeAction.m_firstChild; templateElem != null;
					  														   templateElem = templateElem.m_nextSibling) {
				  templateElem.setMergeKey(currentMergeKey);
				  templateElem.setMergeGroup(currentMergeGroup);
				  xctxt.setSAXLocator(templateElem);
				  transformer.setCurrentElement(templateElem);                   
				  templateElem.execute(transformer);
			  }
		  }
	  }
  }
  
  /**
    * Method definition, to convert XSL xsl:merge-source merge key's initially 
    * computed value, into a normalized typed value of type java.lang.Object.
   */
  private Object getXPathEvaluationRawResult(XObject xpathEvalResult) {
      
	  Object xpathRawResult = null;
      
      if (xpathEvalResult instanceof XString) {
          xpathRawResult = xpathEvalResult.str();    
      }
      else if (xpathEvalResult instanceof XSQName) {
    	  XSQName qName = (XSQName)xpathEvalResult;
    	  String localPart = qName.getLocalPart();
    	  String namespaceUri = qName.getNamespaceUri();
    	  xpathRawResult = localPart + ((namespaceUri == null) ? "" : ":" + namespaceUri); 
      }
      else if (xpathEvalResult instanceof XSString) {
          xpathRawResult = ((XSString)xpathEvalResult).stringValue();  
      }
      else if (xpathEvalResult instanceof XNumber) {
          xpathRawResult = Double.valueOf(((XNumber)xpathEvalResult).num());  
      }
      else if (xpathEvalResult instanceof XSNumericType) {
          String strVal = ((XSNumericType)xpathEvalResult).stringValue();
          xpathRawResult = Double.valueOf(strVal);
      }
      else if (xpathEvalResult instanceof XBoolean) {
          xpathRawResult =  Boolean.valueOf(((XBoolean)xpathEvalResult).bool());     
      }
      else if (xpathEvalResult instanceof XSBoolean) {
          xpathRawResult = Boolean.valueOf(((XSBoolean)xpathEvalResult).value());
      }
      else if ((xpathEvalResult instanceof XSDate) || (xpathEvalResult instanceof XSDateTime) || 
    		                                          (xpathEvalResult instanceof XSTime)) {
          xpathRawResult = xpathEvalResult;
      }
      else if (xpathEvalResult instanceof XSAnyURI) {
    	  xpathRawResult = xpathEvalResult; 
      }
      else {
          // Any other data type for merge key, is treated as string
          xpathRawResult = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);  
      }
      
      return xpathRawResult;      
  }

}
