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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.Constants;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.types.XSNCName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:in-scope-prefixes.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncInScopePrefixes extends FunctionMultiArgs {

	private static final long serialVersionUID = 2372823852330912332L;

	/**
	 * Class constructor.
	 */
	public FuncInScopePrefixes() {
		m_defined_arity = new Short[] { 1 };
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

		ResultSequence result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		if (m_arg0 == null) {
			throw new javax.xml.transform.TransformerException("XPST0017: An XPath 3.1 function 'in-scope-prefixes' "
					                                                                          + "requires an xdm element node as "
					                                                                          + "argument. No function argument has been provided.", 
					                                                                                                     srcLocator);
		}		
		else if (m_arg1 != null) {
			throw new javax.xml.transform.TransformerException("XPST0017: An XPath 3.1 function 'in-scope-prefixes' "
                                                                                              + "cannot have more than one argument.", srcLocator);
		}

		XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

		if (xObj0 instanceof XMLNodeCursorImpl) {
			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj0;
			DTMCursorIterator dtmIter = xmlNodeCursorImpl.iter();
			int nodeHandle = dtmIter.nextNode();
			DTM dtm = xctxt.getDTM(nodeHandle);
			
			if ((dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) && (nodeHandle != DTM.NULL)) {
				Map<String, String> map1 = new HashMap<String, String>();
				
				map1.put(Constants.S_XMLNAMESPACEURI, XMLConstants.XML_NS_PREFIX);
				
				FuncPath funcPath = new FuncPath();
				xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
				funcPath.setArg0(xObj0);
								
				XSString arg0XsPathStr = (XSString)(funcPath.execute(xctxt));				
				
				String localName = dtm.getLocalName(nodeHandle);
				String namespace = dtm.getNamespaceURI(nodeHandle);
				
				String xmlSystemId = XslTransformData.m_xmlSystemId;
				System.setProperty(Constants.XML_DOCUMENT_BUILDER_FACTORY_KEY, Constants.XML_DOCUMENT_BUILDER_FACTORY_VALUE);

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				
				try {
					DocumentBuilder dBuilder = dbf.newDocumentBuilder();

					Document document = dBuilder.parse(xmlSystemId);
					NodeList nodeList = document.getElementsByTagNameNS(namespace, localName);
					int size1 = nodeList.getLength();
					Node node2 = null;
					for (int idx = 0; idx < size1; idx++) {
					   node2 = nodeList.item(idx);
					   String str1 = getFnPathStrElemNode((Element)node2);
					   if (str1.equals(arg0XsPathStr.stringValue())) {
						  break;  
					   }
					}
					
					if (node2 != null) {
						getInScopePrefixes((Element)node2, map1);

						Set<String> keySet1 = map1.keySet();								
						Iterator<String> iter1 = keySet1.iterator();

						ResultSequence rSeq = new ResultSequence();
						while (iter1.hasNext()) {
							String nsUri = iter1.next();
							String prefix = map1.get(nsUri); 
							rSeq.add(new XSString(prefix)); 
						}

						result = rSeq;
					}
				}
				catch (Exception ex) {
					// no op
				}
			}
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'in-scope-prefixes' "
																											+ "argument is not an element node.", srcLocator);	 
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'in-scope-prefixes' "
																											+ "argument is not an element node.", srcLocator); 
		}
		
		int size1 = result.size();		
		
		ResultSequence rSeq2 = new ResultSequence();
		
		boolean isEmptyStrAdded = false;
		for (int idx = 0; idx < size1; idx++) {
			String str1 = ((XSString)result.item(idx)).stringValue();
			if (!"".equals(str1)) {
				XSNCName xsNcName = new XSNCName(str1);
				rSeq2.add(xsNcName);
			}
			else if (!isEmptyStrAdded) {
				rSeq2.add(new XSString(str1));
				isEmptyStrAdded = true;
			}
		}
		
		result = rSeq2;

		return result;
	}

	/**
	 * Method definition, to get XML namespace in-scope-prefixes 
	 * for an XML element node.
	 * 
	 * @param elemNode                      The supplied XML element node, for which 
	 *                                      XML namespace in-scope-prefixes needs
	 *                                      to be determined.
	 * @param map1                          The supplied java.util.Map object, that
	 *                                      helps with XML namespace in-scope-prefixes 
	 *                                      computation.  
	 */
	private void getInScopePrefixes(Element elemNode, Map<String, String> map1) {	  
		
		NamedNodeMap namedNodeMap = elemNode.getAttributes();
		
		int attrCount = namedNodeMap.getLength();
		boolean isXmlNsUndecl = false;
		for (int idx = 0; idx < attrCount; idx++) {
			Node attrNode = namedNodeMap.item(idx);
			String attrName = attrNode.getNodeName();
			String attrValue = attrNode.getNodeValue();
			if (!"".equals(attrValue)) {
			   Set<Entry<String,String>> entrySet = map1.entrySet();
			   Iterator<Entry<String,String>> iter = entrySet.iterator();
			   while (iter.hasNext()) {
				  Entry<String,String> entry = iter.next();
				  String key = entry.getKey();
				  String value = entry.getValue();
				  if (attrName.contains(":")) {
					  String prefixStr = attrName.substring(6);
					  if (value.equals(prefixStr) && "".equals(key)) {
						  /**
						   * Implementing, XML 1.1 namespace undeclaration.
						   * An XML namespace declaration xmlns:prefix="",
						   * undeclares previously declared XML namespace
						   * with xmlns:prefix="..." within the same XML document
						   * with the same XML namespace prefix.
						   */
						  map1.remove(attrValue);
						  map1.remove(key);
						  
						  isXmlNsUndecl = true;
						  
						  break;
					  }
				  }
			   }
			}
			
			if (!isXmlNsUndecl) {
				if ((XMLConstants.XMLNS_ATTRIBUTE).equals(attrName)) {
					String prefixStr = "";

					map1.putIfAbsent(attrValue, prefixStr);
				}
				else if (attrName.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")) {
					String prefixStr = attrName.substring(6);

					map1.putIfAbsent(attrValue, prefixStr);
				}
			}
		}

		Node node = elemNode.getParentNode();
		
		if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
			getInScopePrefixes((Element)node, map1); 
		}
	}
	
	/**
	 * Method definition, to get XPath 3.1 function 
	 * fn:path serialization for the supplied XML element 
	 * node.
	 * 
	 * @param elemNode                 The supplied XML element node
	 * @return                         An fn:path result string corresponding
	 *                                 to the supplied XML element node.
	 */
    private String getFnPathStrElemNode(Element elemNode) {
		
		String result = "";
		
		Node node = elemNode;

		while (node != null) {			
			String localName = null;
			String nsUri = null;
			String name = null;			
			if (node instanceof Document) {
				name = "#document"; 
			}
			else {
				localName = ((Element)node).getLocalName(); 
				nsUri = ((Element)node).getNamespaceURI();	
			}						
			
			Node prevSibling = node.getPreviousSibling();
			int prevSiblingCount = 0;

			while (prevSibling != null) {
				if (prevSibling.getNodeType() == Node.ELEMENT_NODE) {
					String nsUri2 = prevSibling.getNamespaceURI();
					String localName2 = prevSibling.getLocalName();
					boolean nsEqual = false;
					if ((nsUri == null) && (nsUri2 == null)) {
						nsEqual = true;  
					}
					else if ((nsUri != null) && nsUri.equals(nsUri2)) {
						nsEqual = true; 
					}

					if (localName2.equals(localName) && nsEqual) {
						prevSiblingCount++; 
					}
				}

				prevSibling = prevSibling.getPreviousSibling();
			}

			if ("#document".equals(name)) {
				result = "/" + result;  
			}
			else if ("".equals(result)) {
				String nsQualName = localName;
				if (nsUri != null) {
					nsQualName = "Q{" + nsUri + "}" + nsQualName;  
				}
				else {
					nsQualName = "Q{}" + nsQualName; 
				}

				if (prevSiblingCount > 0) {
					result = nsQualName + "[" + (prevSiblingCount + 1) + "]";
				}
				else {
					result = nsQualName + "[1]";
				}
			}
			else {
				String nsQualName = localName;
				if (nsUri != null) {
					nsQualName = "Q{" + nsUri + "}" + nsQualName;  
				}
				else {
					nsQualName = "Q{}" + nsQualName; 
				}

				if (prevSiblingCount > 0) {
					result = nsQualName + "[" + (prevSiblingCount + 1) + "]" + "/" + result;
				}
				else {
					result = nsQualName + "[1]/" + result;
				}
			}

			node = node.getParentNode();
		}

		return result;
	}
}
