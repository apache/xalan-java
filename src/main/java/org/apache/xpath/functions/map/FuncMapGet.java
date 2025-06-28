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
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function, map:get.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapGet extends Function2Args {

	private static final long serialVersionUID = -749857579667076961L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapGet() {
		m_defined_arity = new Short[] { 2 };	
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();
	    XObject arg0Obj = null;
	    if (arg0 instanceof SelfIteratorNoPredicate) {
	       XObject xpathContextItem = xctxt.getXPath3ContextItem();
	       if (xpathContextItem != null) {
	    	  arg0Obj = xpathContextItem; 
	       }
	    }
	    else {
	       arg0Obj = arg0.execute(xctxt);
	    }
	    
	    Map<XObject, XObject> nativeMap = null;
	    if ((arg0Obj != null) && (arg0Obj instanceof XPathMap)) {
	       nativeMap = ((XPathMap)arg0Obj).getNativeMap();
	    }
	    else {
	       throw new javax.xml.transform.TransformerException("FORG0006: The 1st argument provided to function call "
	       		                                                            + "map:get, is not an xdm map.", srcLocator);
	    }
	    
	    Expression arg1 = getArg1();
	    XObject arg1Obj = null;
	    if (arg1 instanceof SelfIteratorNoPredicate) {
	    	XObject xpathContextItem = xctxt.getXPath3ContextItem();
	    	if (xpathContextItem != null) {
	    		arg1Obj = xpathContextItem; 
	    	}
	    } 
	    else {
	       arg1Obj = arg1.execute(xctxt);
	       if (arg1Obj instanceof XString) {
	    	  arg1Obj = new XSString(((XString)arg1Obj).str());  
	       }
	    }
	    
	    result = nativeMap.get(arg1Obj);
	    
	    if (result == null) {	       
	       result = new ResultSequence();	
	    }
	    
	    return result;
	}

}
