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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of an XPath 3.1 function, map:remove.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapRemove extends Function2Args {

	private static final long serialVersionUID = 85207005600072930L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapRemove() {
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
	    
	    XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
       
	    if (xObj0 instanceof ResultSequence) {
	    	if ((((ResultSequence)xObj0).size() == 0) || (((ResultSequence)xObj0).size() > 1)) {
	    	    throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'remove' cannot have its first "
	    																							     + "argument as an empty sequence, or "
	    																							     + "a sequence with size greater than one.", srcLocator);
	    	}
	    }
	    
	    if (xObj0 instanceof ResultSequence) {
	        xObj0 = ((ResultSequence)xObj0).item(0); 
	    }
	    
	    XPathMap arg0Map = null;

	    if (xObj0 instanceof XPathMap) {
	    	arg0Map = (XPathMap)xObj0;   
	    }
	    else {
	    	throw new javax.xml.transform.TransformerException("FORG0006: An XPath 3.1 map function 'remove' has been called with an argument "
	    																											+ "that is not an xdm map.", srcLocator);  
	    }
	    	    	    
	    	    
	    XObject arg1Obj = null;
	    ResultSequence inpSeq1 = null;
	    if (m_arg1 instanceof Variable) {
	    	arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    	
	    	inpSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Obj, xctxt);
	    } 
	    else {
	    	arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    	
	    	inpSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Obj, xctxt);
	    }
	    
	    Map<XObject, XObject> nativeResultMap = new HashMap<XObject, XObject>();
	    
	    Map<XObject, XObject> nativeMapArg0 = arg0Map.getNativeMap();
	    
	    Set<XObject> keysInMap = nativeMapArg0.keySet();
	    Iterator<XObject> iter = keysInMap.iterator();	    
	    String xpathDefaultCollation = xctxt.getDefaultCollation();
	    XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
	    while (iter.hasNext()) {
	    	XObject key = iter.next();
	    	if (!XslTransformEvaluationHelper.contains(inpSeq1, key, xpathDefaultCollation, xpathCollationSupport)) {
	    	   XObject xObj = nativeMapArg0.get(key);
	    	   nativeResultMap.put(key, xObj);
	    	}
	    }
	    
	    XPathMap resultMap = new XPathMap();
	    resultMap.setNativeMap(nativeResultMap);
	    
	    result = resultMap;
	    
	    return result;
	}

}
