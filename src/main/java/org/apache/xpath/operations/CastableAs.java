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

import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.types.XSBase64Binary;
import org.apache.xpath.types.XSHexBinary;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSNormalizedString;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSToken;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Class definition, to implement XPath 3.1 operator 'castable as'.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class CastableAs extends XPathOperator
{

  private static final long serialVersionUID = 7722658885378043019L;

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
  public XObject operate(XObject left, XObject right) 
                                                 throws javax.xml.transform.TransformerException
  {
	  XObject result = null;	  	  
      
      XPathSequenceType seqTypedData = (XPathSequenceType)right;
      
      if (left instanceof XNumber) {
 		 left = XslTransformEvaluationHelper.getXNumberNormalizedValue((XNumber)left);  
 	  }
      
      try {    	  
    	 if ((left instanceof XSAnyAtomicType) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) || 
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNTYPED_ATOMIC))) {
    		 result = new XSBoolean(true);

    		 return result;
    	 }
    	 else if ((left instanceof XSBoolean) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_FLOAT) || 
    			                                  (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DOUBLE) ||
    			                                  (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DECIMAL) ||
    			                                  (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INTEGER))) {
    		 result = new XSBoolean(true);

    		 return result; 
    	 }
    	 else if (((left instanceof XNumber) || (left instanceof XSNumericType)) && 
    			                                                              ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) || 
                                                                               (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) || 
                                                                               (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN))) {
    		 result = new XSBoolean(true);

    		 return result;
    	 }
    	 else if ((left instanceof XSNormalizedString) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) || 
                                                           (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING))) {
    		 result = new XSBoolean(true);

    		 return result; 
    	 }
    	 else if ((left instanceof XSToken) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) || 
    			                                (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
                                                (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN))) {
             result = new XSBoolean(true);

             return result; 
         }
    	 else if ((left instanceof XSFloat) && (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DECIMAL)) {
    		 result = new XSBoolean(true);

    		 return result; 
         }
    	 else if (((left instanceof XSDecimal) || (left instanceof XSFloat) || (left instanceof XSDouble)) && 
    			                                                                       (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INTEGER)) {
             
    		 if (left instanceof XSDecimal) {
    			 // Precision may be lost but cast to integer is true
    			 
    			 result = new XSBoolean(true);
    		 }    		 
    		 else if (left instanceof XSFloat) {
    			 if (!(((XSFloat)left).infinite() || ((XSFloat)left).nan())) {
    				 // Precision may be lost but cast to integer is true
    				 
    				 result = new XSBoolean(true);
    			 }
    			 else {
    				 result = new XSBoolean(false);
    			 }
    		 }
    		 else if (left instanceof XSDouble) {
    			 if (!(((XSDouble)left).infinite() || ((XSDouble)left).nan())) {
    				 // Precision may be lost but cast to integer is true
    				 
    				 result = new XSBoolean(true);
    			 }
    			 else {
    				 result = new XSBoolean(false);
    			 }
    		 }    		 

    		 return result; 
         }
    	 else if (((left instanceof XSDecimal) || (left instanceof XSFloat) || (left instanceof XSDouble)) && 
                                                                           (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.BOOLEAN)) {
    		 // Precision may be lost but cast to boolean is true
    		 
    		 result = new XSBoolean(true);
    	 }
    	 else if ((left instanceof XSDuration) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DAYTIME_DURATION) || 
                                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_YEARMONTH_DURATION))) {
    		 result = new XSBoolean(true);
    		 
             return result; 
    	 }
    	 else if ((left instanceof XSDateTime) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TIME) || 
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DATE) ||
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR_MONTH) ||
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR) ||
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH_DAY) ||
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GDAY) ||
    			                                   (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH))) {    		 
    		 result = new XSBoolean(true);
    		 
             return result; 
    	 }
    	 else if ((left instanceof XSDate) && ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR_MONTH) || 
                                               (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR) ||
                                               (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH_DAY) ||
                                               (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GDAY) ||
                                               (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH))) {    		 
    		 result = new XSBoolean(true);

    		 return result; 
         }
    	 else if ((left instanceof XSBase64Binary) && (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_HEXBINARY)) {
    		 result = new XSBoolean(true);

    		 return result;
    	 }
    	 else if ((left instanceof XSHexBinary) && (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_BASE64BINARY)) {
    		 result = new XSBoolean(true);

    		 return result;
    	 }
    	 else if (((left instanceof XSUntypedAtomic) && (Constants.ATTRNAME_NAN).equals(((XSUntypedAtomic)left).stringValue())) && 
    			                                                                          ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_FLOAT) || 
    			                                                                          (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DOUBLE))) {
             result = new XSBoolean(true);

             return result; 
         }
    	 else if (((left instanceof XSUntypedAtomic) && (Constants.ATTRNAME_NAN).equals(((XSUntypedAtomic)left).stringValue())) && 
    			                                                                          ((seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DECIMAL) || 
    					                                                                  (seqTypedData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INTEGER))) {
    		 result = new XSBoolean(false);

    		 return result; 
    	 }
    	 
    	 if (left instanceof XSUntypedAtomic) {
    		XSUntypedAtomic xsUntypedAtomic = (XSUntypedAtomic)left;
    		java.lang.String str1 = xsUntypedAtomic.stringValue();
    		
    		if ((str1.contains("E") || str1.contains("e")) && (seqTypedData.getBuiltInSequenceType() 
    				                                                                              == XPathSequenceTypeSupport.XS_DECIMAL)) {
    			result = new XSBoolean(false);

       		    return result;
    		}
    	 }
    	  
    	 if (result == null) {
            result = XPathSequenceTypeSupport.castXdmValueToAnotherType(left, seqTypedData, true);
    	 }
    	          
         if (result != null) {
        	result = new XSBoolean(true); 
         }
         else {
        	XSTypeDefinition typeDefn = seqTypedData.getXsTypeDefinition();
        	
        	if ((typeDefn != null) && (typeDefn instanceof XSSimpleType)) {
        	    XSSimpleTypeDecl simpleTypeDecl = (XSSimpleTypeDecl)typeDefn;        	    
        	    java.lang.String inpStrValue = XslTransformEvaluationHelper.getStrVal(left);
        	    
        	    try {
					simpleTypeDecl.validate(inpStrValue, null, null);
					
					result = new XSBoolean(true); 
				} 
        	    catch (InvalidDatatypeValueException ex) {
					result = new XSBoolean(false); 
				}
        	}
        	else {
        		result = new XSBoolean(false);
        	}
         }
      }
      catch (javax.xml.transform.TransformerException ex) {
    	 result = new XSBoolean(false); 
      }
      
      return result;
  }
  
}
