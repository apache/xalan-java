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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:serialize.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSerialize extends FunctionMultiArgs {

	private static final long serialVersionUID = -2074055550828065958L;
	
	private static final int SER_PARAM_COUNT_SUPPORTED = 3;

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

				SerializationHandler rhandler = getXslSerializationHandler(Method.XML, Constants.ATTRVAL_LITERAL_NO, Constants.ATTRVAL_LITERAL_NO, 
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

					XObject fnSerMethodXObj = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_METHOD));
					String methodName = null;				 

					XObject fnSerIndentXObj = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_INDENT));
					String indentStrValue = null;
					if (fnSerIndentXObj != null) {
						if ((fnSerIndentXObj instanceof XSBoolean) || (fnSerIndentXObj instanceof XBoolean) || (fnSerIndentXObj instanceof XBooleanStatic)) {
							indentStrValue = (fnSerIndentXObj.bool()) ? Constants.ATTRVAL_LITERAL_YES : Constants.ATTRVAL_LITERAL_NO;
						}
						else {
							throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																																	+ "'indent' not of the type boolean.", srcLocator);
						}
					}
					else {
						indentStrValue = Constants.ATTRVAL_LITERAL_NO; 
					}

					XObject fnSerOmitXmlDeclXObj = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_OMITXMLDECL));
					String omitXmlDeclStrValue = null;
					if (fnSerOmitXmlDeclXObj != null) {
						if ((fnSerOmitXmlDeclXObj instanceof XSBoolean) || (fnSerOmitXmlDeclXObj instanceof XBoolean) || (fnSerOmitXmlDeclXObj instanceof XBooleanStatic)) {
							omitXmlDeclStrValue = (fnSerOmitXmlDeclXObj.bool()) ? Constants.ATTRVAL_LITERAL_YES : Constants.ATTRVAL_LITERAL_NO;
						}
						else {
							throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
																																	+ "'omit-xml-declaration' not of the type boolean.", srcLocator);
						}
					}
					else {
						omitXmlDeclStrValue = Constants.ATTRVAL_LITERAL_NO; 
					}

					if (fnSerMethodXObj != null) {
						methodName = XslTransformEvaluationHelper.getStrVal(fnSerMethodXObj);
						
						result = getFnSerializeResult(arg0XObj, methodName, indentStrValue, omitXmlDeclStrValue, xctxt, srcLocator, transformer);
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

						SerializationHandler rhandler = getXslSerializationHandler(Method.XML, indentStrValue, omitXmlDeclStrValue, transformer, streamResult);

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
					   
					   String methodName = Method.XML;
					   String indentStrValue = Constants.ATTRVAL_LITERAL_NO;
					   String omitXmlDeclStrValue = Constants.ATTRVAL_LITERAL_NO;
					   
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
								   }
							       else if (paramName.equals(OutputKeys.OMIT_XML_DECLARATION)) {
							    	  omitXmlDeclStrValue = serParamValueLstInstance.get(idx);   
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
					   
					   result = getFnSerializeResult(arg0XObj, methodName, indentStrValue, omitXmlDeclStrValue, xctxt, srcLocator, transformer);					   
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
	 * @param methodName                                        XSL serialization parameter method name
	 * @param indentStrValue                                    XSL serialization parameter indent value
	 * @param omitXmlDeclStrValue                               XSL serialization parameter omit-xml-declaration 
	 *                                                          value, when serialization method is "xml".
	 * @param xctxt                                             An XPath context object
	 * @param srcLocator                                        An XSL transformation SourceLocator object 
	 * @param transformer                                       An XSL transformation TransformerImpl object
	 * @return                                                  An XSL serialization result as xs:string value
	 * @throws TransformerException
	 */
	private XObject getFnSerializeResult(XObject arg0XObj, String methodName, String indentStrValue, 
			                             String omitXmlDeclStrValue, XPathContext xctxt, SourceLocator srcLocator, 
			                                                                                         TransformerImpl transformer) throws TransformerException {
		
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

			SerializationHandler rhandler = getXslSerializationHandler(Method.XML, indentStrValue, omitXmlDeclStrValue, transformer, streamResult);

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

			SerializationHandler rhandler = getXslSerializationHandler(Method.HTML, indentStrValue, null, transformer, streamResult);

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

			SerializationHandler rhandler = getXslSerializationHandler(Method.XHTML, indentStrValue, null, transformer, streamResult);

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

			SerializationHandler rhandler = getXslSerializationHandler(Method.TEXT, indentStrValue, null, transformer, streamResult);

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

				SerializationHandler rhandler = getXslSerializationHandler(Method.JSON, indentStrValue, null, transformer, streamResult);

				result = xslSerializeXdmSequence(rSeq, strWriter, rhandler, xctxt, srcLocator, transformer);
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("SEPM0016 : This implementation supports XPath 3.1 function 'serialize', "
																											+ "serialization methods : xml, html, xhtml, text, json. "
																											+ "The supplied XSL serialization method is " + methodName + ".", srcLocator);
		}

		return result;
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
			
			String keyStr1 = XslTransformEvaluationHelper.getStrVal(key);			

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
		}

    	return result;
    }
    
    /**
     * Method definition, to get XSL SerializationHandler object, using the supplied
     * XSL serialization parameter values. 
     * 
     * @param methodName								An XSL serialization method name
     * @param indentStrValue							An XSL serialization indent value
     * @param omitXmlDeclStrValue                       An XSL serialization omit-xml-declaration 
     *                                                  parameter value, when methodName is "xml". 
     * @param transformerImpl                           An XSL transformation TransformerImpl object
     * @param streamResult                              A StreamResult object instance, that is the
     *                                                  destination of an XSL transformation serialization.  
     *                                              
     * @return                                          An XSL SerializationHandler constructed object
     * @throws TransformerException
     */
    private SerializationHandler getXslSerializationHandler(String methodName, String indentStrValue, 
    		                                                String omitXmlDeclStrValue, TransformerImpl transformerImpl,
    		                                                StreamResult streamResult) throws TransformerException {    	
    	
    	SerializationHandler result = null;    	    	
    	
    	if ((Method.XML).equals(methodName)) {
    	   OutputProperties outputProperties = new OutputProperties(methodName);
    	   outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
    	   outputProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclStrValue);
    	   
    	   result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
    	else if ((Method.HTML).equals(methodName)) {
    	   OutputProperties outputProperties = new OutputProperties(methodName);
    	   outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
    	   
    	   result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
        else if ((Method.XHTML).equals(methodName)) {
           OutputProperties outputProperties = new OutputProperties(methodName);
           outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
           
           result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
        else if ((Method.TEXT).equals(methodName)) {
           OutputProperties outputProperties = new OutputProperties(methodName);
           outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
           
           result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
        else if ((Method.JSON).equals(methodName)) {
           OutputProperties outputProperties = new OutputProperties(methodName);
           outputProperties.setProperty(OutputKeys.INDENT, indentStrValue);
           
           result = transformerImpl.createSerializationHandler(streamResult, outputProperties);
    	}
    	
    	return result;
    }
}
