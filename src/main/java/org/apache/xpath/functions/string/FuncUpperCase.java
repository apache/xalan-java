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
package org.apache.xpath.functions.string;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionDef1Arg;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:upper-case.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncUpperCase extends FunctionDef1Arg
{
    
     private static final long serialVersionUID = 2596924943524147578L;
     
     /**
 	  * Class constructor.
 	  */
      public FuncUpperCase() {
 	      m_arity = new Short[] { 1 };
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
    	  
    	  XObject xObjArg0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
    	  
    	  if (!((xObjArg0 instanceof XNumber) || (xObjArg0 instanceof XSNumericType))) {
    		 String strValueOfArg = (getArg0AsString(xctxt)).toString();
    		 
    		 result = new XSString(strValueOfArg.toUpperCase());
    	  }
    	  else {
    		 throw new TransformerException("XPTY0004 : An XPath 3.1 function 'upper-case' requires a "
    		 		                                                          + "string argument, but the supplied "
    		 		                                                          + "value is numeric.",srcLocator);  
    	  }
    	  
    	  return result;
    	  
      }
}
