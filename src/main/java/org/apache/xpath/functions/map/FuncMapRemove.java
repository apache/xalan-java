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
import org.apache.xpath.Expression;
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

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();
	    Expression arg1 = getArg1();
	    
	    XPathMap arg0Map = null;
	    
	    if (arg0 instanceof Variable) {
	       XObject arg0Obj = ((Variable)arg0).execute(xctxt);
	       if (arg0Obj instanceof XPathMap) {
	    	  arg0Map = (XPathMap)arg0Obj;   
	       }
	       else {
	    	  throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st argument provided to function call "
                                                                        + "map:remove is not an xdm map, or cannot be cast to an xdm map.", srcLocator);  
	       }
	    }
	    else {
	       XObject arg0Obj = arg0.execute(xctxt);
	       if (arg0Obj instanceof XPathMap) {
		      arg0Map = (XPathMap)arg0Obj;   
		   }
		   else {
			  throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st argument provided to function call "
                                                                        + "map:remove is not an xdm map, or cannot be cast to an xdm map.", srcLocator);   
		   }
	    }	    	    
	    	    
	    XObject arg1Obj = null;
	    ResultSequence inpSeq1 = null;
	    if (arg1 instanceof Variable) {
	    	arg1Obj = ((Variable)arg1).execute(xctxt);
	    	inpSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Obj, xctxt);
	    } 
	    else {
	    	arg1Obj = arg1.execute(xctxt);
	    	inpSeq1 = XslTransformEvaluationHelper.getResultSequenceFromXObject(arg1Obj, xctxt);
	    }
	    
	    Map<XObject, XObject> nativeResultMap = new HashMap<XObject, XObject>();
	    
	    Map<XObject, XObject> nativeMap = arg0Map.getNativeMap();
	    Set<XObject> keysInMap = nativeMap.keySet();
	    Iterator<XObject> iter = keysInMap.iterator();	    
	    String xpathDefaultCollation = xctxt.getDefaultCollation();
	    XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
	    while (iter.hasNext()) {
	    	XObject key = iter.next();
	    	if (!XslTransformEvaluationHelper.contains(inpSeq1, key, xpathDefaultCollation, xpathCollationSupport)) {
	    	   XObject value = nativeMap.get(key);
	    	   nativeResultMap.put(key, value);
	    	}
	    }
	    
	    XPathMap resultMap = new XPathMap();
	    resultMap.setNativeMap(nativeResultMap);
	    
	    result = resultMap;
	    
	    return result;
	}

}
