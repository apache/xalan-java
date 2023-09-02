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
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

/*
 * The XalanJ XPath parser, creates and populates an object of this class, 
 * as a representation of XPath 3.1 "for" expression.
 * 
 * The XPath 3.1 spec, defines "for" expression with following grammar,
 * 
 *   ForExpr               ::=      SimpleForClause "return" ExprSingle 
 *   SimpleForClause       ::=      "for" SimpleForBinding ("," SimpleForBinding)*  
 *   SimpleForBinding      ::=      "$" VarName "in" ExprSingle
 *    
 *  Ref : https://www.w3.org/TR/xpath-31/#id-for-expressions
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ForExpr extends Expression {
    
    private static final long serialVersionUID = -7289739978026057248L;

    private List<ForQuantifiedExprVarBinding> fForExprVarBindingList = new 
                                                    ArrayList<ForQuantifiedExprVarBinding>();
    
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
       ResultSequence finalResultSeq = new ResultSequence();
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
       List<XMLNSDecl> prefixTable = null;
       if (elemTemplateElement != null) {
          prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
       }
       
       if (prefixTable != null) {
          fReturnExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                       fReturnExprXPathStr, prefixTable);
       }
       
       XPath returnExprXPath = new XPath(fReturnExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                               XPath.SELECT, null);
       
       ResultSequence resultSeq = getForExpressionEvalResult(fForExprVarBindingList.listIterator(), 
                                                                            returnExprXPath, xctxt);       
       
       // An xdm sequence object 'resultSeq', may have items that are themselves sequence 
       // objects. We need to expand such nested sequence objects, to get a final sequence
       // none of whose items are sequence with cardinality greater than one.   
       XslTransformEvaluationHelper.expandResultSequence(resultSeq, finalResultSeq);
               
       m_xpathVarList.clear();
       
       return finalResultSeq; 
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

    public List<ForQuantifiedExprVarBinding> getForExprVarBindingList() {
        return fForExprVarBindingList;
    }

    public void setForExprVarBindingList(List<ForQuantifiedExprVarBinding> forExprVarBindingList) {
        this.fForExprVarBindingList = forExprVarBindingList;
    }

    public String getReturnExprXPathStr() {
        return fReturnExprXPathStr;
    }

    public void setReturnExprXPathStr(String returnExprXPathStr) {
        this.fReturnExprXPathStr = returnExprXPathStr;
    }
    
    /*
     * This method, does all the evaluations needed to determine the final evaluation result 
     * of the XPath 'for' expression, returned as a 'ResultSequence' object.
     */
    private ResultSequence getForExpressionEvalResult(ListIterator listIter, 
                                                                     XPath returnExprXPath, 
                                                                     XPathContext xctxt) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        int contextNode = xctxt.getContextNode();
        
        Map<QName, XObject> forExprVarBindingMap = xctxt.getXPathVarMap();
        
        if (listIter.hasNext()) {           
           ForQuantifiedExprVarBinding forExprVarBinding = (ForQuantifiedExprVarBinding)listIter.next();            
            
           // Evaluate the XPath 'for' expression's, one of the variable binding xpath expression
           
           String varName = forExprVarBinding.getVarName();
           String varBindingXPathStr = forExprVarBinding.getXPathExprStr();
           
           ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
           List<XMLNSDecl> prefixTable = null;
           if (elemTemplateElement != null) {
              prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
           }
           
           if (prefixTable != null) {
              varBindingXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                            varBindingXPathStr, prefixTable);
           }
           
           XPath varBindingXPath = new XPath(varBindingXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                 XPath.SELECT, null);
           if (fVars != null) {
              m_xpathVarList.add(new QName(varName));
              varBindingXPath.fixupVariables(fVars, fGlobalsSize);
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
               ResultSequence rSeq = (ResultSequence)xsObj;
               for (int idx = 0; idx < rSeq.size(); idx++) {
                   xsObjResultSeq.add(rSeq.item(idx)); 
               }
           }
           else {
               xsObjResultSeq.add(xsObj);
           }
           
           if (xsObjResultSeq.size() == 0) {
               listIter.previous();
               
               return resultSeq;    
           }
           
           // For each xdm item within sequence object 'xsObjResultSeq' (which is the 
           // result of variable binding xpath expression's evaluation), bind the 'for' 
           // expression's binding variable in turn to that item.
           for (int idx = 0; idx < xsObjResultSeq.size(); idx++) {
               XObject xdmItem = xsObjResultSeq.item(idx);
                             
               forExprVarBindingMap.put(new QName(varName), xdmItem);
               
               ResultSequence res = getForExpressionEvalResult(listIter, returnExprXPath, xctxt);
               // Append xdm items of sequence 'res', to the final sequence object 'resultSeq'   
               for (int idx1 = 0; idx1 < res.size(); idx1++) {
                  XObject xObj = res.item(idx1);
                  resultSeq.add(xObj);    
               }
           }
           
           listIter.previous();
           
           return resultSeq;
        }
        else {
            // This else clause, evaluates the XPath 'for' expression's 'return' 
            // expression. The XPath 'for' expression's 'return' expression may
            // be evaluated multiple times depending upon, how may 'for' expression
            // iterations are there.
            
            if (fVars != null) {              
               returnExprXPath.fixupVariables(fVars, fGlobalsSize);
            }
            
            ResultSequence returnExprResultSet = new ResultSequence();
            
            XObject retExprResultVal = returnExprXPath.execute(xctxt, contextNode, 
                                                                        xctxt.getNamespaceContext());
            
            if (retExprResultVal instanceof XNodeSet) { 
               XNodeSet xNodeSet = (XNodeSet)retExprResultVal;
               
               DTMIterator dtmIter = xNodeSet.iterRaw();               
               
               int nextNodeDtmHandle;
               
               while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {       
                  XNodeSet nodeSetItem = new XNodeSet(nextNodeDtmHandle, xctxt);
                  returnExprResultSet.add(nodeSetItem);
               }
            }
            else if (retExprResultVal instanceof ResultSequence) {
                ResultSequence rSeq = (ResultSequence)retExprResultVal;
                for (int idx = 0; idx < rSeq.size(); idx++) {
                    returnExprResultSet.add(rSeq.item(idx)); 
                } 
            }
            else {
                returnExprResultSet.add(retExprResultVal);
            }
            
            return returnExprResultSet; 
        }
    }

}
