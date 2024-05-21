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

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the array:put() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayPut extends Function3Args {

	private static final long serialVersionUID = -3699572884716214890L;

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();	    
	    Expression arg1 = getArg1();	    
	    Expression arg2 = getArg2();
	    
	    ResultSequence arg0Seq = null;	    
	    XObject arg1XObj = null;	    
	    XObject arg2XObj = null;
	    
	    if (arg0 instanceof Variable) {
	       XObject xObject = ((Variable)arg0).execute(xctxt);
	       if (xObject instanceof XNodeSet) {
	    	  // TO DO 
	       }
	       else if (xObject instanceof ResultSequence) {
	    	  arg0Seq = (ResultSequence)xObject; 
	       }
	       else if (xObject instanceof XPathArray) {
		      XPathArray xpathArr = (XPathArray)xObject;
		      List<XObject> nativeArr = xpathArr.getNativeArray();
		      arg0Seq = ElemCopyOf.getResultSequenceFromXPathArray(nativeArr);
		   }
	       else {
	    	  // TO DO  
	       }
	    }
	    else {
	       // TO DO
	    }
	    
	    if (arg1 instanceof Variable) {
	       arg1XObj = ((Variable)arg1).execute(xctxt);
	    }
	    else {
	       arg1XObj = arg1.execute(xctxt);
	    }
	    
	    if (arg2 instanceof Variable) {
		   arg2XObj = ((Variable)arg2).execute(xctxt);
		}
		else {
		   arg2XObj = arg2.execute(xctxt);
		}
	    
	    arg0Seq.set(Integer.valueOf(XslTransformEvaluationHelper.getStrVal(arg1XObj)) - 1, arg2XObj);
	    
	    result = arg0Seq;
	    
	    return result;
	}

}
