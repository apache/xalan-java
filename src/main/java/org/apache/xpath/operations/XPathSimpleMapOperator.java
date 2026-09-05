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
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Class definition, implementing an XPath 3.1 simple 
 * map operator, '!'.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSimpleMapOperator extends XPathOperator
{
    
   private static final long serialVersionUID = -1467842928587523219L;

   /**
    * Apply an XPath operator to two operands, and return the result.
    *
    * @param left non-null reference to the evaluated first operand.
    * @param right non-null reference to the evaluated second operand.
    *
    * @return non-null reference to an XObject object instance that 
    *         represents the result of an XPath simple map operator's 
    *         evaluation. 
    *
    * @throws javax.xml.transform.TransformerException
    */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
   {
       
	   XObject result = null;
       
       final int sourceNode = xctxt.getCurrentNode();
       
       XObject xObj0 = null;
       
       if (m_left instanceof SelfIteratorNoPredicate) {
    	   XObject contextItem = xctxt.getXPath3ContextItem();
    	   if (contextItem != null) {
    		   xObj0 = contextItem;  
    	   }
    	   else {
    		   xObj0 = m_left.execute(xctxt); 
    	   }
       }
       else {  
    	   xObj0 = m_left.execute(xctxt);
       }
       
       if (xObj0 instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xsObjNodeSet = (XMLNodeCursorImpl)xObj0;
           DTMCursorIterator dtmIter = xsObjNodeSet.iterRaw();
           
           int nextNode = DTM.NULL;           
           ResultSequence resultSeq = new ResultSequence();            
           
           while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
              xctxt.pushCurrentNode(nextNode);
              
              try {
            	  XObject xsObj = m_right.execute(xctxt, nextNode);                          	  
            	  resultSeq.add(xsObj);
              }
              finally {
            	  xctxt.popCurrentNode();
              }
           }
           
           result = resultSeq;
       }
       else if (xObj0 instanceof ResultSequence) {
           ResultSequence inpSeq = (ResultSequence)xObj0;
           ResultSequence resultSeq = new ResultSequence();
           int size1 = inpSeq.size();
           
           for (int idx = 0; idx < size1; idx++) {
        	   XObject xObj = inpSeq.item(idx);              
          	  
        	   XObject prevCtxtItem = xctxt.getXPath3ContextItem();
        	   int prevCtxtPosition = xctxt.getXPath3ContextPosition();
        	   int prevCtxtSize = xctxt.getXPath3ContextSize();

        	   xctxt.setXPath3ContextItem(xObj);
        	   xctxt.setXPath3ContextPosition(idx + 1);
        	   xctxt.setXPath3ContextSize(size1);

        	   try {
        		   XObject xsObj = m_right.execute(xctxt, sourceNode);        		   
        		   resultSeq.add(xsObj);
        	   }
        	   finally {
        		   xctxt.setXPath3ContextItem(prevCtxtItem);
        		   xctxt.setXPath3ContextPosition(prevCtxtPosition);
        		   xctxt.setXPath3ContextSize(prevCtxtSize);
        	   }
           }
           
           result = resultSeq;
       }
       else {          	  
    	   XObject prevCtxtItem = xctxt.getXPath3ContextItem();
    	   int prevCtxtPosition = xctxt.getXPath3ContextPosition();
    	   int prevCtxtSize = xctxt.getXPath3ContextSize();

    	   xctxt.setXPath3ContextItem(xObj0);
    	   xctxt.setXPath3ContextPosition(1);
    	   xctxt.setXPath3ContextSize(1);

    	   try {
    		   XObject xsObj = m_right.execute(xctxt, sourceNode);
    		   result = xsObj;
    	   }
    	   finally {
    		   xctxt.setXPath3ContextItem(prevCtxtItem);
    		   xctxt.setXPath3ContextPosition(prevCtxtPosition);
    		   xctxt.setXPath3ContextSize(prevCtxtSize);
    	   }
       }
       
       return result; 
   }

}
