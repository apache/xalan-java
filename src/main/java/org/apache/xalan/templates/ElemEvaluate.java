/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.templates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * An implementation of XSLT 3.0 instruction xsl:evaluate.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class ElemEvaluate extends ElemTemplateElement {

	private static final long serialVersionUID = 1913661536548419704L;
	
	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return The token ID for this element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_EVALUATE;
	}
	
	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_EVALUATE_STRING;
	}
	
	/**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */    
	private Vector m_vars;

	/**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */
	private int m_globals_size;
	
	/**
	 * The value of the "xpath" attribute.
	 * 
	 * This attribute is mandatory.
	 */
	private XPath m_xpathPatternAttr;
	
	/**
	 * Get the "xpath" attribute.
	 */
	public XPath getXpath() {
		return m_xpathPatternAttr;
	}

	/**
	 * Set the "xpath" attribute.
	 */
	public void setXpath(XPath v) {
		this.m_xpathPatternAttr = v;
	}

	/**
	 * The value of xsl:evaluate instruction's "as" 
	 * attribute.
	 */
	private String m_asAttrVal;
	
	/**
	 * Get the value of xsl:evaluate instruction's 'as' 
	 * attribute.
	 */
	public String getAs() {
		return m_asAttrVal;
	}

	/**
	 * Set the value of xsl:evaluate instruction's 'as' 
	 * attribute.
	 */
	public void setAs(String asAttrVal) {
		this.m_asAttrVal = asAttrVal;
	}

	/**
	 * The value of the "base-uri" attribute.
	 */
	private String m_baseUri;
	
	/**
	 * Get the 'base-uri' attribute.
	 */
	public String getBaseUri() {
		return m_baseUri;
	}

	/**
	 * Set the 'base-uri' attribute.
	 */
	public void setBaseUri(String baseUri) {
		this.m_baseUri = baseUri;
	}

	/**
	 * The value of the "with-params" attribute.
	 */
	private XPath m_withParams;
	
	/**
	 * Get the 'with-params' attribute.
	 */
	public XPath getWithParams() {
		return m_withParams;
	}

	/**
	 * Set the 'with-params' attribute.
	 */
	public void setWithParams(XPath withParamsPatternAttr) {
		this.m_withParams = withParamsPatternAttr;
	}

	/**
	 * The value of the "context-item" attribute.
	 */
	private XPath m_contexItem;
	
	/**
	 * Get the "context-item" attribute.
	 */
	public XPath getContextItem() {
		return m_contexItem;
	}

	/**
	 * Set the "context-item" attribute.
	 */
	public void setContextItem(XPath v) {
		this.m_contexItem = v;
	}
	
	/**
	 * The value of the "namespace-context" attribute.
	 */
	private XPath m_namespaceContext;
	
	/**
	 * Get the "namespace-context" attribute.
	 */
	public XPath getNamespaceContext() {
		return m_namespaceContext;
	}

	/**
	 * Set the "namespace-context" attribute.
	 */
	public void setNamespaceContext(XPath namespaceContext) {
		this.m_namespaceContext = namespaceContext;
	}

	/**
	 * The value of the "schema-aware" attribute.
	 */
	private String m_schemaAwareAttr;
	
	/**
	 * Get the "schema-aware" attribute.
	 */
	public String getSchemaAwareAttr() {
		return m_schemaAwareAttr;
	}

	/**
	 * Set the "schema-aware" attribute.
	 */
	public void setSchemaAwareAttr(String schemaAwareAttr) {
		this.m_schemaAwareAttr = schemaAwareAttr;
	}
	
	/**  
	 * Vector of xsl:with-param elements associated with this element.
	 */
	protected ElemWithParam[] m_withParamElems = null;

	/**
	 * Get the count of xsl:with-param elements associated with this element.
	 * @return The number of xsl:with-param elements.
	 */
	public int getWithParamElemCount()
	{
		return (m_withParamElems == null) ? 0 : m_withParamElems.length;
	}
	
	/**
	 * Get a xsl:with-param element associated with this element.
	 *
	 * @param i Index of element to find
	 *
	 * @return xsl:with-param element at given index
	 */
	public ElemWithParam getWithParamElem(int i)
	{
		return m_withParamElems[i];
	}
	
	/**
	 * Set a xsl:with-param element associated with this element.
	 *
	 * @param ElemWithParam xsl:with-param element to set. 
	 */
	public void setWithParamElem(ElemWithParam withparamElem)
	{
		if (m_withParamElems == null)
		{
			m_withParamElems = new ElemWithParam[1];
			m_withParamElems[0] = withparamElem;
		}
		else
		{
			int length = m_withParamElems.length;
			ElemWithParam[] ewp = new ElemWithParam[length + 1];
			System.arraycopy(m_withParamElems, 0, ewp, 0, length);
			m_withParamElems = ewp;
			ewp[length] = withparamElem;
		}
	}

	/**
	 * Class constructor.
	 */
	public ElemEvaluate() {
	   // NO OP
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
	}
	
	/**
	 * This after the template's children have been composed.
	 */
	public void endCompose(StylesheetRoot sroot) throws TransformerException
	{  
		super.endCompose(sroot);
	}
	
	/**
	 * Evaluate an xsl:evaluate instruction, and emit final result of evaluation of
	 * this instruction (via xsl:copy-of semantics) to XSL transform's output.
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{
		XPathContext xctxt = transformer.getXPathContext();
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	    
	    if (!transformer.getProperty(TransformerImpl.XSL_EVALUATE_PROPERTY)) {
	       throw new TransformerException("XTTE0505 : An xsl:evaluate instruction's evaluation is disabled for "
	       		                                                                                        + "this XSL transformation.", srcLocator);
	    }
	    
	    int contextNode = xctxt.getContextNode();

	    Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
    	Map<QName, XObject> varBackupMap = new HashMap<QName, XObject>();
    	
	    try {	    	
	    	if (m_xpathPatternAttr != null) {	    	
	    		if (m_contexItem != null) {	    	
	    			if (m_vars != null) {
	    				m_contexItem.fixupVariables(m_vars, m_globals_size);
	    			}

	    			Expression contextItemExpr = m_contexItem.getExpression();

	    			XObject contextItem = null;
	    			if (contextItemExpr instanceof LocPathIterator) {				
	    				contextItem = contextItemExpr.execute(xctxt);
	    			}
	    			else if (contextItemExpr instanceof Variable) {
	    				contextItem = ((Variable)contextItemExpr).execute(xctxt);
	    			}

	    			if (contextItem instanceof XMLNodeCursorImpl) {
	    				DTMCursorIterator dtmIter = ((XMLNodeCursorImpl)contextItem).iterRaw();
	    				contextNode = dtmIter.nextNode();
	    			}
	    			else {
	    				xctxt.setXPath3ContextItem(contextItem);
	    			}
	    		}

	    		Expression xslEvaluateXPathExpr = m_xpathPatternAttr.getExpression();

	    		XObject xobjTarget = xslEvaluateXPathExpr.execute(xctxt);
	    		String xpathTargetExprStr = XslTransformEvaluationHelper.getStrVal(xobjTarget);
	    		
	    		if (m_withParamElems != null) {			
	    			int currentNode = xctxt.getCurrentNode();	    			

	    			int size = m_withParamElems.length;	    			
	    			for (int i = 0; i < size; i++) {	    					    				
	    				ElemWithParam ewp = m_withParamElems[i];
	    				QName withParamQname = ewp.getName();
	    				if (i > 0) {
	    					for (int ii = 0; ii < i; ii++) {
	    						ElemWithParam prevWithParam = m_withParamElems[ii];
	    						QName prevWithParamQname = prevWithParam.getName();
	    						if (withParamQname.equals(prevWithParamQname.getNamespace(), prevWithParamQname.getLocalPart())) {
	    							throw new TransformerException("XTTE0505 : An xsl:evaluate instruction has xsl:with-param '" + 
	    						                                                                       prevWithParamQname.getLocalName() + "' more than once.", srcLocator);
	    						}
	    					}
	    				}
	    				if (ewp.m_index >= 0) {
	    					XObject prevValue = xpathVarMap.get(withParamQname);
	    					if (prevValue != null) {
	    						varBackupMap.put(withParamQname, prevValue);
	    					}

	    					XObject withParamVal = ewp.getValue(transformer, currentNode);	    				

	    					String withParamAsAttrVal = ewp.getAs();
	    					if (withParamAsAttrVal != null) {	    					   
	    						withParamVal = processXslEvaluateWithparamAsAttribute(withParamQname, withParamVal, xctxt, 
	    								                                              srcLocator, withParamAsAttrVal);
	    					}

	    					xpathVarMap.put(withParamQname, withParamVal);
	    				}
	    			}
	    		}

	    		XPath xpathTargetObj = new XPath(xpathTargetExprStr, srcLocator, xctxt.getNamespaceContext(), 
	    																								XPath.SELECT, null, xctxt.getFunctionTable());
	    		if (m_vars != null) {
	    			xpathTargetObj.fixupVariables(m_vars, m_globals_size);
	    		}

	    		Expression xpathTargetExpr = xpathTargetObj.getExpression();

	    		if (xpathTargetExpr instanceof LocPathIterator) {
	    			evaluateXPathTargetLocPathIterator((LocPathIterator)xpathTargetExpr, transformer, xctxt, contextNode);
	    		}
	    		else {
	    			evaluateXPathTargetExpr(xpathTargetObj, transformer, xctxt, contextNode);
	    		}		
	    	}
	    }
	    finally {
	    	if (!varBackupMap.isEmpty()) {
	    		Set<QName> keySet = varBackupMap.keySet();
	    		Iterator<QName> keySetIter = keySet.iterator();
	    		while (keySetIter.hasNext()) {
	    		    QName keyQname = keySetIter.next();
	    		    XObject entryValue = varBackupMap.get(keyQname);
	    		    xpathVarMap.put(keyQname, entryValue);
	    		}
	    	}
	    }
	}
	
	/**
	 * Add a child to the child list.
	 *
	 * @param newChild Child to add to this node's children list
	 *
	 * @return The child that was just added the children list 
	 */
	public ElemTemplateElement appendChild(ElemTemplateElement newChild)
	{
		int type = ((ElemTemplateElement) newChild).getXSLToken();

		if (Constants.ELEMNAME_WITHPARAM == type)
		{
			setWithParamElem((ElemWithParam) newChild);
		}

		return super.appendChild(newChild);
	}
	
	/**
	 * Call the children visitors.
	 * 
	 * @param visitor The visitor whose appropriate method will be called.
	 */
	protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
	{
		super.callChildVisitors(visitor, callAttrs);
	}
	
	/**
	 * Method to traverse an XSL xsl:evaluate xpath target location path iterator and emit its 
	 * evaluation result to XSL transformation's output.
	 */
	private void evaluateXPathTargetLocPathIterator(LocPathIterator locPathIterator, TransformerImpl transformer, XPathContext xctxt,
																								int contextNode) throws TransformerException {

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		DTMCursorIterator dtmIter = null;		
		try {
			dtmIter = locPathIterator.asIterator(xctxt, contextNode);
		}
		catch (ClassCastException ex) {
			// no op
		}

		if (dtmIter != null) {            	
			Function func = locPathIterator.getFuncExpr();
			XPathDynamicFunctionCall dfc = locPathIterator.getDynamicFuncCallExpr();

			int nextNode;
			ResultSequence resultSeq = new ResultSequence();
			while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
			{
				XMLNodeCursorImpl singletonXPathNode = new XMLNodeCursorImpl(nextNode, xctxt);
				XObject evalResult = singletonXPathNode; 
				if (func != null) {
					// Evaluate an XPath path expression like /a/b/funcCall(..).
					// Find one result item here for a sequence of items, 
              	    // since this is within a loop.
					xctxt.setXPath3ContextItem(singletonXPathNode);                              
					evalResult = func.execute(xctxt);                              
				}
				else if (dfc != null) {
					// Evaluate an XPath path expression like /a/b/$funcCall(..).
					// Find one result item here for a sequence of items, 
              	    // since this is within a loop.
					xctxt.setXPath3ContextItem(singletonXPathNode);                              
					evalResult = dfc.execute(xctxt);                             
				}
				
				resultSeq.add(evalResult);
			}
			
			XObject var = resultSeq;
			
			if (m_asAttrVal != null) {
				checkXslEvaluateAsAttribute(var, xctxt, srcLocator);
			}

			for (int idx = 0; idx < resultSeq.size(); idx++) {
				XObject resultItem = resultSeq.item(idx);
				try {
					copyOfActionOnXdmValue(resultItem, transformer, xctxt);
				}
				catch (SAXException ex) {
					String errMesg = ex.getMessage();
					throw new TransformerException("XPDY0002 : An XPath dynamic error occured during evaluation of xsl:evaluate "
							                                                                        + "instruction." + ((errMesg != null) ? errMesg : ""), srcLocator);
				}
			}
		 }
	}
	
	/**
	 * Method to evaluate an XSL xsl:evaluate xpath target expression, and emit evaluation result to 
	 * XSL transformation's output.
	 */
	private void evaluateXPathTargetExpr(XPath targeXPath, TransformerImpl transformer, XPathContext xctxt, 
			              													   int contextNodeDtmHandle) throws TransformerException {
				
		XObject resultItem = targeXPath.execute(xctxt, contextNodeDtmHandle, xctxt.getNamespaceContext());
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		ResultSequence resultSeq = new ResultSequence();
		resultSeq.add(resultItem);
		
		if (m_asAttrVal != null) {
			checkXslEvaluateAsAttribute(resultSeq, xctxt, srcLocator);
		}

		try {
			copyOfActionOnXdmValue(resultItem, transformer, xctxt);
		}
		catch (SAXException ex) {
			String errMesg = ex.getMessage();
			throw new TransformerException("XPDY0002 : An XPath dynamic error occured during evaluation of xsl:evaluate "
					                                                                        + "instruction." + ((errMesg != null) ? errMesg : ""), srcLocator);
		}
	}

	/*
	 * Given an xdm object instance, this method does xsl:copy-of instruction semantics on the object.
	 */
	private void copyOfActionOnXdmValue(XObject xdmItem, TransformerImpl transformer, XPathContext xctxt) throws 
																										TransformerException, SAXException {		
		SerializationHandler handler = transformer.getSerializationHandler();
		
		int xdmItemType = xdmItem.getType();
		
		switch (xdmItemType) {           
			case XObject.CLASS_NODESET :          
				ElemCopyOf.copyOfActionOnNodeSet((XMLNodeCursorImpl)xdmItem, transformer, handler, xctxt);          
				break;
			case XObject.CLASS_RTREEFRAG :
				SerializerUtils.outputResultTreeFragment(handler, xdmItem, xctxt);
				break;
			case XObject.CLASS_RESULT_SEQUENCE :         
				ResultSequence resultSequence = (ResultSequence)xdmItem;          
				ElemCopyOf.copyOfActionOnResultSequence(resultSequence, transformer, handler, xctxt, true);          
				break;
			case XObject.CLASS_ARRAY :
				XslTransformSharedDatastore.xpathArray = (XPathArray)xdmItem;				          
				break;
			case XObject.CLASS_MAP :
				XslTransformSharedDatastore.xpathMap = (XPathMap)xdmItem;				          
				break;
			default :
				// no op
		}      	           

		String strVal = null;
		if ((xdmItem instanceof XBoolean) || (xdmItem instanceof XNumber) || (xdmItem instanceof XString)) {
			strVal = xdmItem.str();
			handler.characters(strVal.toCharArray(), 0, strVal.length());
		}
		else if (xdmItem instanceof XSAnyAtomicType) {
			strVal = ((XSAnyAtomicType)xdmItem).stringValue();
			handler.characters(strVal.toCharArray(), 0, strVal.length());
		}
	}

	/**
	 * Check whether xsl:evaluate instruction's result matches with xsl:evaluate's sequence type 'as' attribute. 
	 */
	private void checkXslEvaluateAsAttribute(XObject var, XPathContext xctxt, SourceLocator srcLocator)
																						throws TransformerException {
		
		XObject resultWithAsAttribute = null;
		
		if (XslTransformSharedDatastore.xpathInlineFunction != null) {
			XPath seqTypeXPath = new XPath(m_asAttrVal, srcLocator, xctxt.getNamespaceContext(), 
																						XPath.SELECT, null, true);
			XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
			SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
			if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {              	   
				resultWithAsAttribute = XslTransformSharedDatastore.xpathInlineFunction;
				XslTransformSharedDatastore.xpathInlineFunction = null;
			}
			else {
				throw new TransformerException("XTTE0505 : An xsl:evaluate instruction's evaluation result doesn't conform "
																					+ "to specified expected type " + m_asAttrVal + ".", srcLocator); 
			}
		}
		else if (var instanceof XNodeSetForDOM) {
			XObject variableConvertedVal = null;

			try {
				ElemFunction elemFunction = ElemFunction.getXSLFunctionService();
				variableConvertedVal = elemFunction.preprocessXslFunctionOrAVariableResult(var, m_asAttrVal, xctxt, null);
			}
			catch (TransformerException ex) {
				throw new TransformerException(ex.getMessage(), srcLocator); 
			}

			if (variableConvertedVal != null) {
				resultWithAsAttribute = variableConvertedVal;    
			}
			else {
				resultWithAsAttribute = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttrVal, null, xctxt); 
			}
		}
		else if (var instanceof XPathMap) {
			try {
				resultWithAsAttribute = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttrVal, null, xctxt);
			}
			catch (TransformerException ex) {
				String errMesg = ex.getMessage();
				boolean indicatorOne = errMesg.contains("value"); 
				boolean indicatorTwo = errMesg.contains("cannot be cast to a type");
				if (indicatorOne && indicatorTwo) {
					errMesg = errMesg + " An error occured, while evaluating xdm map's key or value [map's sequenceType is '" + m_asAttrVal + "'].";
					throw new TransformerException(errMesg, srcLocator);
				}
				else {
					throw new TransformerException(errMesg, srcLocator);
				}
			}    	   
		}
		else if (var instanceof XPathArray) {
			try {
				resultWithAsAttribute = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttrVal, null, xctxt);
			}
			catch (TransformerException ex) {
				String errMesg = ex.getMessage();
				boolean indicatorOne = errMesg.contains("value"); 
				boolean indicatorTwo = errMesg.contains("cannot be cast to a type");
				if (indicatorOne && indicatorTwo) {
					errMesg = errMesg + " An error occured, while evaluating an xdm array [array's sequenceType is '" + m_asAttrVal + "'].";
					throw new TransformerException(errMesg, srcLocator);
				}
				else {
					throw new TransformerException(errMesg, srcLocator);
				}
			}
		}
		else {
			try {
				resultWithAsAttribute = SequenceTypeSupport.castXdmValueToAnotherType(var, m_asAttrVal, null, xctxt);
			}
			catch (TransformerException ex) {
				throw ex; 
			}
		}
		
		if (resultWithAsAttribute == null) {
			throw new TransformerException("XTTE0505 : An xsl:evaluate instruction's evaluation result doesn't conform "
																								+ "to specified expected type " + m_asAttrVal + ".", srcLocator);
		}
	}
	
	/**
	 * Process xsl:evaluate instruction's xsl:with-param sequence type 'as' attribute. 
	 */
	private XObject processXslEvaluateWithparamAsAttribute(QName withParamQName, XObject withParamVal, XPathContext xctxt, 
			                                               SourceLocator srcLocator, String withParamAsAttrVal) throws TransformerException {
		
		XObject result = null;
		
		if (XslTransformSharedDatastore.xpathInlineFunction != null) {
			XPath seqTypeXPath = new XPath(withParamAsAttrVal, srcLocator, xctxt.getNamespaceContext(), 
																									XPath.SELECT, null, true);
			XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
			SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
			if (seqExpectedTypeData.getSequenceTypeFunctionTest() != null) {              	   
				result = XslTransformSharedDatastore.xpathInlineFunction;
				XslTransformSharedDatastore.xpathInlineFunction = null;
			}
			else {
				throw new TransformerException("XTTE0505 : An xsl:evaluate parameter " + withParamQName.getLocalName() + "'s value doesn't conform "
																									+ "to parameter's expected type " + withParamAsAttrVal + ".", srcLocator); 
			}
		}
		else if (withParamVal instanceof XNodeSetForDOM) {
			XObject objConvertedVal = null;

			try {
				ElemFunction elemFunction = ElemFunction.getXSLFunctionService();
				objConvertedVal = elemFunction.preprocessXslFunctionOrAVariableResult(withParamVal, withParamAsAttrVal, xctxt, withParamQName);
			}
			catch (TransformerException ex) {
				throw new TransformerException(ex.getMessage(), srcLocator); 
			}

			if (objConvertedVal != null) {
				result = objConvertedVal;    
			}
			else {
				result = SequenceTypeSupport.castXdmValueToAnotherType(withParamVal, withParamAsAttrVal, null, xctxt); 
			}
		}
		else if (withParamVal instanceof XPathMap) {
			try {
				result = SequenceTypeSupport.castXdmValueToAnotherType(withParamVal, withParamAsAttrVal, null, xctxt);
			}
			catch (TransformerException ex) {
				String errMesg = ex.getMessage();
				boolean indicatorOne = errMesg.contains("value"); 
				boolean indicatorTwo = errMesg.contains("cannot be cast to a type");
				if (indicatorOne && indicatorTwo) {
					errMesg = errMesg + " An error occured, while evaluating xdm map's key or value [map's sequenceType is '" + withParamAsAttrVal + "'].";
					throw new TransformerException(errMesg, srcLocator);
				}
				else {
					throw new TransformerException(errMesg, srcLocator);
				}
			}    	   
		}
		else if (withParamVal instanceof XPathArray) {
			try {
				result = SequenceTypeSupport.castXdmValueToAnotherType(withParamVal, withParamAsAttrVal, null, xctxt);
			}
			catch (TransformerException ex) {
				String errMesg = ex.getMessage();
				boolean indicatorOne = errMesg.contains("value"); 
				boolean indicatorTwo = errMesg.contains("cannot be cast to a type");
				if (indicatorOne && indicatorTwo) {
					errMesg = errMesg + " An error occured, while evaluating an xdm array [array's sequenceType is '" + withParamAsAttrVal + "'].";
					throw new TransformerException(errMesg, srcLocator);
				}
				else {
					throw new TransformerException(errMesg, srcLocator);
				}
			}
		}
		else {
			try {
				result = SequenceTypeSupport.castXdmValueToAnotherType(withParamVal, withParamAsAttrVal, null, xctxt);
			}
			catch (TransformerException ex) {
				throw ex; 
			}
		}
		
		return result;
	}

}
