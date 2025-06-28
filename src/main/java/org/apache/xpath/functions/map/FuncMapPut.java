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

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of an XPath 3.1 function, map:put.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapPut extends Function3Args {

	private static final long serialVersionUID = -749857579667076961L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapPut() {
		m_defined_arity = new Short[] { 3 };	
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XObject result = null;
	       
	    Expression arg0 = getArg0();
	    XPathMap xpathMap = null;
	    
	    if (arg0 instanceof Variable) {
	       XObject xObject = ((Variable)arg0).execute(xctxt);
	       xpathMap = (XPathMap)xObject;
	    }
	    else {
	       XObject xObject = arg0.execute(xctxt);
		   xpathMap = (XPathMap)xObject;
	    }
	    
	    Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
	    
	    Expression arg1 = getArg1();
	    XObject mapEntryKey = arg1.execute(xctxt);
	    
	    Expression arg2 = getArg2();
	    XObject mapEntryValue = arg2.execute(xctxt);
	    
	    nativeMap.put(mapEntryKey, mapEntryValue);
	    
	    XPathMap xpathMapResult = new XPathMap();
	    xpathMapResult.setNativeMap(nativeMap);
	    
	    result = xpathMapResult; 
	    	    
	    return result;
	}

}
