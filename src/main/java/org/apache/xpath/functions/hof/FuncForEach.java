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
package org.apache.xpath.functions.hof;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of XPath 3.1 function fn:for-each.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncForEach extends Function2Args {

   private static final long serialVersionUID = 2912594883291006421L;
   
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
   * Evaluation of the function call. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {                      
      
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        final int contextNode = xctxt.getCurrentNode();                
        
        XObject xObjectArg = null;
        
        DTMCursorIterator dtmIterArg = null;                
        
        if (m_vars != null) {
           m_arg0.fixupVariables(m_vars, m_globals_size);
        }
                  
        if (m_arg0 instanceof LocPathIterator) {
            dtmIterArg = m_arg0.asIterator(xctxt, contextNode);               
        }
        else {            
            xObjectArg = m_arg0.execute(xctxt, contextNode);
        }
                    
        if (m_arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)m_arg1;
            
            verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
            
            if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
            	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
            }
            
            result = evaluateFnForEach(xctxt, xObjectArg, dtmIterArg, inlineFuncArg); 
        }
        else if (m_arg1 instanceof XPathNamedFunctionReference) {
            XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)m_arg1;
            
            if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
            	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
            }
            
            result = evaluateNamedFunctionReference(xObjectArg, dtmIterArg, namedFuncRef, xctxt);
        }
        else if (m_arg1 instanceof NodeTest) {
            TransformerImpl transformerImpl = getTransformerImplFromXPathExpression(m_arg1);
            
        	ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression(
        			                                                                                 (NodeTest)m_arg1, transformerImpl, srcLocator);
        	
        	if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
            	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
            }
        	
        	result = evaluateFnForEach(xObjectArg, dtmIterArg, elemFunction, transformerImpl, xctxt); 
        }
        else if (m_arg1 instanceof Variable) {
            if (m_vars != null) {
            	m_arg1.fixupVariables(m_vars, m_globals_size);
            }            
            
            XObject arg1VarValue = m_arg1.execute(xctxt);
            
            if (arg1VarValue instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1VarValue;
                
                verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
                
                if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
                	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
                }
                
                result = evaluateFnForEach(xctxt, xObjectArg, dtmIterArg, inlineFuncArg);   
            }
            else if (arg1VarValue instanceof XPathNamedFunctionReference) {
            	XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg1VarValue;
            	
            	if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
                	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
                }
            	
            	result = evaluateNamedFunctionReference(xObjectArg, dtmIterArg, namedFuncRef, xctxt);
            }
            else if (arg1VarValue instanceof XMLNodeCursorImpl) {
            	XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)arg1VarValue;            	
            	DTMCursorIterator dtmIter = xmlNodeCursorImpl.getContainedIter();
            	TransformerImpl transformerImpl = getTransformerImplFromXPathExpression((NodeTest)dtmIter);

            	ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression(
            			                                                                                (NodeTest)dtmIter, transformerImpl, srcLocator);

            	if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
            		xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
            	}

            	result = evaluateFnForEach(xObjectArg, dtmIterArg, elemFunction, transformerImpl, xctxt); 
            }
            else {
                throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function call fn:for-each, "
                                                                                               + "is not a function item.", srcLocator);    
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function call fn:for-each, "
                                                                                               + "is not a function item.", srcLocator);               
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
      m_vars = (Vector)(vars.clone());
      m_globals_size = globalsSize; 
  }
  
  /*
   * Verify the, number of function parameters, that the inline function is allowed to have for fn:for-each.
   */
  private void verifyInlineFunctionParamCardinality(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
                                                                                                javax.xml.transform.TransformerException {
      List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
      if (funcParamList.size() != 1) {
          throw new javax.xml.transform.TransformerException("XPTY0004 : The function fn:for-each's function item argument has " + 
					                                                                                             funcParamList.size() + " parameters. "
					                                                                                             + "Expected 1.", srcLocator);   
      }
  }
  
  /**
   * Evaluate the function call fn:for-each.
   */
  private ResultSequence evaluateFnForEach(XPathContext xctxt, XObject xObjectArg, 
                                           DTMCursorIterator dtmIterArg, XPathInlineFunction xpathInlineFuncArg) 
                                                                                                 throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        List<InlineFunctionParameter> funcParamList = xpathInlineFuncArg.getFuncParamList();
        QName varQname = new QName((funcParamList.get(0)).getParamName());
        
        String funcBodyXPathExprStr = xpathInlineFuncArg.getFuncBodyXPathExprStr();
        
        if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
           return resultSeq;
        }                        
        
        XPath inlineFnXpath = new XPath(funcBodyXPathExprStr, srcLocator, null, XPath.SELECT, null);
        
        if (xObjectArg instanceof ResultSequence) {
           XPathContext xpathContextNew = new XPathContext(false);
           Map<QName, XObject> inlineFunctionVarMap = xpathContextNew.getXPathVarMap();
        
           ResultSequence inpResultSeq = (ResultSequence)xObjectArg;
           for (int idx = 0; idx < inpResultSeq.size(); idx++) {
               XObject inpSeqItem = inpResultSeq.item(idx);
               if (varQname != null) {
                  inlineFunctionVarMap.put(varQname, inpSeqItem);
               }
               
               XObject resultObj = inlineFnXpath.execute(xpathContextNew, DTM.NULL, null);
               resultSeq.add(resultObj);
           }
        
           inlineFunctionVarMap.clear();
        }
        else if (dtmIterArg != null) {                  
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
            
           final int contextNode = xctxt.getCurrentNode();           
            
           int nextNode;
           
           while (DTM.NULL != (nextNode = dtmIterArg.nextNode())) {
               XMLNodeCursorImpl inpSeqItem = new XMLNodeCursorImpl(nextNode, xctxt.getDTMManager());
               if (varQname != null) {
                  inlineFunctionVarMap.put(varQname, inpSeqItem);
               }
               
               if (m_vars != null) {                  
                  if (!m_xpathVarList.contains(varQname)) {
                     m_xpathVarList.add(varQname);
                  }
                  inlineFnXpath.fixupVariables(m_vars, m_globals_size);
                  m_xpathVarList.remove(varQname);
               }
               
               XObject resultObj = inlineFnXpath.execute(xctxt, contextNode, null);
               
               resultSeq.add(resultObj);
           }
        
           inlineFunctionVarMap.clear();
        }
        
        return resultSeq;
   }
  
  /**
   * Evaluate XPath named function reference.
   */
  private ResultSequence evaluateNamedFunctionReference(XObject xObjectArg, DTMCursorIterator dtmIterArg, 
		                                                XPathNamedFunctionReference namedFuncRef, XPathContext xctxt) 
		                                                		                           throws TransformerException {	
	  ResultSequence resultSeq = null;
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  String funcNamespace = namedFuncRef.getFuncNamespace();
	  String funcLocalName = namedFuncRef.getFuncName();
	  int funcArity = namedFuncRef.getArity(); 

	  FunctionTable funcTable = xctxt.getFunctionTable();

	  Object funcIdObj = null;
	  if (FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionId(funcLocalName);
	  }
	  else if (FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMathFuncs(funcLocalName);
	  }
	  else if (FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMapFuncs(funcLocalName);
	  }
	  else if (FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI.equals(funcNamespace)) {
		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinArrayFuncs(funcLocalName);
	  }

	  if (funcIdObj != null) {
		  String funcIdStr = funcIdObj.toString();
		  Function function = funcTable.getFunction(Integer.valueOf(funcIdStr));               
		  try {
			  resultSeq = evaluateFnForEach(xObjectArg, dtmIterArg, function, xctxt);
		  } 
		  catch (WrongNumberArgsException ex) {
			  String expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + funcArity;  
			  throw new javax.xml.transform.TransformerException("XPTY0004 : Wrong number of arguments provided, "
					                                           										+ "during function call " 
					                                           										+ expandedFuncName + ".", srcLocator); 
		  }               
	  }

	  return resultSeq;
   }
   
   /**
    * Method definition to get the result of function call fn:for-each, when 
    * function call's second argument is specified as named function reference.
    */
   private ResultSequence evaluateFnForEach(XObject xObjectArg, DTMCursorIterator dtmIterArg, 
		                                    Function function, XPathContext xctxt) 
          								    		                      throws WrongNumberArgsException, TransformerException {
	 
	  ResultSequence resultSeq = new ResultSequence();
	  
	  if (xObjectArg instanceof ResultSequence) {
		 ResultSequence inpSeq = (ResultSequence)xObjectArg;
		 for (int idx = 0; idx < inpSeq.size(); idx++) {
			XObject xObj = inpSeq.item(idx);
			function.setArg(xObj, 0);
			XObject funcResult = function.execute(xctxt);
			resultSeq.add(funcResult);
		 }
	  }
	  else if (dtmIterArg != null) {          
		  int nextNode;
		  
          while (DTM.NULL != (nextNode = dtmIterArg.nextNode())) {
              XMLNodeCursorImpl inpSeqItem = new XMLNodeCursorImpl(nextNode, xctxt.getDTMManager());              
              function.setArg(inpSeqItem, 0);
  			  XObject funcResult = function.execute(xctxt);
  			  resultSeq.add(funcResult);
          }
	  }

	  return resultSeq;
   }
   
   /**
    * Method definition to get the result of function call fn:for-each, when 
    * function call's second argument is specified as an XSL stylesheet 
    * function reference.
    */
   private ResultSequence evaluateFnForEach(XObject xObjectArg, DTMCursorIterator dtmIterArg, 
		                                    ElemFunction elemFunction, TransformerImpl transformerImpl, 
		                                    XPathContext xctxt) throws TransformerException {
	   
	   ResultSequence resultSeq = new ResultSequence();

	   if (xObjectArg instanceof ResultSequence) {
		   ResultSequence inpSeq = (ResultSequence)xObjectArg;
		   for (int idx = 0; idx < inpSeq.size(); idx++) {
			   XObject xObj = inpSeq.item(idx);				
			   ResultSequence argSeq = new ResultSequence();
			   argSeq.add(xObj);				
			   XObject funcCallResult = elemFunction.evaluateXslFunction(transformerImpl, argSeq);
			   resultSeq.add(funcCallResult);
		   }
	   }
	   else if (dtmIterArg != null) {          
		   int nextNode;
		   
		   while (DTM.NULL != (nextNode = dtmIterArg.nextNode())) {
			   XMLNodeCursorImpl inpSeqItem = new XMLNodeCursorImpl(nextNode, xctxt.getDTMManager());              
			   ResultSequence argSeq = new ResultSequence();
			   argSeq.add(inpSeqItem);				
			   XObject funcCallResult = elemFunction.evaluateXslFunction(transformerImpl, argSeq);
			   resultSeq.add(funcCallResult);
		   }
	   }

	   return resultSeq;
   }
   
}
