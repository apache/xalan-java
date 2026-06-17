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
package org.apache.xpath.functions.array;

import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of the array:for-each-pair function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayForEachPair extends Function3Args {

	private static final long serialVersionUID = -8011606044596320698L;
	
	/**
	 * Class constructor.
	 */
	public FuncArrayForEachPair() {
		m_defined_arity = new Short[] { 3 };
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
        
        XObject arg0Obj = getFunctionArgEffectiveValue(m_arg0, xctxt);
        
        XObject arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
        
        if (!((arg0Obj instanceof XPathArray) || (arg1Obj instanceof XPathArray))) {
           throw new javax.xml.transform.TransformerException("FORG0006 : The function call array:for-each-pair's 1st and 2nd "
           		                                                     + "arguments need to be XPath arrays, or can be evaluated to arrays.", srcLocator);
        }
        
        XPathInlineFunction funcItem3rdArg = null;
        
        if (m_arg2 instanceof Variable) {
           XObject arg2XObj = getFunctionArgEffectiveValue(m_arg2, xctxt);
           
           if (arg2XObj instanceof XPathInlineFunction) {
              funcItem3rdArg = (XPathInlineFunction)arg2XObj;
           }
           else {
              QName varQname = (((Variable)m_arg2).getElemVariable()).getName();
              throw new javax.xml.transform.TransformerException("FORG0006 : The 3rd argument to function call array:for-each-pair "
              		                                                    + "is a variable reference '" + varQname.getLocalName() + "', that doesn't "
              		                                                    + "evaluate to a function item.", srcLocator);  
           }
        }        
        else if (m_arg2 instanceof XPathInlineFunction) {
           funcItem3rdArg = (XPathInlineFunction)m_arg2;                                           
        }
        else {
           throw new javax.xml.transform.TransformerException("FORG0006 : The 3rd argument to function call array:for-each-pair is not a function "
           		                                                     + "item, or cannot be evaluated to a function item.", srcLocator);
        }
        
        result = evaluateArrayForEachPairFunc((XPathArray)arg0Obj, (XPathArray)arg1Obj, funcItem3rdArg, xctxt);
        
        return result;
    }
    
    private XPathArray evaluateArrayForEachPairFunc(XPathArray arg0, XPathArray arg1, XPathInlineFunction funcItem3rdArg, XPathContext xctxt) 
                                                                                       throws javax.xml.transform.TransformerException {        
        XPathArray resultArr = new XPathArray();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        String inlineFnXPathStr = funcItem3rdArg.getFuncBodyXPathExprStr();
        List<InlineFunctionParameter> funcParamList = funcItem3rdArg.getFuncParamList();
        
        if (funcParamList.size() == 2) {                           
            String funcItemFirstArgName = (funcParamList.get(0)).getParamName();
            String funcItemSecondArgName = (funcParamList.get(1)).getParamName();
            
            List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
            
            if (prefixTable != null) {
               inlineFnXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                      inlineFnXPathStr, prefixTable);
            }
            
            XPath inlineFuncXPath = new XPath(inlineFnXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                            XPath.SELECT, null);
            
            // As per XPath 3.1 F&O spec, if the arrays have different size, excess members in the 
            // longer array are ignored.
            int effectiveInpArrSize = 0;        
            if (arg0.size() <= arg1.size()) {
               effectiveInpArrSize = arg0.size();    
            }
            else {
               effectiveInpArrSize = arg1.size(); 
            }
            
            for (int idx = 0; idx < effectiveInpArrSize; idx++) {
               Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
               
               inlineFunctionVarMap.put(new QName(funcItemFirstArgName), arg0.get(idx));
               inlineFunctionVarMap.put(new QName(funcItemSecondArgName), arg1.get(idx));
                
               XObject iterResultVal = inlineFuncXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
               resultArr.add(iterResultVal);
                
               // Reset the function item argument variables
               inlineFunctionVarMap.put(new QName(funcItemFirstArgName), null);
               inlineFunctionVarMap.put(new QName(funcItemSecondArgName), null);
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("XPTY0004 : An array:for-each-pair's function item 3rd argument, "
            		                                                             + "needs to have 2 function parameters.", srcLocator); 
        }
        
        return resultArr;
    }

}
