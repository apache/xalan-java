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
import org.apache.xpath.xs.types.XSBoolean;

/*
 * The XalanJ XPath parser, creates and populates an object of this class, 
 * as a representation of XPath 3.1 quantified expression.
 * 
 * The XPath 3.1 spec, defines quantified expressions with following grammar,
 * 
 *   QuantifiedExpr   ::=   ("some" | "every") "$" VarName "in" ExprSingle ("," "$" VarName "in" 
 *                                                   ExprSingle)* "satisfies" ExprSingle
 *    
 *  Ref : https://www.w3.org/TR/xpath-31/#id-quantified-expressions
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class QuantifiedExpr extends Expression {

    private static final long serialVersionUID = -3073535420126040669L;
    
    // Constant value, denoting XPath quantified expression 'some'.
    public static final int SOME = 0;
    
    // Constant value, denoting XPath quantified expression 'every'.
    public static final int EVERY = 1;
    
    private int fCurrentXPathQuantifier;
    
    private List<ForQuantifiedExprVarBinding> fQuantifiedExprVarBindingList = new 
                                                        ArrayList<ForQuantifiedExprVarBinding>();

    private String fQuantifierTestXPathStr = null;
    
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
        XObject quantifiedExprResult = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
        List<XMLNSDecl> prefixTable = null;
        if (elemTemplateElement != null) {
            prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
        }
        
        if (prefixTable != null) {
            fQuantifierTestXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                          fQuantifierTestXPathStr, prefixTable);
        }
        
        XPath quantifiedExprXpath = new XPath(fQuantifierTestXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                        XPath.SELECT, null);
        
        ResultSequence resultSequence = getQuantifiedExpressionEvalResult(fQuantifiedExprVarBindingList.listIterator(), 
                                                                                                 quantifiedExprXpath, xctxt);
        m_xpathVarList.clear();
        
        boolean isEvalResultDecided = false;
        
        for (int idx = 0; idx < resultSequence.size(); idx++) {
           XObject xsObject = resultSequence.item(idx);
           XSBoolean xsBoolean = (XSBoolean)xsObject;
           
           if (fCurrentXPathQuantifier == SOME) {
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
           if (fCurrentXPathQuantifier == SOME) {
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
       fVars = (Vector)(vars.clone());
       fGlobalsSize = globalsSize;
    }

    @Override
    public boolean deepEquals(Expression expr) {
       return false;
    }

    public int getCurrentXPathQuantifier() {
        return fCurrentXPathQuantifier;
    }

    public void setCurrentXPathQuantifier(int fCurrentXPathQuantifier) {
        this.fCurrentXPathQuantifier = fCurrentXPathQuantifier;
    }

    public List<ForQuantifiedExprVarBinding> getQuantifiedExprVarBindingList() {
        return fQuantifiedExprVarBindingList;
    }

    public void setQuantifiedExprVarBindingList(List<ForQuantifiedExprVarBinding> 
                                                                      fQuantifiedExprVarBindingList) {
        this.fQuantifiedExprVarBindingList = fQuantifiedExprVarBindingList;
    }

    public String getQuantifierTestXPathStr() {
        return fQuantifierTestXPathStr;
    }

    public void setQuantifierTestXPathStr(String fQuantifierTestXPathStr) {
        this.fQuantifierTestXPathStr = fQuantifierTestXPathStr;
    }
    
    /*
     * This method, does the evaluations needed to determine the evaluation result 
     * of the XPath quantified expression, returned as an 'ResultSequence' object 
     * instance.
     */
    private ResultSequence getQuantifiedExpressionEvalResult(ListIterator listIter, 
                                                                     XPath quantifiedExprXpath, 
                                                                     XPathContext xctxt) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        Map<QName, XObject> quantifiedExprVarBindingMap = xctxt.getXPathVarMap();
        
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
           
           XPath varBindingXpath = new XPath(varBindingXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                   XPath.SELECT, null);
           if (fVars != null) {
              if (!m_xpathVarList.contains(new QName(varName))) {
                 m_xpathVarList.add(new QName(varName));
              }
              varBindingXpath.fixupVariables(fVars, fGlobalsSize);
           }
           
           XObject xsObj = varBindingXpath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
           
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
           
           // For each xdm item within sequence object 'xsObjResultSeq' (which is the 
           // result of variable binding xpath expression's evaluation), bind the 
           // quantifier expression's binding variable in turn to that item.
           for (int idx = 0; idx < xsObjResultSeq.size(); idx++) {
               XObject xdmItem = xsObjResultSeq.item(idx);
                             
               quantifiedExprVarBindingMap.put(new QName(varName), xdmItem);
               
               ResultSequence res = getQuantifiedExpressionEvalResult(listIter, quantifiedExprXpath, xctxt);
               // Append xdm items of sequence 'res', to the final sequence object 'resultSeq'   
               for (int idx1 = 0; idx1 < res.size(); idx1++) {
                  resultSeq.add(res.item(idx1));    
               }
           }
           
           listIter.previous();
           
           return resultSeq;
        }
        else {
            // This else clause, evaluates the XPath quantified expression's 'satisfies' 
            // clause. The XPath quantified expression's 'satisfies' clause may be evaluated
            // multiple times depending upon, how may 'some'/'every' expression iterations
            // are there.
            
            if (fVars != null) {              
               quantifiedExprXpath.fixupVariables(fVars, fGlobalsSize);
            }
            
            ResultSequence satisfiesClauseEvalResult = new ResultSequence(); 
            
            XObject quantifiedTestExprValue = quantifiedExprXpath.execute(xctxt, contextNode, 
                                                                                     xctxt.getNamespaceContext());            
            
            satisfiesClauseEvalResult.add(new XSBoolean(quantifiedTestExprValue.bool()));
            
            return satisfiesClauseEvalResult; 
        }
    }

}
