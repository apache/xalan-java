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

import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the array:put function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayPut extends Function3Args {

	private static final long serialVersionUID = -3699572884716214890L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayPut() {
		m_arity = new Short[] { 3 };
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
	    
	    List<XObject> nativeArr = null;
	    
	    XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    
	    if (xObj0 instanceof XPathArray) {
	    	nativeArr = ((XPathArray)xObj0).getNativeArray();
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("FORG0006 : The first argument of array:put function call, "
	    			         																			+ "needs to be an xdm array", srcLocator);   
	    }
	    
	    XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    
	    if (!((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber))) {
	    	throw new javax.xml.transform.TransformerException("FOAY0001 : The second argument of array:put function "
	    																								+ "call, needs to be an xs:integer "
	    																								+ "value", srcLocator); 
	    }
	    
	    XObject xObj2 = getFunctionArgEffectiveValue(m_arg2, xctxt);
	    
	    int arg1Int = 0;	    
	    
	    try {
	       arg1Int = Integer.valueOf(XslTransformEvaluationHelper.getStrVal(xObj1));
	       
	       int inpArrSize = nativeArr.size();	       
	       if (!((arg1Int < 1) || (arg1Int > inpArrSize))) {
	    	   nativeArr.set(arg1Int - 1, xObj2);
		   	    
	   	       XPathArray xpathArr = new XPathArray();	    
	   	       xpathArr.setNativeArray(nativeArr);
	   	    
	   	       result = xpathArr; 
	       }
	       else {	    	   	   	       
	   	       throw new javax.xml.transform.TransformerException("FOAY0001 : The second argument of array:put function all, needs to be "
                                                                                                        + "an xs:integer value in the range 1 to "
                                                                                                        + "array:size($array) inclusive", srcLocator);
	       }
	    }
	    catch (NumberFormatException ex) {
	       throw new javax.xml.transform.TransformerException("FOAY0001 : The second argument of array:put function "
                                                                                                        + "call, needs to be an xs:integer "
                                                                                                        + "value", srcLocator); 
	    }	    	    
	    
	    return result;
	}

}
