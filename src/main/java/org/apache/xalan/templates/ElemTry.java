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

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;

/**
 * An implementation of, XSLT 3.0 xsl:try instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemTry extends ElemTemplateElement implements ExpressionOwner {

	static final long serialVersionUID = -3027484379104675428L;
	
	/**
	 * The "select" expression.
	 */
	private XPath m_selectExpression = null;
	
	/**
	 * Set the "select" attribute.
	 *
	 * @param xpath The XPath expression for the "select" attribute.
	 */
	public void setSelect(XPath xpath)
	{
		m_selectExpression = xpath;  
	}

	/**
	 * Get the "select" attribute.
	 *
	 * @return The XPath expression for the "select" attribute.
	 */
	public XPath getSelect()
	{
		return m_selectExpression;
	}
	
	/**
	 * The "rollback-output" boolean value.
	 * 
	 * Default value is "yes".
	 */
	private boolean m_rollbackOutput = true;
	
	/**
	 * Set the value of "rollback-output" attribute.
	 *
	 * @param rollbackOutput The boolean value for the "rollback-output" attribute.
	 */
	public void setRollbackOutput(boolean rollbackOutput)
	{
		m_rollbackOutput = rollbackOutput;   
	}

	/**
	 * Get the value of "rollback-output" attribute.
	 *
	 * @return The boolean value for the "rollback-output" attribute.
	 */
	public boolean getRollbackOutput()
	{
		return m_rollbackOutput;
	}
	
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
	 * is declared on xsl:try instruction.
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
	 * This class field is used during, XPath.fixupVariables(..) 
	 * evaluation as performed within object of this class.  
	 */    
	private Vector m_vars;

	/**
	 * This class field is used during, XPath.fixupVariables(..) 
	 * evaluation as performed within object of this class.  
	 */
	private int m_globals_size;
	
	/**
	 * Class constructor.
	 */
	public ElemTry() {}
	
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
		
		java.util.Vector vnames = (sroot.getComposeState()).getVariableNames();
	    
	    m_vars = (Vector)(vnames.clone()); 
	    m_globals_size = (sroot.getComposeState()).getGlobalsSize();

	    if (m_selectExpression != null) {
	        m_selectExpression.fixupVariables(vnames, m_globals_size);
	    }
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
	 * @return           The token id for this element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_TRY;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_TRY_STRING;
	}
	
	/**
	 * Run an xsl:try transformation.
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException
	{
        XPathContext xctxt = transformer.getXPathContext();	    	    
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	    
	    final int sourceNode = xctxt.getContextNode();
	    
	    if (m_useWhen != null) {
	    	boolean result1 = isXPathExpressionStatic(m_useWhen.getExpression());
	    	if (result1) {
	    		XObject useWhenResult = m_useWhen.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	    		if (!useWhenResult.bool()) {
	    			return;
	    		}
	    	}
	    	else {
	    		throw new TransformerException("XPST0008 : XSL variables other than XSLT static variables/parameters, cannot be "
                        																									+ "used within XPath static expression.", srcLocator);
	    	}
	    }
	    
	    if ((m_selectExpression != null) && (m_xpath_default_namespace != null)) {    		
	    	m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	    }
	    
	    if (m_selectExpression != null) {
	    	for (ElemTemplateElement t = this.m_firstChild; t != null;
																	t = t.m_nextSibling) {
	    		if (!(t instanceof ElemCatch)) {
	    		   throw new TransformerException("XTSE3140 : An XSL 'try' element has 'select' attribute, but xsl 'try' "
	    		   		                                                                 + "has a child element other than xsl 'catch'.", srcLocator);
	    		}
	    	}
		}
	    
	    // Within xsl:try element, check stylesheet node sibling 
	    // occurrence validity.
	    
	    Node node = getLastChildElem();	    
	    while (node instanceof ElemFallback) {
	       node = node.getPreviousSibling();
	    }
	    
	    if ((node == null) || !(node instanceof ElemCatch)) {
	    	throw new TransformerException("XTSE3140 : An XSL 'try' element's last child element can only be an "
	    			                                                                           + "xsl 'catch' element, or an xsl 'fallback' instruction.", srcLocator);
	    }	    
	    
	    if (m_selectExpression != null) {
	    	// An XSL processing specified by xsl:try element is
	    	// been done by xsl:try element's 'select' attribute.
	    	
	    	try {	    			    		
	    		if (m_vars != null) {
	    			m_selectExpression.fixupVariables(m_vars, m_globals_size);
	    		}

	    		m_selectExpression.setIsConcreteExceptionProcessing(true);
	    		XObject xpathEvalResult = m_selectExpression.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
	    		ResultSequence rSeq = new ResultSequence();
	    		rSeq.add(xpathEvalResult);
	    		SerializationHandler handler = transformer.getSerializationHandler(); 
	    		ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformer, handler, xctxt, false, this);
	    	}
	    	catch (TransformerException ex) {
	    		// Process XSL transformation exception with any suitable 
	    		// available xsl:catch instruction.
	    		
	    		SourceLocator srcLocator1 = ex.getLocator();	    		
	    		if (srcLocator1 == null) {
	    		   ex.setLocator(srcLocator);
	    		}
	    		
	    		handleExceptionWithXslCatch(transformer, xctxt, ex);
			}
	    	catch (SAXException ex) {
	    		throw new TransformerException(ex.getMessage(), srcLocator);
	    	}
	    }
	    else {
	    	// An XSL processing specified by xsl:try element is been done 
	    	// by xsl:try element's contained sequence constructor.
	    	
	    	for (ElemTemplateElement t = this.m_firstChild; t != null; t = t.m_nextSibling) {
	    		if (!(t instanceof ElemCatch)) {
	    			xctxt.setSAXLocator(t);
	    			transformer.setCurrentElement(t);
	    			try {
	    				transformer.transformToRTF(t);	    				
	    				t.execute(transformer);
	    			}
	    			catch (TransformerException ex) {
	    				// Process XSL transformation exception with any suitable 
	    				// available xsl:catch instruction.
	    				
	    				SourceLocator srcLocator1 = ex.getLocator();
	    	    		if (srcLocator1 == null) {
	    	    		   ex.setLocator(srcLocator);
	    	    		}
	    	    		
	    				handleExceptionWithXslCatch(transformer, xctxt, ex);
	    				
	    				break;
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * Add an XSL stylesheet child information.
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

	@Override
	public Expression getExpression() {
		// no op
		return null;
	}

	@Override
	public void setExpression(Expression exp) {
		// no op
	}
	
	/**
	 * Method definition, to handle an XSL transformation run-time exception 
	 * with an applicable xsl:catch element.
	 * 
	 * If a suitable xsl:catch element cannot be found to handle a run-time 
	 * exception, an original exception is thrown to calling code as if 
	 * XSL stylesheet processing took place without using xsl:try instruction.
	 * 
	 * @param transformer					An XSL transformation TransformerImpl object instance
	 * @param xctxt							An XPath context object
	 * @param ex							An XSL run-time TransformerException object. 
	 * @throws TransformerException
	 */
	private void handleExceptionWithXslCatch(TransformerImpl transformer, XPathContext xctxt, 
																			    TransformerException ex) throws TransformerException {
		
		Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
		
		QName errCodeKey = new QName(Constants.XSL_ERROR_NAMESACE, Constants.XSL_ERROR_CODE);
		QName errModuleKey = new QName(Constants.XSL_ERROR_NAMESACE, Constants.XSL_ERROR_MODULE);
		QName errDescKey = new QName(Constants.XSL_ERROR_NAMESACE, Constants.XSL_ERROR_DESCRIPTION);
		QName errLineNumKey = new QName(Constants.XSL_ERROR_NAMESACE, Constants.XSL_ERROR_LINE_NUMBER);
		QName errColNumKey = new QName(Constants.XSL_ERROR_NAMESACE, Constants.XSL_ERROR_COLUMN_NUMBER);
		
		try {
			String errMesg = ex.getMessage();				
			int colonIdx = errMesg.indexOf(':');
			String exceptionErrCodeLocalStr = null;
			if (colonIdx != -1) {
				exceptionErrCodeLocalStr = errMesg.substring(colonIdx + 1);
				colonIdx = exceptionErrCodeLocalStr.indexOf(':');
				if (colonIdx != -1) {
					exceptionErrCodeLocalStr = (exceptionErrCodeLocalStr.substring(0, colonIdx)).trim();
				}
				else {
					colonIdx = errMesg.indexOf(':');
					exceptionErrCodeLocalStr = (errMesg.substring(0, colonIdx)).trim();
				}
			}
			else {
				exceptionErrCodeLocalStr = errMesg;
			}
			
			boolean isRaisedByFnError = errMesg.contains("XPath 'error' function");

			ElemCatch elemCatch = getXslCatchElemToHandleException(transformer, xctxt, exceptionErrCodeLocalStr, 
					                                                                                          this, isRaisedByFnError);						

			if (elemCatch != null) {												
				String xslTrfModuleStr = transformer.getUriStrOfXslStylesheet();
				if (xslTrfModuleStr != null) {				   
					int idx = xslTrfModuleStr.lastIndexOf('/');
					if (idx > -1) {
					   int strLength = xslTrfModuleStr.length();
					   xslTrfModuleStr = "/" + xslTrfModuleStr.substring(idx + 1, strLength);
					}
				}
				else {
					xslTrfModuleStr = "";
				}
				
				String errMesg2 = null;				
				if (exceptionErrCodeLocalStr.length() > 8) {
					colonIdx = errMesg.indexOf(':');					
					exceptionErrCodeLocalStr = (errMesg.substring(0, colonIdx)).trim();
					errMesg2 = (errMesg.substring(colonIdx + 1)).trim();
				}
				else {
					colonIdx = errMesg.indexOf(':');
					errMesg2 = errMesg.substring(colonIdx + 1);
					colonIdx = errMesg2.indexOf(':');
					errMesg2 = (errMesg2.substring(colonIdx + 1)).trim();
				}
				
				if (errMesg.endsWith(".")) {
					int errMesgLngth = errMesg.length(); 				
					errMesg = errMesg.substring(0, errMesgLngth - 1);
					colonIdx = errMesg.indexOf(':'); 
					errMesg = (errMesg.substring(colonIdx + 1)).trim();
				}
				
				int idx = errMesg.indexOf(':');
				if ((idx < 10) && errMesg.startsWith("X")) {
				   errMesg = (errMesg.substring(idx + 1)).trim();
				}
				
				ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
				List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
				String errCodePrfxStr = XslTransformEvaluationHelper.getPrefixFromNsUri(Constants.XSL_ERROR_NAMESACE, prefixTable);
				
				// Set variables err:code, err:module, err:description, err:line-number, 
				// err:column-number within XPath evaluation context.
				
				xpathVarMap.put(errCodeKey, new XSQName(errCodePrfxStr, exceptionErrCodeLocalStr, Constants.XSL_ERROR_NAMESACE));
				if (xslTrfModuleStr != null) {
				   xpathVarMap.put(errModuleKey, new XSString(xslTrfModuleStr));
				}
				xpathVarMap.put(errDescKey, new XSString(errMesg));				
				SourceLocator srcLocator2 = ex.getLocator();
				int errLineNum = srcLocator2.getLineNumber();
				int errColNum = srcLocator2.getColumnNumber();
				xpathVarMap.put(errLineNumKey, new XSInteger(String.valueOf(errLineNum)));
				xpathVarMap.put(errColNumKey, new XSInteger(String.valueOf(errColNum)));

				// Evaluate xsl:catch transformation				
				xctxt.setSAXLocator(elemCatch);
				transformer.setCurrentElement(elemCatch);
				elemCatch.execute(transformer);
			}
			else {
				throw ex;
			}
		}
		finally {
			// Delete variable bindings within XML err: namespace that were 
			// created for xsl:catch processing, from XPath expression context.			
			xpathVarMap.remove(errCodeKey);
			xpathVarMap.remove(errDescKey);
			xpathVarMap.remove(errLineNumKey);
			xpathVarMap.remove(errColNumKey);
		}
	}
	
	/**
	 * Method definition, to find an xsl:catch element that should perform an 
	 * XSL transformation's error recovery, using the supplied XSL transformation's
	 * run-time error code.  
	 * 
	 * @param transformer			            An XSL transformation TransformerImpl object instance
	 * @param xctxt					            An XPath context object 
	 * @param exceptionErrCodeLocalStr			Error code string value
	 * @param elemTry				            An xsl:try element object instance, for which xsl:catch element 
	 *                                          needs to be found for error recovery.
	 * @return
	 */
	private ElemCatch getXslCatchElemToHandleException(TransformerImpl transformer, XPathContext xctxt, 
			                                                                        String exceptionErrCodeLocalStr, 
			                                                                        ElemTry elemTry, boolean isRaisedByFnError) {
		
		ElemCatch xslDesiredCatchElem = null;
		
		for (ElemTemplateElement t = elemTry.m_firstChild; t != null;
																t = t.m_nextSibling) 
		{
			if (t instanceof ElemCatch) {				
				ElemCatch elemCatch = (ElemCatch)t;
				QName[] qNameArr = elemCatch.getErrors();
				if ((qNameArr == null) || ((qNameArr.length == 1) && "*".equals((qNameArr[0]).getLocalName()))) {
					/**
					 * This xsl:catch element doesn't have an attribute "errors",
					 * or an attribute "errors" value is "*". Therefore, this xsl:catch
					 * element is a suitable recovery handler for this exception.
					 */
					xslDesiredCatchElem = elemCatch;
					
					break;
				}
				else {
					for (int idx = 0; idx < qNameArr.length; idx++) {
						QName xslCatchErrQName = qNameArr[idx];
						String xslCatchErrorNs = xslCatchErrQName.getNamespace();
						if (xslCatchErrorNs == null) {
							if (!isRaisedByFnError) {
								continue;
							}
							else if (exceptionErrCodeLocalStr.equals(xslCatchErrQName.getLocalName())) {
								xslDesiredCatchElem = elemCatch;

								break; 
							}
						}
						
						if (exceptionErrCodeLocalStr.equals(xslCatchErrQName.getLocalName()) && 
								                                                             (Constants.XSL_ERROR_NAMESACE).equals(xslCatchErrorNs)) {
							/**
							 * An error code of an XPath dynamic error, matches with this 
							 * xsl:catch's error declaration. Therefore, this xsl:catch 
							 * element is a suitable recovery handler for this exception.
							 */
							xslDesiredCatchElem = elemCatch;
							
							break;
						}
					}
				}
			}
			
			if (xslDesiredCatchElem != null) {
			    break;	
			}
		}

		if (xslDesiredCatchElem == null) {
			/**
			 * None of the xsl:catch elements within this xsl:try element
			 * were able to process the recovery of an XSL dynamic
			 * exception. We'll attempt to find an xsl:catch element if
			 * available within this xsl:try's ancestor scope (i.e, any other
			 * xsl:try ancestor element that can handle this exception).
			 */
			ElemTemplateElement parentElem = elemTry.getParentElem();
			while ((parentElem != null) && !(parentElem instanceof ElemTry)) {
			   parentElem = parentElem.getParentElem();
			}
			
			if ((parentElem != null) && (parentElem instanceof ElemTry)) {
				xslDesiredCatchElem = getXslCatchElemToHandleException(transformer, xctxt, exceptionErrCodeLocalStr, 
						                                                                                          (ElemTry)parentElem, false);
			}
		}
		
		return xslDesiredCatchElem;
	}

}
