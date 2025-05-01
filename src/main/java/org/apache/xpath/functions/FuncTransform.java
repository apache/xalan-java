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
package org.apache.xpath.functions;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.templates.ElemResultDocument;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.utils.Constants;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function, fn:transform.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncTransform extends FunctionDef1Arg
{

	private static final long serialVersionUID = 4109927115539483019L;
	
	/**
	 * Definition of various class, constant field definitions. 
	 */
	
    public static final String XSLT_TRANSFORMER_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
    
    public static final String XSLT_TRANSFORMER_FACTORY_VALUE = "org.apache.xalan.processor.XSL3TransformerFactoryImpl";
    
    public static final String STYLESHEET_LOCATION = "stylesheet-location";    
    
    public static final String STYLESHEET_NODE = "stylesheet-node";
    
    public static final String STYLESHEET_TEXT = "stylesheet-text";
    
    public static final String SOURCE_NODE = "source-node";
    
    public static final String BASE_OUTPUT_URI = "base-output-uri";
    
    public static final String DELIVERY_FORMAT = "delivery-format";
    
    public static final String SERLIALIZATION_PARAMS = "serialization-params";
    
    public static final String DOCUMENT = "document";
    
    public static final String SERIALIZED = "serialized";
    
    public static final String RAW = "raw";
    
    public static final String OUTPUT = "output";
    

	/**
	 * Execute the function. The function must return a valid object.
	 * 
	 * @param xctxt The current execution context.
	 * @return A valid XObject.
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XPathMap result = new XPathMap();
		
		SourceLocator srcLocator = xctxt.getSAXLocator();

		Expression arg0 = m_arg0;
		XObject arg0Obj = arg0.execute(xctxt);
		
		// Reference of result object instance of, function fn:transform's evaluation.
		ElemResultDocument.m_fnTransformResult = result;
		
		System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
    	System.setProperty(XSLT_TRANSFORMER_FACTORY_KEY, XSLT_TRANSFORMER_FACTORY_VALUE);
		
		if (!(arg0Obj instanceof XPathMap)) {
			throw new javax.xml.transform.TransformerException("FODC0005 : The 1st argument to function call fn:transform "
					                                                                                  + "is not an XDM map.", srcLocator);
		}
		else {
		   XPathMap mapArg = (XPathMap)arg0Obj;
		   
		   // An XSL stylesheet input document, for fn:transform function call 
		   // may be provided with one of following ways.
		   XObject xslStylesheetLocObj = mapArg.get(new XSString(STYLESHEET_LOCATION));
		   XObject xslStylesheetNodeObj = mapArg.get(new XSString(STYLESHEET_NODE));
		   XObject xslStylesheetTxtObj = mapArg.get(new XSString(STYLESHEET_TEXT));
		   
		   boolean xslStylesheetSrcInpErr = false;
		   
		   if (xslStylesheetLocObj != null && ((xslStylesheetNodeObj != null) || (xslStylesheetTxtObj != null))) {
			   xslStylesheetSrcInpErr = true;
		   }
		   else if (xslStylesheetNodeObj != null && ((xslStylesheetLocObj != null) || (xslStylesheetTxtObj != null))) {
			   xslStylesheetSrcInpErr = true;
		   }
		   else if (xslStylesheetTxtObj != null && ((xslStylesheetLocObj != null) || (xslStylesheetNodeObj != null))) {
			   xslStylesheetSrcInpErr = true; 
		   }
		   
           XObject sourceInpDocNodeObj = mapArg.get(new XSString(SOURCE_NODE));
		   
		   if (sourceInpDocNodeObj == null) {
			   throw new javax.xml.transform.TransformerException("FODC0005 : For the fn:transform function call, an argument's map key 'source-node' "
			   		                                                                             + "is not provided. This identifies an XML input document that will be "
			   		                                                                             + "transformed by fn:transform function call.", srcLocator); 
		   }
		   
		   if (xslStylesheetSrcInpErr) {
			   throw new javax.xml.transform.TransformerException("FODC0005 : Exactly only one of the following fn:transform argument "
			   		                                                                            + "map entry keys must be present : 'stylesheet-location', "
			   		                                                                            + "'stylesheet-node' or 'stylesheet-text'. This identifies an XSL "
			   		                                                                            + "stylesheet document, that will transform an XML input document source.", srcLocator); 
		   }
		   
		   // The following are optional, function fn:transform's 'map' argument entry 
		   // key values.
		   XObject baseOutputUri = mapArg.get(new XSString(BASE_OUTPUT_URI));		   		   
		   XObject deliveryFormat = mapArg.get(new XSString(DELIVERY_FORMAT));
		   XObject serializationParams = mapArg.get(new XSString(SERLIALIZATION_PARAMS));  // TO DO
		   
		   String baseOutputUriStrValue = ((baseOutputUri != null) ? XslTransformEvaluationHelper.getStrVal(
				   																							baseOutputUri) : null);
		   
		   if (baseOutputUriStrValue != null) {
			   URL baseOutputUrl = getAbsoluteUrlValue(baseOutputUriStrValue, srcLocator);
			   
			   ElemResultDocument.m_BaseOutputUriStrAbsValue = baseOutputUrl.toString(); 
		   }
		   
		   String deliverFormatStrValue = ((deliveryFormat != null) ? XslTransformEvaluationHelper.getStrVal(
				                                                                                            deliveryFormat) : null);
		   if (deliverFormatStrValue != null) {
			   if (!(DOCUMENT.equals(deliverFormatStrValue) || SERIALIZED.equals(deliverFormatStrValue) || 
					   																				 RAW.equals(deliverFormatStrValue))) {
				   throw new javax.xml.transform.TransformerException("FODC0005 : An XPath function fn:transform map argument "
						   																		          + "entry with key \"delivery-format\" can have value either "
						   																                  + "'document', 'serialized' or 'raw'.", srcLocator);			   
			   }
		   }
		   
		   ElemResultDocument.m_fnTransformDeliveryFormat = deliverFormatStrValue;		   		   
		   
		   if (xslStylesheetLocObj != null) {
			    // XPath function fn:transform's XSL transformation, using XSL stylesheet 
			    // information provided by fn:transform's map key 'stylesheet-location'.
			   
			    try {
			    	String hrefStrVal = XslTransformEvaluationHelper.getStrVal(xslStylesheetLocObj);

			    	URL xslSecondaryStylesheetAbsUrl = getAbsoluteUrlValue(hrefStrVal, srcLocator);			   			    

			    	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)sourceInpDocNodeObj;
			    	DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
			    	int xmlInpNodeHandle = dtmCursorIter.nextNode();
			    	DTM dtm = xctxt.getDTM(xmlInpNodeHandle);			    
			    	Node xmlInpDocNode = dtm.getNode(xmlInpNodeHandle);

			    	String xmlInpDocStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(xmlInpDocNode);			    
			    	String xslStylesheetUrlStr = xslSecondaryStylesheetAbsUrl.toString();			    	                

			    	DocumentBuilderFactory xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			    	xmlDocumentBuilderFactory.setNamespaceAware(true);

			    	DocumentBuilder xmlDocumentBuilder = xmlDocumentBuilderFactory.newDocumentBuilder();

			    	Document document = xmlDocumentBuilder.parse(new InputSource(new StringReader(xmlInpDocStr)));

			    	TransformerFactory xslTransformerFactory = TransformerFactory.newInstance();
			    	Transformer transformer = xslTransformerFactory.newTransformer(new StreamSource(xslStylesheetUrlStr));

			    	DOMResult domResult = new DOMResult();    			    	
			    	transformer.transform(new DOMSource(document), domResult);

			    	DTMManager dtmManager = xctxt.getDTMManager();
			    	DTM dtmResult = dtmManager.getDTM(new DOMSource(domResult.getNode()), true, null, false, false);
			    	int resultDocHandle = dtmResult.getDocument();			    	
			    	XMLNodeCursorImpl xdmNodeCursorImpl = new XMLNodeCursorImpl(resultDocHandle, dtmManager); 

			    	if ((deliverFormatStrValue == null) || DOCUMENT.equals(deliverFormatStrValue)) {
			    		if (baseOutputUriStrValue != null) {
			    			result.put(new XSString(baseOutputUriStrValue), xdmNodeCursorImpl);	
			    		}
			    		else {
			    			result.put(new XSString(OUTPUT), xdmNodeCursorImpl);
			    		}
			    	}
			    	else if (SERIALIZED.equals(deliverFormatStrValue)) {
			    		DTMCursorIterator dtmIter = xdmNodeCursorImpl.iterRaw();
			    		int nodeHandle = dtmIter.nextNode();
			    		DTM dtm1 = xctxt.getDTM(nodeHandle);
			    		Node node = dtm1.getNode(nodeHandle);
			    		String xmlStrValue = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
			    		if (baseOutputUriStrValue != null) {
			    			result.put(new XSString(baseOutputUriStrValue), new XSString(xmlStrValue));	
			    		}
			    		else {
			    			result.put(new XSString(OUTPUT), new XSString(xmlStrValue));
			    		}
			    	}
			    	else if (RAW.equals(deliverFormatStrValue)) {
			    		// TO DO
			    	}
			    }
			    catch (Exception ex) {
			    	throw new javax.xml.transform.TransformerException("FODC0005 : An error occured while evaluating "
			    			                                                   + "fn:transform function. Following exception occured : " + 
			    			                                                   ex.getMessage() + ".", srcLocator);
			    }
		   }
		   else if (xslStylesheetNodeObj != null) {
			    // XPath function fn:transform's XSL transformation, using XSL stylesheet 
			    // information provided by fn:transform's map key 'stylesheet-node'.
			   
			    try  {
			    	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)sourceInpDocNodeObj;
			    	DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
			    	int xmlInpNodeHandle = dtmCursorIter.nextNode();
			    	DTM dtm = xctxt.getDTM(xmlInpNodeHandle);			    
			    	Node xmlInpDocNode = dtm.getNode(xmlInpNodeHandle);

			    	XMLNodeCursorImpl xslNodeCursorImpl = (XMLNodeCursorImpl)xslStylesheetNodeObj;
			    	dtmCursorIter = xslNodeCursorImpl.iterRaw();
			    	int xslNodeHandle = dtmCursorIter.nextNode();
			    	dtm = xctxt.getDTM(xslNodeHandle);			    
			    	Node xslDocNode = dtm.getNode(xslNodeHandle);

			    	TransformerFactory xslTransformerFactory = TransformerFactory.newInstance();
			    	Transformer transformer = xslTransformerFactory.newTransformer(new DOMSource(xslDocNode));

			    	DOMResult domResult = new DOMResult();			    	
			    	transformer.transform(new DOMSource(xmlInpDocNode), domResult);

			    	DTMManager dtmManager = xctxt.getDTMManager();
			    	DTM dtmResult = dtmManager.getDTM(new DOMSource(domResult.getNode()), true, null, false, false);
			    	int resultDocHandle = dtmResult.getDocument();			    	
			    	XMLNodeCursorImpl xdmNodeCursorImpl = new XMLNodeCursorImpl(resultDocHandle, dtmManager); 

			    	if ((deliverFormatStrValue == null) || DOCUMENT.equals(deliverFormatStrValue)) {
			    		if (baseOutputUriStrValue != null) {
			    			result.put(new XSString(baseOutputUriStrValue), xdmNodeCursorImpl);	
			    		}
			    		else {
			    			result.put(new XSString(OUTPUT), xdmNodeCursorImpl);
			    		}
			    	}
			    	else if (SERIALIZED.equals(deliverFormatStrValue)) {
			    		DTMCursorIterator dtmIter = xdmNodeCursorImpl.iterRaw();
			    		int nodeHandle = dtmIter.nextNode();
			    		DTM dtm1 = xctxt.getDTM(nodeHandle);
			    		Node node = dtm1.getNode(nodeHandle);
			    		String xmlStrValue = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
			    		if (baseOutputUriStrValue != null) {
			    			result.put(new XSString(baseOutputUriStrValue), new XSString(xmlStrValue));	
			    		}
			    		else {
			    			result.put(new XSString(OUTPUT), new XSString(xmlStrValue));
			    		}		    		
			    	}
			    	else if (RAW.equals(deliverFormatStrValue)) {
			    		// TO DO
			    	}
			    }
		    	catch (Exception ex) {
	    			throw new javax.xml.transform.TransformerException("FODC0005 : An error occured while evaluating fn:transform "
	    					                                                   + "function. Following exception occured : " + 
	    					                                                   ex.getMessage() + ".", srcLocator);
	    		}
		   }
           else {
        	   // XPath function fn:transform's XSL transformation, using XSL stylesheet 
			   // information provided by fn:transform's map key 'stylesheet-text'.
        	   
        	   try {
        		   String xslStylesheetStrValue = XslTransformEvaluationHelper.getStrVal(xslStylesheetTxtObj);

        		   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)sourceInpDocNodeObj;
        		   DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
        		   int xmlInpNodeHandle = dtmCursorIter.nextNode();
        		   DTM dtm = xctxt.getDTM(xmlInpNodeHandle);			    
        		   Node xmlInpDocNode = dtm.getNode(xmlInpNodeHandle);               

        		   DocumentBuilderFactory xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        		   xmlDocumentBuilderFactory.setNamespaceAware(true);
        		   
        		   DocumentBuilder xmlDocumentBuilder = xmlDocumentBuilderFactory.newDocumentBuilder();
        		   Document document = xmlDocumentBuilder.parse(new InputSource(new StringReader(xslStylesheetStrValue)));

        		   TransformerFactory xslTransformerFactory = TransformerFactory.newInstance();
        		   Transformer transformer = xslTransformerFactory.newTransformer(new DOMSource(document));

        		   DOMResult domResult = new DOMResult();		        
        		   transformer.transform(new DOMSource(xmlInpDocNode), domResult);

        		   DTMManager dtmManager = xctxt.getDTMManager();
        		   DTM dtmResult = dtmManager.getDTM(new DOMSource(domResult.getNode()), true, null, false, false);
        		   int resultDocHandle = dtmResult.getDocument();			    	
        		   XMLNodeCursorImpl xdmNodeCursorImpl = new XMLNodeCursorImpl(resultDocHandle, dtmManager); 

        		   if ((deliverFormatStrValue == null) || DOCUMENT.equals(deliverFormatStrValue)) {
        			   if (baseOutputUriStrValue != null) {
        				   result.put(new XSString(baseOutputUriStrValue), xdmNodeCursorImpl);	
        			   }
        			   else {
        				   result.put(new XSString(OUTPUT), xdmNodeCursorImpl);
        			   }
        		   }
        		   else if (SERIALIZED.equals(deliverFormatStrValue)) {
        			   DTMCursorIterator dtmIter = xdmNodeCursorImpl.iterRaw();
        			   int nodeHandle = dtmIter.nextNode();
        			   DTM dtm1 = xctxt.getDTM(nodeHandle);
        			   Node node = dtm1.getNode(nodeHandle);
        			   String xmlStrValue = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
        			   if (baseOutputUriStrValue != null) {
        				   result.put(new XSString(baseOutputUriStrValue), new XSString(xmlStrValue));	
        			   }
        			   else {
        				   result.put(new XSString(OUTPUT), new XSString(xmlStrValue));
        			   }        			   
        		   }
        		   else if (RAW.equals(deliverFormatStrValue)) {
        			   // TO DO
        		   }
		       } 
		       catch (Exception ex) {            
		    	   throw new javax.xml.transform.TransformerException("FODC0005 : An error occured while evaluating "
																	          + "fn:transform function. Following exception occured : " + 
		    			                                                      ex.getMessage() + ".", srcLocator);
		       }
		   }		   		   		   
		}
		
		ElemResultDocument.m_BaseOutputUriStrAbsValue = null;

		return result;
	}

	/**
	 * Function definition to get absolute url object value, given either a relative
	 * url or an absolute url string value as argument.
	 */
	private URL getAbsoluteUrlValue(String hrefStrVal, SourceLocator srcLocator) throws TransformerException {		
		
		URL absUrlValue = null;

		try {
			URI xslStylesheetUri = new URI(hrefStrVal);        	

			if (xslStylesheetUri.isAbsolute()) {
				absUrlValue = new URL(hrefStrVal); 
			}
			else {
				String stylesheetSystemId = null;

				if (srcLocator != null) {
					stylesheetSystemId = srcLocator.getSystemId();
				}
				else {
					ExpressionNode expressionNode = getExpressionOwner();
					stylesheetSystemId = expressionNode.getSystemId(); 
				}

				if (stylesheetSystemId != null) {
					URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
					absUrlValue = resolvedUriArg.toURL();
				}
				else {
					absUrlValue = new URL(hrefStrVal);
				}
			}
		}
		catch (URISyntaxException ex) {
			throw new javax.xml.transform.TransformerException("FODC0005 : The uri '" + hrefStrVal + "' is not a valid absolute uri, "
																									+ "or cannot be resolved to an absolute uri.", srcLocator); 
		}
		catch (MalformedURLException ex) {
			throw new javax.xml.transform.TransformerException("FODC0005 : The uri '" + hrefStrVal + "' is not a valid absolute uri, "
																									+ "or cannot be resolved to an absolute uri.", srcLocator); 
		}
		
		return absUrlValue;
		
	}
	
}
