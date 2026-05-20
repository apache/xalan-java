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
package org.apache.xpath.functions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeData;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;

/**
 * Implementation of an XPath 3.1 function call .(arg),
 * where the function item is available as an XPath 3.1
 * context item.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncPeriod extends FunctionMultiArgs {
	
	private static final long serialVersionUID = -638591636434982044L;
	
	private int m_min_arity = 1;

	private int m_max_arity = Integer.MAX_VALUE - 1;
	
	private int m_defined_arity = 0;

	/**
	 * Class constructor.
	 */
	public FuncPeriod() {
		// no op
	}
	
	/**
	 * Evaluate the function. The function must return
	 * a valid object.
	 * 
	 * @param xctxt The current execution context
	 * @return A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		
		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		final int contextNode = xctxt.getCurrentNode(); 
		
		XObject xpath3CtxtItem = xctxt.getXPath3ContextItem();
		if (xpath3CtxtItem != null) {
			if (xpath3CtxtItem instanceof XPathMap) {
			   if ((m_arg0 != null) && (m_arg1 == null)) {
				  XObject xObj0 = m_arg0.execute(xctxt);
				  
			      result = ((XPathMap)xpath3CtxtItem).get(xObj0);
			   }
			   else {
				  throw new javax.xml.transform.TransformerException("FOAP0001 : While evaluating an XPath 3.1 function call .(arg) an "
				  		                                                                            + "xdm map context item is present, but an "
				  		                                                                            + "xdm map's lookup key is not provided.", srcLocator); 
			   }
			}
			else if (xpath3CtxtItem instanceof XPathInlineFunction) {
				Map<QName, XObject> functionParamAndArgMap = new HashMap<QName, XObject>();
				Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
				
				try {
					XPathInlineFunction xpathInlineFunc = (XPathInlineFunction)xpath3CtxtItem;

					List<InlineFunctionParameter> funcParamList = xpathInlineFunc.getFuncParamList();
					String xpathFuncBodyStr = xpathInlineFunc.getFuncBodyXPathExprStr();		    	
					int funcParamCount = funcParamList.size();		    			    			    			    			    
					int argCount = getFunctionArgumentCount();
					if (argCount != funcParamCount) {
						throw new javax.xml.transform.TransformerException("XPTY0004 : The number of arguments provided for XPath "
																									+ "inline function call, is not equal "
																									+ "to count of function parameters.", srcLocator);
					}

					for (int idx = 0; idx < funcParamCount; idx++) {
						InlineFunctionParameter funcParam = funcParamList.get(idx);
						String funcParamName = funcParam.getParamName();
						SequenceTypeData paramType = funcParam.getParamType();
						
						XObject argValue = getFuncCallArgumentValue(idx, xctxt);

						if (paramType != null) {
							try {
								argValue = SequenceTypeSupport.castXdmValueToAnotherType(argValue, null, paramType, null);                     
								if (argValue == null) {
									throw new TransformerException("XTTE0505 : An item type of argument at position " + (idx + 1) + " of an XPath "
																									+ "function call, doesn't match an expected type.", srcLocator);
								}
							}
							catch (TransformerException ex) {
								throw ex;
							}
						}		    		

						functionParamAndArgMap.put(new QName(funcParamName), argValue);
					}

					inlineFunctionVarMap.putAll(functionParamAndArgMap);

					XPath inlineFnXPath = new XPath(xpathFuncBodyStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

					result = inlineFnXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

					SequenceTypeData funcReturnType = xpathInlineFunc.getReturnType();
					if (funcReturnType != null) {
						try {
							result = SequenceTypeSupport.castXdmValueToAnotherType(result, null, funcReturnType, null);
							if (result == null) {		    				
								throw new TransformerException("XTTE0505 : An item type of result of an XPath function call, doesn't match "
																														+ "an expected type.", srcLocator);
							}
						}
						catch (TransformerException ex) {
							throw ex; 
						}
					}		    			    	
			    }
			    finally {
			    	Set<QName> keysOfArgVariables = functionParamAndArgMap.keySet();    	
			    	Iterator<QName> iter = keysOfArgVariables.iterator();    	
			    	while (iter.hasNext()) {
			    		QName key = iter.next();
			    		inlineFunctionVarMap.remove(key);
			    	}
			    }
			}
		}
		else {
		    throw new javax.xml.transform.TransformerException("FOAP0001 : An XPath 3.1 function call .(arg) requires a context item, "
		    		                                                                                                + "that should be an xdm map or "
		    		                                                                                                + "a function item.", srcLocator);
		}

		return result;
	}

	/**
	 * Method definition, to get an XPath function call's 
	 * argument count.
	 * 
	 * @return                An integer value, representing 
	 *                        function call's argument count.
	 */
	private int getFunctionArgumentCount() {
		
		int result = 0;
		
		if (m_args != null) {
			result = (m_args.length + 3); 
		}
		else if (m_arg2 != null) {
			result = 3;
		}
		else if (m_arg1 != null) {
			result = 2;
		}
		else if (m_arg0 != null) {
			result = 1;
		}
		
		return result;
	}
	
	/**
	 * Method definition, to get XPath function call argument 
	 * value at the specified index.
	 * 
	 * @param idx						 The function call's supplied index 
	 *                                   value for function call's argument.
	 * @param xctxt                      An XPath context object
	 * @return                           The function call's argument value
	 * @throws TransformerException
	 */
	private XObject getFuncCallArgumentValue(int idx, XPathContext xctxt) throws TransformerException {
		
		XObject result = null;
		
		if (idx == 0) {
			result = m_arg0.execute(xctxt);	
		}
		else if (idx == 1) {
			result = m_arg1.execute(xctxt);	
		}
		else if (idx == 2) {
			result = m_arg2.execute(xctxt);	
		}
		else {
			result = (m_args[idx]).execute(xctxt); 
		}
		
		return result;
	}
	
	public int getMinArity() {
		return m_min_arity;
	}

	public void setMinArity(int minArity) {
		this.m_min_arity = minArity;
	}

	public int getMaxArity() {
		return m_max_arity;
	}

	public void setMaxArity(int maxArity) {
		this.m_max_arity = maxArity;
	}
	
	public int getActualArity() {
	    return m_defined_arity; 
	}
	  
	public void setActualArity(int definedArity) {
	    this.m_defined_arity = definedArity; 
	}

}
