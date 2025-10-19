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
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSAnyType;
import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * An implementation of XPath 3.1 'if' expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathIfExpr extends Expression {
    
    private static final long serialVersionUID = 4057572946055830336L;

    private String m_ifConditionXPathStr;
    
    private String m_thenExprXPathStr;
    
    private String m_elseExprXPathStr;        
    
    /**
     * The following two fields of this class, are used during
     * XPath.fixupVariables(..) action as performed within object of
     * this class.
     */
    
    private Vector m_vars;
    
    private int m_globals_size;
    
    /**
     * An optional XPath expression suffix. This could be for example, 
     * function argument information, when XPath 'if' expression before 
     * suffix, evaluates to a function item.
     */
    private String m_xpathSuffixStr;

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
          m_ifConditionXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                    m_ifConditionXPathStr, prefixTable);
       }
       
       XPath ifConditionXPath = new XPath(m_ifConditionXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                 XPath.SELECT, null);
       if (m_vars != null) {
          ifConditionXPath.fixupVariables(m_vars, m_globals_size);
       }
       
       XObject ifConditionXPathResult = ifConditionXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());       
       
       boolean ifConditionEvalResult = false;
       boolean eagerIfConditionCheck = false;
       String strVal = null;
       if ((ifConditionXPathResult instanceof XSString) || (ifConditionXPathResult instanceof XSAnyURI) || 
    		                                               (ifConditionXPathResult instanceof XSUntypedAtomic)) {
    	   eagerIfConditionCheck = true;
    	   XSAnyType xsAnyType = (XSAnyType)ifConditionXPathResult;
    	   strVal = xsAnyType.stringValue();
    	   if ((strVal != null) && (strVal.length() > 0)) {
    		   ifConditionEvalResult = true;  
    	   }
       }
       
       if ((eagerIfConditionCheck && ifConditionEvalResult) || (!eagerIfConditionCheck && ifConditionXPathResult.bool())) {
           if (prefixTable != null) {
              m_thenExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                        m_thenExprXPathStr, prefixTable);
           }
           
           XPath thenExprXPath = new XPath(m_thenExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                               XPath.SELECT, null);
           if (m_vars != null) {
              thenExprXPath.fixupVariables(m_vars, m_globals_size);
           }                      
           
           Expression expr = thenExprXPath.getExpression();
           XObject xpath3CtxtItem = xctxt.getXPath3ContextItem();
                      
           if ((expr instanceof SelfIteratorNoPredicate) && (ifConditionEvalResult || (xpath3CtxtItem != null))) {
        	  if (ifConditionEvalResult) {
        		 evalResult = new XSString(strVal); 
        	  }
        	  else {
        		 evalResult = xpath3CtxtItem;  
        	  }        	    
           }
           else if (expr instanceof XPathNamedFunctionReference) {
        	  evalResult = (XPathNamedFunctionReference)expr;   
           }
           else {
              evalResult = thenExprXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
           }
       }
       else if ((eagerIfConditionCheck && !ifConditionEvalResult) || (!eagerIfConditionCheck && !ifConditionXPathResult.bool())) {
           if (prefixTable != null) {
              m_elseExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_elseExprXPathStr, 
                                                                                                          prefixTable);
           }
           
           XPath elseExprXPath = new XPath(m_elseExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                              XPath.SELECT, null);
           if (m_vars != null) {
              elseExprXPath.fixupVariables(m_vars, m_globals_size);
           }
           
           Expression expr = elseExprXPath.getExpression();
           XObject xpath3CtxtItem = xctxt.getXPath3ContextItem();
           if ((expr instanceof SelfIteratorNoPredicate) && (xpath3CtxtItem != null)) {
        	  evalResult = xpath3CtxtItem;  
           }
           else if (expr instanceof XPathNamedFunctionReference) {
        	  evalResult = (XPathNamedFunctionReference)expr;   
           }
           else {
              evalResult = elseExprXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
           }
       }
       
       if ((evalResult instanceof XPathNamedFunctionReference) && (m_xpathSuffixStr != null)) {
    	   XPathNamedFunctionReference xPathNamedFunctionReference = (XPathNamedFunctionReference)evalResult;
    	   String funcNamespace = xPathNamedFunctionReference.getFuncNamespace();
    	   if ((FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) || 
    		   (FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(funcNamespace) ||
    		   (FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(funcNamespace) ||
    		   (FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(funcNamespace)) {
    		   XSL3FunctionService xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
    		   ResultSequence argSeq = new ResultSequence();
    		   if (!m_xpathSuffixStr.equals("()")) {
    			   int strLength = m_xpathSuffixStr.length();
    			   String normalizedArgStr = m_xpathSuffixStr.substring(1, strLength - 1);    			   
    			   XPath argXPath = new XPath(normalizedArgStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    			   if (m_vars != null) {
    				   argXPath.fixupVariables(m_vars, m_globals_size);
    			   }
    			   
    			   XObject xObj = argXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
    			   if (xObj instanceof ResultSequence) {
    				  argSeq = (ResultSequence)xObj; 
    			   }
    			   else {
    				  argSeq.add(xObj);  
    			   }
    		   }

    		   evalResult = xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)evalResult, null, argSeq, 
																									    				   prefixTable, m_vars, m_globals_size, 
																									    				   getExpressionOwner(), xctxt); 
    		  
    	   }
       }
       
       return evalResult;
    }        

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }
    
    public String getIfConditionXPathStr() {
        return m_ifConditionXPathStr;
    }

    public void setIfConditionXPathStr(String ifConditionXPathStr) {
    	this.m_ifConditionXPathStr = ifConditionXPathStr;
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

    public String getSuffixXPathStr() {
    	return m_xpathSuffixStr;
    }

    public void setSuffixXPathStr(String suffixXPathStr) {
    	this.m_xpathSuffixStr = suffixXPathStr;
    }

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
    	// no op       
    }

    @Override
    public boolean deepEquals(Expression expr) {
    	// no op    	
    	return false;
    }

}
