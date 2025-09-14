/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.util;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSString;

/**
 * A class definition, specifying few Xalan-J XSL 3 Java extension 
 * functions for Xalan-J test cases. The user shall typically write 
 * their own logic for Xalan-J Java extension functions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 */
public class XalanJavaExtension1 {
	
	/**
	 * Method definition to, filter information from the supplied 
	 * Document object, that meet a specific criteria.
	 * 
	 * @param xObjArr			    The supplied Document object instance
	 * @param srch1                 The filtering criteria string value 
	 * @return                      The filtered result as ResultSequence object 
	 */
	public static ResultSequence filterNodes(Document document, String srch1) {
		
		ResultSequence result = new ResultSequence();
		
		NodeList nodeList = document.getElementsByTagName("name");
		int nodeListSize = nodeList.getLength();
		for (int idx = 0; idx < nodeListSize; idx++) {
		   Element elemNode = (Element)(nodeList.item(idx));
		   String str1 = elemNode.getTextContent();
		   if (str1.startsWith(srch1)) {
			  result.add(new XSString(str1)); 
		   }
		}
		
		return result;
    }
	
	/**
	 * Method definition to, process the supplied array of XObject 
	 * values, by transforming to a sequence of square of each array 
	 * item. 
	 * 
	 * @param xObjArr                    The supplied array of XObject instance values
	 * @return                           The result value as ResultSequence object 
	 * @throws TransformerException
	 */
	public static ResultSequence squareSeqItems(XObject[] xObjArr) throws TransformerException {		
        
		ResultSequence result = squareXObjectArr(xObjArr);			
		
		return result;
    }
	
	/**
	 * Method definition to, process the supplied array of XObject 
	 * values, by transforming to a sequence of square of each array 
	 * item. 
	 * 
	 * @param xObjArr                    The supplied array of XObject instance values
	 * @return                           The result value as ResultSequence object 
	 * @throws TransformerException
	 */
    public static ResultSequence squareArrayItems(XObject[] xObjArr) throws TransformerException {		
		
    	ResultSequence result = squareXObjectArr(xObjArr);			
		
		return result;
    }
    
    /**
	 * Method definition to, process the supplied java.util.Map object, 
	 * by transforming to an xdm map whose entry values are square of 
	 * that of the supplied java.util.Map object.
	 * 
	 * @param map1                       The supplied java.util.Map object
	 * @return                           The computed xdm map value 
	 * @throws TransformerException
	 */
    public static XPathMap processMap1(Map<XObject, XObject> map1) throws TransformerException {
		
    	XPathMap result = new XPathMap();
    	
    	Set<XObject> keySet = map1.keySet();
    	Iterator<XObject> iter = keySet.iterator();
    	while (iter.hasNext()) {
    	   XObject key1 = iter.next();
    	   XObject value1 = map1.get(key1);
    	   Double dbl1 = new Double(XslTransformEvaluationHelper.getStrVal(value1));
    	   result.put(key1, new XSDouble(dbl1 * dbl1));
    	}
		
		return result;
    }
    
    /**
     * Method definition, to get numeric average value from JSON 
     * map entries.
     * 
     * @param jsonStr1					  The supplied JSON string value
     * @param round                       Result double value's round size
     * @return                            The computed result
     */
    public static XSDouble getAverage(String jsonStr1, int round) {
    	XSDouble result = null;
    	
    	JSONObject jsonObj = new JSONObject(jsonStr1);
    	Iterator<String> keys = jsonObj.keys();
    	int count = 0;
    	double sum = 0.0;
    	while (keys.hasNext()) {
    	   count++;
    	   String key1 = keys.next();
    	   double value = Double.valueOf((jsonObj.get(key1)).toString());
    	   sum += value;
    	}
    	
    	DecimalFormat decimalFormat = new DecimalFormat("#." + XslTransformEvaluationHelper.getStrWithZeros(round));
    	double valAfterRounding = Double.valueOf(decimalFormat.format(Double.valueOf(sum / count)));
    	
    	result = new XSDouble(valAfterRounding); 
    	
    	return result;
    }
    
    /**
	 * Method definition to, process the supplied array of XObject 
	 * values, by transforming to a sequence of square of each array 
	 * item. 
	 * 
	 * @param xObjArr                    The supplied array of XObject instance values
	 * @return                           The result value as ResultSequence object 
	 * @throws TransformerException
	 */
    private static ResultSequence squareXObjectArr(XObject[] xObjArr) {
        
    	ResultSequence result = new ResultSequence();
		
		for (int idx = 0; idx < xObjArr.length; idx++) {
			XObject xObj = xObjArr[idx];
			String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
			Double dbl1 = Double.valueOf(str1);
			result.add(new XSDouble(dbl1 * dbl1));
		}				
		
		return result;
    }

}
