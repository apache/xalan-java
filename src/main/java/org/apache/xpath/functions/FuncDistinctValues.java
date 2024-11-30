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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 fn:distinct-values function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDistinctValues extends FunctionMultiArgs {
    
   private static final long serialVersionUID = -1637800188441824456L;

  /**
   * Execute the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {      
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        ResultSequence resultSeq = new ResultSequence();
        
        Expression arg0 = getArg0();        
        Expression arg1 = getArg1();
        
  	    if (arg0 == null) {
 		   throw new javax.xml.transform.TransformerException("FOAP0001 : The number of arguments specified while "
 		 		                                                     + "calling distinct-values() function is wrong. Expected "
 		 		                                                     + "number of arguments for distinct-values() function is one "
 		 		                                                     + "or two.", srcLocator);  
 	    }
  	    
  	    XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
		  
	    String collationUri = null;
	      
		if (arg1 != null) {
		   // A collation uri was, explicitly provided during the function call fn:distinct-values
		   XObject collationXObj = arg1.execute(xctxt);
		   collationUri = XslTransformEvaluationHelper.getStrVal(collationXObj); 			 			 
		}
		else {
		   collationUri = xctxt.getDefaultCollation(); 
		}
        
        XObject arg0Obj = arg0.execute(xctxt);
        
        if (arg0Obj instanceof XNodeSet) {
           DTMManager dtmMgr = (DTMManager)xctxt;
           
           XNodeSet xNodeSet = (XNodeSet)arg0Obj;           
           DTMIterator sourceNodes = xNodeSet.iter();
           
           int nextNodeDtmHandle;
           
           while ((nextNodeDtmHandle = sourceNodes.nextNode()) != DTM.NULL) {
              XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);
              String nodeStrValue = xNodeSetItem.str();
              
              DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
              
              if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                 XSUntyped xsUntyped = new XSUntyped(nodeStrValue);                 
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                      xsUntyped, true, collationUri, xpathCollationSupport);
              }
              else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                 XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                      xsUntypedAtomic, true, collationUri, xpathCollationSupport);
              }
              else {
                 XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                      xsUntypedAtomic, true, collationUri, xpathCollationSupport);
              }
           }
        }
        else if (arg0Obj instanceof ResultSequence) {
           ResultSequence inpResultSeq = (ResultSequence)arg0Obj; 
           for (int idx = 0; idx < inpResultSeq.size(); idx++) {
              XObject xObj = inpResultSeq.item(idx);
              if (xObj instanceof XSAnyType) {
                 XSAnyType xsAnyType = (XSAnyType)xObj;
                 XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                      xsAnyType, true, collationUri, xpathCollationSupport);
              }
              else {
                  XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                       xObj, true, collationUri, xpathCollationSupport);
              }
           }
        }
        else {
           // We're assuming here that, an input value is an 
           // xdm singleton item.            
           if (arg0Obj instanceof XSAnyType) {
              XSAnyType xsAnyType = (XSAnyType)arg0Obj;
              XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, 
                                                                   xsAnyType, false, collationUri, xpathCollationSupport);
           }
           else {
              String seqItemStrValue = arg0Obj.str();
              XslTransformEvaluationHelper.addItemToResultSequence(resultSeq, new XString(seqItemStrValue), 
                                                                   false, collationUri, xpathCollationSupport);
           }
        }
            
        return resultSeq;
  }

}
