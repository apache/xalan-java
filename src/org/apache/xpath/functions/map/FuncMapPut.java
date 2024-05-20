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

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of an map:put function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapPut extends Function3Args {

	private static final long serialVersionUID = -749857579667076961L;

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();   // 'map' argument
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
	    
	    Expression arg1 = getArg1();    // 'key' argument
	    XObject arg1Obj = arg1.execute(xctxt);
	    ResultSequence mapEntryKey = null;
	    if (!(arg1Obj instanceof ResultSequence)) {
	       mapEntryKey = new ResultSequence();
	       mapEntryKey.add(arg1Obj);	       
	    }
	    else {
	       mapEntryKey = (ResultSequence)arg1Obj;
	    }
	    
	    Expression arg2 = getArg2();   // 'value' argument
	    XObject arg2Obj = arg2.execute(xctxt);
	    ResultSequence mapEntryValue = null;
	    if (!(arg2Obj instanceof ResultSequence)) {
	       mapEntryValue = new ResultSequence();
	       mapEntryValue.add(arg2Obj);	       
	    }
	    else {
	       mapEntryValue = (ResultSequence)arg2Obj; 
	    }
	    
	    nativeMap.put(mapEntryKey, mapEntryValue);
	    
	    XPathMap xpathMapResult = new XPathMap();
	    xpathMapResult.setNativeMap(nativeMap);
	    
	    result = xpathMapResult; 
	    	    
	    return result;
	}

}
