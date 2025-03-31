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
/*
 * $Id$
 */
package org.apache.xpath.operations;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;

/**
 * An implementation of XPath 3.1 "cast as" expression 
 * evaluation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class CastAs extends Operation
{

   private static final long serialVersionUID = -4194858144694864568L;

  /**
   * Apply the operation to two operands, and return the result.
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
      
      SequenceTypeData seqTypedData = (SequenceTypeData)right;
      
      ExpressionNode exprNode = getExpressionOwner();
      XPathContext xpathContext = null;
      if (exprNode instanceof ElemTemplateElement) {
    	 xpathContext = ((ElemTemplateElement)exprNode).getXPathContext();
      }
      
      try {
    	  if (XslTransformSharedDatastore.xpathCallingOpCode == OpCodes.OP_IDIV) {
    		  if (left instanceof XSDecimal) {
    			  XSDecimal xsDecimal = (XSDecimal)left;
    			  double dblValue = (xsDecimal.getValue()).doubleValue();
    			  if (dblValue >= 0) {
    				  int intValue = (int)(Math.floor(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  else {
    				  int intValue = (int)(Math.ceil(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  
    			  return result;
    		  }
    		  else if (left instanceof XSDouble) {
    			  double dblValue = ((XSDouble)left).doubleValue();    			  
    			  if (dblValue >= 0) {
    				  int intValue = (int)(Math.floor(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  else {
    				  int intValue = (int)(Math.ceil(dblValue));
    				  java.lang.String intStrValue = java.lang.String.valueOf(intValue);

    				  result = new XSInteger(intStrValue);
    			  }
    			  
    			  return result;
    		  }
    	  }
      }
      finally {
    	  // Reset the value of variable XslTransformSharedDatastore.xpathCallingOpCode 
    	  XslTransformSharedDatastore.xpathCallingOpCode = Integer.MIN_VALUE;
      }
      
      result = SequenceTypeSupport.castXdmValueToAnotherType(left, null, seqTypedData, xpathContext);
      
      return result;
  }
  
}
