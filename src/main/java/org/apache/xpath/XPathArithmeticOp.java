/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.Constants;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.XPath3Operator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Class definition, providing utility methods to support 
 * XPath arithmetic operator implementations.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XPathArithmeticOp extends XPath3Operator {
	
	private static final long serialVersionUID = 7567257906066639674L;

	protected static final String CARDINALITY_ERR_MESG = "{0} : A sequence of more than one item, is not allowed as an operand of XPath operator {1}.";

	protected static final String DIV_BY_ZERO_ERR_MESG = "{0} : An integer division by zero error.";

	protected static final String OPERAND_NOT_NUMERIC_ERR_MESG = "{0} : One or both of the operands of XPath operator {1}, are not numeric values.";
	
	protected static final String OP_SYMBOL_PLUS = "+";
	
	protected static final String OP_SYMBOL_MINUS = "-";
	
	protected static final String OP_SYMBOL_MULT = "*";
	
	protected static final String OP_SYMBOL_DIV = "div";  // produces quotient from an XPath numeric division
	
	protected static final String OP_SYMBOL_MOD = "mod";  // produces remainder from an XPath numeric division 
	
    private static final String NON_TERMINATING_DECIMAL_EXPANSION = "Non-terminating decimal expansion";
    
    private static final String DIVISION_BY_ZERO = "Division by zero";
    
    private static final int XPATH_DIV_OP_DEFAULT_SCALE = 18;
	
    
    /**
     * Method definition, to do arithmetic operation on two supplied
     * XNumber object values.
     * 
     * @param lNumber                             An arithmetic operator's, supplied
     *                                            first argument.
     * @param rNumber                             An arithmetic operator's, supplied
     *                                            second argument.
     * @param opSymbol                            The supplied string value, representing
     *                                            the type of arithmetic operation to be
     *                                            performed.
     * @param elemTemplateElement                 The supplied ElemTemplateElement object
     *                                            instance.
     * @return                                    The result of XPath arithmetic operation
     * @throws TransformerException
     */
	protected XObject arithmeticOpOnXNumberValues(XNumber lNumber, XNumber rNumber, 
			                                      String opSymbol, ElemTemplateElement elemTemplateElement) throws TransformerException {

		XObject result = null;

		if (lNumber.isXsInteger() && rNumber.isXsInteger()) {
			String lXsIntegerStr = (lNumber.getXsInteger()).stringValue();
			BigInteger lBigInteger = new BigInteger(lXsIntegerStr);
			String rXsIntegerStr = (rNumber.getXsInteger()).stringValue();
			BigInteger rBigInteger = new BigInteger(rXsIntegerStr);
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSInteger(lBigInteger.add(rBigInteger));
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSInteger(lBigInteger.subtract(rBigInteger));				
			}
			else if (opSymbol.equals(OP_SYMBOL_MULT)) {
			   result = new XSInteger(lBigInteger.multiply(rBigInteger));				
			}
			else if (opSymbol.equals(OP_SYMBOL_DIV)) {
			   BigDecimal lBigDecimal = new BigDecimal(lXsIntegerStr); 
			   BigDecimal rBigDecimal = new BigDecimal(rXsIntegerStr);			   
			   try {				  
			      result = new XSDecimal(lBigDecimal.divide(rBigDecimal));
			   }
			   catch (ArithmeticException ex) {
				   java.lang.String exceptionMesg = ex.getMessage();
				   
	     		   result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg, elemTemplateElement);
			   }
			}
			else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			   try {
				  BigDecimal lBigDecimal = new BigDecimal(lXsIntegerStr); 
				  BigDecimal rBigDecimal = new BigDecimal(rXsIntegerStr);
				  
				  result = new XSDecimal(lBigDecimal.remainder(rBigDecimal));
			   }
			   catch (ArithmeticException ex) {				  
				  error(DIV_BY_ZERO_ERR_MESG, new String[] {"FOAR0001"}, elemTemplateElement);
			   }
			}
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsDecimal()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDecimal()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDecimal()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}
		else if (lNumber.isXsDouble() && rNumber.isXsDouble()) {
			Double lDbl = Double.valueOf((lNumber.getXsDouble()).stringValue());
			Double rDbl = Double.valueOf((rNumber.getXsDouble()).stringValue());
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDouble(lDbl + rDbl);
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDouble(lDbl - rDbl);				
			}
			else if (opSymbol.equals(OP_SYMBOL_MULT)) {
			   result = new XSDouble(lDbl * rDbl);				
			}
			else if (opSymbol.equals(OP_SYMBOL_DIV)) {			   
			   double lDouble = lDbl.doubleValue();
			   double rDouble = rDbl.doubleValue();
			   
			   result = doubleDiv(lDouble, rDouble);
			}
			else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			   result = new XSDecimal(BigDecimal.valueOf(lDbl.doubleValue() % rDbl.doubleValue()));	
			}			
		}
		else if (lNumber.isXsInteger() && rNumber.isXsDecimal()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsInteger()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDecimal()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}
		else if (lNumber.isXsInteger() && rNumber.isXsDouble()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsInteger()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDouble()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsInteger()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDecimal()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsInteger()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}
		else if (lNumber.isXsDecimal() && rNumber.isXsDouble()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDecimal()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDouble()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}
		else if (lNumber.isXsDouble() && rNumber.isXsInteger()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDouble()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsInteger()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}      
		else if (lNumber.isXsDouble() && rNumber.isXsDecimal()) {
			BigDecimal lBigDecimal = new BigDecimal((lNumber.getXsDouble()).stringValue());
			BigDecimal rBigDecimal = new BigDecimal((rNumber.getXsDecimal()).stringValue());
			
			result = arithmeticOpOnBigDecimalValues(lBigDecimal, rBigDecimal, opSymbol, elemTemplateElement);
		}      
		else {
			double lDouble = lNumber.num();
			double rDouble = rNumber.num();
			
			if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			   result = new XSDouble(lDouble + rDouble);
			}
			else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			   result = new XSDouble(lDouble - rDouble);				
			}
			else if (opSymbol.equals(OP_SYMBOL_MULT)) {
			   result = new XSDouble(lDouble * rDouble);				
			}
			else if (opSymbol.equals(OP_SYMBOL_DIV)) {			   
			   result = doubleDiv(lDouble, rDouble);
			}
			else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			   result = new XSDouble(lDouble % rDouble);	
			}
		}

		return result;
	}
	
	/**
	 * Method definition, to get an XNumber object instance, from
	 * the supplied XSNumericType object instance. 
	 * 
	 * @param xsNumericType                  The supplied XSNumericType object 
	 *                                       instance. 
	 * @return                               The constructed XNumber object
	 *                                       instance. 
	 */
	protected XNumber getXNumberFromXSNumericType(XSNumericType xsNumericType) {
	   
	   XNumber result = null;
	   
	   double num = 0.0;
	   
	   /**
	    * The following cases, covers the whole XML Schema built-in
	    * atomic numeric type hierarchy. i.e, for every XML Schema
	    * built-in atomic numeric type, one of the following code
	    * branches will definitely be invoked.
	    */	   
	   if (xsNumericType instanceof XSInteger) {
		  // will provide access to xs:integer and its subtypes
		  XSInteger xsInteger = (XSInteger)xsNumericType;
		  num = (xsInteger.getValue()).doubleValue();
		  
		  result = new XNumber(num);
		  
		  result.setXsInteger(xsInteger);
	   }
	   else if (xsNumericType instanceof XSDecimal) {
		   XSDecimal xsDecimal = (XSDecimal)xsNumericType;
		   num = (xsDecimal.getValue()).doubleValue();
		   
		   result = new XNumber(num);
		   
		   result.setXsDecimal(xsDecimal); 
	   }
	   else if (xsNumericType instanceof XSDouble) {
		   XSDouble xsDouble = (XSDouble)xsNumericType;
		   num = xsDouble.doubleValue();
		   
		   result = new XNumber(num);
		   
		   result.setXsDouble(xsDouble); 
	   }
	   else if (xsNumericType instanceof XSFloat) {
		   XSFloat xsFloat = (XSFloat)xsNumericType;
		   num = (double)(xsFloat.floatValue());
		   
		   result = new XNumber(num);
		   
		   result.setXsDouble(new XSDouble(num)); 
	   }
	   
	   return result;
	}
	
	/**
	 * Method definition, to process an ArithmeticException during
	 * XPath operator 'div' evaluation. 
	 * 
	 * @param lBigDecimal                     XPath operator 'div' first operand 
	 * @param rBigDecimal                     XPath operator 'div' second operand
	 * @param exceptionMesg                   A run-time error message string
	 * @param elemTemplateElement             The supplied ElemTemplateElement object
	 *                                        instance. 
	 * @return                                An xdm result object when run-time
	 *                                        exception is not raised.
	 * @throws TransformerException
	 */
	protected XObject divOpArithmeticExceptionAction(BigDecimal lBigDecimal, BigDecimal rBigDecimal,
			                                         java.lang.String exceptionMesg, ElemTemplateElement elemTemplateElement) throws TransformerException {
		XObject result = null;

		if (exceptionMesg.startsWith(NON_TERMINATING_DECIMAL_EXPANSION)) {
			BigDecimal resultBigDecimal = lBigDecimal.divide(rBigDecimal, XPATH_DIV_OP_DEFAULT_SCALE, RoundingMode.HALF_EVEN);
			
			result = new XSDecimal(resultBigDecimal);
		}
		else if (exceptionMesg.startsWith(DIVISION_BY_ZERO)) {
			error(DIV_BY_ZERO_ERR_MESG, new String[] {"FOAR0001"}, elemTemplateElement); 
		}

		return result;
	}
	
	/**
	 * Method definition, to construct an concrete error message string 
	 * value using information supplied as arguments, to be emitted as an 
	 * javax.xml.transform.TransformerException object.
	 * 
	 * @param errMesg                  The supplied error message string value
	 * @param args                     The supplied string valued arguments for
	 *                                 an error message.
	 * @param elemTemplateElement      The supplied ElemTemplateElement object
	 *                                 instance.                                  
	 */
	protected void error(String errMesg, String[] args, ElemTemplateElement elemTemplateElement) 
			                                                                                 throws javax.xml.transform.TransformerException {
		for (int idx = 0; idx < args.length; idx++) {		 
			errMesg = errMesg.replace("{"+idx+"}", args[idx]);
		}

		throw new javax.xml.transform.TransformerException(errMesg, elemTemplateElement); 
	}
	
	/**
	 * Method definition, to do arithmetic division on two supplied
	 * operand values.
	 * 
	 * @param lDouble                   An arithmetic division's first operand
	 * @param rDouble                   An arithmetic division's second operand
	 * @return                          An xdm result object for an arithmetic 
	 *                                  division.
	 */
	protected XObject doubleDiv(double lDouble, double rDouble) {

		XObject result = null;

		double resultDbl = (lDouble / rDouble);

		if (resultDbl == Double.POSITIVE_INFINITY) {
			result = new XSDouble(Double.POSITIVE_INFINITY);
		}
		else if (resultDbl == Double.NEGATIVE_INFINITY) {
			result = new XSDouble(Double.NEGATIVE_INFINITY); 
		}
		else if (Double.isNaN(resultDbl)) {
			result = new XSDouble(Double.NaN); 
		}
		else {
			result = new XSDecimal(BigDecimal.valueOf(resultDbl));
		}

		return result;
	}
	
	/**
	 * Method definition, to get java primitive double value, from 
	 * an XMLNodeCursorImpl object instance. 
	 * 
	 * @param xmlNodeCursorImpl                   An XMLNodeCursorImpl object 
	 *                                            instance.
	 * @param xctxt                               An XPath context object.
	 * @return                                    The computed double value, corresponding
	 *                                            to XMLNodeCursorImpl object.
	 */
	protected double getDoubleFromXdmNode(XMLNodeCursorImpl xmlNodeCursorImpl, XPathContext xctxt) {

		double result = 0.0;

		int nodeHandle = (xmlNodeCursorImpl.iter()).nextNode();
		DTM dtm = xctxt.getDTM(nodeHandle);

		XMLString xmlString = dtm.getStringValue(nodeHandle);
		java.lang.String rStrVal = xmlString.toString();
		
		result = (Double.valueOf(rStrVal)).doubleValue();

		return result;
	}
	
	/**
	 * Method definition, to check whether an XML Schema, supplied 
	 * built-in type name represents a numeric type.
	 * 
	 * @param typeName					  The supplied XML Schema type 
	 *                                    name string.
	 * @return                            Boolean value true or false
	 */
	protected boolean isXsBuiltInTypeNumeric(java.lang.String typeName) {

		boolean result = false;

		java.lang.String[] built_in_xs1_numeric_type_arr = new java.lang.String[] { "decimal", "double", "float", "integer", "long", 
																					"int", "short", "byte", "nonNegativeInteger", "unsignedLong",
																					"unsignedInt", "unsignedShort", "unsignedByte", "positiveInteger",
																					"nonPositiveInteger", "negativeInteger"};
		List<java.lang.String> strList = Arrays.asList(built_in_xs1_numeric_type_arr);
		if (strList.contains(typeName)) {
			result = true; 
		}

		return result;
	}
	
	/**
     * Method definition, to construct XML namespace PrefixResolver
     * object for XPath expression evaluation. 
     * 
     * @return                      PrefixResolver object instance
     */
    protected PrefixResolver getXMLNsPrefixResolver() {
    	
    	PrefixResolver result = null;
    	
        System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
    	
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    	docBuilderFactory.setNamespaceAware(true);
    	DocumentBuilder docBuilder = null; 
    	try {
    	   docBuilder = docBuilderFactory.newDocumentBuilder();
    	}
    	catch (Exception ex) {
    	   // no op
    	}
    	
    	Document document = docBuilder.newDocument();
    	Element elem = document.createElement("elem1");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:fn", "http://www.w3.org/2005/xpath-functions");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:math", "http://www.w3.org/2005/xpath-functions/math");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:map", "http://www.w3.org/2005/xpath-functions/map");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:array", "http://www.w3.org/2005/xpath-functions/array");
    	elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xs", "http://www.w3.org/2001/XMLSchema");
    	
    	document.appendChild(elem);
    	
    	result = new PrefixResolverDefault(elem);
    	
    	return result;
    }
    
    /**
     * Method definition, to get an xdm sequence type run-time, object 
     * representing XPath operator 'div', 'mod' result type. 
     * 
     * @param xObj1                          An XPath operator 'div', 'mod' 
     *                                       first operand.
     * @param xObj2                          An XPath operator 'div', 'mod' 
     *                                       second operand.
     * @return                               An XPathSequenceTypeData object instance,
     *                                       or null.
     */
    protected XPathSequenceType getXdmSequenceTypeResultData(XObject xObj1, XObject xObj2) {
  	  
    	XPathSequenceType result = null;

    	if ((xObj1 instanceof XSFloat) && (xObj2 instanceof XSDecimal)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_FLOAT);
    	}
    	else if ((xObj1 instanceof XSDecimal) && (xObj2 instanceof XSFloat)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_FLOAT); 
    	}
    	else if ((xObj1 instanceof XSFloat) && (xObj2 instanceof XSInteger)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_FLOAT);
    	}
    	else if ((xObj1 instanceof XSInteger) && (xObj2 instanceof XSFloat)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_FLOAT); 
    	}
    	else if ((xObj1 instanceof XSFloat) && (xObj2 instanceof XSFloat)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_FLOAT); 
    	}
    	else if ((xObj1 instanceof XSDecimal) && (xObj2 instanceof XSDouble)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE); 
    	}
    	else if ((xObj1 instanceof XSDouble) && (xObj2 instanceof XSDecimal)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE); 
    	}
    	else if ((xObj1 instanceof XSDouble) && (xObj2 instanceof XSFloat)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE);
    	}
    	else if ((xObj1 instanceof XSFloat) && (xObj2 instanceof XSDouble)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE); 
    	}
    	else if ((xObj1 instanceof XSDouble) && (xObj2 instanceof XSInteger)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE);
    	}
    	else if ((xObj1 instanceof XSInteger) && (xObj2 instanceof XSDouble)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE);
    	}
    	else if ((xObj1 instanceof XSDouble) && (xObj2 instanceof XSDouble)) {
    		result = new XPathSequenceType();
    		
    		result.setBuiltInSequenceType(XPathSequenceTypeSupport.XS_DOUBLE); 
    	}

    	return result;
  	}
    
    /**
     * Method definition, to do arithmetic operation on two java.math.BigDecimal 
     * values. 
     * 
     * @param lBigDecimal                        Arithmetic operator's first operand
     * @param rBigDecimal                        Arithmetic operator's second operand
     * @param opSymbol                           Arithmetic operator's type, i.e,
     *                                           whether its +, -, *, div, mod.
     * @param elemTemplateElement                The supplied ElemTemplateElement object 
     * @return                                   The result of arithmetic operation 
     * @throws TransformerException
     */
	private XObject arithmeticOpOnBigDecimalValues(BigDecimal lBigDecimal, BigDecimal rBigDecimal, 
			                                       String opSymbol, ElemTemplateElement elemTemplateElement) throws TransformerException {
		
		XObject result = null;

		if (opSymbol.equals(OP_SYMBOL_PLUS)) {
			result = new XSDecimal(lBigDecimal.add(rBigDecimal));
		}
		else if (opSymbol.equals(OP_SYMBOL_MINUS)) {
			result = new XSDecimal(lBigDecimal.subtract(rBigDecimal));				
		}
		else if (opSymbol.equals(OP_SYMBOL_MULT)) {
			result = new XSDecimal(lBigDecimal.multiply(rBigDecimal));
		}
		else if (opSymbol.equals(OP_SYMBOL_DIV)) {
			try {
			   result = new XSDecimal(lBigDecimal.divide(rBigDecimal));				
			}
			catch (ArithmeticException ex) {
			   java.lang.String exceptionMesg = ex.getMessage();
			   
			   result = divOpArithmeticExceptionAction(lBigDecimal, rBigDecimal, exceptionMesg, elemTemplateElement);
			}
		}
		else if (opSymbol.equals(OP_SYMBOL_MOD)) {
			try {
				result = new XSDecimal(lBigDecimal.remainder(rBigDecimal));
			}
			catch (ArithmeticException ex) {				  
				error(DIV_BY_ZERO_ERR_MESG, new String[] {"FOAR0001"}, elemTemplateElement);
			}
		}

		return result;
	}

}
