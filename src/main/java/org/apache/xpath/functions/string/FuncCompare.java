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
package org.apache.xpath.functions.string;

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 function fn:compare.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCompare extends XSL3StringCollationAwareFunction {
    
    private static final long serialVersionUID = 4648998919300586767L;
    
    /**
	 * Class constructor.
	 */
    public FuncCompare() {
	   m_arity = new Short[] { 2, 3 };
    }
    
    /**
     * The number of arguments passed to the fn:compare function 
     * call.
     */
    private int numOfArgs = 0;
    
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
	    
	    /**
	     * An XPath expression FuncArgPlaceholder if not null, for one or more of
	     * the function arguments, implies that the corresponding function argument
	     * has been specified as ? i.e the function call is a partial function
	     * application as defined by XPath 3.1 spec.
	     * 
	     * For this, fn:compare function returns its result as XPathInlineFunction 
	     * object instance.
	     */
	    
	    String arg0Str = null;	        
	    
	    if ((m_arg0 != null) && !(m_arg0 instanceof FuncArgPlaceholder)) {
	    	XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

	    	if (xObj0 instanceof ResultSequence) {
	    	   if (((ResultSequence)xObj0).size() == 0) {
	    	      result = new ResultSequence();
	    	      
	    	      return result;
	    	   }
	    	}
	    	else if (xObj0 instanceof XMLNodeCursorImpl) {
	    	   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj0;
	    	   
	    	   if (xmlNodeCursorImpl.getLength() == 0) {
	    		  result = new ResultSequence();
	    		  
	    		  return result;
	    	   }
	    	}
	    	
	    	if ((xObj0 instanceof XSNumericType) || (xObj0 instanceof XNumber)) {
	    	   throw new TransformerException("XPTY0004 : An XPath 3.1 function 'compare', cannot have a numeric "
	    	   		                                                                                    + "comparand argument.", srcLocator);
	    	}
	    	
	    	arg0Str = XslTransformEvaluationHelper.getStrVal(xObj0);
	    }
	    
	    String arg1Str = null;	        
	    
	    if ((m_arg1 != null) && !(m_arg1 instanceof FuncArgPlaceholder)) {	    	
	    	XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);	    	
	    	Object obj1 = xObj1.object();
	    	
	    	if (!(obj1 instanceof FuncArgPlaceholder)) {
	    		if (xObj1 instanceof ResultSequence) {
	    			if (((ResultSequence)xObj1).size() == 0) {
	    				result = new ResultSequence();

	    				return result;
	    			}
	    		}
	    		else if (xObj1 instanceof XMLNodeCursorImpl) {
	    			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj1;

	    			if (xmlNodeCursorImpl.getLength() == 0) {
	    				result = new ResultSequence();

	    				return result;
	    			}
	    		}
	    		
	    		if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
	 	    	   throw new TransformerException("XPTY0004 : An XPath 3.1 function 'compare', cannot have a numeric "
	 	    	   		                                                                                    + "comparand argument.", srcLocator);
	 	    	}
	    		
	    		arg1Str = XslTransformEvaluationHelper.getStrVal(xObj1);
	    	}
	    }
	    
	    String collationUri = null;	        
	    
	    if ((m_arg2 != null) && !(m_arg2 instanceof FuncArgPlaceholder)) {
	    	XObject xObj2 = getFunctionArgEffectiveValue(m_arg2, xctxt);	    	
	    	Object obj2 = xObj2.object();
	    	
	    	if (!(obj2 instanceof FuncArgPlaceholder)) {	    			    		
	    		collationUri = XslTransformEvaluationHelper.getStrVal(xObj2);
	    	}
	    }
	    
	    XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
	    
	    if ((arg0Str != null) && (arg1Str != null)) {	    	
	    	if (numOfArgs == 2) {
	    		// Using default collation for string comparison
	    		collationUri = xctxt.getDefaultCollation();
	    		
	    		int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(arg0Str, arg1Str, collationUri);

	    		result = new XSInteger(BigInteger.valueOf((long)comparisonResult));
	    	}
	    	else if (collationUri != null) {
	    		int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(arg0Str, arg1Str, collationUri);

	    		result = new XSInteger(BigInteger.valueOf((long)comparisonResult)); 	
	    	}
	    	else {
	    	    String xpathInlineFuncExprStr = "function($collation) { compare('" + arg0Str + "', '" + arg1Str + "', $collation) }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
        }
	    else if ((arg0Str == null) && (arg1Str != null)) {
	    	if (numOfArgs == 2) {
                String xpathInlineFuncExprStr = "function($arg0) { compare($arg0, '" + arg1Str + "') }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    	else if (collationUri != null) {
                String xpathInlineFuncExprStr = "function($arg0) { compare($arg0, '" + arg1Str + "', '" + collationUri + "') }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    	else {
                String xpathInlineFuncExprStr = "function($arg0, $collation) { compare($arg0, '" + arg1Str + "', $collation) }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    }
        else if ((arg0Str != null) && (arg1Str == null)) {
        	if (numOfArgs == 2) {
                String xpathInlineFuncExprStr = "function($arg1) { compare('" + arg0Str + "', $arg1) }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    	else if (collationUri != null) {
                String xpathInlineFuncExprStr = "function($arg1) { compare('" + arg0Str + "', $arg1, '" + collationUri + "') }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    	else {
                String xpathInlineFuncExprStr = "function($arg1, $collation) { compare('" + arg0Str + "', $arg1, $collation) }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    }
        else if ((arg0Str == null) && (arg1Str == null)) {
        	if (numOfArgs == 2) {
                String xpathInlineFuncExprStr = "function($arg0, $arg1) { compare($arg0, $arg1) }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    	else if (collationUri != null) {
                String xpathInlineFuncExprStr = "function($arg0, $arg1) { compare($arg0, $arg1, '" + collationUri + "') }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
	    	else {
                String xpathInlineFuncExprStr = "function($arg0, $arg1, $collation) { compare($arg0, $arg1, $collation) }";
	    	    
	    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		    	
		    	result = xpathObj.execute(xctxt, DTM.NULL, null);
	    	}
        }
	
	    return result;
    }
    
    /**
     * Check that the number of arguments passed to this function is correct.
     *
     * @param argNum The number of arguments that is being passed to the function.
     *
     * @throws WrongNumberArgsException
     */
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException
    {      
       numOfArgs = argNum;
    }
    
    /**
     * Constructs and throws a WrongNumberArgException with the appropriate
     * message for this function object.
     *
     * @throws WrongNumberArgsException
     */
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                                     XPATHErrorResources.ER_TWO_OR_THREE, 
                                                                     null));
    }

}
