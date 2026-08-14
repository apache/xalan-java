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

import java.math.BigInteger;

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
 * Implementation of an XPath 3.1 operator 'to',
 * used for XPath 3.1 range expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class Range extends XPathOperator
{
    
   private static final long serialVersionUID = 7722428363208837859L;
   
   /**
    * This class field, represents maximum number of xs:integer
    * values that may be returned by XPath 3.1 range 'to' expression.
    * This is for optimization purpose and is Xalan-J specific. 
    * Otherwise an XPath range expression takes long time to complete 
    * running for very large differences between XPath range's maximum 
    * and minimum values.
    * 
    * Beyond MAX_RANGE_SIZE limit, this class returns two xs:integer values,
    * the first one of which is lower bound of the range, and the
    * second one is upper bound of the range. The returned ResultSequence
    * object has boolean value m_is_seq_expanded set to false.
    * 
    * For fully populated XPath 3.1 range expression results, the boolean
    * value m_is_seq_expanded is set to true, which is default.
    */
   private static final int MAX_RANGE_SIZE = 100000;

   /**
    * Apply an XPath operator to two operands, and return the result.
    *
    * @param left non-null reference to an evaluated XPath first operand
    * @param right non-null reference to an evaluated XPath second operand
    *
    * @return non-null reference to the XObject that represents the 
    *                  result of an XPath operator evaluation.
    *
    * @throws javax.xml.transform.TransformerException
    */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
    	ResultSequence result = new ResultSequence();

    	XObject xObj0 = null;      

    	XObject xObj1 = null;

    	XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();

    	if (m_left instanceof XSL3ConstructorOrExtensionFunction) {
    		XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_left;

    		if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
    			xObj0 = xslFunctionService.callFunction(xpathFunc, null, xctxt); 
    		}
    		else {
    			xObj0 = m_left.execute(xctxt, true);  
    		}
    	}
    	else {
    		xObj0 = m_left.execute(xctxt, true); 
    	}
          
    	if (m_right instanceof XSL3ConstructorOrExtensionFunction) {
    		XSL3ConstructorOrExtensionFunction xpathFunc = (XSL3ConstructorOrExtensionFunction)m_right;

    		if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xpathFunc.getNamespace())) {
    			xObj1 = xslFunctionService.callFunction(xpathFunc, null, xctxt); 
    		}
    		else {
    			xObj1 = m_right.execute(xctxt, true);  
    		}
    	}
    	else {
    		xObj1 = m_right.execute(xctxt, true); 
    	}

    	if (xObj0 instanceof XNumber) {
    		xObj0 = XslTransformEvaluationHelper.getXdmNumericValueFromXNumber((XNumber)xObj0);
    	}

    	if (xObj1 instanceof XNumber) {
    		xObj1 = XslTransformEvaluationHelper.getXdmNumericValueFromXNumber((XNumber)xObj1);
    	}
    	
    	BigInteger lBigInt = null;

    	if (xObj0 instanceof XSInteger) {
    		lBigInt = ((XSInteger)xObj0).intValue();
    	}
    	else {
    		lBigInt = getBigIntValue(xObj0, xctxt);
    	}

    	BigInteger rBigInt = null;

    	if (xObj1 instanceof XSInteger) {
    		rBigInt = ((XSInteger)xObj1).intValue();
    	}
    	else {
    		rBigInt = getBigIntValue(xObj1, xctxt);
    	}      

    	if (rBigInt.compareTo(lBigInt) >= 0) {
    		
    		BigInteger diff1 = rBigInt.subtract(lBigInt);
    		BigInteger const1 = BigInteger.valueOf(MAX_RANGE_SIZE - 1);
    		
    		if (diff1.compareTo(const1) > 0) {
    			XSInteger lValue = new XSInteger(lBigInt);
    			XSInteger rValue = new XSInteger(rBigInt);
    			result.add(lValue);
    			result.add(rValue);
    			
    			result.setSequenceExpanded(false);    			
    		}
    		else {
    			BigInteger maxIntValue = BigInteger.valueOf(Integer.valueOf(Integer.MAX_VALUE - 1));
    			BigInteger maxLongValue = BigInteger.valueOf(Long.valueOf(Long.MAX_VALUE - 1)); 

    			if (rBigInt.compareTo(maxIntValue) <= 0) {
    				// Using primitive int value comparisons to produce the result    			
    				int lInt = lBigInt.intValue();
    				int rInt = rBigInt.intValue();    			
    				for (int idx = lInt; idx < (rInt + 1); idx++) {
    					XSInteger xsInteger = new XSInteger(idx + "");    				    				
    					result.add(xsInteger); 
    				}
    			}
    			else if (rBigInt.compareTo(maxLongValue) <= 0) {
    				// Using primitive long value comparisons to produce the result    			
    				long lLong = lBigInt.longValue();
    				long rLong = rBigInt.longValue();    			
    				for (long idx = lLong; idx < (rLong + 1); idx++) {
    					XSInteger xsInteger = new XSInteger(idx + "");    				    				
    					result.add(xsInteger); 
    				}
    			}
    			else {
    				// Using BigInteger value comparisons to produce the result     			
    				while (lBigInt.compareTo(rBigInt) <= 0) {
    					XSInteger xsInteger = new XSInteger(lBigInt);    				
    					result.add(xsInteger);

    					lBigInt = lBigInt.add(BigInteger.ONE);
    				}
    			}
    		}
    	}      

    	return result;
      
    }
    
    /**
     * Method definition, to get java.math.BigInteger object, corresponding to
     * the supplied XObject object instance (which is, XPath range 'to' operator's
     * compiled first, or second operand).  
     * 
     * @param xObj                          The supplied XObject object instance
     * @param xctxt                         An XPath context object
     * @return                              An java.math.BigInteger object instance
     * @throws TransformerException
     */
    private BigInteger getBigIntValue(XObject xObj, XPathContext xctxt) throws TransformerException {
    	
        BigInteger result = null;
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();

    	if (xObj instanceof XSNumericType) {
    		java.lang.String strVal = ((XSNumericType)xObj).stringValue();
    		
    		try {
     	       result = new BigInteger(strVal);
     	    }
     	    catch (NumberFormatException ex) {
     	       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath range 'to' operator's "
                                                                                                 + "operand value " + strVal 
                                                                                                 + " is not an integer.", srcLocator);
     	    }
    	}
    	else if (xObj instanceof XNumber) {    		    		
    		java.lang.String strVal = xObj.str();
    		
    		try {
      	       result = new BigInteger(strVal);
      	    }
      	    catch (NumberFormatException ex) {
      	       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath range 'to' operator's "
                                                                                                 + "operand value " + strVal 
                                                                                                 + " is not an integer.", srcLocator);
      	    }
    	}
    	else {
    		java.lang.String strVal = XslTransformEvaluationHelper.getStrVal(xObj);
    		
    	    try {
    	       result = new BigInteger(strVal);
    	    }
    	    catch (NumberFormatException ex) {
    	       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath range 'to' operator's "
                        														                + "operand value " + strVal + " "
                        														                + "is not an integer.", srcLocator);
    	    }    		    		
    	}    	

    	return result;
    }

}
