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
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XRTreeFrag;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Implementation of the XSLT 3.0 xsl:next-iteration instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#element-iterate
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemIterateNextIteration extends ElemTemplateElement implements ExpressionOwner
{
    
     private static final long serialVersionUID = -582877657433106548L;
     
     public static List<XslIterateParamWithparamData> fWithparamList = new ArrayList<XslIterateParamWithparamData>();

     /**
      * Construct an element representing xsl:next-iteration.
      */
     public ElemIterateNextIteration() {}

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
           return Constants.ELEMNAME_ITERATE_NEXTITERATION;
       }

       /**
         * Return the node name.
         *
         * @return The element's name
       */
       public String getNodeName()
       {
           return Constants.ELEMNAME_ITERATE_NEXTITERATION_STRING;
       }

       /**
        * Execute the xsl:next-iteration transformation.
        *
        * @param transformer non-null reference to the the current transform-time state.
        *
        * @throws TransformerException
       */
       public void execute(TransformerImpl transformer) throws TransformerException
       {
           
            XPathContext xpathContext = transformer.getXPathContext();
            
            if (!isXslNextIterationDescendantOfXslIterate(this)) {
                throw new TransformerException("XTSE3120 : An xsl:next-iteration instruction doesn't "
                                                                                + "have xsl:iterate instruction as an ancestor.", 
                                                                                      xpathContext.getSAXLocator()); 
            }
            
            if (isXslInstructionInTailPositionOfSequenceConstructor(this)) {
                elemIterateNextIterationProcessing(transformer);
            }
            else {
                throw new TransformerException("XTSE3120 : an xsl:next-iteration instruction is not in a "
                                                                                           + "tail position within the sequence constructor of currently "
                                                                                           + "active xsl:iterate instruction.", xpathContext.getSAXLocator());   
            }
       }
       
       /**
        * @param transformer              non-null reference to the the current transform-time state.
        *
        * @throws TransformerException    Thrown in a variety of circumstances.
        * 
        * @xsl.usage advanced
        */
        private void elemIterateNextIterationProcessing(TransformerImpl transformer) throws 
                                                                                 TransformerException {
            
            XPathContext xctxt = transformer.getXPathContext();
            
            int contextNode = xctxt.getContextNode();
            
            // Clear the, xsl:next-iteration->xsl:with-param* list storage before
            // evaluating this xsl:next-iteration element.
            fWithparamList.clear();
            
            NodeList nextIterationChildNodes = this.getChildNodes();                        
            
            for (int idx = 0; idx < nextIterationChildNodes.getLength(); idx++) {
                Node nextIterChild = nextIterationChildNodes.item(idx);
                if (nextIterChild instanceof ElemWithParam) {
                    ElemWithParam withParamElem = (ElemWithParam)nextIterChild;
                    QName withParamNameVal = withParamElem.getName();
                    XPath withParamSelectXPath = withParamElem.getSelect();                              
                    XslIterateParamWithparamData paramWithparamDataObj = new XslIterateParamWithparamData();
                    paramWithparamDataObj.setNameVal(withParamNameVal);
                    paramWithparamDataObj.setSelectVal(withParamSelectXPath);
                    if (fWithparamList.contains(paramWithparamDataObj)) {
                        throw new TransformerException("XTSE0670 : duplicate xsl:with-param parameter name '" + withParamNameVal + 
                                                                                                            "'", xctxt.getSAXLocator());   
                    }
                    else {
                        fWithparamList.add(paramWithparamDataObj);  
                    }
                }
            }
            
            if ((ElemIterate.fXslIterateParamWithparamDataList).size() != fWithparamList.size()) {
                throw new TransformerException("XTSE0580 : within xsl:iterate, the number of xsl:param elements are not equal to "
                                                                + "number of xsl:next-iteration's xsl:with-param elements.", xctxt.getSAXLocator());     
            }
            else {
               for (int idx = 0; idx < (ElemIterate.fXslIterateParamWithparamDataList).size(); idx ++) {
                   XslIterateParamWithparamData paramData = (ElemIterate.fXslIterateParamWithparamDataList).get(idx);
                   XslIterateParamWithparamData withParamData = fWithparamList.get(idx);
                   if (!(paramData.getNameVal()).equals(withParamData.getNameVal())) {
                       throw new TransformerException("XTSE3130 : within xsl:iterate, xsl:param and xsl:with-param names at position " + 
                                                                                                  (idx + 1) + " are not same.", xctxt.getSAXLocator());        
                   }
               }
            }
                                    
            Map<QName, XObject> varMap = xctxt.getXPathVarMap();
            
            for (int idx = 0; idx < fWithparamList.size(); idx++) {
                XObject withParamVal = null;
                
                XslIterateParamWithparamData withParamData = fWithparamList.get(idx);                                                
                QName xslParamQName = withParamData.getNameVal();
                
                ElemWithParam elemWithParam = getElemWithParamForQName(xslParamQName);
                
                XPath withParamSelectVal = withParamData.getSelectVal();                
                if (withParamSelectVal != null) {
                   // Evaluate xsl:with-param's value, from its "select" attribute
                   withParamVal = withParamSelectVal.execute(xctxt, contextNode, this);
                }
                else {
                   // Evaluate xsl:with-param's value, from the sequence constructor
                   // provided as content within xsl:with-param element.                   
                   int rootNodeHandleOfRtf = transformer.transformToRTF(elemWithParam);
                   NodeList nodeList = (new XRTreeFrag(rootNodeHandleOfRtf, xctxt, 
                                                                                elemWithParam)).convertToNodeset();                
                   withParamVal = new XNodeSetForDOM(nodeList, xctxt);
                }
                
                String withParamAsAttr = elemWithParam.getAs();
                if (withParamAsAttr != null) {
                   try {
                      withParamVal = SequenceTypeSupport.convertXdmValueToAnotherType(withParamVal, withParamAsAttr, null, 
                                                                                                                 transformer.getXPathContext());
                   }
                   catch (TransformerException ex) {
                      if (withParamSelectVal != null) {
                         throw ex;  
                      }
                      else {
                         throw new TransformerException("XTTE0570 : The value of sequence constructor, supplied within xsl:with-param element cannot "
                                                                                                   + "be cast to the type " + withParamAsAttr, xctxt.getSAXLocator());  
                      }
                   }
                }
                
                varMap.put(xslParamQName, withParamVal);
            }
        }
        
        /**
         * Given value of name for, xsl:next-iteration's xsl:with-param element,
         * find corresponding reference of stylesheet element xsl:with-param.
         * 
         * We find this information, by traversing the stylesheet child elements
         * of xsl:next-iteration element.
         */
        private ElemWithParam getElemWithParamForQName(QName xslWithparamQName) {
           ElemWithParam elemWithParam = null;
           
           ElemTemplateElement elemTemplateElement = getFirstChildElem();
           
           while (elemTemplateElement != null) {
              ElemWithParam elemWithParamTemp = (ElemWithParam)elemTemplateElement;
              if ((elemWithParamTemp.getName()).equals(xslWithparamQName)) {
                 elemWithParam = elemWithParamTemp;
                 break;
              }
              else {
                 elemTemplateElement = elemTemplateElement.getNextSiblingElem(); 
              }
           }
           
           return elemWithParam;
        }
        
        /*
         * Determine whether, an xsl:next-iteration instruction has xsl:iterate instruction 
         * as ancestor. 
         */
        private boolean isXslNextIterationDescendantOfXslIterate(ElemIterateNextIteration 
                                                                                    xslNextInstr) {
            
            boolean isXslNextIterationDescendantOfXslIterate = false;
            
            ElemTemplateElement xslParentElement = xslNextInstr.m_parentNode;
            
            while (xslParentElement != null) {
               if (xslParentElement instanceof ElemIterate) {
                   isXslNextIterationDescendantOfXslIterate = true;
                   break;
               }
               else {
                   xslParentElement = xslParentElement.m_parentNode;    
               }
            }
            
            return  isXslNextIterationDescendantOfXslIterate;   
        }
      
}
