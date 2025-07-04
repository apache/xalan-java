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

import java.math.BigInteger;
import java.util.PrimitiveIterator.OfInt;
import java.util.stream.IntStream;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 range "to" operator.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class Range extends Operation
{
    
   private static final long serialVersionUID = 7722428363208837859L;

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
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
      ResultSequence result = new ResultSequence();
      
      XObject lObj = null;      
      XObject rObj = null;
      
      XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
      
      if (m_left instanceof XSL3ConstructorOrExtensionFunction) {
         XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_left;
         if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
            lObj = xslFunctionService.callFunction(xpathFunc, null, xctxt); 
         }
         else {
            lObj = m_left.execute(xctxt, true);  
         }
      }
      else {
         lObj = m_left.execute(xctxt, true); 
      }
          
      if (m_right instanceof XSL3ConstructorOrExtensionFunction) {
         XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_right;
         if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
            rObj = xslFunctionService.callFunction(xpathFunc, null, xctxt); 
         }
         else {
            rObj = m_right.execute(xctxt, true);  
         }
      }
      else {
         rObj = m_right.execute(xctxt, true); 
      }
                  
      BigInteger lBigInt = getBigIntValue(xctxt, lObj);
      BigInteger rBigInt = getBigIntValue(xctxt, rObj);      
      
      if (rBigInt.compareTo(lBigInt) >= 0) {
    	  BigInteger maxIntValue = new BigInteger((Integer.valueOf(Integer.MAX_VALUE - 1)).toString());
    	  if (rBigInt.compareTo(maxIntValue) <= 0) {
    		  // Using primitive int value comparisons to produce the result
    		  int lInt = lBigInt.intValue();
    		  int rInt = rBigInt.intValue();
    		  IntStream intStr = IntStream.rangeClosed(lInt, rInt);

    		  OfInt intIter = intStr.iterator();          
    		  while (intIter.hasNext()) {        	  
    			  XSInteger resultItem = new XSInteger((intIter.next()).toString());
    			  result.add(resultItem); 
    		  }
    	  }
    	  else {
    		  // Using BigInteger value comparisons to produce the result
    		  for (BigInteger bigIntVal = lBigInt; bigIntVal.compareTo(rBigInt) <= 0; bigIntVal = bigIntVal.add(BigInteger.ONE)) {
    			  XSInteger resultItem = new XSInteger(bigIntVal);
    			  result.add(resultItem);
    		  }
    	  }
      }      
      
      return result;      
    }

    /**
     * Given an XPath range 'to' operator's compiled 1st or 2nd operand, get an 
     * operand's value as java.math.BigInteger object.
     */
    private BigInteger getBigIntValue(XPathContext xctxt, XObject xpathToOperand) throws TransformerException {
    	
    	BigInteger result = null;
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();

    	if (xpathToOperand instanceof XSNumericType) {
    		java.lang.String strVal = ((XSNumericType)xpathToOperand).stringValue();
    		try {
     	       result = new BigInteger(strVal);
     	    }
     	    catch (NumberFormatException ex) {
     	       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath range 'to' operator's "
                                                                                + "operand value " + strVal + " is not an integer.", srcLocator);
     	    }
    	}
    	else if (xpathToOperand instanceof XNumber) {
    		double dbl = ((XNumber)xpathToOperand).num();
    		if (dbl > (long)dbl) {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath range 'to' operator's "
                                                                                + "operand value " + dbl + " is not an integer.", srcLocator);  
    		}
    		else {
    			result = BigInteger.valueOf((long)dbl); 
    		}

    	}
    	else {
    		java.lang.String strVal = XslTransformEvaluationHelper.getStrVal(xpathToOperand);
    	    try {
    	       result = new BigInteger(strVal);
    	    }
    	    catch (NumberFormatException ex) {
    	    	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath range 'to' operator's "
                        														+ "operand value " + strVal + " is not an integer.", srcLocator);
    	    }
    	}    	

    	return result;
    }

}
