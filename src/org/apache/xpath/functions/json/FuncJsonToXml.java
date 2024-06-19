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
package org.apache.xpath.functions.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XPath 3.1 function, json-to-xml().
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncJsonToXml extends FunctionMultiArgs
{
	
	private static final long serialVersionUID = 945183900907386647L;
	
	private static final List<String> OPTIONS_SUPPORTED_LIST = new ArrayList<String>();
	
    /**
     * Class constructor.
     */
    public FuncJsonToXml() {
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.LIBERAL);
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.DUPLICATES);
    }

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
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:json-to-xml needs to have "
           		                                                      + "at-least one argument.", srcLocator);
        }
        else if (m_arg2 != null) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:json-to-xml can "
           		                                                      + "have either 1 or two arguments.", srcLocator);
        }
        
        String jsonStr = null;
        
        if (arg0 instanceof Variable) {
           XObject arg0Obj = ((Variable)arg0).execute(xctxt);
           jsonStr = XslTransformEvaluationHelper.getStrVal(arg0Obj); 
        }
        else {
           XObject arg0Obj = arg0.execute(xctxt);
           jsonStr = XslTransformEvaluationHelper.getStrVal(arg0Obj);
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
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:json-to-xml's optional 2nd "
        	  		                                                     + "argument should be a map, that specifies options for "
        	  		                                                     + "the function call fn:json-to-xml.", srcLocator); 
           }
           else {
        	  optionsMap = (XPathMap)arg1Obj;
        	  
        	  Map<XObject, XObject> optionsNativeMap = optionsMap.getNativeMap();
        	  Set<Entry<XObject,XObject>> optionEntries = optionsNativeMap.entrySet();
        	  Iterator<Entry<XObject,XObject>> optionsIter = optionEntries.iterator();
        	  String optionDuplicatesValStr = null;
        	  while (optionsIter.hasNext()) {
        		 Entry<XObject,XObject> mapEntry = optionsIter.next();
        		 String keyStr = XslTransformEvaluationHelper.getStrVal(mapEntry.getKey());
        		 XObject optionValue = mapEntry.getValue();
        		 if (!OPTIONS_SUPPORTED_LIST.contains(keyStr)) {
        			throw new javax.xml.transform.TransformerException("FOUT1190 : An option '" + keyStr + "' used during "
        					                                       + "function call fn:json-to-xml, is not supported. "
        					                                       + "This implementation supports, only the options 'liberal' & "
        					                                       + "'duplicates' for the function fn:json-to-xml.", srcLocator); 
        		 }
        		 else if (XSLJsonConstants.LIBERAL.equals(keyStr)) {
        			if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBooleanStatic)) {
        			   boolean liberalVal = optionValue.bool();
        			   if (liberalVal) {
        				  throw new javax.xml.transform.TransformerException("FOUT1190 : An implementation for function fn:json-to-xml, doesn't "
        				  		                                         + "support an option liberal=true. An input JSON string to function "
        				  		                                         + "fn:json-to-xml, should strictly conform to the JSON syntax rules, "
        				  		                                         + "as specified by RFC 7159.", srcLocator);  
        			   }
        			}
        			else {
        			   throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:json-to-xml option "
        			   		                                             + "\"liberal\"'s value is not of type xs:boolean.", 
        			   		                                             srcLocator);
        			}
        		 }
        		 else if (XSLJsonConstants.DUPLICATES.equals(keyStr)) {
        			optionDuplicatesValStr = XslTransformEvaluationHelper.getStrVal(optionValue);
        			if (!(XSLJsonConstants.DUPLICATES_REJECT.equals(optionDuplicatesValStr) || 
        				  XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr) || 
        				  XSLJsonConstants.DUPLICATES_RETAIN.equals(optionDuplicatesValStr))) {
        				throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:json-to-xml option "
                                                                        + "\"duplicates\"'s value is not one of following : 'reject', 'use-first', "
                                                                        + "'retain'.", srcLocator);
        			}
        			else if (XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr) || 
        					XSLJsonConstants.DUPLICATES_RETAIN.equals(optionDuplicatesValStr)) {
        				throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:json-to-xml option \"duplicates\"'s value "
        						                                        + "'use-first', or 'retain' is not supported. There should be no duplicate "
        						                                        + "keys within an input JSON document.", srcLocator);
        			}
        		 }
        	  }
           }
        }
        
        Object jsonObj = null;
        try {
           if (jsonStr.charAt(0) == '{') {
        	  jsonObj = new JSONObject(jsonStr);
           }
           else if (jsonStr.charAt(0) == '[') {
        	  jsonObj = new JSONArray(jsonStr); 
           }
           else {
        	  throw new javax.xml.transform.TransformerException("FOJS0001 : The string value provided in XPath "
                                                              + "function call fn:json-to-xml's 1st argument is, not a "
                                                              + "correct lexical JSON string. A JSON string can only start with "
                                                              + "characters '{', or '['.", srcLocator); 
           }
        }
        catch (JSONException ex) {
           String jsonParseErrStr = ex.getMessage();
           throw new javax.xml.transform.TransformerException("FOJS0001 : The string value provided in XPath "
           		                                                      + "function call fn:json-to-xml's 1st argument is, not "
           		                                                      + "a correct lexical JSON string. The JSON parser produced following "
           		                                                      + "error: " + jsonParseErrStr + ".", srcLocator);
        }
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder dBuilder = null;		
        try {
		   dBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		   throw new javax.xml.transform.TransformerException("FOJS0001 : An error occured, within an XML parser "
		   		                                                      + "library.", srcLocator);
		}
		
        Document document = dBuilder.newDocument();
        
        constructXmlDom(jsonObj, document, document, null);
        
        Element docElem = document.getDocumentElement();
        docElem.setAttribute("xmlns", FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI);
         
        DTMManager dtmMgr = xctxt.getDTMManager();             
        DTM dtm = dtmMgr.getDTM(new DOMSource(document), true, null, false, false);            
        result = new XNodeSet(dtm.getDocument(), dtmMgr);           
        
        return result;
    }
    
    /**
     * A method to construct an XML DOM object, from an input JSON object.
     * 
     * @param jsonObj        An object that is either of type org.json.JSONObject, or 
     *                       org.json.JSONArray. 
     * @param document       An empty XML DOM object, that needs to be built 
     *                       to a fully populated DOM document node within this method 
     *                       implementation.
     * @param parentNode     An XML DOM node argument, that is needed to construct fully 
     *                       populated XML DOM object, via recursive calls to this method.
     * @param keyVal         This function argument is needed, to be able to properly create
     *                       certain XML nodes within produced XML DOM object by this method.
     *                                             
     * @return               void
     */
    private void constructXmlDom(Object jsonObj, Document document, Node parentNode, String keyVal) {
    	
    	if (jsonObj instanceof JSONObject) {
    		Element mapElem = document.createElement(XSLJsonConstants.MAP);
    		parentNode.appendChild(mapElem);
    		
    		if (keyVal != null) {
    		   mapElem.setAttribute(XSLJsonConstants.KEY, keyVal);	
    		}
    		
	    	Iterator<String> jsonKeys = ((JSONObject)jsonObj).keys();	    	
	    	while (jsonKeys.hasNext()) {
	      	   String key = jsonKeys.next();
	      	   Object value = ((JSONObject)jsonObj).get(key);        	  
	      	   if (value instanceof String) {
	      		 Element strElem = document.createElement(XSLJsonConstants.STRING);
	      		 strElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode((String)value);
	      		 strElem.appendChild(text);
	      		 mapElem.appendChild(strElem);
	      	   }
	      	   else if (value instanceof Number) {
	      		 Element numberElem = document.createElement(XSLJsonConstants.NUMBER);
	      		 numberElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode(value.toString());
	      		 numberElem.appendChild(text);
	      		 mapElem.appendChild(numberElem);
	      	   }
	      	   else if (value instanceof Boolean) {
	      		 Element boolElem = document.createElement(XSLJsonConstants.BOOLEAN);
	      		 boolElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode(value.toString());
	      		 boolElem.appendChild(text);
	      		 mapElem.appendChild(boolElem);
	      	   }	      	   
	      	   else if (value instanceof JSONArray) {
	      		  Element arrayElem = document.createElement(XSLJsonConstants.ARRAY);
	      		  arrayElem.setAttribute(XSLJsonConstants.KEY, key);
	      		  mapElem.appendChild(arrayElem);
	      		  JSONArray jsonArr = (JSONArray)value;
	      		  int arrLen = jsonArr.length();
	    		  for (int idx = 0; idx < arrLen; idx++) {
	    			  Object arrItem = jsonArr.get(idx);
	    			  if (arrItem instanceof String) {
	    				  Element strElem = document.createElement(XSLJsonConstants.STRING);
	    				  Text text = document.createTextNode((String)arrItem);
	    				  strElem.appendChild(text);
	    				  arrayElem.appendChild(strElem);	 
	    			  }
	    			  else if (arrItem instanceof Number) {
	    				  Element numberElem = document.createElement(XSLJsonConstants.NUMBER);
	    				  Text text = document.createTextNode(arrItem.toString());
	    				  numberElem.appendChild(text);
	    				  arrayElem.appendChild(numberElem); 
	    			  }
	    			  else if (arrItem instanceof Boolean) {
	    				  Element boolElem = document.createElement(XSLJsonConstants.BOOLEAN);
	    				  Text text = document.createTextNode(arrItem.toString());
	    				  boolElem.appendChild(text);
	    				  arrayElem.appendChild(boolElem);  
	    			  }
	    			  else if (arrItem instanceof JSONObject) {
	    				  // Recursive call to this function
	    				  constructXmlDom(arrItem, document, arrayElem, null);
	    			  }
	    			  else if (arrItem instanceof JSONArray) {
	    				  // Recursive call to this function
	    				  constructXmlDom(arrItem, document, arrayElem, null);
	    			  }
	    		  }
	      	   }
	      	   else if (value instanceof JSONObject) {
	      		  // Recursive call to this function
	      		  constructXmlDom(value, document, mapElem, key); 
	      	   }
	      	   else if (JSONObject.NULL.equals(value)) {
	      		  Element nullElem = document.createElement("null");
	      		  nullElem.setAttribute(XSLJsonConstants.KEY, key);
	      		  mapElem.appendChild(nullElem);
	      	   }
	        }	    		    	
    	}
    	else if (jsonObj instanceof JSONArray) {
    		Element arrayElem = document.createElement(XSLJsonConstants.ARRAY);
     		parentNode.appendChild(arrayElem);
     		
     		if (keyVal != null) {
     		   arrayElem.setAttribute(XSLJsonConstants.KEY, keyVal);	
     		}
    		
    		JSONArray jsonArr = (JSONArray)jsonObj;	      		  
    		int arrLen = jsonArr.length();
    		for (int idx = 0; idx < arrLen; idx++) {
    		   Object arrItem = jsonArr.get(idx);
    		   if (arrItem instanceof String) {
    			  Element strElem = document.createElement(XSLJsonConstants.STRING);
  	      		  Text text = document.createTextNode((String)arrItem);
  	      		  strElem.appendChild(text);
  	      		  arrayElem.appendChild(strElem);	 
    		   }
    		   else if (arrItem instanceof Number) {
    			  Element numberElem = document.createElement(XSLJsonConstants.NUMBER);
   	      		  Text text = document.createTextNode(arrItem.toString());
   	      		  numberElem.appendChild(text);
   	      		  arrayElem.appendChild(numberElem); 
    		   }
    		   else if (arrItem instanceof Boolean) {
    			  Element boolElem = document.createElement(XSLJsonConstants.BOOLEAN);
    	          Text text = document.createTextNode(arrItem.toString());
    	          boolElem.appendChild(text);
    	          arrayElem.appendChild(boolElem);  
    		   }
    		   else if (arrItem instanceof JSONObject) {
    			  // Recursive call to this function
      		      constructXmlDom(arrItem, document, arrayElem, null);
      		   }
    		   else if (arrItem instanceof JSONArray) {
    			  // Recursive call to this function
    		      constructXmlDom(arrItem, document, arrayElem, null);
    		   }    		   
    		}
    	}    	
    }
    
}
