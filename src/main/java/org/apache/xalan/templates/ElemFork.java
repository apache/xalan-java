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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.SAXException;

/**
 * Implementation of an XSLT 3.0 instruction xsl:fork.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemFork extends ElemTemplateElement implements ExpressionOwner
{
  
  private static final long serialVersionUID = 6132007133719632871L;
  
  /**
   * An XPath expression for XSL attribute "use-when". 
   */
  private XPath m_useWhen = null;
  
  /**
   * Class constructor.
   */
  public ElemFork() {
	  // No op
  }
  
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
   * @return			     XPath expression for attribute "use-when"
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
   * @return           The token id for this element
   */
  public int getXSLToken()
  {
      return Constants.ELEMNAME_FORK;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_FORK_STRING;
  }

  /**
   * Evaluation of xsl:fork transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
	    
	    XPathContext xctxt = transformer.getXPathContext();
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	    try {	    	
	    	transformer.pushCurrentTemplateRuleIsNull(true);

	    	if (transformer.getDebug()) {
	    		transformer.getTraceManager().emitTraceEvent(this);
	    	}
	    	
	    	/**
	    	 * An xsl:fork instruction may have zero or more xsl:sequence child
	    	 * instructions, or one xsl:for-each-group instruction.
	    	 * 
	    	 * i.e, An xsl:fork instruction, content model is following:
	    	 * 
	    	 * xsl:sequence* | xsl:for-each-group
	    	 */
	    	
	    	ElemTemplateElement xslForkChildElem = getFirstChildElem();	    		    	
	    		    	
	    	if (!(xslForkChildElem instanceof ElemForEachGroup)) {
	    		/**
	    		 * Evaluate xsl:fork's xsl:sequence child instructions in parallel 
	    		 * threads, and emit results of xsl:sequence instructions in order 
	    		 * as specified within an XSL stylesheet.
	    		 */
	    		
	    		List<XslSequenceAndResultPair> xslSequenceEvalResultList = new ArrayList<XslSequenceAndResultPair>();
	    		
	    		List<XslForkSequenceCallable> xslForkTaskCallableList = new ArrayList<XslForkSequenceCallable>();

	    		int xslSeqElemCount = 0;	    		
	    		for (ElemTemplateElement t = this.m_firstChild; t != null;
																		t = t.m_nextSibling) {
	    			xslSeqElemCount++;
	    		}
	    		
	    		/**
	    		 * Instantiate java.util.concurrent.ExecutorService, to run 
	    		 * evaluation of each xsl:sequence instruction (XSL stylesheet 
	    		 * sibling instructions, that are child of xsl:fork instruction) 
	    		 * within a separate thread. 
	    		 */
	    		
	    		ExecutorService threadpool = Executors.newFixedThreadPool((Runtime.getRuntime()).availableProcessors());	    		
	    		
	    		for (ElemTemplateElement t = this.m_firstChild; t != null;
	    																  t = t.m_nextSibling) {
	    			ElemSequence elemSequence = (ElemSequence)t;
	    			elemSequence.setIsCalledFromXslFork(true);   
	    			
	    			XslForkSequenceCallable xslForkWithSequenceCallable = new XslForkSequenceCallable(elemSequence, transformer, xctxt);
	    			xslForkTaskCallableList.add(xslForkWithSequenceCallable);
	    		}
	    		
	    		while (xslSequenceEvalResultList.size() != xslSeqElemCount) {
	    			int size1 = xslForkTaskCallableList.size();
	    			
	    			for (int idx = 0; idx < size1; idx++) {
	    				XslForkSequenceCallable xslForkSequenceCallable = xslForkTaskCallableList.get(idx);
	    				Future<XslSequenceAndResultPair> futureObj = threadpool.submit(xslForkSequenceCallable);
	    				
	    				try { 
	    				   XslSequenceAndResultPair resultObj1 = futureObj.get(Constants.XSL_FORK_SEQ_RUN_TIMEOUT, TimeUnit.SECONDS);
	    				   xslSequenceEvalResultList.add(resultObj1);
	    				} 
	    				catch (InterruptedException ex) {
	    					throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
									    							                                        + "an XSL 'sequence' instruction within, XSL 'fork' instruction, "
									    							                                        + "with following error trace : " + ex.getMessage() + ".", srcLocator);
	    				} 
	    				catch (ExecutionException ex) {
	    					throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
																		                                    + "an XSL 'sequence' instruction within, XSL 'fork' instruction, "
																		                                    + "with following error trace : " + ex.getMessage() + ".", srcLocator);
	    				} 
	    				catch (TimeoutException ex) {
	    					throw new javax.xml.transform.TransformerException("XTDE1665 : An XSL stylesheet dynamic error has occured. An xsl:fork's xsl:sequence instruction "
	    							                                                                        + "took too long to run.", srcLocator);
						}
	    			}
	    		}
	    		
	    		for (int idx = 0; idx < xslSequenceEvalResultList.size(); idx++) {
	    			XslSequenceAndResultPair xslSequenceAndResultPair = xslSequenceEvalResultList.get(idx);	    			
	    			
	    			try {
	    				ElemSequence elemSequence = xslSequenceAndResultPair.getElemSequence();
						elemSequence.emitXdmItemToXSLResultTree(xctxt, transformer, 
								                                         xslSequenceAndResultPair.getEvalResult());
								                                         
					} 
	    			catch (TransformerException ex) {
	    				throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
																		                                + "an XSL 'sequence' instruction within, XSL 'fork' instruction, "
																		                                + "with following error trace : " + ex.getMessage() + ".", srcLocator);
					} 
	    			catch (SAXException ex) {
	    				throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
																		                                + "an XSL 'sequence' instruction within, XSL 'fork' instruction, "
																		                                + "with following error trace : " + ex.getMessage() + ".", srcLocator);
					}
	    		}
	    		
	    		threadpool.shutdown();
	    	}
	    	else {
	    		// Evaluate an xsl:for-each-group instruction
	    		
	    		xctxt.setSAXLocator(xslForkChildElem);
    			transformer.setCurrentElement(xslForkChildElem);                   
    			xslForkChildElem.execute(transformer);	
	    	}	    	
        }
        finally {
            if (transformer.getDebug()) {
    	       transformer.getTraceManager().emitTraceEndEvent(this);
            }
            
            transformer.popCurrentTemplateRuleIsNull();
        }
  }
  
  /**
   * Class definition, to represent an xsl:sequence 
   * evaluation task.
   */
  class XslForkSequenceCallable implements Callable<XslSequenceAndResultPair> {	  	 
	  	  	  
	  private ElemSequence m_elemSequence;
	  
	  private TransformerImpl m_transformer;
	  
	  private XPathContext m_xctxt;
	  
	  private XslSequenceAndResultPair m_xslSeqAndResultPair;
	  
	  /**
	   * Class constructor.
	   */
	  public XslForkSequenceCallable(ElemSequence elemSequence, TransformerImpl transformer, 
			                                                        XPathContext xctxt) {		 
		 this.m_elemSequence = elemSequence;
		 this.m_transformer = transformer;
		 this.m_xctxt = xctxt;		 
	  }
	  
	  /**
	   * Method definition, to represent the workload of a 
	   * task to be run by java.util.concurrent.ExecutorService.
	   */
	  public XslSequenceAndResultPair call() {		  
		  
		  try {				  
			  m_xctxt.setSAXLocator(m_elemSequence);
			  m_transformer.setCurrentElement(m_elemSequence);                   
			  m_elemSequence.execute(m_transformer);
			  XObject xslSequenceEvalResult = m_elemSequence.getXslSequenceEvalResult();
			  m_xslSeqAndResultPair = new XslSequenceAndResultPair(m_elemSequence, xslSequenceEvalResult);
			  
			  return m_xslSeqAndResultPair;
		  }
		  catch (TransformerException ex) {
			  // No op
		  }
		  
		  return null;
	  }
	  
	  public ElemSequence getElemSequence() {
		  return m_elemSequence;
	  }

	  public void setElemSequence(ElemSequence elemSequence) {
		  this.m_elemSequence = elemSequence;
	  }

	  public TransformerImpl getTransformer() {
		  return m_transformer;
	  }

	  public void setTransformer(TransformerImpl transformer) {
		  this.m_transformer = transformer;
	  }

	  public XPathContext getXPathContext() {
		  return m_xctxt;
	  }

	  public void setXPathContext(XPathContext xctxt) {
		  this.m_xctxt = xctxt;
	  }
	  
	  public XslSequenceAndResultPair getXslSequenceEvalResult() {
		 return m_xslSeqAndResultPair; 
	  }	  
  }
  
  /**
   * Class definition, to represent a pair of values, 
   * comprising of an xsl:sequence instruction instance and 
   * an XObject object, instance which is the result of 
   * xsl:sequence instruction evaluation.
   */
  class XslSequenceAndResultPair {
	  
	  private ElemSequence m_elemSequence;
	  
	  private XObject m_evalResult;
	  
	  /**
	   * Class constructor.
	   */
	  public XslSequenceAndResultPair(ElemSequence elemSequence, XObject xObj) {
		  this.m_elemSequence = elemSequence;
		  this.m_evalResult = xObj;
	  }

	  public ElemSequence getElemSequence() {
		  return m_elemSequence;
	  }

	  public void setElemSequence(ElemSequence elemSequence) {
		  this.m_elemSequence = elemSequence;
	  }

	  public XObject getEvalResult() {
		  return m_evalResult;
	  }

	  public void setEvalResult(XObject xObject) {
		  this.m_evalResult = xObject;
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

	  ElemTemplateElement result = null;

	  ElemTemplateElement elemTemplateElement = getFirstChildElem();

	  if (elemTemplateElement == null) {
		  result = super.appendChild(newChild);  
	  }
	  else if ((elemTemplateElement instanceof ElemSequence) && (newChild instanceof ElemSequence)) {
		  result = super.appendChild(newChild);  
	  }
	  else {		 
		  int lineNo = newChild.getLineNumber();
		  int colNo = newChild.getColumnNumber();

		  error(XSLTErrorResources.ER_XSL_FORK, new Object[] { "'" + newChild.getNodeName() + "'", lineNo, colNo }); 
	  }

	  return result;
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
	  return null;
  }

  @Override
  public void setExpression(Expression exp) {
	  // No op
  }

}
