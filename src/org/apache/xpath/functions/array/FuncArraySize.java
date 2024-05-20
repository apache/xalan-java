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

import javax.xml.transform.SourceLocator;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of the array:size() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArraySize extends FunctionOneArg {

	private static final long serialVersionUID = 5600653040311629451L;
	
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();
	    
	    XObject xObject = null;
	    
	    if (arg0 instanceof Variable) {
	       xObject = ((Variable)arg0).execute(xctxt);
	       if (xObject instanceof XNodeSet) {
	    	  result = new XSInteger(((XNodeSet)xObject).getLength() + "");  
	       }
	       else if (xObject instanceof ResultSequence) {
	    	  result = new XSInteger(((ResultSequence)xObject).size() + "");  
	       }
	       else {
	    	  result = new XSInteger("1");  
	       }
	    }
	    else {
	       xObject = arg0.execute(xctxt);
	       if (xObject instanceof XNodeSet) {
		   	  result = new XSInteger(((XNodeSet)xObject).getLength() + "");  
		   }
		   else if (xObject instanceof ResultSequence) {
		      result = new XSInteger(((ResultSequence)xObject).size() + "");  
		   }
		   else {
		      result = new XSInteger("1");  
		   }
	    }
	    
	    return result;
	}

}
