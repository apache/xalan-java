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

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;

/**
 * Implementation of the array:append function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayAppend extends Function2Args {
	
	private static final long serialVersionUID = -4710039738232164809L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayAppend() {
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
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		XPathArray xpathArr = null;

		XObject xdmInputArr = getFunctionArgEffectiveValue(m_arg0, xctxt);

		if (xdmInputArr instanceof XPathArray) {
			xpathArr = (XPathArray)xdmInputArr;
		}
		else {
			throw new javax.xml.transform.TransformerException("FORG0006 : The first argument of array:append function call, "
					                                                                                     + "needs to be an xdm array", srcLocator);	   
		}

		XObject xdmValueToAppend = getFunctionArgEffectiveValue(m_arg1, xctxt);
		
		xpathArr.add(xdmValueToAppend);

		result = xpathArr;

		return result;
	}

}
