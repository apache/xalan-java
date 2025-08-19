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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of XPath 3.1 function fn:data.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncData extends FunctionDef1Arg
{

  private static final long serialVersionUID = 1734069066834829751L;
  
  /**
   * Class constructor.
   */
  public FuncData() {
	  m_defined_arity = new Short[] {0, 1}; 
  }

  /**
   * Implementation of the function. The function must return a valid object.
   * 
   * @param xctxt                           An XPath context object
   * @return                                A valid XObject
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
      ResultSequence result = new ResultSequence();
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      XObject argObj = null;
      
      int contextNode = xctxt.getCurrentNode();
      
      if (m_arg0 == null) {
         // The function call fn:data is called with no arguments
         XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
         if (xpath3ContextItem != null) {
            argObj = xpath3ContextItem;  
         }
         else if (contextNode != DTM.NULL) {
            argObj = new XMLNodeCursorImpl(contextNode, xctxt); 
         }
      }
      else {
         // An explicit argument was provided, to the fn:data 
         // function call.
         argObj = m_arg0.execute(xctxt); 
      }
      
      if (argObj instanceof XMLNodeCursorImpl) {
         XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)argObj;
                  
         DTMCursorIterator dtmIter = xmlNodeCursorImpl.iterRaw();
         
         DTMManager dtmManager = xctxt.getDTMManager();
         
         int nextNode = DTM.NULL;
         while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
            XMLNodeCursorImpl xmlNodeCursorImpl1 = new XMLNodeCursorImpl(nextNode, dtmManager);
            
            DTM dtm = xmlNodeCursorImpl1.getDTM(nextNode);
            
            if (dtm.getNodeType(nextNode) == DTM.DOCUMENT_NODE) {
               XSUntyped xsUntypedVal = new XSUntyped(xmlNodeCursorImpl1.str());
               result.add(xsUntypedVal); 
            }
            else if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
               XSUntyped xsUntypedVal = new XSUntyped(xmlNodeCursorImpl1.str());
               result.add(xsUntypedVal);
            }
            else if ((dtm.getNodeType(nextNode) == DTM.ATTRIBUTE_NODE) ||
                     (dtm.getNodeType(nextNode) == DTM.TEXT_NODE)) {
               XSUntypedAtomic xsUntypedAtomicVal = new XSUntypedAtomic(xmlNodeCursorImpl1.str());
               result.add(xsUntypedAtomicVal);
            }
            else {
               result.add(new XSString(xmlNodeCursorImpl1.str())); 
            }
         }
      }
      else if (argObj instanceof ResultSequence) {
         ResultSequence rSeq = (ResultSequence)argObj;
         
         ResultSequence expandedResultSeq = new ResultSequence();
         XslTransformEvaluationHelper.expandResultSequence(rSeq, expandedResultSeq);
         
         for (int idx = 0; idx < expandedResultSeq.size(); idx++) {
            XObject xdmItem = expandedResultSeq.item(idx);
            if ((xdmItem instanceof XSAnyAtomicType) || (xdmItem instanceof XBooleanStatic) || 
            	(xdmItem instanceof XBoolean) || (xdmItem instanceof XNumber) || (xdmItem instanceof XString) ||
                (xdmItem instanceof XSUntypedAtomic) || (xdmItem instanceof XSUntyped)) {
               result.add(xdmItem); 
            }
            else if (xdmItem instanceof XMLNodeCursorImpl) {
               XMLNodeCursorImpl xmlNodeCursorImpl1 = (XMLNodeCursorImpl)xdmItem;
               
               DTMCursorIterator dtmIter = xmlNodeCursorImpl1.iterRaw();
               
               DTMManager dtmManager = xctxt.getDTMManager();
               
               int nextNode = DTM.NULL;
               while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
            	   XMLNodeCursorImpl xmlNodeCursorImpl2 = new XMLNodeCursorImpl(nextNode, dtmManager);
                   
                   DTM dtm = xmlNodeCursorImpl2.getDTM(nextNode);
                   
                   if (dtm.getNodeType(nextNode) == DTM.DOCUMENT_NODE) {
                      XSUntyped xsUntypedVal = new XSUntyped(xmlNodeCursorImpl2.str());
                      result.add(xsUntypedVal); 
                   }
                   else if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
                      XSUntyped xsUntypedVal = new XSUntyped(xmlNodeCursorImpl2.str());
                      result.add(xsUntypedVal);
                   }
                   else if ((dtm.getNodeType(nextNode) == DTM.ATTRIBUTE_NODE) ||
                            (dtm.getNodeType(nextNode) == DTM.TEXT_NODE)) {
                      XSUntypedAtomic xsUntypedAtomicVal = new XSUntypedAtomic(xmlNodeCursorImpl2.str());
                      result.add(xsUntypedAtomicVal);
                   }
                   else {
                      result.add(new XSString(xmlNodeCursorImpl2.str())); 
                   }
               }
            }
            else if ((xdmItem instanceof XPathInlineFunction) || (xdmItem instanceof XPathMap)) {
               throw new javax.xml.transform.TransformerException("FOTY0013 : An error occured while evaluating the function "
               		                                                                         + "call fn:data. An XSL function or a map cannot be atomized.", srcLocator);
            }
         }
      }
      else if (argObj instanceof XPathArray) {
    	 result = ((XPathArray)argObj).atomize();
      }
      else if ((m_arg0 instanceof Function) || (argObj instanceof XPathInlineFunction) 
    		                                || (argObj instanceof XPathMap)) {
    	 throw new javax.xml.transform.TransformerException("FOTY0013 : An error occured while evaluating the function call fn:data. "
    	  		                                                                            + "An XSL function or a map cannot be atomized.", srcLocator); 
      }
      else {
         result.add(argObj); 
      }
      
      return result; 
   }
}
