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

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the array:join function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayJoin extends FunctionOneArg {

	private static final long serialVersionUID = -6855946670900692995L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayJoin() {
		m_defined_arity = new Short[] { 1 };
	}

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();
	    
	    XObject arg0Obj = null;
	    
	    if (arg0 instanceof Variable) {
	       arg0Obj = ((Variable)arg0).execute(xctxt);
	    }
	    else {
	       arg0Obj = arg0.execute(xctxt);
	    }	    
	    	    
	    if (arg0Obj instanceof ResultSequence) {
	       ResultSequence rSeq = (ResultSequence)arg0Obj;	       	       
	       if (rSeq.size() == 0) {
	          result = new XPathArray();
	       }
	       else {
	    	  List<XObject> resultNativeArr = new ArrayList<XObject>();
	    	  for (int idx = 0; idx < rSeq.size(); idx++) {
	 	    	XObject item = rSeq.item(idx);
	 	    	if (item instanceof XPathArray) {
	 	    	   List<XObject> nativeArr = ((XPathArray)item).getNativeArray();
	 	    	   resultNativeArr.addAll(nativeArr);
	 	    	}
	 	    	else {
	 	    	   throw new javax.xml.transform.TransformerException("FOAY0001 : array:join function call's sequence argument has "
	 	    		 		                                                                   + "an item which is not an array.", srcLocator);  
	 	    	}
	 	      }
	    	  
	    	  XPathArray resultArr = new XPathArray();
		      resultArr.setNativeArray(resultNativeArr);
		      result = resultArr;
	       }
	    }
	    else if (arg0Obj instanceof XPathArray) {
		   result = arg0Obj; 
		}
	    else {
	       throw new javax.xml.transform.TransformerException("FOAY0001 : The 1st argument of function array:join, can either be a "
	       		                                                     + "sequence (containing arrays to be joined/concatinated) or an array.", srcLocator); 
	    }
	    
	    return result;
	}

}
