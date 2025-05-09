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
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSBoolean;

/**
 * Implementation of XPath 3.1 quantified expressions 'some' & 
 * 'every'.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathQuantifiedExpr extends Expression {

    private static final long serialVersionUID = -3073535420126040669L;
    
    /**
     * Constant value, denoting XPath quantified expression 'some'.
     */
    public static final int SOME = 0;
    
    /**
     * Constant value, denoting XPath quantified expression 'every'.
     */
    public static final int EVERY = 1;
    
    /**
     * Class field, representing whether an object instance of this class
     * is evaluating an XPath quantified expression 'some' or every'. 
     */
    private int m_CurrentXPathQuantifier;
    
    /**
     * A java.util.List object supporting implementation of XPath quantified 
     * expression. 
     */
    private List<ForQuantifiedExprVarBinding> m_QuantifiedExprVarBindingList = new 
                                                        ArrayList<ForQuantifiedExprVarBinding>();

    /**
     * A string value supporting implementation of XPath quantified expression.  
     */
    private String m_QuantifierTestXPathStr = null;
    
    /**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */   
    private Vector m_vars;
    
    /**
	 * This class field is used during, XPath.fixupVariables(..) action 
	 * as performed within object of this class.  
	 */
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
        
        XPath quantifiedExprXPath = new XPath(m_QuantifierTestXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                        XPath.SELECT, null);
        quantifiedExprXPath.setIsQuantifiedExpr(true);
        
        ResultSequence resultSequence = getQuantifiedExpressionEvalResult(m_QuantifiedExprVarBindingList.listIterator(), 
        		                                                                                     quantifiedExprXPath, xctxt);
        
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
    
    /**
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
           
           ResultSequence resultSeq2 = new ResultSequence();
           
           int idx2 = varBindingXPathStr.lastIndexOf('/');
           boolean isProcessed = false;
           if (idx2 != -1) {
        	  String xpathLhsStr = varBindingXPathStr.substring(0, idx2);
        	  String xpathRhsStr = varBindingXPathStr.substring(idx2 + 1);
         	  if (!(xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI) || 
															         			 xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI) ||
															         			 xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI) ||
															         			 xpathLhsStr.endsWith(FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI))) {         		 
       		     if (!XPathParser.isStrHasXPathAxisNamePrefix(xpathRhsStr) && (xpathRhsStr.endsWith("()") || xpathRhsStr.endsWith("(.)"))) {    		  
       		    	XPath xpathLhsObj = new XPath(xpathLhsStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
       		    	
       		    	if (m_vars != null) {
       		    		if (!m_xpathVarList.contains(new QName(varName))) {
             			   m_xpathVarList.add(new QName(varName));
             		    }
       		    		xpathLhsObj.fixupVariables(m_vars, m_globals_size);
       		        }
       		    	
       		    	XObject xpathLhsResult = xpathLhsObj.execute(xctxt, contextNode, xctxt.getNamespaceContext());
       		    	
       		    	XPath xpathRhsObj = new XPath(xpathRhsStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
       		    	
       		    	if (xpathLhsResult instanceof XMLNodeCursorImpl) {
       		    	   isProcessed = true;
       		    	   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xpathLhsResult;
       		    	   DTMCursorIterator dtmCursorIter = xmlNodeCursorImpl.iter();
       		    	   int nextNode;
       		    	   while ((nextNode = dtmCursorIter.nextNode()) != DTM.NULL) {
       		    		   XObject xpathRhsResult = xpathRhsObj.execute(xctxt, nextNode, xctxt.getNamespaceContext());
       		    		   resultSeq2.add(xpathRhsResult);
       		    	   }
       		    	}
       		     } 
         	  }  
           }

           if (!isProcessed) {
        	   XPath varBindingXPath = new XPath(varBindingXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
        	   if (m_vars != null) {
        		   if (!m_xpathVarList.contains(new QName(varName))) {
        			   m_xpathVarList.add(new QName(varName));
        		   }
        		   varBindingXPath.fixupVariables(m_vars, m_globals_size);
        	   }

        	   XObject xsObj = varBindingXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());        	    

        	   if (xsObj instanceof XMLNodeCursorImpl) {
        		   XMLNodeCursorImpl xsObjNodeSet = (XMLNodeCursorImpl)xsObj;
        		   DTMCursorIterator dtmIter = xsObjNodeSet.iterRaw();                              

        		   int nextNodeDtmHandle;

        		   while ((nextNodeDtmHandle = dtmIter.nextNode()) != DTM.NULL) {       
        			   XMLNodeCursorImpl singletonXPathNode = new XMLNodeCursorImpl(nextNodeDtmHandle, xctxt);
        			   resultSeq2.add(singletonXPathNode);
        		   }
        	   }
        	   else if (xsObj instanceof ResultSequence) {               
        		   resultSeq2 = (ResultSequence)xsObj;
        	   }
        	   else {
        		   resultSeq2.add(xsObj);
        	   }

        	   if (resultSeq2.size() == 0) {
        		   listIter.previous();

        		   return resultSeq;    
        	   }
           }
           
           Map<QName, XObject> quantifiedExprVarBindingMap = xctxt.getXPathVarMap();
           
           /**
            * For each xdm item within sequence object 'resultSeq2' (which is the
            * result of xpath variable binding expression's evaluation), associate 
            * an XPath quantifier expression's binding variable in turn to that 
            * item.
            */
           for (int idx = 0; idx < resultSeq2.size(); idx++) {
               XObject xdmItem = resultSeq2.item(idx);
                             
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
        	/**
        	 * Here we evaluate, an XPath quantified expression's 'satisfies' clause.
        	 * The XPath quantified expression's 'satisfies' clause is typically evaluated 
        	 * multiple times depending upon, how may XPath quantified expression iterations 
        	 * are there.
        	 */
            
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
