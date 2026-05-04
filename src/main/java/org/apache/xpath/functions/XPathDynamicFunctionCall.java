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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.string.FuncConcat;
import org.apache.xpath.objects.ElemFunctionItem;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSString;

/**
 * This class implements an XPath 3.1 dynamic function call, and
 * xdm map & array information lookup using function call syntax and unary 
 * lookup syntax respectively.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathDynamicFunctionCall extends Expression {
    
    private static final long serialVersionUID = -4177034386870890029L;

    /**
     * There are following three types of XPath expression evaluations, that 
     * this class implementation solves:
     * 
     * $funcVar(arg1, ...)   This is a call to an xdm inline function definition, 
     *                       where variable $funcVar is a function item.
     *                                
     * $mapVar(key)          This is an xdm map entry's value lookup via function 
     *                       call syntax.         
     * 
     * $mapVar?key           This is an xdm map entry's value lookup via unary 
     *                       lookup operator.
     *                  
     * $arrayVar?index       This is an xdm array item lookup for a given array 
     *                       variable reference and index value, for e.g $arrayVar?3.                  
     */
    
    private String m_funcRefVarName;
    
    private boolean m_isUnaryLookup;
    
    private List<String> m_argList;
    
    private List<String> m_trailingArgList;
    
    private String[] m_xpathChainedArgListArr;
    
    /**
     * Function argument run-time value, that is lhs operand of
     * XPath operator "=>" whose rhs operand is this dfc compiled
     * object.
     */
    private XObject m_lArgObj;
    
    /**
     * The class fields m_vars & m_globals_size declared below are used during 
     * XPath.fixupVariables(..) action as performed within object of this class.
     */
    
    private Vector m_vars;
    
    private int m_globals_size;
    
    private XSL3FunctionService m_xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
    
    /**
     * Evaluate an XPath dynamic function call expression.
     */
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
       XObject evalResult = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       int contextNode = xctxt.getContextNode();
       
       Map<QName, XObject> xpathVariableMap = xctxt.getXPathVarMap();
                    
       // Try finding an XPath function reference object
       XObject functionRef = xpathVariableMap.get(new QName(m_funcRefVarName));
       
       if (functionRef == null) {
           ExpressionContext exprContext = xctxt.getExpressionContext();
          
           try {
              functionRef = exprContext.getVariableOrParam(new QName(m_funcRefVarName));
           }
           catch (TransformerException ex) {
              // Trying to get an XPath inline function reference, from within 
              // stylesheet's global variable scope. 
              ExpressionNode expressionNode = getExpressionOwner();
              ExpressionNode stylesheetRootNode = null;
              while (expressionNode != null) {
                 stylesheetRootNode = expressionNode;
                 expressionNode = expressionNode.exprGetParent();                     
              }
              StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
              Map<QName, XPathInlineFunction> globalInlineFunctionVarMap = stylesheetRoot.
                                                                            getInlineFunctionVarMap();
              functionRef = globalInlineFunctionVarMap.get(new QName(m_funcRefVarName));
              
              String errMesg = ex.getMessage();
              if (errMesg.startsWith("XPST0008")) {
            	 throw ex; 
              }
           }           
       }
       
       List<XMLNSDecl> prefixTable = null;
       
       if (functionRef != null) {
    	    ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
            
            if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
            }
                	    
    	    if (functionRef instanceof XPathInlineFunction) {
    	    	if (m_argList == null) {
    	    		m_argList = new ArrayList<String>(); 
    	    	}

    	    	evalResult = m_xsl3FunctionService.evaluateXPathInlineFunction((XPathInlineFunction)functionRef, m_argList, m_lArgObj, 
																					    	    			xctxt, prefixTable, m_vars, m_globals_size, 
																					    	    			m_xpathVarList, m_funcRefVarName);               
	           if ((evalResult instanceof XPathNamedFunctionReference) && (m_trailingArgList != null)) {
	        	  evalResult = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)evalResult, m_trailingArgList, 
	        			                                                                  null, prefixTable, m_vars, m_globals_size, getExpressionOwner(), 
	        			                                                                  xctxt); 
	           }
	        }
    	    else if (functionRef instanceof XPathMap) {
     		   evalResult = getDfcResultFromXdmMap((XPathMap)functionRef, xctxt, srcLocator, contextNode, prefixTable);
     	    }
    	    else if (functionRef instanceof XPathArray) {
    	       evalResult = getDfcResultFromXdmArray((XPathArray)functionRef, xctxt, srcLocator, contextNode, prefixTable);
    	    }
    	    else if (functionRef instanceof ElemFunctionItem) {
    	    	evalResult = getDfcResultFromXslFunction((ElemFunctionItem)functionRef, xctxt, srcLocator, contextNode, prefixTable);
    	    }
    	    else if (functionRef instanceof XPathNamedFunctionReference) {
    	    	evalResult = getDfcResultFromNamedFuncRef((XPathNamedFunctionReference)functionRef, xctxt, srcLocator, contextNode, prefixTable);
    	    }
    	    else {
    	       Object funcRefObj1 = functionRef.object();
    	       if (funcRefObj1 instanceof Function) {
    	    	  evalResult = getDfcResultFromXPathBuiltInFunction((Function)funcRefObj1, xctxt, srcLocator, contextNode, prefixTable);
    	       }
    	       else if (funcRefObj1 instanceof ElemFunction) {    	    	   
    	    	  evalResult = getDfcResultFromXslFunctionDecl(functionRef, (ElemFunction)funcRefObj1, xctxt, srcLocator, contextNode, prefixTable);
    	       }
    	       else if (isFunctionRefXsSimpleTypeDefinition(functionRef)) {
    	    	   evalResult = getDfcResultFromSchemaSimpleTypeRef(functionRef, xctxt, srcLocator, contextNode, prefixTable);
    	       }
    	       else {
    	    	   evalResult = XObject.create(new XSString("")); 
    	       }
    	    }
      }
      else {
         throw new javax.xml.transform.TransformerException("XPST0008 : Variable '" + m_funcRefVarName + "' has "
                                                                                                       + "not been declared, or its declaration is not in scope.", 
                                                                                                                                              xctxt.getSAXLocator());    
      }
       
      if ((evalResult instanceof XPathInlineFunction) && (m_trailingArgList != null)) {
    	 evalResult = m_xsl3FunctionService.evaluateXPathInlineFunction((XPathInlineFunction)evalResult, m_trailingArgList, null, 
    			                                                        xctxt, prefixTable, m_vars, m_globals_size, 
    			                                                        m_xpathVarList, m_funcRefVarName); 
      }
               
      return evalResult;
      
    }

	@Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }
    
    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }
    
	/**
	 * Method definition to get an xdm array item value at a specified index.
	 * 
	 * @param xpathArr							An xdm array object instance
	 * @param indexVal							An array index value
	 * @param xctxt							    An XPath context object
	 * @return									An xdm value retrieved from the specified array index
	 * @throws TransformerException
	 */
    private XObject getXPathArrayResultByIndex(XPathArray xpathArr, XObject indexVal, XPathContext xctxt) 
    		                                                                                           throws TransformerException {
    	XObject evalResult;
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();
    	
    	String argValStr = XslTransformEvaluationHelper.getStrVal(indexVal);     				  

    	Integer intVal = null;     				  
    	try {
    		intVal = Integer.valueOf(argValStr);
    		if (!(intVal > 0 && (intVal <= xpathArr.size()))) {
	    			throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, "
							    					                                                           + "needs to have one numeric argument with value greater or equal to one "
							    					                                                           + "specifying an index value for an array.", srcLocator); 
    		}
    	}
    	catch (NumberFormatException ex) {
    		throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, "
								    				                                                           + "needs to have one numeric argument with value greater or equal to one "
								    				                                                           + "specifying an index value for an array.", srcLocator); 
    	}

    	evalResult = xpathArr.get(intVal - 1);
    	
    	return evalResult;
	}
    
    /**
     * Method definition, to lookup an xdm map entry's value for the specified key.
     * 
     * @param xpathMap								An xdm map object instance
     * @param xpathKeyStr							An XPath expression string for map's key
     * @param prefixTable							List of values from XSL transformation prefix
     *                                              table.
     * @param xctxt                                 An XPath context object
     * @return										Map entry's value for the supplied key	
     * @throws TransformerException
     */
    private XObject getXPathMapEntryValueByKey(XPathMap xpathMap, String xpathKeyStr, 
    									       List<XMLNSDecl> prefixTable, XPathContext xctxt) throws TransformerException {

    	XObject evalResult = null;

    	SourceLocator srcLocator = xctxt.getSAXLocator(); 

    	final int contextNode = xctxt.getCurrentNode();

    	if (prefixTable != null) {
    		xpathKeyStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathKeyStr, prefixTable);
    	}

    	XPath argXPath = new XPath(xpathKeyStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    	if (m_vars != null) {
    		argXPath.fixupVariables(m_vars, m_globals_size);
    	}

    	XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    	
    	if ((argValue instanceof XString) || (argValue instanceof XNumber) || (argValue instanceof XBoolean) || (argValue instanceof XBooleanStatic)) {
    		evalResult = xpathMap.get(argValue);     					 
    		if (evalResult == null) {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm map doesn't have an entry with key name '" + 
																									    					XslTransformEvaluationHelper.getStrVal(argValue) + "'.",  
																									    					srcLocator); 
    		}
    	}
    	else if (argValue instanceof XSAnyAtomicType) {
    		evalResult = xpathMap.get(argValue);     					 
    		if (evalResult == null) {
    			throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm map doesn't have an entry with key name '" + 
																									    					XslTransformEvaluationHelper.getStrVal(argValue) + "'.",  
																									    					srcLocator); 
    		} 
    	}   	
    	else if (argValue instanceof ResultSequence) {
    		ResultSequence argSeq = (ResultSequence)argValue;
    		ResultSequence evalResultSeq = new ResultSequence();
    		int argSeqLength = argSeq.size();
    		for (int idx = 0; idx < argSeqLength; idx++) {
    			XObject argSeqItem = argSeq.item(idx);
    			evalResultSeq.add(xpathMap.get(argSeqItem));
    		}

    		evalResult = evalResultSeq;
    	}
    	else if (argValue instanceof XMLNodeCursorImpl) {    	        		
    		if (m_isUnaryLookup) {
    			evalResult = xpathMap.get(new XSString(xpathKeyStr));
    		}
    		else {
    			ResultSequence argSeq = XslTransformEvaluationHelper.getResultSequenceFromXObject(argValue, xctxt);
        	    ResultSequence evalResultSeq = new ResultSequence();
        	    int argSeqLength = argSeq.size();
        		for (int idx = 0; idx < argSeqLength; idx++) {
        			XObject argSeqItem = argSeq.item(idx);
        			String strValue = XslTransformEvaluationHelper.getStrVal(argSeqItem);
        			evalResultSeq.add(xpathMap.get(new XSString(strValue)));
        		}

        		if (evalResultSeq.size() == 1) {
        		   evalResult = evalResultSeq.item(0);
        		}
        		else {
        		   evalResult = evalResultSeq;
        		}
    		}
    	}
    	else {
    		throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm map doesn't have an entry with the specified key value.", srcLocator); 
    	}

    	return evalResult;		  
    }
    
    /**
     * Method definition to check whether, an XPath dynamic function call
     * resolves to an XPath simple type constructor function call.
     */
    private boolean isFunctionRefXsSimpleTypeDefinition(XObject functionRef) {
		
    	boolean result = false;
		
		if ((functionRef instanceof ResultSequence) && (((ResultSequence)functionRef).size() == 1)) {
			XObject xObj = (((ResultSequence)functionRef)).item(0);
			if (xObj instanceof XSString) {
			   XSString xsString = (XSString)xObj;
			   if ((Expression.XS_SIMPLE_TYPE_NAME).equals(xsString.stringValue()) && (xsString.getXsTypeDefinition() != null)) {
				   result = true; 
			   }
			}
		}
		
		return result;
	}
    
    public String getFuncRefVarName() {
        return m_funcRefVarName;
    }

    public void setFuncRefVarName(String funcRefVarName) {
        this.m_funcRefVarName = funcRefVarName;
    }

    public List<String> getArgList() {
        return m_argList;
    }

    public void setArgList(List<String> argList) {
        this.m_argList = argList;
    }
    
    public void setIsFromUnaryLookupEvaluation(boolean isUnaryLookup) {
		m_isUnaryLookup = isUnaryLookup; 		
	}
	
	public boolean getIsFromUnaryLookupEvaluation() {
		return m_isUnaryLookup;
	}

	public void setTrailingArgList(List<String> strList) {
		m_trailingArgList = strList; 		
	}
	
	public List<String> getTrailingArgList() {
		return m_trailingArgList;
	}
	
	public void setChainedArgXPathStrArray(String[] strArray) {	
		m_xpathChainedArgListArr = strArray; 
	}
	
	public String[] getChainedArgXPathStrArray() {
		return m_xpathChainedArgListArr;
	}

	public XObject getArg0() {
		return m_lArgObj;
	}
	
	public void setArg0(XObject lArgObj) {
		m_lArgObj = lArgObj; 		
	}
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied xdm map, using function call syntax 
	 * within the originating XSL stylesheet.
	 * 
	 * @param xpathMap							An xdm map that is the source of result
	 *                                          value from this method.
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromXdmMap(XPathMap xpathMap, XPathContext xctxt, SourceLocator srcLocator,
			                               int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;

		if (m_xpathChainedArgListArr != null) {
			int arrSize = m_xpathChainedArgListArr.length;
			for (int idx = 0; idx < arrSize; idx++) {
				String argXPathStr = m_xpathChainedArgListArr[idx];     				 
				if (idx == 0) {
					result = getXPathMapEntryValueByKey(xpathMap, argXPathStr, prefixTable, xctxt);
				}
				else if (result instanceof XPathMap) {
					result = getXPathMapEntryValueByKey((XPathMap)result, argXPathStr, prefixTable, xctxt); 
				}
				else if (result instanceof XPathArray) {
					if (prefixTable != null) {
						argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
					}

					XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
					if (m_vars != null) {
						argXPath.fixupVariables(m_vars, m_globals_size);
					}

					XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

					result = getXPathArrayResultByIndex((XPathArray)result, argValue, xctxt); 
				}
			}
		}
		else {
			if (m_argList != null) {
				if (m_argList.size() != 1) {
					throw new javax.xml.transform.TransformerException("XPTY0004 : Function call reference for map information lookup, needs to have "
																																	+ "one argument which should be one of map's key "
																																	+ "name.", srcLocator); 
				}
				else {
					String argXPathStr = m_argList.get(0);
					if ("*".equals(argXPathStr)) {
						// This is xdm map's wild-card key specifier. To return 
						// all the map entry values as typed sequence.     				  
						Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
						ResultSequence rSeq = new ResultSequence();
						Set<XObject> keySet = nativeMap.keySet();
						Iterator<XObject> iter = keySet.iterator();
						while (iter.hasNext()) {
							XObject xObj = iter.next();
							rSeq.add(nativeMap.get(xObj));
						}

						result = rSeq;
					}
					else if (".".equals(argXPathStr)) {
						XObject contextItem = xctxt.getXPath3ContextItem();
						result = xpathMap.get(contextItem);
					}
					else {
						boolean isRngNextFuncCall = false;

						Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();

						if ((nativeMap.size() == 3) && (Constants.FN_XALAN_RNG_NEXT + "()").equals(argXPathStr)) {
							XObject xObj1 = nativeMap.get(new XString(Constants.ELEMNAME_NUMBER_STRING)); 
							XObject xObj2 = nativeMap.get(new XString(Constants.FN_XALAN_RNG_NEXT));
							XObject xObj3 = nativeMap.get(new XString(Constants.FN_XALAN_RNG_PERMUTE_STR));
							if ((xObj1 != null) && (xObj2 != null) && (xObj3 != null)) {
								// An XPath expression is function call fn:random-number-generator's 
								// 'next' key. 
								isRngNextFuncCall = true;
							}
						}

						if (!isRngNextFuncCall) {
							result = getXPathMapEntryValueByKey(xpathMap, argXPathStr, prefixTable, xctxt);

							if ((result instanceof XPathMap) && (m_trailingArgList != null)) {
								if (m_trailingArgList.size() == 1) {
									argXPathStr = m_trailingArgList.get(0); 
									result = getXPathMapEntryValueByKey((XPathMap)result, argXPathStr, prefixTable, xctxt);
								}
								else {
									// Return an empty sequence
									result = new ResultSequence(); 
								}
							}
							else if ((result instanceof XPathInlineFunction) && (m_trailingArgList != null)) {
								result = m_xsl3FunctionService.evaluateXPathInlineFunction((XPathInlineFunction)result, m_trailingArgList, null, 
																																		xctxt, prefixTable, m_vars, m_globals_size, 
																																		m_xpathVarList, m_funcRefVarName);
							}
						}
						else {
							XPath xpathObj = new XPath(Keywords.FUNC_RANDOM_NUMBER_GENERATOR + "()", srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

							if (m_vars != null) {
								xpathObj.fixupVariables(m_vars, m_globals_size);
							}

							result = xpathObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
						}
					}
				}
			}
			else if (m_lArgObj != null) {
				Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();

				result = nativeMap.get(m_lArgObj); 
			}
		}

		return result;		
	}
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied xdm array, using function call syntax 
	 * within the originating XSL stylesheet.
	 * 
	 * @param xpathArr							An xdm array that is the source of result
	 *                                          value from this method.
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromXdmArray(XPathArray xpathArr, XPathContext xctxt, SourceLocator srcLocator,
			                                 int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;

		if (m_xpathChainedArgListArr != null) {
			int arrSize = m_xpathChainedArgListArr.length;
			for (int idx = 0; idx < arrSize; idx++) {
				String argXPathStr = m_xpathChainedArgListArr[idx];     				 
				if (idx == 0) {
					if (prefixTable != null) {
						argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
					}

					XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
					if (m_vars != null) {
						argXPath.fixupVariables(m_vars, m_globals_size);
					}

					XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());      					 
					result = getXPathArrayResultByIndex(xpathArr, argValue, xctxt);
				}
				else if (result instanceof XPathMap) {
					result = getXPathMapEntryValueByKey((XPathMap)result, argXPathStr, prefixTable, xctxt); 
				}
				else if (result instanceof XPathArray) {
					if (prefixTable != null) {
						argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
					}

					XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
					if (m_vars != null) {
						argXPath.fixupVariables(m_vars, m_globals_size);
					}

					XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());

					result = getXPathArrayResultByIndex((XPathArray)result, argValue, xctxt); 
				}
			}
		}
		else {
			if (m_argList != null) {
				if (m_argList.size() != 1) {
					throw new javax.xml.transform.TransformerException("XPTY0004 : Function call syntax for array information lookup, needs to have "
																															+ "one argument which should be position "
																															+ "within an array.", srcLocator); 
				}
				else {
					String argXPathStr = m_argList.get(0);
					if ("*".equals(argXPathStr)) {
						// This is xdm array's wild-card key specifier. To return 
						// all the array values as typed sequence. 
						ResultSequence rSeq = new ResultSequence();
						int arrSize = xpathArr.size();
						for (int idx = 0; idx < arrSize; idx++) {
							rSeq.add(xpathArr.get(idx));
						}    				 
						result = rSeq;
					}
					else if (".".equals(argXPathStr)) {
						XObject contextItem = xctxt.getXPath3ContextItem();     				  
						result = getXPathArrayResultByIndex(xpathArr, contextItem, xctxt);
					}
					else {
						if (prefixTable != null) {
							argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
						}

						XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
						if (m_vars != null) {
							argXPath.fixupVariables(m_vars, m_globals_size);
						}

						XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());	                  
						if (argValue instanceof ResultSequence) {
							ResultSequence rSeqArg = (ResultSequence)argValue;
							ResultSequence rSeqAnswer = new ResultSequence();
							int arrSize = rSeqArg.size();
							for (int idx = 0; idx < arrSize; idx++) {
								XObject oneArgValue = rSeqArg.item(idx);
								String strVal = XslTransformEvaluationHelper.getStrVal(oneArgValue);
								int arrQueryIndex = Integer.valueOf(strVal); 
								rSeqAnswer.add(xpathArr.get(arrQueryIndex - 1));
							}

							result = rSeqAnswer;
						}
						else {
							result = getXPathArrayResultByIndex(xpathArr, argValue, xctxt);
						}
					}
				}
			}
			else if (m_lArgObj != null) {
				String str1 = XslTransformEvaluationHelper.getStrVal(m_lArgObj);
				int i1 = Integer.valueOf(str1);
				result = xpathArr.get(i1 - 1); 
			}
		}

		return result;
	}		
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied function item (encapsulating xsl:function 
	 * compiled representation), using function call syntax within the originating XSL 
	 * stylesheet.
	 * 
	 * @param elemFuncItem					    An xdm function item object (encapsulating xsl:function 
	 *                                          compiled representation) that is the source of result
	 *                                          value from this method.
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromXslFunction(ElemFunctionItem elemFuncItem, XPathContext xctxt, SourceLocator srcLocator,
			                                    int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;

		ResultSequence argSequence = new ResultSequence();
		int argListSize = m_argList.size();
		for (int idx = 0; idx < argListSize; idx++) {
			String argXPathStr = m_argList.get(idx);
			if (prefixTable != null) {
				argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
			}

			XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			if (m_vars != null) {
				argXPath.fixupVariables(m_vars, m_globals_size);
			}

			XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
			argSequence.add(argValue);
		}

		ExpressionNode expressionNode = getExpressionOwner();
		ExpressionNode stylesheetRootNode = null;
		while (expressionNode != null) {
			stylesheetRootNode = expressionNode;
			expressionNode = expressionNode.exprGetParent();                     
		}
		
		StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;

		TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();
		
		ElemFunction elemFunction = elemFuncItem.getElemFunction();

		result = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
		
		return result;
	}
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied xdm function reference object, using 
	 * function call syntax within the originating XSL stylesheet.
	 * 
	 * The XPath named function reference supplied as an argument to this method,
	 * may represent XPath built-in function, XSL function declaration (i.e, 
	 * representing xsl:function), XPath inline function declaration, or an 
	 * XML Schema constructor function call. 
	 * 
	 * @param functionRef					    An XPath function reference object
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromNamedFuncRef(XPathNamedFunctionReference functionRef, XPathContext xctxt, SourceLocator srcLocator,
			                                     int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;
		
		ElemFunction elemFunction = functionRef.getXslStylesheetFunction();
		if (elemFunction != null) {
			// Evaluating XSL stylesheet function call (i.e, xsl:function 
			// call evaluation).
			ResultSequence argSequence = new ResultSequence();
			int argListSize = m_argList.size();
			for (int idx = 0; idx < argListSize; idx++) {
				String argXPathStr = m_argList.get(idx);
				if (prefixTable != null) {
					argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
				}

				XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
				if (m_vars != null) {
					argXPath.fixupVariables(m_vars, m_globals_size);
				}

				XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
				argSequence.add(argValue);
			}

			ExpressionNode expressionNode = getExpressionOwner();
			ExpressionNode stylesheetRootNode = null;
			while (expressionNode != null) {
				stylesheetRootNode = expressionNode;
				expressionNode = expressionNode.exprGetParent();                     
			}
			
			StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;

			TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();

			result = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
		}
		else {
		    result = m_xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)functionRef, m_argList, 
																				   null, prefixTable, m_vars, m_globals_size, getExpressionOwner(), 
																				   xctxt);
		}
		
		return result;
	}
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied XPath built-in function compiled object, 
	 * using function call syntax within the originating XSL stylesheet.
	 * 
	 * @param funcObj					        An XPath built-in function object reference
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromXPathBuiltInFunction(Function funcObj, XPathContext xctxt, SourceLocator srcLocator, 
			                                             int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;

		String funcNamespace = funcObj.getNamespace();
		String funcLocalName = funcObj.getLocalName();

		String requiredArityStr = "";
		String expandedFuncName = null;
		boolean isRuntimeArityOk = false;
		if (funcObj instanceof FuncConcat) {
			FuncConcat funcConcat = (FuncConcat)funcObj;
			int concatRunTimeArityValue = m_argList.size();
			expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + concatRunTimeArityValue;
			int minArity = funcConcat.getMinArity();
			int maxArity = funcConcat.getMaxArity();
			requiredArityStr = (minArity + " .. " + maxArity); 
			if ((concatRunTimeArityValue >= minArity) && (concatRunTimeArityValue <= maxArity)) {
				isRuntimeArityOk = true; 
			}
		}
		else { 	    			 
			short runTimeArityValue = (short)(m_argList.size());
			expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + runTimeArityValue;
			Short[] funcDefinedArity = funcObj.getDefinedArity();
			List<Short> arityList = Arrays.asList(funcDefinedArity);
			int listSize1 = arityList.size();
			StringBuffer strBuff = new StringBuffer();
			for (int idx = 0; idx < listSize1; idx++) {
				String str1 = String.valueOf(arityList.get(idx));
				if (idx < (listSize1 - 1)) {
					strBuff.append(str1 + ",");
				}
				else {
					strBuff.append(str1);
				}
			}

			requiredArityStr = strBuff.toString(); 

			if (arityList.contains(runTimeArityValue)) {
				isRuntimeArityOk = true; 
			}
		}

		int argListSize = m_argList.size();

		if (isRuntimeArityOk) {    	    		 
			try {    	    			     	    			 
				for (int idx = 0; idx < argListSize; idx++) {
					String argXPathStr = m_argList.get(idx);
					if (prefixTable != null) {
						argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
					}

					XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
					if (m_vars != null) {
						argXPath.fixupVariables(m_vars, m_globals_size);
					}

					XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
					funcObj.setArg(argValue, idx);
				}
			}
			catch (WrongNumberArgsException ex) {
				throw new javax.xml.transform.TransformerException("XPTY0004 : Wrong number of arguments provided, "
																										+ "during function call " + expandedFuncName + ".", srcLocator); 
			}

			result = funcObj.execute(xctxt);
		}
		else {
			throw new javax.xml.transform.TransformerException("XPTY0004 : The number of arguments provided for "
																										+ "function call " + expandedFuncName + " is "
																										+ "incorrect. Required " + requiredArityStr + ", supplied " 
																										+ argListSize + ".", srcLocator); 
		}

		return result;
	}
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied XSL function declaration information, 
	 * using function call syntax within the originating XSL stylesheet.
	 * 
	 * @param functionRef					    An XSL stylesheet originating function
	 *                                          reference information.
	 * @param elemFunction					    An XSL stylesheet function compiled object                                         
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromXslFunctionDecl(XObject functionRef, ElemFunction elemFunction, XPathContext xctxt,
			                                        SourceLocator srcLocator, int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;

		StylesheetRoot stylesheetRoot = functionRef.getXslStylesheetRoot();
		TransformerImpl transformerImpl = stylesheetRoot.getTransformerImpl();	    		  

		int funcArity = elemFunction.getArity();    	    	  

		if (m_argList == null) {
			m_argList = new ArrayList<>(); 
		}

		int argListSize = m_argList.size();

		if (argListSize == funcArity) {    	    		  
			ResultSequence argSequence = new ResultSequence();
			for (int idx = 0; idx < argListSize; idx++) {
				String argXPathStr = m_argList.get(idx);
				if (prefixTable != null) {
					argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
				}

				XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
				if (m_vars != null) {
					argXPath.fixupVariables(m_vars, m_globals_size);
				}

				XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
				argSequence.add(argValue);
			}

			result = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
		}
		else if (funcArity == 0) {
			ResultSequence argSequence = new ResultSequence();
			result = elemFunction.evaluateXslFunction(transformerImpl, argSequence);
		}
		else {
			QName funcName = elemFunction.getName();

			throw new javax.xml.transform.TransformerException("XPTY0004 : The number of arguments provided for "
																											+ "stylesheet function call " + funcName.toString() + " is "
																											+ "incorrect. Required " + funcArity + ", supplied " 
																											+ argListSize + ".", srcLocator);  
		}

		return result;
	}
	
	/**
	 * Method definition, to get XPath 3.1 dynamic function call syntax result,
	 * that gets result from the supplied XML Schema simple type constructor function
	 * call, using function call syntax within the originating XSL stylesheet.
	 * 
	 * @param functionRef					    An XSL stylesheet originating function
	 *                                          reference information.                                        
	 * @param xctxt                             An XPath context object
	 * @param srcLocator                        An XSL transformation SourceLocator object
	 * @param contextNode                       An XPath context node handle
	 * @param prefixTable                       An XSL transformation namespace prefix table
	 * @return                                  The result of XPath expression evaluation
	 * @throws TransformerException
	 */
	private XObject getDfcResultFromSchemaSimpleTypeRef(XObject functionRef, XPathContext xctxt, SourceLocator srcLocator, 
			                                            int contextNode, List<XMLNSDecl> prefixTable) throws TransformerException {
		
		XObject result = null;

		XObject xObj = (((ResultSequence)functionRef)).item(0);
		XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)(xObj.getXsTypeDefinition());
		int argCount = m_argList.size();
		if (argCount == 1) {
			String argXPathStr = m_argList.get(0);
			if (prefixTable != null) {
				argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, prefixTable);
			}

			XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
			if (m_vars != null) {
				argXPath.fixupVariables(m_vars, m_globals_size);
			}

			XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
			String argStrValue = XslTransformEvaluationHelper.getStrVal(argValue);
			try {
				// Validate, whether an XML Schema constructor function call argument,
				// is a valid instance of the relevant XML Schema simple type.
				xsSimpleTypeDecl.validate(argStrValue, null, null);
				
				result = argValue; 
			} 
			catch (InvalidDatatypeValueException ex) {
				throw new javax.xml.transform.TransformerException("XPTY0004 : XPath dynamic function call $" + 
																									m_funcRefVarName + "(" + argXPathStr + ") evaluation failed "
																									+ "with a type error : " + ex.getMessage(), srcLocator); 
			}
		}
		else {
			throw new javax.xml.transform.TransformerException("XPTY0004 : XPath dynamic function call $" + m_funcRefVarName + "(..) has " + argCount + " "
																															 + "arguments. Expected 1.", srcLocator); 
		}

		return result;
	}

}
