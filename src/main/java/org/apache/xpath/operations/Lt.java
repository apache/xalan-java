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
import java.util.List;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * The '<' operation expression executer.
 */
public class Lt extends Operation
{
   static final long serialVersionUID = 3388420509289359422L;

  /**
   * Apply the operation to two operands, and return the result.
   *
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right)
          throws javax.xml.transform.TransformerException
  {	  
	  XObject result = null;
	  
	  if (left instanceof XPathArray) {
		  left = ((XPathArray)left).atomize();
	  }

	  if (right instanceof XPathArray) {
		  right = ((XPathArray)right).atomize(); 
	  }
	  
	  XObject lObj = null;
	  XObject rObj = null;
	  
	  List<java.lang.String> strList = new ArrayList<java.lang.String>();
	  
	  if ((left instanceof XMLNodeCursorImpl) && ((right instanceof XString) || (right instanceof XSString))) {
		  lObj = left;
		  rObj = right;
		  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)lObj;		  
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iter();
		  int nextNode = DTM.NULL;
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef1.str();
			  strList.add(nodeStrValue);
		  }
		  
		  lObj = lObj.getFresh();
		  left = left.getFresh();
	  }
	  else if ((right instanceof XMLNodeCursorImpl) && ((left instanceof XString) || (left instanceof XSString))) {
		  lObj = right;
		  rObj = left;
		  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)lObj;		  
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iter();
		  int nextNode = DTM.NULL;
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef1.str();
			  strList.add(nodeStrValue);
		  }
		  
		  lObj = lObj.getFresh();
		  right = right.getFresh();
	  }
	  
	  if (strList.size() > 0) {		  
		  if (rObj instanceof XString) {
			  java.lang.String strR = rObj.str();			
			  for (int i = 0; i < strList.size(); i++) {
				 java.lang.String str2 = strList.get(i);
				 if (str2.compareTo(strR) < 0) {
					return XBoolean.S_TRUE; 
				 }
			  }
			  
			  return XBoolean.S_FALSE;
		  }
		  else if (rObj instanceof XSString) {
			  java.lang.String strR = ((XSString)rObj).stringValue();
			  for (int i = 0; i < strList.size(); i++) {
				  java.lang.String str2 = strList.get(i);
				  if (str2.compareTo(strR) < 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }

			  return XBoolean.S_FALSE;
		  }
      }
	  else if ((lObj != null) && (strList.size() == 0)) {
		  result = XBoolean.S_FALSE;
		  
		  return result;
	  }
  	  
	  if (right instanceof ResultSequence) {
		 if (left instanceof XNumber) {
			 boolean bool = XPathGeneralComparisonRelationalOpSupport.lessThan((XNumber)left, (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		 }
		 else if (left instanceof XSNumericType) {
			 boolean bool = XPathGeneralComparisonRelationalOpSupport.lessThan((XSNumericType)left, (ResultSequence)right, false);
			 result = (bool ? XBoolean.S_TRUE : XBoolean.S_FALSE);
		 }
		 else if (left instanceof ResultSequence) {
			 ResultSequence resultSeqLhs = (ResultSequence)left;
			 ResultSequence resultSeqRhs = (ResultSequence)right;
			 boolean isResult = false;
			 for (int i = 0; i < resultSeqLhs.size(); i++) {
				 XObject xObj1 = resultSeqLhs.item(i); 
				 for (int j = 0; j < resultSeqRhs.size(); j++) {
					 XObject xObj2 = resultSeqRhs.item(j);
					 if (xObj1.lessThan(xObj2)) {
						 isResult = true;
						 break;
					 }
				 }

				 if (isResult) {
					 break; 
				 }
			 }

			 result = (isResult ? XBoolean.S_TRUE : XBoolean.S_FALSE); 
		 }
	  }
	  else {
		 result = left.lessThan(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
	  
	  return result;
  }
}
