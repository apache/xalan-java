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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of the unparsed-text-lines() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncUnparsedTextLines extends FuncUnparsedText {

	private static final long serialVersionUID = 2726874035370568232L;

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
		
		XObject strContentsFromUrl = super.execute(xctxt);
		
		String urlStrValueContents = XslTransformEvaluationHelper.getStrVal(strContentsFromUrl);
		String[] strLines = urlStrValueContents.split("\r\n|\r|\n");
		
		ResultSequence rSeq = new ResultSequence();		
		for (int idx = 0; idx < strLines.length; idx++) {
			String strLine = strLines[idx];
			rSeq.add(new XSString(strLine));
		}
		
		result = rSeq;		
		
		return result;
	}
}
