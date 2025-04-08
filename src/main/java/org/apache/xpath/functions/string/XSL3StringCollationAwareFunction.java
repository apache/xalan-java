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
package org.apache.xpath.functions.string;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.patterns.NodeTest;

/**
 * A class definition, specifying implementation to support XPath 3.1
 * string collation aware functions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSL3StringCollationAwareFunction extends FunctionMultiArgs {
	
	private static final long serialVersionUID = -3275872194097713505L;

	/**
	 * Given an XPath compiled expression, get evaluated string 
	 * value from an expression.
	 * 
	 * @param xctxt			XPath context object
	 * @param arg			An XPath compiled expression object
	 * @return				Computed string value for an XPath expression
	 * @throws TransformerException
	 */
	protected String getArgStringValue(XPathContext xctxt, Expression arg) throws TransformerException {
		String result = null;
		
		if (arg instanceof NodeTest) {
			NodeTest nodeTest = (NodeTest)arg;
		    XMLString xmlStr = nodeTest.xstr(xctxt);
		    result = xmlStr.toString();
		    if ("".equals(result)) {
		    	XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
		    	if (xpath3ContextItem != null) {
				   result = XslTransformEvaluationHelper.getStrVal(xpath3ContextItem);
		    	}
		    }
		}
		else {
			XObject xpathEvalResult = arg.execute(xctxt);
			result = XslTransformEvaluationHelper.getStrVal(xpathEvalResult);
		}
		
		return result;
	}

}
