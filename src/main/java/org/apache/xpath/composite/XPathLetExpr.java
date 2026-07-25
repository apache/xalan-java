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
import java.util.Map;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.functions.string.FuncConcat;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * An XPath 3.1 'let' expression implementation.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathLetExpr extends Expression {

    private static final long serialVersionUID = 3063682088023616108L;

    /**
     * Class field, used to represent XPath 3.1 'let' expression's 
     * variable bindings.
     */
    private List<XPathLetExprVarBinding> m_letExprVarBindingList = 
                                                   new ArrayList<XPathLetExprVarBinding>();
    
    /**
     * Class field, used to represent XPath 3.1 'let' 
     * expression's return clause XPath expression string.
     */
    private String m_returnExprXPathStr = null;
    
    // Class field, used to resolve variable references
    // within an XPath expression.
    private Vector m_vars;
    
    // Class field, used to resolve variable references
    // within an XPath expression.
    private int m_globals_size;
    
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
       
       XObject evalResult = null;
        
       SourceLocator srcLocator = xctxt.getSAXLocator();
        
       final int sourceNode = xctxt.getContextNode();
       
       List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
       
       Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
       
       List<QName> qNameVarList = new ArrayList<QName>();
       
       try {
    	   int size1 = m_letExprVarBindingList.size();
    	   
    	   for (int idx = 0; idx < size1; idx++) {          
    		   XPathLetExprVarBinding letExprVarBinding = m_letExprVarBindingList.get(idx);
    		   String varName = letExprVarBinding.getVarName();
    		   String varResultXPathExprStr = letExprVarBinding.getXPathExprStr();

    		   if (prefixTable != null) {
    			   varResultXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(varResultXPathExprStr, prefixTable);
    		   }

    		   XPath letExprVarBindingXPath = new XPath(varResultXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    		   if (m_vars != null) {
    			   letExprVarBindingXPath.fixupVariables(m_vars, m_globals_size);
    		   }

    		   XObject varBindingEvalResult = null;

    		   Expression expr = letExprVarBindingXPath.getExpression();

    		   XPathNamedFunctionReference xpathNamedFuncRef = null;
    		   if (expr instanceof XPathSequenceConstructor) {
    			   XObject xObj = ((XPathSequenceConstructor)expr).execute(xctxt);
    			   if ((xObj instanceof ResultSequence) && ((ResultSequence)xObj).size() == 1) {
    				   ResultSequence rSeq = (ResultSequence)xObj;
    				   XObject xObj2 = rSeq.item(0);
    				   if (xObj2 instanceof XPathNamedFunctionReference) {
    					   xpathNamedFuncRef = (XPathNamedFunctionReference)xObj2;  
    				   }
    			   }
    			   else {
    				   varBindingEvalResult = xObj; 
    			   }
    		   }
    		   else if (expr instanceof XPathNamedFunctionReference) {
    			   xpathNamedFuncRef = (XPathNamedFunctionReference)expr; 
    		   }
    		   else if (expr instanceof Function) {
    			   varBindingEvalResult = expr.execute(xctxt);
    		   }

    		   if (xpathNamedFuncRef != null) {
    			   String funcNamespace = xpathNamedFuncRef.getFuncNamespace();
    			   String funcLocalName = xpathNamedFuncRef.getFuncName();
    			   int concatArity = 0;
    			   short funcArity = 0;
    			   if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) && (Keywords.FUNC_CONCAT_STRING).equals(funcLocalName)) {
    				   concatArity = xpathNamedFuncRef.getConcatArity();
    				   FuncConcat funcConcat = new FuncConcat();
    				   if ((concatArity < funcConcat.getMinArity()) || (concatArity > funcConcat.getMaxArity())) {
    					   throw new TransformerException("XPTY0004 : XPath function fn:concat's arity can be between " + 
																				    							    funcConcat.getMinArity() + " and " 
																				    							    + funcConcat.getMaxArity() + ".", srcLocator); 
    				   }
    			   }
    			   else {
    				   funcArity = xpathNamedFuncRef.getArity();
    			   } 

    			   FunctionTable funcTable = xctxt.getFunctionTable();

    			   Object funcIdObj = null;    			   
    			   
    			   if ((funcNamespace == null) || ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace))) { 
    				   funcIdObj = funcTable.getFunctionIdForXSLBuiltinFuncs(funcLocalName);
    			   }
    			   else if ((XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(funcNamespace)) {    	       	   
    				   funcIdObj = funcTable.getFunctionIdForXPathBuiltinMathFuncs(funcLocalName);
    			   }
    			   else if ((XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(funcNamespace)) {    	       	   
    				   funcIdObj = funcTable.getFunctionIdForXPathBuiltinMapFuncs(funcLocalName);
    			   }
    			   else if ((XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(funcNamespace)) {     	   
    				   funcIdObj = funcTable.getFunctionIdForXPathBuiltinArrayFuncs(funcLocalName);
    			   }    			   

    			   if (funcIdObj != null) {
    				   String funcIdStr = funcIdObj.toString();
    				   Function function = funcTable.getFunction(Integer.valueOf(funcIdStr));
    				   function.setLocalName(funcLocalName);
    				   function.setNamespace(funcNamespace);        		  
    				   if (function instanceof FuncConcat) {        		     
    					   FuncConcat funcConcat = (FuncConcat)function;
    					   funcConcat.setRuntimeArgCount(concatArity);
    				   }
    				   else {
    					   function.setArity(new Short[] { funcArity });
    				   }

    				   varBindingEvalResult = new XObject(function);
    			   }
    			   else if (xpathNamedFuncRef.getXslStylesheetFunction() != null) {
    				   ElemFunction elemFunction = xpathNamedFuncRef.getXslStylesheetFunction();
    				   varBindingEvalResult = new XObject(elemFunction);
    				   varBindingEvalResult.setXslStylesheetRoot(xpathNamedFuncRef.getXslStylesheetRoot());
    			   }
    			   else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcNamespace)) {
    				   XSL3ConstructorOrExtensionFunction funcObj = new XSL3ConstructorOrExtensionFunction(funcNamespace, funcLocalName, null);
    				   funcObj.setArity(new Short[] { funcArity });
    				   varBindingEvalResult = new XObject(funcObj);        		  
    			   }
    			   else {
    				   String funcQualifiedName = "{" + funcNamespace + "}" + funcLocalName;
    				   
    				   throw new TransformerException("FODC0005 : Function definition for named function reference " + 
    						   																					     funcQualifiedName + " doesn't exist.", srcLocator);
    			   }
    		   }
    		   else if (varBindingEvalResult == null) {
    			   varBindingEvalResult = letExprVarBindingXPath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
    		   }

    		   if (varBindingEvalResult == null) {
    			   throw new TransformerException("FODC0005 : XPath 'let' expression's variable could not be bound to a non-null XDM value.", srcLocator); 
    		   }

    		   QName qNameVar = new QName(varName);
    		   
    		   m_xpathVarList.add(qNameVar);    		   
    		   qNameVarList.add(qNameVar);

    		   xpathVarMap.put(new QName(varName), varBindingEvalResult);
    	   }              

    	   if (prefixTable != null) {
    		   m_returnExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
    				                                                                                m_returnExprXPathStr, prefixTable);
    	   }

    	   XPath returnExprXpath = new XPath(m_returnExprXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

    	   if (m_vars != null) {
    		   returnExprXpath.fixupVariables(m_vars, m_globals_size);
    	   }

    	   evalResult = returnExprXpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());

    	   if (evalResult == null) {
    		   // Return an empty sequence
    		   evalResult = new ResultSequence();   
    	   }
       }
       finally {
    	   int qNameListSize = qNameVarList.size();
    	   for (int idx = 0; idx < qNameListSize; idx++) {
    		  QName qName = qNameVarList.get(idx);
    		  m_xpathVarList.remove(qName);
    		  xpathVarMap.remove(qName);
    	   }
       }
        
       return evalResult;       
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
       m_vars = (Vector)(vars.clone());
       m_globals_size = globalsSize; 
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

    public List<XPathLetExprVarBinding> getLetExprVarBindingList() {
        return m_letExprVarBindingList;
    }

    public void setLetExprVarBindingList(List<XPathLetExprVarBinding> letExprVarBindingList) {
        this.m_letExprVarBindingList = letExprVarBindingList;
    }

    public String getReturnExprXPathStr() {
        return m_returnExprXPathStr;
    }

    public void setReturnExprXPathStr(String returnExprXPathStr) {
        this.m_returnExprXPathStr = returnExprXPathStr;
    }

}
