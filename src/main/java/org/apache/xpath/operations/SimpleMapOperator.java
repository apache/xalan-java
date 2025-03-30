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
package org.apache.xpath.operations;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyType;

/**
 * An implementation of XPath 3.1 simple map '!' operator.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-map-operator
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SimpleMapOperator extends Operation
{
    
   private static final long serialVersionUID = -1467842928587523219L;

   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {
       XObject result = null;        
       
       XObject expr1 = null; 
               
       if (m_left instanceof XSL3ConstructorOrExtensionFunction) {
           XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_left;
           if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
               try {
            	   XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
                   expr1 = xslFunctionService.callFunction(xpathFunc, null, xctxt);
               }
               catch (TransformerException ex) {
                   throw ex; 
               } 
           }
           else {
              expr1 = m_left.execute(xctxt, true);  
           }
       }
       else {
           expr1 = m_left.execute(xctxt, true); 
       }
       
       if (expr1 instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xsObjNodeSet = (XMLNodeCursorImpl)expr1;
           DTMCursorIterator dtmIter = xsObjNodeSet.iterRaw();
           
           int contextNode;           
           ResultSequence resultSeq = new ResultSequence();            
           while ((contextNode = dtmIter.nextNode()) != DTM.NULL) {
              xctxt.pushCurrentNode(contextNode);
              XObject xsObj = m_right.execute(xctxt, contextNode);
              resultSeq.add(xsObj);
              xctxt.popCurrentNode();              
           }
           
           result = resultSeq;
       }
       else if (expr1 instanceof ResultSequence) {
           ResultSequence inpSeq = (ResultSequence)expr1;
           ResultSequence resultSeq = new ResultSequence(); 
           for (int idx = 0; idx < inpSeq.size(); idx++) {
              XObject xObj = inpSeq.item(idx);
              if ((xObj instanceof XSAnyType) || (xObj instanceof XBoolean) || 
                  (xObj instanceof XNumber) || (xObj instanceof XString)) {
            	  // Make copy of few XPath context values
            	  XObject prevCtxtItem = xctxt.getXPath3ContextItem();
            	  int prevCtxtPosition = xctxt.getXPath3ContextPosition();
            	  int prevCtxtSize = xctxt.getXPath3ContextSize();
            	  // Set XPath context values, for XPath expression evaluation
                  xctxt.setXPath3ContextItem(xObj);
                  xctxt.setXPath3ContextPosition(idx + 1);
                  xctxt.setXPath3ContextSize(inpSeq.size());
                  XObject xsObj = m_right.execute(xctxt, DTM.NULL);
                  resultSeq.add(xsObj);
                  // Restore XPath context values
                  xctxt.setXPath3ContextItem(prevCtxtItem);
                  xctxt.setXPath3ContextPosition(prevCtxtPosition);
                  xctxt.setXPath3ContextSize(prevCtxtSize);
              }
              else if (xObj instanceof XMLNodeCursorImpl) {
                  int contextNode = ((XMLNodeCursorImpl)xObj).getCurrentNode();
                  xctxt.pushCurrentNode(contextNode);
                  XObject xsObj = m_right.execute(xctxt, contextNode);
                  resultSeq.add(xsObj);
                  xctxt.popCurrentNode();
              }
           }
           
           result = resultSeq;
       }
       else {
           // We're assuming here that, the XObject object instance expr1
           // represents a singleton value.
           xctxt.setXPath3ContextItem(expr1);
           XObject xsObj = m_right.execute(xctxt, DTM.NULL);
           result = xsObj;
           xctxt.setXPath3ContextItem(null);
       }
       
       return result; 
   }

}
