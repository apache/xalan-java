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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.StackGuard;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.composite.XPathSequenceConstructor;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XRTreeFrag;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XSLT 3.0 xsl:apply-templates element.
 * 
 * @xsl.usage advanced
 */
public class ElemApplyTemplates extends ElemCallTemplate
{
  
  static final long serialVersionUID = 2903125371542621004L;

  /**
   * xsl:template's mode value.
   */
  private QName m_mode = null;
  
  /**
   * Tells if this belongs to a default template,
   * in which case it will act different with
   * regard to processing modes.
   * 
   */
  private boolean m_isDefaultTemplate = false;
  
  /**
   * This class field, represents the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;
  
  /**
   * This class field, represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;

  /**
   * Set the mode attribute for this element.
   *
   * @param mode reference, which may be null.
   */
  public void setMode(QName mode)
  {
	  m_mode = mode;
  }

  /**
   * Get the mode attribute for this element.
   *
   * @return The mode attribute for this element
   */
  public QName getMode()
  {
	  return m_mode;
  }

  /**
   * Set if this belongs to a default template,
   * in which case it will act different with
   * regard to processing modes.
   *
   * @param b boolean value to set.
   */
  public void setIsDefaultTemplate(boolean b)
  {
	  m_isDefaultTemplate = b;
  }
  
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
  // is there on xsl:apply-templates instruction.
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
  
