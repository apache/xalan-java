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

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;

/**
 * The XPath 3.1 "treat as" operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class TreatAs extends XPathOperator
{

  private static final long serialVersionUID = 8897640092248117520L;

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
      
      XPathSequenceType seqTypedData = (XPathSequenceType)right;
      
      if (left instanceof XNumber) {
    	 left = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)left);  
      }
      
      if (left instanceof XSNumericType) {
    	 if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NUMERIC) {
    		result = left;
    		
    		return result;
    	 }
      }
      else if (left instanceof XNumber) {
    	 if (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NUMERIC) {
    	    result = new XSDouble(((XNumber)left).num());
    	    
    	    return result;
    	 }
      }
      
      result = XPathSequenceTypeSupport.castXdmValueToAnotherType(left, seqTypedData, true);
      
      if (result == null) {
    	 throw new TransformerException("XPDY0050 : An XPath 3.1 expression 'treat as' first operand value, "
    	 		                                                                                            + "is not compatible with the sequence "
    	 		                                                                                            + "type supplied as second operand.");  
      }
      
      return result;
  }
  
}
