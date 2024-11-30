/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of an XPath 3.1 function, fn:for-each.
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
   * Implementation of the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {                      
      
        XObject funcEvaluationResult = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        final int contextNode = xctxt.getCurrentNode();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();                
        
        DTMIterator arg0DtmIterator = null;        
        XObject arg0XsObject = null;
        
        if (m_vars != null) {
           arg0.fixupVariables(m_vars, m_globals_size);
        }
                  
        if (arg0 instanceof LocPathIterator) {
            arg0DtmIterator = arg0.asIterator(xctxt, contextNode);               
        }
        else {            
            arg0XsObject = arg0.execute(xctxt, contextNode);
        }
        
        ResultSequence resultSeq = new ResultSequence();
                    
        if (arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1;
            verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
            resultSeq = evaluateFnForEach(xctxt, arg0XsObject, arg0DtmIterator, inlineFuncArg); 
        }
        else if (arg1 instanceof XPathNamedFunctionReference) {
            XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg1;
            resultSeq = evaluateNamedFunctionReference(xctxt, srcLocator, arg0DtmIterator, arg0XsObject,
            		                                   resultSeq, namedFuncRef);
        }
        else if (arg1 instanceof Variable) {
            if (m_vars != null) {
               arg1.fixupVariables(m_vars, m_globals_size);
            }            
            XObject arg1VarValue = arg1.execute(xctxt);
            if (arg1VarValue instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1VarValue;
                verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
                resultSeq = evaluateFnForEach(xctxt, arg0XsObject, arg0DtmIterator, inlineFuncArg);   
            }
            else if (arg1VarValue instanceof XPathNamedFunctionReference) {
            	XPathNamedFunctionReference namedFuncRef = (XPathNamedFunctionReference)arg1VarValue;
                resultSeq = evaluateNamedFunctionReference(xctxt, srcLocator, arg0DtmIterator, arg0XsObject,
                		                                   resultSeq, namedFuncRef);
            }
            else {
                throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument to function call for-each(), "
                                                                                               + "is not a function item.", xctxt.getSAXLocator());    
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument to function call for-each(), "
                                                                                           + "is not a function item.", xctxt.getSAXLocator());               
        }            
        
        funcEvaluationResult = resultSeq;
        
        return funcEvaluationResult;
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
          throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied function fn:for-each's function item has " + 
                                                                                             funcParamList.size() + " parameters. "
                                                                                             + "Expected 1.", srcLocator);   
      }
  }
  
  /**
   * Evaluate the function call fn:for-each.
   */
  private ResultSequence evaluateFnForEach(XPathContext xctxt, XObject arg0XsObject, 
                                               DTMIterator dtmIter, XPathInlineFunction arg1) 
                                                                                    throws TransformerException {
        ResultSequence resultSeq = new ResultSequence(); 
        
        List<InlineFunctionParameter> funcParamList = arg1.getFuncParamList();
        QName varQname = new QName((funcParamList.get(0)).getParamName());
        
        String funcBodyXPathExprStr = arg1.getFuncBodyXPathExprStr();
        
        if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
           return resultSeq;
        }        
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        XPath inlineFnXpath = new XPath(funcBodyXPathExprStr, srcLocator, null, XPath.SELECT, null);
        
        if (arg0XsObject instanceof ResultSequence) {
           XPathContext xpathContextNew = new XPathContext(false);
           Map<QName, XObject> inlineFunctionVarMap = xpathContextNew.getXPathVarMap();
        
           ResultSequence inpResultSeq = (ResultSequence)arg0XsObject;
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
        else if (dtmIter != null) {                  
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
            
           final int contextNode = xctxt.getCurrentNode();           
            
           int dtmNodeHandle;
            
           while (DTM.NULL != (dtmNodeHandle = dtmIter.nextNode())) {
               XNodeSet inpSeqItem = new XNodeSet(dtmNodeHandle, xctxt.getDTMManager());
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
  private ResultSequence evaluateNamedFunctionReference(XPathContext xctxt, SourceLocator srcLocator,
		                                                DTMIterator dtmIter, XObject arg0XsObject, 
		                                                ResultSequence resultSeq, XPathNamedFunctionReference namedFuncRef) 
		                                                		                         throws TransformerException {	
	  String funcNamespace = namedFuncRef.getFuncNamespace();
	  String funcLocalName = namedFuncRef.getFuncName();
	  int funcArity = namedFuncRef.getFuncArity(); 

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
			  resultSeq = evaluateFnForEach(arg0XsObject, xctxt, dtmIter, function);
		  } 
		  catch (WrongNumberArgsException ex) {
			  String expandedFuncName = "{" + funcNamespace + ":" + funcLocalName + "}#" + funcArity;  
			  throw new javax.xml.transform.TransformerException("XPTY0004 : Wrong number of arguments provided, "
					                                           + "during function call " + expandedFuncName + ".", srcLocator); 
		  }               
	  }

	  return resultSeq;
  }
   
   /**
    * Get the result of function call fn:for-each, with function call's 2nd argument 
    * specified as named function reference.
    */
   private ResultSequence evaluateFnForEach(XObject forEachSeq, XPathContext xctxt,
          								    DTMIterator dtmIter, Function function) 
          								    		               throws WrongNumberArgsException, TransformerException {
	  ResultSequence resultSeq = new ResultSequence();
	  
	  if (forEachSeq instanceof ResultSequence) {
		 ResultSequence inpSeq = (ResultSequence)forEachSeq;
		 for (int idx = 0; idx < inpSeq.size(); idx++) {
			XObject xObj = inpSeq.item(idx);
			function.setArg(xObj, 0);
			XObject funcResult = function.execute(xctxt);
			resultSeq.add(funcResult);
		 }
	  }
	  else if (dtmIter != null) {          
		  int dtmNodeHandle;          
          while (DTM.NULL != (dtmNodeHandle = dtmIter.nextNode())) {
              XNodeSet inpSeqItem = new XNodeSet(dtmNodeHandle, xctxt.getDTMManager());              
              function.setArg(inpSeqItem, 0);
  			  XObject funcResult = function.execute(xctxt);
  			  resultSeq.add(funcResult);
          }
	  }

	  return resultSeq;
   }

}
