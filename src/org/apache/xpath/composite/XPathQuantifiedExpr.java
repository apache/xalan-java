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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XPath 3.1 quantified expressions.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathQuantifiedExpr extends Expression {

    private static final long serialVersionUID = -3073535420126040669L;
    
    // Constant value, denoting XPath quantified expression 'some'.
    public static final int SOME = 0;
    
    // Constant value, denoting XPath quantified expression 'every'.
    public static final int EVERY = 1;
    
    private int m_CurrentXPathQuantifier;
    
    private List<ForQuantifiedExprVarBinding> m_QuantifiedExprVarBindingList = new 
                                                        ArrayList<ForQuantifiedExprVarBinding>();

    private String m_QuantifierTestXPathStr = null;
    
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
        XObject quantifiedExprResult = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        if (prefixTable != null) {
            m_QuantifierTestXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                          m_QuantifierTestXPathStr, prefixTable);
        }
        
        XPath quantifiedExprXpath = new XPath(m_QuantifierTestXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                        XPath.SELECT, null);
        quantifiedExprXpath.setIsQuantifiedExpr(true);
        
        ResultSequence resultSequence = getQuantifiedExpressionEvalResult(m_QuantifiedExprVarBindingList.listIterator(), 
        		                                                                                     quantifiedExprXpath, xctxt);
        
        m_xpathVarList.clear();
        
        boolean isEvalResultDecided = false;
        
        for (int idx = 0; idx < resultSequence.size(); idx++) {
           XObject xsObject = resultSequence.item(idx);
           XSBoolean xsBoolean = (XSBoolean)xsObject;
           
           if (m_CurrentXPathQuantifier == SOME) {
              if (xsBoolean.value()) {
                 quantifiedExprResult = XBoolean.S_TRUE;
                 isEvalResultDecided = true;
                 break;      
              }
           }
           else {
              if (!xsBoolean.value()) {
                 quantifiedExprResult = XBoolean.S_FALSE;
                 isEvalResultDecided = true;
                 break;     
              }   
           }
        }
        
        if (!isEvalResultDecided) {
           if (m_CurrentXPathQuantifier == SOME) {
              quantifiedExprResult = XBoolean.S_FALSE;    
           }
           else {
              quantifiedExprResult = XBoolean.S_TRUE;    
           }
        }
        
        return quantifiedExprResult;
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

    public int getCurrentXPathQuantifier() {
        return m_CurrentXPathQuantifier;
    }

    public void setCurrentXPathQuantifier(int fCurrentXPathQuantifier) {
        this.m_CurrentXPathQuantifier = fCurrentXPathQuantifier;
    }

    public List<ForQuantifiedExprVarBinding> getQuantifiedExprVarBindingList() {
        return m_QuantifiedExprVarBindingList;
    }

    public void setQuantifiedExprVarBindingList(List<ForQuantifiedExprVarBinding> 
                                                                      fQuantifiedExprVarBindingList) {
        this.m_QuantifiedExprVarBindingList = fQuantifiedExprVarBindingList;
    }

    public String getQuantifierTestXPathStr() {
        return m_QuantifierTestXPathStr;
    }

    public void setQuantifierTestXPathStr(String fQuantifierTestXPathStr) {
        this.m_QuantifierTestXPathStr = fQuantifierTestXPathStr;
    }
    
    /*
     * This method, does the evaluations needed to determine the evaluation result 
     * of the XPath quantified expression, returned as an 'ResultSequence' object 
     * instance.
     */
    private ResultSequence getQuantifiedExpressionEvalResult(ListIterator listIter, 
                                                                     XPath quantifiedExprXPath, 
                                                                     XPathContext xctxt) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        if (listIter.hasNext()) {           
           ForQuantifiedExprVarBinding quantifiedExprVarBinding = (ForQuantifiedExprVarBinding)listIter.next();            
            
           // Evaluate the current, variable binding xpath expression
           
           String varName = quantifiedExprVarBinding.getVarName();
           String varBindingXPathStr = quantifiedExprVarBinding.getXPathExprStr();                      
           
           if (prefixTable != null) {
               varBindingXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                        varBindingXPathStr, prefixTable);
           }
           
           XPath varBindingXPath = new XPath(varBindingXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                   XPath.SELECT, null);
           if (m_vars != null) {
              if (!m_xpathVarList.contains(new QName(varName))) {
                 m_xpathVarList.add(new QName(varName));
              }
              varBindingXPath.fixupVariables(m_vars, m_globals_size);
           }
           
           XObject xsObj = varBindingXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
           
           ResultSequence xsObjResultSeq = new ResultSequence(); 
           
           if (xsObj instanceof XNodeSet) {
               XNodeSet xsObjNodeSet = (XNodeSet)xsObj;
               DTMIterator dtmIter = xsObjNodeSet.iterRaw();
               
               int nextNodeDtmHandle;
                      
               while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {       
                  XNodeSet singletonXPathNode = new XNodeSet(nextNodeDtmHandle, xctxt);
                  xsObjResultSeq.add(singletonXPathNode);
               }
           }
           else if (xsObj instanceof ResultSequence) {               
               xsObjResultSeq = (ResultSequence)xsObj;
           }
           else {
               xsObjResultSeq.add(xsObj);
           }
           
           if (xsObjResultSeq.size() == 0) {
               listIter.previous();
               
               return resultSeq;    
           }
           
           Map<QName, XObject> quantifiedExprVarBindingMap = xctxt.getXPathVarMap();
           
           // For each xdm item within sequence object 'xsObjResultSeq' (which is the 
           // result of variable binding xpath expression's evaluation), bind the 
           // quantifier expression's binding variable in turn to that item.
           for (int idx = 0; idx < xsObjResultSeq.size(); idx++) {
               XObject xdmItem = xsObjResultSeq.item(idx);
                             
               quantifiedExprVarBindingMap.put(new QName(varName), xdmItem);
               
               ResultSequence res = getQuantifiedExpressionEvalResult(listIter, quantifiedExprXPath, xctxt);
               // Append xdm items of sequence 'res', to the final sequence object 'resultSeq'   
               for (int idx1 = 0; idx1 < res.size(); idx1++) {
                  resultSeq.add(res.item(idx1));    
               }
           }
           
           listIter.previous();
           
           return resultSeq;
        }
        else {
            // Here we evaluate, an XPath quantified expression's satisfies clause. 
        	// The XPath quantified expression's satisfies clause will typically be 
        	// evaluated multiple times depending upon, how may quantified expression 
        	// iterations are there.
            
            if (m_vars != null) {              
               quantifiedExprXPath.fixupVariables(m_vars, m_globals_size);
            }
            
            ResultSequence satisfiesClauseEvalResult = new ResultSequence();
            
            try {
               XObject quantifiedTestExprValue = quantifiedExprXPath.execute(xctxt, contextNode, 
                                                                                     xctxt.getNamespaceContext());
               satisfiesClauseEvalResult.add(new XSBoolean(quantifiedTestExprValue.bool()));
            }
            catch (TransformerException ex) {
               satisfiesClauseEvalResult.add(new XSBoolean(false));	
            }                        
            
            return satisfiesClauseEvalResult; 
        }
    }

}
