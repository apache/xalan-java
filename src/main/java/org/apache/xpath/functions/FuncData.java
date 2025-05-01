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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 function data().
 * 
 * Ref : https://www.w3.org/TR/xpath-functions-31/#func-data
 * 
 * This implementation of function fn:data() assumes that,
 * an XML input document has not been validated with an 
 * XML schema.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncData extends FunctionDef1Arg
{

  private static final long serialVersionUID = 1734069066834829751L;

  /**
   * Execute the function. The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
      ResultSequence result = new ResultSequence();
      
      XObject fnDataEffectiveArgValue = null;
      
      if (m_arg0 == null) {
         // Handles fn:data function call of the form, fn:data()
         XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
         if (xpath3ContextItem != null) {
            fnDataEffectiveArgValue = xpath3ContextItem;  
         }
         else {
            int contextNodeDtmHandle = xctxt.getContextNode();
            fnDataEffectiveArgValue = new XMLNodeCursorImpl(contextNodeDtmHandle, xctxt); 
         }
      }
      else {
         // An explicit argument was provided, to the fn:data 
         // function call.
         fnDataEffectiveArgValue = m_arg0.execute(xctxt); 
      }
      
      if (fnDataEffectiveArgValue instanceof XMLNodeCursorImpl) {
         XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)fnDataEffectiveArgValue;
         
         int nextNodeDtmHandle;
         DTMCursorIterator dtmIter = xNodeSet.iterRaw();
         while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {
            XMLNodeCursorImpl inpNode = new XMLNodeCursorImpl(nextNodeDtmHandle, xctxt.getDTMManager());
            DTM nodeDtm = inpNode.getDTM(nextNodeDtmHandle);
            if (nodeDtm.getNodeType(nextNodeDtmHandle) == DTM.DOCUMENT_NODE) {
               // REVISIT
               XSUntyped xsUntypedVal = new XSUntyped(inpNode.str());
               result.add(xsUntypedVal); 
            }
            else if (nodeDtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
               XSUntyped xsUntypedVal = new XSUntyped(inpNode.str());
               result.add(xsUntypedVal);
            }
            else if ((nodeDtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) ||
                     (nodeDtm.getNodeType(nextNodeDtmHandle) == DTM.TEXT_NODE)) {
               XSUntypedAtomic xsUntypedAtomicVal = new XSUntypedAtomic(inpNode.str());
               result.add(xsUntypedAtomicVal);
            }
            else {
               result.add(new XSString(inpNode.str())); 
            }
         }
      }
      else if (fnDataEffectiveArgValue instanceof ResultSequence) {
         ResultSequence inpResultSeq = (ResultSequence)fnDataEffectiveArgValue;
         ResultSequence expandedResultSeq = new ResultSequence();
         XslTransformEvaluationHelper.expandResultSequence(inpResultSeq, expandedResultSeq);
         for (int idx = 0; idx < expandedResultSeq.size(); idx++) {
            XObject xdmItem = expandedResultSeq.item(idx);
            if ((xdmItem instanceof XBoolean) || (xdmItem instanceof XNumber) || 
                (xdmItem instanceof XString) || (xdmItem instanceof XSAnyAtomicType) || 
                (xdmItem instanceof XSUntypedAtomic) || (xdmItem instanceof XSUntyped)) {
               result.add(xdmItem); 
            }
            else if (xdmItem instanceof XMLNodeCursorImpl) {
               XMLNodeCursorImpl xdmNode = (XMLNodeCursorImpl)xdmItem;               
               int nodeDtmHandle = (xdmNode.iterRaw()).item(0);
               DTM nodeDtm = xdmNode.getDTM(nodeDtmHandle);
               if (nodeDtm.getNodeType(nodeDtmHandle) == DTM.DOCUMENT_NODE) {
                  // REVISIT
                  XSUntyped xsUntypedVal = new XSUntyped(xdmNode.str());
                  result.add(xsUntypedVal); 
               }
               else if (nodeDtm.getNodeType(nodeDtmHandle) == DTM.ELEMENT_NODE) {
                  XSUntyped xsUntypedVal = new XSUntyped(xdmNode.str());
                  result.add(xsUntypedVal);
               }
               else if ((nodeDtm.getNodeType(nodeDtmHandle) == DTM.ATTRIBUTE_NODE) ||
                        (nodeDtm.getNodeType(nodeDtmHandle) == DTM.TEXT_NODE)) {
                  XSUntypedAtomic xsUntypedAtomicVal = new XSUntypedAtomic(xdmNode.str());
                  result.add(xsUntypedAtomicVal);
               }
               else {
                  result.add(new XSString(xdmNode.str())); 
               }
            }
         }
      }
      else {
         result.add(fnDataEffectiveArgValue); 
      }
      
      return result; 
   }
}
