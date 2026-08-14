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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathArithmeticUtil;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An implementation of XPath operator '+'.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 specific changes, to this class)
 */
public class Plus extends XPathArithmeticUtil
{
   static final long serialVersionUID = -4492072861616504256L;

  /**
   * Apply XPath operator to two operands, and return the result.
   *
   * @param left non-null reference to the evaluated first operand.
   * @param right non-null reference to the evaluated second operand.
   *
   * @return non-null reference to an XObject object reference that,
   *         represents the result of XPath expression evaluation.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException {
      
	  XObject result = null;
	  
	  Object lObj = left.object();
	  Object rObj = right.object();
	  
	  XPathContext xctxt = null;
      
      StylesheetRoot stylesheetRoot = null;
	  
      if (XslTransformData.m_stylesheetRoot != null) {
    	  stylesheetRoot = XslTransformData.m_stylesheetRoot; 
    	  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
    	  xctxt = transformerImpl.getXPathContext();
      }
      else {
    	  stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(this);    	  
    	  
    	  if (stylesheetRoot != null) {
    		 TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
     	     xctxt = transformerImpl.getXPathContext();
     	  }
     	  else {
     		 xctxt = new XPathContext();
     	  }  
      }
	  
	  if (left instanceof XPathMap) {
		 throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of '+', but "
		 		                                                                                           + "the supplied type is a map "
		 		                                                                                           + "type which cannot be atomized.", this); 
	  }
	  
	  if (right instanceof XPathMap) {
		 throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of '+', but "
			 		                                                                                       + "the supplied type is a map "
			 		                                                                                       + "type which cannot be atomized.", this); 
	  }
	  
	  if (isXPathOperandXdmFunctionItem(left)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of '+', but "
																										  + "the supplied type is a function "
																										  + "type which cannot be atomized.", this); 
	  }

	  if (isXPathOperandXdmFunctionItem(right)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of '+', but "
																										  + "the supplied type is a function "
																										  + "type which cannot be atomized.", this); 
	  }	  	  
	  
	  java.lang.String lNodeStr = null;
	  java.lang.String rNodeStr = null;
	  
	  java.lang.String typeName1 = null;
	  java.lang.String typeNs1 = null;
	  
	  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();
	  
	  if ((left instanceof XNumber) || (left instanceof XSNumericType)) {
		 if (right instanceof XMLNodeCursorImpl) {
			java.lang.String str1 = ((XMLNodeCursorImpl)right).str();			
			
			try {
			   double dbl = Double.valueOf(str1);
			   
			   right = new XSDouble(dbl);
			}
			catch (NumberFormatException ex) {
			   right = right.getFresh(); 
			}
		 }
	  }
	  
	  if ((right instanceof XNumber) || (right instanceof XSNumericType)) {
		 if (left instanceof XMLNodeCursorImpl) {
			java.lang.String str1 = ((XMLNodeCursorImpl)left).str();			
			
			try {
			   double dbl = Double.valueOf(str1);
			   
			   left = new XSDouble(dbl);
			}
			catch (NumberFormatException ex) {
			   left = left.getFresh(); 
			}
		 }
	  }
	  
	  if ((left instanceof XNumber) && (right instanceof XNumber)) {		  
		 double dbl1 = ((XNumber)left).num();
		 double dbl2 = ((XNumber)right).num();
		 
		 if (!(Double.isInfinite(dbl1) || Double.isInfinite(dbl2) 
				                                              || Double.isNaN(dbl1) || Double.isNaN(dbl2))) {
		    result = arithmeticOpOnXNumberValues((XNumber)left, (XNumber)right, OP_SYMBOL_PLUS, elemTemplateElement);
		    
		    return result;
		 }		 
	  }
	  
	  if ((left instanceof XSInteger) && (right instanceof XNumber)) {
		  XNumber xNumber = (XNumber)right;
		  
		  if (((int)xNumber.num()) == xNumber.num()) {
			  java.lang.String str1 = ((XSInteger)left).stringValue(); 			 			
			  BigInteger bigInt1 = new BigInteger(str1);

			  BigInteger bigInt2 = new BigInteger(((int)xNumber.num()) + "");

			  BigInteger bigIntResult = bigInt1.add(bigInt2);

			  result = new XSInteger(bigIntResult);

			  return result;
		  }
	  }
	  
	  if ((right instanceof XSInteger) && (left instanceof XNumber)) {
		  XNumber xNumber = (XNumber)left;
		  
		  if (((int)xNumber.num()) == xNumber.num()) {
			  java.lang.String str1 = ((XSInteger)right).stringValue(); 			 			
			  BigInteger bigInt1 = new BigInteger(str1);

			  BigInteger bigInt2 = new BigInteger(((int)xNumber.num()) + "");

			  BigInteger bigIntResult = bigInt1.add(bigInt2);

			  result = new XSInteger(bigIntResult);

			  return result;
		  }
	  }
	  
	  if ((left instanceof XSFloat) && (right instanceof XSFloat)) {
		  XSFloat xsFloat1 = (XSFloat)left;
		  XSFloat xsFloat2 = (XSFloat)right;
		  
		  Float flt1 = xsFloat1.floatValue();
		  Float flt2 = xsFloat2.floatValue();
		  
		  if (!Float.isInfinite(flt1) && !Float.isNaN(flt1) && !Float.isInfinite(flt2) && !Float.isNaN(flt2)) {
			 if ((flt1 == 0) && (flt2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MIN_INCLUSIVE)) {
				return right;  
			 }
			 else if ((flt1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MIN_INCLUSIVE) && (flt2 == 0)) {
				return left;  
			 }
			 else if ((flt1 == 0) && (flt2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MAX_INCLUSIVE)) {
				 return right;  
			 }
			 else if ((flt1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MAX_INCLUSIVE) && (flt2 == 0)) {
				 return left;  
			 }
			 else if ((flt1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MAX_INCLUSIVE) && 
					                                                                    (flt2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MIN_INCLUSIVE)) {
				 return new XSFloat(0);  
			 }
			 else if ((flt1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MIN_INCLUSIVE) && 
					                                                                    (flt2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Float.MAX_INCLUSIVE)) {
				 return new XSFloat(0);  
			 }
		  }
	  }
	  
	  if ((left instanceof XSDouble) && (right instanceof XSDouble)) {
		  XSDouble xsDouble1 = (XSDouble)left;
		  XSDouble xsDouble2 = (XSDouble)right;
		  
		  Double dbl1 = xsDouble1.doubleValue();
		  Double dbl2 = xsDouble2.doubleValue();
		  
		  if (!Double.isInfinite(dbl1) && !Double.isNaN(dbl1) && !Double.isInfinite(dbl2) && !Double.isNaN(dbl2)) {
			 if ((dbl1 == 0) && (dbl2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MIN_INCLUSIVE)) {
				return right;  
			 }
			 else if ((dbl1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MIN_INCLUSIVE) && (dbl2 == 0)) {
				return left;  
			 }
			 else if ((dbl1 == 0) && (dbl2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MAX_INCLUSIVE)) {
				 return right;  
			 }
			 else if ((dbl1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MAX_INCLUSIVE) && (dbl2 == 0)) {
				 return left;  
			 }
			 else if ((dbl1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MAX_INCLUSIVE) && 
					                                                                    (dbl2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MIN_INCLUSIVE)) {
				 return new XSDouble(0);  
			 }
			 else if ((dbl1 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MIN_INCLUSIVE) && 
					                                                                    (dbl2 == XPathSequenceTypeSupport.XmlSchemaBuiltinNumericType.Double.MAX_INCLUSIVE)) {
				 return new XSDouble(0);  
			 }
		  }
	  }
	  
	  if (stylesheetRoot == null) {
		  // Stricter type checking, when invoked via an XPath api call
		  
		  if ((left instanceof XSString) || (left instanceof XString)) {
			  throw new TransformerException("XPTY0004 : An XPath operator '+' cannot have a string valued operand.");  
		  }

		  if ((right instanceof XSString) || (right instanceof XString)) {		  
			  throw new TransformerException("XPTY0004 : An XPath operator '+' cannot have a string valued operand.");  
		  }
	  }
	  
	  if ((left instanceof XSDecimal) && (right instanceof XSFloat)) {
		 XSDecimal xsDecimal = (XSDecimal)left;
		 XSFloat xsFloat = (XSFloat)right;
		 
		 float flt1 = (float)(xsDecimal.doubleValue());
		 float flt2 = xsFloat.floatValue();
		 
		 return new XSFloat(flt1 + flt2);
	  }
	  
	  if ((left instanceof XSFloat) && (right instanceof XSDecimal)) {
		  XSFloat xsFloat = (XSFloat)left;
		  XSDecimal xsDecimal = (XSDecimal)right;

		  float flt1 = xsFloat.floatValue();
		  float flt2 = (float)(xsDecimal.doubleValue());

		  return new XSFloat(flt1 + flt2);
	  }
	  
	  if ((left instanceof XSFloat) && (right instanceof XSInteger)) {
		  XSFloat xsFloat = (XSFloat)left;
		  XSInteger xsInteger = (XSInteger)right;

		  float flt1 = xsFloat.floatValue();
		  float flt2 = (float)(xsInteger.doubleValue());

		  return new XSFloat(flt1 + flt2);
	  }
	  
	  if ((left instanceof XSInteger) && (right instanceof XSFloat)) {		  
		  XSInteger xsInteger = (XSInteger)left;
		  XSFloat xsFloat = (XSFloat)right;
		  
		  float flt1 = (float)(xsInteger.doubleValue());
		  float flt2 = xsFloat.floatValue();

		  return new XSFloat(flt1 + flt2);
	  }
	  
	  if ((left instanceof XSFloat) && (right instanceof XSFloat)) {		  
		  XSFloat xsFloat1 = (XSFloat)left;
		  XSFloat xsFloat2 = (XSFloat)right;
		  
		  float flt1 = xsFloat1.floatValue();
		  float flt2 = xsFloat2.floatValue();

		  return new XSFloat(flt1 + flt2);
	  }
	  
	  if ((left instanceof XSDecimal) && (right instanceof XSDouble)) {		  
		  XSDecimal xsDecimal = (XSDecimal)left;
		  XSDouble xsDouble = (XSDouble)right;
		  
		  double dbl1 = xsDecimal.doubleValue();
		  double dbl2 = xsDouble.doubleValue();

		  return new XSDouble(dbl1 + dbl2);
	  }
	  
	  if ((left instanceof XSDouble) && (right instanceof XSDecimal)) {		  
		  XSDouble xsDouble = (XSDouble)left;
		  XSDecimal xsDecimal = (XSDecimal)right;
		  
		  double dbl1 = xsDouble.doubleValue();
		  double dbl2 = xsDecimal.doubleValue();

		  return new XSDouble(dbl1 + dbl2);
	  }
	  
	  if (left instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)left;
		  int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
		  
		  if (nodeHandle != DTM.NULL) {
			  DTM dtm = xctxt.getDTM(nodeHandle);
			  Node node = dtm.getNode(nodeHandle);
			  
			  if (node instanceof ElementPSVI) {
				  ElementPSVI elementPsvi = (ElementPSVI)node;
				  XSTypeDefinition typeDefn = elementPsvi.getTypeDefinition();
				  
				  if (typeDefn instanceof XSComplexTypeDefinition) {
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator '+' operand, cannot be a "
							  																							+ "node validated with schema complex type.");				  
				  }
				  else {
					  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)typeDefn;
					  typeName1 = xsSimpleTypeDecl.getTypeName();
					  typeNs1 = xsSimpleTypeDecl.getTypeNamespace();
					  
					  short xsSimpleTypeVariety = xsSimpleTypeDecl.getVariety();
					  
					  if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_ATOMIC) {
						 if (typeNs1 == null) {
							XSTypeDefinition xsTypeDefn = xsSimpleTypeDecl.getBaseType();
							typeName1 = xsTypeDefn.getName();
							typeNs1 = xsTypeDefn.getNamespace();
						 }						 
					  }
					  else if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_UNION) {
						 XSSimpleTypeDecl xsSimpleTypeDeclMemberType = (XSSimpleTypeDecl)(elementPsvi.getMemberTypeDefinition());
						 typeName1 = xsSimpleTypeDeclMemberType.getTypeName();
						 typeNs1 = xsSimpleTypeDeclMemberType.getTypeNamespace();
						 
						 if (typeNs1 == null) {
							 XSTypeDefinition xsTypeDefn = xsSimpleTypeDeclMemberType.getBaseType();
							 typeName1 = xsTypeDefn.getName();
							 typeNs1 = xsTypeDefn.getNamespace();
						 }
					  }
				  }
			  }
			  else if (node instanceof AttributePSVI) {
				  AttributePSVI attrPsvi = (AttributePSVI)node;
				  XSTypeDefinition typeDefn = attrPsvi.getTypeDefinition();
				  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)typeDefn;

				  typeName1 = xsSimpleTypeDecl.getTypeName();
				  typeNs1 = xsSimpleTypeDecl.getTypeNamespace();
				  
				  short xsSimpleTypeVariety = xsSimpleTypeDecl.getVariety();
				  
				  if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_ATOMIC) {
					 if (typeNs1 == null) {
						XSTypeDefinition xsTypeDefn = xsSimpleTypeDecl.getBaseType();
						typeName1 = xsTypeDefn.getName();
						typeNs1 = xsTypeDefn.getNamespace();
					 }						 
				  }
				  else if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_UNION) {
					  XSSimpleTypeDecl xsSimpleTypeDeclMemberType = (XSSimpleTypeDecl)(attrPsvi.getMemberTypeDefinition());
					  typeName1 = xsSimpleTypeDeclMemberType.getTypeName();
					  typeNs1 = xsSimpleTypeDeclMemberType.getTypeNamespace();
					  
					  if (typeNs1 == null) {
						  XSTypeDefinition xsTypeDefn = xsSimpleTypeDeclMemberType.getBaseType();
						  typeName1 = xsTypeDefn.getName();
						  typeNs1 = xsTypeDefn.getNamespace();
					  }
				  }
			  }

			  XMLString xmlStr1 = dtm.getStringValue(nodeHandle);
			  lNodeStr = xmlStr1.toString();
		  }
	  }
	  
	  java.lang.String typeName2 = null;
	  java.lang.String typeNs2 = null;
	  
	  if (right instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)right;
		  int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
		  
		  if (nodeHandle != DTM.NULL) {
			  DTM dtm = xctxt.getDTM(nodeHandle);
			  Node node = dtm.getNode(nodeHandle);
			  
			  if (node instanceof ElementPSVI) {
				  ElementPSVI elementPsvi = (ElementPSVI)node;
				  XSTypeDefinition typeDefn = elementPsvi.getTypeDefinition();
				  
				  if (typeDefn instanceof XSComplexTypeDefinition) {
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator '+' operand, cannot be a "
							  																							+ "node validated with schema complex type.");
				  }
				  else {
					  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)typeDefn;
					  typeName2 = xsSimpleTypeDecl.getTypeName();
					  typeNs2 = xsSimpleTypeDecl.getTypeNamespace();
					  
					  short xsSimpleTypeVariety = xsSimpleTypeDecl.getVariety();
					  
					  if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_ATOMIC) {
						 if (typeNs2 == null) {
							XSTypeDefinition xsTypeDefn = xsSimpleTypeDecl.getBaseType();
							typeName2 = xsTypeDefn.getName();
							typeNs2 = xsTypeDefn.getNamespace();
						 }						 
					  }
					  else if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_UNION) {
						  XSSimpleTypeDecl xsSimpleTypeDeclMemberType = (XSSimpleTypeDecl)(elementPsvi.getMemberTypeDefinition());
						  typeName2 = xsSimpleTypeDeclMemberType.getTypeName();
						  typeNs2 = xsSimpleTypeDeclMemberType.getTypeNamespace();
						  
						  if (typeNs2 == null) {
							  XSTypeDefinition xsTypeDefn = xsSimpleTypeDeclMemberType.getBaseType();
							  typeName2 = xsTypeDefn.getName();
							  typeNs2 = xsTypeDefn.getNamespace();
						  }
					  }
				  }
			  }
			  else if (node instanceof AttributePSVI) {
				  AttributePSVI attrPsvi = (AttributePSVI)node;
				  XSTypeDefinition typeDefn = attrPsvi.getTypeDefinition();
				  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)typeDefn;

				  typeName2 = xsSimpleTypeDecl.getTypeName();
				  typeNs2 = xsSimpleTypeDecl.getTypeNamespace();
				  
				  short xsSimpleTypeVariety = xsSimpleTypeDecl.getVariety();
				  
				  if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_ATOMIC) {
					 if (typeNs2 == null) {
						XSTypeDefinition xsTypeDefn = xsSimpleTypeDecl.getBaseType();
						typeName2 = xsTypeDefn.getName();
						typeNs2 = xsTypeDefn.getNamespace();
					 }						 
				  }
				  else if (xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_UNION) {
					  XSSimpleTypeDecl xsSimpleTypeDeclMemberType = (XSSimpleTypeDecl)(attrPsvi.getMemberTypeDefinition());
					  typeName2 = xsSimpleTypeDeclMemberType.getTypeName();
					  typeNs2 = xsSimpleTypeDeclMemberType.getTypeNamespace();
					  
					  if (typeNs2 == null) {
						  XSTypeDefinition xsTypeDefn = xsSimpleTypeDeclMemberType.getBaseType();
						  typeName2 = xsTypeDefn.getName();
						  typeNs2 = xsTypeDefn.getNamespace();
					  }
				  }
			  }

			  XMLString xmlStr2 = dtm.getStringValue(nodeHandle);
			  rNodeStr = xmlStr2.toString();
		  }
	  }
	  
	  if ((left instanceof XSDouble) && (right instanceof XSDouble)) {
		 XSDouble xsDouble1 = (XSDouble)left;
		 XSDouble xsDouble2 = (XSDouble)right;
		 
		 Double dbl1 = xsDouble1.doubleValue();
		 Double dbl2 = xsDouble2.doubleValue();
		 
		 if ((dbl1 == Double.POSITIVE_INFINITY) || (dbl2 == Double.POSITIVE_INFINITY)) {
			result = new XSDouble(Double.POSITIVE_INFINITY);
			
			return result;
		 }
		 
         if ((dbl1 == Double.NEGATIVE_INFINITY) || (dbl2 == Double.NEGATIVE_INFINITY)) {
        	result = new XSDouble(Double.NEGATIVE_INFINITY);
 			
 			return result; 
		 }
         
         if (dbl1.isNaN() || dbl2.isNaN()) {
        	result = new XSDouble(Double.NaN);
 			
 			return result; 
		 }
	  }
	  
	  if (left instanceof XSAnyAtomicType) {
		  typeName1 = ((XSAnyAtomicType)left).typeName();
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI; 
	  }
	  else if (left instanceof XNumber) {
		  typeName1 = "double";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI; 
	  }

	  if (right instanceof XSAnyAtomicType) {
		  typeName2 = ((XSAnyAtomicType)right).typeName();
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI; 
	  }
	  else if (right instanceof XNumber) {
		  typeName2 = "double";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI; 
	  }

	  java.lang.String typeName1Actual = typeName1;
	  java.lang.String typeName2Actual = typeName2;

	  if ("untypedAtomic".equals(typeName1)) {
		  typeName1 = "double";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI; 
	  }

	  if ("untypedAtomic".equals(typeName2)) {
		  typeName2 = "double";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  
	  // Validating an XPath 3.1 operator '+' operands compatibility for addition	  
	  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs1) && (XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs2)) {
		 if ((isXsBuiltInTypeNumeric(typeName1) && !isXsBuiltInTypeNumeric(typeName2)) || 
			                                                                       (isXsBuiltInTypeNumeric(typeName2) && !isXsBuiltInTypeNumeric(typeName1))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
                                                                                                                     + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }
		 else if ("date".equals(typeName1) && !("yearMonthDuration".equals(typeName2) || "dayTimeDuration".equals(typeName2))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
			 		                                                                                                 + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }		 
		 else if ("date".equals(typeName2) && !("yearMonthDuration".equals(typeName1) || "dayTimeDuration".equals(typeName1))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
			 		                                                                                                 + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }
		 else if (("time".equals(typeName1) && !"dayTimeDuration".equals(typeName2)) ||
				  ("dayTimeDuration".equals(typeName1) && !("date".equals(typeName2) || "time".equals(typeName2) || 
						                                                                "dateTime".equals(typeName2) ||
						                                                                "dayTimeDuration".equals(typeName2)))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
                                                                                                                     + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }		 
		 else if ("dateTime".equals(typeName1) && !("yearMonthDuration".equals(typeName2) || "dayTimeDuration".equals(typeName2))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
			 		                                                                                                 + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }
		 else if ("dateTime".equals(typeName2) && !("yearMonthDuration".equals(typeName1) || "dayTimeDuration".equals(typeName1))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
			 		                                                                                                 + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }
		 else if (("yearMonthDuration".equals(typeName1) && !("date".equals(typeName2) || "dateTime".equals(typeName2) || 
				                                                                          "yearMonthDuration".equals(typeName2))) ||
				  ("dayTimeDuration".equals(typeName1) && !("date".equals(typeName2) || "time".equals(typeName2) || 
						                                                                "dateTime".equals(typeName2) || 
						                                                                "dayTimeDuration".equals(typeName2)))) {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 operator '+' cannot add values of schema "
                                                                                                                     + "types " + typeName1Actual + " and " + typeName2Actual + ".");
		 }
		 
		 List<XMLNSDecl> nsPrefixTable = null;	  
		 
		 if (stylesheetRoot != null) {
			 nsPrefixTable = stylesheetRoot.getPrefixTable();
		 }
		 else {
			 PrefixResolverDefault xmlNsPrefixResolver = (PrefixResolverDefault)(getXMLNsPrefixResolver());
			 nsPrefixTable = xmlNsPrefixResolver.getPrefixTable();
		 }
		 
		 if (lNodeStr != null) {
			  java.lang.String xpathStr = (XMLConstants.W3C_XML_SCHEMA_NS_URI + ":" + typeName1 + "('" + lNodeStr + "')");
			  xpathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathStr, nsPrefixTable);
			  XPath xpathObj = null;
			  try {
			     xpathObj = new XPath(xpathStr, this, xctxt.getNamespaceContext(), XPath.SELECT, null);
			  }
			  catch (TransformerException ex) {
				 java.lang.String errMesg = ex.getMessage();
				 
				 if (errMesg.contains("XPST0081 : An XML namespace binding for prefix")) {
					xpathObj = new XPath("'" + lNodeStr + "'", this, xctxt.getNamespaceContext(), XPath.SELECT, null);   
				 }
			  }
			  
			  left = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		  }

		  if (rNodeStr != null) {
			  java.lang.String xpathStr = (XMLConstants.W3C_XML_SCHEMA_NS_URI + ":" + typeName2 + "('" + rNodeStr + "')");
			  xpathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathStr, nsPrefixTable);
			  XPath xpathObj = null;
			  try {
			     xpathObj = new XPath(xpathStr, this, xctxt.getNamespaceContext(), XPath.SELECT, null);
			  }
			  catch (TransformerException ex) {
				 java.lang.String errMesg = ex.getMessage();
				 
				 if (errMesg.contains("XPST0081 : An XML namespace binding for prefix")) {
					xpathObj = new XPath("'" + rNodeStr + "'", this, xctxt.getNamespaceContext(), XPath.SELECT, null);   
				 }
			  }
			  
			  right = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		  }
	  }	  	       	  

	  if ((lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String xpathInlineFuncExprStr = "function($arg0, $arg1) { $arg0 + $arg1 }";
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  
		  return result;
	  }
	  else if ((lObj instanceof FuncArgPlaceholder) && !(rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
		  java.lang.String xpathInlineFuncExprStr = "function($arg0) { $arg0 + " + rStr + " }";
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  
		  return result;
	  }
	  else if (!(lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(left);
		  java.lang.String xpathInlineFuncExprStr = "function($arg1) { " + lStr + " + $arg1 }";
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  
		  return result;
	  }
	  
	  Expression leftOperandExpr = getLeftOperand();	  
	  
	  if (leftOperandExpr instanceof SelfIteratorNoPredicate) {
		 left = getModifiedOperandValue(left, (SelfIteratorNoPredicate)leftOperandExpr);
	  }
	  
      Expression rightOperandExpr = getRightOperand();	  
	  
      if (rightOperandExpr instanceof SelfIteratorNoPredicate) {
		 right = getModifiedOperandValue(right, (SelfIteratorNoPredicate)rightOperandExpr);
	  }
   
	  try {
		  if ((left instanceof XSUntyped) && (right instanceof XSUntyped)) {
			  java.lang.String lStrVal = ((XSUntyped)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntyped)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
			  java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XSUntyped) && (right instanceof XSUntypedAtomic)) {
			  java.lang.String lStrVal = ((XSUntyped)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntyped)) {
			  java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStrVal = ((XSUntyped)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDouble(lDouble + rDouble);
		  }
		  else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
			  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
			  
			  result = arithmeticOpOnXNumberValues((XNumber)left, rightXNumber, OP_SYMBOL_PLUS, elemTemplateElement);
		  }
		  else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
			  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
			  double lDbl = leftXNumber.num();
			  double rDbl = ((XNumber)right).num(); 
			  
			  if ((lDbl == Double.NEGATIVE_INFINITY) && ((rDbl != Double.NEGATIVE_INFINITY) || (rDbl != Double.POSITIVE_INFINITY) || (rDbl != Double.NaN))) {
				 result = new XSDouble(Double.NEGATIVE_INFINITY); 
			  }
			  else if ((lDbl == Double.POSITIVE_INFINITY) && ((rDbl != Double.NEGATIVE_INFINITY) || (rDbl != Double.POSITIVE_INFINITY) || (rDbl != Double.NaN))) {
				 result = new XSDouble(Double.POSITIVE_INFINITY); 
			  }
			  else if ((Double.valueOf(lDbl)).isNaN() && ((rDbl != Double.NEGATIVE_INFINITY) || (rDbl != Double.POSITIVE_INFINITY) || (rDbl != Double.NaN))) {
				 result = new XSDouble(Double.NaN); 
			  }
			  else {
			     result = arithmeticOpOnXNumberValues(leftXNumber, (XNumber)right, OP_SYMBOL_PLUS, elemTemplateElement);
			  }
		  }      
		  else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) { 
			  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
			  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
			  
			  result = arithmeticOpOnXNumberValues(leftXNumber, rightXNumber, OP_SYMBOL_PLUS, elemTemplateElement);			  
		  }
		  else if ((left instanceof XNumber) && (right instanceof XNumber)) {          
			  XNumber lNumber = (XNumber)left;
			  XNumber rNumber = (XNumber)right;
			  
			  result = arithmeticOpOnXNumberValues(lNumber, rNumber, OP_SYMBOL_PLUS, elemTemplateElement);
		  }
		  else if ((left instanceof XNumber) && (right instanceof XMLNodeCursorImpl)) {
			  double lDouble = ((XNumber)left).num();

			  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
			  
			  if (rNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {
				  double rDouble = getDoubleFromXdmNode(rNodeSet, xctxt);

				  result = new XNumber(lDouble + rDouble);
			  }
		  }
		  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XNumber)) {
			  double rDouble = ((XNumber)right).num();

			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {
				  double lDouble = getDoubleFromXdmNode(lNodeSet, xctxt);

				  result = new XNumber(lDouble + rDouble);
			  }
		  }
		  else if ((left instanceof XSNumericType) && (right instanceof XMLNodeCursorImpl)) {
			  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
			  
			  if (rNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {
				  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
				  BigDecimal rBigDecimal = new BigDecimal(rNodeStr);				  
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XSNumericType)) {
			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {				  				  
				  BigDecimal lBigDecimal = new BigDecimal(lNodeStr);				  
				  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }

			  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
			  
			  if (rNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  
			  BigDecimal lBigDecimal = new BigDecimal(lNodeStr);			  
			  BigDecimal rBigDecimal = new BigDecimal(rNodeStr);
			  
			  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
			  
			  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
				  result = new XSInteger(resultBigDecimal.toBigInteger()); 
			  }
			  else {
				  result = new XSDecimal(resultBigDecimal);
			  }
		  }      
		  else if ((left instanceof ResultSequence) && (right instanceof XNumber)) {
			  ResultSequence lSeq = (ResultSequence)left;
			  
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {        	  
				  BigDecimal lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));				  
				  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)right).num());
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XNumber) && (right instanceof ResultSequence)) {
			  ResultSequence rSeq = (ResultSequence)right;
			  
			  if (rSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {
				  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)left).num());
				  BigDecimal rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));				  
				  
				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof ResultSequence) && (right instanceof XSNumericType)) {
			  ResultSequence lSeq = (ResultSequence)left;
			  
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {        	  
				  BigDecimal lBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(lSeq.item(0)));				  
				  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)right).stringValue());

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XSNumericType) && (right instanceof ResultSequence)) {
			  ResultSequence rSeq = (ResultSequence)right;
			  
			  if (rSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }
			  else {
				  BigDecimal lBigDecimal = new BigDecimal(((XSNumericType)left).stringValue());
				  BigDecimal rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(rSeq.item(0)));				  

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if ((left instanceof XSYearMonthDuration) && (right instanceof XSYearMonthDuration)) {
			  result = ((XSYearMonthDuration)left).add((XSYearMonthDuration)right);  
		  }
		  else if ((left instanceof XSDayTimeDuration) && (right instanceof XSDayTimeDuration)) {
			  result = ((XSDayTimeDuration)left).add((XSDayTimeDuration)right);  
		  }
		  else if ((left instanceof XSDayTimeDuration) && (right instanceof XSTime)) {
			  result = ((XSDayTimeDuration)left).add((XSTime)right);  
		  }
		  else if (left instanceof XSDate) {
			  result = ((XSDate)left).add(right);  
		  }
		  else if (right instanceof XSDate) {
			  result = ((XSDate)right).add(left);  
		  }
		  else if (left instanceof XSDateTime) {			  
			  result = ((XSDateTime)left).add(right);
		  }
		  else if (right instanceof XSDateTime) {
			  result = ((XSDateTime)right).add(left);  
		  }
		  else if (left instanceof XSTime) {
			  result = ((XSTime)left).add(right);  
		  }
		  else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
			  ResultSequence lSeq = (ResultSequence)left;          
			  
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }

			  ResultSequence rSeq = (ResultSequence)right;
			  
			  if (rSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement); 
			  }

			  XObject lXObj = ((ResultSequence)left).item(0);
			  XObject rXObj = ((ResultSequence)right).item(0);

			  if (lXObj instanceof XSDate) {
				  result = ((XSDate)lXObj).add(rXObj); 
			  }
			  else if (lXObj instanceof XSDateTime) {
				  result = ((XSDateTime)lXObj).add(rXObj); 
			  }
			  else if (lXObj instanceof XSTime) {
				  result = ((XSTime)lXObj).add(rXObj); 
			  }
			  else {				  
				  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
				  BigDecimal lBigDecimal = new BigDecimal(lStr);	              
				  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rXObj);
				  BigDecimal rBigDecimal = new BigDecimal(rStr);

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if (left instanceof ResultSequence) {
			  ResultSequence lSeq = (ResultSequence)left;          
			  
			  if (lSeq.size() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement);  
			  }

			  XObject lXObj = ((ResultSequence)left).item(0);
			  
			  if (lXObj instanceof XSDate) {
				  result = ((XSDate)lXObj).add(right); 
			  }
			  else if (lXObj instanceof XSDateTime) {
				  result = ((XSDateTime)lXObj).add(right); 
			  }
			  else if (lXObj instanceof XSTime) {
				  result = ((XSTime)lXObj).add(right); 
			  }
			  else {        	  
				  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(lXObj);
				  BigDecimal lBigDecimal = new BigDecimal(lStr);	              
				  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
				  BigDecimal rBigDecimal = new BigDecimal(rStr);

				  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
				  
				  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
					  result = new XSInteger(resultBigDecimal.toBigInteger()); 
				  }
				  else {
					  result = new XSDecimal(resultBigDecimal);
				  }
			  }
		  }
		  else if (left instanceof XMLNodeCursorImpl) {
			  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
			  
			  if (lNodeSet.getLength() > 1) {
				  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_PLUS}, elemTemplateElement); 
			  }

			  BigDecimal lBigDecimal = new BigDecimal(lNodeStr);
			  BigDecimal rBigDecimal = new BigDecimal(XslTransformEvaluationHelper.getStrVal(right));			  

			  BigDecimal resultBigDecimal = lBigDecimal.add(rBigDecimal);
			  
			  if (resultBigDecimal.compareTo(new BigDecimal(resultBigDecimal.toBigInteger())) == 0) {
				  result = new XSInteger(resultBigDecimal.toBigInteger()); 
			  }
			  else {
				  result = new XSDecimal(resultBigDecimal);
			  }
		  }
		  else {
			  java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
			  java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
			  
			  result = new XNumber(Double.valueOf(lStrVal) + Double.valueOf(rStrVal));			 
		  }
	  }
	  catch (NumberFormatException ex) {
		  java.lang.String lStrValue = null;
		  
		  if (left instanceof XMLNodeCursorImpl) {			  			  
			  lStrValue = lNodeStr; 
		  }
		  
		  java.lang.String rStrValue = null;
		  
		  if (right instanceof XMLNodeCursorImpl) {			  
			  rStrValue = rNodeStr; 
		  }
		  
		  if ("".equals(lStrValue) || "".equals(rStrValue)) {
		     result = new ResultSequence();
		  }
		  else {
			 throw new javax.xml.transform.TransformerException("XPTY0004 : One or both of the operands of operator + are not numeric."); 
		  }
	  }
	  catch (Exception ex) {
		  result = new ResultSequence();
	  }
      
      return result;
  }

  /**
   * Evaluate this operation directly to a double.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The result of the operation as a double.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    return (m_right.num(xctxt) + m_left.num(xctxt));
  }

}
