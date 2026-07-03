/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions.array;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the array:subarray function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSubarray extends FunctionMultiArgs {

	private static final long serialVersionUID = 8678849210209571670L;
	
	/**
	 * Class constructor.
	 */
	public FuncSubarray() {
		m_arity = new Short[] { 2, 3 };
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
	    
	    if ((m_arg0 == null) || (m_arg1 == null)) {
	        throw new javax.xml.transform.TransformerException("FORG0006 : An array:subarray function call must have "
	        		                                                                                         + "first two arguments.", srcLocator); 
	    }
	    
	    XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

	    XPathArray arg0XPathArr = null;
	    
	    if (xObj0 instanceof XPathArray) {
	    	arg0XPathArr = (XPathArray)xObj0;
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("FORG0006 : The first argument of array:subarray function call, "
	    			                                                                                        + "needs to be an xdm array.", srcLocator);	   
	    }
	    
	    XObject arg1XObj = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    
	    int arg1Int = -1;
	    if ((arg1XObj instanceof XSNumericType) || (arg1XObj instanceof XNumber)) {
	       String arg1StrVal = XslTransformEvaluationHelper.getStrVal(arg1XObj);
	       try {
	          arg1Int = (Integer.valueOf(arg1StrVal)).intValue();
	       }
	       catch (NumberFormatException ex) {
	    	  throw new javax.xml.transform.TransformerException("FORG0006 : The second argument of function call array:subarray is not "
	    	  		                                                           + "an xs:integer value.", srcLocator); 
	       }
	       
	       if ((arg1Int < 1) || (arg1Int > arg0XPathArr.size())) {
	    	  throw new javax.xml.transform.TransformerException("FORG0006 : The second argument of function call array:subarray must not "
	    	  		                                                           + "be less than 1 or greater than size of input array.", srcLocator); 
	       }
	    }
	    else {
	       throw new javax.xml.transform.TransformerException("FORG0006 : The second argument of function call array:subarray is not "
	       		                                                                        + "numeric.", srcLocator);
	    }
	    
	    XPathArray resultArray = new XPathArray();
	    
	    int arg2Int = -1;
	    if (m_arg2 != null) {
	       XObject arg2XObj = getFunctionArgEffectiveValue(m_arg2, xctxt);
	       
	       if ((arg2XObj instanceof XSNumericType) || (arg2XObj instanceof XNumber)) {
		      String arg2StrVal = XslTransformEvaluationHelper.getStrVal(arg2XObj);
		      try {
		    	 arg2Int = (Integer.valueOf(arg2StrVal)).intValue();
		      }
		      catch (NumberFormatException ex) {
		    	 throw new javax.xml.transform.TransformerException("FORG0006 : The third argument provided to function call array:subarray is not "
		    	  		                                                                              + "an xs:integer value.", srcLocator); 
		      }
		   }
	       else {
		      throw new javax.xml.transform.TransformerException("FORG0006 : The third argument present on function call array:subarray "
		      		                                                                        + "is not numeric.", srcLocator);
		   }
	       
	       int resultItemCount = 0;
	       
	       int size1 = arg0XPathArr.size();
	       
	       for (int idx = (arg1Int - 1); idx < size1; idx++) {
	    	  if ((++resultItemCount) <= arg2Int) {
		         resultArray.add(arg0XPathArr.get(idx));
	    	  }
	    	  else {
	    		 break;  
	    	  }
		   }
	    }
	    else {
	       int size1 = arg0XPathArr.size();
	       
	       for (int idx = (arg1Int - 1); idx < size1; idx++) {
			  resultArray.add(arg0XPathArr.get(idx)); 
		   }
	    }
	    
	    result = resultArray;
	    
	    
	    return result;
	}

}
