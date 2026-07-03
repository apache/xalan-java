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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionMultiArgs;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function, map:merge.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapMerge extends FunctionMultiArgs {

	private static final long serialVersionUID = -3510616878635425133L;
	
	private static final String DUPLICATES_KEY_NAME = "duplicates";
	
	private static final String OPTION_REJECT ="reject";
	
	private static final String OPTION_USE_FIRST ="use-first";
	
	private static final String OPTION_USE_LAST ="use-last";
	
	private static final String OPTION_USE_ANY ="use-any";
	
	private static final String OPTION_COMBINE ="combine";
	
	private static final String[] OPTIONS_ARR = new String[] { OPTION_REJECT, OPTION_USE_FIRST, OPTION_USE_LAST, 
			                                                   OPTION_USE_ANY, OPTION_COMBINE };
	
	/**
	 * Class constructor.
	 */
	public FuncMapMerge() {
		m_arity = new Short[] { 1, 2 };	
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
	    
	    if ((m_arg0 == null) && (m_arg1 == null)) {
	       throw new javax.xml.transform.TransformerException("FOAP0001 : The function call, map:merge requires either "
	       		                                                             + "one argument (specifying maps to be merged) or two arguments "
	       		                                                             + "(the 2nd argument is an options map).", srcLocator);
	    }
	    
	    validateFnArg0Type(m_arg0, xctxt);
	    
	    if (m_arg1 != null) {
	       validateFnArg1Type(m_arg1, xctxt);
	    }
	    
	    if ((m_arg1 != null) && OPTION_REJECT.equals(getOptionsStrVal(m_arg1, xctxt)) && isMapMergeToBeRejected(m_arg0, xctxt)) {
	        throw new javax.xml.transform.TransformerException("FOJS0003 : Maps could not be merged, because one or more duplicate "
	        		                                                               + "keys were found within maps to be merged, and an map merge "
	        		                                                               + "option 'reject' was used.", srcLocator);
	    }
	    else {
	    	// map:merge, function call was invoked with an option "reject", but the maps to be 
	    	// merged didn't had duplicate keys (i.e, maps merge is possible).
	    	
	    	// For this case, we merge the maps with default value of options, i.e "use-first"
	    	ResultSequence rSeq = null;
	    	Map<XObject, XObject> nativeResultMap = new HashMap<XObject, XObject>();
	    	if (m_arg0 instanceof Variable) {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = (size1 - 1); idx >= 0; idx--) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			nativeResultMap.putAll(nativeMap);	        	 
	    		}
	    		XPathMap resultMap = new XPathMap();
	    		resultMap.setNativeMap(nativeResultMap);
	    		result = resultMap;
	    	}
	    	else {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = (size1 - 1); idx >= 0; idx--) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			nativeResultMap.putAll(nativeMap);	        	 
	    		}
	    		XPathMap resultMap = new XPathMap();
	    		resultMap.setNativeMap(nativeResultMap);
	    		result = resultMap;
	    	}
	    }
	    
	    if (m_arg1 == null || OPTION_USE_FIRST.equals(getOptionsStrVal(m_arg1, xctxt)) || 
	    		            OPTION_USE_ANY.equals(getOptionsStrVal(m_arg1, xctxt))) {
	    	ResultSequence rSeq = null;	    	
	    	Map<XObject, XObject> nativeResultMap = new HashMap<XObject, XObject>();
	    	if (m_arg0 instanceof Variable) {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = (size1 - 1); idx >= 0; idx--) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			nativeResultMap.putAll(nativeMap);	        	 
	    		}	    		
	    		XPathMap resultMap = new XPathMap();
	    		resultMap.setNativeMap(nativeResultMap);
	    		result = resultMap;
	    	}
	    	else {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = (size1 - 1); idx >= 0; idx--) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			nativeResultMap.putAll(nativeMap);	        	 
	    		}	    		
	    		XPathMap resultMap = new XPathMap();
	    		resultMap.setNativeMap(nativeResultMap);
	    		result = resultMap;
	    	}
	    }
	    else if (OPTION_USE_LAST.equals(getOptionsStrVal(m_arg1, xctxt))) {
	    	ResultSequence rSeq = null;	    	
	    	Map<XObject, XObject> nativeResultMap = new HashMap<XObject, XObject>();	    	
	    	if (m_arg0 instanceof Variable) {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = 0; idx < size1; idx++) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			nativeResultMap.putAll(nativeMap);	        	 
	    		}	    		
	    		XPathMap resultMap = new XPathMap();
	    		resultMap.setNativeMap(nativeResultMap);
	    		result = resultMap;
	    	}
	    	else {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = 0; idx < size1; idx++) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			nativeResultMap.putAll(nativeMap);	        	 
	    		}	    		
	    		XPathMap resultMap = new XPathMap();
	    		resultMap.setNativeMap(nativeResultMap);
	    		result = resultMap;
	    	}
	    }	    
	    else if (OPTION_COMBINE.equals(getOptionsStrVal(m_arg1, xctxt))) {
	    	ResultSequence rSeq = null;	    	
	    	
	    	// This variable shall contain union of keys of all the maps, in 
	    	// an input sequence (i.e, map:merge function call's 1st argument).
	    	Set<XObject> distinctMapKeys = new HashSet<XObject>();
	    	
	    	if (m_arg0 instanceof Variable) {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = 0; idx < size1; idx++) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			distinctMapKeys.addAll(nativeMap.keySet());    	 
	    		}	    		
	    	}
	    	else {
	    		XObject xObj = getFunctionArgEffectiveValue(m_arg0, xctxt);
	    		rSeq = (ResultSequence)xObj;
	    		
	    		int size1 = rSeq.size();
	    		for (int idx = 0; idx < size1; idx++) {
	    			XPathMap map = (XPathMap)(rSeq.item(idx));
	    			Map<XObject, XObject> nativeMap = map.getNativeMap();
	    			distinctMapKeys.addAll(nativeMap.keySet());	        	 
	    		}	    			    		
	    	}
	    	
	    	Iterator<XObject> iter = distinctMapKeys.iterator();	    	
	    	Map<XObject, XObject> nativeResultMap = new HashMap<XObject, XObject>();	    		    
	    	while (iter.hasNext()) {
	    	   XObject key = iter.next();
	    	   ResultSequence concatinatedValues = new ResultSequence();
	    	   int size1 = rSeq.size();
	    	   for (int idx = 0; idx < size1; idx++) {
	    		  XPathMap map = (XPathMap)(rSeq.item(idx));
	    		  XObject mapEntryValue = map.get(key);
	    		  if (mapEntryValue != null) {
	    		     concatinatedValues.add(mapEntryValue);
	    		  }
	    	   }
	    	   
	    	   nativeResultMap.put(key, concatinatedValues);	    	   
	    	}
	    	XPathMap resultMap = new XPathMap();
	    	resultMap.setNativeMap(nativeResultMap);
	    	result = resultMap;
	    }
	    
	    return result;
	}
	
	/**
	 * Determine, whether the merge of maps is to be rejected, due to any duplicate 
	 * keys present within all input maps to be merged.
	 */
	private boolean isMapMergeToBeRejected(Expression inputMapsExpr, XPathContext xctxt) throws TransformerException {
		
		boolean result = true;
		
		XObject xObj = getFunctionArgEffectiveValue(inputMapsExpr, xctxt);
		ResultSequence rSeq = (ResultSequence)xObj;
		
		int totalMapKeys = 0;
		
		// This variable shall contain, distinct key values across 
		// all input maps to be merged.
		Set<XObject> mergeOfKeysSet = new HashSet<XObject>();
		
		int size1 = rSeq.size();
		
		for (int idx = 0; idx < size1; idx++) {
		   XPathMap map = (XPathMap)(rSeq.item(idx));
		   Map<XObject, XObject> nativeMap = map.getNativeMap();
		   Set<XObject> keysSet = nativeMap.keySet();
		   totalMapKeys += keysSet.size();
		   mergeOfKeysSet.addAll(keysSet);
		}
		
		if (totalMapKeys == mergeOfKeysSet.size()) {
			result = false;
		}
		
		return result;
	}

	/**
	 * Get the string value, of map entry's value for entry key name "duplicates".
	 */
	private String getOptionsStrVal(Expression optionsMapExpr, XPathContext xctxt) throws TransformerException {
		
		String result = null;

		XObject xObj = getFunctionArgEffectiveValue(optionsMapExpr, xctxt);

		XPathMap xpathMap = (XPathMap)xObj;
		xObj = xpathMap.get(new XSString(DUPLICATES_KEY_NAME));
		
		result = XslTransformEvaluationHelper.getStrVal(xObj);

		return result; 
	}

	/**
     * Validate the expected type for map:merge function's, first argument.
	 */
	private void validateFnArg0Type(Expression xpathExpr1, XPathContext xctxt) throws TransformerException {
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XObject xObj = getFunctionArgEffectiveValue(xpathExpr1, xctxt);

		if (xObj instanceof ResultSequence) {
			ResultSequence rSeq = (ResultSequence)xObj;
			int size1 = rSeq.size();
			for (int idx = 0; idx < size1; idx++) {
				XObject obj1 = rSeq.item(idx);
				if (!(obj1 instanceof XPathMap)) {
					throw new TransformerException("FOAP0001 : Within map:merge function's first argument sequence, "
																									+ "an xdm item at position " + (idx + 1) + " is "
																									+ "not a map.", srcLocator); 
				}
			}
		}
		else if (!(xObj instanceof XPathMap)) {
			throw new TransformerException("FOAP0001 : The map:merge function's first argument should be a "
																									 + "sequence of one or more maps.", srcLocator);   
		}
	}
	
	/**
     * Validate the expected type of map:merge function's, second argument.
	 */
	private void validateFnArg1Type(Expression xpathExpr1, XPathContext xctxt) throws TransformerException {        
        
		SourceLocator srcLocator = xctxt.getSAXLocator();

		XObject xObj1 = getFunctionArgEffectiveValue(xpathExpr1, xctxt);

		if (xObj1 instanceof XPathMap) {
			validateOptionsMap(xObj1, srcLocator);
		}		
	}

	/**
	 * This method does, validation of map:merge function's options map, and is 
	 * used by the method 'validateTypeOfSecondArg'. 
	 */
	private void validateOptionsMap(XObject obj, SourceLocator srcLocator) throws TransformerException {
		
		XPathMap optionsMap = (XPathMap)obj;
		
		if (optionsMap.size() != 1) {
			throw new TransformerException("FOAP0001 : The map:merge function's 2nd argument if present, should be "
					                                     + "a map having only 1 entry with key named '" + DUPLICATES_KEY_NAME + 
					                                       "'.", srcLocator);  
		}
		else {
			XObject mapEntryValue = optionsMap.get(new XSString(DUPLICATES_KEY_NAME));
			if (mapEntryValue == null) {
				throw new TransformerException("FOAP0001 : The map:merge function's 2nd argument if present, should be "
						                                     + "a map having only 1 entry with key named '" + DUPLICATES_KEY_NAME + 
						                                       "'.", srcLocator); 
			}
			else {
				boolean isOptionsValueOk = false;
				String mapEntryStrVal = XslTransformEvaluationHelper.getStrVal(mapEntryValue);
				for (int idx = 0; idx < OPTIONS_ARR.length; idx++) {
					String allowedVal = OPTIONS_ARR[idx];
					if (allowedVal.equals(mapEntryStrVal)) {
						isOptionsValueOk = true;
						break;
					}
				}

				if (!isOptionsValueOk) {
					throw new TransformerException("FOJS0005 : The allowed values of the map:merge duplicate resolution "
							                                     + "options are : ['reject', 'use-first', 'use-last', 'use-any', "
							                                     + "'combine']. An invalid options value '" + mapEntryStrVal + 
							                                     "' was provided.", srcLocator);
				}
			}
		}
	}

}
