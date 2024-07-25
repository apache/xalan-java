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

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/*
 * An implementation of XPath 3.1 'if' expression.
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-conditionals
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathIfExpr extends Expression {
    
    private static final long serialVersionUID = 4057572946055830336L;

    private String m_conditionalExprXPathStr;
    
    private String m_thenExprXPathStr;
    
    private String m_elseExprXPathStr;
    
    // The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector m_vars;    
    private int m_globals_size;

    public String getConditionalExprXPathStr() {
        return m_conditionalExprXPathStr;
    }

    public void setConditionalExprXPathStr(String conditionalExprXPathStr) {
        this.m_conditionalExprXPathStr = conditionalExprXPathStr;
    }

    public String getThenExprXPathStr() {
        return m_thenExprXPathStr;
    }

    public void setThenExprXPathStr(String thenExprXPathStr) {
        this.m_thenExprXPathStr = thenExprXPathStr;
    }

    public String getElseExprXPathStr() {
        return m_elseExprXPathStr;
    }

    public void setElseExprXPathStr(String elseExprXPathStr) {
        this.m_elseExprXPathStr = elseExprXPathStr;
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
       
       ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
       List<XMLNSDecl> prefixTable = null;
       if (elemTemplateElement != null) {
          prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
       }
       
       if (prefixTable != null) {
          m_conditionalExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                    m_conditionalExprXPathStr, prefixTable);
       }
       
       XPath ifConditionalXPath = new XPath(m_conditionalExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                 XPath.SELECT, null);
       if (m_vars != null) {
          ifConditionalXPath.fixupVariables(m_vars, m_globals_size);
       }
       
       XObject ifConditionalXPathResult = ifConditionalXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
       
       if (ifConditionalXPathResult.bool()) {
           if (prefixTable != null) {
              m_thenExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                        m_thenExprXPathStr, prefixTable);
           }
           
           XPath thenExprXpath = new XPath(m_thenExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                               XPath.SELECT, null);
           if (m_vars != null) {
              thenExprXpath.fixupVariables(m_vars, m_globals_size);
           }
           
           evalResult = thenExprXpath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
       }
       else {
           if (prefixTable != null) {
              m_elseExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_elseExprXPathStr, 
                                                                                                          prefixTable);
           }
           
           XPath elseExprXpath = new XPath(m_elseExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                              XPath.SELECT, null);
           if (m_vars != null) {
              elseExprXpath.fixupVariables(m_vars, m_globals_size);
           }
           
           evalResult = elseExprXpath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
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

}
