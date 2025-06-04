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
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of an XPath 3.1 function fn:filter.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFilter extends Function2Args {

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
   * Execute the function. The function must return a valid object.
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
        
        XObject xObjectArg = null;
        
        DTMCursorIterator dtmIterArg = null;
                  
        if (m_arg0 instanceof LocPathIterator) {
            dtmIterArg = m_arg0.asIterator(xctxt, contextNode);               
        }
        else {
            xObjectArg = m_arg0.execute(xctxt, contextNode);
        }
        
        ResultSequence resultSeq = new ResultSequence();
                    
        if (m_arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)m_arg1;
            
            validateInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
            
            if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
            	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
            }
            
            resultSeq = evaluateFnFilter(xctxt, xObjectArg, dtmIterArg, inlineFuncArg); 
        }                
        else if (m_arg1 instanceof NodeTest) {
        	TransformerImpl transformerImpl = getTransformerImplFromXPathExpression(m_arg1);
            
        	ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression((NodeTest)m_arg1, transformerImpl, srcLocator);
            
        	if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
            	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
            }
        	
        	resultSeq = evaluateFnFilter(xctxt, xObjectArg, dtmIterArg, elemFunction, transformerImpl);            
        }
        else if (m_arg1 instanceof Variable) {
            XObject arg1VarValue = m_arg1.execute(xctxt);
            
            if (arg1VarValue instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1VarValue;
                
                validateInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
                
                if (xObjectArg != null && !(xObjectArg instanceof ResultSequence)) {
                	xObjectArg = castSingletonItemToResultSequence(xObjectArg); 
                }
                
                resultSeq = evaluateFnFilter(xctxt, xObjectArg, dtmIterArg, inlineFuncArg);   
            }
            else {
                throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function call fn:filter, "
                                                                                               + "is not a function item.", srcLocator);    
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function call fn:filter, "
                                                                                               + "is not a function item.", srcLocator);               
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
   * Validate the, number of function parameters, that the inline function is allowed to have for fn:filter.
   */
  private void validateInlineFunctionParamCardinality(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
                                                                                                javax.xml.transform.TransformerException {
      List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
      if (funcParamList.size() != 1) {
          throw new javax.xml.transform.TransformerException("XPTY0004 : The function fn:filter is called with a function item argument having " + 
                                                                                                      funcParamList.size() + " parameter(s). "
                                                                                                      + "Expected 1.", srcLocator);   
      }
  }
  
  /**
   * Method definition to evaluate the function call fn:filter, when its function item 
   * argument was compiled from an XPath inline function expression. One of the argument 
   * xsObject or dtmIterator will be null while the other will be non-null, depending
   * upon whether an input sequence was constructed from a sequence of non XML values
   * or from XML nodes. 
   */
  private ResultSequence evaluateFnFilter(XPathContext xctxt, XObject xsObject, DTMCursorIterator dtmIterator, 
		                                                      XPathInlineFunction xpathInlineFunction) throws TransformerException {
	  
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        List<InlineFunctionParameter> funcParamList = xpathInlineFunction.getFuncParamList();
        QName varQname = new QName((funcParamList.get(0)).getParamName());
        
        String funcBodyXPathExprStr = xpathInlineFunction.getFuncBodyXPathExprStr();
        
        if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
           return resultSeq;
        }                
        
        PrefixResolver prefixResolver = xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (prefixResolver instanceof ElemTemplateElement) {
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)prefixResolver;
           prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           if (prefixTable != null) {
        	  funcBodyXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
        			                                                                            funcBodyXPathExprStr, 
                                                                                                prefixTable);
           }
        }
        
        XPath inlineFnXpath = new XPath(funcBodyXPathExprStr, srcLocator, prefixResolver, XPath.SELECT, null);
        
        if (xsObject instanceof ResultSequence) {
           XPathContext xpathContextNew = new XPathContext(false);
           Map<QName, XObject> inlineFunctionVarMap = xpathContextNew.getXPathVarMap();
        
           ResultSequence inpResultSeq = (ResultSequence)xsObject;
           for (int idx = 0; idx < inpResultSeq.size(); idx++) {
               XObject inpSeqItem = inpResultSeq.item(idx);
               if (varQname != null) {
                  inlineFunctionVarMap.put(varQname, inpSeqItem);
               }
        
               XObject funcEvalResult = inlineFnXpath.execute(xpathContextNew, DTM.NULL, null);
               if ((funcEvalResult instanceof XBoolean) || (funcEvalResult instanceof XBooleanStatic) 
                                                        || (funcEvalResult instanceof XSBoolean)) {
                   if (funcEvalResult.bool()) {
                      resultSeq.add(inpSeqItem);
                   }
               }
               else {
                   throw new javax.xml.transform.TransformerException("XPTY0004 : The function fn:filter's function item argument "
                   		                                                                      + "when evaluated didn't produce a xs:boolean result.", srcLocator); 
               }
           }
        
           inlineFunctionVarMap.clear();
        }
        else if (dtmIterator != null) {
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
        
           final int contextNode = xctxt.getCurrentNode();           
           
           int nextNode;
           
           while (DTM.NULL != (nextNode = dtmIterator.nextNode())) {               
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
               
               XObject funcEvalResult = inlineFnXpath.execute(xctxt, contextNode, null);
               if ((funcEvalResult instanceof XBoolean) || (funcEvalResult instanceof XBooleanStatic) 
                                                        || (funcEvalResult instanceof XSBoolean)) {
                   if (funcEvalResult.bool()) {                       
                       resultSeq.add(inpSeqItem);
                   }
               }
               else {
            	   throw new javax.xml.transform.TransformerException("XPTY0004 : The function fn:filter's function item argument "
                                                                                               + "when evaluated didn't produce a xs:boolean result.", srcLocator);  
               }
           }
        
           inlineFunctionVarMap.clear();
        }
        
        return resultSeq;
   }
  
  /**
   * Method definition to evaluate the function call fn:filter, when its function item 
   * argument was compiled from an XSL xsl:function definition. One of the argument 
   * xsObject or dtmIterator will be null while the other will be non-null, depending
   * upon whether an input sequence was constructed from a sequence of non XML values
   * or from XML nodes. 
   */
  private ResultSequence evaluateFnFilter(XPathContext xctxt, XObject xsObject, DTMCursorIterator dtmIterator,
		  								  ElemFunction elemFunction, TransformerImpl transformerImpl) throws TransformerException {
	  
	  ResultSequence resultSeq = new ResultSequence();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  if (xsObject instanceof ResultSequence) {
		  ResultSequence inpResultSeq = (ResultSequence)xsObject;
          for (int idx = 0; idx < inpResultSeq.size(); idx++) {
        	  XObject inpSeqItem = inpResultSeq.item(idx);
        	  ResultSequence argSeq = new ResultSequence();
        	  argSeq.add(inpSeqItem);
        	  XObject funcEvalResult = elemFunction.evaluateXslFunction(transformerImpl, argSeq);
        	  if ((funcEvalResult instanceof XBoolean) || (funcEvalResult instanceof XBooleanStatic) 
        			                                   || (funcEvalResult instanceof XSBoolean)) {
                  if (funcEvalResult.bool()) {                       
                      resultSeq.add(inpSeqItem);
                  }
              }
              else {
            	  throw new javax.xml.transform.TransformerException("XPTY0004 : The function fn:filter's function item argument "
                                                                                                         + "when evaluated didn't produce a xs:boolean result.", srcLocator);  
              }
          }
	  }
	  else {		  
		  int nextNode;
		  
          while (DTM.NULL != (nextNode = dtmIterator.nextNode())) {               
              XMLNodeCursorImpl inpSeqItem = new XMLNodeCursorImpl(nextNode, xctxt.getDTMManager());
              ResultSequence argSeq = new ResultSequence();
        	  argSeq.add(inpSeqItem);
        	  XObject funcEvalResult = elemFunction.evaluateXslFunction(transformerImpl, argSeq);
        	  if ((funcEvalResult instanceof XBoolean) || (funcEvalResult instanceof XBooleanStatic) 
        			                                   || (funcEvalResult instanceof XSBoolean)) {
                  if (funcEvalResult.bool()) {                       
                      resultSeq.add(inpSeqItem);
                  }
              }
              else {
            	  throw new javax.xml.transform.TransformerException("XPTY0004 : The function fn:filter's function item argument "
                                                                                                         + "when evaluated didn't produce a xs:boolean result.", srcLocator);  
              }
          }		  
	  }
	  
	  return resultSeq;
  }

}
