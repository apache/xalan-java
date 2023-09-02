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
package org.apache.xpath.composite;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.xs.types.XSBoolean;
import org.apache.xpath.xs.types.XSDate;
import org.apache.xpath.xs.types.XSDateTime;
import org.apache.xpath.xs.types.XSDayTimeDuration;
import org.apache.xpath.xs.types.XSDecimal;
import org.apache.xpath.xs.types.XSDouble;
import org.apache.xpath.xs.types.XSDuration;
import org.apache.xpath.xs.types.XSFloat;
import org.apache.xpath.xs.types.XSInt;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSLong;
import org.apache.xpath.xs.types.XSNumericType;
import org.apache.xpath.xs.types.XSString;
import org.apache.xpath.xs.types.XSTime;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;
import org.apache.xpath.xs.types.XSYearMonthDuration;

/**
 * This class provides few utility methods, to help implement
 * XPath 3.1 sequence type expressions.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-sequencetype-syntax
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeSupport {
    
    // Following are various constant int values, denoting 
    // XPath 3.1 and XML Schema data types.
        
    public static int EMPTY_SEQUENCE = 1;
    
    // Represents both XalanJ legacy boolean data type,
    // and XML Schema data type xs:boolean.
    public static int BOOLEAN = 2;
    
    // Represents both XalanJ legacy string data type,
    // and XML Schema data type xs:string.
    public static int STRING = 3;
    
    public static int XS_DATE = 4;
    
    public static int XS_DATETIME = 5;
    
    public static int XS_TIME = 6;
    
    public static int XS_DURATION = 7;
    
    public static int XS_DAYTIME_DURATION = 8;
    
    public static int XS_YEARMONTH_DURATION = 9;
    
    public static int XS_DECIMAL = 10;
    
    public static int XS_INTEGER = 11;
    
    public static int XS_LONG = 12;
    
    public static int XS_INT = 13;
    
    public static int XS_DOUBLE = 14;
    
    public static int XS_FLOAT = 15;
        
    public static class OccurenceIndicator {
       // Represents the sequence type occurrence indicator '?'
       public static int ZERO_OR_ONE = 1;
       
       // Represents the sequence type occurrence indicator '*'
       public static int ZERO_OR_MANY = 2;
       
       // Represents the sequence type occurrence indicator '+'
       public static int ONE_OR_MANY = 3;
    }
    
    public static String Q_MARK = "?";
    
    public static String STAR = "*";
    
    public static String PLUS = "+";
    
    /**
     * This method converts/casts an xdm source value represented by an XObject
     * object instance, to a value of another type. This method is called 
     * recursively at certain places.
     *  
     * @param srcValue                     an XObject object instance that represents a
     *                                     source xdm value. 
     * @param sequenceTypeXPathExprStr     a string representing an XPath 3.1 sequence type,
     *                                     conforming to which the source value srcValue needs
     *                                     to be converted. 
     * @param xctxt                        the current XPath context object.
     * 
     * @return                             an XObject object instance produced, as a result of data type 
     *                                     conversion performed by this method on an object instance
     *                                     srcValue.
     *                                
     * @throws TransformerException 
     */
    public static XObject convertXDMValueToAnotherType(XObject srcValue, String sequenceTypeXPathExprStr, 
                                                                                       XPathContext xctxt) throws TransformerException {
        XObject result = null;
        
        final int contextNode = xctxt.getContextNode(); 
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        try {
            XPath seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, 
                                                                            xctxt.getNamespaceContext(), XPath.SELECT, null, true);
            
            XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, 
                                                                                    xctxt.getNamespaceContext());
            
            SequenceTypeData seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
            
            int expectedType = seqExpectedTypeData.getSequenceType();            
            int itemTypeOccurenceIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
            
            if (srcValue instanceof XString) {
                String srcStrVal = ((XString)srcValue).str();
                
                if (expectedType == STRING) {
                   // The source and expected data types are same. Return the original value unchanged.
                   result = srcValue; 
                }
                else {
                   result = convertStringValueToAnExpectedType(srcStrVal, expectedType);
                }
            }
            else if (srcValue instanceof XSString) {           
               String srcStrVal = ((XSString)srcValue).stringValue();
               
               if (expectedType == STRING) {
                  // The source and expected data types are same. Return the original value unchanged.
                  result = srcValue; 
               }
               else {
                  result = convertStringValueToAnExpectedType(srcStrVal, expectedType);
               }
            }
            else if (srcValue instanceof XNumber) {
               XSDouble xsDouble = new XSDouble(((XNumber)srcValue).num());
               result = performXDMNumericTypeConversion(xsDouble, expectedType);
            }
            else if (srcValue instanceof XSNumericType) {
               result = performXDMNumericTypeConversion((XSNumericType)srcValue, expectedType); 
            }
            else if (srcValue instanceof XBoolean) {
               String srcStrVal = ((XBoolean)srcValue).str();
               if (expectedType == BOOLEAN) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The boolean value " + srcStrVal + " cannot be "
                                                                            + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator); 
               }
            }
            else if (srcValue instanceof XSBoolean) {
               String srcStrVal = ((XSBoolean)srcValue).stringValue();
               if (expectedType == BOOLEAN) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The boolean value " + srcStrVal + " cannot be "
                                                                             + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator); 
               } 
            }
            else if (srcValue instanceof XSDate) {
               String srcStrVal = ((XSDate)srcValue).stringValue();
               if (expectedType == XS_DATE) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The xs:date value " + srcStrVal + " cannot be "
                                                                            + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator);   
               } 
            }
            else if (srcValue instanceof XSDateTime) {
               String srcStrVal = ((XSDateTime)srcValue).stringValue();
               if (expectedType == XS_DATETIME) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The xs:dateTime value " + srcStrVal + " cannot be "
                                                                                + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator);     
               } 
            }
            else if (srcValue instanceof XSTime) {
               String srcStrVal = ((XSTime)srcValue).stringValue();
               if (expectedType == XS_TIME) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The xs:time value " + srcStrVal + " cannot be "
                                                                            + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator);       
               }
            }
            else if (srcValue instanceof XSDuration) {
               String srcStrVal = ((XSDuration)srcValue).stringValue();
               if (expectedType == XS_DURATION) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The xs:duration value " + srcStrVal + " cannot be "
                                                                                + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator);       
               } 
            }
            else if (srcValue instanceof XSDayTimeDuration) {
               String srcStrVal = ((XSDayTimeDuration)srcValue).stringValue();
               if (expectedType == XS_DAYTIME_DURATION) {
                  result = srcValue; 
               }
               else {
                  throw new TransformerException("XTTE0570 : The xs:dayTimeDuration value " + srcStrVal + " cannot be "
                                                                                 + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator);       
               } 
            }
            else if (srcValue instanceof XSYearMonthDuration) {
               String srcStrVal = ((XSYearMonthDuration)srcValue).stringValue();
               if (expectedType == XS_YEARMONTH_DURATION) {
                  result = srcValue; 
               }
               else {
                   throw new TransformerException("XTTE0570 : The xs:yearMonthDuration value " + srcStrVal + " cannot be "
                                                                                  + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + ".", srcLocator);       
               } 
            }
            else if (srcValue instanceof XSUntyped) {
               String srcStrVal = ((XSUntyped)srcValue).stringValue();
               result = convertXDMValueToAnotherType(new XSString(srcStrVal), sequenceTypeXPathExprStr, xctxt);
            }
            else if (srcValue instanceof XSUntypedAtomic) {
               String srcStrVal = ((XSUntypedAtomic)srcValue).stringValue();
               result = convertXDMValueToAnotherType(new XSString(srcStrVal), sequenceTypeXPathExprStr, xctxt);
            }
            else if (srcValue instanceof XNodeSet) {
                XNodeSet xdmNodeSet = (XNodeSet)srcValue;
                
                int nodeSetLen = xdmNodeSet.getLength();
                
                if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == 
                                                                                                     OccurenceIndicator.ZERO_OR_ONE))) {
                    throw new TransformerException("XTTE0570 : A sequence of size " + nodeSetLen + ", cannot be cast to a type " 
                                                                                                         + sequenceTypeXPathExprStr + ".", srcLocator);  
                }
                else { 
                    ResultSequence convertedResultSeq = new ResultSequence();
                    
                    DTMIterator dtmIter = xdmNodeSet.iterRaw();
                    
                    int nextNodeDtmHandle;
                           
                    while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {               
                       XNodeSet nodeSetItem = new XNodeSet(nextNodeDtmHandle, xctxt);
                       String nodeStrVal = nodeSetItem.str();
                       
                       String sequenceTypeNewXPathExprStr = null;                    
                       if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                           sequenceTypeXPathExprStr.endsWith(PLUS)) {
                          sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                       }
                       
                       XObject xObject = convertXDMValueToAnotherType(new XSString(nodeStrVal), sequenceTypeNewXPathExprStr, xctxt);
                       
                       convertedResultSeq.add(xObject);
                    }
                    
                    result = convertedResultSeq;
                }
            }
            else if (srcValue instanceof ResultSequence) {
                ResultSequence srcResultSeq = (ResultSequence)srcValue;
                
                int seqLen = srcResultSeq.size();
                
                if ((seqLen > 0) && (expectedType == EMPTY_SEQUENCE)) {
                   throw new TransformerException("XTTE0570 : The sequence doesn't conform to an expected type empty-sequence(). "
                                                                                               + "The supplied sequence has size " + seqLen + ".", srcLocator);  
                }
                else if ((seqLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == 
                                                                                                 OccurenceIndicator.ZERO_OR_ONE))) {
                    throw new TransformerException("XTTE0570 : A sequence of size " + seqLen + ", cannot be cast to a type " 
                                                                                                                + sequenceTypeXPathExprStr + ".", srcLocator); 
                }
                else {
                    String sequenceTypeNewXPathExprStr = null;                    
                    if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                        sequenceTypeXPathExprStr.endsWith(PLUS)) {
                       sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                    }
                    
                    ResultSequence convertedResultSeq = new ResultSequence();
                    
                    for (int idx = 0; idx < srcResultSeq.size(); idx++) {
                       XObject seqItem = (XObject)(srcResultSeq.item(idx));
                       XObject convertedSeqItem = convertXDMValueToAnotherType(seqItem, sequenceTypeNewXPathExprStr, xctxt);
                       convertedResultSeq.add(convertedSeqItem);
                    }
                    
                    result = convertedResultSeq; 
                }
            }
        }
        catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator); 
        }
        
        return result;
    }
    
    /**
     * Convert a string value to an expected xdm type.
     * 
     * @throws TransformerException
     */
    private static XObject convertStringValueToAnExpectedType(String srcStrVal, int expectedType) throws TransformerException {
        
        XObject result = null;
        
        try {
            if (expectedType == BOOLEAN) {
               if ("0".equals(srcStrVal) || "false".equals(srcStrVal)) {
                  result = new XSBoolean(false); 
               }
               else if ("1".equals(srcStrVal) || "true".equals(srcStrVal)) {
                  result = new XSBoolean(true); 
               }
            }
            else if (expectedType == XS_DATE) {
               result = XSDate.parseDate(srcStrVal); 
            }
            else if (expectedType == XS_DATETIME) {
               result = XSDateTime.parseDateTime(srcStrVal); 
            }
            else if (expectedType == XS_DURATION) {
               result = XSDuration.parseDuration(srcStrVal); 
            }
            else if (expectedType == XS_DAYTIME_DURATION) {
               result = XSDayTimeDuration.parseDayTimeDuration(srcStrVal); 
            }
            else if (expectedType == XS_YEARMONTH_DURATION) {
               result = XSYearMonthDuration.parseYearMonthDuration(srcStrVal); 
            }
            else {
               throw new TransformerException("XTTE0570 : The string value '" + srcStrVal + "' cannot be "
                                                                                                  + "cast to a type " + getDataTypeNameFromIntValue(expectedType) + "."); 
            }
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The string value '" + srcStrVal + "' cannot be cast to "
                                                                                               + "a type " + getDataTypeNameFromIntValue(expectedType) + ".");
        }
        
        return result;
    }
    
    /**
     * This method performs XPath numeric type conversions, and numeric type
     * promotion as defined by XPath 3.1 spec (ref, https://www.w3.org/TR/xpath-31/#promotion).
     */
    private static XObject performXDMNumericTypeConversion(XSNumericType srcXsNumericType, 
                                                                             int expectedType) throws TransformerException {
        XObject result = null;
        
        String srcStrVal = srcXsNumericType.stringValue();
        
        try {
            if (srcXsNumericType instanceof XSFloat) {           
               if (expectedType == XS_FLOAT) {
                  // The source and expected data types are same. Return the original value unchanged.
                  result = srcXsNumericType; 
               }
               else if (expectedType == XS_DOUBLE) {
                  result = new XSDouble(srcStrVal);
               }
            }
            else if (srcXsNumericType instanceof XSDecimal) {           
               if (expectedType == XS_DECIMAL) {
                  // The source and expected data types are same. Return the original value unchanged.
                  result = srcXsNumericType; 
               }
               else if (expectedType == XS_FLOAT) {
                  result = new XSFloat(srcStrVal); 
               }
               else if (expectedType == XS_DOUBLE) {
                  result = new XSDouble(srcStrVal); 
               }
            }
            else if (expectedType == XS_DECIMAL) {
               result = new XSDecimal(srcStrVal);  
            }
            else if (expectedType == XS_INTEGER) {
               result = new XSInteger(srcStrVal); 
            }
            else if (expectedType == XS_LONG) {
               result = new XSLong(srcStrVal); 
            }
            else if (expectedType == XS_INT) {
               result = new XSInt(srcStrVal);
            }
            else if (expectedType == XS_DOUBLE) {
               result = new XSDouble(srcStrVal); 
            }
            else if (expectedType == XS_FLOAT) {
               result = new XSFloat(srcStrVal); 
            }
            else if ((srcXsNumericType.stringType()).equals(getDataTypeNameFromIntValue(expectedType))) {
               // The source and expected data types are same. Return the original value unchanged.
               result = srcXsNumericType;
            }
            else {
               throw new TransformerException("XTTE0570 : The numeric value " + srcStrVal + " cannot be cast "
                                                                                                 + "to a type " + getDataTypeNameFromIntValue(expectedType) + ".");  
            }
        }
        catch (Exception ex) {
            throw new TransformerException("XTTE0570 : The numeric value " + srcStrVal + " cannot be cast "
                                                                                               + "to a type " + getDataTypeNameFromIntValue(expectedType) + ".");
        }
        
        return result;
    }
    
    /**
     * Given a primitive int value for an XDM data type, return the corresponding
     * data type's name.
     */
    private static String getDataTypeNameFromIntValue(int sequenceType) {
       
       String dataTypeName = null;
       
       if (sequenceType == EMPTY_SEQUENCE) {
          dataTypeName = "empty-sequence()";  
       }
       else if (sequenceType == BOOLEAN) {
          dataTypeName = "xs:boolean"; 
       }
       else if (sequenceType == STRING) {
          dataTypeName = "xs:string"; 
       }
       else if (sequenceType == XS_DATE) {
          dataTypeName = "xs:date"; 
       }
       else if (sequenceType == XS_DATETIME) {
          dataTypeName = "xs:dateTime";
       }
       else if (sequenceType == XS_TIME) {
          dataTypeName = "xs:time"; 
       }
       else if (sequenceType == XS_DURATION) {
          dataTypeName = "xs:duration"; 
       }
       else if (sequenceType == XS_DAYTIME_DURATION) {
          dataTypeName = "xs:dayTimeDuration"; 
       }
       else if (sequenceType == XS_YEARMONTH_DURATION) {
          dataTypeName = "xs:yearMonthDuration"; 
       }
       else if (sequenceType == XS_DECIMAL) {
          dataTypeName = "xs:decimal";
       }
       else if (sequenceType == XS_INTEGER) {
          dataTypeName = "xs:integer";
       }
       else if (sequenceType == XS_LONG) {
          dataTypeName = "xs:long"; 
       }
       else if (sequenceType == XS_INT) {
          dataTypeName = "xs:int"; 
       }
       else if (sequenceType == XS_DOUBLE) {
          dataTypeName = "xs:double"; 
       }
       else if (sequenceType == XS_FLOAT) {
          dataTypeName = "xs:float"; 
       }
       
       return dataTypeName;       
    }

}
