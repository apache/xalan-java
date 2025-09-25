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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.operations.UnaryOperation;
import org.xml.sax.SAXException;

/**
 * Implementation of the XSLT 3.0 xsl:on-completion instruction.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemIterateOnCompletion extends ElemTemplateElement implements ExpressionOwner
{

     private static final long serialVersionUID = 7800963884351737333L;

     /**
      * Construct an element representing xsl:on-completion.
      */
     public ElemIterateOnCompletion() {}
     
     private String xpathSelectPatternStr;

     /**
      * The "select" expression.
      */
     protected Expression m_selectExpression = null;

     public void setSelect(XPath xpath)
     {
         m_selectExpression = xpath.getExpression();
         xpathSelectPatternStr = xpath.getPatternString();
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
           return Constants.ELEMNAME_ITERATE_ONCOMPLETION;
       }

       /**
         * Return the node name.
         *
         * @return The element's name
       */
       public String getNodeName()
       {
           return Constants.ELEMNAME_ITERATE_ONCOMPLETION_STRING;
       }

       /**
        * Execute the xsl:on-completion transformation.
        *
        * @param transformer non-null reference to the the current transform-time state.
        *
        * @throws TransformerException
       */
       public void execute(TransformerImpl transformer) throws TransformerException
       {
           transformXslOncompletionInstruction(transformer);
       }

       /**
       * @param transformer              non-null reference to the the current transform-time state.
       *
       * @throws TransformerException    Thrown in a variety of circumstances.
       * 
       * @xsl.usage advanced
       */
       private void transformXslOncompletionInstruction(TransformerImpl transformer) 
                                                                                  throws TransformerException {
    	   
    	   XPathContext xctxt = transformer.getXPathContext();        	   
    	   SourceLocator srcLocator = xctxt.getSAXLocator();     	   
                                                                  
           if (transformer.isXslIterateOnCompletionActive()) {        	          	   
        	   if ((xpathSelectPatternStr != null) && (this.m_firstChild != null)) {
        		  throw new TransformerException("XTSE3125 : An xsl:on-completion element has both 'select' attribute and a non empty sequence constructor.", srcLocator); 
        	   }        	   
                                                                           
               final int prevCurrentNode = xctxt.getCurrentNode();
               final int prevCurrExprNode = xctxt.getCurrentExpressionNode();
               
               try {
            	   // During evaluation of xsl:on-completion instruction, an
                   // XPath context item is absent.
            	   xctxt.pushCurrentNode(DTM.NULL);
                   xctxt.pushCurrentExpressionNode(DTM.NULL);
                   
            	   if (xpathSelectPatternStr != null) {
            		   if (isXslIterOnCompletionExprAccXPathCtxt(m_selectExpression)) {
        				  throw new TransformerException("XPDY0002 : An xsl:on-completion instruction's 'select' attribute "
        				  		                                                                         + "cannot access XPath context item.", srcLocator);   
        			   }
            		   
            		   SerializationHandler rth = transformer.getResultTreeHandler();

            		   try {
            			   m_selectExpression.executeCharsToContentHandler(xctxt, rth);
            		   } 
            		   catch (TransformerException ex) {
            			   throw ex;
            		   } 
            		   catch (SAXException ex) {
            			   throw new TransformerException(ex);
            		   }
            	   }
            	   else {
            		   for (ElemTemplateElement elemTemplate = this.m_firstChild; elemTemplate != null; 
            				                                                      elemTemplate = elemTemplate.m_nextSibling) {            			   
            			   if (isXslIterOnCompletionDescInstrAccXPathCtxt(elemTemplate)) {
            				  throw new TransformerException("XPDY0002 : An xsl:on-completion instruction's sequence constructor "
            				  		                                                                    + "cannot access XPath context item.", elemTemplate); 
            			   }
            			   xctxt.setSAXLocator(elemTemplate);
            			   transformer.setCurrentElement(elemTemplate);
            			   elemTemplate.execute(transformer);
            		   } 
            	   }
               }
               finally {
            	   xctxt.pushCurrentNode(prevCurrentNode);
                   xctxt.pushCurrentExpressionNode(prevCurrExprNode);
               }
           }                      
       }
       
       /**
        * Method definition, to check whether an XPath expression accesses
        * context item.
        * 
        * @param expr							An XPath compiled expression reference
        * @return								Boolean value true if an XPath expression 
        *                                       accesses context item, otherwise false.
        */
       private boolean isXslIterOnCompletionExprAccXPathCtxt(Expression expr) {
    	   
    	   boolean result = false;
    	   
    	   if (expr != null) {
    		   if (expr instanceof SelfIteratorNoPredicate) {
    			   result = true; 
    		   }
    		   else if (expr instanceof Operation) {
    			   Operation opn1 = (Operation)expr;
    			   Expression lOp = opn1.getLeftOperand();
    			   Expression rOp = opn1.getRightOperand();
    			   result = isXslIterOnCompletionExprAccXPathCtxt(lOp);
    			   if (!result) {
    				  result = isXslIterOnCompletionExprAccXPathCtxt(rOp); 
    			   }
    		   }
    		   else if (expr instanceof UnaryOperation) {
    			   UnaryOperation opn1 = (UnaryOperation)expr;
    			   Expression rOp = opn1.getExpression();
    			   result = isXslIterOnCompletionExprAccXPathCtxt(rOp);
    		   }
    	   }
    	   
    	   return result;
       }
       
       /**
        * Method definition, to check whether an XSL stylesheet element and 
        * any of its descendant elements below xsl:on-completion element, access 
        * XPath context item via stylesheet element's 'select' attribute.
        * 
        * @param elemTemplate							An XSL stylesheet element reference
        * @return                                       Boolean value true if an XPath expression 
        *                                               accesses context item, otherwise false.
        */
       private boolean isXslIterOnCompletionDescInstrAccXPathCtxt(ElemTemplateElement elemTemplate) {
    	   
    	   boolean result = false;    	       	   
    	   
    	   if (elemTemplate != null) {
    		   Object obj1 = elemTemplate.getSelect();
    		   Expression expr1 = null;
    		   if (obj1 != null) {    		   
    			   if (obj1 instanceof XPath) {
    				   expr1 = ((XPath)obj1).getExpression();  
    			   }
    			   else if (obj1 instanceof Expression) {
    				   expr1 = (Expression)obj1; 
    			   }
    		   }

    		   if ((expr1 != null) && !(expr1 instanceof XObject)) {
    			   result = isXslIterOnCompletionExprAccXPathCtxt(expr1);    			   
    			   if (!result) {
    				   ElemTemplateElement elemTemplate2 = elemTemplate;  
    				   for (elemTemplate2 = elemTemplate2.m_firstChild; elemTemplate2 != null; elemTemplate2 = elemTemplate2.m_nextSibling) {
    					   result = isXslIterOnCompletionDescInstrAccXPathCtxt(elemTemplate2);    						   
    					   if (result) {
    						   break;
    					   }
    					   else {
    						   elemTemplate2 = elemTemplate2.m_firstChild;
    						   result = isXslIterOnCompletionDescInstrAccXPathCtxt(elemTemplate2);
    						   if (result) {
    							   break;	 
    						   } 
    					   }
    				   }
    			   }
    		   }
    		   else {
    			   ElemTemplateElement elemTemplate2 = elemTemplate;  
    			   for (elemTemplate2 = elemTemplate2.m_firstChild; elemTemplate2 != null; elemTemplate2 = elemTemplate2.m_nextSibling) {
    				   result = isXslIterOnCompletionDescInstrAccXPathCtxt(elemTemplate2);    						   
    				   if (result) {
    					   break;
    				   }
    				   else {
    					   elemTemplate2 = elemTemplate2.m_firstChild;
    					   result = isXslIterOnCompletionDescInstrAccXPathCtxt(elemTemplate2);
    					   if (result || (elemTemplate2 == null)) {
    						   break;	 
    					   } 
    				   }
    			   } 
    		   }
    	   }
    	   
    	   return result;
       }
      
}
