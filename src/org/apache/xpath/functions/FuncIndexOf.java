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

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 fn:index-of function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncIndexOf extends FunctionMultiArgs {

   private static final long serialVersionUID = 2912594883291006421L;

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
        
        ResultSequence arg0ResultSeq = null;
        
        final int contextNode = xctxt.getCurrentNode();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        Expression arg2 = getArg2();
        
  	    if ((arg0 == null) || (arg1 == null)) {
 		   throw new javax.xml.transform.TransformerException("FOAP0001 : The number of arguments specified while "
 		 		                                                            + "calling index-of() function is wrong. Expected "
 		 		                                                            + "number of arguments for index-of() function is two "
 		 		                                                            + "or three.", srcLocator);  
 	    }
  	    
  	    XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
  	    
  	    String collationUri = null;
      
		if (arg2 != null) {
		   // A collation uri was, explicitly provided during the function call fn:index-of
		   XObject collationXObj = arg2.execute(xctxt);
		   collationUri = XslTransformEvaluationHelper.getStrVal(collationXObj); 			 			 
		}
		else {
		   collationUri = xctxt.getDefaultCollation(); 
		}
        
        DTMManager dtmMgr = (DTMManager)xctxt;
        
        if (arg0 instanceof LocPathIterator) {
            arg0ResultSeq = new ResultSequence();
                                
            DTMIterator arg0DtmIterator = arg0.asIterator(xctxt, contextNode);        
            
            int nextNodeDtmHandle;
            
            while ((nextNodeDtmHandle = arg0DtmIterator.nextNode()) != DTM.NULL) {
                XNodeSet xNodeSetItem = new XNodeSet(nextNodeDtmHandle, dtmMgr);                
                String nodeStrValue = xNodeSetItem.str();
                
                DTM dtm = dtmMgr.getDTM(nextNodeDtmHandle);
                
                if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                   XSUntyped xsUntyped = new XSUntyped(nodeStrValue);
                   arg0ResultSeq.add(xsUntyped);
                }
                else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   arg0ResultSeq.add(xsUntypedAtomic);
                }
                else {
                   XSUntypedAtomic xsUntypedAtomic = new XSUntypedAtomic(nodeStrValue);
                   arg0ResultSeq.add(xsUntypedAtomic);
                }                        
            } 
        }
        else {
            XObject arg0Obj = arg0.execute(xctxt);
            if (arg0Obj instanceof ResultSequence) {
               arg0ResultSeq = (ResultSequence)arg0Obj;     
            }
        }

        if (arg0ResultSeq == null) {
           throw new javax.xml.transform.TransformerException("XPTY0004 : The first argument of fn:index-of didn't "
                                                                                        + "evaluate to a sequence.", srcLocator);     
        }
        
        XObject arg1Obj = arg1.execute(xctxt);
        
        if (arg1Obj instanceof XNodeSet) {
           XNodeSet xNodeSet = (XNodeSet)arg1Obj;           
           
           if (xNodeSet.getLength() == 1) {
              String nodeStrValue = xNodeSet.str();
               
              DTMIterator sourceNodes = arg0.asIterator(xctxt, contextNode);
              int dtmNodeHandle = sourceNodes.nextNode();
              
              DTM dtm = dtmMgr.getDTM(dtmNodeHandle);
               
              if (dtm.getNodeType(dtmNodeHandle) == DTM.ELEMENT_NODE) {
                 arg1Obj = new XSUntyped(nodeStrValue);
              }
              else if (dtm.getNodeType(dtmNodeHandle) == DTM.ATTRIBUTE_NODE) {
                 arg1Obj = new XSUntypedAtomic(nodeStrValue);
              }
              else {
                 arg1Obj = new XSUntypedAtomic(nodeStrValue);
              }    
           }
           else {                            
              throw new javax.xml.transform.TransformerException("XPTY0004 : the second argument of "
                                                                                   + "fn:index-of needs to be a sequence of size one.", 
                                                                                            srcLocator); 
           }
        }
        
        for (int idx = 0; idx < arg0ResultSeq.size(); idx++) {
           ResultSequence resultSeqWithOneItem = new ResultSequence();
           resultSeqWithOneItem.add(arg0ResultSeq.item(idx));
           
           // The following call to 'XslTransformEvaluationHelper.contains' method, checks the
           // equality between XObject instances arg0ResultSeq.item(idx) and arg1Obj.  
           if (XslTransformEvaluationHelper.contains(resultSeqWithOneItem, arg1Obj, collationUri, xpathCollationSupport)) {
              resultSeq.add(new XSInteger(BigInteger.valueOf(idx + 1)));    
           }
        }
            
        return resultSeq;
  }

}
