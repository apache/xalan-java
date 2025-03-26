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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.xml.sax.SAXException;

/**
 * Implementation of the XSLT 3.0 xsl:fork instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#fork-instruction
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemFork extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  private static final long serialVersionUID = 6132007133719632871L;
  
  /**
   * The class constructor.
   */
  public ElemFork() {}

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
   * Execute the xsl:fork transformation.
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
	    	
	    	// An xsl:fork instruction has zero or more xsl:sequence sibling child 
	    	// elements, or has xsl:for-each-group as child element.
	    	// i.e, xsl:fork instruction XSL child content has following grammar,
	    	// (xsl:sequence* | xsl:for-each-group).
	    	// We validate this xsl:fork's grammar definition below.
	    	
	    	ElemTemplateElement xslForkLastChildElem = null;
	    	
	    	for (ElemTemplateElement t = this.m_firstChild; t != null;
																	t = t.m_nextSibling) {
	    		if (t.m_nextSibling != null) {
	    		   if (!(t instanceof ElemSequence)) {
	    			  throw new TransformerException("XPTY0004 : All xsl:fork child elements other than last one, "
	    			  																				 + "should be xsl:sequence elements.", srcLocator); 
	    		   }
	    		}
	    		else if (!((t instanceof ElemSequence) || (t instanceof ElemForEachGroup))) {
	    		   throw new TransformerException("XPTY0004 : An xsl:fork's last child element can either be "
	    		   		                                                                                + "xsl:sequence or xsl:for-each-group.", srcLocator);
	    		}
	    		else {
	    		   xslForkLastChildElem = t;	
	    		}
	    	}
	    		    	
	    	if (!(xslForkLastChildElem instanceof ElemForEachGroup)) {
	    		// Evaluate xsl:fork's xsl:sequence child elements in parallel threads, 
	    		// and emit xsl:fork's evaluation result to an XSL transformation serializer 
	    		// in a sequential order as per xsl:sequence element siblings (i.e, first 
	    		// xsl:sequence's evaluation result will be emitted first, then for the 
	    		// second xsl:sequence sibling element and so on.
	    		
	    		List<XslSequenceAndResultPair> xslSequenceEvalResultList = new ArrayList<XslSequenceAndResultPair>();
	    		
	    		List<XslForkWithSequenceCallable> xslForkTaskCallableList = new ArrayList<XslForkWithSequenceCallable>();

	    		// Determine the number of xsl:fork's xsl:sequence child elements, by 
	    		// analyzing an XSL stylesheet's compiled object statically.
	    		int noOfXslSeqElems = 0;	    		
	    		for (ElemTemplateElement t = this.m_firstChild; t != null;
																		t = t.m_nextSibling) {
	    			noOfXslSeqElems++;
	    		}
	    		
	    		// Instantiate java.util.concurrent.ExecutorService, with thread pool size 
	    		// equal to the number of xsl:sequence sibling instructions within 
	    		// xsl:fork element.
	    		ExecutorService executorServiceObj = Executors.newFixedThreadPool(noOfXslSeqElems);	    		
	    		
	    		for (ElemTemplateElement t = this.m_firstChild; t != null;
	    																t = t.m_nextSibling) {
	    			ElemSequence elemSequence = (ElemSequence)t;
	    			elemSequence.setIsCalledFromXslFork(true);   
	    			XslForkWithSequenceCallable xslForkWithSequenceCallable = new 
	    																XslForkWithSequenceCallable(xctxt, transformer, elemSequence);
	    			xslForkTaskCallableList.add(xslForkWithSequenceCallable);
	    		}	    			    		
	    		
	    		while (xslSequenceEvalResultList.size() != noOfXslSeqElems) {
	    			for (int idx = 0; idx < xslForkTaskCallableList.size(); idx++) {
	    				XslForkWithSequenceCallable xslForkWithSequenceCallable = xslForkTaskCallableList.get(idx);
	    				Future<XslSequenceAndResultPair> futureObj = executorServiceObj.submit(xslForkWithSequenceCallable);
	    				try { 
	    				   XslSequenceAndResultPair resultObj1 = futureObj.get();
	    				   xslSequenceEvalResultList.add(resultObj1);
	    				} 
	    				catch (InterruptedException ex) {
	    					throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
	    							                                        + "xsl:sequence instruction within xsl:fork instruction, "
	    							                                        + "with following error trace : " + ex.getMessage() + ".", srcLocator);
	    				} 
	    				catch (ExecutionException ex) {
	    					throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
	    							                                        + "xsl:sequence instruction within xsl:fork instruction, "
	    							                                        + "with following error trace : " + ex.getMessage() + ".", srcLocator);
	    				}
	    			}
	    		}
	    		
	    		for (int idx = 0; idx < xslSequenceEvalResultList.size(); idx++) {
	    			XslSequenceAndResultPair xslSequenceAndResultPair = xslSequenceEvalResultList.get(idx);	    			
	    			try {
	    				ElemSequence elemSequence = xslSequenceAndResultPair.getElemSequence();
						elemSequence.emitXslSequenceResultToSerializer(xctxt, transformer, 
								                                         xslSequenceAndResultPair.getEvalResult());
								                                         
					} catch (TransformerException ex) {
						throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
                                													+ "xsl:sequence instruction within xsl:fork instruction, "
                                													+ "with following error trace : " + ex.getMessage() + ".", srcLocator);
					} catch (SAXException ex) {
						throw new javax.xml.transform.TransformerException("XPTY0004 : An error occured while evaluating "
																					+ "xsl:sequence instruction within xsl:fork instruction, "
																					+ "with following error trace : " + ex.getMessage() + ".", srcLocator);
					}
	    		}
	    		
	    		executorServiceObj.shutdown();
	    	}
	    	else {	     	    		    	
	    		xctxt.setSAXLocator(xslForkLastChildElem);
    			transformer.setCurrentElement(xslForkLastChildElem);                   
    			xslForkLastChildElem.execute(transformer);	
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
	  return null;
  }

  @Override
  public void setExpression(Expression exp) {
	  // NO OP
  }
  
  /**
   * An object of this class, represents an xsl:sequence evaluation task.
   * Multiple such task objects shall be provided to 
   * java.util.concurrent.ExecutorService to run in parallel.
   */
  class XslForkWithSequenceCallable implements Callable<XslSequenceAndResultPair> {
	  
	  private XPathContext xctxt;
	  
	  private TransformerImpl transformer;
	  
	  private ElemSequence elemSequence;
	  
	  private XslSequenceAndResultPair xslSequenceAndResultPair;
	  
	  /**
	   * Class constructor.
	   */
	  public XslForkWithSequenceCallable(XPathContext xctxt, TransformerImpl transformer, 
			                                                              ElemSequence elemSequence) {
		 this.xctxt = xctxt;
		 this.transformer = transformer;
		 this.elemSequence = elemSequence;  
	  }
	  
	  /**
	   * This method definition represents the workload of a 
	   * task to be run by java.util.concurrent.ExecutorService.
	   */
	  public XslSequenceAndResultPair call() {		  
		  try {				  
			  xctxt.setSAXLocator(elemSequence);
			  transformer.setCurrentElement(elemSequence);                   
			  elemSequence.execute(transformer);
			  XObject xslSequenceEvalResult = elemSequence.getXslSequenceEvalResult();
			  xslSequenceAndResultPair = new XslSequenceAndResultPair(elemSequence, xslSequenceEvalResult);
			  
			  return xslSequenceAndResultPair;
		  }
		  catch (TransformerException ex) {
			  // NO OP
		  }
		  
		  return null;
	  }

	  public XPathContext getXPathContext() {
		 return xctxt;
	  }

	  public void setXPathContext(XPathContext xctxt) {
		 this.xctxt = xctxt;
	  }

	  public TransformerImpl getTransformer() {
		 return transformer;
	  }

	  public void setTransformer(TransformerImpl transformer) {
		 this.transformer = transformer;
	  }
	  
	  public ElemSequence getElemSequence() {
		 return elemSequence;
	  }

	  public void setElemSequence(ElemSequence elemSequence) {
		 this.elemSequence = elemSequence;
	  }
	  
	  public XslSequenceAndResultPair getXslSequenceEvalResult() {
		 return xslSequenceAndResultPair; 
	  }	  
  }
  
  /**
   * An object is of this class represents a pair of values, 
   * comprising of an xsl:sequence instruction instance and 
   * an XObject object instance representing result of it's 
   * evaluation.
   */
  class XslSequenceAndResultPair {
	  
	  private ElemSequence elemSequence;
	  
	  private XObject evalResult;
	  
	  public XslSequenceAndResultPair(ElemSequence elemSequence, XObject xObj) {
		  this.elemSequence = elemSequence;
		  this.evalResult = xObj;
	  }

	  public ElemSequence getElemSequence() {
		  return elemSequence;
	  }

	  public void setElemSequence(ElemSequence elemSequence) {
		  this.elemSequence = elemSequence;
	  }

	  public XObject getEvalResult() {
		  return evalResult;
	  }

	  public void setEvalResult(XObject xObject) {
		  this.evalResult = xObject;
	  }
  }

}
