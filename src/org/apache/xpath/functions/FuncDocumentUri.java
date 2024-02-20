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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * Implementation of the document-uri() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDocumentUri extends FunctionDef1Arg
{

	private static final long serialVersionUID = 685581942265897866L;

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
    	
    	String documentUriStr = null;
    	
    	if (m_arg0 != null) {
    	   XObject argValue = m_arg0.execute(xctxt);
    	   if ((argValue != null) && (argValue.getType() == XObject.CLASS_NODESET)) {
    		  XNodeSet nodeSet = (XNodeSet)argValue;
    		  if (nodeSet.getLength() == 1) {
    			 documentUriStr = getXdmNodeDocumentUri(nodeSet, dtmMgr);
    			 if (documentUriStr != null) {
    			    result = new XSAnyURI(documentUriStr);
    			 }
    			 else {
    				result = new ResultSequence();  
    			 }
    		  }
    		  else {
    			  throw new javax.xml.transform.TransformerException("XPDY0002 : While trying to find document uri of a node using "
                                                                            + "function fn:document-uri, the context nodeset is not "
                                                                            + "of size one", srcLocator);   
    		  }
    	   }
    	   else if ((argValue != null) && (argValue instanceof ResultSequence) && (((ResultSequence)argValue).size() == 0)) {
    		  result = new ResultSequence(); 
    	   }
    	   else {
    		  throw new javax.xml.transform.TransformerException("XPDY0002 : The fn:document-uri's argument didn't evaluate to a node", 
                                                                                                                              srcLocator);  
    	   }
    	}
    	else {
    	   int contextXdmNodeHandle = xctxt.getContextNode();
      	   if (contextXdmNodeHandle != DTM.NULL) {    		   
      		  XNodeSet nodeSet = new XNodeSet(contextXdmNodeHandle, dtmMgr);
      		  if (nodeSet.getLength() == 1) {
      			 documentUriStr = getXdmNodeDocumentUri(nodeSet, dtmMgr);
      			 if (documentUriStr != null) {
    			    result = new XSAnyURI(documentUriStr);
    			 }
    			 else {
    				result = new ResultSequence();  
    			 }
      		  }
      		  else {
      			 throw new javax.xml.transform.TransformerException("XPDY0002 : While trying to find document uri of a node using "
                                                                           + "function fn:document-uri, the context nodeset is not "
                                                                           + "of size one", srcLocator);   
      		  }
      	   }
      	   else {
      		  XObject contextItem = xctxt.getXPath3ContextItem();
      		  if ((contextItem != null) && (contextItem.getType() == XObject.CLASS_NODESET)) {
      			 XNodeSet nodeSet = (XNodeSet)contextItem;
      			 if (nodeSet.getLength() == 1) {
      				documentUriStr = getXdmNodeDocumentUri(nodeSet, dtmMgr);
      				if (documentUriStr != null) {
        			   result = new XSAnyURI(documentUriStr);
        			}
        			else {
        			   result = new ResultSequence();  
        			}
         		 }
         		 else {
         			throw new javax.xml.transform.TransformerException("XPDY0002 : While trying to find document uri of a node using "
         					                                                  + "function fn:document-uri, the context nodeset is not "
         					                                                  + "of size one", srcLocator);  
         		 }
      		  }
      		  else {
      		     throw new javax.xml.transform.TransformerException("XPDY0002 : While calling fn:document-uri "
                                                                                   + "function without an argument, a context node must be available", 
                                                                                   srcLocator);
      		  }
      	   }
    	}
    
        return result;  
    }
    
    /**
     * Find and return an XML document uri of an xdm node.
     * 
     * (as per XPath Data Model specification, XML document uri of only document node 
     * is available [which could possibly be null as well, for e.g when an XML document 
     * object has not been constructed from an XML document file or a url location]. 
     * For all other kinds of xdm nodes, XML document uri is null)
     * 
     * @param nodeSet    an xdm node object    
     * @param dtmMgr     XalanJ XSL transformer's DTMManager object
     * @return           XML document uri of the node as string value
     * @throws           TransformerException 
     */
    private String getXdmNodeDocumentUri(XNodeSet nodeSet, DTMManager dtmMgr) {
       String xmlDocumentUri = null;
              
       int nodeHandle = nodeSet.nextNode();
	   DTM dtm = dtmMgr.getDTM(nodeHandle);
	   short nodeType = dtm.getNodeType(nodeHandle);
	   if (nodeType == DTM.DOCUMENT_NODE) {
		  Node node = dtm.getNode(nodeHandle);
		  xmlDocumentUri = ((Document)node).getDocumentURI();
	   }
              
       return xmlDocumentUri;
    }
}
