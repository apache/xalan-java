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
package org.apache.xpath.functions.array;

import java.util.List;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the array:flatten function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayFlatten extends FunctionOneArg {

	private static final long serialVersionUID = -2684722783678792071L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayFlatten() {
		m_defined_arity = new Short[] { 1 };
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
	    ResultSequence result = null;	    
	       
	    Expression arg0 = getArg0();
	    
	    XObject xObject = null;
	    
	    if (arg0 instanceof Variable) {
	       xObject = ((Variable)arg0).execute(xctxt);
	    }
	    else {
	       xObject = arg0.execute(xctxt);
	    }	    
	    
	    if (xObject instanceof ResultSequence) {
	    	result = flatten((ResultSequence)xObject);
	    }
	    else if (xObject instanceof XPathArray) {
	    	result = flatten((XPathArray)xObject);
	    }
	    else {
	        return xObject; 	
	    }
	    
	    return result;
	}

	/**
     * Flatten the contents of an input sequence recursively, 
     * and return the result.
	 */
	private ResultSequence flatten(ResultSequence rSeq) {
		ResultSequence result = new ResultSequence();
		
		for (int idx = 0; idx < rSeq.size(); idx++) {
		   XObject item = rSeq.item(idx);
		   if (item instanceof XPathArray) {
			  ResultSequence seq = flatten((XPathArray)item);
			  for (int idx1 = 0; idx1 < seq.size(); idx1++) {
			     result.add(seq.item(idx1)); 
			  }  
		   }
		   else {
			  result.add(item); 
		   }
		}
		
		return result;
	}

	/**
     * Flatten the contents of an input array recursively, 
     * and return the result.
	 */
	private ResultSequence flatten(XPathArray arr) {
		ResultSequence result = new ResultSequence();
		
		List<XObject> nativeArr = arr.getNativeArray();
		
		for (int idx = 0; idx < nativeArr.size(); idx++) {
		   XObject arrItem = nativeArr.get(idx);
		   if (arrItem instanceof XPathArray) {
			  ResultSequence rSeq = flatten((XPathArray)arrItem);
			  for (int idx1 = 0; idx1 < rSeq.size(); idx1++) {
				 result.add(rSeq.item(idx1)); 
			  }
		   }
		   else {
			  result.add(arrItem);  
		   }
		}
		
		return result;
	}

}
