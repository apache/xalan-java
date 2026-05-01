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
package org.apache.xpath.functions;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.FuncFormatNumber;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.patterns.NodeTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XPath 3.1 function fn:serialize.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSerialize extends FunctionMultiArgs {

	private static final long serialVersionUID = -2074055550828065958L;
	
	/**
	 * Class fields, defining few constant values used by this class
	 * implementation.
	 */
	
	// The number of fn:serialize parameters supported by this implementation.
	// These are : method, indent, encoding, omit-xml-declaration, item-separator
	private static final int SER_PARAM_COUNT_SUPPORTED = 5;
	
	private static final String UTF_8 = "UTF-8";
	
	// This fn:format-number picture string is specified by,
	// 'XSLT and XQuery Serialization 3.1' specification.
	private static final String FORMAT_NUMBER_PIC_STRING = "0.0##########################e0";
	

	/**
	 * Class constructor.
	 */
	public FuncSerialize() {
		m_defined_arity = new Short[] {1, 2}; 
	}
	
	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt						An XPath context object
	 * @return 							A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();				
		
		if (m_arg2 != null) {
		   throw new javax.xml.transform.TransformerException("XPST0017 : An XPath 3.1 function 'serialize' can only accept "
		   		                                                                                                          + "one or two arguments.", srcLocator);
		}
		
		ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();
		
		StylesheetRoot stylesheetRoot = XslTransformEvaluationHelper.getXslStylesheetRootFromXslElementRef(elemTemplateElement);
		
		TransformerImpl transformer = stylesheetRoot.getTransformerImpl();
		
		if (m_arg0 != null) {
			XObject arg0XObj = m_arg0.execute(xctxt);
			
			String itemSeparatorStr = null;
			
			String encodingStr = null;

			if (m_arg1 == null) {
				// An XPath 3.1 function call fn:serialize is called with only one argument
				
				if (arg0XObj instanceof XPathMap) {
				   throw new TransformerException("SENR0001 : A XPath expression evaluation dynamic error occured during function "
							   		                                                                          + "invocation 'serialize'. An xdm map is "
							   		                                                                          + "attempted to be serialized with method 'xml'.", srcLocator);
				}
				
				ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);

				StringWriter strWriter = new StringWriter();
				StreamResult streamResult = new StreamResult(strWriter);
				
				encodingStr = UTF_8;

				SerializationHandler rhandler = getXslSerializationHandler(Method.XML, Constants.ATTRVAL_LITERAL_NO, encodingStr, Constants.ATTRVAL_LITERAL_NO, 
						 																														transformer, streamResult); 			  
				
				result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);			  
			}
			else {
				// An XPath 3.1 function call fn:serialize is called with two arguments
				
				XObject arg1Obj = m_arg1.execute(xctxt);
				
				if (arg1Obj instanceof XPathMap) {
					// XPath 3.1 function fn:serialization parameters are specified using an 
					// xdm map.
					
					XPathMap xpathMap = (XPathMap)arg1Obj;
					
					XObject xObjItemSeparator = xpathMap.get(new XSString(Constants.ATTRNAME_ITEM_SEPARATOR));
					if (xObjItemSeparator != null) {
					   itemSeparatorStr = XslTransformEvaluationHelper.getStrVal(xObjItemSeparator); 
					}

					XObject xMethodObj1 = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_METHOD));
					String methodName = null;				 

					XObject xObj1 = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_INDENT));
					String indentStrValue = null;
					if (xObj1 != null) {
						if ((xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic)) {
							indentStrValue = (xObj1.bool()) ? Constants.ATTRVAL_LITERAL_YES : Constants.ATTRVAL_LITERAL_NO;
						}
						else if ((xObj1 instanceof XSString) || (xObj1 instanceof XString)) {
							String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
							if ((Constants.ATTRVAL_LITERAL_YES.equals(str1)) || (Constants.ATTRVAL_LITERAL_NO.equals(str1))) {
							   indentStrValue = str1; 
							}
							else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
										                                                                                            + "'indent' with value " + str1 + " which is not "
										                                                                                            + "a valid representation of parameter 'indent'.", srcLocator);
							}
						}
						else if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
							String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
							Double dbl = Double.valueOf(str1);
							if (dbl.doubleValue() == 1) {
							   indentStrValue = Constants.ATTRVAL_LITERAL_YES; 
							}
							else if (dbl.doubleValue() == 0) {
							   indentStrValue = Constants.ATTRVAL_LITERAL_NO;	
							}
							else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																							                                        + "'indent' with value " + str1 + " which is not "
																							                                        + "a valid representation of parameter 'indent'.", srcLocator);
							}
						}
						else {
							throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																																	+ "'indent' not having correct value.", srcLocator);
						}
					}
					else {
						indentStrValue = Constants.ATTRVAL_LITERAL_NO; 
					}					

					XObject xObj2 = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_OMITXMLDECL));
					String omitXmlDeclStrValue = null;
					if (xObj2 != null) {
						if ((xObj2 instanceof XSBoolean) || (xObj2 instanceof XBoolean) || (xObj2 instanceof XBooleanStatic)) {
							indentStrValue = (xObj2.bool()) ? Constants.ATTRVAL_LITERAL_YES : Constants.ATTRVAL_LITERAL_NO;
						}
						else if ((xObj2 instanceof XSString) || (xObj2 instanceof XString)) {
							String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);
							if ((Constants.ATTRVAL_LITERAL_YES.equals(str1)) || (Constants.ATTRVAL_LITERAL_NO.equals(str1))) {
							   indentStrValue = str1; 
							}
							else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
										                                                                                            + "'omit-xml-declaration' with value " + str1 + " which is not "
										                                                                                            + "a valid representation of parameter 'omit-xml-declaration'.", srcLocator);
							}
						}
						else if ((xObj2 instanceof XSNumericType) || (xObj2 instanceof XNumber)) {
							String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);
							Double dbl = Double.valueOf(str1);
							if (dbl.doubleValue() == 1) {
							   indentStrValue = Constants.ATTRVAL_LITERAL_YES; 
							}
							else if (dbl.doubleValue() == 0) {
							   indentStrValue = Constants.ATTRVAL_LITERAL_NO;	
							}
							else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																							                                        + "'omit-xml-declaration' with value " + str1 + " which is not "
																							                                        + "a valid representation of parameter 'omit-xml-declaration'.", srcLocator);
							}
						}
						else {
							throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																																	+ "'omit-xml-declaration' not having correct value.", srcLocator);
						}
					}
					else {
						omitXmlDeclStrValue = Constants.ATTRVAL_LITERAL_YES; 
					}
					
					XObject xObj3 = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_ENCODING));
					if (xObj3 != null) {
					   encodingStr = XslTransformEvaluationHelper.getStrVal(xObj3);
					}
					
					if (xMethodObj1 != null) {
						methodName = XslTransformEvaluationHelper.getStrVal(xMethodObj1);
						
						result = getFnSerializeResult(arg0XObj, m_arg0, methodName, indentStrValue, encodingStr, omitXmlDeclStrValue, 
								                                                               xctxt, srcLocator, transformer, itemSeparatorStr);
					}
					else {
						// The default XPath 3.1 function fn:serialize method is "xml"
						
						if (arg0XObj instanceof XPathMap) {
							throw new TransformerException("SENR0001 : A XPath expression evaluation dynamic error occured during function "
																	                                        + "invocation 'serialize'. An xdm map is "
																	                                        + "attempted to be serialized with method 'xml'.", srcLocator);
					    }
						
						ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);

						StringWriter strWriter = new StringWriter();
						StreamResult streamResult = new StreamResult(strWriter);

						SerializationHandler rhandler = getXslSerializationHandler(Method.XML, indentStrValue, encodingStr, omitXmlDeclStrValue, transformer, streamResult);

						result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
					}
				}
				else if (arg1Obj instanceof XMLNodeCursorImpl) {
					// XPath 3.1 function fn:serialization parameters are specified using an 
					// element {http://www.w3.org/2010/xslt-xquery-serialization}serialization-parameters. 
					
					XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg1Obj;
					int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
					DTM dtm = xctxt.getDTM(nodeHandle);				 
					short nodeType = dtm.getNodeType(nodeHandle);
					String nodeLocalName = dtm.getLocalName(nodeHandle);
					String nsUri = dtm.getNamespaceURI(nodeHandle);
					if ((nodeType == DTM.ELEMENT_NODE) && (Constants.XSL_SER_NAMESACE).equals(nsUri) && (Constants.XSL_SER_PARAMS).equals(nodeLocalName)) {
					   List<String> serParamNameLstDefn = new ArrayList<String>();	
					   serParamNameLstDefn.add(OutputKeys.METHOD);
					   serParamNameLstDefn.add(OutputKeys.INDENT);
					   serParamNameLstDefn.add(OutputKeys.OMIT_XML_DECLARATION);
					   serParamNameLstDefn.add(Constants.ATTRNAME_ITEM_SEPARATOR);
					   
					   List<String> serParamNameLstInstance = new ArrayList<String>();
					   List<String> serParamValueLstInstance = new ArrayList<String>();
						
					   int child1 = dtm.getFirstChild(nodeHandle);
					   int child2 = DTM.NULL;					   
					   if (child1 != DTM.NULL) {
						   child2 = dtm.getNextSibling(child1); 
					   }
					   
					   int child3 = DTM.NULL;					   
					   if (child2 != DTM.NULL) {
						   child3 = dtm.getNextSibling(child2); 
					   }
					   
					   int child4 = DTM.NULL;					   
					   if (child3 != DTM.NULL) {
						   child4 = dtm.getNextSibling(child3); 
					   }
					   
					   int child5 = DTM.NULL;					   
					   if (child4 != DTM.NULL) {
						   child5 = dtm.getNextSibling(child4); 
					   }
					   
					   if (child1 != DTM.NULL) {
						   short nodeType1 = dtm.getNodeType(child1);
						   if (nodeType1 == DTM.ELEMENT_NODE) {
							   String nsUri1 = dtm.getNamespaceURI(child1);
							   if ((Constants.XSL_SER_NAMESACE).equals(nsUri1)) {
								   String nodeLocalName1 = dtm.getLocalName(child1);
								   serParamNameLstInstance.add(nodeLocalName1);
								   int attrNode1 = dtm.getAttributeNode(child1, null, Constants.ATTRNAME_VALUE);
								   String value1 = dtm.getNodeValue(attrNode1);
								   if (value1 != null) {
									  serParamValueLstInstance.add(value1); 
								   }
							   }
							   else {
								   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator);
							   }
						   }
						   else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator);
						   }
					   }
					   
					   if (child2 != DTM.NULL) {
						   short nodeType1 = dtm.getNodeType(child2);
						   if (nodeType1 == DTM.ELEMENT_NODE) {
							   String nsUri1 = dtm.getNamespaceURI(child2);
							   if ((Constants.XSL_SER_NAMESACE).equals(nsUri1)) {
								   String nodeLocalName1 = dtm.getLocalName(child2);
								   serParamNameLstInstance.add(nodeLocalName1);
								   int attrNode1 = dtm.getAttributeNode(child2, null, Constants.ATTRNAME_VALUE);
								   String value1 = dtm.getNodeValue(attrNode1);
								   if (value1 != null) {
									  serParamValueLstInstance.add(value1); 
								   }
							   }
							   else {
								   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator); 
							   }
						   }
						   else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator);
						   }
					   }
					   
					   if (child3 != DTM.NULL) {
						   short nodeType1 = dtm.getNodeType(child3);
						   if (nodeType1 == DTM.ELEMENT_NODE) {
							   String nsUri1 = dtm.getNamespaceURI(child3);
							   if ((Constants.XSL_SER_NAMESACE).equals(nsUri1)) {
								   String nodeLocalName1 = dtm.getLocalName(child3);
								   serParamNameLstInstance.add(nodeLocalName1);
								   int attrNode1 = dtm.getAttributeNode(child3, null, Constants.ATTRNAME_VALUE);
								   String value1 = dtm.getNodeValue(attrNode1);
								   if (value1 != null) {
								      serParamValueLstInstance.add(value1); 
								   }
							   }
							   else {
								   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																															  + "second argument, but its not of type "
																															  + "element(output:serialization-parameters)? or map(*).", srcLocator);
							   }
						   }
						   else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																														      + "second argument, but its not of type "
																															  + "element(output:serialization-parameters)? or map(*).", srcLocator);
						   }
					   }
					   
					   if (child4 != DTM.NULL) {
						   short nodeType1 = dtm.getNodeType(child4);
						   if (nodeType1 == DTM.ELEMENT_NODE) {
							   String nsUri1 = dtm.getNamespaceURI(child4);
							   if ((Constants.XSL_SER_NAMESACE).equals(nsUri1)) {
								   String nodeLocalName1 = dtm.getLocalName(child4);
								   serParamNameLstInstance.add(nodeLocalName1);
								   int attrNode1 = dtm.getAttributeNode(child4, null, Constants.ATTRNAME_VALUE);
								   String value1 = dtm.getNodeValue(attrNode1);
								   if (value1 != null) {
								      serParamValueLstInstance.add(value1); 
								   }
							   }
							   else {
								   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																															  + "second argument, but its not of type "
																															  + "element(output:serialization-parameters)? or map(*).", srcLocator);
							   }
						   }
						   else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																														      + "second argument, but its not of type "
																															  + "element(output:serialization-parameters)? or map(*).", srcLocator);
						   }
					   }
					   
					   if (child5 != DTM.NULL) {
						   short nodeType1 = dtm.getNodeType(child5);
						   if (nodeType1 == DTM.ELEMENT_NODE) {
							   String nsUri1 = dtm.getNamespaceURI(child5);
							   if ((Constants.XSL_SER_NAMESACE).equals(nsUri1)) {
								   String nodeLocalName1 = dtm.getLocalName(child5);
								   serParamNameLstInstance.add(nodeLocalName1);
								   int attrNode1 = dtm.getAttributeNode(child5, null, Constants.ATTRNAME_VALUE);
								   String value1 = dtm.getNodeValue(attrNode1);
								   if (value1 != null) {
								      serParamValueLstInstance.add(value1); 
								   }
							   }
							   else {
								   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																															  + "second argument, but its not of type "
																															  + "element(output:serialization-parameters)? or map(*).", srcLocator);
							   }
						   }
						   else {
							   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																														      + "second argument, but its not of type "
																															  + "element(output:serialization-parameters)? or map(*).", srcLocator);
						   }
					   }
					   
					   String methodName = Method.XML;
					   String indentStrValue = Constants.ATTRVAL_LITERAL_NO;
					   String omitXmlDeclStrValue = Constants.ATTRVAL_LITERAL_YES;
					   
					   int size1 = serParamNameLstInstance.size();
					   int size2 = serParamValueLstInstance.size();
					   
					   if (((size1 > 0) && (size1 <= SER_PARAM_COUNT_SUPPORTED)) && (size2 == size1)) {
						  boolean isUnique = (((serParamNameLstInstance.stream()).distinct()).count() == serParamNameLstInstance.size());
						  if (isUnique) {
							 for (int idx = 0; idx < size1; idx++) {
							    String paramName = serParamNameLstInstance.get(idx);
							    if (serParamNameLstDefn.contains(paramName)) {
							       if (paramName.equals(OutputKeys.METHOD)) {
							    	  methodName = serParamValueLstInstance.get(idx);   
							       }
							       else if (paramName.equals(OutputKeys.INDENT)) {
							    	  indentStrValue = serParamValueLstInstance.get(idx);
							    	  if ((Constants.ATTRVAL_LITERAL_YES).equals(indentStrValue) || "true".equals(indentStrValue) || "1".equals(indentStrValue)) {
							    		 indentStrValue = Constants.ATTRVAL_LITERAL_YES;  
							    	  }
							    	  else if ((Constants.ATTRVAL_LITERAL_NO).equals(indentStrValue) || "false".equals(indentStrValue) || "0".equals(indentStrValue)) {
							    		 indentStrValue = Constants.ATTRVAL_LITERAL_NO; 
							    	  }
							    	  else {
							    		 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																						                                         + "'indent' with value " + indentStrValue + " which is not "
																						                                         + "a valid representation of parameter 'indent'.", srcLocator); 
							    	  }
								   }
							       else if (paramName.equals(OutputKeys.OMIT_XML_DECLARATION)) {
							    	  omitXmlDeclStrValue = serParamValueLstInstance.get(idx);
							    	  if ((Constants.ATTRVAL_LITERAL_YES).equals(omitXmlDeclStrValue) || "true".equals(omitXmlDeclStrValue) || "1".equals(omitXmlDeclStrValue)) {
							    		 omitXmlDeclStrValue = Constants.ATTRVAL_LITERAL_YES;  
							    	  }
							    	  else if ((Constants.ATTRVAL_LITERAL_NO).equals(omitXmlDeclStrValue) || "false".equals(omitXmlDeclStrValue) || "0".equals(omitXmlDeclStrValue)) {
							    		 omitXmlDeclStrValue = Constants.ATTRVAL_LITERAL_NO; 
							    	  }
							    	  else {
							    		 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																											    				+ "'omit-xml-declaration' with value " + omitXmlDeclStrValue + " which is not "
																											    				+ "a valid representation of parameter 'omit-xml-declaration'.", srcLocator); 
							    	  }
								   }
							       else if (paramName.equals(Constants.ATTRNAME_ITEM_SEPARATOR)) {
							    	  itemSeparatorStr = serParamValueLstInstance.get(idx);  
							       }
							       else if (paramName.equals(Constants.ATTRNAME_OUTPUT_ENCODING)) {
								      encodingStr = serParamValueLstInstance.get(idx);  
								   }
							    }
							    else {
							       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator);	
							    }
							 }
						  }
						  else {
							 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator);
						  }
					   }
					   else {
						  throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																																+ "second argument, but its not of type "
																																+ "element(output:serialization-parameters)? or map(*).", srcLocator); 
					   }
					   
					   result = getFnSerializeResult(arg0XObj, m_arg0, methodName, indentStrValue, encodingStr, omitXmlDeclStrValue, 
							                                                                  xctxt, srcLocator, transformer, itemSeparatorStr);					   
					}
					else {
						throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a "
																														+ "second argument, but its not of type "
																														+ "element(output:serialization-parameters)? or map(*).", srcLocator);
					}
				}
				else {
					throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function call 'serialize' has a second "
																														+ "argument, but its not of type "
																														+ "element(output:serialization-parameters)? or map(*).", srcLocator); 
				}
			}
		}
		else {
		    throw new javax.xml.transform.TransformerException("XPST0017 : An XPath 3.1 function 'serialize' can only accept one or two arguments.", srcLocator);
		}
		
		
		return result;
	}

	/**
	 * Method definition, to XSL serialize the supplied xdm sequence.
	 * 
	 * @param arg0XObj											The supplied xdm sequence object
	 * @param arg0Expr                                          The function fn:serialize first argument 
	 *                                                          expression object.
	 * @param methodName                                        XSL serialization parameter method name
	 * @param indentStrValue                                    XSL serialization parameter indent value
	 * @param encodingStr                                       XSL serialization parameter encoding name 
	 *                                                          string.
	 * @param omitXmlDeclStr                                    XSL serialization parameter omit-xml-declaration 
	 *                                                          value, when serialization method is "xml".
	 * @param xctxt                                             An XPath context object
	 * @param srcLocator                                        An XSL transformation SourceLocator object 
	 * @param transformer                                       An XSL transformation TransformerImpl object
	 * @param itemSeparatorStr                                  The function fn:serializer parameter "item-separator" 
	 *                                                          string value.
	 * @return                                                  An XSL serialization result as xs:string value
	 * @throws TransformerException
	 */
	private XObject getFnSerializeResult(XObject arg0XObj, Expression arg0Expr, String methodName, String indentStrValue, String encodingStr, 
			                             String omitXmlDeclStr, XPathContext xctxt, SourceLocator srcLocator, 
			                                                                                         TransformerImpl transformer,
			                                                                                         String itemSeparatorStr) throws TransformerException {
		
		XObject result = null;

		if ((Method.XML).equals(methodName)) {			
			if (arg0XObj instanceof XPathMap) {
				throw new TransformerException("SENR0001 : A XPath expression evaluation dynamic error occured during function "
																				                           + "invocation 'serialize'. An xdm map is "
																				                           + "attempted to be serialized with method 'xml'.", srcLocator);
			}
			
			ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);

			StringWriter strWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(strWriter);

			SerializationHandler rhandler = getXslSerializationHandler(Method.XML, indentStrValue, encodingStr, omitXmlDeclStr, transformer, streamResult);
			
			if (itemSeparatorStr != null) {
			   rSeq = addXdmItemSeparator(rSeq, itemSeparatorStr);
			}

			result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
		}
		else if ((Method.HTML).equals(methodName)) {
			if (arg0XObj instanceof XPathMap) {
				throw new TransformerException("SENR0001 : A XPath expression evaluation dynamic error occured during function "
																				                           + "invocation 'serialize'. An xdm map is "
																				                           + "attempted to be serialized with method 'html'.", srcLocator);
			}
			
			ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);

			StringWriter strWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(strWriter);

			SerializationHandler rhandler = getXslSerializationHandler(Method.HTML, indentStrValue, encodingStr, null, transformer, streamResult);
			
			if (itemSeparatorStr != null) {
				rSeq = addXdmItemSeparator(rSeq, itemSeparatorStr);
			}

			result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
		}
		else if ((Method.XHTML).equals(methodName)) {
			if (arg0XObj instanceof XPathMap) {
				throw new TransformerException("SENR0001 : A XPath expression evaluation dynamic error occured during function "
																				                           + "invocation 'serialize'. An xdm map is "
																				                           + "attempted to be serialized with method 'xhtml'.", srcLocator);
			}
			
			ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);

			StringWriter strWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(strWriter);

			SerializationHandler rhandler = getXslSerializationHandler(Method.XHTML, indentStrValue, encodingStr, null, transformer, streamResult);
			
			if (itemSeparatorStr != null) {
				rSeq = addXdmItemSeparator(rSeq, itemSeparatorStr);
			}

			result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
		}
		else if ((Method.TEXT).equals(methodName)) {
			if (arg0XObj instanceof XPathMap) {
				throw new TransformerException("SENR0001 : A XPath expression evaluation dynamic error occured during function "
																				                           + "invocation 'serialize'. An xdm map is "
																				                           + "attempted to be serialized with method 'text'.", srcLocator);
			}
			
			ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);

			StringWriter strWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(strWriter);

			SerializationHandler rhandler = getXslSerializationHandler(Method.TEXT, indentStrValue, encodingStr, null, transformer, streamResult);
			
			if (itemSeparatorStr == null) {
			   itemSeparatorStr = " ";
			}
			
			rSeq = addXdmItemSeparator(rSeq, itemSeparatorStr);

			result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
		}
		else if ((Method.JSON).equals(methodName)) {
			if (arg0XObj instanceof XPathMap) {
				JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)arg0XObj);
				String jsonStr1 = null;
				if ((Constants.ATTRVAL_LITERAL_YES).equals(indentStrValue)) {
					jsonStr1 = jsonObj.toString(2);
				}
				else {
					jsonStr1 = jsonObj.toString(); 
				}

				ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(new XSString(jsonStr1), xctxt);

				StringWriter strWriter = new StringWriter();
				StreamResult streamResult = new StreamResult(strWriter);

				SerializationHandler rhandler = getXslSerializationHandler(Method.JSON, indentStrValue, encodingStr, null, transformer, streamResult);
				
				// The item-separator serialization parameter is not applicable to the JSON output method

				result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
			}			
		}
		else if ((Method.ADAPTIVE).equals(methodName)) {
			/**
			 * XPath 3.1 function fn:serialize parameter "method" with
			 * value 'adaptive', is useful to emit xdm sequence information
			 * with human readable form that is useful for debugging.
			 * 
			 * Ref : https://www.w3.org/TR/xslt-xquery-serialization-31/#adaptive-output
			 */
			
			ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
			int size1 = rSeq.size();
			
			ResultSequence rSeqResult = new ResultSequence();
			
			for (int idx = 0; idx < size1; idx++) {
				XObject xObj1 = rSeq.item(idx);
				if (xObj1 instanceof XMLNodeCursorImpl) {
				   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
				   int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
				   DTM dtm = xctxt.getDTM(nodeHandle);
				   int nodeType = dtm.getNodeType(nodeHandle);
				   if ((nodeType == DTM.DOCUMENT_NODE) || (nodeType == DTM.ELEMENT_NODE) || 
						                                      (nodeType == DTM.TEXT_NODE) || (nodeType == DTM.COMMENT_NODE) || 
						                                                                     (nodeType == DTM.PROCESSING_INSTRUCTION_NODE)) {
					   StringWriter strWriter = new StringWriter();
					   StreamResult streamResult = new StreamResult(strWriter);
					   
					   ResultSequence rSeq2 = new ResultSequence();
					   rSeq2.add(xObj1);

					   SerializationHandler rhandler = getXslSerializationHandler(Method.XML, indentStrValue, encodingStr, omitXmlDeclStr, transformer, streamResult);
					   XObject result1 = xslSerializeXdmSequence(rSeq2, strWriter, rhandler, xctxt, srcLocator, transformer);
					   rSeqResult.add(result1);
				   }
				   else if (nodeType == DTM.ATTRIBUTE_NODE) {					   
					   String localName = dtm.getLocalName(nodeHandle);
					   String nsUri = dtm.getNamespaceURI(nodeHandle);
					   String serializedForm = localName; 
					   if (nsUri != null) {
						  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)getExpressionOwner();
						  List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)(elemTemplateElement.getPrefixTable());
						  String prefix = XslTransformEvaluationHelper.getPrefixFromNsUri(nsUri, prefixTable);
						  serializedForm = prefix + ":" + serializedForm;
					   }
					   
					   String attrValue = dtm.getNodeValue(nodeHandle);					   
					   serializedForm = (serializedForm + "=\"" + attrValue + "\"");
					   
					   rSeqResult.add(new XSString(serializedForm));
				   }
				   else if (nodeType == DTM.NAMESPACE_NODE) {					   					   
					   String nodeName = dtm.getNodeName(nodeHandle);
					   String nodeValue = dtm.getNodeValue(nodeHandle);
					   
					   String serializedForm = (nodeName + "=\"" + nodeValue + "\"");
					   
					   rSeqResult.add(new XSString(serializedForm));
				   }				   
				}
				else if ((xObj1 instanceof XSBoolean) || (xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic)) {
				   boolean bool = xObj1.bool();				   
				   String serializedForm = bool ? "true()" : "false()";
				   
				   rSeqResult.add(new XSString(serializedForm));				   
				}
				else if ((xObj1 instanceof XSString) || (xObj1 instanceof XString) || (xObj1 instanceof XSUntypedAtomic) || (xObj1 instanceof XSAnyURI)) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
				   
				   str1 = str1.replace("\"", "\"\"");
				   str1 = "\"" + str1 + "\"";
				   
				   StringWriter strWriter = new StringWriter();
				   StreamResult streamResult = new StreamResult(strWriter);
				   
				   ResultSequence rSeq2 = new ResultSequence();
				   rSeq2.add(new XSString(str1));

				   SerializationHandler rhandler = getXslSerializationHandler(Method.TEXT, indentStrValue, encodingStr, omitXmlDeclStr, transformer, streamResult);
				   XObject result1 = xslSerializeXdmSequence(rSeq2, strWriter, rhandler, xctxt, srcLocator, transformer);
				   rSeqResult.add(result1);
				}
				else if ((xObj1 instanceof XSInteger) || (xObj1 instanceof XSDecimal)) {
				   String str1 = XslTransformEvaluationHelper.getStrVal(xObj1);
				   
				   rSeqResult.add(new XSString(str1));
				}
				else if (xObj1 instanceof XSDouble) {
					FuncFormatNumber funcFormatNumber = new FuncFormatNumber();					
					
					funcFormatNumber.setIsCalledFromFnSerialize(true);
					
					XObject arg0 = xObj1;
										
					XObject arg1 = new XSString(FORMAT_NUMBER_PIC_STRING);
					
					funcFormatNumber.setArg0(arg0);
					try {
					   funcFormatNumber.setArg(arg1, 1);
					} 
					catch (WrongNumberArgsException ex) {
						// no op
					}
					
					XObject xObj2 = funcFormatNumber.execute(xctxt);
					String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);
					
					str1 = str1.replace('E', 'e');
					
					rSeqResult.add(new XSString(str1));
				}
				else if (xObj1 instanceof XNumber) {
					XNumber xNumber = (XNumber)xObj1;
					XSDouble xsDouble = new XSDouble(xNumber.num());
					
                    FuncFormatNumber funcFormatNumber = new FuncFormatNumber();					
					
					funcFormatNumber.setIsCalledFromFnSerialize(true);
					
					XObject arg0 = xsDouble;
					
					XObject arg1 = new XSString(FORMAT_NUMBER_PIC_STRING);
					
					funcFormatNumber.setArg0(arg0);
					try {
					   funcFormatNumber.setArg(arg1, 1);
					} 
					catch (WrongNumberArgsException ex) {
						// no op
					}
					
					XObject xObj2 = funcFormatNumber.execute(xctxt);
					String str1 = XslTransformEvaluationHelper.getStrVal(xObj2);
					
					str1 = str1.replace('E', 'e');
					
					rSeqResult.add(new XSString(str1));					
				}
				else if (xObj1 instanceof XSQName) {
					XSQName xObj2 = (XSQName)xObj1;
					String localPart = xObj2.getLocalPart();
					String nsUri = xObj2.getNamespaceUri();
					
					String str1 = null;
					if ((nsUri == null) || "".equals(nsUri)) {
					   str1 = "Q{}" + localPart;
					}
					else {
					   str1 = "Q{" + nsUri + "}" + localPart;	
					}
					
					rSeqResult.add(new XSString(str1));
				}
				else if (xObj1 instanceof XSAnyAtomicType) {										
					String typeName = ((XSAnyAtomicType)xObj1).stringType();
					String value1 = XslTransformEvaluationHelper.getStrVal(xObj1);
					
					String str1 = (typeName + "(\"" + value1 + "\")");
					
					rSeqResult.add(new XSString(str1));
				}				
				else if (xObj1 instanceof XPathArray) {
					JSONArray jsonArray = getJSONArrayFromXdmArray((XPathArray)xObj1);
					String str1 = jsonArray.toString();
					
					rSeqResult.add(new XSString(str1));
				}
				else if (xObj1 instanceof XPathMap) {
					JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)xObj1);
					String str1 = jsonObj.toString();
					int length1 = str1.length();
					str1 = str1.replace("\"Numeric##", "");
					int length2 = str1.length();
					if (length1 != length2) {
					   str1 = str1.replace("\":", ":");
					}
					
					str1 = str1.replace("{", "map{");
					
					rSeqResult.add(new XSString(str1));
				}				
				else if (xObj1 instanceof XPathInlineFunction) {
					XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)xObj1;
					List<InlineFunctionParameter> paramList = xpathInlineFunction.getFuncParamList();
					int arity = paramList.size();
					
					String str1 = "(anonymous-function)#" + arity;
					
					rSeqResult.add(new XSString(str1));
				}
				else if (arg0Expr instanceof XPathNamedFunctionReference) {
					XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)arg0Expr;
					String funcName = xpathNamedFunctionReference.getFuncName();
					String nsUri = xpathNamedFunctionReference.getFuncNamespace();
					String str1 = "";
					if (nsUri != null) {
						ElemTemplateElement elemTemplateElement = (ElemTemplateElement)(getExpressionOwner());
						List<XMLNSDecl> prefixTable = elemTemplateElement.getPrefixTable();
						String prefix = XslTransformEvaluationHelper.getPrefixFromNsUri(nsUri, prefixTable);
						if ((prefix != null) && !"".equals(prefix)) {
							str1 = prefix + ":";
						}
						else if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(nsUri)) {
							str1 = "fn:";
						}
						else if ((XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(nsUri)) {    	       	   
							str1 = "math:";
						}
						else if ((XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(nsUri)) {    	       	   
							str1 = "map:";
						}
						else if ((XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(nsUri)) {     	   
							str1 = "array:";
						}
						else if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(nsUri)) {
							str1 = "xs:";
						}
					}					
					
					short arity = xpathNamedFunctionReference.getArity();
					str1 = (str1 + funcName + "#"+ arity);
					rSeqResult.add(new XSString(str1));
				}
			}
			
			if ((size1 == 0) && (arg0Expr instanceof NodeTest)) {
				ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression((NodeTest)arg0Expr, transformer, srcLocator);
                if (elemFunction != null) {				
                	QName fQName = elemFunction.getName();
                	String localPart = fQName.getLocalPart();
                	String nsUri = fQName.getNamespace();
                	short arity = elemFunction.getArity();
                	String str1 = "Q{" + nsUri + "}" + localPart + "#"+ arity;
                	
                	rSeqResult.add(new XSString(str1));
                }
			}
			
			if (itemSeparatorStr == null) {
			   itemSeparatorStr = "\n";
			}
			
			int size2 = rSeqResult.size();
			if (size2 > 1) {
				ResultSequence rSeq2 = new ResultSequence();
				for (int idx = 0; idx < size2; idx++) {
					XSString xObj = (XSString)(rSeqResult.item(idx)); 
					if (idx < (size2 - 1)) {
					   rSeq2.add(xObj);
					   rSeq2.add(new XSString(itemSeparatorStr));
					}
					else {
					   rSeq2.add(xObj);
					}
				}
				
				result = rSeq2; 
			}
			else {
				result = rSeqResult;
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("SEPM0016 : This implementation supports XPath 3.1 function 'serialize', "
																											+ "serialization methods : xml, html, xhtml, text, json, adaptive. "
																											+ "The supplied XSL serialization method is " + methodName + ".", srcLocator);
		}						

		return result;
	}

	/**
	 * Method definition, to add an item separator string value 
	 * between the items within the supplied sequence.
	 * 
	 * @param rSeq							The supplied xdm sequence
	 * @param itemSeparatorStr              The item separator string value
	 */
	private ResultSequence addXdmItemSeparator(ResultSequence rSeq, String itemSeparatorStr) {		
		
		ResultSequence resultSeq = new ResultSequence();
		
		int size2 = rSeq.size();		
		if (size2 > 1) {
			for (int idx = 0; idx < size2; idx++) {
				XObject xObj = rSeq.item(idx); 
				if (idx < (size2 - 1)) {
					resultSeq.add(xObj);
					resultSeq.add(new XSString(itemSeparatorStr));
				}
				else {
					resultSeq.add(xObj);
				}
			} 
		}
		else {
			resultSeq = rSeq; 
		}
		
		return resultSeq;
	}

	/**
	 * Method definition, to XSL serialize the supplied xdm sequence to a 
	 * string bound to the supplied SerializationHandler object. 
	 * 
	 * @param rSeq								The supplied xdm sequence object
	 * @param strWriter                         A StringWriter object to which XSL
	 *                                          serialization result goes to.
	 * @param rhandler                          SerializationHandler object instance
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object 
	 * @param transformer                       An XSL transformation TransformerImpl object instance
	 * @return                                  An XSL serialization result with type xs:string
	 * @throws TransformerException
	 */
	private XObject xslSerializeXdmSequence(ResultSequence rSeq, StringWriter strWriter, SerializationHandler rhandler,
			                                XPathContext xctxt, SourceLocator srcLocator, TransformerImpl transformer) throws TransformerException {
		
		XObject result = null;
		
		try {				 				  
			ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformer, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner()); 

			String str1 = (strWriter.getBuffer()).toString();				 
			result = new XSString(str1);
		} 
		catch (TransformerException ex) {
			String errMesg = ex.getMessage();				 
			throw new javax.xml.transform.TransformerException(errMesg, srcLocator);
		} 
		catch (SAXException ex) {
			String errMesg = ex.getMessage();				 
			throw new javax.xml.transform.TransformerException(errMesg, srcLocator);
		}
		
		return result;
	}
	
	/**
	 * Method definition, to get 'JSONObject' object using the supplied
	 * xdm map object.
	 * 
	 * @param xpathMap							The supplied xdm map object
	 * @return                                  The JSONObject object
	 * @throws TransformerException
	 */
	private JSONObject getJSONObjectFromXdmMap(XPathMap xpathMap) throws TransformerException {
    	
		JSONObject result = new JSONObject();
		
		Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
		Set<XObject> keySet = nativeMap.keySet();
		Iterator<XObject> iter1 = keySet.iterator();
		
		while (iter1.hasNext()) {			
			XObject key = iter1.next();
			XObject value = nativeMap.get(key);
			
			boolean isKeyNumeric = (((key instanceof XSNumericType) || (key instanceof XNumber)) ? true : false);
			
			String keyStr1 = XslTransformEvaluationHelper.getStrVal(key);
			keyStr1 = (isKeyNumeric ? "Numeric##" + keyStr1 : keyStr1);

			if ((value instanceof XString) || (value instanceof XSString)) {
				String valueStr1 = XslTransformEvaluationHelper.getStrVal(value);
				result.put(keyStr1, valueStr1);
			}
			else if (value instanceof XSNumericType) {
				String valueStr1 = XslTransformEvaluationHelper.getStrVal(value);
				result.put(keyStr1, Double.valueOf(valueStr1));
			}
			else if (value instanceof XNumber) {
				double doubleVal = ((XNumber)value).num();
				result.put(keyStr1, Double.valueOf(doubleVal));
			}
			else if (value instanceof XSBoolean) {
				boolean bool1 = value.bool();
				result.put(keyStr1, Boolean.valueOf(bool1));
			}
			else if ((value instanceof XBoolean) || (value instanceof XBooleanStatic)) {
				boolean bool1 = value.bool();
				result.put(keyStr1, Boolean.valueOf(bool1));
			}
			else if (value instanceof XPathMap) {
				JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)value);
				result.put(keyStr1, jsonObj);
			}
			else if (value instanceof XPathArray) {								
				JSONArray jsonArr = new JSONArray();
				
				XPathArray xpathArr = (XPathArray)value;								
				int arrLen = xpathArr.size(); 
				
				for (int idx = 0; idx < arrLen; idx++) {
					XObject arrItem = xpathArr.get(idx);					
					if ((arrItem instanceof XString) || (arrItem instanceof XSString)) {
					    jsonArr.put(XslTransformEvaluationHelper.getStrVal(arrItem));
					}
					else if (arrItem instanceof XSNumericType) {
						String valueStr1 = XslTransformEvaluationHelper.getStrVal(arrItem);
						jsonArr.put(Double.valueOf(valueStr1));
					}
					else if (arrItem instanceof XNumber) {
						String valueStr1 = XslTransformEvaluationHelper.getStrVal(arrItem);
						jsonArr.put(Double.valueOf(valueStr1));
					}
					else if (arrItem instanceof XSBoolean) {
						boolean bool1 = arrItem.bool();
						jsonArr.put(bool1);
					}
					else if ((arrItem instanceof XBoolean) || (arrItem instanceof XBooleanStatic)) {
						boolean bool1 = arrItem.bool();
						jsonArr.put(bool1);
					}
					else if (arrItem instanceof XPathMap) {
						JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)arrItem);
						jsonArr.put(jsonObj);
					}
					else if (arrItem instanceof XPathArray) {
						JSONArray jsonArr2 = getJSONArrayFromXdmArray((XPathArray)arrItem);
						jsonArr.put(jsonArr2);
					}
				}

				result.put(keyStr1, jsonArr);
			}
			else if (value instanceof ResultSequence) {				
                JSONArray jsonArr = new JSONArray();
                
                jsonArr.setIsXdmSequence(true);
				
                ResultSequence rSeq = (ResultSequence)value;								
				int size1 = rSeq.size(); 
				
				for (int idx = 0; idx < size1; idx++) {
					XObject xObj1 = rSeq.item(idx);					
					if ((xObj1 instanceof XString) || (xObj1 instanceof XSString)) {
					    jsonArr.put(XslTransformEvaluationHelper.getStrVal(xObj1));
					}
					else if (xObj1 instanceof XSNumericType) {
						String valueStr1 = XslTransformEvaluationHelper.getStrVal(xObj1);
						jsonArr.put(Double.valueOf(valueStr1));
					}
					else if (xObj1 instanceof XNumber) {
						String valueStr1 = XslTransformEvaluationHelper.getStrVal(xObj1);
						jsonArr.put(Double.valueOf(valueStr1));
					}
					else if (xObj1 instanceof XSBoolean) {
						boolean bool1 = xObj1.bool();
						jsonArr.put(bool1);
					}
					else if ((xObj1 instanceof XBoolean) || (xObj1 instanceof XBooleanStatic)) {
						boolean bool1 = xObj1.bool();
						jsonArr.put(bool1);
					}
					else if (xObj1 instanceof XPathMap) {
						JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)xObj1);
						jsonArr.put(jsonObj);
					}
					else if (xObj1 instanceof XPathArray) {
						JSONArray jsonArr2 = getJSONArrayFromXdmArray((XPathArray)xObj1);
						jsonArr.put(jsonArr2);
					}
					else if (xObj1 instanceof ResultSequence) {
						XPathArray xpathArr = new XPathArray();
						
						ResultSequence rSeq2 = (ResultSequence)xObj1;						
						int size2 = rSeq2.size();
						for (int idx2 = 0; idx2 < size2; idx2++) {
						   XObject xObj2 = rSeq2.item(idx2);
						   xpathArr.add(xObj2);
						}
						
						JSONArray jsonArr2 = getJSONArrayFromXdmArray(xpathArr);						
						jsonArr2.setIsXdmSequence(true);						
						
						jsonArr.put(jsonArr2);
					}
				}

				result.put(keyStr1, jsonArr);
			}
		}

		return result;
    }
	
	/**
	 * Method definition, to get 'JSONArray' object using the supplied
	 * xdm array object.
	 * 
	 * @param xpathArr							The supplied xdm array object
	 * @return                                  The JSONArray object
	 * @throws TransformerException
	 */
    private JSONArray getJSONArrayFromXdmArray(XPathArray xpathArr) throws TransformerException {
    	
    	JSONArray result = new JSONArray();
    	
    	int arrLen = xpathArr.size(); 
    	
    	for (int idx = 0; idx < arrLen; idx++) {
			XObject arrItem = xpathArr.get(idx);					
			if ((arrItem instanceof XString) || (arrItem instanceof XSString)) {
			    result.put(XslTransformEvaluationHelper.getStrVal(arrItem));
			}
			else if (arrItem instanceof XSNumericType) {
				String valueStr1 = XslTransformEvaluationHelper.getStrVal(arrItem);
				result.put(Double.valueOf(valueStr1));
			}
			else if (arrItem instanceof XNumber) {
				String valueStr1 = XslTransformEvaluationHelper.getStrVal(arrItem);
				result.put(Double.valueOf(valueStr1));
			}
			else if (arrItem instanceof XSBoolean) {
				boolean bool1 = arrItem.bool();
				result.put(bool1);
			}
			else if ((arrItem instanceof XBoolean) || (arrItem instanceof XBooleanStatic)) {
				boolean bool1 = arrItem.bool();
				result.put(bool1);
			}
			else if (arrItem instanceof XPathMap) {
				JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)arrItem);
				result.put(jsonObj);
			}
			else if (arrItem instanceof XPathArray) {
				JSONArray jsonArr2 = getJSONArrayFromXdmArray((XPathArray)arrItem);
				result.put(jsonArr2);
			}
			else if (arrItem instanceof ResultSequence) {
				XPathArray xpathArr2 = new XPathArray();

				ResultSequence rSeq2 = (ResultSequence)arrItem;						
				int size2 = rSeq2.size();
				for (int idx2 = 0; idx2 < size2; idx2++) {
					XObject xObj2 = rSeq2.item(idx2);
					xpathArr2.add(xObj2);
				}

				JSONArray jsonArr2 = getJSONArrayFromXdmArray(xpathArr2);						
				jsonArr2.setIsXdmSequence(true);

				result.put(jsonArr2);
			}
		}

    	return result;
    }
    
    /**
     * Method definition, to get XSL SerializationHandler object, using the supplied
     * XSL serialization parameter values. 
     * 
     * @param methodName								An XSL serialization method name value
     * @param indentStrValue							An XSL serialization indent value
     * @param encodingStr                               An XSL serialization encoding name string
     * @param omitXmlDeclStrValue                       An XSL serialization omit-xml-declaration 
     *                                                  parameter value, when methodName is "xml". 
     * @param transformerImpl                           An XSL transformation TransformerImpl object
     * @param streamResult                              A StreamResult object instance, that is the
     *                                                  destination of an XSL transformation serialization.  
     *                                              
     * @return                                          An XSL SerializationHandler constructed object
     * @throws TransformerException
     */
    private SerializationHandler getXslSerializationHandler(String methodName, String indentStrValue, String encodingStr, 
    		                                                String omitXmlDeclStrValue, TransformerImpl transformerImpl,
    		                                                StreamResult streamResult) throws TransformerException {    	
    	
    	SerializationHandler result = null;    	    	
    	
    	if ((Method.XML).equals(methodName)) {
    	   OutputProperties outputProperties = new OutputProperties(methodName);
    	   outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
    	   outputProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclStrValue);
    	   if (encodingStr != null) {
    		  outputProperties.setProperty(OutputKeys.ENCODING, encodingStr);  
    	   }
    	   else {
    		  outputProperties.setProperty(OutputKeys.ENCODING, UTF_8); 
    	   }
    	   
    	   result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
    	else if ((Method.HTML).equals(methodName)) {
    	   OutputProperties outputProperties = new OutputProperties(methodName);
    	   outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
    	   if (encodingStr != null) {
     		  outputProperties.setProperty(OutputKeys.ENCODING, encodingStr);  
     	   }
     	   else {
     		  outputProperties.setProperty(OutputKeys.ENCODING, UTF_8); 
     	   }
    	   
    	   result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
        else if ((Method.XHTML).equals(methodName)) {
           OutputProperties outputProperties = new OutputProperties(methodName);
           outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
           if (encodingStr != null) {
     		  outputProperties.setProperty(OutputKeys.ENCODING, encodingStr);  
     	   }
     	   else {
     		  outputProperties.setProperty(OutputKeys.ENCODING, UTF_8); 
     	   }
           
           result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
        else if ((Method.TEXT).equals(methodName)) {
           OutputProperties outputProperties = new OutputProperties(methodName);
           outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
           if (encodingStr != null) {
     		  outputProperties.setProperty(OutputKeys.ENCODING, encodingStr);  
     	   }
     	   else {
     		  outputProperties.setProperty(OutputKeys.ENCODING, UTF_8); 
     	   }
           
           result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
        else if ((Method.JSON).equals(methodName)) {
           OutputProperties outputProperties = new OutputProperties(methodName);
           outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
           if (encodingStr != null) {
     		  outputProperties.setProperty(OutputKeys.ENCODING, encodingStr);  
     	   }
     	   else {
     		  outputProperties.setProperty(OutputKeys.ENCODING, UTF_8); 
     	   }
           
           result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
    	
    	return result;
    }
}
