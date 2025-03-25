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

import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the fold-right() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * fn:fold-right is one of XPath 3.1's higher-order function
 * (ref, https://www.w3.org/TR/xpath-functions-31/#higher-order-functions).
 * 
 * The XPath function fn:fold-right has following signature, and definition,
 * 
 * fn:fold-right($seq as item()*,
                 $zero as item()*,
                 $f as function(item(), item()*) as item()*) as item()*

 * The fn:fold-right function, processes the supplied sequence from right to left, 
 * applying the supplied function repeatedly to each item in turn, together with
 * an accumulated result value.
 */
public class FuncFoldRight extends XPathHigherOrderBuiltinFunction {
    
    private static final long serialVersionUID = 4675724832355053777L;

    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ResultSequence foldRightFirstArgResultSeq = constructXDMSequenceFromXPathExpression(m_arg0, xctxt);
        
        XObject foldRightBaseVal = m_arg1.execute(xctxt);
        
        XPathInlineFunction foldRightThirdArg = null;
        
        if (m_arg2 instanceof Variable) {
           XObject arg2XObj = m_arg2.execute(xctxt);
           if (arg2XObj instanceof XPathInlineFunction) {
              foldRightThirdArg = (XPathInlineFunction)arg2XObj;
           }
           else {
              QName varQname = (((Variable)m_arg2).getElemVariable()).getName();
              throw new javax.xml.transform.TransformerException("FORG0006 : The third argument to function call "
                                                                         + "fn:fold-right is a variable reference '" + 
                                                                            varQname.getLocalName() + "', that cannot be "
                                                                         + "evaluated to a function item.", srcLocator);  
           }
        }        
        else if (m_arg2 instanceof XPathInlineFunction) {
           foldRightThirdArg = (XPathInlineFunction)m_arg2;                                           
        }
        else {
           throw new javax.xml.transform.TransformerException("FORG0006 : The third argument to function call "
                                                                      + "fn:fold-right is not a function item, or cannot be "
                                                                      + "coerced to a function item.", srcLocator);
        }
                   
        String inlineFnXPathStr = foldRightThirdArg.getFuncBodyXPathExprStr();
        List<InlineFunctionParameter> funcParamList = foldRightThirdArg.getFuncParamList();
           
        if (funcParamList.size() == 2) {              
           String funcItemFirstArgName = (funcParamList.get(0)).getParamName();
           String funcItemSecondArgName = (funcParamList.get(1)).getParamName();
              
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
           List<XMLNSDecl> prefixTable = null;
           if (elemTemplateElement != null) {
              prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           }
              
           if (prefixTable != null) {
              inlineFnXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                           inlineFnXPathStr, prefixTable);
           }
              
           XPath inlineFuncXPath = new XPath(inlineFnXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                              XPath.SELECT, null);              
           for (int idx = foldRightFirstArgResultSeq.size() - 1; idx >= 0; idx--) {
              Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
                 
              inlineFunctionVarMap.put(new QName(funcItemFirstArgName), foldRightFirstArgResultSeq.item(idx));
              
              if (idx == (foldRightFirstArgResultSeq.size() - 1)) {                    
                    inlineFunctionVarMap.put(new QName(funcItemSecondArgName), foldRightBaseVal);
              }
              else {
                    inlineFunctionVarMap.put(new QName(funcItemSecondArgName), result);                   
              }
                 
              result = inlineFuncXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                                  
              // Reset the function item argument variables
              inlineFunctionVarMap.put(new QName(funcItemFirstArgName), null);
              inlineFunctionVarMap.put(new QName(funcItemSecondArgName), null);
           }
        }
        else {
            throw new javax.xml.transform.TransformerException("XPTY0004 : An inline function definition argument to "
                                                                                            + "function fn:fold-right has " + funcParamList.size() + " "
                                                                                            + "parameters. Expected 2.", srcLocator); 
        }
        
        return result;
    }

}
