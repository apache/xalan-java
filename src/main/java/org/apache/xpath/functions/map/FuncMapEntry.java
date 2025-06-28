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

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function, map:entry.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapEntry extends Function2Args {

	private static final long serialVersionUID = -1419049097023024612L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapEntry() {
		m_defined_arity = new Short[] { 2 };	
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XObject result = null;	       
	       
	    Expression arg0 = getArg0();
	    Expression arg1 = getArg1();
	    
	    XObject resultMapEntryKey = null;
	    XObject resultMapEntryVal = null;
	    
	    if (arg0 instanceof Variable) {
	       resultMapEntryKey = ((Variable)arg0).execute(xctxt);
	    }
	    else {
	       resultMapEntryKey = arg0.execute(xctxt);
	    }
	    
	    if (resultMapEntryKey instanceof XString) {
	    	resultMapEntryKey = new XSString(((XString)resultMapEntryKey).str());
	    }
	    
	    if (arg1 instanceof Variable) {
	       resultMapEntryVal = ((Variable)arg1).execute(xctxt);
		}
		else {
		   resultMapEntryVal = arg1.execute(xctxt);
		}
	    
	    if (resultMapEntryVal instanceof XString) {
	       resultMapEntryVal = new XSString(((XString)resultMapEntryVal).str());
	    }
	    
	    XPathMap resultMap = new XPathMap();
	    resultMap.put(resultMapEntryKey, resultMapEntryVal);
	    
	    result = resultMap;
	    
	    return result;
	}

}
