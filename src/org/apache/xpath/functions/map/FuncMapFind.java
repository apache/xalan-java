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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyAtomicType;

/**
 * Implementation of an XPath 3.1 function, map:find.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapFind extends Function2Args {

	private static final long serialVersionUID = -7140205339584548625L;

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XPathArray result = new XPathArray();
	       
	    Expression arg0 = getArg0();
	    XObject arg0Obj = arg0.execute(xctxt);
	    
	    Expression arg1 = getArg1();
	    XObject arg1Obj = arg1.execute(xctxt);
	    if (!((arg1Obj instanceof XSAnyAtomicType) || (arg1Obj instanceof XString) || 
	    		                            (arg1Obj instanceof XNumber) || (arg1Obj instanceof XBoolean))) {
	        throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument of map:find function call is not of "
	        		+                                                                                "type xs:anyAtomicType or it's subtype.");
	    }
	    
	    mapFind(arg0Obj, arg1Obj, result);	    
	    
	    return result;
	}
	
	/**
	 * This method searches an input sequence recursively for map entries having 
	 * specified key, and accumulates in an array the values of such map entries 
	 * that're found.
	 * 
	 * @param list         an input sequence to be searched
	 * @param key          map's key, for which search is been done within 
	 *                     an input sequence.                    
	 * @param result       result of this function call is accumulated into
	 *                     this array object.
	 */
	private void mapFind(XObject list, XObject key, XPathArray result) {
       if (list instanceof ResultSequence) {
    	  ResultSequence rSeq = (ResultSequence)list;
    	  for (int idx = 0; idx < rSeq.size(); idx++) {
    		 XObject seqItem = rSeq.item(idx);
    		 if (seqItem instanceof XPathMap) {
    			XObject mapEntryValue = ((XPathMap)seqItem).get(key);
    			if (mapEntryValue != null) {
    			   result.add(mapEntryValue);
    			   mapFind(mapEntryValue, key, result);
    			}
    		 }
    		 else if ((seqItem instanceof ResultSequence) || (seqItem instanceof XPathArray)) {
    			mapFind(seqItem, key, result); 
    		 }
    	  }
       }
       else if (list instanceof XPathArray) {
    	  XPathArray xpathArr = (XPathArray)list;
    	  for (int idx = 0; idx < xpathArr.size(); idx++) {
    		 XObject arrItem = xpathArr.get(idx);
             if (arrItem instanceof XPathMap) {
            	XObject mapEntryValue = ((XPathMap)arrItem).get(key);
     			if (mapEntryValue != null) {
     			   result.add(mapEntryValue);
     			   mapFind(mapEntryValue, key, result);
     			} 
    		 }
             else if ((arrItem instanceof ResultSequence) || (arrItem instanceof XPathArray)) {
            	mapFind(arrItem, key, result); 
    		 }
    	  }
       }
       else if (list instanceof XPathMap) {
    	  XPathMap xpathMap = (XPathMap)list;
    	  XObject val = xpathMap.get(key);
    	  if (val != null) {
    		 result.add(val);  
    	  }
    	  Map<XObject,XObject> nativeMap = xpathMap.getNativeMap();
    	  Collection<XObject> mapEntryValues = nativeMap.values();
    	  Iterator<XObject> iter = mapEntryValues.iterator();
    	  while (iter.hasNext()) {
    		 XObject value = iter.next();
    		 mapFind(value, key, result);
    	  }
       }
	}

}
