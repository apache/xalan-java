/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
 * Implementation of the QName() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncXPath31QName extends Function2Args
{

	private static final long serialVersionUID = -8738986526447227857L;

	/**
      * Execute the function. The function must return a valid object.
      * 
      * @param xctxt The current execution context.
      * @return A valid XObject.
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
    	
    	Expression arg0 = getArg0();    	
    	if (!isXMLNullNamespace(arg0, xctxt)) {
    	   XObject seqExprValue = arg0.execute(xctxt);
     	   nsUri = XslTransformEvaluationHelper.getStrVal(seqExprValue);
    	}
    	
    	Expression arg1 = getArg1();
    	XObject arg1ExprValue = arg1.execute(xctxt);
    	String arg1Str = XslTransformEvaluationHelper.getStrVal(arg1ExprValue);
    	if (XMLChar.isValidName(arg1Str)) {
    	   // The string value 'arg1Str' is an XML valid name, according to XML 1.0 specification
    	   if (arg1Str.contains(":")) {
    		  if (isXMLNullNamespace(arg0, xctxt)) {
    			 throw new javax.xml.transform.TransformerException("FOCA0002 : The string value of second argument of function "
    			 		                                                   + "call fn:QName contains the character ':', and therefore the "
    			 		                                                   + "first argument cannot represent an XML 'no namespace.'", srcLocator);  
    		  }
			  nsPrefix = arg1Str.substring(0, arg1Str.indexOf(':'));                	
			  localPart = arg1Str.substring(arg1Str.indexOf(':') + 1);  
		   }
		   else {
		      localPart = arg1Str;   
		   }
    	}
    	else {
    	   throw new javax.xml.transform.TransformerException("FOCA0002 : The string value of second argument of function call "
                                                                     + "fn:QName, is not a valid lexical value of a QName.", srcLocator);
    	}
    	
    	result = new XSQName(nsPrefix, localPart, nsUri);
    	    	    	
        return result;  
    }
    
    /**
     * Check whether the first argument of method call fn:QName, represents
     * an XML null namespace.
     */
    private boolean isXMLNullNamespace(Expression seqExpr, XPathContext xctxt) throws TransformerException {
    	boolean isNullXMLNamespace = false;
    	
    	if (seqExpr != null) {
    	   XObject seqExprValue = seqExpr.execute(xctxt);
    	   String nsUri = XslTransformEvaluationHelper.getStrVal(seqExprValue);
    	   if ((nsUri == null) || (nsUri.length() == 0)) {
    		  isNullXMLNamespace = true; 
    	   }
    	}
    	
    	return isNullXMLNamespace; 
    }


}
