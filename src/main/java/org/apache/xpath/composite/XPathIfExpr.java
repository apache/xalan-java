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

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.functions.XSLFunctionBuilder;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XBooleanStatic;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Class definition, representing an implementation of 
 * XPath 3.1 'if' expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathIfExpr extends Expression {
    
    private static final long serialVersionUID = 4057572946055830336L;

    private String m_ifBranchConditionXPathStr;
    
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
       
       XObject result = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator();
       
       final int currentNode = xctxt.getContextNode();
       
       List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
       
       m_ifBranchConditionXPathStr = m_ifBranchConditionXPathStr.trim();
       m_thenExprXPathStr = m_thenExprXPathStr.trim();
       m_elseExprXPathStr = m_elseExprXPathStr.trim();
       
       if (prefixTable != null) {
          m_ifBranchConditionXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                    m_ifBranchConditionXPathStr, prefixTable);
       }
       
       XPath ifConditionXPath = null;
       
       String str1 = null;
       String str2 = null;
       
       boolean x1 = false;
	   boolean x2 = false;
       
       try {
    	  if (m_ifBranchConditionXPathStr.startsWith("(") && m_ifBranchConditionXPathStr.endsWith(")")) {
    		 String str3 = m_ifBranchConditionXPathStr.substring(1, m_ifBranchConditionXPathStr.length() - 1);
    		 if (!"".equals(str3.trim())) {
    		    m_ifBranchConditionXPathStr = str3; 
    		 }
    	  }    	      	  
    	   
          ifConditionXPath = new XPath(m_ifBranchConditionXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                                 XPath.SELECT, null);
       }
       catch (Exception ex) {
    	   // There are XPath expressions like (1, 2, 3, a(b()))[...] ,
    	   // which may be evaluated here within this exception condition
    	   
    	   int idx1 = m_ifBranchConditionXPathStr.indexOf('[');    	   
    	   if (idx1 != -1) {
    		   x1 = true;
    		   str1 = m_ifBranchConditionXPathStr.substring(0, idx1);
    		   str1 = str1.trim();
    		   str2 = m_ifBranchConditionXPathStr.substring(idx1);
    		   str2 = str2.trim();
    		   if (str2.endsWith("]")) {
    			  x2 = true;
    			  str2 = str2.substring(1, str2.length() - 1);
    			  str2 = str2.trim();
    		   }
    	   } 
    	   
    	   if (!x1 || !x2) {
    	      throw new TransformerException("XPST0003 : An XPath 3.1 'if' expression branch condition expression " + m_ifBranchConditionXPathStr 
    			                                                                                                    + " has a syntax error.", srcLocator);
           }
       }
       
       XObject ifConditionXPathResult = null;
       
       if (!x1 && !x2) {
    	   if (m_vars != null) {
    		   ifConditionXPath.fixupVariables(m_vars, m_globals_size);
    	   }

    	   ifConditionXPathResult = ifConditionXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
       }
       else {
    	   try {
    		   int predicateValue = Integer.valueOf(str2);    		  
    		   ifConditionXPath = new XPath(str1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

    		   if (m_vars != null) {
    			   ifConditionXPath.fixupVariables(m_vars, m_globals_size);
    		   }

    		   ifConditionXPathResult = ifConditionXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
    		   
    		   if (ifConditionXPathResult instanceof ResultSequence) {
    			  ResultSequence rSeq = (ResultSequence)ifConditionXPathResult;
    			  if ((predicateValue >= 1) && (predicateValue <= rSeq.size())) {
    				 ifConditionXPathResult = rSeq.item(predicateValue - 1);   
    			  }
    		   }
    		   else if (predicateValue != 1) {
    			  ifConditionXPathResult = new ResultSequence(); 
    		   }
    	   }
    	   catch (Exception ex) {
    		   throw new TransformerException("XPST0003 : An XPath 3.1 'if' expression branch condition expression " + m_ifBranchConditionXPathStr 
    				                                                                                                 + " has a syntax error.", srcLocator); 
    	   }    	       	   
       }
       
       boolean ifConditionEvalResult = false;
       boolean ifConditionEagerCheck = false;
       String strVal = null;
       
       boolean ifBranchConditionEmptySeq = ((ifConditionXPathResult instanceof ResultSequence) && 
    		                                                                          (((ResultSequence)ifConditionXPathResult).size() == 0)) ? 
    		                                                                        		                                  true : false;  
       
       if ((ifConditionXPathResult instanceof XString) || (ifConditionXPathResult instanceof XSString) 
    		                                           || (ifConditionXPathResult instanceof XSAnyURI) || 
    		                                              (ifConditionXPathResult instanceof XSUntypedAtomic)) {
    	   ifConditionEagerCheck = true;
    	   strVal = XslTransformEvaluationHelper.getStrVal(ifConditionXPathResult);
    	   if ((strVal != null) && (strVal.length() > 0)) {
    		   ifConditionEvalResult = true;  
    	   }
       }
       else if ((ifConditionXPathResult instanceof XNumber) || (ifConditionXPathResult instanceof XSNumericType)) {
    	   ifConditionEagerCheck = true;    	   
    	   strVal = XslTransformEvaluationHelper.getStrVal(ifConditionXPathResult);
    	   if (!"NaN".equals(strVal)) {
    		  double dbl = Double.valueOf(strVal);
    		  if (dbl != 0) {
    			 ifConditionEvalResult = true;   
    		  }
    	   }
       }       
       else if ((ifConditionXPathResult instanceof XSBoolean) || (ifConditionXPathResult instanceof XBoolean) 
    		                                                                                         || (ifConditionXPathResult instanceof XBooleanStatic)) {
    	   ifConditionEagerCheck = true;
    	   if (ifConditionXPathResult.bool()) {
    		  ifConditionEvalResult = true;  
    	   }
       }
       else if (!ifBranchConditionEmptySeq && !(ifConditionXPathResult instanceof XMLNodeCursorImpl)) {
    	   throw new TransformerException("FORG0006 : XPath 3.1 effective boolean value is defined only for xdm items with "
    	   		                                                                                           + "types boolean, string, number, uri and node.", srcLocator);
       }
       
       if ((ifConditionEagerCheck && ifConditionEvalResult) || (!ifConditionEagerCheck && ifConditionXPathResult.bool())) {
           if (prefixTable != null) {
              m_thenExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                        m_thenExprXPathStr, prefixTable);
           }
           
           XPath thenExprXPath = null;
           try {
              thenExprXPath = new XPath(m_thenExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                               XPath.SELECT, null);
           }
           catch (Exception ex) {
        	  throw new TransformerException("XPST0003 : An XPath 3.1 'if' expression then clause " + m_thenExprXPathStr 
                                                                                                    + " has a syntax error.", srcLocator); 
           }
           
           
           if (m_vars != null) {
              thenExprXPath.fixupVariables(m_vars, m_globals_size);
           }                      
           
           Expression expr = thenExprXPath.getExpression();
           XObject xpath3CtxtItem = xctxt.getXPath3ContextItem();
                      
           if ((expr instanceof SelfIteratorNoPredicate) && (ifConditionEvalResult || (xpath3CtxtItem != null))) {
        	  if (ifConditionEvalResult) {
        		 result = new XSString(strVal); 
        	  }
        	  else {
        		 result = xpath3CtxtItem;  
        	  }        	    
           }
           else if (expr instanceof XPathNamedFunctionReference) {
        	  result = (XPathNamedFunctionReference)expr;   
           }
           else {
              result = thenExprXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
           }
       }
       else if ((ifConditionEagerCheck && !ifConditionEvalResult) || (!ifConditionEagerCheck && !ifConditionXPathResult.bool())) {
           if (prefixTable != null) {
              m_elseExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(m_elseExprXPathStr, 
                                                                                                          prefixTable);
           }
           
           XPath elseExprXPath = null;
           try {
              elseExprXPath = new XPath(m_elseExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                              XPath.SELECT, null);
           }
           catch (Exception ex) {
        	  throw new TransformerException("XPST0003 : An XPath 3.1 'if' expression else clause " + m_elseExprXPathStr 
                                                                                                    + " has a syntax error.", srcLocator); 
           }
           
           if (m_vars != null) {
              elseExprXPath.fixupVariables(m_vars, m_globals_size);
           }
           
           Expression expr = elseExprXPath.getExpression();
           XObject xpath3CtxtItem = xctxt.getXPath3ContextItem();
           if ((xpath3CtxtItem != null) && (expr instanceof SelfIteratorNoPredicate)) {
        	  result = xpath3CtxtItem;  
           }
           else if (expr instanceof XPathNamedFunctionReference) {
        	  result = (XPathNamedFunctionReference)expr;   
           }
           else {
              result = elseExprXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
           }
       }
       
       if ((result instanceof XPathNamedFunctionReference) && (m_xpathSuffixStr != null)) {
    	   XPathNamedFunctionReference xPathNamedFunctionReference = (XPathNamedFunctionReference)result;
    	   String funcNamespace = xPathNamedFunctionReference.getFuncNamespace();
    	   if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) || 
    		   (XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(funcNamespace) ||
    		   (XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(funcNamespace) ||
    		   (XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(funcNamespace)) {
    		   XSL3FunctionService xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
    		   ResultSequence argSeq = new ResultSequence();
    		   if (!m_xpathSuffixStr.equals("()")) {
    			   int strLength = m_xpathSuffixStr.length();
    			   String normalizedArgStr = m_xpathSuffixStr.substring(1, strLength - 1);    			   
    			   XPath argXPath = new XPath(normalizedArgStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    			   if (m_vars != null) {
    				   argXPath.fixupVariables(m_vars, m_globals_size);
    			   }
    			   
    			   XObject xObj = argXPath.execute(xctxt, currentNode, xctxt.getNamespaceContext());
    			   if (xObj instanceof ResultSequence) {
    				  argSeq = (ResultSequence)xObj; 
    			   }
    			   else {
    				  argSeq.add(xObj);  
    			   }
    		   }

    		   result = xsl3FunctionService.evaluateXPathNamedFunctionReference((XPathNamedFunctionReference)result, null, argSeq, 
																									    				   prefixTable, m_vars, m_globals_size, 
																									    				   getExpressionOwner(), xctxt); 
    		  
    	   }
       }
       
       return result;
    }        

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }
    
    public String getIfBranchConditionXPathStr() {
        return m_ifBranchConditionXPathStr;
    }

    public void setIfBranchConditionXPathStr(String ifConditionXPathStr) {
    	this.m_ifBranchConditionXPathStr = ifConditionXPathStr;
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
