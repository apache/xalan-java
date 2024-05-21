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
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the array:get() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayGet extends Function2Args {
	
	private static final long serialVersionUID = 9085303963460501516L;

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
	    XObject result = null;
	       
	    SourceLocator srcLocator = xctxt.getSAXLocator();
	       
	    Expression arg0 = getArg0();
	    
	    Expression arg1 = getArg1();
	    
	    ResultSequence arg0Seq = null;
	    
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
	       XObject arg1XObj = ((Variable)arg1).execute(xctxt);
	       String arg1Str = XslTransformEvaluationHelper.getStrVal(arg1XObj);
	       result = arg0Seq.item((Integer.valueOf(arg1Str)).intValue() - 1);
	    }
	    else {
	       XObject arg1XObj = arg1.execute(xctxt);
		   String arg1Str = XslTransformEvaluationHelper.getStrVal(arg1XObj);
		   result = arg0Seq.item((Integer.valueOf(arg1Str)).intValue() - 1);
	    }
	    
	    return result;
	}

}
