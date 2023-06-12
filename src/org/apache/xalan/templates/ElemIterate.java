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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformErrorLocatorHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.IntStack;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;

/**
 * XSLT 3.0 xsl:iterate element.
 * 
   <xsl:iterate select = expression>
      <!-- Content: (xsl:param*, xsl:on-completion?, sequence-constructor) -->
   </xsl:iterate>
   
   <xsl:next-iteration>
      <!-- Content: (xsl:with-param*) -->
   </xsl:next-iteration>

   <xsl:break select? = expression>
      <!-- Content: sequence-constructor -->
   </xsl:break>

   <xsl:on-completion select? = expression>
      <!-- Content: sequence-constructor -->
   </xsl:on-completion>
         
   @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * Implementation of the XSLT 3.0 xsl:iterate instruction.
 */
public class ElemIterate extends ElemTemplateElement implements ExpressionOwner
{

     private static final long serialVersionUID = -2692900882677332482L;
     
     private static final String OTHER_ELEM = "OTHER_ELEM";

     /**
      * Construct an element representing xsl:iterate.
      */
     public ElemIterate() {}

     /**
      * The "select" expression.
      */
     protected Expression m_selectExpression = null;

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
        * Execute the xsl:iterate transformation.
        *
        * @param transformer non-null reference to the the current transform-time state.
        *
        * @throws TransformerException
       */
       public void execute(TransformerImpl transformer) throws TransformerException
       {
           transformSelectedNodes(transformer);
       }

       /**
       * @param transformer              non-null reference to the the current transform-time state.
       *
       * @throws TransformerException    Thrown in a variety of circumstances.
       * 
       * @xsl.usage advanced
       */
       public void transformSelectedNodes(TransformerImpl transformer) throws 
                                                                 TransformerException {
    
           final XPathContext xctxtOriginal = transformer.getXPathContext();
        
           XPathContext xctxt = transformer.getXPathContext();
         
           final int sourceNode = xctxt.getCurrentNode();
           
           validateXslElemIterateChildElementsSequence(xctxt);
        
           DTMIterator sourceNodes = m_selectExpression.asIterator(xctxt, sourceNode);
        
           try {               
               xctxt.pushCurrentNode(DTM.NULL);

               IntStack currentNodes = xctxt.getCurrentNodeStack();

               xctxt.pushCurrentExpressionNode(DTM.NULL);

               IntStack currentExpressionNodes = xctxt.getCurrentExpressionNodeStack();

               xctxt.pushSAXLocatorNull();
               xctxt.pushContextNodeList(sourceNodes);
               transformer.pushElemTemplateElement(null);                              
           
               int docID = sourceNode & DTMManager.IDENT_DTM_DEFAULT;
               int child;
               
               ElemIterateOnCompletion xslOnCompletionTemplate = null;
               
               while ((child = sourceNodes.nextNode()) != DTM.NULL) {
                   currentNodes.setTop(child);
                   currentExpressionNodes.setTop(child);

                   if ((child & DTMManager.IDENT_DTM_DEFAULT) != docID)
                   {
                       docID = child & DTMManager.IDENT_DTM_DEFAULT;
                   }                                  
                   
                   for (ElemTemplateElement elemTemplate = this.m_firstChild; 
                                                          elemTemplate != null; 
                                                          elemTemplate = elemTemplate.m_nextSibling) {
                       if ((elemTemplate instanceof ElemIterateOnCompletion) && 
                                                                        (xslOnCompletionTemplate == null)) {
                           xslOnCompletionTemplate = (ElemIterateOnCompletion)elemTemplate;     
                       }
                       
                       if (!((XslTransformErrorLocatorHelper.isXslIterateBreakEvaluated).booleanValue())) {
                          xctxt.setSAXLocator(elemTemplate);
                          transformer.setCurrentElement(elemTemplate);
                          elemTemplate.execute(transformer);
                       }
                       else {
                          break;    
                       }
                   }
                   
                   if ((XslTransformErrorLocatorHelper.isXslIterateBreakEvaluated).booleanValue()) {                       
                       break;   
                   }
               }
               
               if ((xslOnCompletionTemplate != null) && !(XslTransformErrorLocatorHelper.
                                                                                isXslIterateBreakEvaluated).booleanValue()) {
                  XslTransformErrorLocatorHelper.isXslIterateOnCompletionActive = Boolean.TRUE;
                  xctxt.setSAXLocator(xslOnCompletionTemplate);
                  transformer.setCurrentElement(xslOnCompletionTemplate);
                  xslOnCompletionTemplate.execute(transformer);
                  XslTransformErrorLocatorHelper.isXslIterateOnCompletionActive = Boolean.FALSE;
               }
               
               XslTransformErrorLocatorHelper.isXslIterateBreakEvaluated = Boolean.FALSE;
           }
           finally {
              xctxt.popSAXLocator();
              xctxt.popContextNodeList();
              transformer.popElemTemplateElement();
              xctxt.popCurrentExpressionNode();
              xctxt.popCurrentNode();
              sourceNodes.detach();
           }
        
           // restore the xpath context, to where it was before this xsl:iterate 
           // instruction began an evaluation.
           transformer.setXPathContext(xctxtOriginal);        
      }
      