  public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return Token ID for this element types
   */
  public int getXSLToken()
  {
	  return Constants.ELEMNAME_APPLY_TEMPLATES;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);
  }

  /**
   * Return the node name.
   *
   * @return Element name
   */
  public String getNodeName()
  {
	  return Constants.ELEMNAME_APPLY_TEMPLATES_STRING;
  }

  /**
   * Apply the context node to XSL stylesheet's matching templates.
   *
   * @param transformer non-null reference to the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {

	  transformer.pushCurrentTemplateRuleIsNull(false);

	  boolean pushMode = false;

	  try
	  {
		  QName mode = transformer.getMode();

		  if (!m_isDefaultTemplate)
		  {
			  if (((null == mode) && (null != m_mode))
					  								|| ((null != mode) &&!mode.equals(m_mode)))
			  {
				  pushMode = true;
				  
				  transformer.pushMode(m_mode);
			  }
		  }
		  if (transformer.getDebug())
			  transformer.getTraceManager().emitTraceEvent(this);

		  transformSelectedNodes(transformer);
	  }
	  finally
	  {
		  if (transformer.getDebug())
			  transformer.getTraceManager().emitTraceEndEvent(this);

		  if (pushMode)
			  transformer.popMode();

		  transformer.popCurrentTemplateRuleIsNull();
	  }
  }

  
  /**
   * Perform a query if needed, and call transformNode for each child.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException Thrown in a variety of circumstances.
   * @xsl.usage advanced
   */
  public void transformSelectedNodes(TransformerImpl transformer) throws TransformerException
  {

	  final XPathContext xctxt = transformer.getXPathContext();
	  final int sourceNode = xctxt.getCurrentNode();

	  DTMCursorIterator sourceNodes = null;

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  XObject varEvalResult = null;    
	  ResultSequence resultSeq = null;
	  QName xslTemplateInvokeMode = getMode();
	  
	  if ((m_selectExpression != null) && (m_xpath_default_namespace != null)) {    		
		  m_xpath = new XPath(m_xpath.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	  m_selectExpression = m_xpath.getExpression(); 
      }

	  if (m_selectExpression instanceof XPathSequenceConstructor) {
		  resultSeq = (ResultSequence)(((XPathSequenceConstructor)m_selectExpression).execute(xctxt));

		  if (isAllSeqItemsXdmAtomicValues(resultSeq)) {    		
			  executeXslTransformAtomicValueSeq(transformer, xctxt, resultSeq, xslTemplateInvokeMode);

			  return;
		  }
		  else {
			  XMLNodeCursorImpl nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(resultSeq, xctxt);
			  if (nodeSet != null) {
				  sourceNodes = nodeSet.asIterator(xctxt, sourceNode);   
			  }
			  else {
				  return;
			  }
		  }
	  }    
	  else if (m_selectExpression instanceof Variable) {
		  varEvalResult = ((Variable)m_selectExpression).execute(xctxt);

		  if (varEvalResult instanceof ResultSequence) {
			  resultSeq = (ResultSequence)varEvalResult;

			  if (isAllSeqItemsXdmAtomicValues(resultSeq)) {
				  executeXslTransformAtomicValueSeq(transformer, xctxt, resultSeq, xslTemplateInvokeMode);

				  return;
			  }
			  else {
				  XMLNodeCursorImpl nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(resultSeq, xctxt);
				  if (nodeSet != null) {
					  sourceNodes = nodeSet.asIterator(xctxt, sourceNode);   
				  }
				  else {
					  return;
				  }
			  }
		  }
		  else if ((varEvalResult instanceof XSAnyAtomicType) || (varEvalResult instanceof XString) || 
				                                                 (varEvalResult instanceof XBoolean) || 
				                                                 (varEvalResult instanceof XNumber)) {   	       	       	   
			  executeXslTransformAtomicValue(transformer, xctxt, varEvalResult, xslTemplateInvokeMode);

			  return; 
		  }
		  else if (varEvalResult instanceof XMLNodeCursorImpl) {
			  sourceNodes = ((XMLNodeCursorImpl)varEvalResult).asIterator(xctxt, sourceNode);
		  }       
		  else {
			  throw new TransformerException("XTTE0505 : xsl:apply-templates 'select' expression evaluation "
					  														               + "resulted in a value that is not "
					  														               + "supported to be processed.", srcLocator);
		  }
	  }
	  else {
		  varEvalResult = m_selectExpression.execute(xctxt);

		  if (varEvalResult instanceof ResultSequence) {
			  resultSeq = (ResultSequence)varEvalResult;

			  if (isAllSeqItemsXdmAtomicValues(resultSeq)) {
				  executeXslTransformAtomicValueSeq(transformer, xctxt, resultSeq, xslTemplateInvokeMode);

				  return;
			  }
			  else {
				  XMLNodeCursorImpl nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence(resultSeq, xctxt);    		   
				  if (nodeSet != null) {
					  sourceNodes = nodeSet.asIterator(xctxt, sourceNode);   
				  }
				  else {
					  return;
				  }
			  } 
		  }
		  else if (varEvalResult instanceof XMLNodeCursorImpl) {
			  sourceNodes = ((XMLNodeCursorImpl)varEvalResult).asIterator(xctxt, sourceNode);
		  }
		  else if (isXdmItemAtomicValue(varEvalResult)) {    	   
			  executeXslTransformAtomicValue(transformer, xctxt, varEvalResult, xslTemplateInvokeMode);

			  return;
		  }
		  else {
			  throw new TransformerException("XTTE0505 : xsl:apply-templates 'select' expression evaluation "
																						  + "resulted in a value that is not "
																						  + "supported to be processed.", srcLocator);
		  }
	  }

	  VariableStack vars = xctxt.getVarStack();
	  int nParams = getParamElemCount();
	  int thisframe = vars.getStackFrame();
	  StackGuard guard = transformer.getStackGuard();
	  boolean check = (guard.getRecursionLimit() > -1) ? true : false;

	  boolean pushContextNodeListFlag = false;

	  try
	  {
		  xctxt.pushCurrentNode(DTM.NULL);
		  xctxt.pushCurrentExpressionNode(DTM.NULL);
		  xctxt.pushSAXLocatorNull();
		  transformer.pushElemTemplateElement(null);
		  final Vector keys = (m_sortElems == null)
				  ? null
						  : transformer.processSortKeys(this, sourceNode);

		  // Sort if we need to
		  if (null != keys)
			  sourceNodes = sortNodes(xctxt, keys, sourceNodes);

		  if (transformer.getDebug())
		  {
			  transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
																	  "select", new XPath(m_selectExpression),
																	  new org.apache.xpath.objects.XMLNodeCursorImpl(sourceNodes));
		  }

		  final SerializationHandler rth = transformer.getSerializationHandler();
		  final StylesheetRoot sroot = transformer.getStylesheet();
		  final TemplateList tl = sroot.getTemplateListComposed();

		  final boolean quiet = transformer.getQuietConflictWarnings();

		  // Should be able to get this from the iterator 
		  // but there might be a codebase issue.
		  DTM dtm = xctxt.getDTM(sourceNode);

		  int argsFrame = -1;
		  if (nParams > 0)
		  {
			  // This code will create a section on the stack that is all the 
			  // evaluated arguments. These will be copied into the real params 
			  // section of each called template.
			  argsFrame = vars.link(nParams);
			  vars.setStackFrame(thisframe);

			  int maxParamStackFrameIndex = -1;

			  for (int i = 0; i < nParams; i++) 
			  {
				  ElemWithParam ewp = m_paramElems[i];
				  if (transformer.getDebug())
					  transformer.getTraceManager().emitTraceEvent(ewp);

				  XObject obj = null;

				  try {
					  obj = ewp.getValue(transformer, sourceNode);
				  }
				  catch (TransformerException ex) {
					  throw new TransformerException(ex.getMessage(), srcLocator);   
				  }

				  if (transformer.getDebug())
					  transformer.getTraceManager().emitTraceEndEvent(ewp);

				  String tunnelStrVal = ewp.getTunnel();
				  obj.setTunnel(tunnelStrVal);
				  QName qName = ewp.getName();
				  obj.setQName(qName);

				  vars.setLocalVariable(i, obj, argsFrame);
				  maxParamStackFrameIndex = i;
			  }

			  for (int idx = 0; idx < MAX_PARAM_LIMIT; idx++) {
				  XObject obj = vars.getLocalVariable(idx, thisframe);
				  if (obj != null) {
					  String tunnelValStr = obj.getTunnel();
					  if ((obj.getTunnel() != null) && XslTransformEvaluationHelper.isTunnelAttributeYes(tunnelValStr)) {
						  vars.setLocalVariable(++maxParamStackFrameIndex, obj, argsFrame);
					  }
				  }
				  else {
					  break; 
				  }
			  }

			  ElemTemplateElement parentElem = getParentElem();
			  while (!(parentElem instanceof ElemTemplate)) {
				  parentElem = parentElem.getParentElem(); 
			  }

			  List<XObject> tunnelParamObjList = parentElem.getTunnelParamObjList();
			  for (int idx = 0; idx < tunnelParamObjList.size(); idx++) {
				  XObject var = tunnelParamObjList.get(idx);
				  vars.setLocalVariable(++maxParamStackFrameIndex, var, argsFrame);
			  }

			  vars.setStackFrame(argsFrame);
		  }

		  xctxt.pushContextNodeList(sourceNodes);
		  pushContextNodeListFlag = true;

		  IntStack currentNodes = xctxt.getCurrentNodeStack();

		  IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();

		  int child;
		  while (DTM.NULL != (child = sourceNodes.nextNode()))
		  {
			  currentNodes.setTop(child);
			  currentExpressionNodes.setTop(child);

			  if (xctxt.getDTM(child) != dtm)
			  {
				  dtm = xctxt.getDTM(child);
			  }

			  final int exNodeType = dtm.getExpandedTypeID(child);

			  final int nodeType = dtm.getNodeType(child);

			  final QName mode = transformer.getMode();

			  ElemTemplate template = tl.getTemplateFast(xctxt, child, exNodeType, mode, -1, quiet, dtm);

			  // If that didn't locate a node, fall back to a default template rule.
			  if (template == null)
			  {
				  switch (nodeType)
				  {
				  case DTM.DOCUMENT_FRAGMENT_NODE :
				  case DTM.ELEMENT_NODE :
					  template = sroot.getDefaultRule();
					  break;
				  case DTM.ATTRIBUTE_NODE :
				  case DTM.CDATA_SECTION_NODE :
				  case DTM.TEXT_NODE :
					  transformer.pushPairCurrentMatched(sroot.getDefaultTextRule(), child);
					  transformer.setCurrentElement(sroot.getDefaultTextRule());
					  dtm.dispatchCharactersEvents(child, rth, false);
					  transformer.popCurrentMatched();
					  continue;
				  case DTM.DOCUMENT_NODE :
					  template = sroot.getDefaultRootRule();
					  break;
				  default :
					  continue;
				  }
			  }
			  else
			  {
				  transformer.setCurrentElement(template);
			  }

			  transformer.pushPairCurrentMatched(template, child);
			  if (check)
				  guard.checkForInfiniteLoop();

			  int currentFrameBottom;
			  
			  XPath templateMatchXPath = template.getMatch();
			  String templateMatchPatternStr = templateMatchXPath.getPatternString();
			  
			  if (template.m_frameSize > 0)
			  {
				  xctxt.pushRTFContext();
				  currentFrameBottom = vars.getStackFrame();
				  vars.link(template.m_frameSize);

				  if (template.m_inArgsSize > 0)
				  {
					  int paramIndex = 0;
					  
					  List<QName> paramNameList = new ArrayList<QName>();
					  
					  for (ElemTemplateElement elem = template.getFirstChildElem(); 
							  null != elem; elem = elem.getNextSiblingElem()) 
					  {
						  if (Constants.ELEMNAME_PARAMVARIABLE == elem.getXSLToken())
						  {
							  ElemParam ep = (ElemParam)elem;
							  QName paramQName = ep.getName();
							  
							  paramNameList.add(paramQName);
							  if (paramNameList.size() > 1) {
								  List<QName> prevList = paramNameList.subList(0, paramNameList.size() - 1);
								  if (prevList.contains(paramQName)) {
									  throw new TransformerException("XTSE0580 : An XSL template parameter name '" + 
								                                                              paramQName.toString() + "' is not unique.", srcLocator);
								  }
							  }

							  int i;
							  for (i = 0; i < nParams; i++) 
							  {
								  ElemWithParam ewp = m_paramElems[i];
								  if (ewp.m_qnameID == ep.m_qnameID)                  
								  {
									  XObject obj = vars.getLocalVariable(i, argsFrame);

									  XObject argConvertedVal = null;
									  String paramAsAttrStrVal = ep.getAs();

									  if (paramAsAttrStrVal != null) {
										  argConvertedVal = getParamValueAsAttributeProcessing(obj, templateMatchPatternStr, elem.getPrefixTable(), 
												                                               i, paramAsAttrStrVal, transformer, srcLocator);
									  }
									  else {
										  argConvertedVal = obj;  
									  }

									  if (argConvertedVal instanceof ResultSequence) {                
										  XMLNodeCursorImpl nodeSet = XslTransformEvaluationHelper.getXNodeSetFromResultSequence((ResultSequence)argConvertedVal, 
												  xctxt);
										  if (nodeSet != null) {
											  argConvertedVal = nodeSet;  
										  }
									  }

									  vars.setLocalVariable(paramIndex, argConvertedVal);
									  break;
								  }
							  }              
						  }
						  else
							  break;
						  paramIndex++;
					  }                        
				  }                    
			  }
			  else {
				  currentFrameBottom = 0;
			  }

			  if (transformer.getDebug()) {
				  transformer.getTraceManager().emitTraceEvent(template);
			  }

			  String templateAsAttrVal = template.getAs();

			  if (templateAsAttrVal != null) {         
				  try {
					  // Check whether an xsl:template element's result contents conform to the 
					  // SequenceType expression specified as value of xsl:template element's 
					  // 'as' attribute.

					  int dtmNodeHandle = transformer.transformToGlobalRTF(template);

					  NodeList nodeList = (new XRTreeFrag(dtmNodeHandle, xctxt, template)).convertToNodeset();             
					  XObject templateEvalResultForAsAttr = new XNodeSetForDOM(nodeList, xctxt);

					  templateEvalResultForAsAttr = SequenceTypeSupport.castXdmValueToAnotherType(templateEvalResultForAsAttr, templateAsAttrVal, 
							  null, xctxt);
					  if (templateEvalResultForAsAttr != null) {
						  SerializationHandler handler = transformer.getSerializationHandler();        

						  try {
							  if (templateEvalResultForAsAttr instanceof XMLNodeCursorImpl) {
								  ElemCopyOf.copyOfActionOnNodeSet((XMLNodeCursorImpl)templateEvalResultForAsAttr, transformer, 
										  handler, xctxt);
							  }
							  else {
								  ElemCopyOf.copyOfActionOnResultSequence((ResultSequence)templateEvalResultForAsAttr, 
										  transformer, handler, xctxt, false); 
							  }
						  } 
						  catch (TransformerException ex) {
							  throw new TransformerException(ex.getMessage(), srcLocator); 
						  } 
						  catch (SAXException ex) {
							  transformer.getErrorListener().fatalError(new TransformerException(ex)); 
						  }   
					  }
					  else {
						  QName m_name = template.getName();
						  String m_matchPatternStr = (template.getMatch()).getPatternString();
						  String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPatternStr; 
						  throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
																						  + " is " + templateAsAttrVal + ". But the template "
																						  + "result doesn't conform to this required type.", srcLocator);  
					  }
				  }
				  catch (TransformerException ex) {
					  String trfExpMessage = ex.getMessage();
					  if ((trfExpMessage != null) && trfExpMessage.startsWith("XTTE0590")) {
						  throw ex;   
					  }
					  else {
						  QName m_name = template.getName();
						  String m_matchPatternStr = (template.getMatch()).getPatternString();
						  String errTemplateStr = (m_name != null) ? m_name.toString() : m_matchPatternStr; 
						  throw new TransformerException("XTTE0505 : The required result type of template " + errTemplateStr 
																						  + " is " + templateAsAttrVal + ". But the template "
																						  + "result doesn't conform to this required type.", srcLocator);
					  }
				  }  
			  }
			  else {
				  // For the XSL template that matched the context node,
				  // evaluate this XSL stylesheet template's child elements.
				  for (ElemTemplateElement t = template.m_firstChild; 
						  											t != null; t = t.m_nextSibling) {
					  xctxt.setSAXLocator(t);

					  try
					  {
						  transformer.pushElemTemplateElement(t);
						  t.execute(transformer);
					  }
					  finally
					  {
						  transformer.popElemTemplateElement();
					  }
				  }
			  }

			  if (transformer.getDebug()) {
				  transformer.getTraceManager().emitTraceEndEvent(template);
			  }

			  if (template.m_frameSize > 0)
			  {
				  vars.unlink(currentFrameBottom);
				  xctxt.popRTFContext();
			  }

			  transformer.popCurrentMatched();

		  } // end while (DTM.NULL != (child = sourceNodes.nextNode()))
	  }
	  catch (SAXException se)
	  {
		  transformer.getErrorListener().fatalError(new TransformerException(se));
	  }
	  finally
	  {
		  if (transformer.getDebug())
			  transformer.getTraceManager().emitSelectedEndEvent(sourceNode, this,
					  "select", new XPath(m_selectExpression),
					  new org.apache.xpath.objects.XMLNodeCursorImpl(sourceNodes));

		  // Unlink to the original stack frame  
		  if (nParams > 0)
			  vars.unlink(thisframe);
		  xctxt.popSAXLocator();
		  if (pushContextNodeListFlag) xctxt.popContextNodeList();
		  transformer.popElemTemplateElement();
		  xctxt.popCurrentExpressionNode();
		  xctxt.popCurrentNode();
		  sourceNodes.detach();
	  }
  }

  /**
   * Method definition to do type checking and required modification of xsl:template's xsl:param
   * value using the XPath sequence type expression from xsl:param's 'as' attribute.
   * 
   * @param srcValue								      An XDM value on which type checking and 
   *                                                      value conversion is required.
   * @param templateMatchPatternStr                       xsl:template element's 'match' attribute's value
   * @param prefixTable									  An XSL transformation's run-time XML namespace
   *                                                      prefix table list.
   * @param paramIdx									  An xsl:param's relative index value within sibling 
   *                                                      xsl:param elements. 
   * @param paramAsAttrStrVal							  String value of xsl:param's 'as' attribute
   * @param transformer                                   An XSL transformation run-time TransformerImpl object
   * @param srcLocator									  An XSL transformation SourceLocator object 
   * 
   * @return											  An XDM value produced after conversion using xsl:param's 
   *                                                      XPath sequence type 'as' attribute.
   * @throws TransformerException
   */
  private XObject getParamValueAsAttributeProcessing(XObject srcValue, String templateMatchPatternStr, List prefixTable, 
		                                             int paramIdx, String paramAsAttrStrVal, TransformerImpl transformer, 
		                                             SourceLocator srcLocator) throws TransformerException {

	  XObject argConvertedVal = null;
	  
	  XPathContext xctxt = transformer.getXPathContext();

	  try {
		  XPath seqTypeXPath = new XPath(paramAsAttrStrVal, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, true);            
		  XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
		  SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;

		  XMLNodeCursorImpl nodeSet = SequenceTypeSupport.getNodeReference(srcValue);
		  if (nodeSet != null) {
			  XSTypeDefinition typeDef = nodeSet.getXsTypeDefinition();                    	                      	                      	  
			  if (typeDef != null) {                    		  
				  XSTypeDefinition typeDef2 = seqExpectedTypeData.getXsTypeDefinition();
				  if (typeDef2 != null && isTypeXsDefinitionEqual(typeDef, typeDef2)) {                            	  
					  argConvertedVal = nodeSet;                      			
				  }
				  else {
					  throw new TransformerException("XTTE0590 : An XSL template's parameter value at index " + (paramIdx + 1) + " for "
																							  + "template {" + templateMatchPatternStr + "} doesn't "
																							  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
				  }
			  }                    	  
			  else if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {                    		  
				  DTMCursorIterator dtmIter = nodeSet.getContainedIter();                    		  
				  if (dtmIter instanceof NodeTest) {
					  try {
						  ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression(
								  																					(NodeTest)dtmIter, transformer, srcLocator);
						  if (elemFunction != null) {
							  // REVISIT : To check for elemFunction object's conformance with details in 
							  // the object seqExpectedTypeData.getSequenceTypeFunctionTest()  
							  argConvertedVal = new ElemFunctionItem(elemFunction);
						  }
					  }
					  catch (TransformerException ex) {
						  // NO OP
					  }
				  }
			  }
		  }
		  else if ((seqExpectedTypeData.getSequenceTypeFunctionTest() != null) && (srcValue instanceof XPathInlineFunction)) {
			  // REVISIT : To check for argValue's conformance with details in 
			  // the object seqExpectedTypeData.getSequenceTypeFunctionTest()
			  argConvertedVal = srcValue; 
		  }

		  if (argConvertedVal == null) {
			  argConvertedVal = SequenceTypeSupport.castXdmValueToAnotherType(srcValue, paramAsAttrStrVal, null, xctxt, prefixTable);
		  }

		  if (argConvertedVal == null) {
			  throw new TransformerException("XTTE0590 : An XSL template's parameter value at index " + (paramIdx + 1) + " for "
																								  + "template {" + templateMatchPatternStr + "} doesn't "
																								  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
		  }
	  }
	  catch (TransformerException ex) {
		  if ((SequenceTypeSupport.INLINE_FUNCTION_PARAM_TYPECHECK_COUNT_ERROR).equals(ex.getMessage())) {
			  throw new TransformerException("XTTE0590 : The number of XPath inline function parameters, is not equal to "
					  																			  + "the number of expected type specifications for them.", srcLocator);   
		  }
		  else {
			  throw new TransformerException("XTTE0590 : An XSL template's parameter value at index " + (paramIdx + 1) + " for "
																								  + "template {" + templateMatchPatternStr + "} doesn't "
																								  + "match the declared parameter type " + paramAsAttrStrVal + ".", srcLocator);
		  }
	  }
	  
	  return argConvertedVal;	  
  }

  /**
   * This method definition does, an XSL transformation for a sequence of 
   * XDM atomic values.
   */
  private void executeXslTransformAtomicValueSeq(TransformerImpl transformer, final XPathContext xctxt,
		  														                            ResultSequence resultSeq, QName xslTemplateInvokeMode) 
		  														                            		              throws TransformerException {
	  
	  final StylesheetRoot sroot = transformer.getStylesheet();
	  final TemplateList tl = sroot.getTemplateListComposed();
	  
	  Hashtable templateListHashTable = tl.getPatternTable();
	  Set hashTableEntrySet = templateListHashTable.entrySet();
	  
	  for (int idx = 0; idx < resultSeq.size(); idx++) {
		  XObject xObj = resultSeq.item(idx);
		  
		  executeXslTransformXdmAtomicValue(transformer, xctxt, xObj, hashTableEntrySet, xslTemplateInvokeMode);	  
	  }
  }
  
  /**
   * This method initializes few XSL transform context variables, and does 
   * transformation for one XDM atomic value.
   */
  private void executeXslTransformAtomicValue(TransformerImpl transformer, final XPathContext xctxt,
		  														XObject contextItem, QName xslTemplateInvokeMode) throws TransformerException {
	  
	  final StylesheetRoot sroot = transformer.getStylesheet();
	  final TemplateList tl = sroot.getTemplateListComposed();

	  Hashtable templateListHashTable = tl.getPatternTable();
	  Set hashTableEntrySet = templateListHashTable.entrySet();
	  // Iterator iter = hashTableEntrySet.iterator();

	  executeXslTransformXdmAtomicValue(transformer, xctxt, contextItem, hashTableEntrySet, xslTemplateInvokeMode);
	  
	  // Recreate an iterator object instance
	  //iter = hashTableEntrySet.iterator();
  }

  /**
   * This method definition does, an XSL transform for one XDM atomic value.
   */
  private void executeXslTransformXdmAtomicValue(TransformerImpl transformer, final XPathContext xctxt, XObject contextItem,
		  													                               Set hashTableEntrySet, QName xslTemplateInvokeMode) 
		  													                            		     throws TransformerException {
	  
	  Iterator iter = hashTableEntrySet.iterator();	  
	  
	  ElemTemplate elemTemplate = null;	  
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();	  
	  
	  XObject prevCtxItem = xctxt.getXPath3ContextItem();
	  
	  xctxt.setXPath3ContextItem(contextItem);
	  
	  // A list of TemplateDefnPriorityPair object instances, is needed to sort 
	  // ElemTemplate object instances according to numeric priority (if this priority 
	  // is not specified within an XSL stylesheet, then its value is 
	  // java double -Infinity which is the min value of java double as 
	  // provided by JDK).  
	  List<TemplateDefnPriorityPair> templateDefnPriorityPairList = new ArrayList<TemplateDefnPriorityPair>();
	  
	  // This while loop finds an ElemTemplate object instance, that will be
	  // used to do an XSL transformation of an XPath data model (XDM) atomic 
	  // value (e.g, xs:string, xs:integer etc) that has been provided as an 
	  // argument to this method.
	  
	  // An xsl:template instance selected for this purpose, is chosen 
	  // according to xsl:template's mode and priority. At run-time, an 
	  // xsl:template instance will always have priority value (which will be 
	  // java lang double -Infinity value if not specified within an 
	  // XSL stylesheet. Otherwise, xsl:template's priority shall be an xs:decimal 
	  // finite numeric value). At run-time, an xsl:template's mode will 
	  // be null when not specified with an XML attribute named 'mode', otherwise 
	  // xsl:template's mode will be an XML QName typed non-null value.
	  
	  boolean templateSelectedWithoutPriority = false;
	  
	  while (iter.hasNext()) {
		  Entry<Object, Object> hashTableEntry = (Entry<Object, Object>)iter.next();
		  Object hashTableKeyObj = hashTableEntry.getKey();
		  TemplateSubPatternAssociation hashTableValueObj = (TemplateSubPatternAssociation)(hashTableEntry.getValue());		
		  elemTemplate = hashTableValueObj.getTemplate();
		  
		  QName templateDefnMode = elemTemplate.getMode();		  		   		   
		  
		  if ((xslTemplateInvokeMode != null) && (templateDefnMode != null)) {
			 if (xslTemplateInvokeMode.equals(templateDefnMode)) {			  
				templateSelectedWithoutPriority = true;
			 }
		  }
		  else if ((xslTemplateInvokeMode != null) && (templateDefnMode == null)) {
			 templateSelectedWithoutPriority = false; 
		  }
		  else if ((xslTemplateInvokeMode == null) && (templateDefnMode != null)) {
			 templateSelectedWithoutPriority = false; 
		  }
		  else {
			 templateSelectedWithoutPriority = true; 
		  }
		  
		  if (!templateSelectedWithoutPriority) {
			 continue;  
		  }
		  
		  String keyValueStr = (String)hashTableKeyObj;	  
		  String[] strArray1 = keyValueStr.split(Constants.XSL_PATTERN_TABLE_DELIM);
		  if (keyValueStr.startsWith(".[") && (strArray1.length == 3)) {
			  String xslTemplatePriorityStr = strArray1[2];			  
			  String xpathStr = keyValueStr.substring(0, keyValueStr.indexOf(Constants.XSL_PATTERN_TABLE_DELIM));
			  xpathStr = xpathStr.substring(2);
			  xpathStr = xpathStr.substring(0, xpathStr.length() - 1);
			  
			  XPath xpath = new XPath(xpathStr, srcLocator, xctxt.getNamespaceContext(), 
					  															XPath.SELECT, null);						
			  XObject xpathEvalResult = xpath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());						
			  if (xpathEvalResult instanceof XBooleanStatic) {
				  XBooleanStatic xBoolStatic = (XBooleanStatic)xpathEvalResult;
				  templateSelectedWithoutPriority = xBoolStatic.bool(); 
			  }
			  else if (xpathEvalResult instanceof XBoolean) {
				  XBoolean xBoolean = (XBoolean)xpathEvalResult;
				  templateSelectedWithoutPriority = xBoolean.bool(); 
			  }
			  else if (xpathEvalResult instanceof XSBoolean) {
				  XSBoolean xsBoolean = (XSBoolean)xpathEvalResult;
				  templateSelectedWithoutPriority = xsBoolean.bool();
			  }
			  
			  if (templateSelectedWithoutPriority) {
			     TemplateDefnPriorityPair templateDefnPriorityPair = new TemplateDefnPriorityPair(keyValueStr, elemTemplate, 
                      																					(Double.valueOf(xslTemplatePriorityStr)).doubleValue());
			     templateDefnPriorityPairList.add(templateDefnPriorityPair);
			  }
		  }
		  
	  } // while (iter.hasNext())
	  
	  if (templateDefnPriorityPairList.size() == 0) {		  
		  // Reset iterator
		  iter = hashTableEntrySet.iterator();
		  
		  while (iter.hasNext()) {
			  Entry<Object, Object> hashTableEntry = (Entry<Object, Object>)iter.next();
			  Object hashTableKeyObj = hashTableEntry.getKey();
			  TemplateSubPatternAssociation hashTableValueObj = (TemplateSubPatternAssociation)(hashTableEntry.getValue());		
			  elemTemplate = hashTableValueObj.getTemplate();
			  
			  QName templateDefnMode = elemTemplate.getMode();		  		   		   
			  
			  if ((xslTemplateInvokeMode != null) && (templateDefnMode != null)) {
				 if (xslTemplateInvokeMode.equals(templateDefnMode)) {			  
					templateSelectedWithoutPriority = true;
				 }
			  }
			  else if ((xslTemplateInvokeMode != null) && (templateDefnMode == null)) {
				 templateSelectedWithoutPriority = false; 
			  }
			  else if ((xslTemplateInvokeMode == null) && (templateDefnMode != null)) {
				 templateSelectedWithoutPriority = false; 
			  }
			  else {
				 templateSelectedWithoutPriority = true; 
			  }
			  
			  if (!templateSelectedWithoutPriority) {
				 continue;  
			  }
			  
			  String keyValueStr = (String)hashTableKeyObj;			  
			  String[] strArray1 = keyValueStr.split(Constants.XSL_PATTERN_TABLE_DELIM);
			  if (".".equals(strArray1[0]) && (strArray1.length == 3)) {
				 String xslTemplatePriorityStr = strArray1[2];
				 TemplateDefnPriorityPair templateDefnPriorityPair = new TemplateDefnPriorityPair(keyValueStr, elemTemplate, 
	                   																					(Double.valueOf(xslTemplatePriorityStr)).doubleValue());
				 templateDefnPriorityPairList.add(templateDefnPriorityPair);
				  
			  }
			  
		  } // while (iter.hasNext())
	  }

	  templateDefnPriorityPairList.sort(null);

	  TemplateDefnPriorityPair templateDefnPriorityPair = templateDefnPriorityPairList.get(
				  																		templateDefnPriorityPairList.size() - 1);
	  
	  elemTemplate = templateDefnPriorityPair.getElemTemplate();
						 						  		 
	  for (ElemTemplateElement t = elemTemplate.m_firstChild; 
			  												t != null; t = t.m_nextSibling) {
		  xctxt.setSAXLocator(t);
		  transformer.pushElemTemplateElement(t);    		   
		  t.execute(transformer);
		  transformer.popElemTemplateElement();
	  }
	  
	  xctxt.setXPath3ContextItem(prevCtxItem);
  }

  /**
   * Check whether all XDM items within the supplied ResultSequence object 
   * are atomic values. 
   */
  private boolean isAllSeqItemsXdmAtomicValues(ResultSequence resultSeq) {
	  boolean result = true;
	
	  for (int idx = 0; idx < resultSeq.size(); idx++) {
		 XObject xObj = resultSeq.item(idx);
		 if (!isXdmItemAtomicValue(xObj)) {
			result = false;
			break;
		 }
	  }
	  
	  return result;
  }
  
  /**
   * Check whether an supplied XDM item is an atomic value.
   */
  private boolean isXdmItemAtomicValue(XObject xObj) {
	  boolean result = false;
	  
	  if ((xObj instanceof XNumber) || (xObj instanceof XBoolean) || 
		  (xObj instanceof XBooleanStatic) || (xObj instanceof XString) || (xObj instanceof XSAnyAtomicType)) {
	      result = true;
	  }
	  
	  return result;
  }
  
  /**
   * Check whether two XSTypeDefinition objects, represent the same schema type. 
   */
  private boolean isTypeXsDefinitionEqual(XSTypeDefinition typeDef1, XSTypeDefinition typeDef2) {	  
	  boolean result = false;

	  String typeName1 = typeDef1.getName();
	  String typeNamespace1 = typeDef1.getNamespace();

	  String typeName2 = typeDef2.getName();
	  String typeNamespace2 = typeDef2.getNamespace();
	  
	  if ((typeNamespace1 == null) && (typeNamespace2 == null)) {
		 result = typeName1.equals(typeName2);   
	  }
	  else if (((typeNamespace1 == null) && (typeNamespace2 != null)) || ((typeNamespace1 != null) && (typeNamespace2 == null))) {
		 result = false; 
	  }
	  else if (typeNamespace1.equals(typeNamespace2) && typeName1.equals(typeName2)) {
		 result = true; 
	  }

	  return result;
  }
  
  /**
   * A list containing objects of this class, is sortable in an ascending
   * order by numeric xs:double priority values.
   */
  class TemplateDefnPriorityPair implements Comparable<TemplateDefnPriorityPair> {
	  
	  private String keyValueStr = null;
	  
	  private ElemTemplate elemTemplate = null;
	  
	  private double priority;
	  
	  /**
	   * Default constructor.
	   */
	  public TemplateDefnPriorityPair() {
		 // NO OP  
	  }
	  
	  /**
	   * Class constructor.
	   */
	  public TemplateDefnPriorityPair(String keyValueStr, ElemTemplate elemTemplate, double priority) {
		 this.keyValueStr = keyValueStr;
		 this.elemTemplate = elemTemplate;
		 this.priority = priority;
	  }

	  public ElemTemplate getElemTemplate() {
		  return elemTemplate;
	  }

	  public void setElemTemplate(ElemTemplate elemTemplate) {
		  this.elemTemplate = elemTemplate;
	  }

	  public String getKeyValueStr() {
		  return keyValueStr;
	  }

	  public void setKeyValueStr(String keyValueStr) {
		  this.keyValueStr = keyValueStr;
	  }

	  public double getPriority() {
		  return priority;
	  }

	  public void setPriority(double priority) {
		  this.priority = priority;
	  }

	  @Override
	  public int compareTo(TemplateDefnPriorityPair obj1) {
		 int result = 0;
		 
		 if (this.priority < obj1.getPriority()) {
			result = -1; 
		 }
		 else if (this.priority > obj1.getPriority()) {
			result = 1;  
		 }
		 
		 return result;
	  }

  }

}
