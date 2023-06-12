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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformErrorLocatorHelper;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.xml.sax.SAXException;

/**
 * XSLT 3.0 xsl:break element.
 * 
   <xsl:break select? = expression>
      <!-- Content: sequence-constructor -->
   </xsl:break>
         
   @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * Implementation of the XSLT 3.0 xsl:break instruction.
 * 
 * The XSLT xsl:break element is intended to be used, within xsl:iterate element.
 */
public class ElemIterateBreak extends ElemTemplateElement implements ExpressionOwner
{

     private static final long serialVersionUID = -7523525132174243558L;

     /**
      * Construct an element representing xsl:break.
      */
     public ElemIterateBreak() {}
     
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
           return Constants.ELEMNAME_ITERATE_BREAK;
       }

       /**
         * Return the node name.
         *
         * @return The element's name
       */
       public String getNodeName()
       {
           return Constants.ELEMNAME_ITERATE_BREAK_STRING;
       }

       /**
        * Execute the xsl:break transformation.
        *
        * @param transformer non-null reference to the the current transform-time state.
        *
        * @throws TransformerException
       */
       public void execute(TransformerImpl transformer) throws TransformerException
       {
           if (this.m_nextSibling != null) {
              XPathContext xpathContext = transformer.getXPathContext();
              throw new TransformerException("XTSE3120 : an xsl:break instruction must not have any other "
                                                                        + "stylesheet element as its following sibling.", 
                                                                                         xpathContext.getSAXLocator());    
           }
           else {              
              transformXslBreakInstruction(transformer);
              XslTransformErrorLocatorHelper.isXslIterateBreakEvaluated = Boolean.TRUE;
           }
       }

       /**
       * @param transformer              non-null reference to the the current transform-time state.
       *
       * @throws TransformerException    Thrown in a variety of circumstances.
       * 
       * @xsl.usage advanced
       */
       public void transformXslBreakInstruction(TransformerImpl transformer) throws 
                                                                 TransformerException {
           XPathContext xctxt = transformer.getXPathContext();
           
           if ((xpathSelectPatternStr != null) && (this.m_firstChild != null)) {
              throw new TransformerException("XTSE3125 : an xsl:break element with a 'select' attribute "
                                                                                 + "must be empty.", xctxt.getSAXLocator());          
           }
           else {               
              if (xpathSelectPatternStr != null) {
                  SerializationHandler rth = transformer.getResultTreeHandler();
                  
                  try {
                      m_selectExpression.executeCharsToContentHandler(xctxt, rth);
                  } catch (TransformerException ex) {
                      throw ex;
                  } catch (SAXException ex) {
                      throw new TransformerException(ex);
                  }
              }
              else {
                  for (ElemTemplateElement elemTemplate = this.m_firstChild; elemTemplate != null; 
                                                                      elemTemplate = elemTemplate.m_nextSibling) {
                     xctxt.setSAXLocator(elemTemplate);
                     transformer.setCurrentElement(elemTemplate);
                     elemTemplate.execute(transformer);
                  } 
              }
           }
       }
      
}
