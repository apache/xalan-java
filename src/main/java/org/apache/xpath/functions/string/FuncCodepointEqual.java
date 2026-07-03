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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 function fn:codepoint-equal.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCodepointEqual extends Function2Args {
    
    private static final long serialVersionUID = -2518383964235644671L;
    
    /**
	 * Class constructor.
	 */
    public FuncCodepointEqual() {
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
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();

        XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt); 
        
        XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
        
        if ((xObj0 instanceof XSNumericType) || (xObj0 instanceof XNumber)) {
        	throw new TransformerException("XPTY0004 : An XPath 3.1 function 'codepoint-equal' accepts string as its first "
        			                                                                               + "argument. But the supplied argument "
        			                                                                               + "is numeric.", srcLocator);
        }
        
        if ((xObj1 instanceof XSNumericType) || (xObj1 instanceof XNumber)) {
        	throw new TransformerException("XPTY0004 : An XPath 3.1 function 'codepoint-equal' accepts string as its second "
        			                                                                               + "argument. But the supplied argument "
        			                                                                               + "is numeric.", srcLocator);
        }
        
        // If either of the argument to this function is an empty sequence, 
        // the function returns an empty sequence.
        
        // Get the string value of first argument, to function call fn:codepoint-equal
        
        String arg0Str = null;
        
        if (xObj0 instanceof XMLNodeCursorImpl) {
        	XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)xObj0;
        	if (nodeSet.getLength() == 0) {
        		result = new ResultSequence();

        		return result;
        	}
        	else {
        		arg0Str = nodeSet.str(); 
        	}
        }
        else if (xObj0 instanceof ResultSequence) {
        	ResultSequence resultSequence = (ResultSequence)xObj0;
        	if (resultSequence.size() == 0) {
        		result = new ResultSequence();

        		return result; 
        	}
        	else {
        		arg0Str = getResultSequenceStrValue(resultSequence); 
        	}
        }
        else if (xObj0 instanceof XSAnyType) {
           arg0Str = ((XSAnyType)xObj0).stringValue();  
        }
        else {
           arg0Str = xObj0.str();  
        }
        
        // Get the string value of second argument, to function call fn:codepoint-equal
        
        String arg1Str = null;
        
        if (xObj1 instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl nodeSet = (XMLNodeCursorImpl)xObj1;
           if (nodeSet.getLength() == 0) {
        	   result = new ResultSequence();

        	   return result; 
           }
           else {
        	   arg1Str = nodeSet.str();  
           } 
        }
        else if (xObj1 instanceof ResultSequence) {
           ResultSequence resultSequence = (ResultSequence)xObj1;
           if (resultSequence.size() == 0) {
        	   result = new ResultSequence();

        	   return result; 
           }
           else {
        	   arg1Str = getResultSequenceStrValue(resultSequence);    
           } 
        }
        else if (xObj1 instanceof XSAnyType) {
           arg1Str = ((XSAnyType)xObj1).stringValue();  
        }
        else {
           arg1Str = xObj1.str();  
        }
        
        // Comparison of string arguments for this function, using 'Unicode codepoint collation'
        XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
        
        int strCmpResult = xPathCollationSupport.compareStringsUsingCollation(arg0Str, arg1Str, xctxt.getDefaultCollation());
        
        if (strCmpResult == 0) {
        	// The strings are equal codepoint by codepoint            
        	result = new XSBoolean(true); 
        }
        else {
        	// The strings are not equal codepoint by codepoint
        	result = new XSBoolean(false); 
        }
        
        return result;
    }
    
    /**
     * Method definition, to get string value for the supplied
     * xdm sequence object. 
     * 
     * @param seq1                         The supplied xdm sequence object
     * @return                             String value for the supplied
     *                                     xdm sequence object.
     */
    private String getResultSequenceStrValue(ResultSequence seq1) throws TransformerException {
        
    	String result = null;
        
        StringBuffer strBuff = new StringBuffer(); 
        
        int size1 = seq1.size();
        for (int idx = 0; idx < size1; idx++) {
           XObject xObj = seq1.item(idx);
           
           if ((xObj instanceof XSNumericType) || (xObj instanceof XNumber)) {
        	  throw new TransformerException("XPTY0004 : An XPath 3.1 function 'codepoint-equal' accepts string for both of "
        	  		                                                                                         + "its arguments, but one or both of supplied "
        	  		                                                                                         + "argument contains a numeric value.");  
           }
           
           if (xObj instanceof XMLNodeCursorImpl) {
              strBuff.append(((XMLNodeCursorImpl)xObj).str());
           }
           else if (xObj instanceof XSAnyType) {
              strBuff.append(((XSAnyType)xObj).stringValue()); 
           }
           else {
              strBuff.append(xObj.str()); 
           }
        }
        
        return result; 
    }

}
