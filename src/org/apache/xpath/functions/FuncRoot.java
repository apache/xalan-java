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
/*
 * $Id$
 */
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;

/**
 * Implementation of an XPath 3.1 fn:root function.
 * 
 * Author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncRoot extends FunctionMultiArgs {
	
	   private static final long serialVersionUID = -7868705139354872185L;

	   /**
	   * Implementation of the function. The function must return
	   * a valid object.
	   * 
	   * @param xctxt 	            the current evaluation context
	   * @return                    a valid XObject
	   *
	   * @throws javax.xml.transform.TransformerException
	   */
	   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
	    
		   XObject result = null;
		   
		   SourceLocator srcLocator = xctxt.getSAXLocator();
		   
		   if (m_arg1 != null) {
			  throw new javax.xml.transform.TransformerException("XPDY0002: The function call fn:root cannot have "
			  		                                                     + "more than one argument.", srcLocator);  
		   }
		   
		   int nodeHandle = DTM.NULL;
		   
		   if (m_arg0 != null) {
		       XObject firstArgEvalResult = m_arg0.execute(xctxt);
		       if (firstArgEvalResult instanceof XNodeSet) {
		    	   XNodeSet xObject = (XNodeSet)firstArgEvalResult;
		    	   if (xObject.getLength() == 1) {
		    		   XNodeSet nodeSet = (XNodeSet)xObject;
				       DTMIterator dtmIter = nodeSet.iterRaw();
				       nodeHandle = dtmIter.nextNode(); 
		    	   }
		    	   else {
		    		  throw new javax.xml.transform.TransformerException("XPDY0002: The first argument of function call "
		    		  		                                           + "fn:root, is not a nodeset with length one.", srcLocator); 
		    	   }
		       }
		       else {
		    	   throw new javax.xml.transform.TransformerException("XPTY0004: A type error has occured. The "
		    	   		                                               + "first argument of function call fn:root is not a node.", srcLocator);  
		       }
		   }
		   else {
		       nodeHandle = xctxt.getCurrentNode();
		   }
		   
		   if (nodeHandle == DTM.NULL) {
		       throw new javax.xml.transform.TransformerException("XPDY0002: An XPath node argument (either as an explicit first "
		       		                                                      + "argument, or an implicit context node if first argument is absent) for "
		       		                                                      + "function call fn:root cannot be absent.", srcLocator);
		   }
		   else {			   
			   DTMManager dtmMgr = xctxt.getDTMManager();
			   DTM dtm = dtmMgr.getDTM(nodeHandle);		       
		       
			   int docRoot = dtm.getDocumentRoot(nodeHandle);		       
		       result = new XNodeSet(docRoot, dtmMgr);
		   }
		    
		   return result; 		    
	   }
	   
}
