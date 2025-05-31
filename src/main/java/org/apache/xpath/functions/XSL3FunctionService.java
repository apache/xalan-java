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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.types.XSByte;
import org.apache.xpath.types.XSGDay;
import org.apache.xpath.types.XSGMonth;
import org.apache.xpath.types.XSGMonthDay;
import org.apache.xpath.types.XSGYear;
import org.apache.xpath.types.XSGYearMonth;
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
 * This class provides support to evaluate XPath 3.1's XML Schema 
 * constructor function calls and XSL stylesheet function calls. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSL3FunctionService {
	
	/**
	  * Class field declarations, specifying constant values used by this
	  * implementation.
	  * 		
	 */
	
	public static final String UTF_8 = "UTF-8";
	
	public static final String UTF_16 = "UTF-16";
	
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
    public XObject callFunction(XSL3ConstructorOrExtensionFunction xpathExpr,
    		                    TransformerImpl transformerImpl,
    		                    XPathContext xctxt) throws TransformerException {        
    	
    	XObject evalResult = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator();

    	StylesheetRoot stylesheetRoot = null;

    	try {        
    		XSL3ConstructorOrExtensionFunction funcObj = xpathExpr;
    		
    		String funcName = funcObj.getFunctionName();
    		String funcNamespace = funcObj.getNamespace();
    		
    		if (!(Constants.S_EXTENSIONS_JAVA_URL).equals(funcNamespace)) {
    			// Both XPath constructor (prefix:typeName) and XSL stylesheet function 
    			// calls (xsl:function), are syntactically similar to Xalan-J XPath 
    			// extension function calls (prefix:functionName). The implementation
    			// here need not run when an XSL stylesheet specifies Xalan-J XPath 
    			// extension function.
    			ExpressionNode expressionNode = xpathExpr.getExpressionOwner();
    			ExpressionNode stylesheetRootNode = null;
    			while (expressionNode != null) {
    				stylesheetRootNode = expressionNode;
    				expressionNode = expressionNode.exprGetParent();                     
    			}

    			stylesheetRoot = (StylesheetRoot)stylesheetRootNode;

    			if (stylesheetRoot != null) {
    				if (transformerImpl == null) {
    					transformerImpl = stylesheetRoot.getTransformerImpl();  
    				}

    				TemplateList templateList = stylesheetRoot.getTemplateListComposed();

    				int argCount = funcObj.getArgCount();
    				ElemTemplate elemTemplate = templateList.getXslFunction(new QName(funcNamespace, funcName), argCount);

    				if ((elemTemplate != null) && (elemTemplate instanceof ElemFunction)) {
    					// Evaluate XSL stylesheet function call
    					ResultSequence argSequence = new ResultSequence();
    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
    						XObject argVal = (funcObj.getArg(idx)).execute(xctxt);    					
    						argSequence.add(argVal);
    					}

    					evalResult = ((ElemFunction)elemTemplate).evaluateXslFunction(transformerImpl, argSequence);
    					
    					return evalResult;
    				}    				
    			}
    			
    			if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcNamespace)) {                
    				// Evaluate XPath constructor function call, for schema types 
    				// in XML Schema namespace.
    				
    				ResultSequence argSequence = new ResultSequence();
    				ResultSequence evalResultSequence = null;

    				try {
	    				switch (funcName) {
		    				case Keywords.XS_STRING :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSString.class, xctxt);
		    					break;
		    				case Keywords.XS_NORMALIZED_STRING :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSNormalizedString.class, xctxt);
		    					break;
		    				case Keywords.XS_TOKEN :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSToken.class, xctxt);
		    					break;
		    				case Keywords.XS_DECIMAL :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSDecimal.class, xctxt);
		    					break;
		    				case Keywords.XS_FLOAT :  					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSFloat.class, xctxt);
		    					break;
		    				case Keywords.XS_DOUBLE :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSDouble.class, xctxt);    					
		    					break;
		    				case Keywords.XS_INTEGER :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSInteger.class, xctxt);    					
		    					break;
		    				case Keywords.XS_NON_POSITIVE_INTEGER :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSNonPositiveInteger.class, xctxt);
		    					break;
		    				case Keywords.XS_NEGATIVE_INTEGER :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSNegativeInteger.class, xctxt);
		    					break;
		    				case Keywords.XS_NON_NEGATIVE_INTEGER :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSNonNegativeInteger.class, xctxt);
		    					break;
		    				case Keywords.XS_POSITIVE_INTEGER :    					 
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSPositiveInteger.class, xctxt);
		    					break;
		    				case Keywords.XS_LONG :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSLong.class, xctxt);
		    					break;
		    				case Keywords.XS_INT :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSInt.class, xctxt);
		    					break;
		    				case Keywords.XS_SHORT :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSShort.class, xctxt);
		    					break;
		    				case Keywords.XS_BYTE :
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSByte.class, xctxt);
		    					break;
		    				case Keywords.XS_UNSIGNED_LONG :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSUnsignedLong.class, xctxt);
		    					break;
		    				case Keywords.XS_UNSIGNED_INT :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSUnsignedInt.class, xctxt);
		    					break;
		    				case Keywords.XS_UNSIGNED_SHORT :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSUnsignedShort.class, xctxt);
		    					break;
		    				case Keywords.XS_UNSIGNED_BYTE :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSUnsignedByte.class, xctxt);
		    					break;
		    				case Keywords.XS_GYEAR_MONTH :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSGYearMonth.class, xctxt);
		    					break;
		    				case Keywords.XS_GYEAR :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSGYear.class, xctxt);
		    					break;
		    				case Keywords.XS_GMONTH_DAY :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSGMonthDay.class, xctxt);
		    					break;
		    				case Keywords.XS_GDAY :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSGDay.class, xctxt);
		    					break;
		    				case Keywords.XS_GMONTH :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSGMonth.class, xctxt);
		    					break;
		    				case Keywords.FUNC_BOOLEAN_STRING :    					
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						Boolean boolVal = Boolean.valueOf(("0".equals(argStr) || "false".equals(argStr)) ? 
		    								                                                               "false" : "true");
		    						argSequence.add(new XSBoolean(boolVal));
		    					}
		    					evalResultSequence = (new XSBoolean()).constructor(argSequence);
		    					evalResult = evalResultSequence.item(0);
		
		    					break;
		    				case Keywords.XS_DATE :
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);    						    						
		    						argSequence.add(XSDate.parseDate(argStr));
		    					}
		    					evalResultSequence = (new XSDate()).constructor(argSequence); 
		    					evalResult = evalResultSequence.item(0);
		
		    					break;
		    				case Keywords.XS_DATETIME :
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						argSequence.add(XSDateTime.parseDateTime(argStr));
		    					}
		    					evalResultSequence = (new XSDateTime()).constructor(argSequence); 
		    					evalResult = evalResultSequence.item(0);
		
		    					break;
		    				case Keywords.XS_DURATION :
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						XSDuration xsDuration = XSDuration.parseDuration(argStr);
		    						if (xsDuration != null) {
		    							argSequence.add(xsDuration);
		    							evalResultSequence = (new XSDuration()).constructor(argSequence); 
		    							evalResult = evalResultSequence.item(0);
		    						}
		    						else {
		    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:duration value '" + 
		    																							argStr + "' is present in the input.", srcLocator); 
		    						}
		    					}
		
		    					break;
		    				case Keywords.XS_YEAR_MONTH_DURATION :                   
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						XSDuration xsDuration = XSYearMonthDuration.parseYearMonthDuration(argStr);
		    						if (xsDuration != null) {
		    							argSequence.add(xsDuration);
		    							evalResultSequence = (new XSYearMonthDuration()).constructor(argSequence); 
		    							evalResult = evalResultSequence.item(0);
		    						}
		    						else {
		    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:yearMonthDuration value '" + 
		    																							argStr + "' is present in the input.", srcLocator); 
		    						}
		    					}
		
		    					break;
		    				case Keywords.XS_DAY_TIME_DURATION :                 
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						XSDuration xsDuration = XSDayTimeDuration.parseDayTimeDuration(argStr);
		    						if (xsDuration != null) {
		    							argSequence.add(xsDuration);
		    							evalResultSequence = (new XSDayTimeDuration()).constructor(argSequence); 
		    							evalResult = evalResultSequence.item(0);
		    						}
		    						else {
		    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:dayTimeDuration value '" + 
		    																							argStr + "' is present in the input.", srcLocator); 
		    						}                            
		    					}
		
		    					break;
		    				case Keywords.XS_TIME :                 
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						XSTime xsTime = XSTime.parseTime(argStr);
		    						if (xsTime != null) {
		    							argSequence.add(xsTime);
		    							evalResultSequence = (new XSTime()).constructor(argSequence); 
		    							evalResult = evalResultSequence.item(0);
		    						}
		    						else {
		    							throw new TransformerException("FORG0001 : An incorrectly formatted xs:time value '" + 
		    																							argStr + "' is present in the input.", srcLocator); 
		    						}                            
		    					}
		
		    					break;
		    				case Keywords.XS_ANY_URI :    					
		    					evalResult = evaluateXPathBuiltInConstructorFunctionCall(funcObj, XSAnyURI.class, xctxt);
		    					break;
		    				case Keywords.XS_QNAME :
		    					XSQName xsQName = new XSQName();
		    					for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
		    						Expression funcArg = funcObj.getArg(idx);    						
		    						String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
		    						if (idx == 0) {
		    							xsQName.setPrefix(argStr);	
		    						}
		    						else if (idx == 1) {
		    							xsQName.setLocalPart(argStr);
		    						}
		    						else {
		    							xsQName.setNamespaceUri(argStr);
		    						}
		    					}
		
		    					evalResult = xsQName;
		
		    					break;
		    				default:
		    					// NO OP
	    				}
    			    }
    			    catch (Exception ex) {
    			       String exceptionMesgStr = null;
    			       if (ex instanceof InvocationTargetException) {
    			    	  Throwable throwable = ((InvocationTargetException)ex).getTargetException();
    			    	  exceptionMesgStr = throwable.getMessage();
    			       }
    			       else {
    			          exceptionMesgStr = (ex.getMessage() != null) ? ex.getMessage() : "";
    			       }
    			       
    			       exceptionMesgStr = exceptionMesgStr.equals("") ? "" : (exceptionMesgStr + ".");
    			       if (exceptionMesgStr.endsWith("..")) {
    			    	   exceptionMesgStr = exceptionMesgStr.substring(0, exceptionMesgStr.length() - 1); 
    			       }
    			       
    			       String errMesgStrTrailingSuffix = ((exceptionMesgStr != null) && (exceptionMesgStr.length() > 0)) ? " "+ exceptionMesgStr : ""; 
    			       
    			       throw new TransformerException("FODC0005 : A dynamic error has occured, evaluating XPath built-in "
    			       		                                                             + "constructor function call for xs:" + funcName + "." + 
    			    		                                                                errMesgStrTrailingSuffix, srcLocator);
    			    }
    			}
    			else {
    				// We check and evaluate below, XPath constructor function calls to 
    				// XML Schema user-defined simple types.

    				Node elemTemplateElem = stylesheetRoot.getFirstChildElem();

    				while (elemTemplateElem != null && !(Constants.ELEMNAME_IMPORT_SCHEMA_STRING).equals(elemTemplateElem.getLocalName())) {   
    					elemTemplateElem = elemTemplateElem.getNextSibling();
    				}

    				if (elemTemplateElem != null) {
    					NodeList nodeList = elemTemplateElem.getChildNodes();
    					Node xsSchemaTopMostNode = nodeList.item(0);    					

    					if (xsSchemaTopMostNode != null) {    						
    						// xsl:import-schema instruction's child contents, has 
    						// XML Schema document available as string value.

    						String xmlSchemaDocumentStr = null;

    						XSModel xsModel = null;        		   
    						XSLoaderImpl xsLoader = new XSLoaderImpl();

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
    									String argStrVal = XslTransformEvaluationHelper.getStrVal(xsSimpleTypeInpObj);
										xsSimpleTypeDecl.validate(argStrVal, null, null);
										evalResult = new XSString(XS_VALID_TRUE);
										evalResult.setObject(argStrVal);
    									evalResult.setXsTypeDefinition(xsTypeDefinition);    								}
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

    						XSModel xsModel = null;        		   
    						XSLoaderImpl xsLoader = new XSLoaderImpl();

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
    										String argStrVal = XslTransformEvaluationHelper.getStrVal(xsSimpleTypeInpObj);
    										xsSimpleTypeDecl.validate(argStrVal, null, null);
    										evalResult = new XSString(XS_VALID_TRUE);
    										evalResult.setObject(argStrVal);
    										evalResult.setXsTypeDefinition(xsTypeDefinition);
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
    
    /**
     * Method definition to syntactically check, well-formedness of function arity of 
     * the function to be called, for named function references specified with syntax 
     * functionNameString#integerLiteral.
     */
    public boolean isFuncArityWellFormed(String funcRefStr) {	      	
    	boolean isFuncArityWellFormed = true;

    	int idx = funcRefStr.indexOf('#');
    	String intStr = funcRefStr.substring(idx + 1);
    	Integer intVal = null;
    	try {
    		intVal = Integer.valueOf(intStr);
    		if (intVal < 0) {
    			isFuncArityWellFormed = false;
    		}
    	}
    	catch (NumberFormatException ex) {
    		isFuncArityWellFormed = false; 
    	}

    	return isFuncArityWellFormed;
    }
    
    /**
     * Evaluate the XPath built-in constructor function call.
     */
    private XObject evaluateXPathBuiltInConstructorFunctionCall(XSL3ConstructorOrExtensionFunction funcObj, 
    		                                                     Class dataType, XPathContext xctxt) throws TransformerException, 
                                                                                                 InstantiationException, IllegalAccessException, 
                                                                                                 NoSuchMethodException, SecurityException, 
                                                                                                 IllegalArgumentException, InvocationTargetException {
    	XObject evalResult = null;
    	
    	ResultSequence evalResultSequence = null;
    	ResultSequence argSequence = new ResultSequence();
    	
    	for (int idx = 0; idx < funcObj.getArgCount(); idx++) {
			Expression funcArg = funcObj.getArg(idx);    						
			String argStr = getXPathBuiltInConstructorFunctionArgStr(funcArg, xctxt);
			Constructor cons = dataType.getConstructor(new Class[] {String.class});
			Object obj = cons.newInstance(new String[] {argStr});
			argSequence.add((XObject)obj);
		}
    	
    	Object obj = dataType.newInstance();
    	
    	Method method = dataType.getMethod("constructor", new Class[] {ResultSequence.class});
    	evalResultSequence = (ResultSequence)(method.invoke(obj, new Object[] {argSequence}));

		evalResult = evalResultSequence.item(0);
    	
    	return evalResult;
    }

    /**
     * Get the effective value of XPath built-in constructor function call's argument.
     */
    private String getXPathBuiltInConstructorFunctionArgStr(Expression funcArg, XPathContext xctxt) 
    		                                                                              throws TransformerException {
    	String argStr = null;
    	
    	if (funcArg instanceof SelfIteratorNoPredicate) {
    		XObject contextItem = xctxt.getXPath3ContextItem();
    		if (contextItem != null) {
    		   argStr = XslTransformEvaluationHelper.getStrVal(contextItem);
    		}
    		else {
    		   XObject argVal = funcArg.execute(xctxt);
        	   argStr = XslTransformEvaluationHelper.getStrVal(argVal);
    		}
    	}
    	else {
    		XObject argVal = funcArg.execute(xctxt);
    		argStr = XslTransformEvaluationHelper.getStrVal(argVal); 
    	}
    	
    	return argStr;
    }
    
}
