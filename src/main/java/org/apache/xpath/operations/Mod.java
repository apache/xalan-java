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
import org.apache.xpath.XPathArithmeticOp;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.composite.XPathSequenceType;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An implementation of XPath operator 'mod'.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3 specific changes, to this class)
 */
public class Mod extends XPathArithmeticOp
{
   static final long serialVersionUID = 5009471154238918201L;

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
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
  {
	  
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
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of 'mod', but "
																												  + "the supplied type is a map "
																												  + "type which cannot be atomized.", this); 
	  }

	  if (right instanceof XPathMap) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of 'mod', but "
																												  + "the supplied type is a map "
																												  + "type which cannot be atomized.", this); 
	  }
	  
	  if (isXPathOperandXdmFunctionItem(left)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of 'mod', but "
																												  + "the supplied type is a function "
																												  + "type which cannot be atomized.", this); 
	  }

	  if (isXPathOperandXdmFunctionItem(right)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of 'mod', but "
																												  + "the supplied type is a function "
																												  + "type which cannot be atomized.", this); 
	  }
	  
	  if ((left instanceof ResultSequence) && (((ResultSequence)left).size() == 0)) {
		 return new ResultSequence();  
	  }
	  
	  if ((right instanceof ResultSequence) && (((ResultSequence)right).size() == 0)) {
		 return new ResultSequence();  
	  }
	  
	  XPathSequenceType xpathSeqTypeResultData = getXdmSequenceTypeResultData(left, right);

	  java.lang.String lNodeStr = null;
	  java.lang.String rNodeStr = null;

	  java.lang.String typeName1 = null;
	  java.lang.String typeNs1 = null;

	  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();
	  
	  if (left instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)left;
		  
		  int nextNode = xmlNodeCursorImpl.asNode(xctxt);
		  
		  if (nextNode != DTM.NULL) {
			  DTM dtm = xctxt.getDTM(nextNode);
			  Node node = dtm.getNode(nextNode);
			  if (node instanceof ElementPSVI) {
				  ElementPSVI elementPsvi = (ElementPSVI)node;
				  XSTypeDefinition typeDefn = elementPsvi.getTypeDefinition();
				  if (typeDefn instanceof XSComplexTypeDefinition) {
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'mod' operand, cannot be a "
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

			  XMLString xmlStr1 = dtm.getStringValue(nextNode);
			  lNodeStr = xmlStr1.toString();
		  }
	  }
	  
	  java.lang.String typeName2 = null;
	  java.lang.String typeNs2 = null;
	  
	  if (right instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)right;
		  
		  int nextNode = xmlNodeCursorImpl.asNode(xctxt);
		  
		  if (nextNode != DTM.NULL) {
			  DTM dtm = xctxt.getDTM(nextNode);
			  Node node = dtm.getNode(nextNode);
			  if (node instanceof ElementPSVI) {
				  ElementPSVI elementPsvi = (ElementPSVI)node;
				  XSTypeDefinition typeDefn = elementPsvi.getTypeDefinition();
				  if (typeDefn instanceof XSComplexTypeDefinition) {
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'mod' operand, cannot be a "
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

			  XMLString xmlStr2 = dtm.getStringValue(nextNode);
			  rNodeStr = xmlStr2.toString();
		  }
	  }
	  
	  if (left instanceof XSNumericType) {
		  if ((right instanceof XSString) || (right instanceof XString)) {
			  java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(right);
			  
			  try {
				  double dbl2 = Double.valueOf(str2);
				  right = new XSDouble(dbl2);

				  typeName2 = "double";
				  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			  }
			  catch (NumberFormatException ex) {
				  // no op	
			  }
		  }
	  }

	  if (right instanceof XSNumericType) {
		  if ((left instanceof XSString) || (left instanceof XString)) {
			  java.lang.String str1 = XslTransformEvaluationHelper.getStrVal(left);
			  
			  try {
				  double dbl1 = Double.valueOf(str1);
				  left = new XSDouble(dbl1);

				  typeName2 = "double";
				  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			  }
			  catch (NumberFormatException ex) {
				  // no op	
			  }
		  }
	  }
	  
	  // Validating an XPath 3.1 operator 'mod' operands compatibility for computing modulus	  
	  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs1) && (XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs2)) {
		  if ((isXsBuiltInTypeNumeric(typeName1) && !isXsBuiltInTypeNumeric(typeName2)) || 
				                                                                   (isXsBuiltInTypeNumeric(typeName2) && !isXsBuiltInTypeNumeric(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'mod' cannot apply values of schema "
					                                                                                                        + "types " + typeName1 + " and " + typeName2 + ".");
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
		  java.lang.String xpathInlineFuncExprStr = "function($arg0, $arg1) { $arg0 mod $arg1 }";
		  
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  
		  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  
		  if (xpathSeqTypeResultData != null) {
  			 result.setCastAsType(xpathSeqTypeResultData); 
  		  }

		  return result;
	  }
	  else if ((lObj instanceof FuncArgPlaceholder) && !(rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
		  java.lang.String xpathInlineFuncExprStr = "function($arg0) { $arg0 mod " + rStr + " }";
		  
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  
		  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  
		  if (xpathSeqTypeResultData != null) {
	  		 result.setCastAsType(xpathSeqTypeResultData); 
	  	  }

		  return result;
	  }
	  else if (!(lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(left);
		  java.lang.String xpathInlineFuncExprStr = "function($arg1) { " + lStr + " mod $arg1 }";
		  
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  
		  result = xpathObj.execute(xctxt, DTM.NULL, null);
		  
		  if (xpathSeqTypeResultData != null) {
	  		 result.setCastAsType(xpathSeqTypeResultData); 
	  	  }

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

	  if ((left instanceof XSUntyped) && (right instanceof XSUntyped)) {
          java.lang.String lStrVal = ((XSUntyped)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntyped)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble % rDouble);
      }
      else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntypedAtomic)) {
          java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble % rDouble);
      }
      else if ((left instanceof XSUntyped) && (right instanceof XSUntypedAtomic)) {
          java.lang.String lStrVal = ((XSUntyped)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntypedAtomic)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble % rDouble);
      }
      else if ((left instanceof XSUntypedAtomic) && (right instanceof XSUntyped)) {
          java.lang.String lStrVal = ((XSUntypedAtomic)left).stringValue();
          double lDouble = (Double.valueOf(lStrVal)).doubleValue();
          
          java.lang.String rStrVal = ((XSUntyped)right).stringValue();
          double rDouble = (Double.valueOf(rStrVal)).doubleValue();
          
          result = new XSDouble(lDouble % rDouble);
      }
	  else if ((left instanceof XNumber) && (right instanceof XSNumericType)) {
		  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
		  
    	  result = arithmeticOpOnXNumberValues((XNumber)left, rightXNumber, OP_SYMBOL_MOD, elemTemplateElement);
	  }
	  else if ((left instanceof XSNumericType) && (right instanceof XNumber)) {
		  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
		  
    	  result = arithmeticOpOnXNumberValues(leftXNumber, (XNumber)right, OP_SYMBOL_MOD, elemTemplateElement);
	  }     
	  else if ((left instanceof XSNumericType) && (right instanceof XSNumericType)) {
		  XNumber leftXNumber = getXNumberFromXSNumericType((XSNumericType)left);
    	  XNumber rightXNumber = getXNumberFromXSNumericType((XSNumericType)right);
    	  
    	  result = arithmeticOpOnXNumberValues(leftXNumber, rightXNumber, OP_SYMBOL_MOD, elemTemplateElement);
	  }
	  else if ((left instanceof XNumber) && (right instanceof XNumber)) {
		  XNumber lNumber = (XNumber)left;
	   	  XNumber rNumber = (XNumber)right;
	   	  
	   	  result = arithmeticOpOnXNumberValues(lNumber, rNumber, OP_SYMBOL_MOD, elemTemplateElement);
	  }
	  else if ((left instanceof XNumber) && (right instanceof XMLNodeCursorImpl)) {
		  double lDouble = ((XNumber)left).num();

		  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
		  if (rNodeSet.getLength() > 1) {			  
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);
		  }
		  else {
			  double rDouble = Double.valueOf(rNodeStr);

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XNumber)) {
		  double rDouble = ((XNumber)right).num();

		  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
		  if (lNodeSet.getLength() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {
			  double lDouble = Double.valueOf(lNodeStr);

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof XSNumericType) && (right instanceof XMLNodeCursorImpl)) {
		  java.lang.String lStrVal = ((XSNumericType)left).stringValue();
		  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

		  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
		  if (rNodeSet.getLength() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {
			  double rDouble = Double.valueOf(rNodeStr);

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XSNumericType)) {
		  java.lang.String rStrVal = ((XSNumericType)right).stringValue();
		  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

		  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
		  if (lNodeSet.getLength() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement); 
		  }
		  else {
			  double lDouble = Double.valueOf(lNodeStr);

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof XMLNodeCursorImpl) && (right instanceof XMLNodeCursorImpl)) {
		  double lDouble = 0.0d;
		  double rDouble = 0.0d;

		  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
		  if (lNodeSet.getLength() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {
			  lDouble = Double.valueOf(lNodeStr);
		  }

		  XMLNodeCursorImpl rNodeSet = (XMLNodeCursorImpl)right;
		  if (rNodeSet.getLength() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement); 
		  }
		  else {
			  rDouble = Double.valueOf(rNodeStr);
		  }

		  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
	  }     
	  else if ((left instanceof ResultSequence) && (right instanceof XNumber)) {
		  ResultSequence rsLeft = (ResultSequence)left;          
		  if (rsLeft.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {
			  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
			  double lDouble = (Double.valueOf(lStr)).doubleValue();

			  double rDouble = ((XNumber)right).num();

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof XNumber) && (right instanceof ResultSequence)) {
		  ResultSequence rsRight = (ResultSequence)right;          
		  if (rsRight.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {             
			  double lDouble = ((XNumber)left).num();

			  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
			  double rDouble = (Double.valueOf(rStr)).doubleValue();

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof ResultSequence) && (right instanceof XSNumericType)) {
		  ResultSequence rsLeft = (ResultSequence)left;          
		  if (rsLeft.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {
			  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
			  double lDouble = (Double.valueOf(lStr)).doubleValue();

			  java.lang.String rStrVal = ((XSNumericType)right).stringValue();
			  double rDouble = (Double.valueOf(rStrVal)).doubleValue();

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  } 
	  }
	  else if ((left instanceof XSNumericType) && (right instanceof ResultSequence)) {
		  ResultSequence rsRight = (ResultSequence)right;          
		  if (rsRight.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }
		  else {                          
			  java.lang.String lStrVal = ((XSNumericType)left).stringValue();
			  double lDouble = (Double.valueOf(lStrVal)).doubleValue();

			  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
			  double rDouble = (Double.valueOf(rStr)).doubleValue();

			  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
		  }
	  }
	  else if ((left instanceof ResultSequence) && (right instanceof ResultSequence)) {
		  ResultSequence rsLeft = (ResultSequence)left;          
		  if (rsLeft.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }

		  ResultSequence rsRight = (ResultSequence)right;          
		  if (rsRight.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);  
		  }

		  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(rsLeft.item(0));
		  double lDouble = (Double.valueOf(lStr)).doubleValue();

		  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(rsRight.item(0));
		  double rDouble = (Double.valueOf(rStr)).doubleValue();

		  result = new XSDecimal(BigDecimal.valueOf(lDouble % rDouble));
	  }
	  else if (left instanceof ResultSequence) {
		  ResultSequence rSeq = (ResultSequence)left;
		  if (rSeq.size() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement); 
		  }                  

		  BigDecimal lBigDecimal = null;
		  BigDecimal rBigDecimal = null;

		  try {
			  java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(rSeq.item(0));
			  java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
			  
			  lBigDecimal = new BigDecimal(lStrVal); 
			  rBigDecimal = new BigDecimal(rStrVal);
			  
			  result = new XSDecimal(lBigDecimal.remainder(rBigDecimal));
		  }
		  catch (NumberFormatException ex) {
			  error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);
		  }
		  catch (ArithmeticException ex) {
			  error(DIV_BY_ZERO_ERR_MESG, new java.lang.String[] {"FOAR0001"}, elemTemplateElement);
		  }
	  }
	  else if (left instanceof XSYearMonthDuration) {
		  try {
			  java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
			  
			  result = ((XSYearMonthDuration)left).div(new XSDouble(rStrVal));
		  }
		  catch (XPathException ex) {
			  throw new javax.xml.transform.TransformerException(ex.getMessage());  
		  }
	  }
	  else if (left instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl lNodeSet = (XMLNodeCursorImpl)left;
		  if (lNodeSet.getLength() > 1) {
			  error(CARDINALITY_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement); 
		  }                  

		  BigDecimal lBigDecimal = null;
		  BigDecimal rBigDecimal = null;

		  try {
			  java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
			  
			  lBigDecimal = new BigDecimal(lNodeStr); 
			  rBigDecimal = new BigDecimal(rStrVal);
			  
			  result = new XSDecimal(lBigDecimal.remainder(rBigDecimal));
		  }
		  catch (NumberFormatException ex) {
			  error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement);
		  }
		  catch (ArithmeticException ex) {
			  error(DIV_BY_ZERO_ERR_MESG, new java.lang.String[] {"FOAR0001"}, elemTemplateElement);
		  }         
	  }
	  else if ((left instanceof XString) || (left instanceof XSString)) {
		  throw new TransformerException("XPTY0004 : An XPath operator 'mod' is not defined for schema type string valued operands.");
	  }
      else if ((right instanceof XString) || (right instanceof XSString)) {
    	  throw new TransformerException("XPTY0004 : An XPath operator 'mod' is not defined for schema type string valued operands.");
	  }
	  else {
		  try {
			  java.lang.String lStrVal = XslTransformEvaluationHelper.getStrVal(left);
			  java.lang.String rStrVal = XslTransformEvaluationHelper.getStrVal(right);
			  
			  result = new XSDecimal(BigDecimal.valueOf(Double.valueOf(lStrVal) % Double.valueOf(rStrVal)));
		  }
		  catch (NumberFormatException ex) {
			  error(OPERAND_NOT_NUMERIC_ERR_MESG, new java.lang.String[] {"XPTY0004", OP_SYMBOL_MOD}, elemTemplateElement); 
		  }
	  }
	  
	  if (result != null) {
		  if (xpathSeqTypeResultData != null) {
			  result.setCastAsType(xpathSeqTypeResultData); 
		  } 
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
  public double num(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
      return (m_left.num(xctxt) % m_right.num(xctxt));
  }

}
