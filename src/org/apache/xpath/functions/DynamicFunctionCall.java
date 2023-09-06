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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.XObject;

/*
 * The XalanJ XPath parser, creates and populates an object of this class, 
 * as a representation of XPath 3.1 dynamic function call.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-dynamic-function-invocation
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class DynamicFunctionCall extends Expression {
    
    private static final long serialVersionUID = -4177034386870890029L;

    private String funcRefVarName;
    
    private List<String> argList;
    
    // The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector fVars;    
    private int fGlobalsSize;

    public String getFuncRefVarName() {
        return funcRefVarName;
    }

    public void setFuncRefVarName(String funcRefVarName) {
        this.funcRefVarName = funcRefVarName;
    }

    public List<String> getArgList() {
        return argList;
    }

    public void setArgList(List<String> argList) {
        this.argList = argList;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
        // no op
    }

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        
       XObject evalResult = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       int contextNode = xctxt.getContextNode();
       
       Map<QName, XObject> inlineFunctionVarMap = xctxt.getXPathVarMap();
       
       // We find below, reference of an XPath inline function, that this dynamic
       // function call refers to.       
       XObject functionRef = inlineFunctionVarMap.get(new QName(funcRefVarName));
       
       if (functionRef == null) {
           ExpressionContext exprContext = xctxt.getExpressionContext();
          
           try {
              functionRef = exprContext.getVariableOrParam(new QName(funcRefVarName));
           }
           catch (TransformerException ex) {
              // Try to get an XPath inline function reference, from within 
              // stylesheet's global scope. 
              ExpressionNode expressionNode = getExpressionOwner();
              ExpressionNode stylesheetRootNode = null;
              while (expressionNode != null) {
                 stylesheetRootNode = expressionNode;
                 expressionNode = expressionNode.exprGetParent();                     
              }
              StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
              Map<QName, InlineFunction> globalInlineFunctionVarMap = stylesheetRoot.
                                                                            getInlineFunctionVarMap();
              functionRef = globalInlineFunctionVarMap.get(new QName(funcRefVarName)); 
           }           
       }
       
       if ((functionRef != null) && (functionRef instanceof InlineFunction)) {
           InlineFunction inlineFunction = (InlineFunction)functionRef;
           
           String inlineFnXPathStr = inlineFunction.getFuncBodyXPathExprStr();
           List<String> funcParamNameList = inlineFunction.getFuncParamNameList();           
           
           if (argList.size() != funcParamNameList.size()) {
               throw new javax.xml.transform.TransformerException("XPTY0004 : Number of arguments required for "
                                                       + "dynamic call to function is " + funcParamNameList.size() + ". "
                                                       + "Number of arguments provided " + argList.size() + ".", xctxt.getSAXLocator());    
           }
           
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
           List<XMLNSDecl> prefixTable = null;
           if (elemTemplateElement != null) {
               prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           }
           
           Map<QName, XObject> functionParamAndArgMap = new HashMap<QName, XObject>();
           
           for (int idx = 0; idx < funcParamNameList.size(); idx++) {              
              String funcParamName = funcParamNameList.get(idx);
              
              String argXPathStr = argList.get(idx);
              
              if (prefixTable != null) {
                  argXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(argXPathStr, 
                                                                                                      prefixTable);
              }
              
              XPath argXPath = new XPath(argXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                        XPath.SELECT, null);
              if (fVars != null) {
                 argXPath.fixupVariables(fVars, fGlobalsSize);
              }
              
              XObject argValue = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
              
              m_xpathVarList.add(new QName(funcParamName));
              
              functionParamAndArgMap.put(new QName(funcParamName), argValue);
           }
           
           inlineFunctionVarMap.putAll(functionParamAndArgMap);
           
           if (prefixTable != null) {
              inlineFnXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(inlineFnXPathStr, 
                                                                                                           prefixTable);
           }
           
           XPath inlineFnXPath = new XPath(inlineFnXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                       XPath.SELECT, null);
           if (fVars != null) {
              inlineFnXPath.fixupVariables(fVars, fGlobalsSize);
           }
           
           evalResult = inlineFnXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
           
           inlineFunctionVarMap.clear();           
       }
       else {
           throw new javax.xml.transform.TransformerException("XPST0008 variable '" + funcRefVarName + "' has "
                                                        + "not been declared, or its declaration is not in scope.", 
                                                                                                      xctxt.getSAXLocator());    
       }
               
       return evalResult;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        fVars = (Vector)(vars.clone());
        fGlobalsSize = globalsSize; 
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }

}
