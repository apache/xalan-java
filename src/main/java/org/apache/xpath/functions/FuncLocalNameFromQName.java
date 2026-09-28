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

import org.apache.xalan.templates.Constants;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.types.XSNCName;

import xml.xpath31.processor.types.XSQName;

/**
 * An implementation of XPath 3.1 function, fn:local-name-from-QName.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncLocalNameFromQName extends FunctionDef1Arg {

	private static final long serialVersionUID = -6264955161789592394L;

	/**
	 * Class constructor.
	 */
	public FuncLocalNameFromQName() {
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
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		XObject arg0Value = getFunctionArgEffectiveValue(m_arg0, xctxt);
		
		if (arg0Value instanceof ResultSequence) {
			ResultSequence rSeq = (ResultSequence)arg0Value;

			if (rSeq.size() == 0) {
				result = new ResultSequence();

				return result;
			}
			else if (rSeq.size() == 1) {
				arg0Value = rSeq.item(0); 
			}
			else {
			    throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'local-name-from-QName' first argument, "
		                                                                                                                                    + "cannot be a sequence with size greater than one.", srcLocator); 
			}
		}

		if (arg0Value instanceof XSQName) {		  
			XSQName xsQname = (XSQName)arg0Value;
			String localPart = xsQname.getLocalPart();	      
			
			if (!(Constants.ANONYMOUS_FUNCTION).equals(localPart)) {
				result = new XSNCName(localPart);
			}
			else {
				result = new ResultSequence(); 
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("XPTY0004: An XPath 3.1 function 'local-name-from-QName' first argument, "
                                                                                                                                        + "is not with an XML schema type 'QName'.", srcLocator);  
		}

		return result;
	}
}
