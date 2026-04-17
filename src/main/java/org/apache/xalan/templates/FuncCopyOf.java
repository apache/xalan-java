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
package org.apache.xalan.templates;

import javax.xml.transform.SourceLocator;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XSLT 3.0 function copy-of.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCopyOf extends FunctionMultiArgs {

	private static final long serialVersionUID = -7148329234400897227L;
	
	/**
	 * Class constructor.
	 */
	public FuncCopyOf() {
		m_defined_arity = new Short[] { 0, 1 };
	}
	
	/**
	 * Evaluate the function. The function must return
	 * a valid object.
	 * 
	 * @param xctxt The current execution context
	 * @return A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		if (m_arg1 != null) {
			throw new javax.xml.transform.TransformerException("FOAP0001 : An XSL function fn:copy-of may be called with only 0 or 1 arguments.", srcLocator); 
		}

		if (m_arg0 != null) {
			XObject arg0Value = m_arg0.execute(xctxt);

			result = arg0Value;
		}
		else if (xctxt.getXPath3ContextItem() != null) {
			result = xctxt.getXPath3ContextItem(); 
		}
		else {
			final int sourceNode = xctxt.getCurrentNode();

			if (sourceNode != DTM.NULL) {
               XMLNodeCursorImpl xmlNodeCursorImpl = new XMLNodeCursorImpl(sourceNode, xctxt);
               
               result = xmlNodeCursorImpl; 
			}
			else {
				result = new ResultSequence(); 
			}
		}

		return result;
	}

}
