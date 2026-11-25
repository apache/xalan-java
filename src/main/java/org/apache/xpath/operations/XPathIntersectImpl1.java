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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * An implementation for XPath 3.1 operator 'intersect'.
 * 
 * An XPath 3.1 operator 'intersect', represents set intersection 
 * between two xdm node sets, by node identity.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathIntersectImpl1 extends XPathOperator
{
    
  private static final long serialVersionUID = 5559048164177617210L;

  /**
   * Method definition, to find result of XPath 3.1 operator 
   * 'intersect'.
   *  
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException 
  {

	XObject result = null;
	
	SourceLocator srcLocator = xctxt.getSAXLocator();
	
    XObject xObj1 = m_left.execute(xctxt);
    
    XObject xObj2 = m_right.execute(xctxt);
    
    if ((xObj1 instanceof XMLNodeCursorImpl) && (xObj2 instanceof XMLNodeCursorImpl)) {
    	DTMCursorIterator dtmCursorIterator1 = ((XMLNodeCursorImpl)xObj1).iter();
        
    	int nextNode = DTM.NULL;
        
        List<Integer> nodeHandleList1 = new ArrayList<Integer>();
        
        while ((nextNode = dtmCursorIterator1.nextNode()) != DTM.NULL) {
        	nodeHandleList1.add(Integer.valueOf(nextNode)); 
        }
        
        DTMCursorIterator dtmCursorIterator2 = ((XMLNodeCursorImpl)xObj2).iter();
        List<Integer> nodeHandleList2 = new ArrayList<Integer>();
        
        while ((nextNode = dtmCursorIterator2.nextNode()) != DTM.NULL) {
        	nodeHandleList2.add(Integer.valueOf(nextNode)); 
        }

		Set<Integer> xpathIntersectResultSet = nodeHandleList1.stream()
														      .distinct().filter(nodeHandleList2::contains)
                										      .collect(Collectors.toSet());
		List<Integer> resultList = Arrays.asList(xpathIntersectResultSet.toArray(new Integer[0]));
		
		resultList.sort(null);
		
		result = new XMLNodeCursorImpl(resultList, xctxt);
    }
    else if ((xObj1 instanceof ResultSequence) && (xObj2 instanceof ResultSequence)) {
    	ResultSequence rSeq1 = (ResultSequence)xObj1;
    	ResultSequence rSeq2 = (ResultSequence)xObj2;
    	
    	List<Integer> nodeHandleList1 = new ArrayList<Integer>();
    	List<Integer> nodeHandleList2 = new ArrayList<Integer>();
    	
    	int size1 = rSeq1.size();
    	int size2 = rSeq2.size();
    	
    	for (int idx = 0; idx < size1; idx++) {
    	   XObject xObj = rSeq1.item(idx);
    	   
    	   if (xObj instanceof XMLNodeCursorImpl) {
    		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
    		  
    		  nodeHandleList1.add(Integer.valueOf(nodeDtmHandle));
    	   }
    	   else {
    		  throw new TransformerException("XPTY0004 : An XPath 3.1 operator 'intersect' first operand "
    		  		                                                                                     + "contains an item that is not a node.", srcLocator);
    	   }
    	}
    	
    	for (int idx = 0; idx < size2; idx++) {
     	   XObject xObj = rSeq2.item(idx);
     	   
     	   if (xObj instanceof XMLNodeCursorImpl) {
     		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
     		  
     		  nodeHandleList2.add(Integer.valueOf(nodeDtmHandle));
     	   }
     	   else {
     		  throw new TransformerException("XPTY0004 : An XPath 3.1 operator 'intersect' second operand "
                                                                                                          + "contains an item that is not a node.", srcLocator);
     	   }
     	}
    	
    	Set<Integer> xpathIntersectResultSet = nodeHandleList1.stream()
    													      .distinct().filter(nodeHandleList2::contains)
    													      .collect(Collectors.toSet());
    	List<Integer> resultList = Arrays.asList(xpathIntersectResultSet.toArray(new Integer[0]));

    	resultList.sort(null);
    	
    	result = new XMLNodeCursorImpl(resultList, xctxt);
    }
    else if ((xObj1 instanceof XMLNodeCursorImpl) && (xObj2 instanceof ResultSequence)) {    	
    	DTMCursorIterator dtmCursorIterator = ((XMLNodeCursorImpl)xObj1).iter();
        
    	int nextNode = DTM.NULL;
        
        List<Integer> nodeHandleList1 = new ArrayList<Integer>();
        
        while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
        	nodeHandleList1.add(Integer.valueOf(nextNode)); 
        }
    	
        ResultSequence rSeq2 = (ResultSequence)xObj2;
        List<Integer> nodeHandleList2 = new ArrayList<Integer>();
        
        int size2 = rSeq2.size();
    	
        for (int idx = 0; idx < size2; idx++) {
     	   XObject xObj = rSeq2.item(idx);
     	   
     	   if (xObj instanceof XMLNodeCursorImpl) {
     		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
     		  
     		  nodeHandleList2.add(Integer.valueOf(nodeDtmHandle));
     	   }
     	   else {
     		  throw new TransformerException("XPTY0004 : An XPath 3.1 operator 'intersect' second operand "
                                                                                                          + "contains an item that is not a node.", srcLocator);
     	   }
     	}
    	
    	Set<Integer> xpathIntersectResultSet = nodeHandleList1.stream()
    													      .distinct().filter(nodeHandleList2::contains)
    													      .collect(Collectors.toSet());
    	List<Integer> resultList = Arrays.asList(xpathIntersectResultSet.toArray(new Integer[0]));

    	resultList.sort(null);
    	
    	result = new XMLNodeCursorImpl(resultList, xctxt);
    }
    else if ((xObj1 instanceof ResultSequence) && (xObj2 instanceof XMLNodeCursorImpl)) {    	
        ResultSequence rSeq1 = (ResultSequence)xObj1;
        
        List<Integer> nodeHandleList1 = new ArrayList<Integer>();
        
        int size1 = rSeq1.size();
    	
        for (int idx = 0; idx < size1; idx++) {
     	   XObject xObj = rSeq1.item(idx);
     	   
     	   if (xObj instanceof XMLNodeCursorImpl) {
     		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
     		  
     		  nodeHandleList1.add(Integer.valueOf(nodeDtmHandle));
     	   }
     	   else {
     		  throw new TransformerException("XPTY0004 : An XPath 3.1 operator 'intersect' first operand "
                                                                                                         + "contains an item that is not a node.", srcLocator);
     	   }
     	}
    	
    	DTMCursorIterator dtmCursorIterator = ((XMLNodeCursorImpl)xObj2).iter();
        
    	int nextNode = DTM.NULL;
        
        List<Integer> nodeHandleList2 = new ArrayList<Integer>();
        
        while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
        	nodeHandleList2.add(Integer.valueOf(nextNode)); 
        }
    	
    	Set<Integer> xpathIntersectResultSet = nodeHandleList1.stream()
    													      .distinct().filter(nodeHandleList2::contains)
    													      .collect(Collectors.toSet());
    	List<Integer> resultList = Arrays.asList(xpathIntersectResultSet.toArray(new Integer[0]));

    	resultList.sort(null);
    	
    	result = new XMLNodeCursorImpl(resultList, xctxt);
    }
    
    return result;

  }

}
