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
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

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
		m_defined_arity = new Short[] { 2, 3 };
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
	    
		XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0Expr = getArg0();	    
	    Expression arg1Expr = getArg1();
	    Expression arg2Expr = getArg2();
	    
	    if ((arg0Expr == null) || (arg1Expr == null)) {
	        throw new javax.xml.transform.TransformerException("FORG0006 : An array:subarray function call must have "
	        		                                                  + "first two arguments.", srcLocator); 
	    }

	    XPathArray arg0XPathArr = null;	    
	    if (arg0Expr instanceof Variable) {
	       XObject xdmInputArr = ((Variable)arg0Expr).execute(xctxt);
	       if (xdmInputArr instanceof XPathArray) {
	    	  arg0XPathArr = (XPathArray)xdmInputArr;
	       }
	       else {
	    	  throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:subarray function call, "
                                                                                              + "needs to be an xdm array.", srcLocator); 
	       }
	    }
	    else {
	       XObject xdmInputArr = arg0Expr.execute(xctxt);
		   if (xdmInputArr instanceof XPathArray) {
			   arg0XPathArr = (XPathArray)xdmInputArr;
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:subarray function call, "
                                                                                              + "needs to be an xdm array.", srcLocator);	   
		   }
	    }
	    
	    XObject arg1XObj = null;
	    if (arg1Expr instanceof Variable) {
	       arg1XObj = ((Variable)arg1Expr).execute(xctxt);	       
	    }
	    else {
	       arg1XObj = arg1Expr.execute(xctxt);	       
	    }
	    
	    int arg1Int = -1;
	    if ((arg1XObj instanceof XSNumericType) || (arg1XObj instanceof XNumber)) {
	       String arg1StrVal = XslTransformEvaluationHelper.getStrVal(arg1XObj);
	       try {
	          arg1Int = (Integer.valueOf(arg1StrVal)).intValue();
	       }
	       catch (NumberFormatException ex) {
	    	  throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument of function call array:subarray is not "
	    	  		                                                           + "an xs:integer value.", srcLocator); 
	       }
	       
	       if ((arg1Int < 1) || (arg1Int > arg0XPathArr.size())) {
	    	  throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument of function call array:subarray must not "
	    	  		                                                           + "be less than 1 or greater than size of input array.", srcLocator); 
	       }
	    }
	    else {
	       throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument of function call array:subarray is not "
	       		                                                                        + "numeric.", srcLocator);
	    }
	    
	    XPathArray resultArray = new XPathArray();
	    
	    int arg2Int = -1;
	    if (arg2Expr != null) {
	       XObject arg2XObj = arg2Expr.execute(xctxt);
	       if ((arg2XObj instanceof XSNumericType) || (arg2XObj instanceof XNumber)) {
		      String arg2StrVal = XslTransformEvaluationHelper.getStrVal(arg2XObj);
		      try {
		    	 arg2Int = (Integer.valueOf(arg2StrVal)).intValue();
		      }
		      catch (NumberFormatException ex) {
		    	 throw new javax.xml.transform.TransformerException("FORG0006 : The 3rd argument provided to function call array:subarray is not "
		    	  		                                                                              + "an xs:integer value.", srcLocator); 
		      }
		   }
	       else {
		      throw new javax.xml.transform.TransformerException("FORG0006 : The 3rd argument present on function call array:subarray "
		      		                                                                        + "is not numeric.", srcLocator);
		   }
	       
	       int resultItemCount = 0;
	       for (int idx = (arg1Int - 1); idx < arg0XPathArr.size(); idx++) {
	    	  if ((++resultItemCount) <= arg2Int) {
		         resultArray.add(arg0XPathArr.get(idx));
	    	  }
	    	  else {
	    		 break;  
	    	  }
		   }
	    }
	    else {	       
	       for (int idx = (arg1Int - 1); idx < arg0XPathArr.size(); idx++) {
			  resultArray.add(arg0XPathArr.get(idx)); 
		   }
	    }
	    
	    result = resultArray;
	    
	    
	    return result;
	}

}
