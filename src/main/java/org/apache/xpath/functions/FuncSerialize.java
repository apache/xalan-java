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
import java.util.Iterator;
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
	
	private static final String YES = "yes";
	
	private static final String NO = "no";

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
		TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
		
		if (m_arg0 != null) {
		   XObject arg0XObj = m_arg0.execute(xctxt);
		   
		   if (m_arg1 == null) {			  
			  ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
			  
			  OutputProperties outputProperties = new OutputProperties(Method.XML);
			  StringWriter strWriter = new StringWriter();
			  StreamResult streamResult = new StreamResult(strWriter);
			  SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);
			  
			  try {				 				  
				 ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());					 
				 
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
		   }
		   else {
			  // An XPath 3.1 function call fn:serialize is called with two arguments.			   
			  XObject arg1Obj = m_arg1.execute(xctxt);    // Evaluating function fn:serialize params argument
			  if (arg1Obj instanceof XPathMap) {
				 XPathMap xpathMap = (XPathMap)arg1Obj;				 
				 
				 XObject fnSerMethodXObj = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_METHOD));
				 String serMethodStr = null;				 
				 
				 XObject fnSerIndentXObj = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_INDENT));
				 String serIndentStr = null;
				 if (fnSerIndentXObj != null) {
					if ((fnSerIndentXObj instanceof XSBoolean) || (fnSerIndentXObj instanceof XBoolean) || (fnSerIndentXObj instanceof XBooleanStatic)) {
					   serIndentStr = (fnSerIndentXObj.bool()) ? YES : NO;
					}
					else {
					   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
					   		                                                                                                          + "'indent' not of the type boolean.", srcLocator);
					}
				 }
				 else {
					serIndentStr = NO; 
				 }
				 
				 XObject fnSerOmitXmlDeclXObj = xpathMap.get(new XSString(Constants.ATTRNAME_OUTPUT_OMITXMLDECL));
				 String fnSerOmitXmlDeclStr = null;
				 if (fnSerOmitXmlDeclXObj != null) {
					 if ((fnSerOmitXmlDeclXObj instanceof XSBoolean) || (fnSerOmitXmlDeclXObj instanceof XBoolean) || (fnSerOmitXmlDeclXObj instanceof XBooleanStatic)) {
						 fnSerOmitXmlDeclStr = (fnSerOmitXmlDeclXObj.bool()) ? YES : NO;
					 }
					 else {
						 throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'serialize' is called with parameter "
								                                                                                                        + "'omit-xml-declaration' not of the type boolean.", srcLocator);
					 }
				 }
				 else {
					 fnSerOmitXmlDeclStr = NO; 
				 }
				 
				 if (fnSerMethodXObj != null) {
					serMethodStr = XslTransformEvaluationHelper.getStrVal(fnSerMethodXObj);
					if ((Method.XML).equals(serMethodStr)) {
						ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
						
						OutputProperties outputProperties = new OutputProperties(Method.XML);
						outputProperties.setProperty(OutputKeys.INDENT, serIndentStr);
						outputProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, fnSerOmitXmlDeclStr);
						
						StringWriter strWriter = new StringWriter();
						StreamResult streamResult = new StreamResult(strWriter);
						SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);
						
						try {				 				  
							ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());
							
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
					}
					else if ((Method.HTML).equals(serMethodStr)) {
						ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
						
						OutputProperties outputProperties = new OutputProperties(Method.HTML);
						outputProperties.setProperty(OutputKeys.INDENT, serIndentStr);
						
						StringWriter strWriter = new StringWriter();
						StreamResult streamResult = new StreamResult(strWriter);
						SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);
						
						try {				 				  
							ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());
							
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
					}
					else if ((Method.XHTML).equals(serMethodStr)) {
						ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
						
						OutputProperties outputProperties = new OutputProperties(Method.XHTML);
						outputProperties.setProperty(OutputKeys.INDENT, serIndentStr);
						
						StringWriter strWriter = new StringWriter();
						StreamResult streamResult = new StreamResult(strWriter);
						SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);
						
						try {				 				  
							ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());
							
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
					}
                    else if ((Method.TEXT).equals(serMethodStr)) {
						ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
						
						OutputProperties outputProperties = new OutputProperties(Method.TEXT);
						StringWriter strWriter = new StringWriter();
						StreamResult streamResult = new StreamResult(strWriter);
						SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);
						
						try {				 				  
							ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());
							
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
					}
                    else if ((Method.JSON).equals(serMethodStr)) {
                       if (arg0XObj instanceof XPathMap) {
                    	   JSONObject jsonObj = getJSONObjectFromXdmMap((XPathMap)arg0XObj);
                    	   String jsonStr1 = null;
                    	   if (YES.equals(serIndentStr)) {
                    	      jsonStr1 = jsonObj.toString(2);
                    	   }
                    	   else {
                    		  jsonStr1 = jsonObj.toString(); 
                    	   }
                    	   
                    	   ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(new XSString(jsonStr1), xctxt);

                    	   OutputProperties outputProperties = new OutputProperties(Method.JSON);
                    	   
                    	   StringWriter strWriter = new StringWriter();
                    	   StreamResult streamResult = new StreamResult(strWriter);
                    	   SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);

                    	   try {
                    		   ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());
                    		   
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
                       }
					}
                    else {
                       throw new javax.xml.transform.TransformerException("SEPM0016 : This implementation supports XPath 3.1 function 'serialize', "
                       		                                                                              + "serialization methods : xml, html, xhtml, text, json. "
                       		                                                                              + "The supplied XSL serialization method is " + serMethodStr + ".", srcLocator);
                    }
				 }
				 else {
					 // The default fn:serialize method is "xml"					 
					 ResultSequence rSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg0XObj, xctxt);
					 
					 OutputProperties outputProperties = new OutputProperties(Method.XML);
					 outputProperties.setProperty(OutputKeys.INDENT, serIndentStr);
					 outputProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, fnSerOmitXmlDeclStr);
					 
					 StringWriter strWriter = new StringWriter();
					 StreamResult streamResult = new StreamResult(strWriter);
					 SerializationHandler rhandler = transformerImpl.createSerializationHandler(streamResult, outputProperties);
					 
					 try {				 				  
						 ElemCopyOf.copyOfActionOnResultSequence(rSeq, transformerImpl, rhandler, xctxt, false, (ElemTemplateElement)getExpressionOwner());
						 
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
				 }
			  }
			  else if (arg1Obj instanceof XMLNodeCursorImpl) {				  
				 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg1Obj;
				 int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
				 DTM dtm = xctxt.getDTM(nodeHandle);				 
				 short nodeType = dtm.getNodeType(nodeHandle);
				 String nodeLocalName = dtm.getLocalName(nodeHandle);
				 String nsUri = dtm.getNamespaceURI(nodeHandle);
				 if ((nodeType == DTM.ELEMENT_NODE) && (Constants.XSL_SER_NAMESACE).equals(nsUri) && (Constants.XSL_SER_PARAMS).equals(nodeLocalName)) {
					// TO DO
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
}
