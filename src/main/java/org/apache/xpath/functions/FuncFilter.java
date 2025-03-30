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
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Execute the filter() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * fn:filter is one of XPath 3.1's higher-order function
 * (ref, https://www.w3.org/TR/xpath-functions-31/#higher-order-functions).
 * 
 * The XPath function fn:filter has following signature, and definition,
 * 
 * fn:filter($seq as item()*, $f as function(item()) as xs:boolean) as item()*
 * 
 * The function fn:filter returns those items from the sequence $seq for which the 
 * supplied function $f returns true.
 * 
 * Error conditions,
   As a consequence of the function signature and the function calling rules, a type 
   error occurs if the supplied function $f returns anything other than a single 
   xs:boolean item. There is no conversion to an effective boolean value.
 */
public class FuncFilter extends Function2Args {

   private static final long serialVersionUID = 2912594883291006421L;
   
   private static final String FUNCTION_NAME = "filter()";
   
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
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();                
        
        DTMCursorIterator arg0DtmIterator = null;
        
        XObject arg0XsObject = null;
                  
        if (arg0 instanceof LocPathIterator) {
            arg0DtmIterator = arg0.asIterator(xctxt, contextNode);               
        }
        else {
            arg0XsObject = arg0.execute(xctxt, contextNode);
        }
        
        ResultSequence resultSeq = new ResultSequence();
                    
        if (arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1;
            validateInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
            resultSeq = evaluateFnFilter(xctxt, arg0XsObject, arg0DtmIterator, inlineFuncArg); 
        }
        else if (arg1 instanceof Variable) {
            XObject arg1VarValue = arg1.execute(xctxt);
            if (arg1VarValue instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1VarValue;
                validateInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
                resultSeq = evaluateFnFilter(xctxt, arg0XsObject, arg0DtmIterator, inlineFuncArg);   
            }
            else {
                throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function call filter(), "
                                                                                               + "is not a function item.", xctxt.getSAXLocator());    
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("FORG0006 : The second argument to function call filter(), "
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
   * Validate the, number of function parameters, that the inline function is allowed to have for fn:filter.
   */
  private void validateInlineFunctionParamCardinality(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
                                                                                                javax.xml.transform.TransformerException {
      List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
      if (funcParamList.size() != 1) {
          throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied function fn:filter's function item has " + 
                                                                                                      funcParamList.size() + " parameters. "
                                                                                                      + "Expected 1.", srcLocator);   
      }
  }
  
  /*
   * Evaluate the function call fn:filter.
   */
  private ResultSequence evaluateFnFilter(XPathContext xctxt, XObject arg0XsObject, 
                                                 DTMCursorIterator arg0DtmIterator, XPathInlineFunction arg1) 
                                                                                    throws TransformerException {
        ResultSequence resultSeq = new ResultSequence(); 
        
        List<InlineFunctionParameter> funcParamList = arg1.getFuncParamList();
        QName varQname = new QName((funcParamList.get(0)).getParamName());
        
        String funcBodyXPathExprStr = arg1.getFuncBodyXPathExprStr();
        
        if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
           return resultSeq;
        }                
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
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
               if (resultObj instanceof XBoolean) {
                   if (((XBoolean)resultObj).bool()) {
                      resultSeq.add(inpSeqItem);
                   }
               }
               else {
                   throw new javax.xml.transform.TransformerException("XPTY0004 : The item type of the result of calling "
                                                                                              + "function filter() is not xs:boolean.", xctxt.getSAXLocator()); 
               }
           }
        
           inlineFunctionVarMap.clear();
        }
        else if (arg0DtmIterator != null) {
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
        
           final int contextNode = xctxt.getCurrentNode();           
           
           int dtmNodeHandle;
           
           while (DTM.NULL != (dtmNodeHandle = arg0DtmIterator.nextNode())) {               
               XMLNodeCursorImpl inpSeqItem = new XMLNodeCursorImpl(dtmNodeHandle, xctxt.getDTMManager());
               
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
               if (resultObj instanceof XBoolean) {
                   if (((XBoolean)resultObj).bool()) {                       
                       resultSeq.add(inpSeqItem);
                   }
               }
               else {
                   throw new javax.xml.transform.TransformerException("XPTY0004 : The item type of the result of calling "
                                                                                              + "function filter() is not xs:boolean.", xctxt.getSAXLocator());  
               }
           }
        
           inlineFunctionVarMap.clear();
        }
        
        return resultSeq;
   }

}
