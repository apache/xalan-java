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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of XPath 3.1 fn:resolve-QName() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncResolveQName extends Function2Args
{

	private static final long serialVersionUID = -6607276959184577402L;
	
	/**
     * Class constructor.
     */
    public FuncResolveQName() {
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
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
    	XObject result = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator();   	

    	if (isXdmEmptySequence(m_arg0, xctxt)) {
    		result = new ResultSequence();
    	}
    	else if (isXdmSeqSingleton(m_arg0, xctxt)) {    	   
    		XObject arg1Value = getFunctionArgEffectiveValue(m_arg1, xctxt);
    		
    		if (arg1Value instanceof XMLNodeCursorImpl) {
    			XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)arg1Value;    		  
    			int nodeHandle = nodeSet.nextNode();
    			DTMManager dtmMgr = xctxt.getDTMManager();
    			DTM dtm = dtmMgr.getDTM(nodeHandle);
    			if (dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) {
    				XObject arg0Value = getFunctionArgEffectiveValue(m_arg0, xctxt);
    				
    				String qnameLexicalStrVal = XslTransformEvaluationHelper.getStrVal(arg0Value);
    				String nsPrefix = null;
    				String localPart = null;
    				if (qnameLexicalStrVal.contains(":")) {
    					nsPrefix = qnameLexicalStrVal.substring(0, qnameLexicalStrVal.indexOf(':'));                	
    					localPart = qnameLexicalStrVal.substring(qnameLexicalStrVal.indexOf(':') + 1);  
    				}
    				else {
    					localPart = qnameLexicalStrVal;   
    				}

    				Node node = dtm.getNode(nodeHandle);
    				String nsUri = getNsuriFromInscopeNamespacesOfXMLElement(node, nsPrefix);
    				
    				result = new XSQName(nsPrefix, localPart, nsUri);
    			}
    			else {
    				throw new TransformerException("XPTY0004 : The required item type of the second argument of "
    						                             + "function fn:resolve-QName() is element(). But the supplied "
    						                             + "value's item type is different.", srcLocator);  
    			}
    		}
    		else {
    			throw new TransformerException("XPTY0004 : The required item type of the second argument of "
    					                             + "function fn:resolve-QName() is element(). But the supplied "
    					                             + "value's item type is different.", srcLocator);    
    		}
    	}
    	else {
    		throw new TransformerException("XPTY0004 : The first argument of function call fn:resolve-QName() needs to be "
    				                             + "a lexical qname string, of form prefix:localName or localName.", srcLocator); 
    	}
    	    	    	
        return result;  
    }

	/**
     * Method definition, to check whether, the first argument of XPath 
     * function call fn:resolve-QName evaluates to an xdm empty sequence.
     */
	private boolean isXdmEmptySequence(Expression expr1, XPathContext xctxt) throws TransformerException {
		
		boolean result = false;
		
		if (expr1 != null) {
		   XObject seqExprValue = getFunctionArgEffectiveValue(expr1, xctxt);
		   
		   if (seqExprValue instanceof ResultSequence) {
			  if (((ResultSequence)seqExprValue).size() == 0) {
				 result = true;  
			  }
		   }
		   else if (seqExprValue instanceof XMLNodeCursorImpl) {
			  if (((XMLNodeCursorImpl)seqExprValue).getLength() == 0) {
				 result = true;  
			  }
		   }
		}
		else {
		   result = true;
		}
		
		return result;
	}
	
	/**
     * Method definition, to check whether, the first argument of XPath 
     * function call fn:resolve-QName evaluates to an xdm sequence 
     * with size one.
     */
    private boolean isXdmSeqSingleton(Expression expr1, XPathContext xctxt) throws TransformerException {
    	
    	boolean result = false;
    	
		if (expr1 != null) {
		   XObject seqExprValue = getFunctionArgEffectiveValue(expr1, xctxt);
		   
		   if (seqExprValue instanceof ResultSequence) {
		      if (((ResultSequence)seqExprValue).size() == 1) {
			     result = true;  
			  }
		   }
		   else if (seqExprValue instanceof XMLNodeCursorImpl) {
			  if (((XMLNodeCursorImpl)seqExprValue).getLength() == 1) {
			     result = true;  
			  }
		   }
		   else if (seqExprValue instanceof XObject) {
		      result = true; 
		   }
		}
    	
    	return result; 
	}
    
    /**
     * Method definition, to do, using a supplied XML element node reference and 
     * a namespace prefix, get an XML namespace uri for the prefix, from within in-scope 
     * namespace bindings of an XML element node.
     * 
     * @param node        					An XML element node reference
     * @param nsPrefix    					An XML namespace prefix
     */
	private String getNsuriFromInscopeNamespacesOfXMLElement(Node node, String nsPrefix) {
		
		String result = null;
		
		NamedNodeMap attrNodeMap = node.getAttributes();
		Node attrNode = null;
		
		if (nsPrefix != null) {
		   attrNode = attrNodeMap.getNamedItem(Constants.ATTRNAME_XMLNS + nsPrefix);
		}
		else {
		   attrNode = attrNodeMap.getNamedItem(Constants.ATTRNAME_XMLNSDEF);
		}
		
		if (attrNode != null) {
		   result = attrNode.getNodeValue();
		}
		else {
		   result = getNsuriFromInscopeNamespacesOfXMLElement(node.getParentNode(), nsPrefix);
		}
		
		return result;
	}

}
