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
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.patterns.NodeTest;

import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of an XPath 3.1 function fn:function-name.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFunctionName extends FunctionDef1Arg
{
	
	private static final long serialVersionUID = -356026092579370687L;

	/**
	 * Class constructor.
	 */
	public FuncFunctionName() {
		m_defined_arity = new Short[] { 1 };
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
		
		Expression arg0 = getArg0();								
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		FunctionTable funcTable = xctxt.getFunctionTable();
		
		if (arg0 instanceof Variable) {
			Variable var1 = (Variable)arg0;
			Expression selectExpr = null;
			ElemVariable elemVariable = var1.getElemVariable();
			if (elemVariable != null) {
			   org.apache.xpath.XPath xpathSelectExpr = elemVariable.getSelect();
			   selectExpr = xpathSelectExpr.getExpression();
			}
			else {
			   Map<QName,XObject> xpathVarMap = xctxt.getXPathVarMap();
			   QName varQName = var1.getQName();
			   XObject varValue = xpathVarMap.get(varQName);
			   if (varValue != null) {
				   Object obj1 = varValue.object();				   
				   if (obj1 instanceof XSL3ConstructorOrExtensionFunction) {
					   XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)obj1;
					   String localName = xsl3ConstructorOrExtensionFunction.getFunctionName();
					   String namespace = xsl3ConstructorOrExtensionFunction.getNamespace();
					   
					   result = new XSQName(null, localName, namespace);

					   return result;
				   }
				   else if (obj1 instanceof Function) {
					   Function func1 = (Function)obj1;
					   String localName = func1.getLocalName();
					   String namespace = func1.getNamespace();
					   
					   result = new XSQName(null, localName, namespace);

					   return result;
				   }
			   }
			}
			
			XObject xObj = var1.execute(xctxt);
			if (xObj instanceof XPathNamedFunctionReference) {
			   XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)xObj;
			   
			   result = getFunctionNameFromNamedFuncRef(xpathNamedFunctionReference, funcTable, srcLocator);
			}
			else if ((selectExpr != null) && (selectExpr instanceof NodeTest)) {
			   NodeTest nodeTest = (NodeTest)selectExpr;
				
			   result = getFunctionNameFromNodeTestExpr(nodeTest, srcLocator);
			}
			else if (xObj instanceof XPathInlineFunction) {
			   result = new ResultSequence();
			}
			else {
			   throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for the supplied function "
					   																					 + "reference not found.", srcLocator);
			}
		}
		else if (arg0 instanceof XPathNamedFunctionReference) {
			XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)arg0;
			
			result = getFunctionNameFromNamedFuncRef(xpathNamedFunctionReference, funcTable, srcLocator);			   			   
		}
		else if (arg0 instanceof NodeTest) {
			NodeTest nodeTest = (NodeTest)arg0;

			result = getFunctionNameFromNodeTestExpr(nodeTest, srcLocator);
		}		
		else if (arg0 instanceof XSL3ConstructorOrExtensionFunction) {
			XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)arg0;
			XObject xObj = xsl3ConstructorOrExtensionFunction.execute(xctxt);
			if (xObj instanceof XPathNamedFunctionReference) {
				XPathNamedFunctionReference xpathNamedFunctionReference = (XPathNamedFunctionReference)xObj;
				
				result = getFunctionNameFromNamedFuncRef(xpathNamedFunctionReference, funcTable, srcLocator);
			}
			else if (xObj instanceof XPathInlineFunction) {
			    result = new ResultSequence();
			}
		}
		else if (arg0 instanceof XPathInlineFunction) {
			result = new ResultSequence();
		}
		else {
			throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for the supplied function "
																												+ "reference not found.", srcLocator); 
		}

		return result;
	}

	/**
	 * Method definition to get an XSL function's name, using the 
	 * supplied XPath named function reference.
	 * 
	 * @param xpathNamedFunctionReference			An XPath named function reference 
	 *                                              run-time object.
	 * @param funcTable								An XPath function table object
	 * @param srcLocator							An XSL transformation SourceLocator run-time object 
	 * @return										An XSL function's name as xs:QName value
	 * @throws TransformerException
	 */
	private XObject getFunctionNameFromNamedFuncRef(XPathNamedFunctionReference xpathNamedFunctionReference, 
			                                        FunctionTable funcTable, SourceLocator srcLocator) throws TransformerException {
		
		XObject result = null;
		
		String localName = xpathNamedFunctionReference.getFuncName();
		String namespace = xpathNamedFunctionReference.getFuncNamespace();
		String prefix = null;
		Short arity = xpathNamedFunctionReference.getArity();
		if ((FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI).equals(namespace)) {
			prefix = "fn";
			Object funcId = funcTable.getFunctionId(localName);
			if (funcId != null) {
				Function function = funcTable.getFunction(Integer.valueOf(funcId.toString()));
				Short[] definedArity = function.getDefinedArity();
				List<Short> arityList = Arrays.asList(definedArity);
				if (!arityList.contains(arity)) {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									  + "reference {" + namespace + "}" + localName + "#" 
																									  + arity + " not found.", srcLocator); 
				}
			}
			if (funcId == null) {
				throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																								      + "reference {" + namespace + "}" + localName + "#" 
																								      + arity + " not found.", srcLocator);  
			}

			result = new XSQName(prefix, localName, namespace);
		}
		else if ((FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(namespace)) {
			prefix = "math";
			Object funcId = funcTable.getFunctionId(localName);
			if (funcId != null) {
				Function function = funcTable.getFunction(Integer.valueOf(funcId.toString()));
				Short[] definedArity = function.getDefinedArity();
				List<Short> arityList = Arrays.asList(definedArity);
				if (!arityList.contains(arity)) {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									 + "reference {" + namespace + "}" + localName + "#" 
																									 + arity + " not found.", srcLocator); 
				}
			}
			if (funcId == null) {
				throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									 + "reference {" + namespace + "}" + localName + "#" 
																									 + arity + " not found.", srcLocator);  
			}

			result = new XSQName(prefix, localName, namespace);
		}
		else if ((FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(namespace)) {
			prefix = "map";
			Object funcId = funcTable.getFunctionId(localName);
			if (funcId != null) {
				Function function = funcTable.getFunction(Integer.valueOf(funcId.toString()));
				Short[] definedArity = function.getDefinedArity();
				List<Short> arityList = Arrays.asList(definedArity);
				if (!arityList.contains(arity)) {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator); 
				}
			}
			if (funcId == null) {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator);  
			}

			result = new XSQName(prefix, localName, namespace);
		}
		else if ((FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(namespace)) {
			prefix = "array";
			Object funcId = funcTable.getFunctionId(localName);
			if (funcId != null) {
				Function function = funcTable.getFunction(Integer.valueOf(funcId.toString()));
				Short[] definedArity = function.getDefinedArity();
				List<Short> arityList = Arrays.asList(definedArity);
				if (!arityList.contains(arity)) {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator); 
				}
			}
			if (funcId == null) {
				throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator);  
			}

			result = new XSQName(prefix, localName, namespace);
		}
		else if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(namespace)) {
			prefix = "xs";            	   
			XSL3FunctionService xsl3FunctionService = XSLFunctionBuilder.getXSLFunctionService();
			if (xsl3FunctionService.isXmlSchemaBuiltInAtomicTypeName(localName)) {
				if (arity != 1) {
					throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator);
				}
			}
			else {
				throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator); 
			}

			result = new XSQName(prefix, localName, namespace);
		}
		else {
			result = new XSQName(prefix, localName, namespace);
		}
		
		return result;
	}
	
	/**
	 * Method definition to get an XSL function's name, using the
	 * supplied Xalan-J NodeTest object. 
	 * 
	 * @param nodeTest							Xalan-J NodeTest object
	 * @param srcLocator						An XSL transformation SourceLocator run-time object
	 * @return                                  An XSL function's name as xs:QName value
	 * @throws TransformerException
	 */
	private XObject getFunctionNameFromNodeTestExpr(NodeTest nodeTest, SourceLocator srcLocator) 
			                                                                                throws TransformerException {

		XObject result = null;

		ExpressionNode expressionNode = nodeTest.getExpressionOwner();
		ExpressionNode stylesheetRootNode = null;
		while (expressionNode != null) {
			stylesheetRootNode = expressionNode;
			expressionNode = expressionNode.exprGetParent();                     
		}

		StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode; 
		ElemFunction elemFunction = XslTransformEvaluationHelper.getElemFunctionFromNodeTestExpression(
																									nodeTest, stylesheetRoot.getTransformerImpl(), 
																									srcLocator);
		String funcNameRef = nodeTest.getLocalName();
		String namespace = nodeTest.getNamespace();
		int hashCharIdx = funcNameRef.indexOf('#');
		String localName = funcNameRef.substring(0, hashCharIdx);
		int arity = Integer.valueOf(funcNameRef.substring(hashCharIdx + 1));

		if (elemFunction != null) {
			QName xslFuncName = elemFunction.getName();
			result = new XSQName(xslFuncName.getPrefix(), localName, namespace);
		}
		else {
			throw new javax.xml.transform.TransformerException("XPST0017 : An XPath function definition for function "
																									+ "reference {" + namespace + "}" + localName + "#" 
																									+ arity + " not found.", srcLocator);
		}

		return result;
    }
	
}
