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

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of XPath 3.1 fn:namespace-uri-from-QName function.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncNamespaceUriFromQName extends FunctionDef1Arg {

	private static final long serialVersionUID = 4356910888178229773L;
	
	/**
	 * Class constructor.
	 */
	public FuncNamespaceUriFromQName() {
		m_defined_arity = new Short[] { 1 };
	}

   /**
   * Implementation of the function. The function must return
   * a valid object.
   * 
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
   public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

	  XObject result = null;
    
      SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  Expression arg0 = getArg0();
	  XObject arg0Value = arg0.execute(xctxt);
	  
	  if (arg0Value instanceof XSQName) {
		 result = new XSAnyURI(((XSQName)arg0Value).getNamespaceUri()); 
	  }
	  else {
		 throw new javax.xml.transform.TransformerException("FOAP0001: The first argument within fn:namespace-uri-from-QName "
		 		                                                         + "function call is not of type xs:QName", srcLocator);  
	  }

      return result;
  }
}
