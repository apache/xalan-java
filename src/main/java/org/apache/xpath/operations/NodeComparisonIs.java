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
package org.apache.xpath.operations;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * A class definition, to implement XPath 3.1 operator 'is'. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class NodeComparisonIs extends XPathOperator
{
    
    private static final long serialVersionUID = 7254558212157001252L;
   
    /**
     * Apply an XPath operator to its two operands, and return the result.
     *
     * @param left  non-null reference to an XPath operator's evaluated 
     *              first operand.              
     * @param right non-null reference to an XPath operator's evaluated 
     *              second operand.
     *
     * @return non-null reference to an XObject object instance, that 
     *         represents the result of XPath operator evaluation. 
     *
     * @throws javax.xml.transform.TransformerException
     */
   public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
   {
       
	   XObject result = null;
       
       XMLNodeCursorImpl xmlNodeCursorImpl1 = null;
       XMLNodeCursorImpl xmlNodeCursorImpl2 = null;
       
       if (left instanceof ResultSequence) {
    	  if (((ResultSequence)left).size() == 0) {
    		 result = new ResultSequence();
    		 
    		 return result;
    	  }
       }
       
       if (right instanceof ResultSequence) {
    	   if (((ResultSequence)right).size() == 0) {
    		  result = new ResultSequence();
    		  
    		  return result;
    	   }
       }
       
       if (left instanceof XMLNodeCursorImpl) {
          xmlNodeCursorImpl1 = (XMLNodeCursorImpl)left;
       }
       else if ((left instanceof ResultSequence) && (((ResultSequence)left).size() == 1) && 
    		                                                                            (((ResultSequence)left).item(0) instanceof XMLNodeCursorImpl)) {
    	   xmlNodeCursorImpl1 = (XMLNodeCursorImpl)(((ResultSequence)left).item(0));  
       }
       
       if (xmlNodeCursorImpl1 != null) {
          xmlNodeCursorImpl1 = (XMLNodeCursorImpl)(xmlNodeCursorImpl1.getFresh());
       }
       
       if (right instanceof XMLNodeCursorImpl) {
          xmlNodeCursorImpl2 = (XMLNodeCursorImpl)right; 
       }
       else if ((right instanceof ResultSequence) && (((ResultSequence)right).size() == 1) && 
                                                                                          (((ResultSequence)right).item(0) instanceof XMLNodeCursorImpl)) {
    	   xmlNodeCursorImpl2 = (XMLNodeCursorImpl)(((ResultSequence)right).item(0));  
       }
       
       if (xmlNodeCursorImpl2 != null) {
          xmlNodeCursorImpl2 = (XMLNodeCursorImpl)(xmlNodeCursorImpl2.getFresh());
       }
       
       if ((xmlNodeCursorImpl1 != null) && (xmlNodeCursorImpl2 != null)) {
    	   xmlNodeCursorImpl1 = (XMLNodeCursorImpl)(xmlNodeCursorImpl1.getFresh());
    	   xmlNodeCursorImpl2 = (XMLNodeCursorImpl)(xmlNodeCursorImpl2.getFresh());
    			   
    	   int nodeHandle1 = (xmlNodeCursorImpl1.iter()).nextNode();
    	   int nodeHandle2 = (xmlNodeCursorImpl2.iter()).nextNode();

    	   if ((nodeHandle1 == DTM.NULL) || (nodeHandle2 == DTM.NULL)) {
    		   result = new ResultSequence();  
    	   }
    	   else if (nodeHandle1 == nodeHandle2) {    		      		   
    		   result = XBoolean.S_TRUE;
    	   }
    	   else {
    		   result = XBoolean.S_FALSE;  
    	   }
       }
       else if (xmlNodeCursorImpl1 == null) {
    	   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator 'is' first operand doesn't exisit."); 
       }
       else if (xmlNodeCursorImpl2 == null) {
    	   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator 'is' second operand doesn't exisit.");
       }
       
       return result; 
   }

}
