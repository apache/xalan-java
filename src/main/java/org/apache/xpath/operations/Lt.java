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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathCollationSupport;
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
		  DTMCursorIterator iter = nodeRef.iterRaw();
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
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl nodeRef1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String nodeStrValue = nodeRef1.str();
			  strList.add(nodeStrValue);
		  }
		  
		  lObj = lObj.getFresh();
		  right = right.getFresh();
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
		  left = left.getFresh();
		  right = right.getFresh();
		  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)left;		  
		  List<java.lang.String> strList1 = new ArrayList<java.lang.String>();
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String str1 = node1.str();
			  strList1.add(str1);
		  }
		  
		  nodeRef = (XMLNodeCursorImpl)right;		  
		  List<java.lang.String> strList2 = new ArrayList<java.lang.String>();
		  dtmManager = nodeRef.getDTMManager();
		  iter = nodeRef.iterRaw();
		  nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String str1 = node1.str();
			  strList2.add(str1);
		  }		  		  
		  
		  int size1 = strList1.size();
		  int size2 = strList2.size();
		  for (int idx1 = 0; idx1 < size1; idx1++) {
			  java.lang.String str1 = strList1.get(idx1);			  
			  Integer int1 = null;
			  try {
				 int1 = Integer.valueOf(str1);
			  }
			  catch (NumberFormatException ex) {
				 // no op 
			  }			  
			  for (int idx2 = 0; idx2 < size2; idx2++) {
				  java.lang.String str2 = strList2.get(idx2);				  
				  Integer int2 = null;
				  try {
					  int2 = Integer.valueOf(str2);
				  }
				  catch (NumberFormatException ex) {
					  // no op 
				  }				  
				  if ((int1 != null) && (int2 != null)) {
					  Integer i1 = Integer.valueOf(str1.substring(0, 1));
					  Integer i2 = Integer.valueOf(str2.substring(0, 1));
					  if (i1.intValue() < i2.intValue()) {
						 return XBoolean.S_TRUE;  
					  }
				  }				  
				  else if (str1.compareTo(str2) < 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }			  			  
		  }
		  
		  return XBoolean.S_FALSE;
	  }
	  else if ((left instanceof ResultSequence) && (right instanceof XMLNodeCursorImpl)) {
		  right = right.getFresh();		  		  
			  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)right;		  
		  List<java.lang.String> strListR = new ArrayList<java.lang.String>();
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String str1 = node1.str();
			  strListR.add(str1);
		  }
		  
		  ResultSequence rSeqL = (ResultSequence)left;
		  int lSize = rSeqL.size();
		  int rSize = strListR.size();		   
		  for (int idx = 0; idx < lSize; idx++) {
			 // Loop the LHS ResultSequence object			  
			 XObject xObj1 = rSeqL.item(idx);
			 Double lDbl = null;
			 java.lang.String lStr = null;
			 if (xObj1 instanceof XNumber) {
				lDbl = ((XNumber)xObj1).num();  
			 }
			 else if (xObj1 instanceof XSNumericType) {
				XSNumericType xsNumericType = (XSNumericType)xObj1;
				lDbl = Double.valueOf(xsNumericType.stringValue());
			 }
			 else {
				lStr = XslTransformEvaluationHelper.getStrVal(xObj1);  
			 }
			 
			 // Loop the RHS node list string values
			 for (int idx2 = 0; idx2 < rSize; idx2++) {
				 java.lang.String strRhs = strListR.get(idx2);
				 if ((lStr != null) && (lStr.compareTo(strRhs) < 0)) {
					 return XBoolean.S_TRUE;
				 }
				 else if (lDbl != null) {
					 try {	
						 Double rDbl = Double.valueOf(strRhs);
						 if (lDbl.doubleValue() < rDbl.doubleValue()) {
							 return XBoolean.S_TRUE;	 
						 }
					 }
					 catch (NumberFormatException ex) {
                         // Compare lDbl's string value with string value strRhs
						 lStr = lDbl.toString();
						 if (lStr.compareTo(strRhs) < 0) {
							return XBoolean.S_TRUE; 
						 }
					 }
				 }
			 }
		  }
		  
		  return XBoolean.S_FALSE;
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof ResultSequence)) {
		  ResultSequence swapSeq = (ResultSequence)right;
		  right = left.getFresh();
		  left = swapSeq; 
			  
		  XMLNodeCursorImpl nodeRef = (XMLNodeCursorImpl)right;		  
		  List<java.lang.String> strListR = new ArrayList<java.lang.String>();
		  DTMManager dtmManager = nodeRef.getDTMManager();
		  DTMCursorIterator iter = nodeRef.iterRaw();
		  int nextNode = DTM.NULL;		  
		  while ((nextNode = iter.nextNode()) != DTM.NULL) {
			  XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, dtmManager);
			  java.lang.String str1 = node1.str();
			  strListR.add(str1);
		  }
		  
		  ResultSequence rSeqL = (ResultSequence)left;
		  int lSize = rSeqL.size();
		  int rSize = strListR.size();		   
		  for (int idx = 0; idx < lSize; idx++) {
			 // Loop the LHS ResultSequence object			  
			 XObject xObj1 = rSeqL.item(idx);
			 Double lDbl = null;
			 java.lang.String lStr = null;
			 if (xObj1 instanceof XNumber) {
				lDbl = ((XNumber)xObj1).num();  
			 }
			 else if (xObj1 instanceof XSNumericType) {
				XSNumericType xsNumericType = (XSNumericType)xObj1;
				lDbl = Double.valueOf(xsNumericType.stringValue());
			 }
			 else {
				lStr = XslTransformEvaluationHelper.getStrVal(xObj1);  
			 }
			 
			 // Loop the RHS node list string values
			 for (int idx2 = 0; idx2 < rSize; idx2++) {
				 java.lang.String strRhs = strListR.get(idx2);
				 if ((lStr != null) && (lStr.compareTo(strRhs) < 0)) {
					 return XBoolean.S_TRUE;
				 }
				 else if (lDbl != null) {
					 try {	
						 Double rDbl = Double.valueOf(strRhs);
						 if (lDbl.doubleValue() < rDbl.doubleValue()) {
							 return XBoolean.S_TRUE;	 
						 }
					 }
					 catch (NumberFormatException ex) {
                         // Compare lDbl's string value with string value strRhs
						 lStr = lDbl.toString();
						 if (lStr.compareTo(strRhs) < 0) {
							return XBoolean.S_TRUE; 
						 }
					 }
				 }
			 }
		  }
		  
		  return XBoolean.S_FALSE;
	  }
	  else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
		  ResultSequence rSeq1 = (ResultSequence)left;
		  ResultSequence rSeq2 = (ResultSequence)right;
		  for (int idx1 = 0; idx1 < rSeq1.size(); idx1++) {
			 XObject xObj1 = rSeq1.item(idx1);
			 for (int idx2 = 0; idx2 < rSeq2.size(); idx2++) {
				XObject xObj2 = rSeq2.item(idx2);
				if (xObj1.vcLessThan(xObj2, null, XPathCollationSupport.UNICODE_CODEPOINT_COLLATION_URI, true)) {
				   return XBoolean.S_TRUE;
				}
			 }
		  }
		  
		  return XBoolean.S_FALSE;
	  }
	  
	  int listSize = strList.size();
	  
	  if (listSize > 0) {		  
		  if (rObj instanceof XString) {
			  java.lang.String strR = rObj.str();			
			  for (int i = 0; i < listSize; i++) {
				 java.lang.String str2 = strList.get(i);
				 if (str2.compareTo(strR) < 0) {
					return XBoolean.S_TRUE; 
				 }
			  }
			  
			  return XBoolean.S_FALSE;
		  }
		  else if (rObj instanceof XSString) {
			  java.lang.String strR = ((XSString)rObj).stringValue();
			  for (int i = 0; i < listSize; i++) {
				  java.lang.String str2 = strList.get(i);
				  if (str2.compareTo(strR) < 0) {
					  return XBoolean.S_TRUE; 
				  }
			  }

			  return XBoolean.S_FALSE;
		  }
      }
	  else if ((lObj != null) && (listSize == 0)) {
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
	  }
	  else {
		 result = left.lessThan(right) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
	  }
	  
	  return result;
  }
}
