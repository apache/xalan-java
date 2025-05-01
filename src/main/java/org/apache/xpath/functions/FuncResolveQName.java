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
      * Execute the function. The function must return a valid object.
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

    	Expression arg0 = getArg0();    	

    	if (isXdmEmptySequence(arg0, xctxt)) {
    		result = new ResultSequence();
    	}
    	else if (isXdmSequenceOfLengthOne(arg0, xctxt)) {    	   
    		Expression arg1 = getArg1();
    		XObject arg1Value = arg1.execute(xctxt);
    		if (arg1Value instanceof XMLNodeCursorImpl) {
    			XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)arg1Value;    		  
    			int nodeHandle = nodeSet.nextNode();
    			DTMManager dtmMgr = xctxt.getDTMManager();
    			DTM dtm = dtmMgr.getDTM(nodeHandle);
    			if (dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) {
    				XObject arg0Value = arg0.execute(xctxt);
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
     * Check whether, first argument of fn:resolve-QName method call 
     * evaluates to an xdm empty sequence.
     */
	private boolean isXdmEmptySequence(Expression seqExpr, XPathContext xctxt) throws TransformerException {
		boolean isEmptySequence = false;
		
		if (seqExpr != null) {
		   XObject seqExprValue = seqExpr.execute(xctxt);
		   if (seqExprValue instanceof ResultSequence) {
			  if (((ResultSequence)seqExprValue).size() == 0) {
				 isEmptySequence = true;  
			  }
		   }
		   else if (seqExprValue instanceof XMLNodeCursorImpl) {
			  if (((XMLNodeCursorImpl)seqExprValue).getLength() == 0) {
				 isEmptySequence = true;  
			  }
		   }
		}
		else {
		   isEmptySequence = true;
		}
		
		return isEmptySequence;
	}
	
	/**
     * Check whether, first argument of fn:resolve-QName method call 
     * evaluates to an xdm sequence whose length is one.
     */
    private boolean isXdmSequenceOfLengthOne(Expression seqExpr, XPathContext xctxt) throws TransformerException {
    	boolean isSequenceOfLengthOne = false;
    	
		if (seqExpr != null) {
		   XObject seqExprValue = seqExpr.execute(xctxt);
		   if (seqExprValue instanceof ResultSequence) {
		      if (((ResultSequence)seqExprValue).size() == 1) {
			     isSequenceOfLengthOne = true;  
			  }
		   }
		   else if (seqExprValue instanceof XMLNodeCursorImpl) {
			  if (((XMLNodeCursorImpl)seqExprValue).getLength() == 1) {
			     isSequenceOfLengthOne = true;  
			  }
		   }
		   else if (seqExprValue instanceof XObject) {
		      isSequenceOfLengthOne = true; 
		   }
		}
    	
    	return isSequenceOfLengthOne; 
	}
    
    /**
     * Given an XML element node and a namespace prefix, find an XML 
     * namespace uri for the prefix, from within in-scope namespace bindings 
     * of an element.
     * 
     * @param node        an XML element node, whose in-scope namespaces bindings
     *                    needs to be searched.
     * @param nsPrefix    namespace prefix
     */
	private String getNsuriFromInscopeNamespacesOfXMLElement(Node node, String nsPrefix) {
		String nsUri = null;
		
		NamedNodeMap attrNodeMap = node.getAttributes();
		Node attrNode = null;
		
		if (nsPrefix != null) {
		   attrNode = attrNodeMap.getNamedItem(Constants.ATTRNAME_XMLNS + nsPrefix);
		}
		else {
		   attrNode = attrNodeMap.getNamedItem(Constants.ATTRNAME_XMLNSDEF);
		}
		
		if (attrNode != null) {
		   nsUri = attrNode.getNodeValue();
		}
		else {
		   // Recursive call to this function
		   nsUri = getNsuriFromInscopeNamespacesOfXMLElement(node.getParentNode(), nsPrefix);
		}
		
		return nsUri;
	}

}
