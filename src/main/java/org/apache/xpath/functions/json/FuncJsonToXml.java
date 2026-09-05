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
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
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
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:json-to-xml.
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
       
       m_arity = new Short[] { 1, 2 };
    }

    /**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
    	
    	XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        if ((m_arg0 == null) && (m_arg1 == null)) {
           throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function 'json-to-xml' needs to have at least one argument.", srcLocator);
        }

        XObject arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
           
        String jsonStr = (XslTransformEvaluationHelper.getStrVal(arg0Obj)).trim();
                 
        XPathMap optionsMap = null;
        
        // The following are XPath 3.1 function fn:json-to-xml 'options' default values, 
        // when function fn:json-to-xml's argument 'options' is absent.
        
  	    boolean optionIsLiberal = false;        	          	          	          	  
  	    
  	    String optionDuplicatesValStr = XSLJsonConstants.DUPLICATES_RETAIN;
  	    
  	    boolean optionValidate = XSLJsonConstants.VALIDATE_FALSE;
        
        if (m_arg1 != null) {
           XObject arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
           
           if (!(arg1Obj instanceof XPathMap)) {
        	  throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function 'json-to-xml' optional second argument should be an xdm map, "
        	  		                                                                                                                     + "that specifies options for the "
        	  		                                                                                                                     + "function call 'json-to-xml'.", srcLocator); 
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
        			throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-to-xml' option '" + keyStr + "' is not supported. This "
        					                                                                                                          + "implementation supports, the function "
        					                                                                                                          + "'json-to-xml' options 'liberal', 'duplicates', 'validate'.", srcLocator); 
        		 }
        		 else if (XSLJsonConstants.LIBERAL.equals(keyStr)) {
        			if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBoolean) 
        					                               || (optionValue instanceof XBooleanStatic)) {
        				optionIsLiberal = optionValue.bool();        			   
        			}
        			else {
        			   throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-to-xml' option \"liberal\" value is not of an XML schema type boolean.", srcLocator);
        			}
        		 }
        		 else if (XSLJsonConstants.DUPLICATES.equals(keyStr)) {
        			if (!((optionValue instanceof XString) || (optionValue instanceof XSString))) {
        				throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function 'json-to-xml' option \"duplicate\" value is not of an XML schema type string.", srcLocator);
        			}
        			else {
        				optionDuplicatesValStr = XslTransformEvaluationHelper.getStrVal(optionValue);        			
        				
        				if (!(XSLJsonConstants.DUPLICATES_REJECT.equals(optionDuplicatesValStr) || XSLJsonConstants.DUPLICATES_USE_FIRST.equals(optionDuplicatesValStr) || 
        						                                                                   XSLJsonConstants.DUPLICATES_RETAIN.equals(optionDuplicatesValStr))) {
        					throw new javax.xml.transform.TransformerException("FOJS0005 : An XPath 3.1 function call 'json-to-xml' option \"duplicates\" value is not one of following : 'reject', 'use-first', "
        																																												            + "'retain'.", srcLocator);
        				}
        			}
        		 }
        		 else if (XSLJsonConstants.VALIDATE.equals(keyStr)) {
        			 if ((optionValue instanceof XSBoolean) || (optionValue instanceof XBoolean) 
        					                                || (optionValue instanceof XBooleanStatic)) {
        				 optionValidate = optionValue.bool();        			   
        			 }
        			 else {
        				 throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function call 'json-to-xml' option \"validate\"' value is not of an XML schema type boolean.", srcLocator);
        			 } 
        		 }
        	  }
        	  
        	  if (optionValidate && (XSLJsonConstants.DUPLICATES_RETAIN).equals(optionDuplicatesValStr)) {
        		 throw new javax.xml.transform.TransformerException("FOUT1190 : An XPath 3.1 function 'json-to-xml' option values duplicates : 'retain' and validate : 'true' are not compatible.", srcLocator); 
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
        	  throw new javax.xml.transform.TransformerException("FOJS0003 : An XPath 3.1 function call 'json-to-xml' first argument is, not "
													                                                                                    + "a correct json lexical string. A json string can only start with "
													                                                                                    + "characters '{', or '['.", srcLocator); 
           }
        }
        catch (JSONException ex) {
           String jsonParseErrStr = ex.getMessage();
           
           throw new javax.xml.transform.TransformerException("FOJS0003 : An XPath 3.1 function call 'json-to-xml' first argument is, not "
																           		                                                     + "a correct json lexical string. The json parse resulted in following "
																           		                                                     + "error: " + jsonParseErrStr + ". The function call 'json-to-xml' "
																           		                                                     + "options used were liberal : " + optionIsLiberal + ", duplicates : " + 
																           		                                                     optionDuplicatesValStr + ".", srcLocator);
        }
        
        System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder documentBuilder = null;
        
        try {
		   documentBuilder = dbf.newDocumentBuilder();
		} 
        catch (ParserConfigurationException ex) {
		   throw new TransformerException("FOJS0001 : An XML parser configuration exception has occured.", srcLocator);
		}
		
        Document document = documentBuilder.newDocument();
        
        constructXmlDom(jsonObj, document, document, null);
        
        DTM dtm = null;
        
        if ((XSLJsonConstants.DUPLICATES_RETAIN).equals(optionDuplicatesValStr)) {
           mutateXmlDom(document.getDocumentElement());
           
           dtm = xctxt.getDTM(new DOMSource(document), true, null, false, false);
        }
        else if (optionValidate == XSLJsonConstants.VALIDATE_TRUE) {
           try {
        	   String xmlDocStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(document);
        	   
        	   if (isXmlStrValidWithJsonSchema(xmlDocStr)) {        		            		           		   
        		  dtm = xctxt.getDTM(new DOMSource(document), true, null, false, false);
        	   }
           }
           catch (Exception ex) {
        	   throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator);
           }
        }
        else {        	
           dtm = xctxt.getDTM(new DOMSource(document), true, null, false, false);
        }
        
        int resultNodeDtmHandle = dtm.getDocument(); 
        
        result = new XMLNodeCursorImpl(resultNodeDtmHandle, xctxt);
        
        return result;
    }
    
    /**
     * Method definition, to mutate XML org.w3c.dom.Element object 
     * instance for xdm map's duplicate key resolution option 'retain'.
     * 
     * @param currElem                         An XML document's, document 
     *                                         element that needs to be mutated.
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
     * Method definition, to construct an XML org.w3c.dom.Document object 
     * instance from the supplied json (org.json.JSONObject, org.json.JSONArray) 
     * object.
     * 
     * @param jsonObj        An object that is either of type org.json.JSONObject, or 
     *                       org.json.JSONArray. 
     * @param document       An empty XML Document object, that needs to be populated 
     *                       within this method implementation.
     * @param parentNode     An XML Node object argument, that is needed to construct a fully  
     *                       populated XML Document object.
     * @param keyVal         This function argument is needed, to be able to properly create
     *                       specific XML nodes within the produced XML Document object by 
     *                       this method.
     *                                             
     * @return               void
     */
    private void constructXmlDom(Object jsonObj, Document document, Node parentNode, String keyVal) {
    	
    	if (jsonObj instanceof JSONObject) {
    		Element mapElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.MAP);
    		parentNode.appendChild(mapElem);    		
    		
    		if (keyVal != null) {
    		   mapElem.setAttribute(XSLJsonConstants.KEY, keyVal);	
    		}
    		
	    	Iterator<String> jsonKeys = ((JSONObject)jsonObj).keys();	    	
	    	
	    	while (jsonKeys.hasNext()) {
	      	   String key = jsonKeys.next();
	      	   Object value = ((JSONObject)jsonObj).get(key);
	      	   
	      	   if (value instanceof String) {
	      		 Element strElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.STRING);
	      		 strElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode((String)value);
	      		 strElem.appendChild(text);
	      		 mapElem.appendChild(strElem);
	      	   }
	      	   else if (value instanceof Number) {
	      		 Element numberElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NUMBER);
	      		 numberElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode(value.toString());
	      		 numberElem.appendChild(text);
	      		 mapElem.appendChild(numberElem);
	      	   }
	      	   else if (value instanceof Boolean) {
	      		 Element boolElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.BOOLEAN);
	      		 boolElem.setAttribute(XSLJsonConstants.KEY, key);
	      		 Text text = document.createTextNode(value.toString());
	      		 boolElem.appendChild(text);
	      		 mapElem.appendChild(boolElem);
	      	   }	      	   
	      	   else if (value instanceof JSONArray) {
	      		  Element arrayElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.ARRAY);
	      		  arrayElem.setAttribute(XSLJsonConstants.KEY, key);
	      		  mapElem.appendChild(arrayElem);
	      		  JSONArray jsonArr = (JSONArray)value;
	      		  int arrLen = jsonArr.length();
	    		  
	      		  for (int idx = 0; idx < arrLen; idx++) {
	    			  Object arrItem = jsonArr.get(idx);
	    			  
	    			  if (arrItem instanceof String) {
	    				  Element strElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.STRING);
	    				  Text text = document.createTextNode((String)arrItem);
	    				  strElem.appendChild(text);
	    				  arrayElem.appendChild(strElem);	 
	    			  }
	    			  else if (arrItem instanceof Number) {
	    				  Element numberElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NUMBER);
	    				  Text text = document.createTextNode(arrItem.toString());
	    				  numberElem.appendChild(text);
	    				  arrayElem.appendChild(numberElem); 
	    			  }
	    			  else if (arrItem instanceof Boolean) {
	    				  Element boolElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.BOOLEAN);
	    				  Text text = document.createTextNode(arrItem.toString());
	    				  boolElem.appendChild(text);
	    				  arrayElem.appendChild(boolElem);  
	    			  }
	    			  else if (JSONObject.NULL.equals(arrItem)) {
	         			  Element nullElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NULL);
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
	      		  Element nullElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, "null");
	      		  nullElem.setAttribute(XSLJsonConstants.KEY, key);
	      		  mapElem.appendChild(nullElem);
	      	   }
	        }	    		    	
    	}
    	else if (jsonObj instanceof JSONArray) {
    		Element arrayElem = null;
    		
    		if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
    		   arrayElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.ARRAY);
     		}
     		else {
     		   arrayElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.ARRAY);
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
    			  Element strElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.STRING);
  	      		  Text text = document.createTextNode((String)arrItem);
  	      		  strElem.appendChild(text);
  	      		  arrayElem.appendChild(strElem);	 
    		   }
    		   else if (arrItem instanceof Number) {
    			  Element numberElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NUMBER);
   	      		  Text text = document.createTextNode(arrItem.toString());
   	      		  numberElem.appendChild(text);
   	      		  arrayElem.appendChild(numberElem); 
    		   }
    		   else if (arrItem instanceof Boolean) {
    			  Element boolElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.BOOLEAN);
    	          Text text = document.createTextNode(arrItem.toString());
    	          boolElem.appendChild(text);
    	          arrayElem.appendChild(boolElem);  
    		   }
    		   else if ((JSONObject.NULL).equals(arrItem)) {
     			  Element nullElem = document.createElementNS(XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI, XSLJsonConstants.NULL);
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
