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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
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
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of an XPath 3.1 function array:filter.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayFilter extends Function2Args {
   
	private static final long serialVersionUID = -7341393974878279742L;

	/**
	 * Class constructor.
	 */
	public FuncArrayFilter() {
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
        
        XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
        
        XObject xObj1 = getFunctionArgEffectiveValue(m_arg1, xctxt);
        
        if (!(xObj0 instanceof XPathArray)) {
           throw new TransformerException("XPTY0004 : An XPath 3.1 function array 'filter' first "
           		                                                                              + "argument is not an xdm array.", srcLocator);
        }
                    
        if (m_arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)m_arg1;
            
            verifyInlineFunctionParamArity(inlineFuncArg, srcLocator);
            
            result = evaluateFnArrayFilter((XPathArray)xObj0, inlineFuncArg, xctxt); 
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
				
        		XPathArray xpathArr = (XPathArray)xObj0;
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
        				
        				if ((funcEvalResult instanceof XSBoolean) || (funcEvalResult instanceof XBoolean) || 
        						                                     (funcEvalResult instanceof XBooleanStatic)) {
        					if (funcEvalResult.bool()) {
        					   result.add(xObj);
        					}
        				}
        				else {
        					throw new TransformerException("XPTY0004 : An xdm item type of result of evaluation of function "
        							                                                                                + "item, second argument of function "
        							                                                                                + "array 'filter' if not boolean.", srcLocator);
        				}
        			}
        			else if (function instanceof Function2Args) {
        				if (b1) {
        					function2Args.setArg(xObj, 0);
        				}

        				if (b2) {
        					function2Args.setArg(xObj, 1);
        				}
        				
                        XObject funcEvalResult = function2Args.execute(xctxt);        				
        				
                        if ((funcEvalResult instanceof XSBoolean) || (funcEvalResult instanceof XBoolean) || 
                        		                                     (funcEvalResult instanceof XBooleanStatic)) {
                        	if (funcEvalResult.bool()) {
                        		result.add(xObj);
                        	}
                        }
                        else {
                        	throw new TransformerException("XPTY0004 : An xdm item type of result of evaluation of function "
																				                        			+ "item, second argument of function "
																				                        			+ "array 'filter' if not boolean.", srcLocator);
                        }
        			}
        			else if (function instanceof FunctionOneArg) {
        				if (b1) {
        					functionOneArg.setArg(xObj, 0);
        				}
        				
                        XObject funcEvalResult = functionOneArg.execute(xctxt);        				
        				
                        if ((funcEvalResult instanceof XSBoolean) || (funcEvalResult instanceof XBoolean) || 
                        											 (funcEvalResult instanceof XBooleanStatic)) {
                        	if (funcEvalResult.bool()) {
                        		result.add(xObj);
                        	}
                        }
                        else {
                        	throw new TransformerException("XPTY0004 : An xdm item type of result of evaluation of function "
																				                        			+ "item, second argument of function "
																				                        			+ "array 'filter' if not boolean.", srcLocator);
                        }
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
        	XPathNamedFunctionReference xpathNamedFunctionRef = (XPathNamedFunctionReference)m_arg1;
        	String funcName = xpathNamedFunctionRef.getFuncName();
        	short arity = xpathNamedFunctionRef.getArity();
        	if (arity != 1) {
        	   throw new TransformerException("XPTY0004 : The supplied function '" + funcName + "' has " + arity + " parameters. A "
        	   		                                                                          + "function with one parameter is expected.", 
        	   		                                                                                                                 srcLocator);
        	}
        }
        else if (xObj1 instanceof XPathMap) {
        	// An XPath 3.1 function array:filter, using a map as the predicate
        	
        	XPathMap xpathMap = (XPathMap)xObj1;        	
        	XPathArray arg0Arr =(XPathArray)xObj0;
        	
        	result= filterArrayOne(arg0Arr, xpathMap);        	
        }
        else if (m_arg1 instanceof XPathMapConstructor) {
        	// An XPath 3.1 function array:filter, using a map as the predicate
        	
        	XPathMapConstructor xpathMapConstructor = (XPathMapConstructor)m_arg1;
        	XPathMap xpathMap = (XPathMap)(xpathMapConstructor.execute(xctxt));        	
        	XPathArray arg0Arr =(XPathArray)xObj0;
        	
        	result = filterArrayOne(arg0Arr, xpathMap);
        }
        else if (m_arg1 instanceof XPathArrayConstructor) {
        	// An XPath 3.1 function array:filter, using an array as the predicate
        	
            result = new XPathArray();
        	
            XPathArrayConstructor xpathArrConstructor = (XPathArrayConstructor)m_arg1;
        	XPathArray xpathArr = (XPathArray)(xpathArrConstructor.execute(xctxt));
        	
        	XPathArray arg0Arr =(XPathArray)xObj0;
        	
        	result = filterArrayTwo(arg0Arr, xpathArr);        	
        }
        else if (xObj1 instanceof XPathArray) {
        	// An XPath 3.1 function array:filter, using an array as the predicate
        	
        	result = new XPathArray();
        	
        	XPathArray xpathArr = (XPathArray)xObj1; 
        	XPathArray arg0Arr =(XPathArray)xObj0;
        	
        	result = filterArrayTwo(arg0Arr, xpathArr);
        }
        else if (m_arg1 instanceof Variable) {            
            if (xObj1 instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)xObj1;
                verifyInlineFunctionParamArity(inlineFuncArg, srcLocator);
                
                result = evaluateFnArrayFilter((XPathArray)xObj0, inlineFuncArg, xctxt);   
            }            
            else {
                throw new TransformerException("XPTY0004 : An XPath 3.1 function array 'filter', second argument is not a function item.", srcLocator);    
            }
        }
        else {
        	throw new TransformerException("XPTY0004 : An XPath 3.1 function array 'filter', second argument is not a function item.", srcLocator);               
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
   * Method definition, to verify the, number of function parameters, that 
   * an inline function argument is allowed to have for XPath 3.1 array 'filter' 
   * function call.
   */
  private void verifyInlineFunctionParamArity(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
                                                                                                      javax.xml.transform.TransformerException {
      List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
      if (funcParamList.size() != 1) {
          throw new TransformerException("XPTY0004 : An XPath 3.1 function array 'filter' function item argument has " + 
                                                                                                      funcParamList.size() + " parameters. "
                                                                                                      + "Expected one.", srcLocator);   
      }
  }
  
  /**
   * Method definition, to evaluate an XPath 3.1 function 
   * array 'filter', provided with the supplied arguments.
   * 
   * @param xpathArr					The supplied xdm array
   * @param funcItem					An xdm function item that is evaluated for 
   *                                    each member of array.
   * @param xctxt						An XPath context object
   * @return							An xdm array whose members are, those input 
   *                                    array items for which, function item evaluates 
   *                                    to xs:boolean true.
   * @throws TransformerException
   */
  private XPathArray evaluateFnArrayFilter(XPathArray xpathArr, XPathInlineFunction funcItem, XPathContext xctxt) 
                                                                                                             throws TransformerException {
	  XPathArray result = new XPathArray();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  final int sourceNode = xctxt.getCurrentNode();

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

	  Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();

	  try {
		  int size1 = xpathArr.size();

		  for (int idx = 0; idx < size1; idx++) {
			  XObject inpItem = xpathArr.get(idx);
			  if (funcItemParamName != null) {
				  inlineFunctionVarMap.put(funcItemParamName, inpItem);
			  }

			  XObject resultObj = inlineFnXpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			  
			  if ((resultObj instanceof XSBoolean) || (resultObj instanceof XBoolean) || (resultObj instanceof XBooleanStatic)) {
				  if (resultObj.bool()) {
					  result.add(inpItem);
				  }
			  }
			  else {
				  throw new TransformerException("XPTY0004 : While evaluating an XPath 3.1 function array 'filter', for one of "
																										  + "an array member, array 'filter' "
																										  + "funtion item argument didn't evaluate to a boolean result.", 
																										  srcLocator); 
			  }
		  }
      }
      finally {   
	     inlineFunctionVarMap.clear();
      }

	  return result;
   }
  
  /**
   * Method definition, to filter an xdm array, using 
   * xdm map as predicate.
   * 
   * @param arg0Arr                     The supplied xdm array to be 
   *                                    filtered.
   * @param xpathMap                    An xdm map specifying filtering, 
   *                                    for an xdm array.
   * @return                            An xdm array result after filtering
   * @throws TransformerException
   */
   private XPathArray filterArrayOne(XPathArray arg0Arr, XPathMap xpathMap) throws TransformerException {
		
	   XPathArray result = new XPathArray();

	   int size1 = arg0Arr.size();
	   for (int idx = 0; idx < size1; idx++) {
		   XObject arrItem = arg0Arr.get(idx);
		   XObject mapEntryValue = xpathMap.get(arrItem);
		   if ((mapEntryValue instanceof XSBoolean) || (mapEntryValue instanceof XBoolean) || 
				                                       (mapEntryValue instanceof XBooleanStatic)) {
			   if (mapEntryValue.bool()) {
				   result.add(arrItem); 
			   }
		   }
	   }

	   return result;
   }
   
   /**
    * Method definition, to filter an xdm array, using 
    * xdm array as predicate.
    * 
    * @param arg0Arr                     The supplied xdm array to be 
    *                                    filtered.
    * @param xpathArr                    An xdm array specifying filtering, 
    *                                    for another xdm array.
    * @return                            An xdm array result after filtering
    * @throws TransformerException
    */
   private XPathArray filterArrayTwo(XPathArray arg0Arr, XPathArray xpathArr)
			                                                                throws TransformerException {
	    
	   XPathArray result = new XPathArray();	   
	   
	   boolean isFnArrFilterResultOk = true;
	   
	   int size1 = arg0Arr.size();
	   for (int idx = 0; idx < size1; idx++) {
		   XObject arrArg0Item = arg0Arr.get(idx);
		   if (arrArg0Item instanceof XSInteger) {
			   BigInteger bigInt = ((XSInteger)arrArg0Item).intValue();
			   int arrIndex = bigInt.intValue();
			   if (arrIndex <= 0) {
				   isFnArrFilterResultOk = false;

				   break;	
			   }
			   else {
				   XObject xObj = xpathArr.get(arrIndex - 1);
				   if ((xObj instanceof XSBoolean) || (xObj instanceof XBoolean) 
						                           || (xObj instanceof XBooleanStatic)) {
					   if (xObj.bool()) {
						   result.add(arrArg0Item); 
					   }
				   }
			   }
		   }
	   }

	   if (!isFnArrFilterResultOk) {
		   result = new XPathArray();
	   }

	   return result;
	}

}
