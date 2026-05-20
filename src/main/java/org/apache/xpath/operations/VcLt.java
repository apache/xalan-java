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

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

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
import org.apache.xml.utils.Constants;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathRelationalOp;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSString;

/**
 * The XPath 3.1 value comparison "lt" operation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class VcLt extends XPathRelationalOp
{

   private static final long serialVersionUID = 3832212036565766741L;

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
      SourceLocator srcLocator = null;
      
      StylesheetRoot stylesheetRoot = null;
	  
      if (XslTransformData.m_stylesheetRoot != null) {
    	  stylesheetRoot = XslTransformData.m_stylesheetRoot; 
    	  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
    	  xctxt = transformerImpl.getXPathContext();
    	  
    	  srcLocator = xctxt.getSAXLocator(); 
      }
      else {
    	  stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(this);
    	  TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
    	  xctxt = transformerImpl.getXPathContext();
    	  
    	  srcLocator = xctxt.getSAXLocator();  
      }
	  
      if (left instanceof XPathMap) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of 'lt', but "
																												  + "the supplied type is a map "
																												  + "type which cannot be atomized.", srcLocator); 
	  }

	  if (right instanceof XPathMap) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of 'lt', but "
																												  + "the supplied type is a map "
																												  + "type which cannot be atomized.", srcLocator); 
	  }
	  
	  if ((left instanceof XPathInlineFunction) || (left instanceof ElemFunctionItem)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of 'lt', but "
																												  + "the supplied type is a function "
																												  + "type which cannot be atomized.", srcLocator); 
	  }

	  if ((right instanceof XPathInlineFunction) || (right instanceof ElemFunctionItem)) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of 'lt', but "
																												  + "the supplied type is a function "
																												  + "type which cannot be atomized.", srcLocator); 
	  }
	  
	  boolean isLEmpty = false;
	  if (left instanceof ResultSequence) {
		  if (((ResultSequence)left).size() == 0) {
			  isLEmpty = true;
		  }
		  else if (((ResultSequence)left).size() > 1) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator lt's first operand cannot be a "
			  		                                                                                                      + "sequence of size greater than one.", srcLocator); 
		  }
	  }
	  else if (left instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl nodeRef1 = (XMLNodeCursorImpl)left;
		  if (nodeRef1.getLength() == 0) {
			  isLEmpty = true;
		  }
		  else if (nodeRef1.getLength() > 1) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator lt's first operand cannot be a "
			  		                                                                                                     + "sequence of size greater than one.", srcLocator);
		  }
	  }

	  boolean isREmpty = false;
	  if (right instanceof ResultSequence) {
		  if (((ResultSequence)right).size() == 0) {
			  isREmpty = true;
		  }
		  else if (((ResultSequence)right).size() > 1) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator lt's second operand cannot be a "
			  																											 + "sequence of size greater than one.", srcLocator);
		  }
	  }
	  else if (right instanceof XMLNodeCursorImpl) {
		  XMLNodeCursorImpl nodeRef1 = (XMLNodeCursorImpl)right;
		  if (nodeRef1.getLength() == 0) {
			  isREmpty = true;
		  }
		  else if (nodeRef1.getLength() > 1) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator lt's second operand cannot be a "
			  																									         + "sequence of size greater than one.", srcLocator);
		  }
	  }
	  
	  java.lang.String lNodeStr = null;
	  java.lang.String rNodeStr = null;
	  
	  java.lang.String typeName1 = null;
	  java.lang.String typeNs1 = null;
	  
	  if (left instanceof XMLNodeCursorImpl) {
		  left = left.getFresh();
		  
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)left;
		  int nodeHandle = (xmlNodeCursorImpl.iterRaw()).nextNode();
		  if (nodeHandle != DTM.NULL) {
			  DTM dtm = xctxt.getDTM(nodeHandle);
			  Node node = dtm.getNode(nodeHandle);
			  if (node instanceof ElementPSVI) {
				  ElementPSVI elementPsvi = (ElementPSVI)node;
				  XSTypeDefinition typeDefn = elementPsvi.getTypeDefinition();
				  if (typeDefn instanceof XSComplexTypeDefinition) {
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' operand, cannot be a "
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
					  
					  // To do, xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_LIST
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
				  
				  // To do, xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_LIST
			  }
			  else {
				  typeName1 = "string";
				  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			  }

			  XMLString xmlStr1 = dtm.getStringValue(nodeHandle);
			  lNodeStr = xmlStr1.toString();			  			  
		  }
	  }
	  else if (left instanceof XSAnyAtomicType) {
		  XSAnyAtomicType xsAnyAtomicType = (XSAnyAtomicType)left;
		  typeName1 = xsAnyAtomicType.stringType();

		  int colonIdx = typeName1.indexOf(':');
		  typeName1 = typeName1.substring(colonIdx + 1);
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  else if (left instanceof XNumber) {
		  double dbl = ((XNumber)left).num();
		  left = new XSDouble(dbl);
		  
		  typeName1 = "double";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }	  
	  else if ((left instanceof XBoolean) || (left instanceof XBooleanStatic)) {
		  left = new XSBoolean(left.bool());
		  
		  typeName1 = "boolean";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  else if (left instanceof XString) {
		  left = new XSString(((XString)left).str());
		  
		  typeName1 = "string";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  
	  if (left instanceof XSString) {
		  java.lang.String str1 = ((XSString)left).stringValue();		  
		  if ((Constants.XS_VALID_TRUE).equals(str1) && (left.getXsTypeDefinition() != null)) {
			  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(left.getXsTypeDefinition());
			  java.lang.String typeName = xsSimpleTypeDecl.getTypeName();
			  java.lang.String typeNs = xsSimpleTypeDecl.getTypeNamespace();
			  if ((typeName != null) && !((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs))) {
				  xsSimpleTypeDecl = (XSSimpleTypeDecl)(xsSimpleTypeDecl.getBaseType());
				  typeName1 = xsSimpleTypeDecl.getTypeName();
				  typeNs1 = xsSimpleTypeDecl.getTypeNamespace();

				  lNodeStr = (left.object()).toString();
			  }
		  }
	  }
	  
	  if ((left instanceof XSAnyURI) && ((right instanceof XSString) || (right instanceof XString))) {
		  java.lang.String str1 = ((XSAnyURI)left).stringValue(); 

		  left = new XSString(str1);
		  
		  typeName1 = "string";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  
	  java.lang.String typeName2 = null;
	  java.lang.String typeNs2 = null;
	  
	  if (right instanceof XMLNodeCursorImpl) {
		  right = right.getFresh();
		  
		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)right;
		  int nodeHandle = (xmlNodeCursorImpl.iterRaw()).nextNode();
		  if (nodeHandle != DTM.NULL) {
			  DTM dtm = xctxt.getDTM(nodeHandle);
			  Node node = dtm.getNode(nodeHandle);
			  if (node instanceof ElementPSVI) {
				  ElementPSVI elementPsvi = (ElementPSVI)node;
				  XSTypeDefinition typeDefn = elementPsvi.getTypeDefinition();
				  if (typeDefn instanceof XSComplexTypeDefinition) {
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' operand, cannot be a "
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
					  
					  // To do, xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_LIST
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
				  
				  // To do, xsSimpleTypeVariety == XSSimpleTypeDecl.VARIETY_LIST
			  }
			  else {
				  typeName2 = "string";
				  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			  }

			  XMLString xmlStr2 = dtm.getStringValue(nodeHandle);
			  rNodeStr = xmlStr2.toString();
		  }
	  }
	  else if (right instanceof XSAnyAtomicType) {
		  XSAnyAtomicType xsAnyAtomicType = (XSAnyAtomicType)right;
		  typeName2 = xsAnyAtomicType.stringType();

		  int colonIdx = typeName2.indexOf(':');
		  typeName2 = typeName2.substring(colonIdx + 1);
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  else if (right instanceof XNumber) {
		  double dbl = ((XNumber)right).num();
		  right = new XSDouble(dbl);
		  
		  typeName2 = "double";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  else if ((right instanceof XBoolean) || (right instanceof XBooleanStatic)) {
		  right = new XSBoolean(right.bool());
		  
		  typeName2 = "boolean";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  else if (right instanceof XString) {
		  right = new XSString(((XString)right).str());
		  
		  typeName2 = "string";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  
	  if (right instanceof XSString) {
		 java.lang.String str1 = ((XSString)right).stringValue(); 
		 if ((Constants.XS_VALID_TRUE).equals(str1) && (right.getXsTypeDefinition() != null)) {
			XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(right.getXsTypeDefinition());
			java.lang.String typeName = xsSimpleTypeDecl.getTypeName();
			java.lang.String typeNs = xsSimpleTypeDecl.getTypeNamespace();
			if ((typeName != null) && !((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs))) {
			   xsSimpleTypeDecl = (XSSimpleTypeDecl)(xsSimpleTypeDecl.getBaseType());
			   typeName2 = xsSimpleTypeDecl.getTypeName();
			   typeNs2 = xsSimpleTypeDecl.getTypeNamespace();
			   
			   rNodeStr = (right.object()).toString();
			}
		 }
	  }
	  
	  if ((right instanceof XSAnyURI) && ((left instanceof XSString) || (left instanceof XString))) {
		  java.lang.String str2 = ((XSAnyURI)right).stringValue(); 

		  right = new XSString(str2);
		  
		  typeName2 = "string";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  
	  if ((left instanceof XSAnyURI) && (right instanceof XSAnyURI)) {
		  java.lang.String str1 = ((XSAnyURI)left).stringValue(); 
		  left = new XSString(str1);
		  
		  typeName1 = "string";
		  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		  
		  java.lang.String str2 = ((XSAnyURI)right).stringValue(); 
		  right = new XSString(str2);
		  
		  typeName2 = "string";
		  typeNs2 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	  }
	  
	  // Validating an XPath 3.1 operator 'lt' operands compatibility for value comparison	  
	  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs1) && (XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs2)) {
		  if ((isXsBuiltInTypeNumeric(typeName1) && !isXsBuiltInTypeNumeric(typeName2)) || 
				                                                          (isXsBuiltInTypeNumeric(typeName2) && !isXsBuiltInTypeNumeric(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
					                                                                                                     + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("boolean".equals(typeName1) && !"boolean".equals(typeName2)) || ("boolean".equals(typeName2) && !"boolean".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("string".equals(typeName1) && !"string".equals(typeName2)) || ("string".equals(typeName2) && !"string".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("date".equals(typeName1) && !"date".equals(typeName2)) || ("date".equals(typeName2) && !"date".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("time".equals(typeName1) && !"time".equals(typeName2)) || ("time".equals(typeName2) && !"time".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("dateTime".equals(typeName1) && !"dateTime".equals(typeName2)) || ("dateTime".equals(typeName2) && !"dateTime".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("yearMonthDuration".equals(typeName1) && !"yearMonthDuration".equals(typeName2)) || ("yearMonthDuration".equals(typeName2) && !"yearMonthDuration".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("dayTimeDuration".equals(typeName1) && !"dayTimeDuration".equals(typeName2)) || ("dayTimeDuration".equals(typeName2) && !"dayTimeDuration".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }		  
		  else if (("hexBinary".equals(typeName1) && !"hexBinary".equals(typeName2)) || ("hexBinary".equals(typeName2) && !"hexBinary".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }
		  else if (("base64Binary".equals(typeName1) && !"base64Binary".equals(typeName2)) || ("base64Binary".equals(typeName2) && !"base64Binary".equals(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'lt' cannot, compare values of schema "
				 		                                                                                                 + "types " + typeName1 + " and " + typeName2 + ".");
		  }		  
	  }
	  
	  List<XMLNSDecl> nsPrefixTable = stylesheetRoot.getPrefixTable();

	  if (lNodeStr != null) {
		  java.lang.String xpathStr = (XMLConstants.W3C_XML_SCHEMA_NS_URI + ":" + typeName1 + "('" + lNodeStr + "')");
		  xpathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathStr, nsPrefixTable);
		  XPath xpathObj = null;
		  try {
		     xpathObj = new XPath(xpathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		  }
		  catch (TransformerException ex) {
			 java.lang.String errMesg = ex.getMessage();
			 if (errMesg.contains("XPST0081 : An XML namespace binding for prefix")) {
				xpathObj = new XPath("'" + lNodeStr + "'", srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);   
			 }
		  }
		  
		  left = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
	  }

	  if (rNodeStr != null) {
		  java.lang.String xpathStr = (XMLConstants.W3C_XML_SCHEMA_NS_URI + ":" + typeName2 + "('" + rNodeStr + "')");
		  xpathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathStr, nsPrefixTable);
		  XPath xpathObj = null;
		  try {
		     xpathObj = new XPath(xpathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		  }
		  catch (TransformerException ex) {
			 java.lang.String errMesg = ex.getMessage();
			 if (errMesg.contains("XPST0081 : An XML namespace binding for prefix")) {
				xpathObj = new XPath("'" + rNodeStr + "'", srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);   
			 }
		  }
		  
		  right = xpathObj.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
	  }
	  
	  if (isLEmpty || isREmpty) {
		 // An XPath 3.1 operator 'lt' has one or both of it's operands as empty sequence
		 result = new ResultSequence(); 
	  }
	  else {
		 result = left.vcLessThan(right, getExpressionOwner(), xctxt.getDefaultCollation(), true) ? XBoolean.S_TRUE : XBoolean.S_FALSE;;  
	  }
	  
      return result;
  }
}
