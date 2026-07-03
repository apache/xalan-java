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
package org.apache.xpath.functions.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;

/**
 * Implementation of an XPath 3.1 function, map:keys.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapKeys extends FunctionOneArg {
	
	private static final long serialVersionUID = -7109316358636297324L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapKeys() {
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
		
		ResultSequence resultSeq = new ResultSequence();

		SourceLocator srcLocator = xctxt.getSAXLocator();

		XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);

		if (xObj0 instanceof ResultSequence) { 
			ResultSequence rSeq = (ResultSequence)xObj0; 			
			if ((rSeq.size() == 0) || (rSeq.size() > 1)) {
			   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'keys' cannot have "
			   		                                                                                    + "its first argument as an empty sequence, "
			   		                                                                                    + "or a sequence with size greater than one.", srcLocator);
			}
		}

		XObject xObj1 = null;
		if (xObj0 instanceof ResultSequence) {
			xObj1 = ((ResultSequence)xObj0).item(0); 
		}
		else {
		    xObj1 = xObj0; 
		}
		
		XPathMap xpathMap = null;
		if (!(xObj1 instanceof XPathMap)) {
			throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'keys' requires its first argument to be "
																									    + "an xdm map.", srcLocator);
		}
		else {
		    xpathMap = (XPathMap)xObj1;
		}
		
		Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
		Set<XObject> keySet = nativeMap.keySet();
		Iterator<XObject> iter = keySet.iterator();
		while (iter.hasNext()) {
			XObject keyVal = iter.next();
			resultSeq.add(keyVal);
		}

		return resultSeq;
	}

}
