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
import java.util.Map;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

/**
 * Implementation of the XSLT 3.0 xsl:iterate instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemIterate extends ElemTemplateElement implements ExpressionOwner
{

     private static final long serialVersionUID = -2692900882677332482L;
     
     private static final String OTHER_ELEM = "OTHER_ELEM";
     
     public static List<XslIterateParamWithparamData> fXslIterateParamWithparamDataList = new 
                                                                             ArrayList<XslIterateParamWithparamData>();

     /**
      * Construct an element representing xsl:iterate.
      */
     public ElemIterate() {}

     /**
      * The "select" expression.
      */
     protected Expression m_selectExpression = null;
     
     /**
      * Class field to store, XPath expression for subsequent 
      * processing.
      */
     protected XPath m_xpath = null;
     
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

     public void setSelect(XPath xpath)
     {
         m_selectExpression = xpath.getExpression();
         m_xpath = xpath;
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
      * @see ExpressionOwner#setExpression(Expression)
      */
     public void setExpression(Expression exp)
     {
         exp.exprSetParent(this);
         m_selectExpression = exp;
     }
     
     /**
      * @see ExpressionOwner#getExpression()
     */
     public Expression getExpression()
     {
         return m_selectExpression;
     }

     /**
      * Variable to indicate whether, an attribute 'expand-text'
      * is declared on xsl:iterate instruction.
      */
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
     
     /**
      * Get a boolean value indicating whether, an "expand-text" 
      * attribute has been declared. 
      */
     public boolean getExpandTextDeclared() {
   	     return m_expand_text_declared;
     }
     
     /**
     * This function is called after everything else has been recomposed, 
     * and allows the template to set remaining values that may be based 
     * on some other property that depends on recomposition.
     *
     * @throws TransformerException
     */
     public void compose(StylesheetRoot sroot) throws TransformerException {
         super.compose(sroot);

         java.util.Vector vnames = sroot.getComposeState().getVariableNames();

         if (m_selectExpression != null) {             
             m_selectExpression.fixupVariables(vnames, sroot.getComposeState().
                                                                  getGlobalsSize());
         }
         else {
             m_selectExpression = getStylesheetRoot().m_selectDefault.
                                                               getExpression();
         }
      }
  
      /**
       * This after the template's children have been composed.
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
           return Constants.ELEMNAME_ITERATE;
       }

       /**
         * Return the node name.
         *
         * @return The element's name
       */
       public String getNodeName()
       {
          return Constants.ELEMNAME_ITERATE_STRING;
       }

       /**
        * Execute an xsl:iterate transformation.
        *
        * @param transformer non-null reference to the the current transform-time state.
        *
        * @throws TransformerException
       */
       public void execute(TransformerImpl transformer) throws TransformerException
       {
           transformSelectedXdmItems(transformer);
       }
       
       /**
        * Method definition, to process an xdm input sequence/array, or an XML node set
        * with xsl:iterate instruction.
        * 
        * @param transformer									Xalan-J's XSL transformation object
        * @throws TransformerException
        */
       private void transformSelectedXdmItems(TransformerImpl transformer) throws TransformerException {
        
           XPathContext xctxt = transformer.getXPathContext();                       
         
           final int sourceNode = xctxt.getCurrentNode();
           
           SourceLocator srcLocator = xctxt.getSAXLocator();
           
           xctxt.setPos(0);
           xctxt.setLast(0);
           
           if (m_xpath_default_namespace != null) {    		
        	   m_xpath = new XPath(m_xpath.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);        	   
        	   m_selectExpression = m_xpath.getExpression();
           }
           
           // Clear the, xsl:iterate->xsl:param list storage before this xsl:iterate 
           // instruction's evaluation.
           fXslIterateParamWithparamDataList.clear();
           
           verifyXslElemIterateChildElementsSequence(xctxt);
           
           XObject inpXObject = null;
           
           if (m_selectExpression instanceof LocPathIterator) {
        	   LocPathIterator locPathIter = (LocPathIterator)m_selectExpression;
        	   
        	   Function func = locPathIter.getFuncExpr();
        	   XPathDynamicFunctionCall dfc = locPathIter.getDynamicFuncCallExpr();
               
               DTMCursorIterator dtmIter = null;
               try {
                   dtmIter = locPathIter.asIterator(xctxt, sourceNode);
               }
               catch (ClassCastException ex) {
                   // no op
               }
               
               if (dtmIter != null) {
            	   ResultSequence resultSeq = null;
            	   if (func != null) {            		   
            		   resultSeq = new ResultSequence();
            		   int nextNode;
            		   while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
            		   {
            			   XMLNodeCursorImpl xdmNodeObj = new XMLNodeCursorImpl(nextNode, xctxt);
            			   // Evaluate an XPath expression like /a/b/funcCall(..).
       					   // Find one result item for a sequence of items.            			   
            			   XObject evalResult = evaluateXPathSuffixFunction(xctxt, srcLocator, func, xdmNodeObj);
            			   resultSeq.add(evalResult);
            		   }
            		   
            		   inpXObject = resultSeq;
            	   }
            	   else if (dfc != null) {            		   
            		   resultSeq = new ResultSequence();
            		   int nextNode;
            		   while ((nextNode = dtmIter.nextNode()) != DTM.NULL)
            		   {
            			   XMLNodeCursorImpl xdmNodeObj = new XMLNodeCursorImpl(nextNode, xctxt);
            			   // Evaluate an XPath expression like /a/b/$funcCall(..).
       					   // Find one result item for a sequence of items.            			   
            			   XObject evalResult = evaluateXPathSuffixDfc(xctxt, dfc, xdmNodeObj);
            			   resultSeq.add(evalResult);
            		   }
            		   
            		   inpXObject = resultSeq;
            	   }
               }
           }
           
           if (inpXObject == null) {
              inpXObject = m_selectExpression.execute(xctxt);
           }
           
           List<XObject> itemsToBeProcessed = null;
               
           if ((inpXObject instanceof ResultSequence) || (inpXObject instanceof XPathArray)) {
        	   if (inpXObject instanceof ResultSequence) {
        		   itemsToBeProcessed = ((ResultSequence)inpXObject).getResultSequenceItems();
        	   }
        	   else {
        		   XPathArray xpathArr = (XPathArray)inpXObject;
        		   itemsToBeProcessed = xpathArr.getNativeArray();
        	   }
                   
               ElemIterateOnCompletion xslOnCompletionTemplate = null;
                   
               boolean isBreakFromXslContentLoop = false;
               
               final int prevContextSize = xctxt.getXPath3ContextSize();
               final XObject prevCtxtItem = xctxt.getXPath3ContextItem();
               final int prevCtxtPosition = xctxt.getXPath3ContextPosition();
               
               try {
            	   int inpSeqLength = itemsToBeProcessed.size();
            	   if (inpSeqLength == 0) {
            		   /**
            		    * We do little static type checking (like, variable references which
            		    * are not in scope) on xsl:on-completion instruction, even if there
            		    * are no input items to be processed.
            		    */
            		   for (ElemTemplateElement elemTemplate = this.m_firstChild; elemTemplate != null; 
            				                                                      elemTemplate = elemTemplate.m_nextSibling) {
            			   if (elemTemplate instanceof ElemIterateOnCompletion) {            			                                                       
            				   xctxt.setXPath3ContextSize(-1);
            				   xctxt.setXPath3ContextItem(null);
            				   xctxt.setXPath3ContextPosition(-1);

            				   xslOnCompletionTemplate = (ElemIterateOnCompletion)elemTemplate; 
            				   transformer.setXslIterateOnCompletionActive(true);
            				   xctxt.setSAXLocator(xslOnCompletionTemplate);
            				   transformer.setCurrentElement(xslOnCompletionTemplate);

            				   // This evaluates xsl:on-completion instruction, but doesn't emit 
            				   // the result to an XSL transformation output.
            				   int nodeHandle = transformer.transformToRTF(this);                          

            				   transformer.setXslIterateOnCompletionActive(false);

            				   return;
            			   }
            		   }            	               	   
            	   }

            	   for (int idx = 0; idx < inpSeqLength; idx++) {
            		   XObject resultSeqItem = itemsToBeProcessed.get(idx);

            		   if (resultSeqItem instanceof XMLNodeCursorImpl) {
            			   resultSeqItem = ((XMLNodeCursorImpl)resultSeqItem).getFresh(); 
            		   }

            		   setXPathContextForXslSequenceProcessing(inpSeqLength, idx, resultSeqItem, xctxt);                                                                     
            		   for (ElemTemplateElement t = this.m_firstChild; t != null; t = t.m_nextSibling) {
            			   if ((idx == 0) && (t instanceof ElemParam)) {
            				   ElemParam elemParam = (ElemParam)t;
            				   QName paramQname = elemParam.getName();
            				   Map<QName, XObject> varMap = xctxt.getXPathVarMap();
            				   if (varMap.get(paramQname) != null) {
            					  varMap.put(paramQname, null); 
            				   }
            			   }
            			   
            			   if ((t instanceof ElemIterateOnCompletion) && (xslOnCompletionTemplate == null)) {
            				   xslOnCompletionTemplate = (ElemIterateOnCompletion)t;     
            			   }
            			   else if (!transformer.isXslIterateBreakEvaluated()) {
            				   xctxt.setSAXLocator(t);
            				   transformer.setCurrentElement(t);
            				   t.execute(transformer); 
            			   }
            			   else {                              
            				   resetXPathContextForXslSequenceProcessing(resultSeqItem, xctxt);                               
            				   isBreakFromXslContentLoop = true;                               
            				   break;    
            			   }
            		   }

            		   if (!isBreakFromXslContentLoop) {                         
            			   resetXPathContextForXslSequenceProcessing(resultSeqItem, xctxt);  
            		   }
            		   else {                          
            			   break;
            		   }
            	   }                                    

            	   if ((xslOnCompletionTemplate != null) && !transformer.isXslIterateBreakEvaluated()) {
            		   xctxt.setXPath3ContextSize(-1);
                	   xctxt.setXPath3ContextItem(null);
                	   xctxt.setXPath3ContextPosition(-1);
                	   
            		   transformer.setXslIterateOnCompletionActive(true);
            		   xctxt.setSAXLocator(xslOnCompletionTemplate);
            		   transformer.setCurrentElement(xslOnCompletionTemplate);
            		   xslOnCompletionTemplate.execute(transformer);
            		   transformer.setXslIterateOnCompletionActive(false);
            	   }

            	   transformer.setXslIterateBreakEvaluated(false);
               }
               finally {
            	   xctxt.setXPath3ContextSize(prevContextSize);
            	   xctxt.setXPath3ContextItem(prevCtxtItem);
            	   xctxt.setXPath3ContextPosition(prevCtxtPosition);
               }
           }
           else {
               // Evaluate xsl:iterate instruction, when value of its "select" attribute evaluates 
               // to a node set.
        	   
        	   Map<QName, XObject> varMap = xctxt.getXPathVarMap();
        	   
               DTMCursorIterator sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode); 
        
               XObject prevContextItem = xctxt.getXPath3ContextItem();
               int prevContextPos = xctxt.getXPath3ContextPosition();
               int prevContextSize = xctxt.getXPath3ContextSize();
               
               try {                          	               	                                            
                   ElemIterateOnCompletion xslOnCompletionTemplate = null;
                   int nodeSetLength = sourceNodes.getLength();

                   int iterCount = 0;
                   int nextNode;
                   while ((nextNode = sourceNodes.nextNode()) != DTM.NULL) { 
                	  iterCount++;
                      
                      for (ElemTemplateElement t = this.m_firstChild; t != null; t = t.m_nextSibling) {                    	  
                    	  if ((iterCount == 1) && (t instanceof ElemParam)) {
                    		  ElemParam elemParam = (ElemParam)t;
                    		  QName paramQname = elemParam.getName();
                    		  varMap = xctxt.getXPathVarMap();
                    		  if (varMap.get(paramQname) != null) {
                    			  varMap.put(paramQname, null); 
                    		  }
                    	  }
                    	  
                    	  if (!(t instanceof ElemParam)) {
                    		 xctxt.setXPath3ContextItem(null);
                    		 xctxt.setXPath3ContextPosition(iterCount);
                    		 xctxt.setXPath3ContextSize(nodeSetLength);                    		 
                    	     xctxt.pushCurrentNode(nextNode);
                    	  }
                    	  
                    	  if ((t instanceof ElemIterateOnCompletion) && (xslOnCompletionTemplate == null)) {
                              xslOnCompletionTemplate = (ElemIterateOnCompletion)t;     
                          }
                          else if (!transformer.isXslIterateBreakEvaluated()) {
                        	  if ((iterCount == 1) && (t instanceof ElemParam)) {
                        		 ElemParam elemParam = (ElemParam)t;
                        		 QName qName = elemParam.getName();
                        		 XObject paramValue = elemParam.getValue(transformer, sourceNode);
                        		 varMap.put(qName, paramValue);
                        	  }
                        	  else {
                        	     xctxt.setSAXLocator(t);
                        	     transformer.setCurrentElement(t);                        		  
                        	     t.execute(transformer);
                        	  }
                          }
                          else {                        	  
                              break;    
                          }
                      }                                      
                   
                      if (transformer.isXslIterateBreakEvaluated()) {
                          break;   
                      }                                            
                  }
               
                  if ((xslOnCompletionTemplate != null) && !transformer.isXslIterateBreakEvaluated()) {
                	   xctxt.setXPath3ContextItem(prevContextItem);
                 	   xctxt.setXPath3ContextPosition(prevContextPos);
             		   xctxt.setXPath3ContextSize(prevContextSize);
                       xctxt.pushCurrentNode(sourceNode);
                       transformer.setXslIterateOnCompletionActive(true);
                       xctxt.setSAXLocator(xslOnCompletionTemplate);
                       transformer.setCurrentElement(xslOnCompletionTemplate);
                       xslOnCompletionTemplate.execute(transformer);
                       transformer.setXslIterateOnCompletionActive(false);
                  }
               
                  transformer.setXslIterateBreakEvaluated(false);
             }
             finally {
            	 xctxt.setXPath3ContextItem(prevContextItem);
            	 xctxt.setXPath3ContextPosition(prevContextPos);
            	 xctxt.setXPath3ContextSize(prevContextSize);
            	 xctxt.popCurrentNode();
            	 xctxt.pushCurrentNode(sourceNode);            	 
            	 sourceNodes.detach();

            	 // Clear xsl:iterate xsl:param variable storage
            	 for (int idx = 0; idx < fXslIterateParamWithparamDataList.size(); idx++) {
            		 XslIterateParamWithparamData xslIterateParamWithparamData = fXslIterateParamWithparamDataList.get(idx);
            		 QName varQName = xslIterateParamWithparamData.getName();
            		 varMap.remove(varQName);
            	 }
             }
          }          
          
      }
      
       /**
        * Method definition, to check XSLT 3.0 static constraints for various XSL elements 
        * within xsl:iterate instruction.
        * 
        * @param xctxt								    An XPath context object
        * @throws TransformerException
        */
      private void verifyXslElemIterateChildElementsSequence(XPathContext xctxt) 
                                                                       throws TransformerException {
          
    	  List<String> xslElemNamesList = new ArrayList<String>();

    	  SourceLocator srcLocator = xctxt.getSAXLocator();

    	  for (ElemTemplateElement elemTemplate = this.m_firstChild; elemTemplate != null; elemTemplate = elemTemplate.m_nextSibling) {
    		  if (elemTemplate instanceof ElemParam) {                  
    			  xslElemNamesList.add(Constants.ELEMNAME_PARAMVARIABLE_STRING);
    		  }
    		  else if (elemTemplate instanceof ElemIterateOnCompletion) {
    			  xslElemNamesList.add(Constants.ELEMNAME_ITERATE_ONCOMPLETION_STRING);   
    		  }
    		  else if (elemTemplate instanceof ElemUnknown) {
    			  throw new TransformerException("XTSE0010 : An XSL stylesheet element occurs at an incorrect "
															    					  + "position within xsl:iterate instruction (for e.g, "
															    					  + "xsl:variable followed by xsl:param).", srcLocator);
    		  }
    		  else {
    			  xslElemNamesList.add(OTHER_ELEM);
    		  }
    	  }
    	  
    	  List<String> errMesgList = new ArrayList<String>();
    	  verifyXslNextIterAndBreakConstraints(this, errMesgList);
    	  int errMesgListCount = errMesgList.size();
    	  if (errMesgListCount > 0) {
    		  StringBuffer strBuff = new StringBuffer();
    		  for (int idx = 0; idx < errMesgListCount; idx++) {
    			  String errMesg = errMesgList.get(idx);
    			  if (idx < (errMesgListCount - 1)) {
    				  strBuff.append(errMesg + "\n");
    			  }
    			  else {
    				  strBuff.append(errMesg);
    			  }
    		  }
    		  
    		  String errMesgStr = strBuff.toString();
    		  throw new TransformerException(errMesgStr, srcLocator);
    	  }

    	  /**
    	   * Get index of XSLT stylesheet specific element(s)'s, first occurrence within the list object
    	   * 'xslElemNamesList'. If a particular kind of XSLT stylesheet element that is checked, is
    	   * not present within the list object 'xslElemNamesList', its index is returned as -1.
    	   */
    	  int paramIdx = xslElemNamesList.indexOf(Constants.ELEMNAME_PARAMVARIABLE_STRING);
    	  int onCompletionIdx = xslElemNamesList.indexOf(Constants.ELEMNAME_ITERATE_ONCOMPLETION_STRING);          
    	  int otherElemIdx = xslElemNamesList.indexOf(OTHER_ELEM);

    	  if ((paramIdx != -1) && (onCompletionIdx != -1) && (paramIdx > onCompletionIdx)) {
    		  throw new TransformerException("XTSE0010 : An xsl:param element must occur before xsl:on-completion element.", srcLocator);    
    	  }                    
    	  else if ((paramIdx != -1) && (otherElemIdx != -1) && (paramIdx > otherElemIdx)) {
    		  throw new TransformerException("XTSE0010 : An xsl:param element must occur before any other element within xsl:iterate element.", srcLocator);
    	  }
    	  else if ((paramIdx != -1) && (otherElemIdx != -1) && (onCompletionIdx != -1) && (paramIdx < otherElemIdx) && (otherElemIdx < onCompletionIdx)) {
    		  throw new TransformerException("XTSE0010 : An xsl:on-completion element must be the first child element of xsl:iterate after the xsl:param elements.", srcLocator);
    	  }          

    	  if (paramIdx != -1) {
    		  for (ElemTemplateElement elemTemplate = this.m_firstChild; elemTemplate != null; 
    				  elemTemplate = elemTemplate.m_nextSibling) {
    			  if (elemTemplate instanceof ElemParam) {
    				  ElemParam paramElem = (ElemParam)elemTemplate;
    				  QName paramNameVal = paramElem.getName();
    				  XPath paramSelectXPath = paramElem.getSelect();
    				  XslIterateParamWithparamData paramWithparamDataObj = new XslIterateParamWithparamData();
    				  paramWithparamDataObj.setName(paramNameVal);
    				  paramWithparamDataObj.setSelect(paramSelectXPath);
    				  if (fXslIterateParamWithparamDataList.contains(paramWithparamDataObj)) {
    					  throw new TransformerException("XTSE0580 : The name of an xsl:param '" + paramNameVal + "' is not unique within xsl:iterate.", srcLocator);    
    				  }
    				  else {
    					  fXslIterateParamWithparamDataList.add(paramWithparamDataObj);    
    				  }
    			  }                                    
    		  }
    	  }          
    }

    /**
     * Method definition, to verify within an xsl:iterate instruction, that xsl:next-iteration 
     * and xsl:break instructions are in tail position of xsl:iterate's sequence constructor, 
     * and few other XSL stylesheet xsl:iterate constraints as well.
     * 
     * @param elemTemplateElem						      An object representing an xsl:iterate 
     *                                                    instruction, or another XSL instruction 
     *                                                    within xsl:iterate.
     * @param errMesgList                                 A list of error message strings, populated
     *                                                    by this method.                                                     
     * @throws TransformerException
     */
	private void verifyXslNextIterAndBreakConstraints(ElemTemplateElement elemTemplateElem, List<String> errMesgList) throws TransformerException {				
		
		while (elemTemplateElem != null) {			
		    if (elemTemplateElem instanceof ElemIterateBreak) {
		    	if (!isXslInstructionInTailPositionOfSequenceConstructor(elemTemplateElem)) {
		    	   String errMesg = "XTSE3120 : An xsl:break instruction is not in a tail position of xsl:iterate's sequence constructor.";
		    	   if (!errMesgList.contains(errMesg)) {
		    	      errMesgList.add(errMesg);
		    	   }
		    	}
		    	else if ((elemTemplateElem.getParentElem() instanceof ElemLiteralResult) || (elemTemplateElem.getParentElem() instanceof ElemElement)) {
		    	   String errMesg = "XTSE3120 : An xsl:break instruction cannot occur as child of XSL element constructor.";
		    	   if (!errMesgList.contains(errMesg)) {
		    	      errMesgList.add(errMesg);
		    	   }
		    	}
		    	else if (elemTemplateElem.getParentElem() instanceof ElemForEach) {
			       String errMesg = "XTSE3120 : An xsl:break instruction cannot occur as child of xsl:for-each instruction.";
		    	   if (!errMesgList.contains(errMesg)) {
		    	      errMesgList.add(errMesg);
		    	   }
			    }
		    }
		    else if ((elemTemplateElem instanceof ElemIterateNextIteration) && !isXslInstructionInTailPositionOfSequenceConstructor(elemTemplateElem)) {
		    	String errMesg = "XTSE3120 : An xsl:next-iteration instruction is not in a tail position of xsl:iterate's sequence constructor.";
		    	if (!errMesgList.contains(errMesg)) {
		    	   errMesgList.add(errMesg);
		    	}
		    }
		    else if (elemTemplateElem instanceof ElemIterateOnCompletion) {
		    	if (!(elemTemplateElem.getParentElem() instanceof ElemIterate)) {
		    		String errMesg = "XTSE0010 : An xsl:on-completion element can only occur as child of xsl:iterate instruction, after zero or more xsl:param elements.";
		    		if (!errMesgList.contains(errMesg)) {
		    			errMesgList.add(errMesg);
		    		}
		    	}
		    }
		    
		    ElemTemplateElement elem = elemTemplateElem.getFirstChildElem();
		    while (elem != null) {
		       verifyXslNextIterAndBreakConstraints(elem, errMesgList);
		       elem = elem.getNextSiblingElem();
		    }
		    
		    elemTemplateElem = elemTemplateElem.getNextSiblingElem();
		}  
	}
      
}
