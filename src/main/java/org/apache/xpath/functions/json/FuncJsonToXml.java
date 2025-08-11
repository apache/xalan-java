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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function, fn:json-to-xml.
 * 
 * The result of the function call fn:json-to-xml,
 * is an XML document conforming to an XML Schema document
 * specified at : https://www.w3.org/TR/xpath-functions-31/#schema-for-json.
 * 
 * (It's also useful to refer, to following json RFC : 
 * https://datatracker.ietf.org/doc/html/rfc7159)
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncJsonToXml extends JsonFunction
{
	
	private static final long serialVersionUID = 945183900907386647L;
	
	private static final List<String> OPTIONS_SUPPORTED_LIST = new ArrayList<String>();
	
    /**
     * Class constructor.
     */
    public FuncJsonToXml() {
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.LIBERAL);
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.DUPLICATES);
       OPTIONS_SUPPORTED_LIST.add(XSLJsonConstants.VALIDATE);
       
       m_defined_arity = new Short[] { 1, 2 };
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
           		                                                                                              + "have either one or two arguments.", srcLocator);
        }
        
        String jsonStr = null;
        
        if (arg0 instanceof Variable) {
           XObject arg0Obj = ((Variable)arg0).execute(xctxt);
           jsonStr = (XslTransformEvaluationHelper.getStrVal(arg0Obj)).trim(); 
        }
        else {
           XObject arg0Obj = arg0.execute(xctxt);
           jsonStr = (XslTransformEvaluationHelper.getStrVal(arg0Obj)).trim();
        }
                 
        XPathMap optionsMap = null;
        
        // The following are default values, in absence of fn:json-to-xml 
  	    // options argument.
  	    boolean optionIsLiberal = false;        	          	          	          	  
  	    String optionDuplicatesValStr = XSLJsonConstants.DUPLICATES_RETAIN;
  	    boolean optionValidate = XSLJsonConstants.VALIDATE_FALSE;
        
        if (arg1 != null) {
           XObject arg1Obj = null;
           if (arg1 instanceof Variable) {
        	  arg1Obj = ((Variable)arg1).execute(xctxt);               
           }
           else {
        	  arg1Obj = arg1.execute(xctxt);                
           }
           
           if (!(arg1Obj instanceof XPathMap)) {
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath function fn:json-to-xml's optional second "
									        	  		                                                       + "argument should be a map, that specifies options for "
									        	  		                                                       + "the function call fn:json-to-xml.", srcLocator); 
           }
           else {
        	  optionsMap = (XPathMap)arg1Obj;        	          	       
        	  
        	  Map<XObject, XObject> optionsNativeMap = optionsMap.getNativeMap();
        	  Set<Entry<XObject,XObject>> optionEntries = optionsNativeMap.entrySet();
        	  Iterator<Entry<XObject,XObject>> optionsIter = optionEntries.iterator();
        	  while (optionsIter.hasNext()) {
        		 Entry<XObject,XObject> mapEntry = optionsIter.next();
        		 String keyStr = XslTransformEvaluationHelper.getStrVal(mapEntry.getKey());
        		 XObject optionValue = mapEntry.getValue();
        		 if (!OPTIONS_SUPPORTED_LIST.contains(keyStr)) {
        			throw new javax.xml.transform.TransformerException("FOUT1190 : An option '" + keyStr + "' used for the "
											        					                                       + "function call fn:json-to-xml, is not supported. "
											        					                                       + "This implementation supports, only the options 'liberal', "
											        					                                       + "'duplicates' and 'validate' for the function fn:json-to-xml.", srcLocator); 
        		 }
        		 else if (XSLJsonConstants.LIBERAL.equals(keyStr)) {
        			if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBoolean) 
        					                               || (optionValue instanceof XBooleanStatic)) {
        				optionIsLiberal = optionValue.bool();        			   
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
        		 }
        		 else if (XSLJsonConstants.VALIDATE.equals(keyStr)) {
        			 if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBoolean) 
        					                                || (optionValue instanceof XBooleanStatic)) {
        				 optionValidate = optionValue.bool();        			   
        			 }
        			 else {
        				 throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:json-to-xml option "
																				        						 + "\"validate\"'s value is not of type xs:boolean.", 
																				        						 srcLocator);
        			 } 
        		 }
        	  }
        	  
        	  if (optionValidate && (XSLJsonConstants.DUPLICATES_RETAIN).equals(optionDuplicatesValStr)) {
        		 throw new javax.xml.transform.TransformerException("FOUT1190 : The function fn:json-to-xml option values duplicates : 'retain' "
        		 		                                                                                         + "and validate : 'true' are incompatible.", srcLocator); 
        	  }
           }
        }
        
        Object jsonObj = null;
        
        JSONParserConfiguration jsonParserConf = getJsonParserConfiguration(optionIsLiberal, optionDuplicatesValStr);
        
        try {
           if (jsonStr.charAt(0) == '{') {
        	  jsonObj = new JSONObject(jsonStr, jsonParserConf);
           }
           else if (jsonStr.charAt(0) == '[') {
        	  jsonObj = new JSONArray(jsonStr, jsonParserConf); 
           }
           else {
        	  throw new javax.xml.transform.TransformerException("FOJS0001 : The string value provided in XPath function call fn:json-to-xml's 1st argument is, not a "
													                                                             + "correct lexical JSON string. A JSON string can only start with "
													                                                             + "characters '{', or '['.", srcLocator); 
           }
        }
        catch (JSONException ex) {
           String jsonParseErrStr = ex.getMessage();
           throw new javax.xml.transform.TransformerException("FOJS0001 : The string value provided in XPath function call fn:json-to-xml's 1st argument is, not "
											           		                                                     + "a correct lexical JSON string. The JSON parser produced following "
											           		                                                     + "error: " + jsonParseErrStr + ".", srcLocator);
        }
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder dBuilder = null;		
        try {
		   dBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		   throw new javax.xml.transform.TransformerException("FOJS0001 : A run-time error has occured, within an XML parser library implementation.", srcLocator);
		}
		
        Document document = dBuilder.newDocument();
        
        constructXmlDom(jsonObj, document, document, null);
        
        if ((XSLJsonConstants.DUPLICATES_RETAIN).equals(optionDuplicatesValStr)) {
           mutateXmlDom(document.getDocumentElement());
        }
         
        DTMManager dtmMgr = xctxt.getDTMManager();
        int dtmHandleOfResultNode = dtmMgr.getDTMHandleFromNode(document);           
        result = new XMLNodeCursorImpl(dtmHandleOfResultNode, dtmMgr);           
        
        return result;
    }
    
    /**
     * Method definition, to mutate XML DOM object for xdm map's
     * duplicate key resolution option 'retain'.
     * 
     * @param currElem  XML document's context element that needs
     *                  to be mutated.
     */
    private void mutateXmlDom(Element currElem) {
        String nodeName = currElem.getNodeName();      
        if ("map".equals(nodeName)) {
        	List<SortableXmlDomElement> mapElemChildList = new ArrayList<SortableXmlDomElement>();
        	Node node = currElem.getFirstChild();
        	while (node != null) {
        		Element elem = (Element)node;
        		String keyStr = elem.getAttribute("key");
        		int i = keyStr.indexOf('_');
        		int keyStrSeqNo = Integer.valueOf(keyStr.substring(0, i));
        		elem.removeAttribute("key");
        		elem.setAttribute("key", keyStr.substring(i + 1));        		
        		mapElemChildList.add(new SortableXmlDomElement(keyStrSeqNo, elem));
        		
        		node = node.getNextSibling();
        	}

        	mapElemChildList.sort(null);                      

        	NodeList nodeList = currElem.getChildNodes();
        	for (int idx = 0; idx < nodeList.getLength(); idx++) {
        		Node node1 = nodeList.item(idx);
        		currElem.removeChild(node1);
        	}           

        	for (int idx = 0; idx < mapElemChildList.size(); idx++) {
        		Element elem2 = (mapElemChildList.get(idx)).getElem();
        		mutateXmlDom(elem2);        		
        		currElem.appendChild(elem2);
        	}        	        	        
        }
        else if ("array".equals(nodeName)) {
        	Node node = currElem.getFirstChild();
        	while (node != null) {
        		Element elem = (Element)node;
        		String nodeName2 = elem.getNodeName();
        		if ("map".equals(nodeName2)) {
        			List<SortableXmlDomElement> mapElemChildList = new ArrayList<SortableXmlDomElement>();
                	Node node2 = elem.getFirstChild();
                	while (node2 != null) {
                		Element elem2 = (Element)node2;
                		String keyStr = elem2.getAttribute("key");
                		int i = keyStr.indexOf('_');
                		int keyStrSeqNo = Integer.valueOf(keyStr.substring(0, i));
                		elem2.removeAttribute("key");
                		elem2.setAttribute("key", keyStr.substring(i + 1));        		
                		mapElemChildList.add(new SortableXmlDomElement(keyStrSeqNo, elem2));
                		
                		node2 = node2.getNextSibling();
                	}

                	mapElemChildList.sort(null);
                	
                	NodeList nodeList = elem.getChildNodes();
                	for (int idx = 0; idx < nodeList.getLength(); idx++) {
                		Node node1 = nodeList.item(idx);
                		elem.removeChild(node1);
                	}           

                	for (int idx = 0; idx < mapElemChildList.size(); idx++) {
                		Element elem3 = (mapElemChildList.get(idx)).getElem();
                		mutateXmlDom(elem3);
                		elem.appendChild(elem3);
                	}
        		}
        		
        		node = node.getNextSibling();
        	}
        }
    }
    
    /**
     * Class definition, to support xdm map's duplicate key
     * resolution option 'retain'.
     */
    class SortableXmlDomElement implements Comparable {    	
    	
    	private int m_idx;
    	
    	private Element m_elem;
    	
    	public SortableXmlDomElement(int idx, Element elem) {
    		m_idx = idx;
    		m_elem = elem;
    	}

		public int getIdx() {
			return m_idx;
		}

		public void setIdx(int idx) {
			this.m_idx = idx;
		}

		public Element getElem() {
			return m_elem;
		}

		public void setElem(Element elem) {
			this.m_elem = elem;
		}

		@Override
		public int compareTo(Object obj2) {
			int result = 0;
			
			SortableXmlDomElement sortableXmlDomElem2 = (SortableXmlDomElement)obj2;
			int idx2 = sortableXmlDomElem2.getIdx();
			if (m_idx < idx2) {
			   result = -1;	
			}
			else if (m_idx > idx2) {
			   result = 1;	
		    }
			
			return result;
		}
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
    		Element mapElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.MAP);
    		parentNode.appendChild(mapElem);    		
    		if (keyVal != null) {
    		   mapElem.setAttribute(XSLJsonConstants.KEY, keyVal);	
    		}
    		
	    	Iterator<String> jsonKeys = ((JSONObject)jsonObj).keys();	    	
	    	while (jsonKeys.hasNext()) {
	      	   String key = jsonKeys.next();
	      	   Object value = ((JSONObject)jsonObj).get(key);
	      	   
	      	   if (value instanceof String) {
	      		 Element strElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.STRING);
	      		 strElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode((String)value);
	      		 strElem.appendChild(text);
	      		 mapElem.appendChild(strElem);
	      	   }
	      	   else if (value instanceof Number) {
	      		 Element numberElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NUMBER);
	      		 numberElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode(value.toString());
	      		 numberElem.appendChild(text);
	      		 mapElem.appendChild(numberElem);
	      	   }
	      	   else if (value instanceof Boolean) {
	      		 Element boolElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.BOOLEAN);
	      		 boolElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode(value.toString());
	      		 boolElem.appendChild(text);
	      		 mapElem.appendChild(boolElem);
	      	   }	      	   
	      	   else if (value instanceof JSONArray) {
	      		  Element arrayElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.ARRAY);
	      		  arrayElem.setAttribute(XSLJsonConstants.KEY, key);
	      		  mapElem.appendChild(arrayElem);
	      		  JSONArray jsonArr = (JSONArray)value;
	      		  int arrLen = jsonArr.length();
	    		  for (int idx = 0; idx < arrLen; idx++) {
	    			  Object arrItem = jsonArr.get(idx);
	    			  if (arrItem instanceof String) {
	    				  Element strElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.STRING);
	    				  Text text = document.createTextNode((String)arrItem);
	    				  strElem.appendChild(text);
	    				  arrayElem.appendChild(strElem);	 
	    			  }
	    			  else if (arrItem instanceof Number) {
	    				  Element numberElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NUMBER);
	    				  Text text = document.createTextNode(arrItem.toString());
	    				  numberElem.appendChild(text);
	    				  arrayElem.appendChild(numberElem); 
	    			  }
	    			  else if (arrItem instanceof Boolean) {
	    				  Element boolElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.BOOLEAN);
	    				  Text text = document.createTextNode(arrItem.toString());
	    				  boolElem.appendChild(text);
	    				  arrayElem.appendChild(boolElem);  
	    			  }
	    			  else if (JSONObject.NULL.equals(arrItem)) {
	         			  Element nullElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NULL);
	         	          arrayElem.appendChild(nullElem);  
	         		  }
	    			  else if (arrItem instanceof JSONObject) {
	    				  constructXmlDom(arrItem, document, arrayElem, null);
	    			  }
	    			  else if (arrItem instanceof JSONArray) {
	    				  constructXmlDom(arrItem, document, arrayElem, null);
	    			  }
	    		  }
	      	   }	      	   
	      	   else if (value instanceof JSONObject) {
	      		  constructXmlDom(value, document, mapElem, key); 
	      	   }
	      	   else if ((JSONObject.NULL).equals(value)) {
	      		  Element nullElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, "null");
	      		  nullElem.setAttribute(XSLJsonConstants.KEY, key);
	      		  mapElem.appendChild(nullElem);
	      	   }
	        }	    		    	
    	}
    	else if (jsonObj instanceof JSONArray) {
    		Element arrayElem = null;
    		if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
    		   arrayElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.ARRAY);
     		}
     		else {
     		   arrayElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.ARRAY);
     		}
    		
     		parentNode.appendChild(arrayElem);
     		
     		if (keyVal != null) {
     		   arrayElem.setAttribute(XSLJsonConstants.KEY, keyVal);	
     		}
    		
    		JSONArray jsonArr = (JSONArray)jsonObj;	      		  
    		int arrLen = jsonArr.length();
    		for (int idx = 0; idx < arrLen; idx++) {
    		   Object arrItem = jsonArr.get(idx);
    		   if (arrItem instanceof String) {
    			  Element strElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.STRING);
  	      		  Text text = document.createTextNode((String)arrItem);
  	      		  strElem.appendChild(text);
  	      		  arrayElem.appendChild(strElem);	 
    		   }
    		   else if (arrItem instanceof Number) {
    			  Element numberElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NUMBER);
   	      		  Text text = document.createTextNode(arrItem.toString());
   	      		  numberElem.appendChild(text);
   	      		  arrayElem.appendChild(numberElem); 
    		   }
    		   else if (arrItem instanceof Boolean) {
    			  Element boolElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.BOOLEAN);
    	          Text text = document.createTextNode(arrItem.toString());
    	          boolElem.appendChild(text);
    	          arrayElem.appendChild(boolElem);  
    		   }
    		   else if ((JSONObject.NULL).equals(arrItem)) {
     			  Element nullElem = document.createElementNS(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NULL);
     	          arrayElem.appendChild(nullElem);  
     		   }
    		   else if (arrItem instanceof JSONObject) {
      		      constructXmlDom(arrItem, document, arrayElem, null);
      		   }
    		   else if (arrItem instanceof JSONArray) {
    		      constructXmlDom(arrItem, document, arrayElem, null);
    		   }    		   
    		}
    	}    	
    }
    
}
