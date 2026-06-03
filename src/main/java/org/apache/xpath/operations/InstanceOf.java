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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.AnyURIDV;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathSequenceTypeArrayTest;
import org.apache.xpath.composite.XPathSequenceTypeData;
import org.apache.xpath.composite.XPathSequenceTypeKindTest;
import org.apache.xpath.composite.XPathSequenceTypeMapTest;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.composite.XPathSequenceTypeSupport.OccurrenceIndicator;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.types.XMLAttribute;
import org.apache.xpath.types.XSBase64Binary;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSGDay;
import org.apache.xpath.types.XSGMonth;
import org.apache.xpath.types.XSGMonthDay;
import org.apache.xpath.types.XSGYear;
import org.apache.xpath.types.XSGYearMonth;
import org.apache.xpath.types.XSHexBinary;
import org.apache.xpath.types.XSID;
import org.apache.xpath.types.XSIdRef;
import org.apache.xpath.types.XSLanguage;
import org.apache.xpath.types.XSNCName;
import org.apache.xpath.types.XSName;
import org.apache.xpath.types.XSNegativeInteger;
import org.apache.xpath.types.XSNmToken;
import org.apache.xpath.types.XSNonNegativeInteger;
import org.apache.xpath.types.XSNonPositiveInteger;
import org.apache.xpath.types.XSPositiveInteger;
import org.apache.xpath.types.XSShort;
import org.apache.xpath.types.XSUnsignedByte;
import org.apache.xpath.types.XSUnsignedInt;
import org.apache.xpath.types.XSUnsignedLong;
import org.apache.xpath.types.XSUnsignedShort;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSNormalizedString;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSToken;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An implementation of XPath 3.1 'instance of' operator.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class InstanceOf extends Operation
{

   private static final long serialVersionUID = -5941900193967481806L;

   /**
   * Apply the operation to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated left operand
   * @param right non-null reference to the evaluated right operand
   *
   * @return non-null reference to the XObject that represents the result of the operation
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right) 
                                                 throws javax.xml.transform.TransformerException
  {            
            
      XObject result = null;
      
      XPathContext xctxt = null;
      
      StylesheetRoot stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(this);
      if (stylesheetRoot != null) {
 		 TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
  	     xctxt = transformerImpl.getXPathContext();
  	  }
  	  else {
  		 xctxt = new XPathContext();
  	  }
	  	  
	  PrefixResolver xmlNsPrefixResolver = xctxt.getNamespaceContext();
	  
      XPathSequenceTypeData seqTypedData = null;      
      XPathSequenceTypeData castAsType = left.getCastAsType();
      
      if (castAsType != null) {    	  
    	  if (castAsType.equal((XPathSequenceTypeData)right)) {
    		 result = XBoolean.S_TRUE; 
    	  }
    	  else {
    		 result = XBoolean.S_FALSE; 
    	  }
    	  
    	  return result;
      }
      else {    	  	  	  
    	  seqTypedData = (XPathSequenceTypeData)right;  
      }
      
      int xsBuiltInSeqType = seqTypedData.getBuiltInSequenceType();      
      XPathSequenceTypeKindTest sequenceTypeKindTest = seqTypedData.getSequenceTypeKindTest();
      int seqTypeOccurenceIndicator = seqTypedData.getItemTypeOccurrenceIndicator();
      
      int xsBuiltInType = seqTypedData.getBuiltInSequenceType();
      if (xsBuiltInType == XPathSequenceTypeSupport.XS_INTEGER) {
    	  if (left instanceof XNumber) {
    		  XNumber xNumber = (XNumber)left;
    		  double dbl = xNumber.num();
    		  if ((dbl == (int)dbl) || (dbl == (long)dbl)) {
    			  return XBoolean.S_TRUE; 
    		  }
    	  }
    	  
    	  if (left instanceof XSDouble) {
    		  XSDouble xsDouble = (XSDouble)left;
    		  double dbl = xsDouble.doubleValue();
    		  if ((dbl == (int)dbl) || (dbl == (long)dbl)) {
    			  return XBoolean.S_TRUE; 
    		  }
    	  }
    	  
    	  if (left instanceof XSFloat) {
    		  XSFloat xsFloat = (XSFloat)left;
    		  double fl1 = xsFloat.floatValue();
    		  if ((fl1 == (int)fl1) || (fl1 == (long)fl1)) {
    			  return XBoolean.S_TRUE; 
    		  }
    	  }
    	  
    	  if (left instanceof XSDecimal) {
    		  XSDecimal xsDecimal = (XSDecimal)left;
    		  double dbl = xsDecimal.doubleValue();
    		  if ((dbl == (int)dbl) || (dbl == (long)dbl)) {
    			  return XBoolean.S_TRUE; 
    		  }
    	  }
      }
      
      if (left instanceof ElemFunctionItem) {
    	  /**
    	   * Converting, xsl:function declaration signature, to an 
    	   * equivalent XPath inline function declaration whose
    	   * function body is not specified.
    	   */
    	  
    	  java.lang.String xpathInlineFuncDefnStr = "function(";
    	  
    	  ElemFunctionItem elemFunctionItem = (ElemFunctionItem)left;
    	  ElemFunction elemFunction = elemFunctionItem.getElemFunction();
    	  ElemTemplateElement elemTemplateElement = elemFunction.getFirstChildElem();
    	  int paramCount = 0;
    	  while (elemTemplateElement != null) {
    		 if (elemTemplateElement instanceof ElemParam) {
    			paramCount++; 
    			ElemParam elemParam = (ElemParam)elemTemplateElement;
    			java.lang.String paramAsStr = elemParam.getAs();
    			xpathInlineFuncDefnStr = xpathInlineFuncDefnStr + "$a" + paramCount + " as " + paramAsStr + ",";
    		 }
    		 
    		 elemTemplateElement = elemTemplateElement.getNextSiblingElem();
    	  }
    	  
    	  if (xpathInlineFuncDefnStr.endsWith(",")) {
    		  int strLength1 = xpathInlineFuncDefnStr.length();
    		  xpathInlineFuncDefnStr = xpathInlineFuncDefnStr.substring(0, strLength1 - 1);
    		  xpathInlineFuncDefnStr = xpathInlineFuncDefnStr + ")";
    	  }
    	  else {
    		  xpathInlineFuncDefnStr = xpathInlineFuncDefnStr + ")"; 
    	  }
    	  
    	  java.lang.String funcReturnTypeAsStr = elemFunction.getAs();
    	  if (funcReturnTypeAsStr != null) {
    		  xpathInlineFuncDefnStr = xpathInlineFuncDefnStr + " as " + funcReturnTypeAsStr; 
    	  }
    	  
    	  xpathInlineFuncDefnStr = xpathInlineFuncDefnStr + " { 'no_op' }";
    	      	  
    	  XPath xpathObj = new XPath(xpathInlineFuncDefnStr, this, xmlNsPrefixResolver, XPath.SELECT, null);
    	  
    	  left = xpathObj.execute(xctxt, DTM.NULL, xmlNsPrefixResolver);
      }
      else if (left instanceof XdmAttributeItem) {
    	  if ((sequenceTypeKindTest != null) && (sequenceTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ATTRIBUTE_KIND) ||
    			                                (sequenceTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND)) {
    		  result = XBoolean.S_TRUE;
    	  }
    	  else {
    		  result = XBoolean.S_FALSE;
    	  }
    	  
    	  return result;
      }
      
      if (left instanceof XSQName) {
    	 java.lang.String localPart = ((XSQName)left).getLocalPart();
    	 if ((Constants.ANONYMOUS_FUNCTION).equals(localPart)) {
    		 left = new ResultSequence(); 
    	 }
      }
      
      if (left instanceof ResultSequence) {
    	 int rSeqLength = ((ResultSequence)left).size();    	 
    	 if (rSeqLength == 0) {
    		 if ((seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE) || 
    				                                                                 (seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY)) {
    			 result = XBoolean.S_TRUE; 
    		 }
    		 else if (xsBuiltInSeqType == XPathSequenceTypeSupport.EMPTY_SEQUENCE) {
    			 result = XBoolean.S_TRUE;
    		 }
    		 else if (seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ABSENT) {
    			 result = XBoolean.S_FALSE;
    		 }
    		 
    		 return result;
         }
    	 else if ((sequenceTypeKindTest != null) && (sequenceTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND)) {
    		 if (rSeqLength == 1) {
    			 result = XBoolean.S_TRUE;
    		 }
    		 else if ((seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) ||
    				  (seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY)) {
    			 // here, rSeqLength > 1
    			 result = XBoolean.S_TRUE; 
    		 }
    		 else {
    			 result = XBoolean.S_FALSE;  
    		 }
    		 
    		 return result;
    	 }
      }
      
      boolean isInstanceOfResult = false;
      
      try {    	 
    	 if (left instanceof XPathInlineFunction) {
    		ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();    		
            XObject xObj = XPathSequenceTypeSupport.castXdmValueToAnotherType(left, null, seqTypedData, 
            		                                                                           xctxt, elemTemplateElement.getPrefixTable());
            if (xObj != null) {
               isInstanceOfResult = true;	
            }
         }
    	 else if (left instanceof XNodeSetForDOM) {
    		XNodeSetForDOM xNodeSetForDOM = (XNodeSetForDOM)left;
    		int nodeHandle = xNodeSetForDOM.asNode(xctxt);
    		DTM dtm = xctxt.getDTM(nodeHandle);
    		Node node = dtm.getNode(nodeHandle);
    		java.lang.String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
    		xmlStr = xmlStr.trim();
    		if ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".equals(xmlStr)) {
    			// XPath 'instance of' operator's LHS is an empty sequence    			
    			boolean isSequenceCardinalityOk = false;    			
    			if ((seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE) || 
    				                                                                  (seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY)) {
    				isSequenceCardinalityOk = true; 
    			}
    			else if (xsBuiltInSeqType == XPathSequenceTypeSupport.EMPTY_SEQUENCE) {
    				isSequenceCardinalityOk = true;
    			}
    			else if (seqTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ABSENT) {
    				isSequenceCardinalityOk = false;
    			}    			

    			if (isSequenceCardinalityOk) {
    				result = XBoolean.S_TRUE;
    			}
    			else {
    				result = XBoolean.S_FALSE;
    			}
    			
    			return result;
    		}
    		
    		left = left.getFresh();
    	 }
         
    	 if (!isInstanceOfResult) {
    		 if ((left instanceof XMLNodeCursorImpl) && (xsBuiltInSeqType == XPathSequenceTypeSupport.XS_QNAME)) {
    			 java.lang.String str1 = ((XMLNodeCursorImpl)left).str();
    			 java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{.{1,}\\}.{1,}");
    			 java.util.regex.Matcher matcher = pattern.matcher(str1);
    			 if (matcher.matches()) {
    				 isInstanceOfResult = true; 
    			 }    			 
    		 }
    		 else {
    		     isInstanceOfResult = isInstanceOf(left, seqTypedData);
    		 }
    	 }
      }
      catch (Exception ex) {    	 
    	 isInstanceOfResult = false; 
      }
      
      result = (isInstanceOfResult ? XBoolean.S_TRUE : XBoolean.S_FALSE);  
      
      return result;
  }
  
  /**
   * Method definition, to check whether, an xdm value is an instance
   * of a specified xdm sequence type.
   * 
   * @param xdmValue				                The supplied xdm value
   * @param seqTypeData                             The supplied xdm sequence 
   *                                                type information.
   * @return                                        Boolean value true or false
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws TransformerException
   * @throws Exception
   */
  private boolean isInstanceOf(XObject xdmValue, XPathSequenceTypeData seqTypeData) 
		                                                                    throws ParserConfigurationException, SAXException, 
                                                                                   IOException, TransformerException, Exception {
    
      boolean isInstanceOf = false;
      
      XPathSequenceTypeKindTest sequenceTypeKindTest = seqTypeData.getSequenceTypeKindTest();
      
      final int seqTypeOccrIndicator = seqTypeData.getItemTypeOccurrenceIndicator();
      
      boolean isXdmValueString = ((xdmValue instanceof XString) || (xdmValue instanceof XSString));  
      
      if (isXdmValueString && "".equals(XslTransformEvaluationHelper.getStrVal(xdmValue)) && ((sequenceTypeKindTest.getKindVal() == XPathSequenceTypeSupport.DOCUMENT_KIND) && 
    		                                                                                  ((seqTypeOccrIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE) || 
    		                                                                                   (seqTypeOccrIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY)))) {
    	  isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSUntypedAtomic) && (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNTYPED_ATOMIC)) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSUntyped) && (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNTYPED)) {
          isInstanceOf = true;
      }
      else if (isXdmValueString && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) || 
									(seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XString) || (xdmValue instanceof XSNormalizedString)) && 
    		                                                ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
												             (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
												             (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XString) || (xdmValue instanceof XSToken)) && 
	    		                                           ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
	    		                                            (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
	    		                                            (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||		 
	    		                                            (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSAnyURI) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_URI) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSQName) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_QNAME) || 
                                                 (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XBoolean) || (xdmValue instanceof XSBoolean)) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.BOOLEAN) || 
    		                                                                           (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (xdmValue instanceof XNumber) {          
    	  if (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE) {
    		 isInstanceOf = true;
    	  }
    	  else if ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DECIMAL) ||
    			   (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DOUBLE)) {
             isInstanceOf = true; 
          }
          else {
             double doubleVal = ((XNumber)xdmValue).num();
             if ((doubleVal == (int)doubleVal) && (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INTEGER)) {
            	 isInstanceOf = true; 
             }
             else if ((doubleVal == (float)doubleVal) && (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_FLOAT)) {
            	 isInstanceOf = true; 
             }
          }    	  
      }
      else if ((xdmValue instanceof XSDouble) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DOUBLE) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
         isInstanceOf = true; 
      }
      else if ((xdmValue instanceof XSFloat) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_FLOAT) || 
    		                                     (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSInteger) || (xdmValue instanceof XSNonNegativeInteger) || 
									    		   (xdmValue instanceof XSPositiveInteger) || (xdmValue instanceof XSNonPositiveInteger) || 
									    		   (xdmValue instanceof XSNegativeInteger) || (xdmValue instanceof XSLong) || 
									    		   (xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
									    		   ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INTEGER) ||                                                   
									                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {          
    	  isInstanceOf = true;                    
      }
      else if (((xdmValue instanceof XSLong) || (xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		                                      ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_LONG) || 
                                                   (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {          
    	  isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSInt) || (xdmValue instanceof XSShort)) && 
    		                                     ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_INT) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {    	  
    	  isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSShort) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_SHORT) || 
                                                 (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSByte) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_BYTE) || 
                                                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedLong) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNSIGNED_LONG) || 
                                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedInt) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNSIGNED_INT) || 
                                                       (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedShort) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNSIGNED_SHORT) || 
                                                         (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSUnsignedByte) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_UNSIGNED_BYTE) || 
                                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSNonNegativeInteger) || (xdmValue instanceof XSPositiveInteger)) && 
    		                                                   ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NON_NEGATIVE_INTEGER) || 
                                                                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {    	  
    	  isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSPositiveInteger) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_POSITIVE_INTEGER) || 
                                                           (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if (((xdmValue instanceof XSNonPositiveInteger) || (xdmValue instanceof XSNegativeInteger)) && 
												    		   ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NON_POSITIVE_INTEGER) || 
												                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSNegativeInteger) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NEGATIVE_INTEGER) || 
                                                           (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }      
      else if ((xdmValue instanceof XSDecimal) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DECIMAL) || 
    		                                       (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDate) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DATE) || 
    		                                    (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDateTime) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DATETIME) || 
    		                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSTime) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TIME) || 
    		                                    (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDuration) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DURATION) || 
    		                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSDayTimeDuration) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_DAYTIME_DURATION) || 
    		                                               (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSYearMonthDuration) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_YEARMONTH_DURATION) || 
    		                                                 (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;
      }
      else if ((xdmValue instanceof XSGYearMonth) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR_MONTH) ||
    		                                          (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGYear) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GYEAR) ||
    		                                     (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGMonthDay) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH_DAY) ||
    		                                         (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGDay) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GDAY) ||
    		                                    (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSGMonth) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_GMONTH) ||
    		                                      (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSBase64Binary) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_BASE64BINARY) ||
                                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSHexBinary) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_HEXBINARY) ||
                                                     (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSLanguage) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_LANGUAGE) ||
    		                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
    		                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
    		                                        (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
                                                    (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSName) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NAME) ||
								                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
								                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
								                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
								                (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSNCName) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NCNAME) || 
    		                                      (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NAME) ||
                                                  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
                                                  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
                                                  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
                                                  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSNmToken) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NMTOKEN) ||
									               (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
									               (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
									               (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
									               (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSID) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ID) ||
								    		  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NCNAME) || 
								    		  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NAME) || 
								    		  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
								              (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
								              (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
								              (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if ((xdmValue instanceof XSIdRef) && ((seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_IDREF) ||
    		  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NCNAME) || 
    		  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NAME) || 
    		  (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.STRING) ||
              (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_NORMALIZED_STRING) ||
              (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_TOKEN) ||
              (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.XS_ANY_ATOMIC_TYPE))) {
          isInstanceOf = true;  
      }
      else if (xdmValue instanceof XMLNodeCursorImpl) {
    	  xdmValue = xdmValue.getFresh();
    	  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xdmValue;
    	  if (xmlNodeCursorImpl.m_is_for_each_group) {
    		  // This XMLNodeCursorImpl object is constructed via xsl:for-each-group 
    		  // instruction, to group a sequence of atomic values.
    		  
    		  DTMCursorIterator iter = xmlNodeCursorImpl.getContainedIter();    	  
    		  int node = iter.nextNode();
    		  DTM dtm = iter.getDTM(node);
    		  short nodeType = dtm.getNodeType(node);
    		  if (nodeType == DTM.TEXT_NODE) {
    			  Node nodeObj = dtm.getNode(node);
    			  java.lang.String nodeStrValue = nodeObj.getNodeValue();
    			  Double dblValue = null;
    			  XObject xObj = null;
    			  try {
    				  dblValue = Double.valueOf(nodeStrValue);
    			  }
    			  catch (NumberFormatException ex) {
    				  // no op 
    			  }

    			  if (dblValue != null) {
    				  xObj = new XSDecimal(BigDecimal.valueOf(dblValue));
    			  }
    			  else {
    				  xObj = new XSString(nodeStrValue);;  
    			  }

    			  XObject result = XPathSequenceTypeSupport.castXdmValueToAnotherType(xObj, seqTypeData, true);
    			  if (result != null) {
    				  isInstanceOf = true; 
    			  }
    		  }
    	  }
    	  else {    		 
             isInstanceOf = isNodesetInstanceOfType((XMLNodeCursorImpl)xdmValue, seqTypeData);
    	  }
      }
      else if (xdmValue instanceof ResultSequence) {
          isInstanceOf = isSequenceInstanceOfType((ResultSequence)xdmValue, seqTypeData); 
      }
      else if (xdmValue instanceof XPathMap) {
    	  isInstanceOf = isXdmMapConformsWithSeqType((XPathMap)xdmValue, seqTypeData);
      }
      else if (xdmValue instanceof XPathArray) {
    	  isInstanceOf = isXdmArrayConformsWithSeqType((XPathArray)xdmValue, seqTypeData);
      }
      else if (xdmValue instanceof XMLAttribute) {
    	  XMLAttribute xmlAttribute = (XMLAttribute)xdmValue;
    	  java.lang.String localName = xmlAttribute.getLocalName();
    	  java.lang.String nsUri = xmlAttribute.getNamespaceUri();
    	  QName attrQName = new QName(nsUri, localName);
    	  XPathSequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();
    	  if (seqTypeKindTest != null) {
    		  if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.NODE_KIND) || (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND)) {
    			  isInstanceOf = true; 
    		  }
    		  else if (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ATTRIBUTE_KIND) {
    			  java.lang.String expectedLocalName = seqTypeKindTest.getNodeLocalName();    		 
    			  if ((expectedLocalName != null) && !"".equals(expectedLocalName)) {
    				  java.lang.String expectedNsUri = seqTypeKindTest.getNodeNsUri();
    				  QName expectedQName = new QName(expectedNsUri, expectedLocalName);
    				  if (attrQName.equals(expectedQName)) {
    					  isInstanceOf = true;
    				  }
    			  }
    			  else {
    				  isInstanceOf = true;
    			  }
    		  }
    	  }
      }
      else if (sequenceTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND) {
    	  if (xdmValue instanceof ResultSequence) {
    		 ResultSequence rSeq = (ResultSequence)xdmValue;
    		 int rSeqSize = rSeq.size();
    		 if ((rSeqSize == 0) && ((seqTypeOccrIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
    				                 (seqTypeOccrIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
    			isInstanceOf = true; 
    		 }
    		 else if (rSeqSize == 1) {
    			isInstanceOf = true; 
    		 }
    		 else if ((seqTypeOccrIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) ||
    				  (seqTypeOccrIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY)) {
    			 // here, rSeqSize > 1
    			 isInstanceOf = true; 
    		 }
    	  }
    	  else {
    		 isInstanceOf = true; 
    	  }
      }
    
      return isInstanceOf;
  }

  
  /**
   * Method definition, to check whether, an xdm nodeset is an
   * instance of a specified xdm sequence type. 
   * 
   * @param nodeSet										The specified xdm nodeset
   * @param seqTypeData                                 The specified xdm sequence 
   *                                                    type information.
   * @return                                            Boolean value true or false
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws TransformerException
   * @throws Exception
   */
  private boolean isNodesetInstanceOfType(XMLNodeCursorImpl nodeSet, XPathSequenceTypeData seqTypeData) throws 
                                                                         ParserConfigurationException, SAXException, 
                                                                         IOException, TransformerException, Exception {
	  
	  boolean isInstanceOf = false;
          
	  int nodeSetLen = nodeSet.getLength();
	  	  	  	  
	  XPathSequenceTypeKindTest seqTypeKindTest = seqTypeData.getSequenceTypeKindTest();
	  int itemTypeOccurenceIndicator = seqTypeData.getItemTypeOccurrenceIndicator();
	  
	  if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND)) {
		  if ((nodeSetLen == 0) && ((itemTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) || 
				                                                              (itemTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_ONE))) {
			  isInstanceOf = true; 
		  }
		  else if (nodeSetLen == 1) {
			  isInstanceOf = true; 
		  }
		  else if ((itemTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ZERO_OR_MANY) ||
				   (itemTypeOccurenceIndicator == XPathSequenceTypeSupport.OccurrenceIndicator.ONE_OR_MANY)) {
			  // here, nodeSetLen > 1
			  isInstanceOf = true; 
		  }
	  }
	  else if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE))) {
		  isInstanceOf = false; 
	  }
	  else {
		  DTMCursorIterator dtmIter = nodeSet.iterRaw();

		  List<Boolean> nodeSetSequenceTypeResultList = new ArrayList<Boolean>();

		  int nextNode;
		  while ((nextNode = dtmIter.nextNode()) != DTM.NULL) {			   
			  DTM dtm = dtmIter.getDTM(nextNode);
			  java.lang.String nodeName = dtm.getNodeName(nextNode);
			  java.lang.String nodeNsUri = dtm.getNamespaceURI(nextNode);
			  
			  short nodeType = dtm.getNodeType(nextNode);

			  if (nodeType == DTM.DOCUMENT_NODE) {				  
				  if ((seqTypeKindTest != null) && (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.DOCUMENT_KIND)) {
					  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
				  }
				  else {
					  isInstanceOf = false;
					  
					  break;
				  }
			  }
			  else if (nodeType == DTM.ELEMENT_NODE) {
				  XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(nextNode, dtmIter.getDTMManager());				  
				  if (seqTypeKindTest != null) {					  
					  XPathSequenceTypeKindTest seqTypeKindTest2 = xmlNodeCursorImpl.getSeqTypeKindTest();
					  if ((nodeSetLen == 1) && (seqTypeKindTest2 != null) && seqTypeKindTest2.equal(seqTypeKindTest)) {
						 return true; 
					  }
					  
					  java.lang.String elemNodeKindTestNodeName = seqTypeKindTest.getNodeLocalName();
					  if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
							  																XPathSequenceTypeSupport.STAR.equals(elemNodeKindTestNodeName)) {
						  elemNodeKindTestNodeName = nodeName;  
					  }

					  if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
							                                                       && (XPathSequenceTypeSupport.isTwoXmlNamespaceValuesEqual(nodeNsUri, 
							                                                    		                                       seqTypeKindTest.getNodeNsUri()))) {
						  XSTypeDefinition xsTypeDefn = seqTypeData.getXsTypeDefinition();
						  if (xsTypeDefn != null) {
							  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, dtmIter.getDTMManager());
							  if (XPathSequenceTypeSupport.isXdmElemNodeValidWithSchemaType(node, m_xctxt, xsTypeDefn)) {
								  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true));
							  }
							  else {
								  isInstanceOf = false;
								  
								  break;
							  }
						  }
						  else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(seqTypeKindTest.getDataTypeUri())) {
							  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, dtmIter.getDTMManager());
							  
							  /**
							   * Check whether this element node has complexContent (i.e, presence of
							   * child element or attribute). If 'yes' then instance of check will be 
							   * false for this case.
							   */
							  node = (XMLNodeCursorImpl)(node.getFresh());
							  DTMCursorIterator dtmIter1 = ((XMLNodeCursorImpl)node).iterRaw();
							  int nodeHandle = dtmIter1.nextNode();
							  DTM dtm1 = m_xctxt.getDTM(nodeHandle);
							  Node node1 = dtm1.getNode(nodeHandle);
							  							  
							  NodeList childNodes = node1.getChildNodes();
							  boolean isComplexContent = false;
							  for (int idx = 0; idx < childNodes.getLength(); idx++) {
								  Node childNode = childNodes.item(idx);
								  if (childNode.getNodeType() == Node.ELEMENT_NODE) {
									  isComplexContent = true;
									  
									  break;
								  }
							  }
							  
							  if (!isComplexContent) {
								  NamedNodeMap attrNodes = node1.getAttributes();							  
								  for (int idx = 0; idx < attrNodes.getLength(); idx++) {
									  Node attrNode = attrNodes.item(idx);
									  java.lang.String nodeNameStr = attrNode.getNodeName();									
									  if (!"xmlns".equals(nodeNameStr)) {
										  isComplexContent = true;
										  
										  break;
									  }
								  }
							  }
							  
							  if (isComplexContent) {
								  isInstanceOf = false;
								  
								  break;
							  }
							  else {
								  dtmIter1.reset();
							  }
							  
							  java.lang.String dataTypeLocalName = seqTypeKindTest.getDataTypeLocalName();
							  
							  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)m_xctxt.getNamespaceContext();
							  List<XMLNSDecl> prefixTable = null;
							  if (elemTemplateElement != null) {
								  prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
							  }
							  java.lang.String xmlSchemaNsPrefix = XslTransformEvaluationHelper.getPrefixFromNsUri(XMLConstants.
									                                                                                   W3C_XML_SCHEMA_NS_URI, prefixTable);
				              
							  java.lang.String xpathConstructorFuncExprStr = null;
							  if (xmlSchemaNsPrefix != null) {
								  xpathConstructorFuncExprStr = xmlSchemaNsPrefix + ":" + dataTypeLocalName + "('" + node.str() + "')";
								  xpathConstructorFuncExprStr += " instance of " + xmlSchemaNsPrefix + ":" + dataTypeLocalName;
							  }
							  else {
								  xpathConstructorFuncExprStr = "xs:" + dataTypeLocalName + "('" + node.str() + "')";
								  xpathConstructorFuncExprStr += " instance of xs:" + dataTypeLocalName; 
							  }
							  
							  XPath xpath = new XPath(xpathConstructorFuncExprStr, m_xctxt.getSAXLocator(), m_xctxt.getNamespaceContext(), 
                                      																 XPath.SELECT, null);
							  XObject xObj = null;
							  try {
							     xObj = xpath.executeInstanceOf(m_xctxt, DTM.NULL, null);
							     isInstanceOf = ((xObj.bool() == true) ? true : false);
							  }
							  catch (TransformerException ex) {
								  isInstanceOf = false;
								  
								  break;
							  }
							  if (isInstanceOf) {
								 nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 break; 
							  }
						  }
						  else {
							  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.SCHEMA_ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
                              																	&& (XPathSequenceTypeSupport.isTwoXmlNamespaceValuesEqual(nodeNsUri, 
                              																				seqTypeKindTest.getNodeNsUri()))) {
						  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
						  XSModel xsModel = stylesheetRoot.getXsModel();
						  if (xsModel != null) {
							  XSElementDeclaration elemDecl = xsModel.getElementDeclaration(elemNodeKindTestNodeName, seqTypeKindTest.getNodeNsUri());
							  if (elemDecl != null) {
								 nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 /**
								  * When an XML input document has been validated with a schema but the schema
								  * doesn't have a global element declaration for this element node, we
								  * produce 'instance of' result as false, instead of emitting an XPath
								  * dynamic error.    
								  */
								 isInstanceOf = false;
								 
								 break; 
							  }
						  }
						  else {
							  // When an XML input document has not been validated with a schema, we produce 
							  // 'instance of' result as false, instead of emitting an XPath dynamic error.
							  isInstanceOf = false;
							  
							  break; 
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.NODE_KIND) || 
							   (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND)) {
						  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
					  }
				  }
				  else {					  
					  int nodeHandle = xmlNodeCursorImpl.asNode(m_xctxt);
					  DTM dtm2 = m_xctxt.getDTM(nodeHandle);
					  java.lang.String nodeName2 = dtm2.getNodeName(nodeHandle);
					  int childNode = DTM.NULL;
					  int childNode2 = DTM.NULL;
					  childNode = dtm2.getFirstChild(nodeHandle);
					  if (childNode != DTM.NULL) {
						  childNode2 = dtm2.getFirstChild(childNode);
					  }

					  java.lang.String strValue = xmlNodeCursorImpl.str();
					  
					  int xsBuiltInSeqType = seqTypeData.getBuiltInSequenceType();
					  
					  java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("b_[0-9]{5}");
					  java.util.regex.Matcher matcher = pattern.matcher(nodeName2);
					  try {
						  if ((childNode != DTM.NULL) && (childNode2 == DTM.NULL) && matcher.matches()) {
							  if ((xsBuiltInSeqType == XPathSequenceTypeSupport.XS_ANY_URI) && ((new AnyURIDV()).getActualValue(strValue, null) != null)) {						  
								  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true));
							  }
						  }
						  else {
							  isInstanceOf = false;

							  break;
						  }
					  }
					  catch (InvalidDatatypeValueException ex) {
						  isInstanceOf = false;

						  break;
					  }
				  }
			  }
			  else if (nodeType == DTM.ATTRIBUTE_NODE) {				  
				  if (seqTypeKindTest != null) {
					  java.lang.String attrNodeKindTestNodeName = seqTypeKindTest.getNodeLocalName();
					  if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
							  XPathSequenceTypeSupport.STAR.equals(attrNodeKindTestNodeName)) {
						  attrNodeKindTestNodeName = nodeName;  
					  }

					  if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
																						  && (XPathSequenceTypeSupport.isTwoXmlNamespaceValuesEqual(
																								  nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
						  XSTypeDefinition xsTypeDefn = seqTypeData.getXsTypeDefinition();
						  if (xsTypeDefn != null) {
							  if (xsTypeDefn instanceof XSSimpleType) {
								  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)xsTypeDefn;
								  XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, dtmIter.getDTMManager());
								  try {
								      xsSimpleTypeDecl.validate(node.str(), null, null);
								      nodeSetSequenceTypeResultList.add(Boolean.valueOf(true));
								  }
								  catch (InvalidDatatypeValueException ex) {
									  isInstanceOf = false;
									  
									  break;
								  }
							  }
							  else {
								  isInstanceOf = false;
								  
								  break; 
							  }
						  }
						  else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(seqTypeKindTest.getDataTypeUri())) {
                              XMLNodeCursorImpl node = new XMLNodeCursorImpl(nextNode, dtmIter.getDTMManager());							  
							  java.lang.String dataTypeLocalName = seqTypeKindTest.getDataTypeLocalName();
							  java.lang.String xpathConstructorFuncExprStr = "xs:" + dataTypeLocalName + "('" + node.str() + "')";
							  xpathConstructorFuncExprStr += " instance of xs:" + dataTypeLocalName; 							  
							  XPath xpath = new XPath(xpathConstructorFuncExprStr, m_xctxt.getSAXLocator(), m_xctxt.getNamespaceContext(), 
                                      																 XPath.SELECT, null);
							  XObject xObj = null;
							  try {
							     xObj = xpath.executeInstanceOf(m_xctxt, DTM.NULL, null);
							     isInstanceOf = ((xObj.bool() == true) ? true : false);
							  }
							  catch (TransformerException ex) {
								  isInstanceOf = false;
								  
								  break;
							  }
							  if (isInstanceOf) {
								  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
								 break; 
							  } 
						  }
						  else {
						      nodeSetSequenceTypeResultList.add(Boolean.valueOf(true));
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.SCHEMA_ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
																								  && (XPathSequenceTypeSupport.isTwoXmlNamespaceValuesEqual(
																										  nodeNsUri, seqTypeKindTest.getNodeNsUri()))) {
						  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
						  XSModel xsModel = stylesheetRoot.getXsModel();
						  if (xsModel != null) {
							  XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrNodeKindTestNodeName, seqTypeKindTest.getNodeNsUri());
							  if (attrDecl != null) {
								 nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
							  }
							  else {
                                 /**
                                  * When an XML input document has been validated with a schema but the schema
                                  * doesn't have a global attribute declaration for this attribute node, we
                                  * produce 'instance of' result as false, instead of emitting an XPath
                                  * dynamic error.
                                  */
								 isInstanceOf = false;
								 
								 break; 
							  }
						  }
						  else {
							  // When an XML input document has not been validated with a schema, we produce 
							  // 'instance of' result as false, instead of emitting an XPath dynamic error.   
							  isInstanceOf = false;
							  
							  break; 
						  }
					  }
					  else if ((seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.NODE_KIND) || 
							  (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.ITEM_KIND)) {
						  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true));   
					  }   
				  }
				  else {
					  isInstanceOf = false;
					  
					  break;
				  } 
			  }
			  else if (nodeType == DTM.TEXT_NODE) {				  
				  if (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.TEXT_KIND) {
					  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
				  }
			  }
			  else if (nodeType == DTM.NAMESPACE_NODE) {				  
				  if (seqTypeKindTest.getKindVal() == XPathSequenceTypeSupport.NAMESPACE_NODE_KIND) {
					  nodeSetSequenceTypeResultList.add(Boolean.valueOf(true)); 
				  }
			  }
		  }

		  if (nodeSetSequenceTypeResultList.size() > 0 && (nodeSetSequenceTypeResultList.size() == nodeSetLen)) {
			  isInstanceOf = true; 
		  }
	  }
	  
	  return isInstanceOf;
  }
  
  /**
   * Method definition, to check whether, an xdm sequence object is
   * an instance of the specified xdm sequence type.
   * 
   * @param resultSeq								The supplied xdm sequence
   * @param seqTypeData                             The supplied xdm sequence 
   *                                                type information.
   * @return                                        Boolean value true or false
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws TransformerException
   * @throws Exception
   */
  private boolean isSequenceInstanceOfType(ResultSequence resultSeq, XPathSequenceTypeData seqTypeData) 
		                                                                            throws ParserConfigurationException, 
                                                                                           SAXException, IOException, 
                                                                                           TransformerException, Exception {
	  
	  boolean result = false;

	  int seqLen = resultSeq.size();

	  if ((seqLen == 0) && (seqTypeData.getItemTypeOccurrenceIndicator() == OccurrenceIndicator.ONE_OR_MANY)) {
		  result = false;  
	  }
	  else if ((seqLen > 0) && (seqTypeData.getBuiltInSequenceType() == XPathSequenceTypeSupport.EMPTY_SEQUENCE)) {
		  result = false;  
	  }
	  else if ((seqLen > 1) && (seqTypeData.getItemTypeOccurrenceIndicator() == OccurrenceIndicator.ZERO_OR_ONE)) {
		  result = false;
	  }

	  XPathSequenceTypeData sequenceTypeDataNew = new XPathSequenceTypeData();          
	  if (seqTypeData.getSequenceTypeKindTest() != null) {
		  sequenceTypeDataNew.setSequenceTypeKindTest(seqTypeData.getSequenceTypeKindTest()); 
	  }
	  else {
		  sequenceTypeDataNew.setBuiltInSequenceType(seqTypeData.getBuiltInSequenceType()); 
	  }

	  boolean isInstanceOfOnSeqItem = true;

	  for (int idx = 0; idx < resultSeq.size(); idx++) {
		  XObject seqItem = (XObject)(resultSeq.item(idx));
		  if (!isInstanceOf(seqItem, sequenceTypeDataNew)) {
			  isInstanceOfOnSeqItem = false;
			  
			  break;
		  }
	  }

	  result = isInstanceOfOnSeqItem;
	  
	  return result;
  }
  
  /**
   * Method definition, to checks whether, an xdm map conforms with 
   * the supplied sequence type.
   * 
   * @param map								The supplied xdm map object
   * @param seqTypeData						An xdm sequence type information
   * @return                                Boolean value true or false
   */
  private boolean isXdmMapConformsWithSeqType(XPathMap map, XPathSequenceTypeData seqTypeData) {
	  boolean isInstanceOf = false;
	  
	  XPathSequenceTypeMapTest sequenceTypeMapTest = seqTypeData.getSequenceTypeMapTest();
	  if (sequenceTypeMapTest != null) {
		 if (sequenceTypeMapTest.isAnyMapTest()) {
		    isInstanceOf = true;
		 }
	  }
	  
	  return isInstanceOf; 
  }
  
  /**
   * Method definition, to checks whether, an xdm array conforms with 
   * the supplied sequence type.
   * 
   * @param xpathArr						The supplied xdm array object
   * @param seqTypeData						An xdm sequence type information
   * @return                                Boolean value true or false
   */
  private boolean isXdmArrayConformsWithSeqType(XPathArray xpathArr, XPathSequenceTypeData seqTypeData) {
	  
	  boolean isInstanceOf = false;
	  
	  XPathSequenceTypeArrayTest sequenceTypeArrayTest = seqTypeData.getSequenceTypeArrayTest();
	  if (sequenceTypeArrayTest != null) {
		  if (sequenceTypeArrayTest.isAnyArrayTest()) {
			  isInstanceOf = true;
		  }
		  else {
			  List<XObject> nativeArr = xpathArr.getNativeArray();
			  Iterator<XObject> arrIter = nativeArr.iterator();
			  // We check below each of array items, with an expected type
			  isInstanceOf = true; 
			  while (arrIter.hasNext()) {
				  XObject arrItem = arrIter.next();
				  if (arrItem instanceof ResultSequence) {
					  arrItem = ((ResultSequence)arrItem).item(0);
				  }
				  XPathSequenceTypeData arrayItemTypeInfo = sequenceTypeArrayTest.getArrayItemTypeInfo();
				  try {
					  XObject arrayItemTypeCheckResult = XPathSequenceTypeSupport.castXdmValueToAnotherType(
							                                                                          arrItem, null, arrayItemTypeInfo, null);
					  if (arrayItemTypeCheckResult == null) {             				
						  isInstanceOf = false;
						  
						  break;
					  }
				  }
				  catch (TransformerException ex) {
					  isInstanceOf = false;
					  
					  break; 
				  }
			  } 	
		  }
	  }
	  
	  return isInstanceOf;
  }
  
}
