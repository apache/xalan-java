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
package org.apache.xpath.functions;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSLoaderImpl;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.utils.QName;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSNegativeInteger;
import org.apache.xpath.types.XSNonNegativeInteger;
import org.apache.xpath.types.XSNonPositiveInteger;
import org.apache.xpath.types.XSPositiveInteger;
import org.apache.xpath.types.XSShort;
import org.apache.xpath.types.XSUnsignedByte;
import org.apache.xpath.types.XSUnsignedInt;
import org.apache.xpath.types.XSUnsignedLong;
import org.apache.xpath.types.XSUnsignedShort;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

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
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * An object of this class supports evaluation of an XPath 
 * constructor function call, or an XSL stylesheet function 
 * call.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSLFunctionService {
	
	// Class fields, specifying constant values used by this 
	// implementation.				
	
	public static final String UTF_16 = "UTF-16";
	
	public static final String UTF_8 = "UTF-8";
	
	public static final String XML_DOM_FORMAT_PRETTY_PRINT = "format-pretty-print";
	
	public static final String XS_VALID_TRUE = "XS_VALID_TRUE";
    
	/**
	 * Process a call to an XPath constructor function, or an XSL 
	 * stylesheet function.
	 * 
	 * @param transformerImpl             xsl transformation implementation object
	 * @param xpathExpr                   xpath expression object
	 * @param xctxt                       xpath context object
	 * @return                            evaluation result of an XPath function call 
	 *                                    handled by this method.
	 * @throws TransformerException
	 */
    public XObject callFunction(XSLConstructorStylesheetOrExtensionFunction xpathExpr,
    		                    TransformerImpl transformerImpl,
    		                    XPathContext xctxt) throws TransformerException {        
    	
    	XObject evalResult = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator();

    	StylesheetRoot stylesheetRoot = null;

    	try {        
    		XSLConstructorStylesheetOrExtensionFunction funcObj = xpathExpr;
    		
    		String funcName = funcObj.getFunctionName();
    		String funcNamespace = funcObj.getNamespace();
    		
    		if (!(Constants.S_EXTENSIONS_JAVA_URL).equals(funcNamespace)) {
    			// Both XPath constructor (prefix:typeName) and XSL stylesheet function 
    			// calls (xsl:function), are syntactically similar to Xalan-J XPath 
    			// extension function calls (prefix:functionName). All the code within 
    			// this code-block, need not run, when an XSL stylesheet specifies an 
    			// Xalan-J XPath extension function.
    			ExpressionNode expressionNode = xpathExpr.getExpressionOwner();
    			ExpressionNode stylesheetRootNode = null;
    			while (expressionNode != null) {
    				stylesheetRootNode = expressionNode;
    				expressionNode = expressionNode.exprGetParent();                     
    			}

    			stylesheetRoot = (StylesheetRoot)stylesheetRootNode;

    			if (transformerImpl == null) {
    				transformerImpl = stylesheetRoot.getTransformerImpl();  
    			}

    			TemplateList templateList = stylesheetRoot.getTemplateListComposed();

    			ElemTemplate elemTemplate = templateList.getTemplate(new QName(funcNamespace, funcName));

    			if ((elemTemplate != null) && (elemTemplate instanceof ElemFunction)) {
    				// Evaluate XSL stylesheet function call
    				ResultSequence argSequence = new ResultSequence();
    				for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    					XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    					argSequence.add(argVal);
    				}

    				evalResult = ((ElemFunction)elemTemplate).evaluateXslFunction(transformerImpl, argSequence);
    			}            
    			else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcNamespace)) {                
    				// Evaluate XPath constructor function call, for schema types 
    				// in XML Schema namespace.
    				ResultSequence argSequence = new ResultSequence();
    				ResultSequence evalResultSequence = null;

    				switch (funcName) {
    				case Keywords.XS_STRING :                        
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSString(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSString()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_NORMALIZED_STRING :                        
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSNormalizedString(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSNormalizedString()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_TOKEN :                        
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSToken(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSToken()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_DECIMAL :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSDecimal(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSDecimal()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_FLOAT :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSFloat(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSFloat()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;                
    				case Keywords.XS_DOUBLE :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSDouble(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSDouble()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;                
    				case Keywords.XS_INTEGER :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSInteger()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0); 

    					break;
    				case Keywords.XS_NON_POSITIVE_INTEGER :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSNonPositiveInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSNonPositiveInteger()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0); 

    					break;
    				case Keywords.XS_NEGATIVE_INTEGER :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSNegativeInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSNegativeInteger()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0); 

    					break;
    				case Keywords.XS_NON_NEGATIVE_INTEGER :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSNonNegativeInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSNonNegativeInteger()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0); 

    					break;
    				case Keywords.XS_POSITIVE_INTEGER :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSPositiveInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSPositiveInteger()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0); 

    					break;
    				case Keywords.XS_LONG :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSLong(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSLong()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_INT :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSInt(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSInt()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_SHORT :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSShort(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSShort()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_BYTE :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSByte(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSByte()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_UNSIGNED_LONG :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSUnsignedLong(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSUnsignedLong()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_UNSIGNED_INT :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSUnsignedInt(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSUnsignedInt()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_UNSIGNED_SHORT :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSUnsignedShort(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSUnsignedShort()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_UNSIGNED_BYTE :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSUnsignedByte(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSUnsignedByte()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.FUNC_BOOLEAN_STRING :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						String argStrVal = XslTransformEvaluationHelper.getStrVal(argVal);
    						Boolean boolVal = Boolean.valueOf(("0".equals(argStrVal) || "false".equals(argStrVal)) ? 
    								"false" : "true");
    						argSequence.add(new XSBoolean(boolVal));
    					}

    					evalResultSequence = (new XSBoolean()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_DATE :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(XSDate.parseDate(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSDate()).constructor(argSequence); 
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_DATETIME :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(XSDateTime.parseDateTime(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSDateTime()).constructor(argSequence); 
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_DURATION :
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
    						XSDuration xsDuration = XSDuration.parseDuration(strVal);
    						if (xsDuration != null) {
    							argSequence.add(xsDuration);
    							evalResultSequence = (new XSDuration()).constructor(argSequence); 
    							evalResult = evalResultSequence.item(0);
    						}
    						else {
    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:duration value '" + 
    									                                            strVal + "' is present in the input.", srcLocator); 
    						}
    					}

    					break;
    				case Keywords.XS_YEAR_MONTH_DURATION :                   
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
    						XSDuration xsDuration = XSYearMonthDuration.parseYearMonthDuration(strVal);
    						if (xsDuration != null) {
    							argSequence.add(xsDuration);
    							evalResultSequence = (new XSYearMonthDuration()).constructor(argSequence); 
    							evalResult = evalResultSequence.item(0);
    						}
    						else {
    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:yearMonthDuration value '" + 
    									                                            strVal + "' is present in the input.", srcLocator); 
    						}
    					}

    					break;
    				case Keywords.XS_DAY_TIME_DURATION :                 
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
    						XSDuration xsDuration = XSDayTimeDuration.parseDayTimeDuration(strVal);
    						if (xsDuration != null) {
    							argSequence.add(xsDuration);
    							evalResultSequence = (new XSDayTimeDuration()).constructor(argSequence); 
    							evalResult = evalResultSequence.item(0);
    						}
    						else {
    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:dayTimeDuration value '" + 
    									                                             strVal + "' is present in the input.", srcLocator); 
    						}                            
    					}

    					break;
    				case Keywords.XS_TIME :                 
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
    						XSTime xsTime = XSTime.parseTime(strVal);
    						if (xsTime != null) {
    							argSequence.add(xsTime);
    							evalResultSequence = (new XSTime()).constructor(argSequence); 
    							evalResult = evalResultSequence.item(0);
    						}
    						else {
    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:time value '" + 
    									                                             strVal + "' is present in the input.", srcLocator); 
    						}                            
    					}

    					break;
    				case Keywords.XS_ANY_URI :                        
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						argSequence.add(new XSAnyURI(XslTransformEvaluationHelper.getStrVal(argVal)));
    					}

    					evalResultSequence = (new XSAnyURI()).constructor(argSequence);
    					evalResult = evalResultSequence.item(0);

    					break;
    				case Keywords.XS_QNAME :
    					XSQName xsQName = new XSQName();
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);
    						String argStrVal = XslTransformEvaluationHelper.getStrVal(argVal);
    						if (idx == 0) {
    							xsQName.setPrefix(argStrVal);	
    						}
    						else if (idx == 1) {
    							xsQName.setLocalPart(argStrVal);
    						}
    						else {
    							xsQName.setNamespaceUri(argStrVal);
    						}
    					}

    					evalResult = xsQName;

    					break;
    				default:
    					// no op
    				}
    			}
    			else {
    				// We check and evaluate below, XPath constructor function calls to 
    				// XML Schema user-defined simple types.
    				HashMap stylesheetAvailableElems = stylesheetRoot.getAvailableElements();

    				if (stylesheetAvailableElems.containsKey(new QName(Constants.S_XSLNAMESPACEURL, 
    						                                           Constants.ELEMNAME_IMPORT_SCHEMA_STRING))) {
    					Node elemTemplateElem = stylesheetRoot.getFirstChildElem();

    					while (elemTemplateElem != null && !(Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {   
    						elemTemplateElem = elemTemplateElem.getNextSibling();
    					}

    					NodeList nodeList = elemTemplateElem.getChildNodes();
    					Node xsSchemaTopMostNode = nodeList.item(0);

    					XSModel xsModel = null;        		   
    					XSLoaderImpl xsLoader = new XSLoaderImpl();

    					if (xsSchemaTopMostNode != null) {
    						// xsl:import-schema instruction's child contents, has 
    						// XML Schema document available as string.
    						String xmlSchemaDocumentStr = null;

    						try {
    							DOMImplementationLS domImplLS = (DOMImplementationLS)((DOMImplementationRegistry.newInstance()).getDOMImplementation("LS"));
    							LSSerializer lsSerializer = domImplLS.createLSSerializer();
    							DOMConfiguration domConfig = lsSerializer.getDomConfig();
    							domConfig.setParameter(XML_DOM_FORMAT_PRETTY_PRINT, Boolean.TRUE);
    							xmlSchemaDocumentStr = lsSerializer.writeToString((Document)xsSchemaTopMostNode);
    							xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst(UTF_16, UTF_8);
    							xmlSchemaDocumentStr = xmlSchemaDocumentStr.replaceFirst("schema", "schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");

    							DOMInputImpl lsInput = new DOMInputImpl();
    							lsInput.setCharacterStream(new StringReader(xmlSchemaDocumentStr));

    							xsModel = xsLoader.load(lsInput);    						

    							if (xsModel != null) {
    								XSTypeDefinition xsTypeDefinition = xsModel.getTypeDefinition(funcName, funcNamespace);
    								if (xsTypeDefinition != null) {
    									XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)xsTypeDefinition;
    									XObject xsSimpleTypeInpObj = (funcObj.getArg(0)).execute(xctxt);
    									xsSimpleTypeDecl.validate(XslTransformEvaluationHelper.getStrVal(xsSimpleTypeInpObj), null, null);
    									evalResult = new XSString(XS_VALID_TRUE);
    								}
    								else {
    									throw new javax.xml.transform.TransformerException("FODC0005 : There's no in-scope schema type definition available "
    											                                                       + "with name {" + funcNamespace + "}:" + funcName + ".", srcLocator);							
    								}	    							    						
    							}
    							else {
    								throw new javax.xml.transform.TransformerException("FODC0005 : While processing xsl:import-schema instruction, a compiled "
    										                                                           + "representation of an XML Schema document could not be built.", srcLocator);
    							}
    						}
    						catch (InvalidDatatypeValueException ex) {
    							throw new TransformerException(ex.getMessage(), srcLocator); 
    						}
    						catch (Exception ex) {
    							throw new TransformerException(ex.getMessage(), srcLocator);
    						} 
    					}
    					else {
    						// Use an XML Schema document available, at the uri specified by xsl:import-schema 
    						// element's attribute 'schema-location'.
    						NamedNodeMap importSchemaNodeAttributes = ((Element)elemTemplateElem).getAttributes();        			   

    						if (importSchemaNodeAttributes != null) {
    							Node attrNode1 = importSchemaNodeAttributes.item(0);
    							Node attrNode2 = importSchemaNodeAttributes.item(1);	        			   	        			   	        			   

    							try {
    								if (attrNode1 != null) {
    									URI inpUri = new URI(attrNode1.getNodeValue());
    									String stylesheetSystemId = srcLocator.getSystemId();

    									if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
    										URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
    										URL url = resolvedUri.toURL(); 
    										if (!"namespace".equals(attrNode1.getNodeName())) {
    											xsModel = xsLoader.loadURI(url.toString());
    										}
    									}
    								}

    								if (attrNode2 != null && xsModel == null) {
    									URI inpUri = new URI(attrNode2.getNodeValue());
    									String stylesheetSystemId = srcLocator.getSystemId();

    									if (!inpUri.isAbsolute() && (stylesheetSystemId != null)) {
    										URI resolvedUri = (new URI(stylesheetSystemId)).resolve(inpUri);
    										URL url = resolvedUri.toURL();
    										if ("schema-location".equals(attrNode2.getNodeName())) {
    											xsModel = xsLoader.loadURI(url.toString());
    										}
    									}	        				  
    								}

    								if (xsModel != null) {
    									XSTypeDefinition xsTypeDefinition = xsModel.getTypeDefinition(funcName, funcNamespace);
    									if (xsTypeDefinition != null) {
    										XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)xsTypeDefinition;
    										XObject xsSimpleTypeInpObj = (funcObj.getArg(0)).execute(xctxt);
    										xsSimpleTypeDecl.validate(XslTransformEvaluationHelper.getStrVal(xsSimpleTypeInpObj), null, null);
    										evalResult = new XSString(XS_VALID_TRUE);
    									}
    									else {
    										throw new javax.xml.transform.TransformerException("FODC0005 : There's no in-scope schema type definition available "
    												                                                 + "with name {" + funcNamespace + "}:" + funcName + ".", srcLocator);							
    									}
    								}
    								else {
    									throw new javax.xml.transform.TransformerException("FODC0005 : While processing xsl:import-schema instruction, a compiled "
    											                                                     + "representation of an XML Schema document could not be built.", srcLocator);
    								}
    							}
    							catch (URISyntaxException ex) {
    								throw new javax.xml.transform.TransformerException("FODC0005 : The schema uri specified with xsl:import-schema instruction "
    										                                                         + "is not a valid absolute uri, or cannot be resolved to an absolute uri.", srcLocator);   
    							}
    							catch (MalformedURLException ex) {
    								throw new javax.xml.transform.TransformerException("FODC0005 : The schema uri specified with xsl:import-schema instruction "
    										                                                         + "is not a valid absolute uri, or cannot be resolved to an absolute uri.", srcLocator); 
    							}
    							catch (InvalidDatatypeValueException ex) {
    								throw new TransformerException(ex.getMessage(), srcLocator);
    							}
    						}	            				 
    					}	            			
    				}
    			}
    		}
    	}
    	catch (TransformerException ex) {
    		throw new TransformerException(ex.getMessage(), srcLocator); 
    	}

    	return evalResult;        
    }
    
}
