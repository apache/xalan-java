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
package org.apache.xpath.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.XPathSequenceType;

/**
 * A class definition, that represents an XPath 3.1 inline 
 * function expression, compiled object instance.
 *  
 * @author Mukul Gandhi <mukulg@apache.org>
 *   
 * @xsl.usage advanced 
 */
public class XPathInlineFunction extends XObject {

    private static final long serialVersionUID = 9219253671212483045L;
    
    /**
     * This class field, is a list, that represents an XPath inline 
     * function expression parameters.
     */
    private List<InlineFunctionParameter> m_funcParamList = new ArrayList<InlineFunctionParameter>();
    
    /**
     * This class field, is a string value, that represents an XPath 
     * expression string for an, XPath inline function expression. This 
     * has default value "()" i.e an empty sequence, for an empty 
     * function body.
     */
    private String m_funcBodyXPathExprStr = "()";
    
    /**
     * This class field, is an object instance, that represents an
     * XPath inline function expression return type.
     */
    private XPathSequenceType m_returnType = null;
    
    /**
     * This class field, is an XPath optional, predicate string,
     * after XPath inline function expression string.
     */
    private String m_xpath_predicate_str = null;
    
    /**
     * This class field, is an XPath optional, function argument list
     * string, after XPath inline function expression string, or after
     * XPath predicate string.
     */
    private String m_func_arg_str = null;
       
    // Class field, used for Xalan-J fixupVariables action
    private Vector m_vars;
    
    // Class field, used for Xalan-J fixupVariables action
    private int m_globals_size;
    
    
    /**
     * Evaluate an, XPath inline function expression.
     * 
     * @param xctxt                      An XPath context object
     * 
     * @return                           A valid XObject
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException 
    {    	    	
    	XObject result = null;
    	
    	SourceLocator srcLocator = xctxt.getSAXLocator();

    	final int sourceNode = xctxt.getCurrentNode();
    	
    	if ((m_xpath_predicate_str == null) && (m_func_arg_str == null)) {
    	   XPathInlineFunction xpathInlineFunction = new XPathInlineFunction();
    	   
    	   xpathInlineFunction.setFuncParamList(m_funcParamList);
    	   xpathInlineFunction.setFuncBodyXPathExprStr(m_funcBodyXPathExprStr);
    	   xpathInlineFunction.setReturnType(m_returnType);
    	   
    	   result = xpathInlineFunction;
    	   
    	   return result;
    	}
    	
    	List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
    	
    	if (prefixTable != null) {
    	   m_funcBodyXPathExprStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_funcBodyXPathExprStr, prefixTable);
  	    }
    	
    	Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
    	
    	List<QName> funcVarAdded = new ArrayList<QName>();
		
		int size1 = m_funcParamList.size();
		
		XObject xctxtItemPrev = xctxt.getXPath3ContextItem();
		
		try {
			for (int idx = 0; idx < size1; idx++) {
				InlineFunctionParameter xpathInlineFuncParam = m_funcParamList.get(idx);
				String funcParamName = xpathInlineFuncParam.getParamName();

				QName qName = new QName(funcParamName);

				funcVarAdded.add(qName);
			}

			if ((m_func_arg_str != null) && (m_xpath_predicate_str == null)) {
				result = getXPathInlineFunctionResult(xctxt, srcLocator, sourceNode, xpathVarMap, size1);
			}
			else if ((m_xpath_predicate_str != null) && (m_func_arg_str != null)) {
				XPathInlineFunction xpathInlineFunction = new XPathInlineFunction();

				xpathInlineFunction.setFuncParamList(m_funcParamList);
				xpathInlineFunction.setFuncBodyXPathExprStr(m_funcBodyXPathExprStr);
				xpathInlineFunction.setReturnType(m_returnType);
				xpathInlineFunction.setXpathExprPredicateStr(m_xpath_predicate_str);
				xpathInlineFunction.setFuncArgString(m_func_arg_str);

				xctxt.setXPath3ContextItem(xpathInlineFunction);
				
				if (prefixTable != null) {
				    m_xpath_predicate_str = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_xpath_predicate_str, prefixTable);
			  	}
				
				XPath xpathObj1 = new XPath(m_xpath_predicate_str, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
				
				if (m_vars != null) {
				   xpathObj1.fixupVariables(m_vars, m_globals_size);
				}

				XObject xObj1 = xpathObj1.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
			    
				if (xObj1.bool()) {
				   result = getXPathInlineFunctionResult(xctxt, srcLocator, sourceNode, xpathVarMap, size1);
				}
				else {
				   result = new ResultSequence();
				}
			}
        }
		catch (TransformerException ex) {
			throw ex;
		}
        finally {
        	int size2 = funcVarAdded.size();
        	
        	for (int idx = 0; idx < size2; idx++) {
        	   QName qName = funcVarAdded.get(idx);
        	   xpathVarMap.remove(qName);
        	}
        	
        	xctxt.setXPath3ContextItem(xctxtItemPrev);
        }
    	
    	return result;
    }
    
    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        m_vars = (Vector)(vars.clone());
        m_globals_size = globalsSize; 
    }
    
    public int getType()
    {
        return CLASS_FUNCTION_ITEM;
    }
    
    public List<InlineFunctionParameter> getFuncParamList() {
        return m_funcParamList;
    }

    public void setFuncParamList(List<InlineFunctionParameter> funcParamList) {
        this.m_funcParamList = funcParamList;
    }

    public String getFuncBodyXPathExprStr() {
        return m_funcBodyXPathExprStr;
    }

    public void setFuncBodyXPathExprStr(String funcBodyXPathExprStr) {    	    	    	
        this.m_funcBodyXPathExprStr = funcBodyXPathExprStr;
    }
    
    public XPathSequenceType getReturnType() {
        return m_returnType;
    }

    public void setReturnType(XPathSequenceType returnType) {
        this.m_returnType = returnType;
    }

	public String getXpathExprPredicateStr() {
		return m_xpath_predicate_str;
	}

	public void setXpathExprPredicateStr(String xpathExprPredicateStr) {
		this.m_xpath_predicate_str = xpathExprPredicateStr;
	}

	public String getFuncArgString() {
		return m_func_arg_str;
	}

	public void setFuncArgString(String str1) {
		this.m_func_arg_str = str1;
	}
	
	/**
	 * Method definition, to evaluate an XPath inline function expression.
	 * 
	 * @param xctxt                                An XPath context object
	 * @param srcLocator                           An XSL transformation source locator 
	 *                                             object instance.
	 * @param sourceNode                           An XPath context node
	 * @param xpathVarMap                          An XPath context variable map
	 * @param size1                                Function arity, value
	 * @return                                     An XPath inline function evaluation result 
	 * @throws TransformerException
	 */
	private XObject getXPathInlineFunctionResult(XPathContext xctxt, SourceLocator srcLocator, int sourceNode,
			                                                                                      Map<QName, XObject> xpathVarMap, int size1) throws TransformerException {
		
		XObject result = null;		
		
		List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);
		
