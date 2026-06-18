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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.util.XMLChar;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of XPath 3.1 function fn:QName.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncQName extends Function2Args
{

	private static final long serialVersionUID = -8738986526447227857L;
	
	/**
	 * Class constructor.
	 */
	public FuncQName() {
		m_defined_arity = new Short[] { 2 };
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

    	String nsPrefix = null;
    	String nsUri = null;
    	String localPart = null;
    	 	
    	if (!isXMLNullNamespace(m_arg0, xctxt)) {
    	   XObject seqExprValue = getFunctionArgEffectiveValue(m_arg0, xctxt);
    	   
     	   nsUri = XslTransformEvaluationHelper.getStrVal(seqExprValue);
    	}
    	
    	XObject arg1ExprValue = getFunctionArgEffectiveValue(m_arg1, xctxt);
    	
    	String arg1Str = XslTransformEvaluationHelper.getStrVal(arg1ExprValue);
    	if (XMLChar.isValidName(arg1Str)) {
    	   // The string value 'arg1Str' is an XML valid name, according to XML 1.0 specification
    	   if (arg1Str.contains(":")) {
    		  if (isXMLNullNamespace(m_arg0, xctxt)) {
    			 throw new javax.xml.transform.TransformerException("FOCA0002 : The string value of second argument of XPath 3.1 function "
    			 		                                                   + "call 'QName' contains the character ':', and therefore the "
    			 		                                                   + "first argument cannot represent an XML null namespace.", srcLocator);  
    		  }
			  nsPrefix = arg1Str.substring(0, arg1Str.indexOf(':'));                	
			  localPart = arg1Str.substring(arg1Str.indexOf(':') + 1);  
		   }
		   else {
		      localPart = arg1Str;   
		   }
    	}
    	else {
    	   throw new javax.xml.transform.TransformerException("FOCA0002 : The string value of second argument of XPath 3.1 function call "
                                                                     + "'QName', is not a valid lexical value of a QName.", srcLocator);
    	}
    	
    	result = new XSQName(nsPrefix, localPart, nsUri);
    	    	    	
        return result;  
    }
    
    /**
     * Method definition, to check whether the first argument of method call fn:QName, 
     * represents an XML null namespace.
     */
    private boolean isXMLNullNamespace(Expression expr1, XPathContext xctxt) throws TransformerException {
    	
    	boolean result = false;
    	
    	if (expr1 != null) {
    	   XObject xObj = getFunctionArgEffectiveValue(expr1, xctxt);
    	   
    	   String nsUri = XslTransformEvaluationHelper.getStrVal(xObj);
    	   if ((nsUri == null) || (nsUri.length() == 0)) {
    		  result = true; 
    	   }
    	}
    	
    	return result; 
    }


}
