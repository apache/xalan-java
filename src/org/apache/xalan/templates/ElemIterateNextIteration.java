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
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;

/**
 * XSLT 3.0 xsl:next-iteration element.
 *    
   <xsl:next-iteration>
      <!-- Content: (xsl:with-param*) -->
   </xsl:next-iteration>
         
   @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * Implementation of the XSLT 3.0 xsl:next-iteration instruction.
 * 
 * The XSLT xsl:next-iteration element is intended to be used, within 
 * xsl:iterate element.
 */
public class ElemIterateNextIteration extends ElemTemplateElement implements ExpressionOwner
{
    
     private static final long serialVersionUID = -582877657433106548L;

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
            // TO DO         
        }
      
}
