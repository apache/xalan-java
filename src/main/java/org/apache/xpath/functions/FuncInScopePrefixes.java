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
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.Constants;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of XPath 3.1 function fn:in-scope-prefixes.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncInScopePrefixes extends FunctionOneArg {

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

		XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

		if (xObj0 instanceof XMLNodeCursorImpl) {
			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj0;
			DTMCursorIterator dtmIter = xmlNodeCursorImpl.iterRaw();
			int nodeHandle = dtmIter.nextNode();
			DTM dtm = xctxt.getDTM(nodeHandle);
			Node node = dtm.getNode(nodeHandle);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Map<String, String> map1 = new HashMap<String, String>();
				
				map1.put(Constants.S_XMLNAMESPACEURI, XMLConstants.XML_NS_PREFIX);
				
				getInScopePrefixes((Element)node, map1);			
				
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
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'in-scope-prefixes' "
																											+ "argument is not an element node.", srcLocator);	 
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'in-scope-prefixes' "
																											+ "argument is not an element node.", srcLocator); 
		}

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
		for (int idx = 0; idx < attrCount; idx++) {
			Node attrNode = namedNodeMap.item(idx);
			String attrName = attrNode.getNodeName();
			String attrValue = attrNode.getNodeValue();
			if ((XMLConstants.XMLNS_ATTRIBUTE).equals(attrName)) {
				String prefixStr = "";
				
				map1.putIfAbsent(attrValue, prefixStr);
			}
			else if (attrName.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")) {
				String prefixStr = attrName.substring(6);
				
				map1.putIfAbsent(attrValue, prefixStr);
			}
		}

		Node node = elemNode.getParentNode();
		
		if ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
			getInScopePrefixes((Element)node, map1); 
		}
	}
}
