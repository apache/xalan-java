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
package org.apache.xpath.functions.array;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathArrayConstructor;
import org.apache.xpath.composite.XPathMapConstructor;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSNumericType;

/**
 * Evaluation of an XPath 3.1 function array:for-each.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayForEach extends Function2Args {

   private static final long serialVersionUID = 2912594883291006421L;
   
   /**
    * Class constructor.
    */
   public FuncArrayForEach() {
	   m_arity = new Short[] { 2 };
   }
   
   /**
    * This class field is used during, XPath.fixupVariables(..) action 
    * as performed within object of this class.  
    */    
   private Vector m_vars;
   
   /**
    * This class field is used during, XPath.fixupVariables(..) action 
    * as performed within object of this class.  
    */
   private int m_globals_size;

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
      
	    XPathArray result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();                
        
        if (m_vars != null) {
           m_arg0.fixupVariables(m_vars, m_globals_size);
        }
        
        XObject xObjArg0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
        
        if (!(xObjArg0 instanceof XPathArray)) {
           throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function array 'for-each' first "
           		                                                                              + "argument is not an xdm array.", srcLocator);
        }
                    
        if (m_arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)m_arg1;
            verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
            
            result = evaluateArrayForEach((XPathArray)xObjArg0, inlineFuncArg, xctxt); 
        }
        else if (m_arg1 instanceof Function) {
        	Function function = (Function)m_arg1;        	        	
        	
        	result = new XPathArray();
        	
        	try {
        		Expression arg0 = null;
        		Expression arg1 = null;
        		Expression arg2 = null;
        		
        		Function3Args function3Args = null;
        		Function2Args function2Args = null;
        		FunctionOneArg functionOneArg = null;
        		
        		if (function instanceof Function3Args) {
        			function3Args = (Function3Args)function;				
        			arg0 = function3Args.getArg0();
        			arg1 = function3Args.getArg1();
        			arg2 = function3Args.getArg2();
        		}
        		else if (function instanceof Function2Args) {
        			function2Args = (Function2Args)function;				
        			arg0 = function2Args.getArg0();
        			arg1 = function2Args.getArg1();
        		}
        		else if (function instanceof FunctionOneArg) {
        			functionOneArg = (FunctionOneArg)function;				
        			arg0 = functionOneArg.getArg0();
        		}
				
				boolean b1 = false;
				if (arg0 instanceof FuncArgPlaceholder) {
				   b1 = true;
				}
				
				boolean b2 = false;
				if (arg1 instanceof FuncArgPlaceholder) {
				   b2 = true;
				}
				
				boolean b3 = false;
				if (arg2 instanceof FuncArgPlaceholder) {
				   b3 = true;
				}
				
        		XPathArray xpathArr = (XPathArray)xObjArg0;
        		int size1 = xpathArr.size();
        		for (int idx = 0; idx < size1; idx++) {
        			XObject xObj = xpathArr.get(idx);

        			if (function instanceof Function3Args) {        				
        				if (b1) {
        					function3Args.setArg(xObj, 0);
        				}

        				if (b2) {
        					function3Args.setArg(xObj, 1);
        				}

        				if (b3) {
        					function3Args.setArg(xObj, 2);
        				}
        				
        				XObject funcEvalResult = function3Args.execute(xctxt);
        				
        			    result.add(funcEvalResult);        					
        			}
        			else if (function instanceof Function2Args) {
        				if (b1) {
        					function2Args.setArg(xObj, 0);
        				}

        				if (b2) {
        					function2Args.setArg(xObj, 1);
        				}
        				
                        XObject funcEvalResult = function2Args.execute(xctxt);
                        
                        result.add(funcEvalResult);
        			}
        			else if (function instanceof FunctionOneArg) {
        				if (b1) {
        					functionOneArg.setArg(xObj, 0);
        				}
        				
                        XObject funcEvalResult = functionOneArg.execute(xctxt);
                        
                        result.add(funcEvalResult);
        			}
        		}
            }
            catch (TransformerException ex) {
            	throw ex;
            }
        	catch (WrongNumberArgsException ex) {
        		// no op
        	}
        }
        else if (m_arg1 instanceof XPathNamedFunctionReference) {
            XPathNamedFunctionReference xpathNamedFuncRef = (XPathNamedFunctionReference)m_arg1;
            String localName = xpathNamedFuncRef.getFuncName();
            String fNamespace = xpathNamedFuncRef.getFuncNamespace();
            short arity = xpathNamedFuncRef.getArity();
            if (arity == 1) {
            	result = new XPathArray();
            	
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
                	Function function = funcTable.getFunction((Integer)funcId);

                	XPathArray xpathArr = (XPathArray)xObjArg0;
                	int size1 = xpathArr.size();        		
                	for (int idx = 0; idx < size1; idx++) {
                		XObject xObj = xpathArr.get(idx);
                		function.setArg0(xObj);
                		XObject funcEvalResult = function.execute(xctxt);

                		result.add(funcEvalResult);
                	}
                }
            }
            else {
            	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function array 'for-each' "
				            			                                                                   + "argument function item arity should be "
				            			                                                                   + "one. The supplied argument function item "
				            			                                                                   + "arity is " + arity + ".", srcLocator);  
            }
        }
        else if (m_arg1 instanceof Variable) {
            if (m_vars != null) {
            	m_arg1.fixupVariables(m_vars, m_globals_size);
            }
            
            XObject xObjArg1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
            
            if (xObjArg1 instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)xObjArg1;
                verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
                
                result = evaluateArrayForEach((XPathArray)xObjArg0, inlineFuncArg, xctxt);   
            }
            else {
                throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function array 'for-each', "
                                                                                                    + "second argument is not a function item.", srcLocator);    
            }
        }
        else if (m_arg1 instanceof XPathMapConstructor) {
        	// An XPath 3.1 function array:for-each's second argument, 
        	// uses xdm map as the mapping function.
        	
        	XPathMap xpathMapObj1 = (XPathMap)(((XPathMapConstructor)m_arg1).execute(xctxt));
        	
        	result = new XPathArray();
        	
        	XPathArray xpathArr0 = (XPathArray)xObjArg0;
        	int size1 = xpathArr0.size();
        	for (int idx = 0; idx < size1; idx++) {
        	   XObject xObj = xpathArr0.get(idx);
        	   XObject xObj1 = xpathMapObj1.get(xObj);
        	   if (xObj1 != null) {
        	      result.add(xObj1);
        	   }
        	   else {
        		   result.add(new ResultSequence()); 
        	   }
        	}
        }
        else if (m_arg1 instanceof XPathMap) {
        	// An XPath 3.1 function array:for-each's second argument, 
        	// uses xdm map as the mapping function.
        	
        	XPathMap xpathMapObj1 = (XPathMap)m_arg1;
        			
            result = new XPathArray();
        	
        	XPathArray xpathArr0 = (XPathArray)xObjArg0;
        	int size1 = xpathArr0.size();
        	for (int idx = 0; idx < size1; idx++) {
        	   XObject xObj = xpathArr0.get(idx);
        	   XObject xObj1 = xpathMapObj1.get(xObj);
        	   if (xObj1 != null) {
        	      result.add(xObj1);
        	   }
        	   else {
        		   result.add(new ResultSequence()); 
        	   }
        	}
        }
        else if (m_arg1 instanceof XPathArrayConstructor) {
        	// An XPath 3.1 function array:for-each's second argument, 
        	// uses xdm array as the mapping function.
        	
        	XPathArray xpathArrObj1 = (XPathArray)(((XPathArrayConstructor)m_arg1).execute(xctxt));
        	
        	int size2 = xpathArrObj1.size();
        	
        	result = new XPathArray();
        	
        	XPathArray xpathArr0 = (XPathArray)xObjArg0;
        	int size1 = xpathArr0.size();
        	for (int idx = 0; idx < size1; idx++) {
        	   XObject xObj = xpathArr0.get(idx);
        	   if ((xObj instanceof XNumber) || (xObj instanceof XSNumericType)) {
        		  String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
        		  try {
        		     int a1 = Integer.valueOf(str1);
        		     if ((a1 >= 1) && (a1 <= size2)) {
        		    	XObject xObj1 = xpathArrObj1.get(a1 - 1);
        		    	result.add(xObj1);
        		     }
        		     else {
        		    	result.add(new ResultSequence()); 
        		     }
        		  }
        		  catch (NumberFormatException ex) {
        			 result.add(new ResultSequence()); 
        		  }
        	   }
        	}
        }
        else if (m_arg1 instanceof XPathArray) {
        	// An XPath 3.1 function array:for-each's second argument, 
        	// uses xdm array as the mapping function.
        	
        	XPathArray xpathArrObj1 = (XPathArray)m_arg1;
        	
        	int size2 = xpathArrObj1.size();
        	
        	result = new XPathArray();
        	
        	XPathArray xpathArr0 = (XPathArray)xObjArg0;
        	int size1 = xpathArr0.size();
        	for (int idx = 0; idx < size1; idx++) {
        	   XObject xObj = xpathArr0.get(idx);
        	   if ((xObj instanceof XNumber) || (xObj instanceof XSNumericType)) {
        		  String str1 = XslTransformEvaluationHelper.getStrVal(xObj);
        		  try {
        		     int a1 = Integer.valueOf(str1);
        		     if ((a1 >= 1) && (a1 <= size2)) {
        		    	XObject xObj1 = xpathArrObj1.get(a1 - 1);
        		    	result.add(xObj1);
        		     }
        		     else {
        		    	result.add(new ResultSequence()); 
        		     }
        		  }
        		  catch (NumberFormatException ex) {
        			 result.add(new ResultSequence()); 
        		  }
        	   }
        	}
        }
        else {
        	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function array 'for-each', "
                    																				+ "second argument is not a function item.", srcLocator);               
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
  
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
      m_vars = (Vector)(vars.clone());
      m_globals_size = globalsSize; 
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
  
  /**
   * Method definition, to verify the number of function parameters, that 
   * an XPath 3.1 inline function is allowed to have for function array 'for-each'.
   */
  private void verifyInlineFunctionParamCardinality(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
                                                                                                javax.xml.transform.TransformerException {
      List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
      if (funcParamList.size() != 1) {
          throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 function array 'for-each' function item has " + 
                                                                                                     funcParamList.size() + " parameters. "
                                                                                                     + "Expected 1.", srcLocator);   
      }
  }
  
  /**
   * Method definition, to evaluate an XPath 3.1 function array:for-each 
   * for the supplied function arguments.
   * 
   * @param xpathArr					The supplied xdm array
   * @param funcItem					An xdm function item that is evaluated 
   *                                    for each xdm array member.
   * @param xctxt						An XPath context object
   * @return							An xdm array whose members are, 
   *                                    concatenation of function item's 
   *                                    evaluation results.
   * @throws TransformerException
   */
  private XPathArray evaluateArrayForEach(XPathArray xpathArr, XPathInlineFunction funcItem, XPathContext xctxt) 
                                                                                    throws TransformerException {
	    XPathArray result = new XPathArray();
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator();
        
        List<InlineFunctionParameter> funcParamList = funcItem.getFuncParamList();
        QName funcItemParamName = new QName((funcParamList.get(0)).getParamName());
        
        String funcBodyXPathExprStr = funcItem.getFuncBodyXPathExprStr();
        
        if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
           return result;
        }
        
        List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
        
        if (prefixTable != null) {
           funcBodyXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
        		                                                                                funcBodyXPathExprStr, prefixTable);
        }
        
        XPath inlineFnXpath = new XPath(funcBodyXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        
        verifyXPathInlineFuncContextItemAccess(inlineFnXpath.getExpression(), funcBodyXPathExprStr, srcLocator);
        
        XPathContext xpathContextNew = new XPathContext(false);
        Map<QName, XObject> inlineFunctionVarMap = xpathContextNew.getXPathVarMap();
        
        int size1 = xpathArr.size();
        
        for (int idx = 0; idx < size1; idx++) {
        	XObject funcItemParamValue = xpathArr.get(idx);
        	if (funcItemParamName != null) {
        	   inlineFunctionVarMap.put(funcItemParamName, funcItemParamValue);
        	}

        	XObject resultObj = inlineFnXpath.execute(xpathContextNew, DTM.NULL, null);
        	result.add(resultObj);
        }

        inlineFunctionVarMap.clear();
        
        return result;
   }

}
