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
package org.apache.xpath.operations;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

/**
 * Class definition, to implement an XPath 3.1 operator "||".
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class StrConcat extends XPathOperator
{

   private static final long serialVersionUID = -820873651965512508L;

   /**
    * Apply an XPath operator to its two operands, and return the result.
    *
    * @param left  non-null reference to an XPath operator's evaluated 
    *              first operand.              
    * @param right non-null reference to an XPath operator's evaluated 
    *              second operand.
    *
    * @return non-null reference to an XObject object instance, that 
    *         represents the result of XPath operator evaluation. 
    *
    * @throws javax.xml.transform.TransformerException
    */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
    	XObject result = null;
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator(); 

    	XObject xpath3CtxtItem = xctxt.getXPath3ContextItem();
    	
    	XObject xObj0 = null;    	
    	
    	if ((m_left instanceof SelfIteratorNoPredicate) && (xpath3CtxtItem != null)) {
    	   xObj0 = xpath3CtxtItem; 
    	}
    	else {
    	   xObj0 = m_left.execute(xctxt);
    	}
    	
    	if (xObj0 instanceof ResultSequence) {
    	   ResultSequence rSeq = (ResultSequence)xObj0;
    	   
    	   if (rSeq.size() > 1) {
    		  throw new TransformerException("XPTY0004 : An XPath 3.1 operator '||' first argument is a "
    		  		                                                                                    + "sequence with cardinality greater "
    		  		                                                                                    + "than 1. Required cardinality is 0, or 1.", srcLocator);   
    	   }
    	}

    	XObject xObj1 = null;
    	
    	if ((m_right instanceof SelfIteratorNoPredicate) && (xpath3CtxtItem != null)) {
    	   xObj1 = xpath3CtxtItem; 
    	}
    	else {
    	   xObj1 = m_right.execute(xctxt);
    	}
    	
    	if (xObj1 instanceof ResultSequence) {
     	   ResultSequence rSeq = (ResultSequence)xObj1;
     	   
     	   if (rSeq.size() > 1) {
     		  throw new TransformerException("XPTY0004 : An XPath 3.1 operator '||' second argument is a "
     		  		                                                                                     + "sequence with cardinality greater "
     		  		                                                                                     + "than 1. Required cardinality is 0, or 1.", srcLocator);   
     	   }
     	}

    	result = new XString(XslTransformEvaluationHelper.getStrVal(xObj0) + XslTransformEvaluationHelper.getStrVal(xObj1));

    	return result;
    }
    
    /**
     * Apply an XPath operator to its two operands, and return the result.
     *
     * @param left  non-null reference to an XPath operator's evaluated 
     *              first operand.              
     * @param right non-null reference to an XPath operator's evaluated 
     *              second operand.
     *
     * @return non-null reference to an XObject object instance, that 
     *         represents the result of XPath operator evaluation. 
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject operate(XObject left, XObject right) {
    	
    	XObject result = null;
    	
    	result = new XString(XslTransformEvaluationHelper.getStrVal(left) + XslTransformEvaluationHelper.getStrVal(right));
    	
    	return result;
    }

}
