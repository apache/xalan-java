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
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the array:get function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayGet extends Function2Args {
	
	private static final long serialVersionUID = 9085303963460501516L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayGet() {
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
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();	    	    
	    
	    ResultSequence arg0Seq = null;
	    
	    XObject arg0Value = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    
	    if (arg0Value instanceof XPathArray) {
	    	arg0Seq = getArg0Seq((XPathArray)arg0Value, srcLocator);
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of array:get function call, "
	    			                                                                                    + "needs to be of type array", srcLocator);  
	    }
	    
	    if (m_arg1 instanceof SelfIteratorNoPredicate) {
	       XObject xObj1 = xctxt.getXPath3ContextItem();
	       
	       if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
		      result = getFuncResult(arg0Seq, xObj1, srcLocator);   
		   }
		   else {
		      throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:get function "
		    	  		                                                + "call, needs to be an xs:integer value", srcLocator); 
		   }
	    }
	    else if (m_arg1 instanceof Variable) {
	       XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	       
	       if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
	    	  result = getFuncResult(arg0Seq, xObj1, srcLocator);   
	       }
	       else {
	    	  throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:get function "
	    	  		                                                    + "call, needs to be an xs:integer value", srcLocator); 
	       }
	    }
	    else {
	       XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	       
	       if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
		      result = getFuncResult(arg0Seq, xObj1, srcLocator);   
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("FOAY0001 : The 2nd argument of array:get function "
                                                                         + "call, needs to be an xs:integer value", srcLocator);	   
		   }
	    }
	    
	    return result;
	}

	/**
	 * Get the value of array:get function's first argument, as ResultSequence object.
	 */
	private ResultSequence getArg0Seq(XPathArray arg0Value, SourceLocator srcLocator) {
		ResultSequence arg0Seq = null;

	    List<XObject> nativeArr = arg0Value.getNativeArray();
	    arg0Seq = ElemCopyOf.getResultSequenceFromXPathArray(nativeArr);
		
		return arg0Seq;
	}
	
	/**
	 * Get the result of array:get function call. 
	 */
	private XObject getFuncResult(ResultSequence arg0Seq, XObject arg1, SourceLocator srcLocator) 			                                                                                   throws TransformerException {
		XObject result = null;
		
	    String secondArgStr = XslTransformEvaluationHelper.getStrVal(arg1);
		result = arg0Seq.item((Integer.valueOf(secondArgStr)).intValue() - 1);
		
		return result;
	}

}
