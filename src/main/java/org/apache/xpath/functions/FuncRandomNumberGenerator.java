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
package org.apache.xpath.functions;

import java.util.Calendar;
import java.util.Random;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.context.FuncCurrentDateTime;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.operations.Variable;

import com.sun.org.apache.xml.internal.dtm.DTM;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * Implementation of an XPath 3.1 function fn:random-number-generator.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncRandomNumberGenerator extends FunctionMultiArgs {

	private static final long serialVersionUID = -7202234011428135803L;
	
	/**
	 * Class constructor.
	 */
	public FuncRandomNumberGenerator() {
		m_defined_arity = new Short[] { 0, 1 };
	}
	
	/**
	 * Implementation of the function.
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{          
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		XPathMap xpathMap = new XPathMap();
		
		double randomValue = 0.0;		
		if (m_arg1 != null) {
			throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function fn:random-number-generator can "
					                                                                                            + "have arity zero or one.", srcLocator); 
		}
		else if (m_arg0 != null) {
			XObject arg0Obj = null;
			if (m_arg0 instanceof Variable) {
			   arg0Obj = ((Variable)m_arg0).execute(xctxt);
			}
			else {
		       arg0Obj = m_arg0.execute(xctxt);
			}
		    
		    double rndNumberGeneratorSeed = 0.0;
		    
		    if (arg0Obj instanceof XNumber) {
		       rndNumberGeneratorSeed = ((XNumber)arg0Obj).num();
		    }
		    else if (arg0Obj instanceof XSNumericType) {
		       XSNumericType xsNumericType = (XSNumericType)arg0Obj;
		       String numericStrValue = xsNumericType.stringValue();
		       rndNumberGeneratorSeed = (Double.valueOf(numericStrValue)).doubleValue();
		    }
		    else if (m_arg0 instanceof FuncCurrentDateTime) {
		       XSDateTime xsDateTime = (XSDateTime)(((FuncCurrentDateTime)m_arg0).execute(xctxt));
		       Calendar calendar = xsDateTime.getCalendar();
		       rndNumberGeneratorSeed = (double)calendar.getTimeInMillis();
		    }
		    else if (arg0Obj instanceof XSDateTime) {
		       XSDateTime xsDateTime = (XSDateTime)arg0Obj;
		       Calendar calendar = xsDateTime.getCalendar();
		       rndNumberGeneratorSeed = (double)calendar.getTimeInMillis();
		    }
		    else if ((arg0Obj instanceof XSAnyAtomicType) || (arg0Obj instanceof XString) 
		    		                                         || (arg0Obj instanceof XBoolean) 
		    		                                            || (arg0Obj instanceof XBooleanStatic)) {
		    	String arg0Str = XslTransformEvaluationHelper.getStrVal(arg0Obj);
		    	int seedValue = 0;
		    	int strLength = arg0Str.length();
		    	for (int idx = 0; idx < strLength; idx++) {
		    		int chrIntValue = arg0Str.charAt(idx);
		    		seedValue += chrIntValue;
		    	}

		    	rndNumberGeneratorSeed = (double)seedValue;
		    }
		    else {
		    	throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function fn:random-number-generator's "
		    			                                                                       + "'seed' argument is not of type xs:anyAtomicType.", srcLocator);
		    }
		    
		    Random random = new Random();
		    random.setSeed((long)rndNumberGeneratorSeed);
		    
		    randomValue = random.nextDouble(); 
		}
		else {
			Random random = new Random();
			randomValue = random.nextDouble();
		}
		
		xpathMap.put(new XSString(Constants.ELEMNAME_NUMBER_STRING), new XSDouble(randomValue));
		
		String funcNextXPathStr = "function () { " + Keywords.FUNC_RANDOM_NUMBER_GENERATOR + "() }";
		
		XPath nextExprXPath = new XPath(funcNextXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		XObject xObj = nextExprXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		xpathMap.put(new XSString(Constants.FN_XALAN_RNG_NEXT), xObj);
		
        String funcPermuteXPathStr = "function ($seq) { " + Constants.FN_XALAN_RNG_PERMUTE + "}";
		
		XPath funcPermuteXPath = new XPath(funcPermuteXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		XObject xObj2 = funcPermuteXPath.execute(xctxt, DTM.NULL, xctxt.getNamespaceContext());
		
		xpathMap.put(new XSString(Constants.FN_XALAN_RNG_PERMUTE_STR), xObj2);
		
		result = xpathMap;
		
		return result;
	}

}
