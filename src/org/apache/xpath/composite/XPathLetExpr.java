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
 * An implementation of XPath 3.1 'let' expression.
 *    
 * Ref : https://www.w3.org/TR/xpath-31/#id-let-expressions
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathLetExpr extends Expression {

    private static final long serialVersionUID = 3063682088023616108L;

    private List<LetExprVarBinding> m_LetExprVarBindingList = 
                                                   new ArrayList<LetExprVarBinding>();
    
    private String m_ReturnExprXPathStr = null;
    
    // The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector m_vars;    
    private int m_globals_size;

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
       
       Map<QName, XObject> letExprVarBindingMap = new HashMap<QName, XObject>();
       
       for (int idx = 0; idx < m_LetExprVarBindingList.size(); idx++) {          
          LetExprVarBinding letExprVarBinding = m_LetExprVarBindingList.get(idx);
          String varName = letExprVarBinding.getVarName();
          String fXpathExprStr = letExprVarBinding.getXpathExprStr();
          
          if (prefixTable != null) {
             fXpathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                  fXpathExprStr, prefixTable);
          }
          
          XPath letExprVarBindingXpath = new XPath(fXpathExprStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                             XPath.SELECT, null);
          if (m_vars != null) {
             letExprVarBindingXpath.fixupVariables(m_vars, m_globals_size);
          }
          
          XObject varBindingEvalResult = letExprVarBindingXpath.execute(xctxt, contextNode, 
                                                                                     xctxt.getNamespaceContext());
          
          m_xpathVarList.add(new QName(varName));
          
          letExprVarBindingMap.put(new QName(varName), varBindingEvalResult);
       }              
       
       Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
       xpathVarMap.putAll(letExprVarBindingMap);
       
       if (prefixTable != null) {
          m_ReturnExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                m_ReturnExprXPathStr, prefixTable);
       }
       
       XPath returnExprXpath = new XPath(m_ReturnExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                             XPath.SELECT, null);
       
       if (m_vars != null) {
          returnExprXpath.fixupVariables(m_vars, m_globals_size);
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
       m_vars = (Vector)(vars.clone());
       m_globals_size = globalsSize; 
    }

    @Override
    public boolean deepEquals(Expression expr) {        
       return false;
    }

    public List<LetExprVarBinding> getLetExprVarBindingList() {
        return m_LetExprVarBindingList;
    }

    public void setLetExprVarBindingList(List<LetExprVarBinding> fLetExprVarBindingList) {
        this.m_LetExprVarBindingList = fLetExprVarBindingList;
    }

    public String getReturnExprXPathStr() {
        return m_ReturnExprXPathStr;
    }

    public void setReturnExprXPathStr(String fReturnExprXPathStr) {
        this.m_ReturnExprXPathStr = fReturnExprXPathStr;
    }

}
