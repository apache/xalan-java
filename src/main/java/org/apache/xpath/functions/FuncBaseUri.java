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
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSAnyURI;

/**
 * Implementation of an XPath 3.1 function fn:base-uri.
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
		m_arity = new Short[] {0, 1}; 
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
    	
    	int sourceNode = xctxt.getContextNode();
    	
    	String str1 = null;
    	
    	if (m_arg0 != null) {
    	   XObject argValue = getFunctionArgEffectiveValue(m_arg0, xctxt);
    	   
    	   if ((argValue != null) && (argValue.getType() == XObject.CLASS_NODESET)) {
    		  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)argValue;
    		  
    		  if (xmlNodeCursorImpl.getLength() == 1) {
    			 str1 = getXdmNodeBaseUri(xmlNodeCursorImpl, xctxt);
    			 
    			 if (str1 != null) {
    			    result = new XSAnyURI(str1);
    			 }
    			 else {
    				result = new ResultSequence();  
    			 }
    		  }
    		  else {
    			  throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument cannot be an "
    			  		                                                                                            + "xdm sequence with size other "
    			  		                                                                                            + "than one.", srcLocator);   
    		  }
    	   }
    	   else if ((argValue != null) && (argValue instanceof ResultSequence)) {
    		  ResultSequence rSeq = (ResultSequence)argValue;
    		  
    		  int size1 = rSeq.size();
    		  
    		  if (size1 == 1) {
    			 XObject xObj = rSeq.item(0);
    			 
    			 if (xObj.getType() == XObject.CLASS_NODESET) {
    				 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
    				 
    				 if (xmlNodeCursorImpl.getLength() == 1) {
    					 str1 = getXdmNodeBaseUri(xmlNodeCursorImpl, xctxt);

    					 if (str1 != null) {
    						 result = new XSAnyURI(str1);
    					 }
    					 else {
    						 result = new ResultSequence();  
    					 }
    				 }
    				 else {
    					 throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument cannot be an "
																		    							                   + "xdm sequence with size other "
																		    							                   + "than one.", srcLocator);   
    				 } 
    			 }
    			 else {
    				 throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument didn't evaluate to "
                                                                                                                             + "an xdm node.", srcLocator);
    			 }
    		  }
    		  else if (size1 > 1) {
    			 throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument cannot be an "
																									                + "xdm sequence with size other "
																									                + "than one.", srcLocator); 
    		  }
    		  else {
    			 result = new ResultSequence();
    			 
    			 return result;
    		  }
    	   }
    	   else {
    		  throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument didn't evaluate to "
    		  		                                                                                                  + "an xdm node.", srcLocator);  
    	   }
    	}
    	else {    	       		
    	   if (sourceNode != DTM.NULL) {    		   
      		  XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(sourceNode, xctxt);
      		  
      		  if (xmlNodeCursorImpl.getLength() == 1) {
      			 str1 = getXdmNodeBaseUri(xmlNodeCursorImpl, xctxt);
      			 
      			 if (str1 != null) {
    			    result = new XSAnyURI(str1);
    			 }
    			 else {
    				result = new ResultSequence();  
    			 }
      		  }
      		  else {
      			 throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument cannot be an "
																											       + "xdm sequence with size other "
																											       + "than one.", srcLocator);   
      		  }
      	   }
      	   else {
      		  XObject xpathCtxtItem = xctxt.getXPath3ContextItem();
      		  
      		  if (xpathCtxtItem instanceof ResultSequence) {
      			 ResultSequence rSeq = (ResultSequence)xpathCtxtItem;
      			 
      			 int size1 = rSeq.size();
      			 
      			 if (size1 == 1) {
      			    xpathCtxtItem = rSeq.item(0);  
      			 }
      			 else {
      				throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument cannot be an "
																									                  + "xdm sequence with size other "
																									                  + "than one.", srcLocator); 
      			 }
      		  }
      		  
      		  if (xpathCtxtItem != null) {
      			  if (xpathCtxtItem.getType() == XObject.CLASS_NODESET) {
      				  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathCtxtItem;

      				  if (xmlNodeCursorImpl.getLength() == 1) {
      					  str1 = getXdmNodeBaseUri(xmlNodeCursorImpl, xctxt);

      					  if (str1 != null) {
      						  result = new XSAnyURI(str1);
      					  }
      					  else {
      						  result = new ResultSequence();  
      					  }
      				  }
      				  else {
      					  throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument cannot be an "
																								      				        + "xdm sequence with size other "
																								      					    + "than one.", srcLocator);  
      				  }
      			  }
      			  else {
      				  throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' argument didn't evaluate to an xdm node.", srcLocator);
      			  }
      		  }
      		  else {
      		      throw new TransformerException("XPDY0002 : An XPath 3.1 function 'base-uri' is supplied with no argument, and an XPath context item is absent.", srcLocator);
      		  }
      	   }
    	}
    
        return result;  
    }
    
    /**
     * Method definition, to get an XML base uri for an xdm node.
     * 
     * (as per XPath Data Model specification, an XML base uri for only xdm 'document', 
     *  'element' and 'PI' nodes are available. For all other kinds of xdm nodes, 
     *  XML base uri is null)
     * 
     * An XML base uri of an xdm node, is either source uri for an XML document to which 
     * an xdm node belongs, or the value of an XML attribute (on an xdm node, or its nearest 
     * ancestor xdm nodes) named xml:base (if an XML attribute named xml:base is present, 
     * then it overrides XML document's source uri for the value of XML base uri) present 
     * on an xdm element node.
     * 
     * @param xmlNodeCursorImpl                   An xdm node object instance    
     * @param xctxt                               An XPath context object, instance
     * @return                                    An XML base uri of the node as string value
     * @throws                                    TransformerException 
     */
    private String getXdmNodeBaseUri(XMLNodeCursorImpl xmlNodeCursorImpl, XPathContext xctxt) {
       
       String result = null;
              
       int nodeHandle = xmlNodeCursorImpl.nextNode();
	   
       DTM dtm = xctxt.getDTM(nodeHandle);
	   
	   short nodeType = dtm.getNodeType(nodeHandle);
	   
	   if ((nodeType == DTM.DOCUMENT_NODE) || (nodeType == DTM.ELEMENT_NODE) || 
				                              (nodeType == DTM.PROCESSING_INSTRUCTION_NODE)) {
		  Node node = dtm.getNode(nodeHandle);
		  
		  // The function call node.getBaseURI() considers the 'node', 
		  // and also the ancestor nodes of the 'node'.
		  
		  result = node.getBaseURI();
	   }
              
       return result;
    }
}