		if (prefixTable != null) {
			m_func_arg_str = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_func_arg_str, prefixTable);
		}
		
		XPath xpathObj1 = new XPath(m_func_arg_str, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
		   xpathObj1.fixupVariables(m_vars, m_globals_size);
		}

		XObject xObj1 = xpathObj1.execute(xctxt, sourceNode, xctxt.getNamespaceContext());

		ResultSequence rSeq = (ResultSequence)xObj1;

		int size2 = rSeq.size();

		if (size2 != size1) {
			throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 inline function call, arity error. Expected number of function arguments " 
																										                                              + size1 + ", supplied " 
																										                                              + size2 + ".", srcLocator);  
		}
		else {
			for (int idx = 0; idx < size2; idx++) {
				InlineFunctionParameter xpathInlineFuncParam = m_funcParamList.get(idx);
				String funcParamName = xpathInlineFuncParam.getParamName();

				QName qName = new QName(funcParamName);
				xpathVarMap.put(qName, rSeq.item(idx));
			}
		}			
		
		if (prefixTable != null) {
			m_funcBodyXPathExprStr = XslTransformEvaluationHelper.replaceXmlNsUrisWithPrefixesXPathStr(m_funcBodyXPathExprStr, prefixTable);
		}

		XPath xpathObj = new XPath(m_funcBodyXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
		
		if (m_vars != null) {
		   xpathObj.fixupVariables(m_vars, m_globals_size);
		}

		result = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());

		return result;
	}
    
}
