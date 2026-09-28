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

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * An implementation of XPath 3.1 function, fn:namespace-uri-for-prefix.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncNamespaceUriForPrefix extends Function2Args {

	private static final long serialVersionUID = 6212323137442859818L;

	/**
	 * Class constructor.
	 */
	public FuncNamespaceUriForPrefix() {
		m_arity = new Short[] { 2 };
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();	  

		XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
		
		String nsPrefixStr = XslTransformEvaluationHelper.getStrVal(xObj0);

		XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
		
		if (xObj1 instanceof ResultSequence) {
		   ResultSequence rSeq = (ResultSequence)xObj1;
		   int size1 = rSeq.size();
		   
		   if (size1 == 1) {
			  xObj1 = rSeq.item(0);  
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'namespace-uri-for-prefix' second argument, "
																																		      + "should be an xdm element node.", srcLocator); 
		   }
		}
		
		if (xObj1 instanceof XMLNodeCursorImpl) {
			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;
			
			if (xmlNodeCursorImpl.getLength() == 1) {
				DTMCursorIterator iter1 = xmlNodeCursorImpl.iterRaw();
				int nextNode = iter1.nextNode();
				
				DTMManager dtmMgr1 = xmlNodeCursorImpl.getDTMManager();
				
				DTM dtm = dtmMgr1.getDTM(nextNode);
				Node node = dtm.getNode(nextNode);
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					result = getNamespaceUriForPrefix(nsPrefixStr, node);
					
					if (result == null) {
					   result = new ResultSequence();
					}
				}
				else {
					throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'namespace-uri-for-prefix' second argument, "
																																			        + "should be an xdm element node.", srcLocator); 
				}
			}
			else {
				throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'namespace-uri-for-prefix' second argument, "
					     																														+ "should be an xdm element node.", srcLocator); 
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'namespace-uri-for-prefix' second argument, "
				     																									                    + "is not an xdm element node.", srcLocator);  
		}

		return result;
	}

	/**
	 * Method definition, to get namespace uri for prefix, looking 
	 * within the supplied 'node' argument and 'node' argument's ancestor 
	 * nodes.  
	 * 
	 * @param nsPrefixStr                   The supplied XML namespace prefix 
	 *                                      string.
	 * @param node                          An XML document node, from where to
	 *                                      start looking for an XML namespace uri
	 *                                      information for the supplied namespace
	 *                                      prefix.
	 * @return                              An xdm value with type xs:anyURI
	 */
	private XSAnyURI getNamespaceUriForPrefix(String nsPrefixStr, Node node) {
		
		XSAnyURI result = null;

		NamedNodeMap elemAttributes = node.getAttributes();
		int attrListSize = elemAttributes.getLength();
		
		for (int idx = 0; idx < attrListSize; idx++) {
			Attr attr = (Attr)elemAttributes.item(idx);
			String attrName = attr.getName();
									
			if ((XMLConstants.XMLNS_ATTRIBUTE).equals(attrName) && ((nsPrefixStr == null) || ("".equals(nsPrefixStr)))) {
				result = new XSAnyURI(attr.getValue());  
			}
			else if (attrName.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")) {
				String nsPrefixOfAttr = attrName.substring(6);
				
				if (nsPrefixStr.equals(nsPrefixOfAttr)) {
					result = new XSAnyURI(attr.getValue()); 
				}
			}
		}

		if (result != null) {
			return result;  
		}
		else {
			result = getNamespaceUriForPrefix(nsPrefixStr, node.getParentNode()); 
		}

		return result;
	}
}
