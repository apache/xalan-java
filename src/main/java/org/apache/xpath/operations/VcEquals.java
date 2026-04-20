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

import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * The XPath 3.1 value comparison "eq" operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class VcEquals extends Operation
{

  private static final long serialVersionUID = 4786065470189699263L;

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
  public XObject operate(XObject left, XObject right) throws 
                                                  javax.xml.transform.TransformerException
  {  
	  XObject result = null;
	  
	  if (left instanceof ElemFunctionItem) {
		 throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath operator eq's lhs argument is a function type, which cannot be atomized.");   
	  }
	  
	  if (right instanceof ElemFunctionItem) {
		 throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath operator eq's rhs argument is a function type, which cannot be atomized.");   
	  }
	  
	  boolean isLEmpty = false;
	  if (left instanceof ResultSequence) {
		  if (((ResultSequence)left).size() == 0) {
			  isLEmpty = true;
		  }
	  }
	  else if (left instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl nodeRef1 = (XMLNodeCursorImpl)left;
		  if (nodeRef1.getLength() == 0) {
			  isLEmpty = true;
		  }
	  }

	  boolean isREmpty = false;
	  if (right instanceof ResultSequence) {
		  if (((ResultSequence)right).size() == 0) {
			  isREmpty = true;
		  }
	  }
	  else if (right instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl nodeRef1 = (XMLNodeCursorImpl)right;
		  if (nodeRef1.getLength() == 0) {
			  isREmpty = true;
		  }
	  }
	  
	  if (isLEmpty || isREmpty) {
		 // If one or both the operands of XPath operator eq 
		 // are empty, result is empty sequence.
		 result = new ResultSequence(); 
	  }
	  else {
		 result = left.vcEquals(right, getExpressionOwner(), null, true) ? XBoolean.S_TRUE : XBoolean.S_FALSE;  
	  }
	  
      return result;
  }

}
