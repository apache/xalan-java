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
 * Implementation of an XPath 3.1 function array:get.
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
	    	XPathArray xpathArr = getNormalizedXdmArray((XPathArray)arg0Value);
	    	
	    	arg0Seq = getXdmSeqFromArray(xpathArr);
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("XPTY0004 : The first argument of XPath 3.1 function array 'get', "
	    			                                                                                                      + "should be of type xdm array.", srcLocator);  
	    }
	    
	    if (m_arg1 instanceof SelfIteratorNoPredicate) {
	       XObject xObj1 = xctxt.getXPath3ContextItem();
	       
	       if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
		      result = getFuncResult(arg0Seq, xObj1, srcLocator);   
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("XPTY0004 : The second argument of XPath 3.1 function array 'get', "
                                                                                                                            + "should be of type schema type integer.", srcLocator);
		   }
	    }
	    else if (m_arg1 instanceof Variable) {
	       XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	       
	       if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
	    	  result = getFuncResult(arg0Seq, xObj1, srcLocator);   
	       }
	       else {
	    	  throw new javax.xml.transform.TransformerException("XPTY0004 : The second argument of XPath 3.1 function array 'get', "
                       																										+ "should be of type schema type integer.", srcLocator); 
	       }
	    }
	    else {
	       XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
	       
	       if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
		      result = getFuncResult(arg0Seq, xObj1, srcLocator);   
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("XPTY0004 : The second argument of XPath 3.1 function array 'get', "
                       																									  + "should be of type schema type integer.", srcLocator);	   
		   }
	    }
	    
	    return result;
	}

	/**
	 * Method definition, to get an xdm sequence object, from the
	 * supplied xdm array.
	 * 
	 * @param xpathArr                       The supplied xdm array, object
	 *                                       instance.
	 * @return                               An xdm sequence, object
	 */
	private ResultSequence getXdmSeqFromArray(XPathArray xpathArr) {
		
		ResultSequence result = null;

	    List<XObject> nativeArr = xpathArr.getNativeArray();
	    
	    result = ElemCopyOf.getResultSequenceFromXPathArray(nativeArr);
		
		return result;
	}
	
	/**
	 * Method definition, to get an XPath 3.1 function array 'get'
	 * result using the supplied contents of the array (as, sequence)
	 * and value of an array index.
	 * 
	 * @param rSeq                               The supplied contents of the
	 *                                           array, as an xdm sequence.
	 * @param arrIndexObj                        An XObject instance, representing an 
	 *                                           array index value, which is an array location 
	 *                                           from which array 'get' function value is sought.
	 *                                           
	 * @param srcLocator                         An XSL transformation SourceLocator object 
	 * @return                                   The result of XPath 3.1 function array 'get'.                          
	 * @throws TransformerException
	 */
	private XObject getFuncResult(ResultSequence rSeq, XObject arrIndexObj, SourceLocator srcLocator) 			                                                                                   throws TransformerException {
		
		XObject result = null;
		
	    String secondArgStr = XslTransformEvaluationHelper.getStrVal(arrIndexObj);
	    
	    int intValue1 = 0;
	    
	    try {
	       intValue1 = (Integer.valueOf(secondArgStr)).intValue();
	    }
	    catch (NumberFormatException ex) {
	       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function array 'get' second argument "
	       		                                                                                     + "should be of schema type integer.", srcLocator);
	    }
	    
	    int size1 = rSeq.size();
	    
	    if ((intValue1 >= 1) && (intValue1 <= size1)) {
		   result = rSeq.item(intValue1 - 1);
	    }
	    else {
	       throw new javax.xml.transform.TransformerException("FOAY0001 : An XPath 3.1 function array 'get' second argument "
                                                                                                     + "should be of schema type integer with "
                                                                                                     + "value between 1 and supplied array size inclusive.", srcLocator);
	    }
		
		return result;
	}

}
