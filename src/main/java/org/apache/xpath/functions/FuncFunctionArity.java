/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions;

import java.util.Arrays;
import java.util.List;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of an XPath 3.1 function fn:function-arity.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFunctionArity extends FunctionDef1Arg
{
	
	private static final long serialVersionUID = 8093916820370748865L;

	/**
	 * Class constructor;
	 */
	public FuncFunctionArity() {
	    m_arity = 1;
	}

	/**
	 * Execute the function. The function must return a valid object.
	 * 
	 * @param xctxt The current execution context.
	 * @return A valid XObject.
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		Expression arg0 = getArg0();		
		
		short funcArity = FUNC_ARITY_NOT_KNOWN;
		
		if (arg0 instanceof Variable) {
		   Variable var1 = (Variable)arg0;
		   org.apache.xpath.XPath xpathSelectExpr = (var1.getElemVariable()).getSelect();
		   Expression selectExpr = xpathSelectExpr.getExpression();
		   XObject xObj = var1.execute(xctxt);
		   if (xObj instanceof XPathNamedFunctionReference) {
			   XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)xObj;
			   
			   result = getFunctionArityXPathNamedFuncReference(xpathNamedFunctionReference, xctxt);
		   }
		   else if ((selectExpr != null) && (selectExpr instanceof NodeTest)) {
			   TransformerImpl transformerImpl = getXSLTransformerImpl();			   
			   ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression((NodeTest)selectExpr, 
					                                                                                          transformerImpl, srcLocator);
			   if (elemFunction != null) {
				   funcArity = elemFunction.getArity();
				   result = new XSInteger(String.valueOf(funcArity));
			   }
			   else {
				   NodeTest nodeTest = (NodeTest)selectExpr;
				   String funcNameRefStr = getFunctionNameRefStrValue(nodeTest); 
				   throw new TransformerException("XPST0017 : An XSL function definition for function reference " + funcNameRefStr 
						                                                                                          + " not found.", srcLocator);  
			   }
		   }
		   if (xObj instanceof XPathInlineFunction) {
			   XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)xObj;
			   List<InlineFunctionParameter> funcParamList = xpathInlineFunction.getFuncParamList();
			   result = new XSInteger(String.valueOf(funcParamList.size()));
		   }
		}
		else if (arg0 instanceof XPathNamedFunctionReference) {
		   XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)arg0;
		   
		   result = getFunctionArityXPathNamedFuncReference(xpathNamedFunctionReference, xctxt);
		}
		else if (arg0 instanceof NodeTest) {
			TransformerImpl transformerImpl = getXSLTransformerImpl();			
			ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression((NodeTest)arg0, 
					                                                                                        transformerImpl, srcLocator);			
			if (elemFunction != null) {
			   funcArity = elemFunction.getArity();
			   result = new XSInteger(String.valueOf(funcArity));
			}
			else {
			   NodeTest nodeTest = (NodeTest)arg0;
			   String funcNameRefStr = getFunctionNameRefStrValue(nodeTest);  
			   throw new TransformerException("XPST0017 : An XSL function definition for function reference " + funcNameRefStr 
					                                                                                          + " not found.", srcLocator); 
			}
		}
		else if (arg0 instanceof XPathInlineFunction) {
			XPathInlineFunction xpathInlineFunction = (XPathInlineFunction)arg0;
			List<InlineFunctionParameter> funcParamList = xpathInlineFunction.getFuncParamList();
			result = new XSInteger(String.valueOf(funcParamList.size()));
		}
		
		return result;
	}
    
	/**
	 * Method definition to get an XSL transformation's TransformerImpl 
	 * object instance within the current XSL transformation context. 
	 * 
	 * @return						TransformerImpl object instance
	 */
	private TransformerImpl getXSLTransformerImpl() {		
		
		TransformerImpl result = null;

		ExpressionNode exprNode = getExpressionOwner();
		ExpressionNode stylesheetRootNode = null;
		while (exprNode != null) {
			stylesheetRootNode = exprNode;
			exprNode = exprNode.exprGetParent();                     
		}

		StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
		result = stylesheetRoot.getTransformerImpl();
		
		return result;
	}
	
	/**
	 * Method definition to get, an XPath function's arity for named function reference.
	 * 
	 * @param xpathNamedFunctionReference						An XPathNamedFunctionReference object
	 * @param xctxt												An XPath context object
	 * @return													An XPath function's arity value
	 * @throws TransformerException
	 */
	private XObject getFunctionArityXPathNamedFuncReference(XPathNamedFunctionReference xpathNamedFunctionReference, 
			                                                                                               XPathContext xctxt) throws TransformerException {
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		String funcName = xpathNamedFunctionReference.getFuncName();
		String funcNamespace = xpathNamedFunctionReference.getFuncNamespace();			   
		if ((FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI).equals(funcNamespace) || (FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(funcNamespace) ||
				                                                                 (FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(funcNamespace) || 
				                                                                 (FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(funcNamespace)) {				   
			XSL3FunctionService xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
			Function function = xsl3FunctionService.getXPathBuiltInFunction(funcName, funcNamespace, xctxt);
			
			/**
			 * This is an XPath function's arity value, computed mainly during
			 * XPath parse of function expression specified within an XSL
			 * stylesheet or an XPath literal expression. This arity value is
			 * further verified below with an XPath function's actual arity value.
			 */
			final Short funcArity = xpathNamedFunctionReference.getArity();
			
			short funcActualArity = FUNC_ARITY_NOT_KNOWN; 
			Short[] arityArr = function.getArityArray();
			if (arityArr != null) {
			   List<Short> list1 = Arrays.asList(arityArr);
			   if (list1.contains(funcArity)) {
				  funcActualArity = funcArity;
				  result = new XSInteger(String.valueOf(funcActualArity));
			   }
			   else {
				  String funcNameRefStr = "{" + funcNamespace + "}" + funcName + "#" + funcArity; 
				  throw new TransformerException("XPST0017 : An XSL function definition for function reference " + funcNameRefStr 
																												 + " not found.", srcLocator); 
			   }
			}
			else {
				funcActualArity = function.getArity();
				if ((funcArity != FUNC_ARITY_NOT_KNOWN) && (funcActualArity == funcArity)) {
					result = new XSInteger(String.valueOf(funcActualArity)); 
				}
				else {
					String funcNameRefStr = "{" + funcNamespace + "}" + funcName + "#" + funcArity; 
					throw new TransformerException("XPST0017 : An XSL function definition for function reference " + funcNameRefStr 
																												   + " not found.", srcLocator);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Method definition to get, a string value representing name 
	 * of an XPath NodeTest object reference.  
	 * 
	 * @param nodeTest					An XPath NodeTest object reference
	 * @return							String value representing name of
	 *                                  an XPath NodeTest object reference.	
	 */
	private String getFunctionNameRefStrValue(NodeTest nodeTest) {
		
		String result = null;
		
		String localName = nodeTest.getLocalName();
		String nsUri = nodeTest.getNamespace();
		int hashCharIdx = localName.indexOf('#');
		QName qName = null;
		if (hashCharIdx != -1) {
			qName = new QName(nsUri, localName.substring(0, hashCharIdx));
		}
		else {
			qName = new QName(nsUri, localName); 
		}
		int arity = FUNC_ARITY_NOT_KNOWN;
		if (hashCharIdx != -1) {
			arity = Integer.valueOf(localName.substring(hashCharIdx + 1));
		}
		
		result = qName.toString() + ((arity != FUNC_ARITY_NOT_KNOWN) ? ("#" + arity) : "");
		
		return result;
	}
}
