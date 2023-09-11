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
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.XSConstructorFunctionUtil;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.xs.types.XSAnyType;
import org.xml.sax.SAXException;

/**
 * The XPath 3.1 simple map '!' operation.
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
       ResultSequence result = new ResultSequence();
       
       SourceLocator srcLocator = xctxt.getSAXLocator(); 
       
       XObject expr1 = null; 
               
       if (m_left instanceof FuncExtFunction) {
           FuncExtFunction extFunction = (FuncExtFunction)m_left;
           if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(extFunction.getNamespace())) {
               try {
                   expr1 = XSConstructorFunctionUtil.processFuncExtFunctionOrXPathOpn(xctxt, m_left, null);
               }
               catch (TransformerException ex) {
                   throw ex; 
               }
               catch (SAXException ex) {
                   throw new TransformerException(ex.getMessage(), srcLocator);
               } 
           }
           else {
              expr1 = m_left.execute(xctxt, true);  
           }
       }
       else {
           expr1 = m_left.execute(xctxt, true); 
       }
       
       if (expr1 instanceof XNodeSet) {
           XNodeSet xsObjNodeSet = (XNodeSet)expr1;
           DTMIterator dtmIter = xsObjNodeSet.iterRaw();
           
           int contextNode;
           
           while ((contextNode = dtmIter.nextNode()) != DTM.NULL) {
              xctxt.pushCurrentNode(contextNode);
              XObject xsObj = m_right.execute(xctxt, contextNode);
              result.add(xsObj);
              xctxt.popCurrentNode();              
           } 
       }
       else if (expr1 instanceof ResultSequence) {
           ResultSequence rSeq = (ResultSequence)expr1;
           for (int idx = 0; idx < rSeq.size(); idx++) {
              XObject xObj = rSeq.item(idx);
              if ((xObj instanceof XSAnyType) || (xObj instanceof XBoolean) || 
                  (xObj instanceof XNumber) || (xObj instanceof XString)) {
                  xctxt.setXPath3ContextItem(xObj);
                  XObject xsObj1 = m_right.execute(xctxt, DTM.NULL);
                  result.add(xsObj1);
                  xctxt.setXPath3ContextItem(null);                  
              }
              else if (xObj instanceof XNodeSet) {
                  int contextNode = ((XNodeSet)xObj).getCurrentNode();
                  XObject xsObj = m_right.execute(xctxt, contextNode);
                  result.add(xsObj);
              }
           }
       }
       else {
           // we're assuming here that, the XObject object instance expr1
           // represents a singleton value.
           xctxt.setXPath3ContextItem(expr1);
           XObject xsObj = m_right.execute(xctxt, DTM.NULL);
           result.add(xsObj);
           xctxt.setXPath3ContextItem(null);
       }
       
       return result; 
   }

}
