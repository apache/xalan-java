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
import org.apache.xalan.xslt.util.StringUtil;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
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
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathInlineFunctionParameter;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.operations.XPathRange;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSInteger;

/**
 * Class definition, to implement an XPath 3.1 expression 'let'.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathLetExpr extends Expression {

    private static final long serialVersionUID = 3063682088023616108L;

    /**
     * An XPath 3.1 'let' expression's variable binding list.
     */
    private List<XPathLetExprVarBinding> m_letExprVarBindingList = new ArrayList<XPathLetExprVarBinding>();
    
    /**
     * An XPath 3.1 'let' expression's 'return' clause, 
     * XPath expression string.
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
    		   
    		   varResultXPathExprStr = StringUtil.xmlNsSeparatorWhitespaceNormalization(varResultXPathExprStr);
    		   varResultXPathExprStr = varResultXPathExprStr.replace(": =", ":=");

    		   if (prefixTable != null) {
    			   varResultXPathExprStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(varResultXPathExprStr, prefixTable);
    		   }

    		   XPath letExprVarBindingXPath = new XPath(varResultXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    		   
    		   if (m_vars != null) {
    			   letExprVarBindingXPath.fixupVariables(m_vars, m_globals_size);
    		   }

    		   XObject varBindingEvalResult = null;

    		   Expression expr1 = letExprVarBindingXPath.getExpression();

    		   XPathNamedFunctionReference xpathNamedFuncRef = null;
    		   
    		   if (expr1 instanceof XPathSequenceConstructor) {
    			   XObject xObj = ((XPathSequenceConstructor)expr1).execute(xctxt);
    			   
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
    		   else if (expr1 instanceof XPathNamedFunctionReference) {
    			   xpathNamedFuncRef = (XPathNamedFunctionReference)expr1; 
    		   }    		   
    		   else if (expr1 instanceof Function) {
    			   varBindingEvalResult = expr1.execute(xctxt);
    		   }
    		   
    		   if (expr1 instanceof XPathInlineFunction) {
    			   xpathInlineFunctionTypeCheck(expr1, xctxt, prefixTable, srcLocator);
    		   }

    		   if (xpathNamedFuncRef != null) {
    			   String funcNamespace = xpathNamedFuncRef.getFuncNamespace();
    			   String funcLocalName = xpathNamedFuncRef.getFuncName();
    			       			   
    			   short funcArity = 0;
    			   int fnConcatArity = 0;
    			   
    			   if (!((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) && (Keywords.FUNC_CONCAT_STRING).equals(funcLocalName))) {
    				   funcArity = xpathNamedFuncRef.getArity();
    			   }
    			   else {
    				   // An XPathNamedFunctionReference object instance is referring 
    				   // to, XPath function fn:concat.
    				   
                       fnConcatArity = xpathNamedFuncRef.getConcatArity();
    				   
    				   FuncConcat funcConcat = new FuncConcat();
    				   
    				   if ((fnConcatArity < funcConcat.getMinArity()) || (fnConcatArity > funcConcat.getMaxArity())) {
    					   throw new TransformerException("XPTY0004 : An XPath function 'concat' Xalan-J arity limit is between " + 
																				    							                  funcConcat.getMinArity() + " and " 
																				    							                  + funcConcat.getMaxArity() + ".", srcLocator); 
    				   }
    			   } 

    			   FunctionTable funcTable = xctxt.getFunctionTable();

    			   Object funcIdObj = null;    			   
    			   
    			   if ((funcNamespace == null) || ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace))) { 
    				   funcIdObj = funcTable.getFunctionIdForXSLBuiltinFunc(funcLocalName);
    			   }
    			   else if ((XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(funcNamespace)) {    	       	   
    				   funcIdObj = funcTable.getFunctionIdForXPathBuiltinMathFunc(funcLocalName);
    			   }
    			   else if ((XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(funcNamespace)) {    	       	   
    				   funcIdObj = funcTable.getFunctionIdForXPathBuiltinMapFunc(funcLocalName);
    			   }
    			   else if ((XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(funcNamespace)) {     	   
    				   funcIdObj = funcTable.getFunctionIdForXPathBuiltinArrayFunc(funcLocalName);
    			   }    			   

    			   if (funcIdObj != null) {
    				   String funcIdStr = funcIdObj.toString();
    				   
    				   Function function = funcTable.getFunction(Integer.valueOf(funcIdStr));
    				   
    				   function.setLocalName(funcLocalName);
    				   function.setNamespace(funcNamespace);        		  
    				   
    				   if (function instanceof FuncConcat) {        		     
    					   FuncConcat funcConcat = (FuncConcat)function;
    					   funcConcat.setFuncArgCount(fnConcatArity);
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
    				   
    				   throw new TransformerException("FODC0005 : An XSL function definition for named function reference " + 
    						   																					            funcQualifiedName + " doesn't exist.", srcLocator);
    			   }
    		   }
    		   else if (varBindingEvalResult == null) {
    			   varBindingEvalResult = letExprVarBindingXPath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
    		   }

    		   if (varBindingEvalResult == null) {
    			   throw new TransformerException("FODC0005 : An XPath 3.1 'let' expression variable binding value doesn't exist.", srcLocator); 
    		   }

    		   QName qNameVar = new QName(varName);
    		   
    		   m_xpathVarList.add(qNameVar);    		   
    		   qNameVarList.add(qNameVar);

    		   xpathVarMap.put(new QName(varName), varBindingEvalResult);
    	   }              

    	   if (prefixTable != null) {
    		   m_returnExprXPathStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(
    				                                                                                m_returnExprXPathStr, prefixTable);
    	   }

    	   XPath returnExprXpath = new XPath(m_returnExprXPathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

    	   if (m_vars != null) {
    		   returnExprXpath.fixupVariables(m_vars, m_globals_size);
    	   }

    	   evalResult = returnExprXpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
    	   
    	   if (evalResult instanceof XMLNodeCursorImpl) {
    		   XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)evalResult;
    		   DTMCursorIterator iter1 = xmlNodeCursorImpl.iter();
    		       		   			   
			   ResultSequence rSeq = new ResultSequence();
			   
			   int nextNode = DTM.NULL;
			   
			   List<Integer> nodeHandleList = new ArrayList<Integer>();
			   
			   int count = 0;
    		   
    		   while ((nextNode = iter1.nextNode()) != DTM.NULL) {
    			   DTM dtm = xctxt.getDTM(nextNode);    			  
    			   String nodeName = dtm.getNodeName(nextNode);
    			   
    			   count++;

    			   if (StringUtil.isXdmNodeShallowSynthetic(nodeName)) {
    				   Node node = dtm.getNode(nextNode);
    				   String str1 = node.getTextContent();

    				   try {
    					   double dbl = Double.valueOf(str1);    					
    					   int int1 = (int)dbl;

    					   if (int1 == dbl) {
    						   rSeq.add(new XSInteger(str1));
    					   }
    					   else {
    						   rSeq.add(new XSDouble(str1));
    					   }
    				   }
    				   catch (NumberFormatException ex) {
    					   // No op 
    				   }
    			   }
    			   else {
    				   nodeHandleList.add(nextNode);
    			   }
    		   }
    		   
    		   if (rSeq.size() == count) {    			  
    			  evalResult = rSeq;
    		   }
    		   else if (nodeHandleList.size() == count) {
    			  evalResult = new XMLNodeCursorImpl(nodeHandleList, xctxt); 
    		   }
    		   else {
    			  evalResult = returnExprXpath.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
    		   }
    	   }

    	   if (evalResult == null) {
    		   evalResult = new ResultSequence();   
    	   }
       }
       catch (TransformerException ex) {
    	   throw ex;
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
       // No op
    }

    @Override
    public boolean deepEquals(Expression expr) {   	
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
    
    /**
     * Method definition, to do XPath 3.1 'inline function' 
     * expression body type checking. This method does little
     * bit of XPath expression type checking.
     * 
     * @param expr1                        An XPath 3.1 'inline function' 
     *                                     expression body's compiled expression.
     * @param xctxt                        An XPath context object instance
     * @param prefixTable                  An XSL namespace prefix table, object
     *                                     instance
     * @param srcLocator                   An XSL SourceLocator object instance 
     * @throws TransformerException
     */
    private void xpathInlineFunctionTypeCheck(Expression expr1, XPathContext xctxt, List<XMLNSDecl> prefixTable, 
    		                                                                                       SourceLocator srcLocator) throws TransformerException {

    	XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)expr1;
    	String xpathInlineFuncBodyStr = xpathInlineFunction.getFuncBodyXPathExprStr();
    	
    	xpathInlineFuncBodyStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(xpathInlineFuncBodyStr, prefixTable);

    	XPath xpathObj = new XPath(xpathInlineFuncBodyStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

    	if (m_vars != null) {
    		xpathObj.fixupVariables(m_vars, m_globals_size);
    	}

    	Expression expr2 = xpathObj.getExpression();

    	if (expr2 instanceof XPathRange) {
    		XPathRange xpathRange = (XPathRange)expr2;
    		Expression expr_a = xpathRange.getLeftOperand();
    		Expression expr_b = xpathRange.getRightOperand();

    		String str1 = null;

    		if (expr_a instanceof Variable) {
    			str1 = (((Variable)expr_a).getQName()).getLocalName(); 
    		}

    		if (expr_b instanceof Variable) {
    			str1 = (((Variable)expr_b).getQName()).getLocalName();
    		}

    		List<XPathInlineFunctionParameter> list1 = xpathInlineFunction.getFuncParamList();
    		int size2 = list1.size();

    		for (int idx2 = 0; idx2 < size2; idx2++) {
    			XPathInlineFunctionParameter xpathInlineFuncParam = list1.get(idx2);
    			String paramName = xpathInlineFuncParam.getParamName();
    			XPathSequenceType paramType = xpathInlineFuncParam.getParamType();

    			if ((str1 != null) && str1.equals(paramName)) {
    				int xsTypeId = paramType.getBuiltInSequenceType();

    				if ((xsTypeId == XPathSequenceTypeSupport.XS_DOUBLE) || 
    						                                            (xsTypeId == XPathSequenceTypeSupport.XS_FLOAT)) {
    					throw new TransformerException("XPTY0004 : An XPath 3.1 range 'to' operator's operand cannot be with XML Schema "
																							    							            + "type 'double'/'float'. The required XML schema "
																							    							            + "type is 'integer'.", srcLocator); 
    				}
    			}
    		}
    	}
    }

}
