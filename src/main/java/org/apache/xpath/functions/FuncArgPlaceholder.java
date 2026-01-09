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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.Constants;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSString;

/**
 * A class definition, to help implement XPath partial function 
 * application using placeholder function argument expression ?,
 * and also XPath map and array unary lookup expressions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArgPlaceholder extends Expression {

	private static final long serialVersionUID = 684157463367484125L;
	
	/**
	 * This class field is used to, evaluate XPath map and array
	 * unary lookup expressions.
	 */
	private XPathDynamicFunctionCall m_xpathDynamicFunctionCall;
	
	/**
	 * Class constructor.
	 */
	public FuncArgPlaceholder() {
	   // no op	
	}

	@Override
	public XObject execute(XPathContext xctxt) throws TransformerException {
		
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		if (m_xpathDynamicFunctionCall != null) {
			String funcRefVarName = m_xpathDynamicFunctionCall.getFuncRefVarName(); 
			if ((Constants.UNARY_LOOKUP_MAP_ARRAY).equals(funcRefVarName)) {
				List<String> argList = m_xpathDynamicFunctionCall.getArgList(); 
				XObject xObj = xctxt.getXPath3ContextItem();    	  
				if (xObj instanceof XPathMap) {
					String mapKeyStr = argList.get(0);
					XPathMap xpathMap = (XPathMap)xObj;
					if (!"*".equals(mapKeyStr)) {
						result = xpathMap.get(new XSString(mapKeyStr));
						if (result == null) {
							try {
								// Validate the string value mapKeyStr, for it to be an integer
								int keyIntValue = Integer.valueOf(mapKeyStr);
								
								result = xpathMap.get(new XSDouble(mapKeyStr));
							}
							catch (NumberFormatException ex) {
								result = new ResultSequence();
							}
						}
					}
					else {
						// An xdm map lookup expression is ?*
						Map<XObject,XObject> nativeMap = xpathMap.getNativeMap();
						Set<XObject> keySet = nativeMap.keySet();
						ResultSequence rSeq = new ResultSequence();
						for (Iterator<XObject> iter = keySet.iterator(); iter.hasNext(); ) {
						   XObject key1 = iter.next();
						   rSeq.add(nativeMap.get(key1));
						}
						
						result = rSeq;
					}

					return result;
				}
				else if (xObj instanceof XPathArray) {
					String arrayKeyStr = argList.get(0);
					if (!"*".equals(arrayKeyStr)) {
						try {
							int keyIntValue = Integer.valueOf(arrayKeyStr);
							if (keyIntValue > 0) {
								XPathArray xpathArr = (XPathArray)xObj;
								result = xpathArr.get(keyIntValue - 1);
							}
							else {
								throw new TransformerException("XPTY0004 : For an XPath array information lookup, the key specifier "
                                                                                                        + "doesn't evaluate to an integer "
                                                                                                        + "value greater than zero.", srcLocator);
							}
						}
						catch (NumberFormatException ex) {
							throw new TransformerException("XPTY0004 : For an XPath array information lookup, the key specifier "
									                                                                    + "doesn't evaluate to an integer "
									                                                                    + "value.", srcLocator);
						}
					}
					else {
						// An xdm map lookup expression is ?*
						XPathArray xpathArr = (XPathArray)xObj;
						int arraySize = xpathArr.size();
						ResultSequence rSeq = new ResultSequence();
						for (int idx = 0; idx < arraySize; idx++) {
						   rSeq.add(xpathArr.get(idx));
						}
						
						result = rSeq;
					}
					
					return result;
				}
				else {
					throw new TransformerException("XPTY0004 : For an XPath map or array lookup expression ?KeySpecifier's "
																										       + "evaluation, the context item "
																										       + "is not an XPath map or array.", srcLocator);
				}
			}			
		}
		
		return result;
	}
	
	@Override
	public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
		// no op
	}

	@Override
	public void fixupVariables(Vector vars, int globalsSize) {
	   // no op
	}

	@Override
	public boolean deepEquals(Expression expr) {
		// no op
		return false;		
	}

	public XPathDynamicFunctionCall getXPathDynamicFunctionCall() {
		return m_xpathDynamicFunctionCall;
	}

	public void setXPathDynamicFunctionCall(XPathDynamicFunctionCall xpathDynamicFunctionCall) {
		this.m_xpathDynamicFunctionCall = xpathDynamicFunctionCall;
	}

}
