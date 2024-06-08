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

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.operations.Variable;

/**
 * Implementation of XPath 3.1 function, array:fold-right.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayFoldRight extends Function3Args {

	private static final long serialVersionUID = 2535975872380722894L;

	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        Expression arg2 = getArg2();
        
        int contextNode = xctxt.getContextNode();
        
        XPathArray foldRightArr = null;
        if (arg0 instanceof Variable) {        		
           XObject arg0Obj = ((Variable)arg0).execute(xctxt);
           if (arg0Obj instanceof XPathArray) {
        	   foldRightArr = (XPathArray)arg0Obj;
           }
           else {
        	  throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st argument provided to function call "
        	  		                                                    + "array:fold-right is not an xdm array, or cannot "
        	  		                                                    + "be cast to an xdm array.", srcLocator); 
           }
        }
        else {
           XObject arg0Obj = arg0.execute(xctxt);
           if (arg0Obj instanceof XPathArray) {
        	   foldRightArr = (XPathArray)arg0Obj;
           }
           else {
         	  throw new javax.xml.transform.TransformerException("XPTY0004 : The 1st argument provided to function call "
         	  		                                                   + "array:fold-right is not an xdm array, or cannot "
         	  		                                                   + "be cast to an xdm array.", srcLocator); 
           }
        }
        
        XObject foldRightInitObj = null;
        
        if (arg1 instanceof Variable) {
           foldRightInitObj = ((Variable)arg1).execute(xctxt);
        }
        else {
           foldRightInitObj = arg1.execute(xctxt);	
        }
        
        InlineFunction foldRightFunc = null;
        
        if (arg2 instanceof Variable) {
           XObject arg2XObj = arg2.execute(xctxt);
           if (arg2XObj instanceof InlineFunction) {
        	   foldRightFunc = (InlineFunction)arg2XObj;
           }
           else {
              QName varQname = (((Variable)arg2).getElemVariable()).getName();
              throw new javax.xml.transform.TransformerException("FORG0006 : The 3rd argument to function call array:fold-right is a variable reference '" + 
                                                                             varQname.getLocalName() + "', that doesn't "
                                                                             + "evaluate to a function item.", srcLocator);  
           }
        }        
        else if (arg2 instanceof InlineFunction) {
        	foldRightFunc = (InlineFunction)arg2;                                           
        }
        else {
           throw new javax.xml.transform.TransformerException("FORG0006 : The 3rd argument to function call array:fold-right is not a "
           		                                                          + "function item, or cannot be coerced to a function item.", srcLocator);
        }
                   
        String inlineFnXPathStr = foldRightFunc.getFuncBodyXPathExprStr();
        List<InlineFunctionParameter> funcParamList = foldRightFunc.getFuncParamList();
           
        if (funcParamList.size() == 2) {              
           String funcItemParam1Name = (funcParamList.get(0)).getParamName();
           String funcItemParam2Name = (funcParamList.get(1)).getParamName();
              
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
           for (int idx = foldRightArr.size() - 1; idx >= 0; idx--) {
              Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
                 
              inlineFunctionVarMap.put(new QName(funcItemParam1Name), foldRightArr.get(idx));
              
              if (idx == (foldRightArr.size() - 1)) {                    
                  inlineFunctionVarMap.put(new QName(funcItemParam2Name), foldRightInitObj);
              }
              else {
                  inlineFunctionVarMap.put(new QName(funcItemParam2Name), result);                   
              }
                 
              result = inlineFuncXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
                                  
              // Reset the function item argument variables
              inlineFunctionVarMap.put(new QName(funcItemParam1Name), null);
              inlineFunctionVarMap.put(new QName(funcItemParam2Name), null);
           }
        }
        else {
        	throw new javax.xml.transform.TransformerException("XPTY0004 : The function array:fold-right's 3rd argument is "
        			                                                  + "a function having " + funcParamList.size() + " parameters. "
        			                                                  + "Expected 2.", srcLocator); 
        }
        
        return result;
    }

}
