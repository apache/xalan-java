/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

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
	
    private static final String XSLT_TRANSFORMER_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
    
    private static final String XSLT_TRANSFORMER_FACTORY_VALUE = "org.apache.xalan.processor.XSL3TransformerFactoryImpl";

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
		
		if (!(arg0Obj instanceof XPathMap)) {
			throw new javax.xml.transform.TransformerException("FODC0005 : The 1st argument to function call fn:transform "
					                                                                                  + "is not an XDM map.", srcLocator);
		}
		else {
		   XPathMap mapArg = (XPathMap)arg0Obj;
		   
		   // An XSL stylesheet input document, for fn:transform function call 
		   // may be provided with one of following ways.
		   XObject xslStylesheetLocObj = mapArg.get(new XSString("stylesheet-location"));
		   XObject xslStylesheetNodeObj = mapArg.get(new XSString("stylesheet-node"));
		   XObject xslStylesheetTxtObj = mapArg.get(new XSString("stylesheet-text"));
		   
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
		   
           XObject sourceInpDocNodeObj = mapArg.get(new XSString("source-node"));
		   
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
		   else if (xslStylesheetLocObj != null) {
			   String hrefStrVal = XslTransformEvaluationHelper.getStrVal(xslStylesheetLocObj);
			   
			   URL xslStylesheetResolvedUrl = null;
			   
			   try {
		        	URI xslStylesheetUri = new URI(hrefStrVal);        	

		        	if (xslStylesheetUri.isAbsolute()) {
		        		xslStylesheetResolvedUrl = new URL(hrefStrVal); 
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
		        			xslStylesheetResolvedUrl = resolvedUriArg.toURL();
		        		}
		        		else {
		        			xslStylesheetResolvedUrl = new URL(hrefStrVal);
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
			   
			    // Perform XPath function fn:transform's XSL transformation.
			   
			    XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)sourceInpDocNodeObj;
			    DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iterRaw();
			    int xmlInpNodeHandle = dtmCursorIter.nextNode();
			    DTM dtm = xctxt.getDTM(xmlInpNodeHandle);			    
			    Node xmlInpDocNode = dtm.getNode(xmlInpNodeHandle);
			    
			    try {
			    	String xmlInpDocStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(xmlInpDocNode);			    
			    	String xslStylesheetUrlStr = xslStylesheetResolvedUrl.toString();

			    	System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
			    	System.setProperty(XSLT_TRANSFORMER_FACTORY_KEY, XSLT_TRANSFORMER_FACTORY_VALUE);                

			    	DocumentBuilderFactory xmlDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			    	xmlDocumentBuilderFactory.setNamespaceAware(true);

			    	DocumentBuilder xmlDocumentBuilder = null;
			    	try {
			    		xmlDocumentBuilder = xmlDocumentBuilderFactory.newDocumentBuilder();
			    	} catch (ParserConfigurationException ex) {            
			    		ex.printStackTrace();
			    	}

			    	Document document = xmlDocumentBuilder.parse(new InputSource(new StringReader(xmlInpDocStr)));

			    	TransformerFactory xslTransformerFactory = TransformerFactory.newInstance();
			    	Transformer transformer = xslTransformerFactory.newTransformer(new StreamSource(xslStylesheetUrlStr));

			    	DOMResult domResult = new DOMResult();		        
			    	transformer.transform(new DOMSource(document), domResult);
			    	
			    	DTMManager dtmManager = xctxt.getDTMManager();
			    	DTM dtmResult = dtmManager.getDTM(new DOMSource(domResult.getNode()), true, null, false, false);
			    	int resultDocHandle = dtmResult.getDocument();			    	
			    	XMLNodeCursorImpl xdmNodeCursorImpl = new XMLNodeCursorImpl(resultDocHandle, dtmManager); 
			    	
			    	result.put(new XSString("output"), xdmNodeCursorImpl);
			    }
			    catch (Exception ex) {
			    	throw new javax.xml.transform.TransformerException("FODC0005 : An error occured while evaluating "
			    			                                                                     + "fn:transform function. Following exception occured : " + 
			    			                                                                         ex.getMessage() + ".", srcLocator);
			    }
		   }
		   else if (xslStylesheetNodeObj != null) {
			    // Perform XPath function fn:transform's XSL transformation.
			   
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
			    
		    	System.setProperty(XSLT_TRANSFORMER_FACTORY_KEY, XSLT_TRANSFORMER_FACTORY_VALUE);
		    	
		    	TransformerFactory xslTransformerFactory = TransformerFactory.newInstance();
		    	Transformer transformer = xslTransformerFactory.newTransformer(new DOMSource(xslDocNode));

		    	DOMResult domResult = new DOMResult();		        
		    	transformer.transform(new DOMSource(xmlInpDocNode), domResult);
		    	
		    	DTMManager dtmManager = xctxt.getDTMManager();
		    	DTM dtmResult = dtmManager.getDTM(new DOMSource(domResult.getNode()), true, null, false, false);
		    	int resultDocHandle = dtmResult.getDocument();			    	
		    	XMLNodeCursorImpl xdmNodeCursorImpl = new XMLNodeCursorImpl(resultDocHandle, dtmManager); 
		    	
		    	result.put(new XSString("output"), xdmNodeCursorImpl);
		   }
           else {
        	  // To use stylesheet-text's value
        	   
			  // TO DO 
		   }		   		   		   
		}

		return result;
	}
}
