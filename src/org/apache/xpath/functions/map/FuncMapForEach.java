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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of an XPath 3.1 function, map:for-each.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapForEach extends Function2Args {

	private static final long serialVersionUID = 4474670594915534994L;
	
	// The following two fields of this class, are used during 
	// XPath.fixupVariables(..) action as performed within object of 
	// this class.    
	private Vector fVars;    
	private int fGlobalsSize;

	/**
	 * Implementation of the function. The function must return a valid object.
	 * 
	 * @param xctxt The current execution context.
	 * 
	 * @return A valid XObject.
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();

		Expression arg0 = getArg0();
		Expression arg1 = getArg1();

		XObject arg0XsObject = null;

		if (fVars != null) {
		   arg0.fixupVariables(fVars, fGlobalsSize);
		}

		if (arg0 instanceof XPathMap) {
		   arg0XsObject = (XPathMap)arg0;              
		}
		else if (arg0 instanceof Variable) {
		   arg0XsObject = ((Variable)arg0).execute(xctxt);
		   if (!(arg0XsObject instanceof XPathMap)) {
			   throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument to function call map:for-each, "
                                                                                            + "is not a map.", xctxt.getSAXLocator()); 
		   }
		}
		else {
		   arg0XsObject = arg0.execute(xctxt);
		   if (!(arg0XsObject instanceof XPathMap)) {
			   throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument to function call map:for-each, "
                                                                                            + "is not a map.", xctxt.getSAXLocator()); 
		   }
		}

		if (arg1 instanceof XPathInlineFunction) {
			XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1;
			verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
			result = evaluateFnMapforEach(xctxt, (XPathMap)arg0XsObject, inlineFuncArg); 
		}
		else if (arg1 instanceof Variable) {
			if (fVars != null) {
				arg1.fixupVariables(fVars, fGlobalsSize);
			}

			XObject arg1VarValue = arg1.execute(xctxt);
			if (arg1VarValue instanceof XPathInlineFunction) {
				XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1VarValue;
				verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
				result = evaluateFnMapforEach(xctxt, (XPathMap)arg0XsObject, inlineFuncArg);   
			}
			else {
				throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument to function call map:for-each, "
						                                                               + "is not a function item.", xctxt.getSAXLocator());    
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument to function call map:for-each, "
					                                                                   + "is not a function item.", xctxt.getSAXLocator());               
		}

		return result;
	}

	/**
	 * Check that the number of arguments passed to this function is correct.
	 *
	 * @param argNum The number of arguments that is being passed to the function.
	 *
	 * @throws WrongNumberArgsException
	 */
	public void checkNumberArgs(int argNum) throws WrongNumberArgsException
	{
		if (argNum != 2) {
		   reportWrongNumberArgs();
		}
	}

	/**
	 * Constructs and throws a WrongNumberArgException with the appropriate
	 * message for this function object.
	 *
	 * @throws WrongNumberArgsException
	 */
	protected void reportWrongNumberArgs() throws WrongNumberArgsException {
		throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
																	XPATHErrorResources.ER_TWO, null)); //"2"
	}

	public void fixupVariables(java.util.Vector vars, int globalsSize)
	{
		fVars = (Vector)(vars.clone());
		fGlobalsSize = globalsSize; 
	}

	/*
	 * Verify the number of function parameters, that the inline function is allowed to 
	 * have for map:for-each function call.
	 */
	private void verifyInlineFunctionParamCardinality(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
																								javax.xml.transform.TransformerException {
		List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
		if (funcParamList.size() != 2) {
			throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied function map:for-each's function item has " + 
																			 funcParamList.size() + " parameters. Expected 2.", srcLocator);   
		}
	}

	/*
	 * Construct result of map:for-each function call.
	 */
	private ResultSequence evaluateFnMapforEach(XPathContext xctxt, XPathMap xpathMap, XPathInlineFunction funcItem) 
					                                                                                     throws TransformerException {
		ResultSequence resultSeq = new ResultSequence();
		
		List<InlineFunctionParameter> funcParamList = funcItem.getFuncParamList();
        QName fiParam0 = new QName((funcParamList.get(0)).getParamName());
        QName fiParam1 = new QName((funcParamList.get(1)).getParamName());				
        
        String funcBodyXPathExprStr = funcItem.getFuncBodyXPathExprStr();
        
        if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
           return resultSeq;
        }
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        XPath inlineFnXpath = new XPath(funcBodyXPathExprStr, srcLocator, null, XPath.SELECT, null);
		
		Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
		Set<Entry<XObject, XObject>> mapEntrySet = nativeMap.entrySet();
		
		Iterator<Entry<XObject, XObject>> iter = mapEntrySet.iterator();
		
		XPathContext xpathContextNew = new XPathContext(false);
        Map<QName, XObject> inlineFunctionVarMap = xpathContextNew.getXPathVarMap();
        
		while (iter.hasNext()) {
		   Entry<XObject, XObject> mapEntry = iter.next();
		   XObject key = mapEntry.getKey();
		   XObject value = mapEntry.getValue();

           if (fiParam0 != null) {
              inlineFunctionVarMap.put(fiParam0, key);
           }
           if (fiParam1 != null) {
              inlineFunctionVarMap.put(fiParam1, value);
           }
           
           XObject resultObj = inlineFnXpath.execute(xpathContextNew, DTM.NULL, null);
           resultSeq.add(resultObj);
		}
		
		inlineFunctionVarMap.clear();

		return resultSeq;
	}
}