       /*
        * The XSLT 3.0 spec specifies constraints, about what should be the order of XSLT elements 
        * xsl:param, xsl:on-completion and xsl:next-iteration within the xsl:iterate element. This 
        * method ensures that, these XSLT xsl:iterate element constraints are validated during 
        * an XSLT document transformation.  
        */
      private void validateXslElemIterateChildElementsSequence(XPathContext xctxt) 
                                                                       throws TransformerException {
          
          List<String> xslElemNamesList = new ArrayList<String>();
          
          for (ElemTemplateElement elemTemplate = this.m_firstChild; 
                                                             elemTemplate != null; 
                                                             elemTemplate = elemTemplate.m_nextSibling) {
              if ((elemTemplate instanceof ElemUnknown) && ((Constants.ELEMNAME_PARAMVARIABLE_STRING).
                                                                                    equals(elemTemplate.getLocalName()))) {
                  // revisit.
                  // currently, 'elemTemplate instanceof ElemParam' is evaluated as false but 'elemTemplate 
                  // instanceof ElemUnknown' is evaluated as true when this "if" condition was evaluated.                  
                  xslElemNamesList.add(Constants.ELEMNAME_PARAMVARIABLE_STRING);
                  
                  // ElemUnknown elemUnknown = (ElemUnknown)elemTemplate;
                  // String nameVal = elemUnknown.getAttribute("name");
                  // String selectVal = elemUnknown.getAttribute("select");
                  // NodeList nodeList = elemUnknown.getChildNodes();
              }
              else if (elemTemplate instanceof ElemIterateOnCompletion) {
                  xslElemNamesList.add(Constants.ELEMNAME_ITERATE_ONCOMPLETION_STRING);   
              }
              else if (elemTemplate instanceof ElemIterateNextIteration) {
                  xslElemNamesList.add(Constants.ELEMNAME_ITERATE_NEXTITERATION_STRING);   
              }
              else {
                  xslElemNamesList.add(OTHER_ELEM);
              }
          }
          
          // get index of specific item's first occurrence with the list object 'xslElemNamesList'.
          // if a particular kind of xdm item that is checked is not present within the list object 
          // 'xslElemNamesList', its index is returned as -1.
          int paramIdx = xslElemNamesList.indexOf(Constants.ELEMNAME_PARAMVARIABLE_STRING);
          int onCompletionIdx = xslElemNamesList.indexOf(Constants.ELEMNAME_ITERATE_ONCOMPLETION_STRING);
          int nextIterationIdx = xslElemNamesList.indexOf(Constants.ELEMNAME_ITERATE_NEXTITERATION_STRING);
          int otherElemIdx = xslElemNamesList.indexOf(OTHER_ELEM);
          
          if ((paramIdx != -1) && (onCompletionIdx != -1) && (paramIdx > onCompletionIdx)) {
              throw new TransformerException("XTSE0010 : an xsl:param element must occur before xsl:on-completion "
                                                                                            + "element.", xctxt.getSAXLocator());    
          }          
          else if ((paramIdx != -1) && (nextIterationIdx != -1) && (paramIdx > nextIterationIdx)) {
              throw new TransformerException("XTSE0010 : an xsl:param element must occur before xsl:next-iteration "
                                                                                            + "element.", xctxt.getSAXLocator());
          }
          else if ((paramIdx != -1) && (otherElemIdx != -1) && (paramIdx > otherElemIdx)) {
              throw new TransformerException("XTSE0010 : an xsl:param element must occur before any other element within "
                                                                                     + "xsl:iterate element.", xctxt.getSAXLocator());
          }
          else if ((paramIdx != -1) && (otherElemIdx != -1) && (onCompletionIdx != -1) && (paramIdx < otherElemIdx) && 
                                                                                                 (otherElemIdx < onCompletionIdx)) {
              throw new TransformerException("XTSE0010 : an xsl:on-completion element must be the first child element of xsl:iterate "
                                                                                 + "after the xsl:param elements.", xctxt.getSAXLocator());
          }
          else if ((onCompletionIdx != -1) && (nextIterationIdx != -1) && (onCompletionIdx > nextIterationIdx)) {
              throw new TransformerException("XTSE0010 : an xsl:on-completion element must occur before xsl:next-iteration "
                                                                                                   + "element.", xctxt.getSAXLocator());
          }
          else if ((nextIterationIdx != -1) && (nextIterationIdx != (xslElemNamesList.size() - 1))) {
              throw new TransformerException("XTSE3120 : an xsl:next-iteration element when present, must be the last instruction within "
                                                                                         + "an xsl:iterate loop.", xctxt.getSAXLocator());
          }
      }
      
}
