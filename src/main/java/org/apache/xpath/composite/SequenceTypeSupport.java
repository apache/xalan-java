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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.apache.xalan.templates.ElemSequence;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.Function;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNodeSetForDOM;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;
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
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * This class provides few utility methods, to support 
 * evaluation of XPath 3.1 sequence type expressions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeSupport {
    
    /** 
     * Following are various constant int values, denoting XPath 3.1 and 
     * XML Schema data types.
     */
        
    public static int EMPTY_SEQUENCE = 1;
    
    /**
     * Represents both, an XML Schema data type xs:boolean and 
     * Xalan-J legacy boolean data type.
     */
    public static int BOOLEAN = 2;
    
    /**
     * Represents both, an XML Schema data type xs:string and 
     * Xalan-J legacy string data type.
     */
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
    
    public static int XS_UNTYPED_ATOMIC = 16;
    
    public static int XS_UNTYPED = 17;
    
    public static int XS_NORMALIZED_STRING = 18;
    
    public static int XS_TOKEN = 19;
    
    public static int XS_ANY_URI = 20;
    
    public static int XS_QNAME = 21;
        
    public static int XS_ANY_ATOMIC_TYPE = 50;
    
    public static int XS_NON_POSITIVE_INTEGER = 51;
    
    public static int XS_NEGATIVE_INTEGER = 52;
    
    public static int XS_NON_NEGATIVE_INTEGER = 53;
    
    public static int XS_POSITIVE_INTEGER = 54;
    
    public static int XS_SHORT = 55;
    
    public static int XS_BYTE = 56;
    
    public static int XS_UNSIGNED_LONG = 57;
    
    public static int XS_UNSIGNED_INT = 58;
    
    public static int XS_UNSIGNED_SHORT = 59;
    
    public static int XS_UNSIGNED_BYTE = 60;
    
    public static int XS_GYEAR_MONTH = 61;
    
    public static int XS_GYEAR = 62;
    
    public static int XS_GMONTH_DAY = 63;
    
    public static int XS_GDAY = 64;
    
    public static int XS_GMONTH = 65;
    
    public static int XS_BASE64BINARY = 66;
    
    public static int XS_HEXBINARY = 67;
    
    public static int XS_LANGUAGE = 68;
    
    public static int XS_NAME = 69;
    
    public static int XS_NCNAME = 70;
    
    public static int XS_NMTOKEN = 71;
    
    public static int XS_ID = 72;
    
    public static int XS_IDREF = 73;
    
    /** 
     * Following are constant int values denoting XPath 3.1 sequence
     * type KindTest expressions.
     */
    
    public static int ELEMENT_KIND = 101;
    
    public static int ATTRIBUTE_KIND = 102;
    
    public static int TEXT_KIND = 103;
    
    public static int NAMESPACE_NODE_KIND = 104;
    
    public static int NODE_KIND = 105;
    
    public static int ITEM_KIND = 106;
    
    public static int SCHEMA_ELEMENT_KIND = 107;
    
    public static int SCHEMA_ATTRIBUTE_KIND = 108;
    
    public static int DOCUMENT_KIND = 109;
    
    /**
     * Sequence type occurrence indicator values, for Xalan-J 
     * implementation.
     */
    public static class OccurrenceIndicator {       
       // No occurrence indicator has been specified within an 
       // XSL stylesheet in a sequence type expression.
       public static int ABSENT = 0;
       
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
    
    public static String INLINE_FUNCTION_PARAM_TYPECHECK_COUNT_ERROR = "INLINE_FUNCTION_PARAM_TYPECHECK_COUNT_ERROR";
    
    private static List<XMLNSDecl> m_PrefixTable;
    
    /**
     * This class specifies min and max value ranges for XML Schema 
     * built-in types derived from type xs:integer, as defined by 
     * XML Schema specification.
     */
    public static class XmlSchemaBuiltinNumericType {
    	
    	public static class Long {
    	   public static long MIN_INCLUSIVE = -9223372036854775808l;
    	   public static long MAX_INCLUSIVE = 9223372036854775807l;
    	}
    	
    	public static class Int {
     	   public static int MIN_INCLUSIVE = -2147483648;
     	   public static int MAX_INCLUSIVE = 2147483647;
     	}
    	
    	public static class Short {
      	   public static short MIN_INCLUSIVE = -32768;
      	   public static short MAX_INCLUSIVE = 32767;
      	}
    	
    	public static class Byte {
       	   public static byte MIN_INCLUSIVE = -128;
       	   public static byte MAX_INCLUSIVE = 127;
       	}
    	
    	public static class NonNegativeInteger {
           public static byte MIN_INCLUSIVE = 0;
        }
    	
    	public static class PositiveInteger {
           public static byte MIN_INCLUSIVE = 1;
        }
    	
    	public static class NonPositiveInteger {
           public static byte MAX_INCLUSIVE = 0;
        }
    	
    	public static class NegativeInteger {
           public static byte MAX_INCLUSIVE = -1;
        }
    	
    	public static class UnsignedLong {
    	   public static long MIN_INCLUSIVE = 0;
           public static String MAX_INCLUSIVE = new String("18446744073709551615");
        }
    	
    	public static class UnsignedInt {
     	   public static int MIN_INCLUSIVE = 0;
           public static long MAX_INCLUSIVE = 4294967295l;
        }
    	
    	public static class UnsignedShort {
      	   public static int MIN_INCLUSIVE = 0;
           public static int MAX_INCLUSIVE = 65535;
        }
    	
    	public static class UnsignedByte {
       	   public static int MIN_INCLUSIVE = 0;
           public static int MAX_INCLUSIVE = 255;
        }
    }
    
    /**
     * This method casts an XPath 3.1 xdm source value represented by an
     * XObject object instance, to a value of another xdm data type.
     * 
     * For XPath sequence type expressions that represent KindTest (i.e,
     * element(), attribute() etc), this method only checks whether an XML input
     * item conforms with the provided KindTest sequence type expression, and
     * returns an input value unchanged.
     * 
     * @param srcValue                        an XObject object instance that represents a source xdm value
     * @param sequenceTypeXPathExprStr        an XPath sequence type expression string
     * @param seqExpectedTypeDataInp          an XPath sequence type compiled expression
     * @param xctxt                           the current XPath context object
     * @param prefixTable                     an XSLT transformation run-time XML namespace bindings prefix table
     * 
     * @return                                an XObject object instance produced, as a result of data type
     *                                        conversion performed by this method on an object instance
     *                                        srcValue.
     * 
     * @throws TransformerException
     */
    public static XObject castXdmValueToAnotherType(XObject srcValue, String sequenceTypeXPathExprStr, 
                                                    SequenceTypeData seqExpectedTypeDataInp,  
                                                    XPathContext xctxt, List prefixTable) throws TransformerException {
    	XObject result = null;
    	
    	m_PrefixTable = prefixTable;    	
    	result = castXdmValueToAnotherType(srcValue, sequenceTypeXPathExprStr, null, xctxt);
    	
    	return result;
    }
    
    /**
     * This method, supports XPath implementation of "cast as", "castable as" and 
     * "treat as" expressions. 
     */
    public static XObject castXdmValueToAnotherType(XObject srcValue, SequenceTypeData expectedSeqTypeData, boolean isTreatAs) 
    		                                                                                throws TransformerException {
        XObject result = null;
        
        srcValue.setTreatAs(isTreatAs);
    	
        result = castXdmValueToAnotherType(srcValue, null, expectedSeqTypeData, null);

        return result;
    }
    
    
    /**
     * This method casts an XPath 3.1 xdm source value represented by an
     * XObject object instance, to a value of another xdm data type.
     * 
     * For XPath sequence type expressions that represent KindTest (i.e,
     * element(), attribute() etc), this method only checks whether an XML input
     * item conforms with the provided KindTest sequence type expression, and
     * returns an input value unchanged.
     *  
     * @param srcValue                     an XObject object instance that represents a
     *                                     source xdm value. 
     * @param sequenceTypeXPathExprStr     an XPath sequence type expression string 
     * @param expectedSeqTypeData          an XPath sequence type compiled expression                                                                  
     * @param xctxt                        the current XPath context object
     * 
     * @return                             an XObject object instance produced, as a result of data type 
     *                                     conversion performed by this method on an object instance
     *                                     srcValue.
     *                                
     * @throws TransformerException 
     */
    public static XObject castXdmValueToAnotherType(XObject srcValue, String sequenceTypeXPathExprStr, 
                                                    SequenceTypeData expectedSeqTypeData, XPathContext xctxt) 
                                                    		                                          throws TransformerException {
        XObject result = null;
        
        int contextNode = DTM.NULL;        
        SourceLocator srcLocator = null;
        
        if (xctxt != null) {
           contextNode = xctxt.getContextNode();        
           srcLocator = xctxt.getSAXLocator();
        }
        
        try {        	
            if ((srcValue instanceof XMLNodeCursorImpl) && (expectedSeqTypeData != null)) {
                if (expectedSeqTypeData.getBuiltInSequenceType() > 0) {
                	// When XPath "cast as" operator's LHS is a node and RHS is a built-in 
                	// simple type, we check "cast as" on string value of node.
                	srcValue = srcValue.getFresh();
                	String strValue = XslTransformEvaluationHelper.getStrVal(srcValue);
                	srcValue = new XSString(strValue);
                }
            }
        	
            XPath seqTypeXPath = null;
            XObject seqTypeExpressionEvalResult = null;
            SequenceTypeData seqExpectedTypeData = null;
            
            if ((xctxt != null) && (sequenceTypeXPathExprStr != null) && (expectedSeqTypeData == null)) {
            	seqTypeXPath = new XPath(sequenceTypeXPathExprStr, srcLocator, xctxt.getNamespaceContext(), 
            			                                                                          XPath.SELECT, null, true);            
            	seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());            
            	seqExpectedTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;
            }
            else {
            	seqExpectedTypeData = expectedSeqTypeData; 
            }

            int expectedType = seqExpectedTypeData.getBuiltInSequenceType();            
            int itemTypeOccurenceIndicator = seqExpectedTypeData.getItemTypeOccurrenceIndicator();
            SequenceTypeKindTest sequenceTypeKindTest = seqExpectedTypeData.getSequenceTypeKindTest();
            
            if (srcValue instanceof XPathNamedFunctionReference) {
            	XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)srcValue; 
            	SequenceTypeFunctionTest seqTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
            	if (seqTypeFunctionTest != null) {
            		if (seqTypeFunctionTest.isAnyFunctionTest()) {
            			return srcValue;
            		}
            		else {
            			// REVISIT        		     		 
            			List<String> funcParamSpecList = seqTypeFunctionTest.getTypedFunctionTestParamSpecList();
            			String funcReturnSeqType = seqTypeFunctionTest.getTypedFunctionTestReturnType();
            			
            			String funcName = xpathNamedFunctionReference.getFuncName();
            			int funcArity = xpathNamedFunctionReference.getArity();
            			FunctionTable funcTable = xctxt.getFunctionTable();
            			Object funcIdInFuncTable = funcTable.getFunctionId(funcName);
            			Function function = funcTable.getFunction((int)funcIdInFuncTable);
            			if (function != null) {
            				return srcValue; 
            			}
            		}
            	}
            }
            
            if ((srcValue instanceof ResultSequence) && (sequenceTypeKindTest == null)) {
            	ResultSequence rSeq = (ResultSequence)srcValue;
            	for (int idx = 0; idx < rSeq.size(); idx++) {
            		XObject xObj = rSeq.item(idx);
            		if ((xObj instanceof XSGYearMonth) && (expectedType != XS_GYEAR_MONTH)) {
            			String expectedTypeStr = sequenceTypeXPathExprStr; 
            			if (sequenceTypeXPathExprStr.endsWith("?") || sequenceTypeXPathExprStr.endsWith("*") || sequenceTypeXPathExprStr.endsWith("+")) {
            				expectedTypeStr = expectedTypeStr.substring(0, expectedTypeStr.length() - 1);
            			}
            			throw new TransformerException("XTTE0570 : An item of type xs:gYearMonth cannot be converted to " + expectedTypeStr + " as specified within an XSL stylesheet.");
            		}
            		else if ((xObj instanceof XSGYear) && (expectedType != XS_GYEAR)) {
            			String expectedTypeStr = sequenceTypeXPathExprStr; 
            			if (sequenceTypeXPathExprStr.endsWith("?") || sequenceTypeXPathExprStr.endsWith("*") || sequenceTypeXPathExprStr.endsWith("+")) {
            				expectedTypeStr = expectedTypeStr.substring(0, expectedTypeStr.length() - 1);
            			}
            			throw new TransformerException("XTTE0570 : An item of type xs:gYear cannot be converted to " + expectedTypeStr + " as specified within an XSL stylesheet.");
            		}
            		else if ((xObj instanceof XSGMonthDay) && (expectedType != XS_GMONTH_DAY)) {
            			String expectedTypeStr = sequenceTypeXPathExprStr; 
            			if (sequenceTypeXPathExprStr.endsWith("?") || sequenceTypeXPathExprStr.endsWith("*") || sequenceTypeXPathExprStr.endsWith("+")) {
            				expectedTypeStr = expectedTypeStr.substring(0, expectedTypeStr.length() - 1);
            			}
            			throw new TransformerException("XTTE0570 : An item of type xs:gMonthDay cannot be converted to " + expectedTypeStr + " as specified within an XSL stylesheet.");
            		}
            		else if ((xObj instanceof XSGDay) && (expectedType != XS_GDAY)) {
            			String expectedTypeStr = sequenceTypeXPathExprStr; 
            			if (sequenceTypeXPathExprStr.endsWith("?") || sequenceTypeXPathExprStr.endsWith("*") || sequenceTypeXPathExprStr.endsWith("+")) {
            				expectedTypeStr = expectedTypeStr.substring(0, expectedTypeStr.length() - 1);
            			}
            			throw new TransformerException("XTTE0570 : An item of type xs:gDay cannot be converted to " + expectedTypeStr + " as specified within an XSL stylesheet.");
            		}
            		else if ((xObj instanceof XSGMonth) && (expectedType != XS_GMONTH)) {
            			String expectedTypeStr = sequenceTypeXPathExprStr; 
            			if (sequenceTypeXPathExprStr.endsWith("?") || sequenceTypeXPathExprStr.endsWith("*") || sequenceTypeXPathExprStr.endsWith("+")) {
            				expectedTypeStr = expectedTypeStr.substring(0, expectedTypeStr.length() - 1);
            			}
            			throw new TransformerException("XTTE0570 : An item of type xs:gMonth cannot be converted to " + expectedTypeStr + " as specified within an XSL stylesheet.");
            		}
            	}
            }
            
            XMLNodeCursorImpl nodeSet = getNodeReference(srcValue);            
            XSTypeDefinition xsTypeDefinition = seqExpectedTypeData.getXsTypeDefinition();
            
            if ((nodeSet != null) && (xsTypeDefinition != null)) {
	            boolean isNodeValid = isXdmElemNodeValidWithSchemaType(nodeSet, xctxt, xsTypeDefinition);	            
	            if (isNodeValid) {
	               result = srcValue;
	               
	               if (!srcValue.isTreatAs()) {
	            	  // This is done for XPath "cast as" expression. Modifying the
	            	  // static type of xdm value this way, is currently supported only
	            	  // for user-defined schema types.
	                  result.setXsTypeDefinition(xsTypeDefinition);
	               }
	               else {
	            	  // Reset XPath treat as/castable as indicator of XObject value
	            	  srcValue.setTreatAs(false);
	            	  result.setTreatAs(false);
	               }
	               
	               return result;
	            }
            }
            else if ((nodeSet != null) && ((sequenceTypeKindTest != null) && (sequenceTypeKindTest.getKindVal() == 
            		                                                                             SequenceTypeSupport.SCHEMA_ELEMENT_KIND))) {            	
            	DTMCursorIterator dtmIter = nodeSet.iterRaw();
            	int nodeDtmHandle = dtmIter.nextNode();
            	DTM dtm = dtmIter.getDTM(nodeDtmHandle);
            	java.lang.String nodeName = dtm.getNodeName(nodeDtmHandle);
            	java.lang.String nodeNsUri = dtm.getNamespaceURI(nodeDtmHandle);
            	dtmIter.reset();
            	if ((nodeName.equals(sequenceTypeKindTest.getNodeLocalName())) && (SequenceTypeSupport.isTwoXmlNamespaceValuesEqual(nodeNsUri, 
            																						sequenceTypeKindTest.getNodeNsUri()))) {
            		StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
            		XSModel xsModel = stylesheetRoot.getXsModel();
            		if (xsModel != null) {
            			XSElementDeclaration elemDecl = xsModel.getElementDeclaration(nodeName, nodeNsUri);
            			if (elemDecl != null) {
            				if (isXdmElemNodeValidWithSchemaType(nodeSet, xctxt, elemDecl.getTypeDefinition())) {
            					result = srcValue;            				            				
            				
            					return result;
            				}
            			}
            			else {
            				throw new TransformerException("XPTY0004 : A sequence type schema-element test was requested, but a schema global element "
				            						                                + "declaration for element node '" + nodeName + "' is not available in "
				            						                                + "the schema fetched via xsl:import-schema instruction.");
            			}
            		}
            		else {
            			throw new TransformerException("XPTY0004 : A sequence type schema-element test was requested, but an XML input "
            																									+ "document has not been validated with a schema.");
            		}
            	}
            }
            
            if (srcValue != null) {
            	if (sequenceTypeKindTest != null) {
	            	if (sequenceTypeKindTest.getKindVal() == ITEM_KIND) {
	            		if (srcValue instanceof ResultSequence) {
	            		  ResultSequence rSeq = (ResultSequence)srcValue;
	               		  if (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) {
	               			 result = srcValue;   
	               		  }
	               		  else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY) && (rSeq.size() > 0)) {
	               			 result = srcValue; 
	               		  }
	               		  else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (rSeq.size() <= 1)) {
	               			 result = srcValue; 
	               		  }
	               		  
	               		  return result;
	               	   }
	            	   else if (srcValue instanceof XPathArray) {
	            		  XPathArray xpathArr = (XPathArray)srcValue;
	            		  if (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) {
	            			 result = srcValue;   
	            		  }
	            		  else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY) && (xpathArr.size() > 0)) {
	            			 result = srcValue; 
	            		  }
	            		  else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE) && (xpathArr.size() <= 1)) {
	            			 result = srcValue; 
	            		  }
	            		  
	            		  return result;
	            	   }
	            	}
	            	else if (sequenceTypeKindTest.getKindVal() == ELEMENT_KIND) {
	            	   if (srcValue instanceof XNodeSetForDOM) {	            		   
	            		   XNodeSetForDOM xNodeSetForDOM = (XNodeSetForDOM)srcValue;
	            		   Object obj1 = xNodeSetForDOM.object();
	            		   if (obj1 instanceof DTMNodeList) {	            			   
	            			   String dataTypeExpectedLocalName = sequenceTypeKindTest.getDataTypeLocalName();
	            			   String dataTypeExpectedNsUri = sequenceTypeKindTest.getDataTypeUri();
	            			   if ((dataTypeExpectedLocalName == null) || (Keywords.XS_UNTYPED.equals(dataTypeExpectedLocalName) && 
	            					                                       XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(dataTypeExpectedNsUri))) {
	            				   // An XPath sequence type expression, hasn't specified an XML Schema 
	            				   // data type or sequence type expression has specified the data type 
	            				   // as xs:untyped.	            				   
	            				   DTMNodeList dtmNodeList = (DTMNodeList)obj1;
		            			   DTMCursorIterator dtmCursorIter = dtmNodeList.getDTMIterator();
		            			   int nodeHandle = dtmCursorIter.nextNode();
		            			   DTM dtm = xctxt.getDTM(nodeHandle);
		            			   int childNodeHandle = dtm.getFirstChild(nodeHandle);	            				  
		            			   int nextSiblingNodeHandle = dtm.getNextSibling(childNodeHandle);
		            			   String nodeExpectedLocalName = sequenceTypeKindTest.getNodeLocalName();
		            			   String nodeExpectedNsUri = sequenceTypeKindTest.getNodeNsUri();	            			   
		            			   if ((childNodeHandle != DTM.NULL) && (nextSiblingNodeHandle == DTM.NULL) && 
		            					                                   ((itemTypeOccurenceIndicator == OccurrenceIndicator.ABSENT) || 
		            							                            (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE))) {
		            				   Node node = dtm.getNode(childNodeHandle);
		            				   String nodeLocalName = node.getLocalName();
		            				   String nodeNsUri = node.getNamespaceURI();	            				   	            				   
		            				   boolean isNodeNameOk = isNodeNameOk(nodeLocalName, nodeNsUri, nodeExpectedLocalName, nodeExpectedNsUri);
		            				   if (isNodeNameOk) {
		            					   result = new XMLNodeCursorImpl(childNodeHandle, xctxt);

			            				   return result;
		            				   }	            				   
		            			   }
		            			   else if ((childNodeHandle != DTM.NULL) && (nextSiblingNodeHandle != DTM.NULL) && 
		            					                                        ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) || 
		            					                                         (itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY))) {
		            				   List<Integer> seqNodeHandles = new ArrayList<Integer>();
		            				   Node node = dtm.getNode(childNodeHandle);
		            				   String nodeLocalName = node.getLocalName();
		            				   String nodeNsUri = node.getNamespaceURI();
		            				   boolean isNodeNameOk = isNodeNameOk(nodeLocalName, nodeNsUri, nodeExpectedLocalName, nodeExpectedNsUri);
		            				   if (isNodeNameOk) {
		            					   seqNodeHandles.add(Integer.valueOf(childNodeHandle));
		            				   }
		            				   node = dtm.getNode(nextSiblingNodeHandle);
		            				   isNodeNameOk = isNodeNameOk(nodeLocalName, nodeNsUri, nodeExpectedLocalName, nodeExpectedNsUri);
		            				   if (isNodeNameOk) {
		            					   seqNodeHandles.add(Integer.valueOf(nextSiblingNodeHandle));
		            				   }
		            				   while ((nextSiblingNodeHandle = dtm.getNextSibling(nextSiblingNodeHandle)) != DTM.NULL) {
		            					   node = dtm.getNode(nextSiblingNodeHandle);
			            				   isNodeNameOk = isNodeNameOk(nodeLocalName, nodeNsUri, nodeExpectedLocalName, nodeExpectedNsUri);
			            				   if (isNodeNameOk) {
			            					   seqNodeHandles.add(Integer.valueOf(nextSiblingNodeHandle));
			            				   } 
		            				   }

		            				   if (seqNodeHandles.size() > 0) {
		            					   result = new XMLNodeCursorImpl(seqNodeHandles, xctxt);

		            					   return result;
		            				   }
		            			   } 
	            			   }
	            			   else {
	            				   // REVISIT
	            				   // We need to validate an XML element instance node 
	            			   }
	            		   }
	            	   }
	            	}
	            	else if (sequenceTypeKindTest.getKindVal() == DOCUMENT_KIND) {
	            	   if (srcValue instanceof XMLNodeCursorImpl) {
	            		  XMLNodeCursorImpl nodeSet1 = (XMLNodeCursorImpl)srcValue;
	            		  DTMManager dtmMgr = nodeSet1.getDTMManager();
	            		  int nodeHandle = nodeSet1.nextNode();
	            		  DTM dtm = dtmMgr.getDTM(nodeHandle);
	            		  if (dtm.getNodeType(nodeHandle) == DTM.DOCUMENT_NODE) {
	            			 result = srcValue;
	            			 
	            			 return result;
	            		  }
	            	   }
	            	}	            	
            	}
            	else if (seqExpectedTypeData.getBuiltInSequenceType() == SequenceTypeSupport.XS_ANY_ATOMIC_TYPE) {
            		if (srcValue instanceof ResultSequence) {
            			ResultSequence rSeq = (ResultSequence)srcValue;
            			if ((rSeq.size() == 1) && ((itemTypeOccurenceIndicator == OccurrenceIndicator.ABSENT) || 
            					                   (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE) || 
            					                   (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) ||
            					                   (itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY))) {
            			   if (rSeq.item(0) instanceof XSAnyAtomicType) {
            			      result = rSeq.item(0);
            			      
            			      return result;
            			   }
            			}
            			else if ((rSeq.size() > 0) && ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) || 
            					                       (itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY))) {
            			    boolean isSeqAnyAtomicType = true;
            			    for (int idx = 0; idx < rSeq.size(); idx++) {
            			       if (!(rSeq.item(0) instanceof XSAnyAtomicType)) {
            			    	  isSeqAnyAtomicType = false;
            			    	  break;
            			       }
            			    }
            			    if (isSeqAnyAtomicType) {
            			       result = rSeq;
            			       
            			       return result;
            			    }
            			}
            			else if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE) || 
            					 (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY)) {
            				// an input sequence is empty
            				result = rSeq;
            				
         			        return result;
            			}
            		}
            		else if (srcValue instanceof XNodeSetForDOM) {
            			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)srcValue;
            			DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
            			DTMManager dtmManager = xctxt.getDTMManager();
            			int docNodeHandle = dtmCursorIter.nextNode();
            			DTM dtm = dtmManager.getDTM(docNodeHandle);
            			int child = dtm.getFirstChild(docNodeHandle);
            			if (dtm.getNodeType(child) == DTM.TEXT_NODE) {
            				String nodeStrValue = dtm.getNodeValue(child);
            				if (nodeStrValue.contains(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX)) {
            					nodeStrValue = (nodeStrValue.replace(ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX, " ")).trim();
            					if ((itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_MANY) || 
            							                                                 (itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY)) {             						            					
            						String[] strArray = nodeStrValue.split(" ");
            						ResultSequence rSeq = new ResultSequence();
            						for (int idx = 0; idx < strArray.length; idx++) {
            							rSeq.add(new XSString(strArray[idx]));
            						}

            						result = rSeq;
            					}
            					else {
            						result = new XSString(nodeStrValue);
            					}
            				}
            				else {
            					result = new XSString(nodeStrValue);
            				}
            				
            				return result;
            			}
            			else if (child == DTM.NULL) {
            				// Return an empty sequence 
            				result = new ResultSequence();
            				
            				return result;
            			}
            			else {
            			   // Error handling	
            			}
            		}
            	}            	
            }
            
            if (srcValue instanceof XSAnyURI) {
                String srcStrVal = ((XSAnyURI)srcValue).stringValue();
                
                if ((expectedType == XS_ANY_URI) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (expectedType == STRING) {
                   result = new XSString(srcStrVal);
                }
                else if (sequenceTypeKindTest != null) {
                      result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:anyURI", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XString) {
                String srcStrVal = ((XString)srcValue).str();
                
                if ((expectedType == STRING) || (expectedType == XS_NORMALIZED_STRING) || 
                		                        (expectedType == XS_TOKEN) || (expectedType == XS_ANY_ATOMIC_TYPE) || 
                		                        (expectedType == XS_UNTYPED_ATOMIC)) {
                   result = srcValue; 
                }
                else if (expectedType == XS_LANGUAGE) {
                   result = new XSLanguage(XslTransformEvaluationHelper.getStrVal(srcValue));
                }
                else if (expectedType == XS_NAME) {
                   result = new XSName(XslTransformEvaluationHelper.getStrVal(srcValue));
                }
                else if (expectedType == XS_NCNAME) {
                   result = new XSNCName(XslTransformEvaluationHelper.getStrVal(srcValue));
                }
                else if (expectedType == XS_NMTOKEN) {
                    result = new XSNmToken(XslTransformEvaluationHelper.getStrVal(srcValue));
                }
                else if (expectedType == XS_ID) {
                    result = new XSID(XslTransformEvaluationHelper.getStrVal(srcValue));
                }
                else if (expectedType == XS_IDREF) {
                    result = new XSIdRef(XslTransformEvaluationHelper.getStrVal(srcValue));
                }
                else if (sequenceTypeKindTest != null) {
                   if (sequenceTypeKindTest.getKindVal() == TEXT_KIND) {
                      result = srcValue; 
                   }
                   else {
                      result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:string", sequenceTypeXPathExprStr);
                   }
                }
                else {
                   result = castStringValueToAnExpectedType(srcStrVal, expectedType, sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSString) {           
               String srcStrVal = ((XSString)srcValue).stringValue();
               
               if ((expectedType == STRING) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  if (sequenceTypeKindTest.getKindVal() == TEXT_KIND) {
                     result = srcValue; 
                  }
                  else {
                     result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:string", sequenceTypeXPathExprStr);
                  }
               }
               else {
                  result = castStringValueToAnExpectedType(srcStrVal, expectedType, sequenceTypeXPathExprStr);
               }
            }            
            else if (srcValue instanceof XNumber) {
               XSDouble xsDouble = new XSDouble(((XNumber)srcValue).num());
               String srcStrVal = xsDouble.stringValue(); 
               
               if ((expectedType == XS_DOUBLE) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:double", sequenceTypeXPathExprStr);
               }
               else {
                  result = xpathNumericTypeConversionAndPromotion(xsDouble, expectedType, sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSNumericType) {
               XSNumericType xsNumericType = (XSNumericType)srcValue;
               
               try {
                   result = xpathNumericTypeConversionAndPromotion(xsNumericType, expectedType, sequenceTypeXPathExprStr);
               }
               catch (TransformerException ex1) {
                  if (sequenceTypeKindTest != null) {
                     String srcStrVal = xsNumericType.stringValue();
                     try {
                        result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                         xsNumericType.stringType(), sequenceTypeXPathExprStr);
                     }
                     catch (TransformerException ex2) {
                        throw ex2;
                     }
                  }
                  else {
                     throw ex1;   
                  }
               }
            }
            else if (srcValue instanceof XSBase64Binary) {
                String srcStrVal = ((XSBase64Binary)srcValue).stringValue();
                if ((expectedType == XS_BASE64BINARY) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:base64Binary", sequenceTypeXPathExprStr);
                }
            }
            else if ((srcValue instanceof XBoolean) || (srcValue instanceof XBooleanStatic)) {
               String srcStrVal = srcValue.str();
               if ((expectedType == BOOLEAN) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:boolean", sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSBoolean) {
               String srcStrVal = ((XSBoolean)srcValue).stringValue();
               if ((expectedType == BOOLEAN) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:boolean", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSDate) {
               String srcStrVal = ((XSDate)srcValue).stringValue();
               if ((expectedType == XS_DATE) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:date", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSDateTime) {
               String srcStrVal = ((XSDateTime)srcValue).stringValue();
               if ((expectedType == XS_DATETIME) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                    "xs:dateTime", sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSTime) {
               String srcStrVal = ((XSTime)srcValue).stringValue();
               if ((expectedType == XS_TIME) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:time", sequenceTypeXPathExprStr);
               }
            }
            else if (srcValue instanceof XSGYearMonth) {
            	String srcStrVal = ((XSGYearMonth)srcValue).stringValue();
            	if ((expectedType == XS_GYEAR_MONTH) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
            		result = srcValue; 
            	}
            	else if (sequenceTypeKindTest != null) {
            		result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
            																					   "xs:gYearMonth", sequenceTypeXPathExprStr);
            	}
            }
            else if (srcValue instanceof XSHexBinary) {
            	String srcStrVal = ((XSHexBinary)srcValue).stringValue();
                if ((expectedType == XS_HEXBINARY) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:hexBinary", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSLanguage) {
            	String srcStrVal = ((XSLanguage)srcValue).stringValue();
                if ((expectedType == XS_LANGUAGE) || (expectedType == STRING) || 
                		                             (expectedType == XS_NORMALIZED_STRING) || (expectedType == XS_TOKEN) || 
                		                             (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:language", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSName) {
            	String srcStrVal = ((XSName)srcValue).stringValue();
                if ((expectedType == XS_NAME) || (expectedType == STRING) || 
                		                         (expectedType == XS_NORMALIZED_STRING) || (expectedType == XS_TOKEN) || 
                		                         (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:Name", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSNCName) {
            	String srcStrVal = ((XSNCName)srcValue).stringValue();
                if ((expectedType == XS_NCNAME) || (expectedType == XS_NAME) || (expectedType == STRING) || 
                		                           (expectedType == XS_NORMALIZED_STRING) || (expectedType == XS_TOKEN) || 
                		                           (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:NCName", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSNmToken) {
            	String srcStrVal = ((XSNmToken)srcValue).stringValue();
                if ((expectedType == XS_NMTOKEN) || (expectedType == STRING) || 
                		                            (expectedType == XS_NORMALIZED_STRING) || (expectedType == XS_TOKEN) || 
                		                            (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:NMTOKEN", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSID) {
            	String srcStrVal = ((XSID)srcValue).stringValue();
                if ((expectedType == XS_ID) || (expectedType == XS_NCNAME) || (expectedType == XS_NAME) || 
                		                       (expectedType == STRING) || (expectedType == XS_NORMALIZED_STRING) || 
                		                       (expectedType == XS_TOKEN) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:ID", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSIdRef) {
            	String srcStrVal = ((XSIdRef)srcValue).stringValue();
                if ((expectedType == XS_IDREF) || (expectedType == XS_NCNAME) || (expectedType == XS_NAME) || 
                		                          (expectedType == STRING) || (expectedType == XS_NORMALIZED_STRING) || 
                		                          (expectedType == XS_TOKEN) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:ID", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSQName) {
            	String srcStrVal = ((XSQName)srcValue).stringValue();
                if ((expectedType == XS_QNAME) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                   result = srcValue; 
                }
                else if (sequenceTypeKindTest != null) {
                   result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                     "xs:QName", sequenceTypeXPathExprStr);
                }
            }
            else if (srcValue instanceof XSGYear) {
            	String srcStrVal = ((XSGYear)srcValue).stringValue();
            	if ((expectedType == XS_GYEAR) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
            		result = srcValue; 
            	}
            	else if (sequenceTypeKindTest != null) {
            		result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
            																					   "xs:gYear", sequenceTypeXPathExprStr);
            	}
            }
            else if (srcValue instanceof XSGMonthDay) {
            	String srcStrVal = ((XSGMonthDay)srcValue).stringValue();
            	if ((expectedType == XS_GMONTH_DAY) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
            		result = srcValue; 
            	}
            	else if (sequenceTypeKindTest != null) {
            		result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
            																					   "xs:gMonthDay", sequenceTypeXPathExprStr);
            	}
            }
            else if (srcValue instanceof XSGDay) {
            	String srcStrVal = ((XSGDay)srcValue).stringValue();
            	if ((expectedType == XS_GDAY) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
            		result = srcValue; 
            	}
            	else if (sequenceTypeKindTest != null) {
            		result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
            																					   "xs:gDay", sequenceTypeXPathExprStr);
            	}
            }
            else if (srcValue instanceof XSGMonth) {
            	String srcStrVal = ((XSGMonth)srcValue).stringValue();
            	if ((expectedType == XS_GMONTH) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
            		result = srcValue; 
            	}
            	else if (sequenceTypeKindTest != null) {
            		result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
            																					   "xs:gMonth", sequenceTypeXPathExprStr);
            	}
            }
            else if (srcValue instanceof XSDayTimeDuration) {
               String srcStrVal = ((XSDayTimeDuration)srcValue).stringValue();     
               if ((expectedType == XS_DAYTIME_DURATION) || (expectedType == XS_DURATION) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:dayTimeDuration", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSYearMonthDuration) {
               String srcStrVal = ((XSYearMonthDuration)srcValue).stringValue();      
               if ((expectedType == XS_YEARMONTH_DURATION) || (expectedType == XS_DURATION) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:yearMonthDuration", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSDuration) {
               String srcStrVal = ((XSDuration)srcValue).stringValue();
               if ((expectedType == XS_DURATION) || (expectedType == XS_ANY_ATOMIC_TYPE)) {
                  result = srcValue; 
               }
               else if (sequenceTypeKindTest != null) {
                  result = performXdmItemTypeNormalizationOnAtomicType(sequenceTypeKindTest, srcValue, srcStrVal, 
                                                                                                   "xs:duration", sequenceTypeXPathExprStr);
               } 
            }
            else if (srcValue instanceof XSUntyped) {
               String srcStrVal = ((XSUntyped)srcValue).stringValue();
               result = castXdmValueToAnotherType(new XSString(srcStrVal), sequenceTypeXPathExprStr, 
                                                                                                    expectedSeqTypeData, xctxt);
            }
            else if (srcValue instanceof XSUntypedAtomic) {
               String srcStrVal = ((XSUntypedAtomic)srcValue).stringValue();
               result = castXdmValueToAnotherType(new XSString(srcStrVal), sequenceTypeXPathExprStr, 
                                                                                                    expectedSeqTypeData, xctxt);
            }
            else if (srcValue instanceof XNodeSetForDOM) {               
               result = castXNodeSetForDOMInstance(srcValue, sequenceTypeXPathExprStr, expectedSeqTypeData, 
                                                                              xctxt, srcLocator, itemTypeOccurenceIndicator, 
                                                                                                                       sequenceTypeKindTest);
            }
            else if (srcValue instanceof XMLNodeCursorImpl) {
               result = castXNodeSetInstance(srcValue, sequenceTypeXPathExprStr, expectedSeqTypeData, 
                                                                              xctxt, srcLocator, itemTypeOccurenceIndicator, 
                                                                                                                       sequenceTypeKindTest);
            }
            else if (srcValue instanceof ResultSequence) {
               ResultSequence srcResultSeq = (ResultSequence)srcValue;
               if (srcResultSeq.size() == 1) {
                   result = castXdmValueToAnotherType(srcResultSeq.item(0), sequenceTypeXPathExprStr, expectedSeqTypeData, xctxt);   
               }
               else if ((srcResultSeq.size() == 0) && (expectedType == EMPTY_SEQUENCE)) {
            	   result = srcResultSeq; 
               }
               else if ((srcResultSeq.size() == 0) && (itemTypeOccurenceIndicator == 0)) {
            	   return null;
               }
               else if ((srcResultSeq.size() > 1) && (itemTypeOccurenceIndicator == 0)) {
            	   return null;
               }
               else {
                   result = castResultSequenceInstance((ResultSequence)srcValue, sequenceTypeXPathExprStr, expectedSeqTypeData, xctxt, srcLocator, 
                                                        expectedType, itemTypeOccurenceIndicator);
               }
            }
            else if (srcValue instanceof XPathInlineFunction) {
               SequenceTypeFunctionTest sequenceTypeFunctionTest = seqExpectedTypeData.getSequenceTypeFunctionTest();
               if (sequenceTypeFunctionTest != null) {
            	  XPathInlineFunction inlineFunctionExpr = (XPathInlineFunction)srcValue;
            	  if (sequenceTypeFunctionTest.isAnyFunctionTest()) {
            		 result = srcValue; 
            	  }
            	  else {
            		 List<InlineFunctionParameter> inlineFuncParameterList = inlineFunctionExpr.getFuncParamList();            		 
            		 
            		 List<String> functionExpectedParamTypes = sequenceTypeFunctionTest.getTypedFunctionTestParamSpecList();
            		 if (functionExpectedParamTypes.size() == inlineFuncParameterList.size()) {
            		    for (int idx = 0; idx < functionExpectedParamTypes.size(); idx++) {
            		       String expectedParamTypeStr = functionExpectedParamTypes.get(idx);
            		       if (m_PrefixTable != null) {
            		    	  expectedParamTypeStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
            		    			                                                                 expectedParamTypeStr, m_PrefixTable);  
            		       }
            		       
            		       XPath expectedParamTypeXPath = new XPath(expectedParamTypeStr, srcLocator, 
            		    		                                                    xctxt.getNamespaceContext(), XPath.SELECT, 
            		    		                                                    null, true);
            		       XObject evalResult = expectedParamTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
            		       SequenceTypeData sequenceTypeData1 = (SequenceTypeData)evalResult;
            		       SequenceTypeData sequenceTypeData2 = (inlineFuncParameterList.get(idx)).getParamType();
            		       if (!sequenceTypeData1.equal(sequenceTypeData2)) {
            		    	  throw new TransformerException("XPTY0004 : Sequence type information for function test doesn't match.");  
            		       }
            		    }
            		 }
            		 else {
            			throw new TransformerException(INLINE_FUNCTION_PARAM_TYPECHECK_COUNT_ERROR); 
            		 }
            		 
            		 SequenceTypeData funcReturnType = inlineFunctionExpr.getReturnType();
            		 String functionReturnTypeStr = sequenceTypeFunctionTest.getTypedFunctionTestReturnType();
            		 
            		 if (m_PrefixTable != null) {
            			functionReturnTypeStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
       		    	    		                                                                     functionReturnTypeStr, m_PrefixTable);  
       		         }
            		 
            		 XPath functionReturnTypeXPath = new XPath(functionReturnTypeStr, srcLocator, 
                                                                                xctxt.getNamespaceContext(), XPath.SELECT, 
                                                                                null, true);
                     XObject evalResult = functionReturnTypeXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                     if (!funcReturnType.equal((SequenceTypeData)evalResult)) {
       		    	    throw new TransformerException("XPTY0004 : Sequence type information for function test doesn't match.");  
       		         }
                     
            		 result = srcValue;
            	  }
               }
            }
            else if (srcValue instanceof XPathMap) {
            	SequenceTypeMapTest sequenceTypeMapTest = seqExpectedTypeData.getSequenceTypeMapTest();
                if (sequenceTypeMapTest != null) {
             	   if (sequenceTypeMapTest.isAnyMapTest()) {
             		  result = srcValue; 
             	   }
             	   else {
             		  XPathMap xpathMap = (XPathMap)srcValue;
             		  Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
             		  Set<XObject> mapKeys = nativeMap.keySet();
             		  Iterator<XObject> keysIter = mapKeys.iterator();
             		  // The following code, checks each of map's entry with expected 
             		  // type of map entry's key and value. 
             		  while (keysIter.hasNext()) {
             			 XObject mapKey = keysIter.next();
             			 XObject mapEntry = nativeMap.get(mapKey);
             			 String keyStrVal = XslTransformEvaluationHelper.getStrVal(mapKey);
             			 if (mapKey instanceof ResultSequence) {
             				mapKey = ((ResultSequence)mapKey).item(0);
             			 }
             			 if (mapEntry instanceof ResultSequence) {
             				mapEntry = ((ResultSequence)mapEntry).item(0);
             			 }
             			 SequenceTypeData keySeqTypeData = sequenceTypeMapTest.getKeySequenceTypeData();
             			 SequenceTypeData valueSeqTypeData = sequenceTypeMapTest.getValueSequenceTypeData();
             			 XObject mapKeyValTypeCheckResult = SequenceTypeSupport.castXdmValueToAnotherType(mapKey, null, keySeqTypeData, xctxt);
             			 String mapSequenceTypeStr = (sequenceTypeXPathExprStr != null) ? sequenceTypeXPathExprStr : "";
             			 if (mapKeyValTypeCheckResult == null) {             				
             				throw new TransformerException("XPTY0004 : An XPath map entry with key '" + keyStrVal + "', doesn't have a value of specified XPath sequence type."); 
             			 }
             			 XObject mapEntryValTypeCheckResult = SequenceTypeSupport.castXdmValueToAnotherType(mapEntry, null, valueSeqTypeData, xctxt);
             			 if (mapEntryValTypeCheckResult == null) {
             				throw new TransformerException("XPTY0004 : An XPath map entry with key '" + keyStrVal + "', doesn't have a value of specified XPath sequence type.");  
             			 }           			 
             		  }
             		  
             		  result = srcValue; 
             	   }
                }
            }
            else if (srcValue instanceof XPathArray) {
            	SequenceTypeArrayTest sequenceTypeArrayTest = seqExpectedTypeData.getSequenceTypeArrayTest();
                if (sequenceTypeArrayTest != null) {
             	   if (sequenceTypeArrayTest.isAnyArrayTest()) {
             		  result = srcValue; 
             	   }
             	   else {
             		  XPathArray xpathArr = (XPathArray)srcValue;
             		  List<XObject> nativeArr = xpathArr.getNativeArray();
             		  Iterator<XObject> arrIter = nativeArr.iterator();
             		  // We check below each of array items, with an expected type 
             		  while (arrIter.hasNext()) {
             			 XObject arrItem = arrIter.next();
             			 if (arrItem instanceof ResultSequence) {
             				arrItem = ((ResultSequence)arrItem).item(0);
             			 }
             			 SequenceTypeData arrayItemTypeInfo = sequenceTypeArrayTest.getArrayItemTypeInfo();
             			 XObject arrayItemTypeCheckResult = SequenceTypeSupport.castXdmValueToAnotherType(arrItem, null, arrayItemTypeInfo, xctxt);
             			 String arrayItemSequenceTypeStr = (sequenceTypeXPathExprStr != null) ? sequenceTypeXPathExprStr : "";
             			 if (arrayItemTypeCheckResult == null) {             				
             				throw new TransformerException("XPTY0004 : One or more, of XPath array's item doesn't conform to array item's expected "
             						                                                                                       + "type '" + arrayItemSequenceTypeStr + "'."); 
             			 }           			 
             		  }
             		  
             		  result = srcValue;  
             	   }
                }
            }
        }
        catch (TransformerException ex) {
           if (ex.getLocator() != null) {
              throw ex;  
           }
           else {
              throw new TransformerException(ex.getMessage(), srcLocator);  
           }
        }
        catch (Exception ex) {
            String srcStrVal = XslTransformEvaluationHelper.getStrVal(srcValue); 
            throw new TransformerException("XTTE0570 : The source value '" + srcStrVal + "' cannot be cast "
                                                                                    + "to a type " + sequenceTypeXPathExprStr + ".", srcLocator); 
        }
        
        return result;
    }

	/**
     * Check whether, two XML namespace values are equal.
     */
    public static boolean isTwoXmlNamespaceValuesEqual(String ns0, String ns1) {
        boolean xmlNamespacesEqual = false;
        
        if ((ns0 == null) && (ns1 == null)) {
           xmlNamespacesEqual = true; 
        }
        else if ((ns0 != null) && (ns1 != null)) {
           xmlNamespacesEqual = ns0.equals(ns1);  
        }
        
        return xmlNamespacesEqual; 
    }

    /**
     * Validate an xdm element node with an XML Schema type definition.
     */
	public static boolean isXdmElemNodeValidWithSchemaType(XMLNodeCursorImpl xdmNode, XPathContext xctxt,
													      XSTypeDefinition xsTypeDefinition)
														       throws Exception, ParserConfigurationException, SAXException, 
															   IOException, TransformerException {
		
		boolean isNodeValidWithSchemaType = false;

		// Validate the node with an XML Schema type
		xdmNode = (XMLNodeCursorImpl)(xdmNode.getFresh());
		DTMCursorIterator dtmIter = ((XMLNodeCursorImpl)xdmNode).iterRaw();
		int nodeHandle = dtmIter.nextNode();
		DTM dtm = xctxt.getDTM(nodeHandle);
		Node node = dtm.getNode(nodeHandle);

		String xmlDocumentStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);

		isNodeValidWithSchemaType = isXmlStrValid(xmlDocumentStr, null, xsTypeDefinition);

		return isNodeValidWithSchemaType;
	}

	/**
	 * Validate an XML document provided as string value, with the specified element 
	 * declaration or type definition. 
	 */
	public static boolean isXmlStrValid(String xmlDocumentStr, XSElementDecl elemDecl, XSTypeDefinition xsTypeDefinition)
			                                                         throws ParserConfigurationException, SAXException, IOException {
		boolean isNodeValidWithSchemaType = false;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbf.newDocumentBuilder();
		Document document = dBuilder.parse(new ByteArrayInputStream(xmlDocumentStr.getBytes()));
					
		String xsTargetNamespace = null;
		if (xsTypeDefinition != null) {
		   xsTargetNamespace = xsTypeDefinition.getNamespace();
		}
		else {
		   xsTargetNamespace = elemDecl.getNamespace();
		}

		SchemaGrammar grammar = new SchemaGrammar(xsTargetNamespace, new XSDDescription(), null);	            			            		

		XSElementDecl xsActiveElemDecl = null;
		if (elemDecl != null) {
			xsActiveElemDecl = elemDecl; 
		}
		else {
			String elemName = (document.getDocumentElement()).getNodeName();			
			xsActiveElemDecl = new XSElementDecl();
			xsActiveElemDecl.fName = elemName;
			xsActiveElemDecl.fTargetNamespace = xsTargetNamespace;
			xsActiveElemDecl.fType = xsTypeDefinition;
			xsActiveElemDecl.setIsGlobal();
		}

		grammar.addGlobalElementDecl(xsActiveElemDecl);	            		
		XMLGrammarPoolImpl grammarPool = new XMLGrammarPoolImpl();
		grammarPool.putGrammar(grammar);

		XMLSchemaFactory xmlSchemaFactory = new XMLSchemaFactory();
		Schema schema = xmlSchemaFactory.newSchema(grammarPool);
		Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
		
		isNodeValidWithSchemaType = true;
		
		return isNodeValidWithSchemaType;
	}
	
	/**
	 * Given an XObject object instance check whether it represents
	 * an xdm node. A non-null object reference returned by this
	 * method, indicates that an input object reference is a node.
	 */
	public static XMLNodeCursorImpl getNodeReference(XObject item) {
		
		XMLNodeCursorImpl nodeSet = null;
		
		if (item instanceof XMLNodeCursorImpl) {
		   nodeSet = (XMLNodeCursorImpl)item;			
		}
		else if (item instanceof ResultSequence) {
			ResultSequence rSeq = (ResultSequence)item;
			if ((rSeq.size() == 1) && (rSeq.item(0) instanceof XMLNodeCursorImpl)) {
			   nodeSet = (XMLNodeCursorImpl)(rSeq.item(0));
			}
		}
		
		return nodeSet;
	}
	
	/**
	 * Method definition, to produce SequenceTypeData object from, the supplied
	 * XPath sequence type string value.
	 * 
	 * @param seqTypeStr						An XPath sequence type string value
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @return                                  SequenceTypeData object
	 * @throws TransformerException
	 */
    public static SequenceTypeData getSequenceTypeDataFromSeqTypeStr(String seqTypeStr, XPathContext xctxt, 
                                                                     SourceLocator srcLocator) throws TransformerException {
    	SequenceTypeData seqTypeData = null;

    	XPath seqTypeXPath = new XPath(seqTypeStr, srcLocator, xctxt.getNamespaceContext(), 
    			                                                                       XPath.SELECT, null, true);
    	XObject seqTypeExpressionEvalResult = seqTypeXPath.execute(xctxt, xctxt.getContextNode(), xctxt.getNamespaceContext());
    	seqTypeData = (SequenceTypeData)seqTypeExpressionEvalResult;

    	return seqTypeData;
    }
    
	/**
	 * Method definition, to cast a string value to an expected XDM type.
	 * 
	 * @param srcStrVal									  Source string value
	 * @param expectedType								  An expected type's integer code	
	 * @param sequenceTypeXPathExprStr					  An expected type's XPath sequence type string value 
	 *                                                    as provided within an XSL stylesheet.
	 * @return											  An XDM value produced after the type cast operation	
	 * @throws TransformerException
	 */
    private static XObject castStringValueToAnExpectedType(String srcStrVal, int expectedType, 
    		                                               String sequenceTypeXPathExprStr) throws TransformerException {
        
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
            else if (expectedType == XS_DECIMAL) {                            
               boolean isStrValueNumeric = isStrValueNumericFormat(srcStrVal);               
               if (isStrValueNumeric) {
            	   result = new XSDecimal(srcStrVal); 
               }
            }
            else if (expectedType == XS_INTEGER) {
               result = new XSInteger(srcStrVal); 
            }
            else if (expectedType == XS_NON_POSITIVE_INTEGER) {
               result = new XSNonPositiveInteger(srcStrVal); 
            }
            else if (expectedType == XS_NEGATIVE_INTEGER) {
               result = new XSNegativeInteger(srcStrVal); 
            }
            else if (expectedType == XS_NON_NEGATIVE_INTEGER) {
               result = new XSNonNegativeInteger(srcStrVal); 
            }
            else if (expectedType == XS_POSITIVE_INTEGER) {
               result = new XSPositiveInteger(srcStrVal); 
            }
            else if (expectedType == XS_LONG) {
               result = new XSLong(srcStrVal); 
            }
            else if (expectedType == XS_INT) {
               result = new XSInt(srcStrVal);
            }
            else if (expectedType == XS_SHORT) {
               result = new XSShort(srcStrVal);
            }
            else if (expectedType == XS_BYTE) {
               result = new XSByte(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_LONG) {
               result = new XSUnsignedLong(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_INT) {
               result = new XSUnsignedInt(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_SHORT) {
               result = new XSUnsignedShort(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_BYTE) {
               result = new XSUnsignedByte(srcStrVal);
            }            
            else if (expectedType == XS_FLOAT) {
            	boolean isStrValueNumeric;
            	if (!("INF".equals(srcStrVal) || "-INF".equals(srcStrVal))) {
            	   isStrValueNumeric = isStrValueNumericFormat(srcStrVal);
            	}
            	else {
            	   isStrValueNumeric = true;
            	}
            	
                if (isStrValueNumeric) {
             	   result = new XSFloat(srcStrVal); 
                }              
            }
            else if (expectedType == XS_DOUBLE) {               
            	boolean isStrValueNumeric;
            	if (!("INF".equals(srcStrVal) || "-INF".equals(srcStrVal))) {
            	   isStrValueNumeric = isStrValueNumericFormat(srcStrVal);
            	}
            	else {
            	   isStrValueNumeric = true;
            	}
            	
                if (isStrValueNumeric) {
             	   result = new XSDouble(srcStrVal); 
                }
            }
            else if (expectedType == XS_DATE) {
               result = XSDate.parseDate(srcStrVal); 
            }
            else if (expectedType == XS_DATETIME) {
               result = XSDateTime.parseDateTime(srcStrVal); 
            }
            else if (expectedType == XS_TIME) {
               result = XSTime.parseTime(srcStrVal); 
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
            else if (expectedType == XS_GYEAR_MONTH) {
               result = new XSGYearMonth(srcStrVal); 
            }
            else if (expectedType == XS_GYEAR) {
               result = new XSGYear(srcStrVal); 
            }
            else if (expectedType == XS_GMONTH_DAY) {
               result = new XSGMonthDay(srcStrVal); 
            }
            else if (expectedType == XS_GDAY) {
               result = new XSGDay(srcStrVal); 
            }
            else if (expectedType == XS_GMONTH) {
               result = new XSGMonth(srcStrVal); 
            }
            else if (expectedType == XS_BASE64BINARY) {
               result = new XSBase64Binary(srcStrVal);
            }
            else if (expectedType == XS_HEXBINARY) {
               result = new XSHexBinary(srcStrVal);
            }
            else if (expectedType == XS_ANY_URI) {
               result = new XSAnyURI(srcStrVal);
            }
            else {
               String effectiveTypeDefnStr = (sequenceTypeXPathExprStr != null) ? sequenceTypeXPathExprStr : getDataTypeNameFromIntValue(expectedType);  	               
               throw new TransformerException("XTTE0570 : The supplied value cannot be cast to type " + effectiveTypeDefnStr + "."); 
            }
        }
        catch (TransformerException ex) {
            throw ex;    
        }
        catch (Exception ex) {
        	String effectiveTypeDefnStr = (sequenceTypeXPathExprStr != null) ? sequenceTypeXPathExprStr : getDataTypeNameFromIntValue(expectedType);        	
        	throw new TransformerException("XTTE0570 : The supplied value cannot be cast to type " + effectiveTypeDefnStr + ".");
        }
        
        return result;
    }

    /**
     * Method definition to check whether, the supplied string value
     * is formatted as number.
     * 
     * @param strValue					The supplied string value
     * @return
     */
	private static boolean isStrValueNumericFormat(String strValue) {		
		boolean isStrValueNumeric = true;
		
        try {
           BigDecimal bigDecimal = new BigDecimal(strValue);
        }
        catch (NumberFormatException ex) {
        	isStrValueNumeric = false; 
        }
        
        return isStrValueNumeric;
	}
    
    /**
     * This method does numeric type conversion, and numeric type promotion as 
     * defined by XPath 3.1 spec.
     */
    private static XObject xpathNumericTypeConversionAndPromotion(XSNumericType srcXsNumericType, 
                                                                  int expectedType, String sequenceTypeXPathExprStr) throws TransformerException {
        XObject result = null;
        
        String srcStrVal = srcXsNumericType.stringValue();
        
        String dataTypeName = getDataTypeNameFromIntValue(expectedType);
        
        try {
        	if (expectedType == XS_ANY_ATOMIC_TYPE) {
                result = srcXsNumericType;
            }
        	else if (srcXsNumericType instanceof XSFloat) {           
               if (expectedType == XS_FLOAT) {
                  // The source and expected data types are same. Return the original value unchanged.
                  result = srcXsNumericType; 
               }
               else if (expectedType == XS_DOUBLE) {
                  result = new XSDouble(srcStrVal);
               }
            }
            else if (expectedType == XS_INT) {
                result = new XSInt(srcStrVal);
            }
            else if (expectedType == XS_SHORT) {
                result = new XSShort(srcStrVal);
            }
            else if (expectedType == XS_BYTE) {
                result = new XSByte(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_LONG) {
                result = new XSUnsignedLong(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_INT) {
                result = new XSUnsignedInt(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_SHORT) {
                result = new XSUnsignedShort(srcStrVal);
            }
            else if (expectedType == XS_UNSIGNED_BYTE) {
                result = new XSUnsignedByte(srcStrVal);
            }
            else if (expectedType == XS_LONG) {
                result = new XSLong(srcStrVal); 
            }
            else if (expectedType == XS_INTEGER) {
                result = new XSInteger(srcStrVal); 
            }
            else if (expectedType == XS_NON_POSITIVE_INTEGER) {
                result = new XSNonPositiveInteger(srcStrVal); 
            }
            else if (expectedType == XS_NEGATIVE_INTEGER) {
                result = new XSNegativeInteger(srcStrVal); 
            }
            else if (expectedType == XS_NON_NEGATIVE_INTEGER) {
                result = new XSNonNegativeInteger(srcStrVal); 
            }
            else if (expectedType == XS_POSITIVE_INTEGER) {
                result = new XSPositiveInteger(srcStrVal); 
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
            else if (expectedType == XS_DOUBLE) {
               result = new XSDouble(srcStrVal); 
            }
            else if (expectedType == XS_FLOAT) {
               result = new XSFloat(srcStrVal); 
            }
            else if ((srcXsNumericType.stringType()).equals(dataTypeName)) {
               // The source and expected data types are same. Return the original value unchanged.
               result = srcXsNumericType;
            }
            else {
               if (dataTypeName == null) {
            	   dataTypeName = sequenceTypeXPathExprStr;   
               }               
               throw new TransformerException("XTTE0570 : The numeric value " + srcStrVal + " cannot be cast or promoted "
                                                                                                 + "to a type " + dataTypeName + ".");  
            }
        }
        catch (TransformerException ex) {
            throw ex;  
        }
        catch (Exception ex) {
        	if (dataTypeName == null) {
         	   dataTypeName = sequenceTypeXPathExprStr;   
            }        	
            throw new TransformerException("XTTE0570 : The numeric value " + srcStrVal + " cannot be cast or promoted "
                                                                                               + "to a type " + dataTypeName + ".");
        }
        
        return result;
    }
    
    /**
     * Given a primitive int value for an xdm data type, return the corresponding
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
       else if (sequenceType == XS_NON_POSITIVE_INTEGER) {
          dataTypeName = "xs:nonPositiveInteger";
       }
       else if (sequenceType == XS_NEGATIVE_INTEGER) {
          dataTypeName = "xs:negativeInteger";
       }
       else if (sequenceType == XS_NON_NEGATIVE_INTEGER) {
          dataTypeName = "xs:nonNegativeInteger";
       }
       else if (sequenceType == XS_POSITIVE_INTEGER) {
          dataTypeName = "xs:positiveInteger";
       }
       else if (sequenceType == XS_LONG) {
          dataTypeName = "xs:long"; 
       }
       else if (sequenceType == XS_INT) {
          dataTypeName = "xs:int"; 
       }
       else if (sequenceType == XS_SHORT) {
          dataTypeName = "xs:short"; 
       }
       else if (sequenceType == XS_BYTE) {
          dataTypeName = "xs:byte"; 
       }
       else if (sequenceType == XS_UNSIGNED_LONG) {
          dataTypeName = "xs:unsignedLong"; 
       }
       else if (sequenceType == XS_UNSIGNED_INT) {
          dataTypeName = "xs:unsignedInt"; 
       }
       else if (sequenceType == XS_UNSIGNED_SHORT) {
          dataTypeName = "xs:unsignedShort"; 
       }
       else if (sequenceType == XS_UNSIGNED_BYTE) {
          dataTypeName = "xs:unsignedByte"; 
       }
       else if (sequenceType == XS_DOUBLE) {
          dataTypeName = "xs:double"; 
       }
       else if (sequenceType == XS_FLOAT) {
          dataTypeName = "xs:float"; 
       }
       
       return dataTypeName;       
    }
    
    /**
     * Given an XObject object instance representing an atomic data value, check whether
     * a sequence type item() type annotation is applicable to the atomic data value.
     */
    private static XObject performXdmItemTypeNormalizationOnAtomicType(SequenceTypeKindTest sequenceTypeKindTest, 
                                                                                     XObject srcValue, String srcStrVal, 
                                                                                     String srcDataTypeName, 
                                                                                     String sequenceTypeXPathExprStr) throws TransformerException {
        XObject result = null;
        
        if (sequenceTypeKindTest.getKindVal() == ITEM_KIND) {
           result = srcValue;
        }
        else {
           throw new TransformerException("XTTE0570 : The " + srcDataTypeName + " value '" + srcStrVal + "' cannot be cast to "
                                                                                                   + "a type " + sequenceTypeXPathExprStr + "."); 
        }
        
        return result;
    }
    
    /**
     * This method provides support, for following XSL transformation actions,
     * 
     * 1) An xsl:variable element has an "as" attribute (specifying the expected type of variable's value),
     *    not having a "select" attribute, and having a contained sequence constructor (which when
     *    evaluated, construct's the variable's value). An xsl:variable's evaluated value passed as an
     *    argument to this method, is checked against the variable's expected type.
     *      
     * 2) An xsl:template element has an "as" attribute (specifying the expected type of template's
     *    evaluated content). The template's evaluated content passed as an argument to this method, 
     *    is checked against the expected type.  
     */
    private static XObject castXNodeSetForDOMInstance(XObject srcValue,
                                                                  String sequenceTypeXPathExprStr,
                                                                  SequenceTypeData seqExpectedTypeDataInp, XPathContext xctxt,
                                                                  SourceLocator srcLocator, int itemTypeOccurenceIndicator,
                                                                  SequenceTypeKindTest sequenceTypeKindTest)
                                                                                                          throws TransformerException {
        XObject result = null;

        XNodeSetForDOM xNodeSetForDOM = (XNodeSetForDOM)srcValue;
        DTMNodeList dtmNodeList = (DTMNodeList)(xNodeSetForDOM.object());

        DTMManager dtmMgr = xNodeSetForDOM.getDTMManager();

        List<Integer> xdmNodesDtmList = new ArrayList<Integer>();

        Node localRootNode = dtmNodeList.item(0);
        NodeList nodeList = localRootNode.getChildNodes();
        int nodeSetLen = nodeList.getLength();

        ResultSequence convertedResultSeq = new ResultSequence();
        
        if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == 0) || (itemTypeOccurenceIndicator == 
                                                                                             OccurrenceIndicator.ZERO_OR_ONE))) {
            throw new TransformerException("XTTE0570 : A sequence of size " + nodeSetLen + ", cannot be cast to a type " 
                                                                                                         + sequenceTypeXPathExprStr + ".", srcLocator);  
        }
        else {            
            for (int idx = 0; idx < nodeSetLen; idx++) {
                Node node = nodeList.item(idx);

                int nodeDtmHandle = dtmMgr.getDTMHandleFromNode(node);
                xdmNodesDtmList.add(Integer.valueOf(nodeDtmHandle));

                String sequenceTypeNewXPathExprStr = null;                    
                if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                               sequenceTypeXPathExprStr.endsWith(PLUS)) {
                    sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                }

                if (sequenceTypeKindTest != null) {
                    String nodeName = node.getLocalName();
                    String nodeNsUri = node.getNamespaceURI();

                    if (sequenceTypeKindTest.getDataTypeLocalName() != null) {
                        String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr; 
                        throw new TransformerException("XTTE0570 : The required item type of an xdm node is " + dataTypeStr + ". "
                        		                                                  + "The supplied value " + nodeName + " does'nt match an expected type. "
                        		                                                  + "The supplied node has not been validated with a schema.", srcLocator); 
                    }
                    else {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            String elemNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                            if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
                                                                                                   STAR.equals(elemNodeKindTestNodeName)) {
                                elemNodeKindTestNodeName = nodeName;  
                            }

                            boolean isSeqTypeMatchOk = false;

                            if ((sequenceTypeKindTest.getKindVal() == ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
                                                                                             && (isTwoXmlNamespaceValuesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                                isSeqTypeMatchOk = true;
                            }
                            else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                                isSeqTypeMatchOk = true;
                            }

                            if (!isSeqTypeMatchOk) {
                                String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                                throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                                		                                             + "The supplied value " + nodeName + " does'nt match an expected "
                                		                                             + "type.", srcLocator);
                                
                            }
                        }
                        else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                            String attrNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                            if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
                                                                                                   STAR.equals(attrNodeKindTestNodeName)) {
                                attrNodeKindTestNodeName = nodeName;  
                            }

                            boolean isSeqTypeMatchOk = false;

                            if ((sequenceTypeKindTest.getKindVal() == ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
                                                                                               && (isTwoXmlNamespaceValuesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                                isSeqTypeMatchOk = true;  
                            }
                            else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                                isSeqTypeMatchOk = true;  
                            }

                            if (!isSeqTypeMatchOk) {
                                String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;                                
                                throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                                                                                           + "The supplied value " + nodeName + " does'nt match an expected "
                                                                                           + "type.", srcLocator);
                            } 
                        }
                        else if (node.getNodeType() == Node.TEXT_NODE) {
                            if (!((sequenceTypeKindTest.getKindVal() == TEXT_KIND) || 
                                                                (sequenceTypeKindTest.getKindVal() == NODE_KIND) || 
                                                                (sequenceTypeKindTest.getKindVal() == ITEM_KIND))) {
                                String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                                throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                                                                                           + "The supplied value does'nt match an expected type.", srcLocator);  
                            }
                        }
                    }
                }
                else {
                    if (sequenceTypeNewXPathExprStr == null) {
                        sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr;  
                    }

                    String nodeStrVal = node.getTextContent();
                    XObject xObject = castXdmValueToAnotherType(new XSString(nodeStrVal), sequenceTypeNewXPathExprStr, 
                                                                                                                      seqExpectedTypeDataInp, xctxt);                       
                    convertedResultSeq.add(xObject); 
                }
            }
        }

        if (convertedResultSeq.size() > 0) {
            result = convertedResultSeq;  
        }
        else if (xdmNodesDtmList.size() > 0) {
            result = new XMLNodeCursorImpl(xdmNodesDtmList, dtmMgr); 
        }
        
        return result;
    }
    
    /**
     * This method casts a provided XObject object instance representing an xdm
     * node set, to another type specified by an xdm sequence type expression.
     */
    private static XObject castXNodeSetInstance(XObject srcValue,
                                                              String sequenceTypeXPathExprStr,
                                                              SequenceTypeData seqExpectedTypeDataInp, XPathContext xctxt,
                                                              SourceLocator srcLocator,
                                                              int itemTypeOccurenceIndicator,
                                                              SequenceTypeKindTest sequenceTypeKindTest)
                                                                                                     throws TransformerException {
        XObject result = null;
        
        XMLNodeCursorImpl xdmNodeSet = (XMLNodeCursorImpl)srcValue;
        
        int nodeSetLen = xdmNodeSet.getLength();
                
        if ((nodeSetLen == 0) && 
            ((sequenceTypeKindTest != null) && isSequenceTypeExprNodeKind(sequenceTypeKindTest.getKindVal())) && 
            (itemTypeOccurenceIndicator == 0)) {
        	// Here, an input value is an empty sequence, and the sequence type expression refers 
        	// to one the various node kinds and no occurrence indicator character has been 
        	// specified (few examples of such sequence type expressions are, element(), attribute(), 
        	// attribute(name), node() etc). Therefore, an input value (i.e, an empty sequence) hasn't 
        	// matched the sequence type specified.
        	return result;
        }
        
        if ((nodeSetLen > 1) && ((itemTypeOccurenceIndicator == OccurrenceIndicator.ABSENT) || (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE))) {
            throw new TransformerException("XTTE0570 : A sequence of size " + nodeSetLen + ", cannot be cast to a type " 
                                                                                                           + sequenceTypeXPathExprStr + ".", srcLocator);  
        }
        else { 
            ResultSequence convertedResultSeq = new ResultSequence();

            DTMCursorIterator dtmIter = (DTMCursorIterator)xdmNodeSet;
            
            int nextNodeDtmHandle;
                   
            while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {               
               XMLNodeCursorImpl nodeSetItem = new XMLNodeCursorImpl(nextNodeDtmHandle, dtmIter.getDTMManager());               
               
               String sequenceTypeNewXPathExprStr = null;
               if (sequenceTypeXPathExprStr != null) {
                   if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                                sequenceTypeXPathExprStr.endsWith(PLUS)) {
                      sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                   }
               }
               
               if (sequenceTypeKindTest != null) {
                  DTM dtm = null;
                  DTMManager dtmMgr = (DTMManager)xctxt; 
                  if (dtmMgr != null) {
                     dtm = dtmMgr.getDTM(nextNodeDtmHandle);
                  }
                  else {
                     dtm = dtmIter.getDTM(nextNodeDtmHandle);
                  }
                  
                  String nodeName = dtm.getNodeName(nextNodeDtmHandle);
                  String nodeNsUri = dtm.getNamespaceURI(nextNodeDtmHandle);
                  
                  if (sequenceTypeKindTest.getDataTypeLocalName() != null) {
                     String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                     throw new TransformerException("XTTE0570 : The required item type of an xdm node is " + dataTypeStr + ". "
                                                                          + "The supplied value " + nodeName + " does'nt match an expected type. "
                                                                          + "The supplied node has not been validated with a schema.", srcLocator);
                  }
                  else {
                     if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ELEMENT_NODE) {
                        String elemNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                        String nodeNsUri1 = nodeNsUri; 
                        if (elemNodeKindTestNodeName == null || "".equals(elemNodeKindTestNodeName) || 
                                                                                                STAR.equals(elemNodeKindTestNodeName)) {
                           elemNodeKindTestNodeName = nodeName;
                           nodeNsUri = null;
                        }
                        
                        if ((sequenceTypeKindTest.getKindVal() == ELEMENT_KIND) && (nodeName.equals(elemNodeKindTestNodeName)) 
                                                                                                 && (isTwoXmlNamespaceValuesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                           convertedResultSeq.add(nodeSetItem);  
                        }
                        else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                           convertedResultSeq.add(nodeSetItem); 
                        }
                        else {
                           String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                           throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                           		                                                + "The supplied value " + nodeName + " does'nt match an expected "
                           		                                                + "type.", srcLocator); 
                        }
                        
                        nodeNsUri = nodeNsUri1; 
                        
                        if (nodeSetLen == 1) {
                           result = nodeSetItem;
                           
                           return result;
                        }
                     }
                     else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.ATTRIBUTE_NODE) {
                        String attrNodeKindTestNodeName = sequenceTypeKindTest.getNodeLocalName();
                        if (attrNodeKindTestNodeName == null || "".equals(attrNodeKindTestNodeName) || 
                                                                                                STAR.equals(attrNodeKindTestNodeName)) {
                           attrNodeKindTestNodeName = nodeName;  
                        }
                        
                        if ((sequenceTypeKindTest.getKindVal() == ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
                                                                                                   && (isTwoXmlNamespaceValuesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                            convertedResultSeq.add(nodeSetItem);  
                        }
                        else if ((sequenceTypeKindTest.getKindVal() == SCHEMA_ATTRIBUTE_KIND) && (nodeName.equals(attrNodeKindTestNodeName)) 
                                																   && (isTwoXmlNamespaceValuesEqual(nodeNsUri, sequenceTypeKindTest.getNodeNsUri()))) {
                        	StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
                        	XSModel xsModel = stylesheetRoot.getXsModel();
                        	if (xsModel != null) {
                        		XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrNodeKindTestNodeName, sequenceTypeKindTest.getNodeNsUri());
                        		if (attrDecl != null) {
                        			convertedResultSeq.add(nodeSetItem); 
                        		}
                        		else {
                        			throw new TransformerException("XTTE0570 : A sequence type check with schema-attribute was requested for an attribute node " + nodeName + ", "
                                                                                                               + "but the schema used to validate an XML input document doesn't "
                                                                                                               + "contain a global attribute declaration for this attribute node.", 
                                                                                                                                                                           srcLocator);
                        		}
                        	}
                        	else {                        		
                                throw new TransformerException("XTTE0570 : A sequence type check with schema-attribute was requested for an attribute node " + nodeName + ", "
                                		                                                                         + "but an XML input document has not been validated with a "
                                		                                                                         + "schema.", srcLocator);
                        	}
                        }
                        else if ((sequenceTypeKindTest.getKindVal() == NODE_KIND) || (sequenceTypeKindTest.getKindVal() == ITEM_KIND)) {
                            convertedResultSeq.add(nodeSetItem); 
                        }
                        else {
                            String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                            throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                            		                                             + "The supplied value " + nodeName + " does'nt match an expected "
                            		                                             + "type.", srcLocator);  
                        }
                        
                        if (nodeSetLen == 1) {
                           result = nodeSetItem;
                           
                           return result;
                        }
                     }
                     else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.TEXT_NODE) {
                        if (sequenceTypeKindTest.getKindVal() == TEXT_KIND) {
                           convertedResultSeq.add(nodeSetItem); 
                        }
                        else {
                           String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                           throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                                                                                + "The supplied value does'nt match an expected type.", srcLocator); 
                        }
                        
                        if (nodeSetLen == 1) {
                           result = nodeSetItem;
                           
                           return result;
                        }
                     }
                     else if (dtm.getNodeType(nextNodeDtmHandle) == DTM.NAMESPACE_NODE) {
                         if (sequenceTypeKindTest.getKindVal() == NAMESPACE_NODE_KIND) {
                            convertedResultSeq.add(nodeSetItem); 
                         }
                         else {
                            String dataTypeStr = (sequenceTypeNewXPathExprStr != null) ? sequenceTypeNewXPathExprStr : sequenceTypeXPathExprStr;
                            throw new TransformerException("XTTE0570 : The required item type of an xdm input node is " + dataTypeStr + ". "
                                                                                 + "The supplied value does'nt match an expected type.", srcLocator);
                         }
                         
                         if (nodeSetLen == 1) {
                            result = nodeSetItem;
                            
                            return result;
                         }
                     }
                     else {
                         convertedResultSeq.add(nodeSetItem);
                         
                         if (nodeSetLen == 1) {
                            result = nodeSetItem;
                            
                            return result;
                         }
                     }
                  }
               }
               else {
                  if (sequenceTypeNewXPathExprStr == null) {
                     sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr;  
                  }
                  
                  if (!((xctxt == null) && ((seqExpectedTypeDataInp != null) && 
                                                               (seqExpectedTypeDataInp.getSequenceTypeKindTest() == null)))) {
                     String nodeStrVal = nodeSetItem.str();
                     XObject xObject = castXdmValueToAnotherType(new XSString(nodeStrVal), sequenceTypeNewXPathExprStr, 
                                                                                                                    seqExpectedTypeDataInp, xctxt);                     
                     if (nodeSetLen == 1) {
                         result = xObject;
                         
                         return result;
                     }
                     else {
                    	 convertedResultSeq.add(xObject); 
                     }
                  }
                  else {
                     throw new TransformerException("XTTE0570 : The source xdm sequence cannot be cast "
                                                                                         + "to the provided sequence type.", srcLocator); 
                  }                  
               }
            }
            
            result = convertedResultSeq;
        }
        
        return result;
    }

	/**
     * This method casts an xdm input sequence value, to another type 
     * specified by a sequence type expression.
     */
    private static XObject castResultSequenceInstance(ResultSequence srcSeqValue, String sequenceTypeXPathExprStr,
                                                      SequenceTypeData seqExpectedTypeDataInp, XPathContext xctxt,
                                                      SourceLocator srcLocator, int expectedType, int itemTypeOccurenceIndicator) 
                                                    		                                                     throws TransformerException {
        
        XObject result = null;
        
        int seqLen = srcSeqValue.size();
        
        if ((seqLen == 0) && (itemTypeOccurenceIndicator == OccurrenceIndicator.ONE_OR_MANY)) {
           throw new TransformerException("XTTE0570 : An empty sequence is not allowed, as result of evaluation for sequence "
                                                                                                              + "type's occurence indicator +.", srcLocator);  
        }
        else if ((seqLen > 0) && (expectedType == EMPTY_SEQUENCE)) {
           throw new TransformerException("XTTE0570 : The sequence doesn't conform to an expected type empty-sequence(). "
                                                                                               + "The supplied sequence has size " + seqLen + ".", srcLocator);  
        }
        else if ((seqLen > 1) && (itemTypeOccurenceIndicator == OccurrenceIndicator.ZERO_OR_ONE)) {
            throw new TransformerException("XTTE0570 : A sequence of size " + seqLen + ", cannot be cast to a type " 
                                                                                                                + sequenceTypeXPathExprStr + ".", srcLocator); 
        }
        else {
            String sequenceTypeNewXPathExprStr = null;
            if (sequenceTypeXPathExprStr != null) {
                if (sequenceTypeXPathExprStr.endsWith(Q_MARK) || sequenceTypeXPathExprStr.endsWith(STAR) || 
                                                                                                sequenceTypeXPathExprStr.endsWith(PLUS)) {
                   sequenceTypeNewXPathExprStr = sequenceTypeXPathExprStr.substring(0, sequenceTypeXPathExprStr.length() - 1);  
                }
            }
            
            ResultSequence convertedResultSeq = new ResultSequence();
            
            for (int idx = 0; idx < srcSeqValue.size(); idx++) {
               XObject seqItem = (XObject)(srcSeqValue.item(idx));                       
               XObject convertedSeqItem = castXdmValueToAnotherType(seqItem, sequenceTypeNewXPathExprStr, 
                                                                                                         seqExpectedTypeDataInp, xctxt);
               convertedResultSeq.add(convertedSeqItem);
            }
            
            result = convertedResultSeq; 
        }
        
        return result;
    }
    
    /**
     * Check whether the, xdm sequence type's xalan-j kind value refers to one of 
     * the various xdm node kinds.
     */
    private static boolean isSequenceTypeExprNodeKind(int seqTypekindVal) {		
    	boolean result = false;
		
		result = ((seqTypekindVal == SequenceTypeSupport.ELEMENT_KIND) || 
										                      (seqTypekindVal == SequenceTypeSupport.ATTRIBUTE_KIND) || 
										                      (seqTypekindVal == SequenceTypeSupport.NAMESPACE_NODE_KIND) || 
										                      (seqTypekindVal == SequenceTypeSupport.NODE_KIND));
		
		return result; 
	}
    
    /**
     * Check whether, an XML document input node's name conforms to the 
     * node's expected name specified within sequence type.
     */
    private static boolean isNodeNameOk(String nodeLocalName, String nodeNsUri, String nodeExpectedLocalName,
																			    String nodeExpectedNsUri) {
		boolean result = true;
		
		if (!((nodeExpectedLocalName == null) || ("".equals(nodeExpectedLocalName))) && !"*".equals(nodeExpectedLocalName)) {
			if (!nodeLocalName.equals(nodeExpectedLocalName)) {
				result = false;
			}
			else if ((nodeNsUri != null) && (nodeExpectedNsUri == null)) {
				result = false;
			}
			else if ((nodeExpectedNsUri != null) && (nodeNsUri == null)) {
				result = false;
			}
			else if ((nodeExpectedNsUri != null) && (nodeNsUri != null) && !nodeExpectedNsUri.equals(nodeNsUri)) {
				result = false;
			} 
		}		
		
		return result;
	}

}
