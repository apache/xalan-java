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
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the for-each-pair() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * fn:for-each-pair is one of XPath 3.1's higher-order function
 * (ref, https://www.w3.org/TR/xpath-functions-31/#higher-order-functions).
 * 
 * The XPath function fn:for-each-pair has following signature, and definition,
 * 
 * fn:for-each-pair($seq1 as item()*,
                    $seq2 as item()*,
                    $action as function(item(), item()) as item()*) as item()*
                    
 * The fn:for-each-pair function, applies the function item $action to successive 
 * pairs of items taken one from $seq1 and one from $seq2, returning the 
 * concatenation of the resulting sequences in order.
 */
public class FuncForEachPair extends XPathHigherOrderBuiltinFunctionsSupport {

    private static final long serialVersionUID = 5864311267789581972L;

    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject evalResult = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        ResultSequence forEachPairFirstArgInpSeq = constructXDMSequenceFromXPathExpression(m_arg0, xctxt);        
        ResultSequence forEachPairSecondArgInpSeq = constructXDMSequenceFromXPathExpression(m_arg1, xctxt);
        
        InlineFunction forEachPairThirdArg = null;
        
        if (m_arg2 instanceof Variable) {
           XObject arg2XObj = m_arg2.execute(xctxt);
           if (arg2XObj instanceof InlineFunction) {
              forEachPairThirdArg = (InlineFunction)arg2XObj;
           }
           else {
              QName varQname = (((Variable)m_arg2).getElemVariable()).getName();
              throw new javax.xml.transform.TransformerException("FORG0006 : The third argument to function call "
                                                                   + "fn:for-each-pair is a variable reference '" + 
                                                                       varQname.getLocalName() + "', that cannot be "
                                                                       + "evaluated to a function item.", srcLocator);  
           }
        }        
        else if (m_arg2 instanceof InlineFunction) {
           forEachPairThirdArg = (InlineFunction)m_arg2;                                           
        }
        else {
           throw new javax.xml.transform.TransformerException("FORG0006 : The third argument to function call "
                                                                    + "fn:for-each-pair is not a function item, or cannot be "
                                                                    + "coerced to a function item.", srcLocator);
        }
        
        evalResult = evaluateForEachPairInlineFunction(forEachPairThirdArg, xctxt, forEachPairFirstArgInpSeq, 
                                                                                                  forEachPairSecondArgInpSeq);
        
        return evalResult;
    }
    
    /**
     * This method produces the result data, for an XPath fn:for-each-pair function call.
     * 
     * @param inlineFunc     an inline function that needs to be evaluated
     * @param xctxt          the XPath expression context
     * @param inpSeq1        the first xdm input sequence argument to function fn:for-each-pair     
     * @param inpSeq2        the second xdm input sequence argument to function fn:for-each-pair
     * 
     * @return               the result of evaluation by this method, as an object of
     *                       class ResultSequence.    
     * 
     * @throws javax.xml.transform.TransformerException
     */
    private ResultSequence evaluateForEachPairInlineFunction(InlineFunction inlineFunc, XPathContext xctxt, 
                                                                    ResultSequence inpSeq1, ResultSequence inpSeq2) 
                                                                                       throws javax.xml.transform.TransformerException {        
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        String inlineFnXPathStr = inlineFunc.getFuncBodyXPathExprStr();
        List<InlineFunctionParameter> funcParamList = inlineFunc.getFuncParamList();
        
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
            
            int inpEffectiveIterationSize = 0;        
            if (inpSeq1.size() <= inpSeq2.size()) {
               inpEffectiveIterationSize = inpSeq1.size();    
            }
            else {
               inpEffectiveIterationSize = inpSeq2.size(); 
            }
            
            for (int idx = 0; idx < inpEffectiveIterationSize; idx++) {
               Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
               
               inlineFunctionVarMap.put(new QName(funcItemFirstArgName), inpSeq1.item(idx));
               inlineFunctionVarMap.put(new QName(funcItemSecondArgName), inpSeq2.item(idx));
                
               XObject iterResultVal = inlineFuncXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
               resultSeq.add(iterResultVal);
                
               // Reset the function item argument variables
               inlineFunctionVarMap.put(new QName(funcItemFirstArgName), null);
               inlineFunctionVarMap.put(new QName(funcItemSecondArgName), null);
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("XPTY0004 : An inline function definition argument to "
                                                                     + "function fn:for-each-pair has " + funcParamList.size() + " "
                                                                     + "parameters. Expected 2.", srcLocator); 
        }
        
        return resultSeq;
    }

}
