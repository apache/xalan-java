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

import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function, map:contains.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapContains extends Function2Args {

	private static final long serialVersionUID = -749857579667076961L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapContains() {
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
	    
	    XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt); 
	           
	    if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
	    	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' cannot have its first "
	    																											+ "argument as an empty "
	    																											+ "sequence.", srcLocator);  
	    }

	    XPathMap xpathMap = (XPathMap)xObj0;
	    
	    Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
	    
	    XObject arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    
	    if (arg1Obj instanceof XString) {
	       arg1Obj = new XSString(((XString)arg1Obj).str());
	    }
	    
	    if (nativeMap.get(arg1Obj) != null) {
	       result = new XSBoolean(true);	
	    }
	    else {
	       result = new XSBoolean(false);	
	    }
	    
	    return result;
	}

}
