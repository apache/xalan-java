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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the array:insert-before function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayInsertBefore extends Function3Args {

	private static final long serialVersionUID = -6993811075719408978L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayInsertBefore() {
		m_defined_arity = new Short[] { 3 };
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0Expr = getArg0();	    
	    Expression arg1Expr = getArg1();	    
	    Expression arg2Expr = getArg2();
	    
	    XPathArray arg0XPathArr = null;
	    
	    List<XObject> nativeArr = null;	    
	    if (arg0Expr instanceof Variable) {
	       XObject xObject = ((Variable)arg0Expr).execute(xctxt);
	       if (xObject instanceof XPathArray) {
	    	  arg0XPathArr = (XPathArray)xObject;
		      nativeArr = arg0XPathArr.getNativeArray();
		   }
	       else {
	    	   throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:insert-before function call, "
                                                                         + "needs to be an array", srcLocator);   
	       }
	    }
	    else {
	    	XObject xObject = arg0Expr.execute(xctxt);
		    if (xObject instanceof XPathArray) {
		       arg0XPathArr = (XPathArray)xObject;
			   nativeArr = arg0XPathArr.getNativeArray();
			}
		    else {
		       throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:insert-before function call, "
	                                                                         + "needs to be an array", srcLocator);   
		    }
	    }
	    
	    XObject arg1XObj = null;
	    if (arg1Expr instanceof Variable) {
	       arg1XObj = ((Variable)arg1Expr).execute(xctxt);
	       if (!((arg1XObj instanceof XSNumericType) || (arg1XObj instanceof XNumber))) {
	    	  throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:insert-before function "
                                                                                               + "call, needs to be an xs:integer value", srcLocator); 
	       }
	    }
	    else {
	       arg1XObj = arg1Expr.execute(xctxt);
	       if (!((arg1XObj instanceof XSNumericType) || (arg1XObj instanceof XNumber))) {
		      throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:insert-before function "
	                                                                                           + "call, needs to be an xs:integer value", srcLocator); 
		   }
	    }
	    
	    XObject arg2XObj = null;
	    if (arg2Expr instanceof Variable) {
		   arg2XObj = ((Variable)arg2Expr).execute(xctxt);
		}
		else {
		   arg2XObj = arg2Expr.execute(xctxt);
		}
	    
	    int arg1Int;	    
	    try {
	       arg1Int = Integer.valueOf(XslTransformEvaluationHelper.getStrVal(arg1XObj));
	       int inpArrSize = nativeArr.size();
	       if ((arg1Int < 1) || (arg1Int > (inpArrSize + 1))) {
	    	   throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:insert-before function all, needs to be "
	    	   		                                                       + "an xs:integer value in the range 1 to (array:size($array) + 1) inclusive", srcLocator);  
	       }	       
	    }
	    catch (NumberFormatException ex) {
	       throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:insert-before function "
                                                                      + "call, needs to be an xs:integer value", srcLocator); 
	    }
	    
	    List<XObject> resultL = nativeArr.subList(0, arg1Int - 1);
	    List<XObject> resultR = nativeArr.subList(arg1Int - 1, nativeArr.size());
	    
	    List<XObject> resultNativeArr = new ArrayList<XObject>();
	    resultNativeArr.addAll(resultL);
	    resultNativeArr.add(arg2XObj);
	    resultNativeArr.addAll(resultR);
	    
	    XPathArray resultArr = new XPathArray();
	    resultArr.setNativeArray(resultNativeArr);
	    
	    result = resultArr;
	    
	    return result;
	}

}
