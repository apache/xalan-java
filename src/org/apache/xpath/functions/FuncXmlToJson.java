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

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function, xml-to-json().
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncXmlToJson extends FunctionMultiArgs
{

	private static final long serialVersionUID = -7072675375842927045L;
	
	// Reference within this implementation to an, XML Schema document file 
	// (which specifies an XML schema for, an XML document input to function fn:xml-to-json) 
	// provided by XPath 3.1 F&O spec.
	private static final String XML_JSON_SCHEMA_DOCUMENT = "schema-for-json.xsd";
	
	private static final int JSON_INDENT_FACTOR = 1;

	/**
     * Implementation of the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
    	XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = m_arg0;        
        Expression arg1 = m_arg1;
        
        if ((arg0 == null) && (arg1 == null)) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:xml-to-json needs to have "
           		                                                      + "at-least one argument.", srcLocator);
        }
        else if (m_arg2 != null) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:xml-to-json can "
           		                                                      + "have either 1 or two arguments.", srcLocator);
        }
        
        XNodeSet arg0NodeSet = null;
        
        if (arg0 instanceof Variable) {
           XObject arg0Obj = ((Variable)arg0).execute(xctxt);
           if (arg0Obj instanceof XNodeSet) {
        	  arg0NodeSet = (XNodeSet)arg0Obj;  
           }
           else {
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : The function fn:xml-to-json's 1st argument "
        	  		                                                     + "needs to be an xdm node.", srcLocator); 
           }
        }
        else {
           XObject arg0Obj = arg0.execute(xctxt);
           if (arg0Obj instanceof XNodeSet) {
        	  arg0NodeSet = (XNodeSet)arg0Obj;        	  
           }
           else {
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : The function fn:xml-to-json's 1st argument "
        	  		                                                     + "needs to be an xdm node.", srcLocator); 
           }
        }
        
        XPathMap optionsMap = null;        
        if (arg1 != null) {
           XObject arg1Obj = null;
           if (arg1 instanceof Variable) {
        	  arg1Obj = ((Variable)arg1).execute(xctxt);               
           }
           else {
        	  arg1Obj = arg1.execute(xctxt);                
           }
           
           if (!(arg1Obj instanceof XPathMap)) {
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:xml-to-json's optional 2nd "
        	  		                                                     + "argument should be a map, that specifies options for "
        	  		                                                     + "the function call fn:xml-to-json.", srcLocator); 
           }
           else {
        	  optionsMap = (XPathMap)arg1Obj;    
           }
        }
        
        boolean jsonIndent = false;        
        if (optionsMap != null) {
           Map<XObject, XObject> nativeMap = optionsMap.getNativeMap();
           if (nativeMap.size() != 1) {
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:xml-to-json's 2nd argument, "
        	  		                                                     + "needs to be a map with only one entry having a key "
        	  		                                                     + "named 'indent' with type xs:boolean.", srcLocator);  
           }
           else {
        	  XObject jsonIndentVal = nativeMap.get(new XSString("indent"));
        	  if ((jsonIndentVal != null) && ((jsonIndentVal instanceof XSBoolean) || 
        			                          (jsonIndentVal instanceof XBooleanStatic))) {
        		 jsonIndent = jsonIndentVal.bool();  
        	  }
        	  else {
        		 throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:xml-to-json's 2nd argument, "
                                                                         + "needs to be a map with only one entry having a key "
                                                                         + "named 'indent' with type xs:boolean.", srcLocator); 
        	  }
           }
        }
        
        DTMIterator dtmIter = arg0NodeSet.iterRaw();
   	    int nodeHandle = dtmIter.nextNode();
   	    DTM dtm = xctxt.getDTM(nodeHandle);
   	    Node node = dtm.getNode(nodeHandle);
   	    String xmlDocStr = null;   	    
   	    try {
   	       xmlDocStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
   	       
   	       if (isXmlStrValidWithSchema(xmlDocStr, XML_JSON_SCHEMA_DOCUMENT)) {
   	    	  Object obj = getJsonFromXmlNode(node, null);
   	    	  if (obj instanceof JSONObject) {
   	    		 String jsonStr = jsonIndent ? ((JSONObject)obj).toString(JSON_INDENT_FACTOR) : 
   	    			                                                   ((JSONObject)obj).toString();   
   	    	     result = new XSString(jsonStr);
   	    	  }
   	    	  else {
   	    		 String jsonStr = jsonIndent ? ((JSONArray)obj).toString(JSON_INDENT_FACTOR) : 
   	    			                                                   ((JSONArray)obj).toString();
   	    		 result = new XSString(jsonStr); 
   	    	  }
   	       }
   	    }
   	    catch (javax.xml.transform.TransformerException ex) {
   	       throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator);
   	    }
   	    catch (Exception ex) {
 	       throw new javax.xml.transform.TransformerException("FOAP0001 : There was an issue, processing an xdm input "
 	       		                                            + "node provided as an argument to function call "
 	       		                                            + "fn:xml-to-json.", srcLocator);
 	    }
        
        return result;
    }
    
    /**
     * This function checks, whether an XML document input string is valid according 
     * to XML schema constraints. An XML schema used by this function, is constructed
     * from an XML Schema document file 'schema-for-json.xsd'.
     * 
     * @param xmlStr           An XML document string that needs to be validated by an 
     *                         XML Schema document.
     * @param schemaFileName   XML Schema document's file name
     * 
     * @return             true, or false indicating whether an XML input document is valid
     *                     or invalid when validated by an XML Schema document.
     * @throws javax.xml.transform.TransformerException
     */
    private boolean isXmlStrValidWithSchema(String xmlStr, String schemaFileName) throws 
                                                                       javax.xml.transform.TransformerException {
        boolean isXmlStrValid = false;
         
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        
        try {
			Schema schema = schemaFactory.newSchema(this.getClass().getResource(schemaFileName));
			Validator xmlSchemaValidaor = schema.newValidator();
			StringReader xmlInputStrReader = new StringReader(xmlStr);
			xmlSchemaValidaor.validate(new StreamSource(xmlInputStrReader));
			isXmlStrValid = true;
		} 
        catch (SAXException ex) {
			throw new javax.xml.transform.TransformerException("FOAP0001 : An XML Schema validation of function fn:xml-to-json's "
					                                                  + "1st argument which is an XML node, produced following validation messages : " 
					                                                  + ex.getMessage()); 
		}
        catch (IOException ex) {
        	throw new javax.xml.transform.TransformerException("FOAP0001 : An XML Schema validation of function fn:xml-to-json's "
        			                                                  + "1st argument which is an XML node, produced following validation messages : " 
        			                                                  + ex.getMessage());
        }        
        
        return isXmlStrValid; 
    }
    
    /**
     * This function converts a given XDM node, to a corresponding JSON object.
     * 
     * @param node                   An XDM node, initially provided as an input to this function.
     *                               When this function is initially called, this object has
     *                               reference to the root of an XML document (which is value
     *                               of 1st argument of function fn:xml-to-json).                               
     * @param parentJsonNode         An intermediate JSON node, that supports construction of
     *                               final JSON node returned by this method.
     *                               
     * @return                       An object of type org.json.JSONObject, or org.json.JSONArray                                                            
     *                               
     */
    private Object getJsonFromXmlNode(Node node, Object parentJsonNode) {
    	
    	Object result = null;
        
        short nodeType = node.getNodeType();
        
        if (nodeType == Node.ELEMENT_NODE) {
           String elemName = node.getNodeName();
           if ("map".equals(elemName)) {
        	  JSONObject jsonObj = new JSONObject(); 
        	  NodeList nodeList = node.getChildNodes();
        	  for (int idx = 0; idx < nodeList.getLength(); idx++) {
        		 Node node1 = nodeList.item(idx);
         		 if (node1.getNodeType() == Node.ELEMENT_NODE) {
         			String nodeName = node1.getNodeName();
         			if ("map".equals(nodeName) || "array".equals(nodeName)) {
         			   // Recursive call to this function
         			   Object result1 = getJsonFromXmlNode(node1, jsonObj);
         			   Element elem = (Element)node1;
                       String keyVal = elem.getAttribute("key");
         			   jsonObj.put(keyVal, result1);
         			}
         			else if ("number".equals(nodeName)) {
                       Element elem = (Element)node1;
                       String keyVal = elem.getAttribute("key");
         			   String nodeStrVal = node1.getTextContent();
         			   jsonObj.put(keyVal, Double.valueOf(nodeStrVal));
         			}
         			else if ("string".equals(nodeName)) {
                       Element elem = (Element)node1;
                       String keyVal = elem.getAttribute("key");
          			   String nodeStrVal = node1.getTextContent();
          			   jsonObj.put(keyVal, nodeStrVal);
          			}
         			else if ("boolean".equals(nodeName)) {
                       Element elem = (Element)node1;
                       String keyVal = elem.getAttribute("key");
           			   String nodeStrVal = node1.getTextContent();
           			   boolean boolVal;
                       if ("0".equals(nodeStrVal) || "false".equals(nodeStrVal)) {
                 	      boolVal = false;  
                       }
                       else {
                 	      boolVal = true; 
                       }
           			   jsonObj.put(keyVal, boolVal);
           			}
         			else if ("null".equals(nodeName)) {
         				Element elem = (Element)node1;
                        String keyVal = elem.getAttribute("key");
                        jsonObj.put(keyVal, JSONObject.NULL);
         			}
         		 }
        	  }
        	  
        	  result = jsonObj;
           }
           else if ("array".equals(elemName)) {
        	  JSONArray jsonArr = new JSONArray();
        	  NodeList nodeList = node.getChildNodes();
        	  for (int idx = 0; idx < nodeList.getLength(); idx++) {
        		 Node node1 = nodeList.item(idx);
        		 if (node1.getNodeType() == Node.ELEMENT_NODE) {
        			String nodeName = node1.getNodeName();
        			if ("map".equals(nodeName) || "array".equals(nodeName)) {
        			   // Recursive call to this function
        			   Object result1 = getJsonFromXmlNode(node1, jsonArr);
        			   jsonArr.put(result1);
        			}
        			else if ("number".equals(nodeName)) {
        			   String nodeStrVal = node1.getTextContent();
        			   jsonArr.put(Double.valueOf(nodeStrVal));
        			}
                    else if ("string".equals(nodeName)) {
                       String nodeStrVal = node1.getTextContent();
                       jsonArr.put(nodeStrVal);
        			}
                    else if ("boolean".equals(nodeName)) {
                       String nodeStrVal = node1.getTextContent();
                       boolean boolVal;
                       if ("0".equals(nodeStrVal) || "false".equals(nodeStrVal)) {
                    	  boolVal = false;  
                       }
                       else {
                    	  boolVal = true; 
                       }
                       jsonArr.put(boolVal);
        			}
                    else if ("null".equals(nodeName)) {
                       jsonArr.put(JSONObject.NULL);
        			}
        		 }
        	  }
        	  
        	  result = jsonArr; 
           }
        }        
        
        return result;
    }
    
}
