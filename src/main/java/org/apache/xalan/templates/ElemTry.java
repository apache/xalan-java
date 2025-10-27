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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.Source;
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
	 * The class constructor.
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
	 * @return The token ID for this element
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
	    
	    int contextNode = xctxt.getContextNode();
	    
	    if (m_selectExpression != null) {
	    	for (ElemTemplateElement t1 = this.m_firstChild; t1 != null;
																	t1 = t1.m_nextSibling) {
	    		if (!(t1 instanceof ElemCatch)) {
	    		   throw new TransformerException("XTSE3140 : An XSL 'try' element has 'select' attribute, but XSL 'try' "
	    		   		                                                                 + "has a child element other than XSL 'catch'.", srcLocator);
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
	    	throw new TransformerException("XTSE3140 : An XSL 'try' element's last child element can only be a "
	    			                                                                           + "XSL 'catch' element, or an XSL 'fallback' instruction.", srcLocator);
	    }	    
	    
	    if (m_selectExpression != null) {
	    	// An XSL processing specified by xsl:try element is
	    	// been done by xsl:try element's 'select' attribute.	    	
	    	try {	    			    		
	    		if (m_vars != null) {
	    			m_selectExpression.fixupVariables(m_vars, m_globals_size);
	    		}

	    		m_selectExpression.setIsConcreteExceptionProcessing(true);
	    		XObject xpathEvalResult = m_selectExpression.execute(xctxt, contextNode, xctxt.getNamespaceContext());
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
	    	// An XSL processing specified by xsl:try element is
	    	// been done by xsl:try element's contained sequence
	    	// constructor.    		
	    	for (ElemTemplateElement t1 = this.m_firstChild; t1 != null; t1 = t1.m_nextSibling) {
	    		if (!(t1 instanceof ElemCatch)) {
	    			xctxt.setSAXLocator(t1);
	    			transformer.setCurrentElement(t1);
	    			try {
	    				transformer.transformToRTF(t1);	    				
	    				t1.execute(transformer);
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
		// NO OP
		return null;
	}

	@Override
	public void setExpression(Expression exp) {
		// NO OP
	}
	
	/**
	 * During processing within xsl:try element when a run-time exception 
	 * javax.xml.transform.TransformerException has occurred, handle the run-time
	 * exception with an applicable xsl:catch element.
	 * 
	 * If a suitable xsl:catch element cannot be found to handle a run-time exception, 
	 * an original exception is thrown to the client code as if XSL stylesheet
	 * processing took place without using xsl:try instruction.
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
			String errCodeLocalStr = null;
			if (colonIdx != -1) {
				errCodeLocalStr = errMesg.substring(colonIdx + 1);
				colonIdx = errCodeLocalStr.indexOf(':');
				if (colonIdx != -1) {
					errCodeLocalStr = (errCodeLocalStr.substring(0, colonIdx)).trim();
				}
				else {
					colonIdx = errMesg.indexOf(':');
					errCodeLocalStr = (errMesg.substring(0, colonIdx)).trim();
				}
			}
			else {
				errCodeLocalStr = errMesg;
			}

			ElemCatch elemCatch = getXslCatchElemToHandleException(transformer, xctxt, errCodeLocalStr, this);						

			if (elemCatch != null) {				
				Source trfSource = transformer.getSource();
				String xslTrfSystemId = trfSource.getSystemId();
				String xslTrfModuleStr = null;
				if (xslTrfSystemId != null) {
					try {					
						URI xslStylesheetUri = new URI(xslTrfSystemId);
						xslTrfModuleStr = xslStylesheetUri.toString(); 
					}
					catch (URISyntaxException ex2) {
						File file = new File(xslTrfSystemId);
						xslTrfModuleStr = (file.toURI()).toString();
					}
				}
				
				String errMesg2 = null;				
				if (errCodeLocalStr.length() > 8) {
					colonIdx = errMesg.indexOf(':');					
					errCodeLocalStr = (errMesg.substring(0, colonIdx)).trim();
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
				xpathVarMap.put(errCodeKey, new XSQName(errCodePrfxStr, errCodeLocalStr, Constants.XSL_ERROR_NAMESACE));
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
	 * Given an xsl:try element and a run-time error code string value, find the 
	 * xsl:catch element that should perform an XSL transformation's error 
	 * recovery.
	 * 
	 * @param transformer			An XSL transformation TransformerImpl object instance
	 * @param xctxt					An XPath context object 
	 * @param errCodeStr			Error code string value
	 * @param elemTry				An xsl:try element object instance, for which xsl:catch element 
	 *                              needs to be found for error recovery.
	 * @return
	 */
	private ElemCatch getXslCatchElemToHandleException(TransformerImpl transformer, XPathContext xctxt, 
			                                                                                  String errCodeStr, ElemTry elemTry) {
		
		ElemCatch xslDesiredCatchElem = null;
		
		for (ElemTemplateElement t1 = elemTry.m_firstChild; t1 != null;
																t1 = t1.m_nextSibling) 
		{
			if (t1 instanceof ElemCatch) {				
				ElemCatch elemCatch = (ElemCatch)t1;
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
						boolean noNsErrorCode = !errCodeStr.contains(":") ? true : false;
						String xslCatchErrorNs = xslCatchErrQName.getNamespace();
						if (errCodeStr.equals(xslCatchErrQName.getLocalName()) && (Constants.XSL_ERROR_NAMESACE.equals(xslCatchErrorNs) || 
								                                                   ((xslCatchErrorNs == null) && noNsErrorCode))) {
							/**
							 * An error code of an XPath dynamic error, matches
							 * with this xsl:catch's error declaration. Therefore, this
							 * xsl:catch element is a suitable recovery handler for this
							 * exception.
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
				xslDesiredCatchElem = getXslCatchElemToHandleException(transformer, xctxt, errCodeStr, (ElemTry)parentElem);
			}
		}
		
		return xslDesiredCatchElem;
	}

}
