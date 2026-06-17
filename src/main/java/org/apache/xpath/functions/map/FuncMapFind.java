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
	
	/**
	 * Class constructor.
	 */
	public FuncMapFind() {
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
		
		XPathArray result = new XPathArray();
	       
	    XObject arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    
	    XObject arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
	    
	    if (!((arg1Obj instanceof XSAnyAtomicType) || (arg1Obj instanceof XString) || 
	    		                            (arg1Obj instanceof XNumber) || (arg1Obj instanceof XBoolean))) {
	        throw new javax.xml.transform.TransformerException("FORG0006 : The second argument of map:find function call is not of "
	        		+                                                                                              "type xs:anyAtomicType or it's subtype.");
	    }
	    
	    searchXdmSequence(arg0Obj, arg1Obj, result);	    
	    
	    return result;
	}
	
	/**
	 * Method definition, to search within an input sequence recursively for map 
	 * entries having specified key, and accumulates within result array the values of 
	 * the map entries that're found.
	 * 
	 * @param seq1         An input sequence to be searched
	 * @param key          An xdm map's key, for which search is been done within 
	 *                     an input sequence.                    
	 * @param result       Result of this function call is accumulated into
	 *                     this array object.
	 */
	private void searchXdmSequence(XObject seq1, XObject key, XPathArray result) {
       
		if (seq1 instanceof ResultSequence) {
    	  ResultSequence rSeq = (ResultSequence)seq1;
    	  int size1 = rSeq.size();
    	  for (int idx = 0; idx < size1; idx++) {
    		 XObject seqItem = rSeq.item(idx);
    		 
    		 if (seqItem instanceof XPathMap) {
    			XObject mapEntryValue = ((XPathMap)seqItem).get(key);
    			
    			if (mapEntryValue != null) {
    			   result.add(mapEntryValue);
    			   
    			   searchXdmSequence(mapEntryValue, key, result);
    			}
    		 }
    		 else if ((seqItem instanceof ResultSequence) || (seqItem instanceof XPathArray)) {
    			searchXdmSequence(seqItem, key, result); 
    		 }
    	  }
       }
       else if (seq1 instanceof XPathArray) {
    	  XPathArray xpathArr = (XPathArray)seq1;
    	  int size1 = xpathArr.size();
    	  for (int idx = 0; idx < size1; idx++) {
    		 XObject arrItem = xpathArr.get(idx);
             
    		 if (arrItem instanceof XPathMap) {
            	XObject mapEntryValue = ((XPathMap)arrItem).get(key);
     			
            	if (mapEntryValue != null) {
     			   result.add(mapEntryValue);
     			   
     			   searchXdmSequence(mapEntryValue, key, result);
     			} 
    		 }
             else if ((arrItem instanceof ResultSequence) || (arrItem instanceof XPathArray)) {
            	searchXdmSequence(arrItem, key, result); 
    		 }
    	  }
       }
       else if (seq1 instanceof XPathMap) {
    	  XPathMap xpathMap = (XPathMap)seq1;
    	  XObject val = xpathMap.get(key);
    	  
    	  if (val != null) {
    		 result.add(val);  
    	  }
    	  
    	  Map<XObject,XObject> nativeMap = xpathMap.getNativeMap();
    	  Collection<XObject> mapEntryValues = nativeMap.values();
    	  Iterator<XObject> iter = mapEntryValues.iterator();
    	  
    	  while (iter.hasNext()) {
    		 XObject value = iter.next();
    		 
    		 searchXdmSequence(value, key, result);
    	  }
       }
	}

}
