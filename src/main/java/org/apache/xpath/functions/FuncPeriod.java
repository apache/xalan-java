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

import java.util.Arrays;
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
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.composite.XPathSequenceTypeData;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;

/**
 * Implementation of an XPath 3.1 function call .(arg),
 * where an XPath 3.1 context item supplies, the function item.
 * 
 * @author : Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncPeriod extends FunctionMultiArgs {
	
	private static final long serialVersionUID = -638591636434982044L;	

	/**
	 * Class constructor.
	 */
	public FuncPeriod() {
		m_min_arity = 1;
		m_max_arity = Integer.MAX_VALUE - 1;
	}
	
	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
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
				  XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
				  
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
						XPathSequenceTypeData paramType = funcParam.getParamType();
						
						XObject argValue = getFuncCallArgumentValue(idx, xctxt);

						if (paramType != null) {
							try {
								argValue = XPathSequenceTypeSupport.castXdmValueToAnotherType(argValue, null, paramType, null);                     
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

					XPathSequenceTypeData funcReturnType = xpathInlineFunc.getReturnType();
					if (funcReturnType != null) {
						try {
							result = XPathSequenceTypeSupport.castXdmValueToAnotherType(result, null, funcReturnType, null);
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
			else if (xpath3CtxtItem instanceof XPathNamedFunctionReference) {
				XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)xpath3CtxtItem;    					   
				String localName = xpathNamedFunctionReference.getFuncName();
				String fNamespace = xpathNamedFunctionReference.getFuncNamespace();
				Short arity = xpathNamedFunctionReference.getArity();
				int argCount = getFunctionArgumentCount();				
				if ((int)arity == argCount) {					
					FunctionTable funcTable = xctxt.getFunctionTable();
					
					Object funcId = null;

					if ((fNamespace == null) || ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(fNamespace))) { 
						funcId = funcTable.getFunctionIdForXSLBuiltinFuncs(localName);
					}
					else if ((XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(fNamespace)) {    	       	   
						funcId = funcTable.getFunctionIdForXPathBuiltinMathFuncs(localName);
					}
					else if ((XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(fNamespace)) {    	       	   
						funcId = funcTable.getFunctionIdForXPathBuiltinMapFuncs(localName);
					}
					else if ((XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(fNamespace)) {     	   
						funcId = funcTable.getFunctionIdForXPathBuiltinArrayFuncs(localName);
					}					
						
					if (funcId != null) {
						Function function = funcTable.getFunction(Integer.valueOf(funcId.toString()));
						List<Short> funcDefinedArity = Arrays.asList(function.getArity());
						if (funcDefinedArity.contains(arity)) {
							for (int idx = 0; idx < argCount; idx++) {									 									
								XObject argValue = getFuncCallArgumentValue(idx, xctxt);
								try {
									function.setArg(argValue, idx);
								} 
								catch (WrongNumberArgsException ex) {
									// no op
								}
							}

							result = function.execute(xctxt);
						}
						else {
							throw new TransformerException("XPTY0004 : The function arity value specified in an XPath named "
																										+ "function reference is " + arity + ", but the corresponding "
																										+ "XPath function {" + fNamespace + "}" + localName + " doesn't "
																										+ "allow this arity.", srcLocator);  
						}
					}										
					else if (fNamespace != null) {
						// This may handle, XSL stylesheet function call, and 
						// XPath 3.1 schema type constructor function call.						
						XSL3ConstructorOrExtensionFunction xsl3ConsExtFuncObj = new XSL3ConstructorOrExtensionFunction(fNamespace, localName, null);
						for (int idx = 0; idx < argCount; idx++) {
							XObject argValue = getFuncCallArgumentValue(idx, xctxt);
							try {
								xsl3ConsExtFuncObj.setArg(argValue, idx);
							} 
							catch (WrongNumberArgsException ex) {
								// no op
							}						
						}
						
						result = xsl3ConsExtFuncObj.execute(xctxt);
					}
				}
				else {
					throw new TransformerException("XPTY0004 : The number of arguments provided during an XPath function call {" + fNamespace + "}" + localName 
																    									+ " is " + argCount + ", but the corresponding XPath named function "
																    									+ "reference specifies the function arity value as " + arity + ".", 
																    									srcLocator); 
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
			result = getFunctionArgEffectiveValue(m_arg0, xctxt);
		}
		else if (idx == 1) {
			result = getFunctionArgEffectiveValue(m_arg1, xctxt);	
		}
		else if (idx == 2) {
			result = getFunctionArgEffectiveValue(m_arg2, xctxt);
		}
		else {			
			result = getFunctionArgEffectiveValue(m_args[idx], xctxt); 
		}
		
		return result;
	}	

}
