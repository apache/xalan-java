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
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.InlineFunction;
import org.apache.xpath.objects.XObject;

/*
 * The XalanJ xpath parser, creates and populates an object of 
 * this class, as a representation of XPath 3.1 dynamic 
 * function call.
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
       
       ExpressionContext exprContext = xctxt.getExpressionContext();
       
       XObject functionRef = exprContext.getVariableOrParam(new QName(funcRefVarName));
       
       if (functionRef instanceof InlineFunction) {
           InlineFunction inlineFunction = (InlineFunction)functionRef;
           
           String inlineFnXpathStr = inlineFunction.getFuncBodyXPathExprStr();
           List<String> funcParamNameList = inlineFunction.getFuncParamNameList();           
           
           if (argList.size() != funcParamNameList.size()) {
               throw new javax.xml.transform.TransformerException("XPTY0004 : Number of arguments required for "
                                                       + "dynamic call to function is " + funcParamNameList.size() + ". "
                                                       + "Number of arguments provided " + argList.size() + ".", xctxt.getSAXLocator());    
           }
           
           Map<QName, XObject> inlineFunctionVarMap = xctxt.getInlineFunctionVarMap();
           
           for (int idx = 0; idx < funcParamNameList.size(); idx++) {              
              String funcParamName = funcParamNameList.get(idx);
              
              String argXPathStr = argList.get(idx);
              XObject argValue = null;
              if (argXPathStr.startsWith("$")) {
                 argValue = exprContext.getVariableOrParam(new QName(argXPathStr.substring(1)));
              }
              else {
                 XPath argXpath = new XPath(argXPathStr, srcLocator, null, XPath.SELECT, null);
                 argValue = argXpath.execute(xctxt, contextNode, null);
              }
              
              inlineFunctionVarMap.put(new QName(funcParamName), argValue);
           }
           
           XPath inlineFnXpath = new XPath(inlineFnXpathStr, srcLocator, null, XPath.SELECT, null);
           evalResult = inlineFnXpath.execute(xctxt, contextNode, null);
           
           inlineFunctionVarMap.clear();
       }
        
       return evalResult;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
       // no op
    }

    @Override
    public boolean deepEquals(Expression expr) {
        return false;
    }

}
