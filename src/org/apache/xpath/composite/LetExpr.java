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
package org.apache.xpath.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/*
 * The XalanJ XPath parser, creates and populates an object of this class, 
 * as a representation of XPath 3.1 "let" expression.
 * 
 * The XPath 3.1 spec, defines "let" expression with following grammar,
 * 
 *   LetExpr               ::=     SimpleLetClause "return" ExprSingle 
 *   SimpleLetClause       ::=     "let" SimpleLetBinding ("," SimpleLetBinding)* 
 *   SimpleLetBinding      ::=     "$" VarName ":=" ExprSingle
 *    
 *  Ref : https://www.w3.org/TR/xpath-31/#id-let-expressions
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class LetExpr extends Expression {

    private static final long serialVersionUID = 3063682088023616108L;

    private List<LetExprVarBinding> fLetExprVarBindingList = 
                                                   new ArrayList<LetExprVarBinding>();
    
    private String fReturnExprXPathStr = null;
    
    // The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector fVars;    
    private int fGlobalsSize;

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // no op
    }
    
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
       
       XObject evalResult = null;
        
       SourceLocator srcLocator = xctxt.getSAXLocator();
        
       int contextNode = xctxt.getContextNode();              
       
       ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
       List<XMLNSDecl> prefixTable = null;
       if (elemTemplateElement != null) {
          prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
       }
       
       Map<QName, XObject> letExprVarBackupMap = new HashMap<QName, XObject>();
       
       for (int idx = 0; idx < fLetExprVarBindingList.size(); idx++) {          
          LetExprVarBinding letExprVarBinding = fLetExprVarBindingList.get(idx);
          String varName = letExprVarBinding.getVarName();
          String fXpathExprStr = letExprVarBinding.getXpathExprStr();
          
          if (prefixTable != null) {
             fXpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                  fXpathExprStr, prefixTable);
          }
          
          XPath letExprVarBindingXpath = new XPath(fXpathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                             XPath.SELECT, null);
          if (fVars != null) {
             letExprVarBindingXpath.fixupVariables(fVars, fGlobalsSize);
          }
          
          XObject varBindingEvalResult = letExprVarBindingXpath.execute(xctxt, contextNode, 
                                                                                     xctxt.getNamespaceContext());
          
          m_xpathVarList.add(new QName(varName));
          
          letExprVarBackupMap.put(new QName(varName), varBindingEvalResult);
       }
       
       Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
       
       xpathVarMap.putAll(letExprVarBackupMap);
       
       if (prefixTable != null) {
          fReturnExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                fReturnExprXPathStr, prefixTable);
       }
       
       XPath returnExprXpath = new XPath(fReturnExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                             XPath.SELECT, null);
       
       if (fVars != null) {
          returnExprXpath.fixupVariables(fVars, fGlobalsSize);
       }
       
       evalResult = returnExprXpath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
       
       if (evalResult == null) {
          // Return an empty sequence, here
          evalResult = new ResultSequence();   
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

    public List<LetExprVarBinding> getLetExprVarBindingList() {
        return fLetExprVarBindingList;
    }

    public void setLetExprVarBindingList(List<LetExprVarBinding> fLetExprVarBindingList) {
        this.fLetExprVarBindingList = fLetExprVarBindingList;
    }

    public String getReturnExprXPathStr() {
        return fReturnExprXPathStr;
    }

    public void setReturnExprXPathStr(String fReturnExprXPathStr) {
        this.fReturnExprXPathStr = fReturnExprXPathStr;
    }

}
