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

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * Implementation of the base-uri() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncBaseUri extends FunctionDef1Arg
{

	private static final long serialVersionUID = -637288976892401962L;
	
	/**
	 * Class constructor.
	 */
	public FuncBaseUri() {
		m_defined_arity = new Short[] {0, 1}; 
	}

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
    	
    	DTMManager dtmMgr = xctxt.getDTMManager();
    	
    	String baseUriStr = null;
    	
    	if (m_arg0 != null) {
    	   XObject argValue = m_arg0.execute(xctxt);
    	   if ((argValue != null) && (argValue.getType() == XObject.CLASS_NODESET)) {
    		  XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)argValue;
    		  if (nodeSet.getLength() == 1) {
    			 baseUriStr = getXdmNodeBaseUri(nodeSet, dtmMgr);
    			 if (baseUriStr != null) {
    			    result = new XSAnyURI(baseUriStr);
    			 }
    			 else {
    				result = new ResultSequence();  
    			 }
    		  }
    		  else {
    			  throw new javax.xml.transform.TransformerException("XPDY0002 : While trying to find base uri of a node using "
                                                                            + "function fn:base-uri, the context nodeset is not "
                                                                            + "of size one", srcLocator);   
    		  }
    	   }
    	   else if ((argValue != null) && (argValue instanceof ResultSequence) && (((ResultSequence)argValue).size() == 0)) {
    		  result = new ResultSequence(); 
    	   }
    	   else {
    		  throw new javax.xml.transform.TransformerException("XPDY0002 : The fn:base-uri argument didn't evaluate to a node", 
                                                                                                                              srcLocator);  
    	   }
    	}
    	else {
    	   int contextXdmNodeHandle = xctxt.getContextNode();
      	   if (contextXdmNodeHandle != DTM.NULL) {    		   
      		  XMLNodeCursorImpl nodeSet = new XMLNodeCursorImpl(contextXdmNodeHandle, dtmMgr);
      		  if (nodeSet.getLength() == 1) {
      			 baseUriStr = getXdmNodeBaseUri(nodeSet, dtmMgr);
      			 if (baseUriStr != null) {
    			    result = new XSAnyURI(baseUriStr);
    			 }
    			 else {
    				result = new ResultSequence();  
    			 }
      		  }
      		  else {
      			 throw new javax.xml.transform.TransformerException("XPDY0002 : While trying to find base uri of a node using "
                                                                           + "function fn:base-uri, the context nodeset is not "
                                                                           + "of size one", srcLocator);   
      		  }
      	   }
      	   else {
      		  XObject contextItem = xctxt.getXPath3ContextItem();
      		  if ((contextItem != null) && (contextItem.getType() == XObject.CLASS_NODESET)) {
      			 XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)contextItem;
      			 if (nodeSet.getLength() == 1) {
      				baseUriStr = getXdmNodeBaseUri(nodeSet, dtmMgr);
      				if (baseUriStr != null) {
        			   result = new XSAnyURI(baseUriStr);
        			}
        			else {
        			   result = new ResultSequence();  
        			}
         		 }
         		 else {
         			throw new javax.xml.transform.TransformerException("XPDY0002 : While trying to find base uri of a node using "
         					                                                  + "function fn:base-uri, the context nodeset is not "
         					                                                  + "of size one", srcLocator);  
         		 }
      		  }
      		  else {
      		     throw new javax.xml.transform.TransformerException("XPDY0002 : While calling fn:base-uri "
                                                                                   + "function without an argument, a context node must be available", 
                                                                                   srcLocator);
      		  }
      	   }
    	}
    
        return result;  
    }
    
    /**
     * Find and return an XML base uri of an xdm node.
     * 
     * (as per XPath Data Model specification, XML base uri of only document, element 
     * and PI nodes are available [which could possibly be null as well, for e.g when 
     * an XML document object has not been constructed from an XML document file or 
     * a url location]. For all other kinds of xdm nodes, XML base uri is null)
     * 
     * An XML base uri of a node, is either uri of the document to which an xdm node
     * belongs, or value of an XML attribute named xml:base (if an XML attribute named 
     * xml:base is present, then it overrides XML document's uri for the value of 
     * xdm base uri) present on an xdm element node (an XML attribute named xml:base on 
     * an element node itself, or on nearest ancestor element node is considered 
     * when considering xml:base attribute).
     * 
     * @param nodeSet    an xdm node object    
     * @param dtmMgr     XalanJ XSL transformer's DTMManager object
     * @return           XML base uri of the node as string value
     * @throws           TransformerException 
     */
    private String getXdmNodeBaseUri(XMLNodeCursorImpl nodeSet, DTMManager dtmMgr) {
       String xmlBaseUri = null;
              
       int nodeHandle = nodeSet.nextNode();
	   DTM dtm = dtmMgr.getDTM(nodeHandle);
	   short nodeType = dtm.getNodeType(nodeHandle);
	   if ((nodeType == DTM.DOCUMENT_NODE) || (nodeType == DTM.ELEMENT_NODE) || 
				                              (nodeType == DTM.PROCESSING_INSTRUCTION_NODE)) {
		  Node node = dtm.getNode(nodeHandle);
		  xmlBaseUri = node.getBaseURI();
	   }
              
       return xmlBaseUri;
    }
}
