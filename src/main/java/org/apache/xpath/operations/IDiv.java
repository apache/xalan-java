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

import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
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
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathArithmeticOp;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.compiler.OpCodes;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * An XPath 'idiv' operation implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class IDiv extends XPathArithmeticOp
{

  private static final long serialVersionUID = 5138215729063791579L;

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
  public XObject operate(XObject left, XObject right) throws javax.xml.transform.TransformerException
  {
	  XObject result = null;
	  
	  Object lObj = left.object();
	  Object rObj = right.object();
	  
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
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of 'idiv', but "
																												  + "the supplied type is a map "
																												  + "type which cannot be atomized.", srcLocator); 
	  }

	  if (right instanceof XPathMap) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of 'idiv', but "
																												  + "the supplied type is a map "
																												  + "type which cannot be atomized.", srcLocator); 
	  }
	  
	  if (left instanceof XPathInlineFunction) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the first operand of 'idiv', but "
																												  + "the supplied type is a function "
																												  + "type which cannot be atomized.", srcLocator); 
	  }

	  if (right instanceof XPathInlineFunction) {
		  throw new javax.xml.transform.TransformerException("FOTY0013 : An xdm atomic value is required for the second operand of 'idiv', but "
																												  + "the supplied type is a function "
																												  + "type which cannot be atomized.", srcLocator); 
	  }
	  
	  java.lang.String lNodeStr = null;
	  java.lang.String rNodeStr = null;
	  
	  java.lang.String typeName1 = null;
	  java.lang.String typeNs1 = null;
	  
	  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();
	  
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
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'idiv' operand, cannot be a "
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
					  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'idiv' operand, cannot be a "
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

			  XMLString xmlStr2 = dtm.getStringValue(nodeHandle);
			  rNodeStr = xmlStr2.toString();
		  }
	  }
	  
	  if (left instanceof XSNumericType) {
		  if ((right instanceof XSString) || (right instanceof XString)) {
			  java.lang.String str2 = XslTransformEvaluationHelper.getStrVal(right);
			  
			  try {
				  Integer.valueOf(str2);
				  right = new XSInteger(str2+"");

				  typeName2 = "integer";
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
				  Integer.valueOf(str1);
				  left = new XSInteger(str1+"");

				  typeName1 = "integer";
				  typeNs1 = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			  }
			  catch (NumberFormatException ex) {
				  // no op	
			  }
		  }
	  }
	  
	  // Validating an XPath 3.1 operator 'idiv' operands compatibility for integer division	  
	  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs1) && (XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(typeNs2)) {
		  if ((isXsBuiltInTypeNumeric(typeName1) && !isXsBuiltInTypeNumeric(typeName2)) || 
				                                                                   (isXsBuiltInTypeNumeric(typeName2) && !isXsBuiltInTypeNumeric(typeName1))) {
			  throw new javax.xml.transform.TransformerException("FOTY0013 : An XPath 3.1 operator 'idiv' cannot apply values of schema "
					                                                                                                        + "types " + typeName1 + " and " + typeName2 + ".");
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
	  }

	  if ((lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String xpathInlineFuncExprStr = "function($arg0, $arg1) { $arg0 idiv $arg1 }";
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  result = xpathObj.execute(xctxt, DTM.NULL, null);

		  return result;
	  }
	  else if ((lObj instanceof FuncArgPlaceholder) && !(rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String rStr = XslTransformEvaluationHelper.getStrVal(right);
		  java.lang.String xpathInlineFuncExprStr = "function($arg0) { $arg0 idiv " + rStr + " }";
		  XPath xpathObj = new XPath(xpathInlineFuncExprStr, null, null, XPath.SELECT, null);
		  result = xpathObj.execute(xctxt, DTM.NULL, null);

		  return result;
	  }
	  else if (!(lObj instanceof FuncArgPlaceholder) && (rObj instanceof FuncArgPlaceholder)) {
		  java.lang.String lStr = XslTransformEvaluationHelper.getStrVal(left);
		  java.lang.String xpathInlineFuncExprStr = "function($arg1) { " + lStr + " idiv $arg1 }";
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
	  
	  java.lang.String arg0Str = null;
	  
	  if ((left instanceof XSInteger) || (left instanceof XSDecimal) || 
			                                                      (left instanceof XSFloat) || 
			                                                      (left instanceof XSDouble) || 
	                                                              (left instanceof XNumber) ||
	                                                              (left instanceof XSString)) {
		  arg0Str = XslTransformEvaluationHelper.getStrVal(left);		 
	  }
	  
	  java.lang.String arg1Str = XslTransformEvaluationHelper.getStrVal(right);

	  java.lang.String xpathCastAsStr = "(" + arg0Str + " div " + arg1Str + ") cast as xs:integer";

	  List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
	  Iterator<XMLNSDecl> iter = prefixTable.iterator();

	  boolean isXsNsDeclAvailable = false;  	      
	  while (iter.hasNext()) {
		  XMLNSDecl xmlNSDecl = iter.next();
		  java.lang.String uri = xmlNSDecl.getURI();
		  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(uri)) {
			  isXsNsDeclAvailable = true;

			  break;
		  }  	    	 
	  }

	  try {
		  // Add XML Schema namespace binding to Xalan-J namespace prefix table, 
		  // if this namespace binding is currently not there in prefix table.
		  if (!isXsNsDeclAvailable) {
			  prefixTable.add(new XMLNSDecl("xs", XMLConstants.W3C_XML_SCHEMA_NS_URI, false));  
		  }

		  IDivEvaluatorPrefixResolver iDivOpPrefixResolver = new IDivEvaluatorPrefixResolver(prefixTable);

		  XPath xpath = new XPath(xpathCastAsStr, srcLocator, iDivOpPrefixResolver, XPath.SELECT, null);

		  XslTransformData.m_xpathCallingOpCode = OpCodes.XPath3OpCodes.OP_IDIV;

		  // Get the result of XPath 'idiv' operator evaluation
		  result = xpath.execute(xctxt, xctxt.getCurrentNode(), xctxt.getNamespaceContext());
	  }
	  finally {
		  // Remove XML Schema namespace binding from Xalan-J namespace prefix 
		  // table, that was previously added to evaluate XPath 'idiv' operation.

		  if (!isXsNsDeclAvailable) {
			  iter = prefixTable.iterator();
			  while (iter.hasNext()) {
				  XMLNSDecl xmlNSDecl = iter.next();
				  java.lang.String uri = xmlNSDecl.getURI();
				  if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(uri)) {
					  prefixTable.remove(xmlNSDecl);

					  break;
				  }  	    	 
			  }
		  }
	  }

	  return result;
  }
  
  /**
   * An PrefixResolver class definition for XPath 'idiv' operation 
   * implementation.
   */
  class IDivEvaluatorPrefixResolver implements PrefixResolver {

	List<XMLNSDecl> prefixTable = null;
	  
	public IDivEvaluatorPrefixResolver(List<XMLNSDecl> prefixTable) {
		this.prefixTable = prefixTable;	
	}
	
	@Override
	public java.lang.String getNamespaceForPrefix(java.lang.String prefix) {
		
		java.lang.String result = null;

		Iterator<XMLNSDecl> iter = prefixTable.iterator();
		while (iter.hasNext()) {
			XMLNSDecl xmlNSDecl = iter.next();
			java.lang.String uri = xmlNSDecl.getURI();
			if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(uri)) {
				result = uri;
			}  	    	 
		}
		
		return result;
	}

	@Override
	public java.lang.String getNamespaceForPrefix(java.lang.String prefix, Node context) {
		// no op
		return null;
	}

	@Override
	public java.lang.String getBaseIdentifier() {
		// no op
		return null;
	}

	@Override
	public boolean handlesNullPrefixes() {
		// no op
		return false;
	}
	  
  }

}
