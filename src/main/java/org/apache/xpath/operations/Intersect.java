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
package org.apache.xpath.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * An implementation of XPath 'intersect' operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class Intersect extends Operation
{
    
  private static final long serialVersionUID = 5559048164177617210L;

  /**
   * Method to find result of XPath 'intersect' operation.
   *  
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException 
  {

	XObject result = null;
	
	SourceLocator srcLocator = xctxt.getSAXLocator();
	
    XObject expr1 = m_left.execute(xctxt);
    XObject expr2 = m_right.execute(xctxt);
    
    if ((expr1 instanceof XMLNodeCursorImpl) && (expr2 instanceof XMLNodeCursorImpl)) {
    	DTMCursorIterator dtmIter = ((XMLNodeCursorImpl)expr1).iterRaw();
        int nextNode;
        List<Integer> nodeHandleList1 = new ArrayList<Integer>();
        while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
        	nodeHandleList1.add(Integer.valueOf(nextNode)); 
        }
        
        dtmIter = ((XMLNodeCursorImpl)expr2).iterRaw();
        List<Integer> nodeHandleList2 = new ArrayList<Integer>();
        while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
        	nodeHandleList2.add(Integer.valueOf(nextNode)); 
        }

		Set<Integer> intersectResultSet = nodeHandleList1.stream()
														 .distinct().filter(nodeHandleList2::contains)
                										 .collect(Collectors.toSet());
		List<Integer> list = Arrays.asList(intersectResultSet.toArray(new Integer[0]));
		
		list.sort(null);
		result = new XMLNodeCursorImpl(list, xctxt.getDTMManager());
    }
    else if ((expr1 instanceof ResultSequence) && (expr2 instanceof ResultSequence)) {
    	ResultSequence seq1 = (ResultSequence)expr1;
    	ResultSequence seq2 = (ResultSequence)expr2;
    	List<Integer> nodeHandleList1 = new ArrayList<Integer>();
    	List<Integer> nodeHandleList2 = new ArrayList<Integer>();
    	for (int idx = 0; idx < seq1.size(); idx++) {
    	   XObject xObj = seq1.item(idx);
    	   if (xObj instanceof XMLNodeCursorImpl) {
    		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
    		  nodeHandleList1.add(Integer.valueOf(nodeDtmHandle));
    	   }
    	   else {
    		  throw new javax.xml.transform.TransformerException("XPTY0004 : An item in left operand of XPath 'intersect' "
    		  		                                                                                + "operation evaluation is not a node.", srcLocator);
    	   }
    	}
    	
    	for (int idx = 0; idx < seq2.size(); idx++) {
     	   XObject xObj = seq2.item(idx);
     	   if (xObj instanceof XMLNodeCursorImpl) {
     		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
     		  nodeHandleList2.add(Integer.valueOf(nodeDtmHandle));
     	   }
     	   else {
     		  throw new javax.xml.transform.TransformerException("XPTY0004 : An item in left operand of XPath 'intersect' "
                                                                                                    + "operation evaluation is not a node.", srcLocator);
     	   }
     	}
    	
    	Set<Integer> intersectResultSet = nodeHandleList1.stream()
    													 .distinct().filter(nodeHandleList2::contains)
    													 .collect(Collectors.toSet());
    	List<Integer> list = Arrays.asList(intersectResultSet.toArray(new Integer[0]));

    	list.sort(null);
    	result = new XMLNodeCursorImpl(list, xctxt.getDTMManager());
    }
    else if ((expr1 instanceof XMLNodeCursorImpl) && (expr2 instanceof ResultSequence)) {    	
    	DTMCursorIterator dtmIter = ((XMLNodeCursorImpl)expr1).iterRaw();
        int nextNode;
        List<Integer> nodeHandleList1 = new ArrayList<Integer>();
        while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
        	nodeHandleList1.add(Integer.valueOf(nextNode)); 
        }
    	
        ResultSequence seq2 = (ResultSequence)expr2;
        List<Integer> nodeHandleList2 = new ArrayList<Integer>();
    	for (int idx = 0; idx < seq2.size(); idx++) {
     	   XObject xObj = seq2.item(idx);
     	   if (xObj instanceof XMLNodeCursorImpl) {
     		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
     		  nodeHandleList2.add(Integer.valueOf(nodeDtmHandle));
     	   }
     	   else {
     		  throw new javax.xml.transform.TransformerException("XPTY0004 : An item in left operand of XPath 'intersect' "
                                                                                                    + "operation evaluation is not a node.", srcLocator);
     	   }
     	}
    	
    	Set<Integer> intersectResultSet = nodeHandleList1.stream()
    													 .distinct().filter(nodeHandleList2::contains)
    													 .collect(Collectors.toSet());
    	List<Integer> list = Arrays.asList(intersectResultSet.toArray(new Integer[0]));

    	list.sort(null);
    	result = new XMLNodeCursorImpl(list, xctxt.getDTMManager());
    }
    else if ((expr1 instanceof ResultSequence) && (expr2 instanceof XMLNodeCursorImpl)) {    	
        ResultSequence seq1 = (ResultSequence)expr1;
        List<Integer> nodeHandleList1 = new ArrayList<Integer>();
    	for (int idx = 0; idx < seq1.size(); idx++) {
     	   XObject xObj = seq1.item(idx);
     	   if (xObj instanceof XMLNodeCursorImpl) {
     		  int nodeDtmHandle = ((XMLNodeCursorImpl)xObj).nextNode();
     		  nodeHandleList1.add(Integer.valueOf(nodeDtmHandle));
     	   }
     	   else {
     		  throw new javax.xml.transform.TransformerException("XPTY0004 : An item in left operand of XPath 'intersect' "
     		  		                                                                                + "operation evaluation is not a node.", srcLocator);
     	   }
     	}
    	
    	DTMCursorIterator dtmIter = ((XMLNodeCursorImpl)expr2).iterRaw();
        int nextNode;
        List<Integer> nodeHandleList2 = new ArrayList<Integer>();
        while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {
        	nodeHandleList2.add(Integer.valueOf(nextNode)); 
        }
    	
    	Set<Integer> intersectResultSet = nodeHandleList1.stream()
    													 .distinct().filter(nodeHandleList2::contains)
    													 .collect(Collectors.toSet());
    	List<Integer> list = Arrays.asList(intersectResultSet.toArray(new Integer[0]));

    	list.sort(null);
    	result = new XMLNodeCursorImpl(list, xctxt.getDTMManager());
    }
    
    return result;

  }

}
