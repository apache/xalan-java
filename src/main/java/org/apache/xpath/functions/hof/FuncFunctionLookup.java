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
package org.apache.xpath.functions.hof;

import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.TemplateList;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathStaticContext;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.composite.XPathExprFuncCallExtendedArg;
import org.apache.xpath.composite.XPathNamedFunctionReference;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSQName;

/**
 * Implementation of an XPath 3.1 function fn:function-lookup.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncFunctionLookup extends Function2Args {

	private static final long serialVersionUID = 7378680517734704023L;
	
	private FunctionTable m_funcTable = null;
	
	/**
	 * Class field, representing XPath function call argument suffix 
	 * expression, within a chained function call like func1(..)(...) 
	 * where m_extended_arg represents (...).
	 */
	private Expression m_extended_arg = null;
	
	/**
	 * Class constructor.
	 */
	public FuncFunctionLookup() {
		m_arity = new Short[] { 2 };
	}
	
	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{                      

		XObject result = null;

		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		final int sourceNode = xctxt.getCurrentNode(); 
		
		m_funcTable = xctxt.getFunctionTable();
		
		XObject xObjArg0 = getFunctionArgEffectiveValue(m_arg0, xctxt);
		
		XSQName xsQName = null;
		
		if (xObjArg0 instanceof XSQName) {
			xsQName = (XSQName)xObjArg0;
		}
		else {
			throw new TransformerException("XPTY0004 : An XPath 3.1 function 'function-lookup' first argument "
					                                                                               + "is not schema type QName.", srcLocator);
		}
		
		XObject xObjArg2 = getFunctionArgEffectiveValue(m_arg1, xctxt);
		
		int arity = 0;
		
		if ((xObjArg2 instanceof XNumber) || (xObjArg2 instanceof XSNumericType)) {
		   String str1 = XslTransformEvaluationHelper.getStrVal(xObjArg2);		   
		   try {
		      arity = Integer.valueOf(str1);
		      
		      if (arity < 0) {
		    	 throw new TransformerException("XPTY0004 : An XPath 3.1 function 'function-lookup' second argument is not "
		    	 		                                                                                                   + "a non negative integer.", srcLocator);  
		      }
		   }
		   catch (NumberFormatException ex) {
			  throw new TransformerException("XPTY0004 : An XPath 3.1 function 'function-lookup' second argument is not numeric.", srcLocator);  
		   }
		}
		else {
		   throw new TransformerException("XPTY0004 : An XPath 3.1 function 'function-lookup' second argument is not numeric.", srcLocator);
		}
						
		String localName = xsQName.getLocalPart();
		
		String namespace = xsQName.getNamespaceUri();		
		if (namespace == null) {
			namespace = XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI; 	
		}		
				
		boolean xslFuncExists = false;
		
		if ((XPathStaticContext.XPATH_BUILT_IN_FUNCS_NS_URI).equals(namespace)) {
		    Object funcId = m_funcTable.getFunctionIdForXSLBuiltinFuncs(localName);
		    
		    xslFuncExists = xpathBuiltInFuncExists(funcId, arity);
		}
		else if ((XPathStaticContext.XPATH_BUILT_IN_MATH_FUNCS_NS_URI).equals(namespace)) {
		    Object funcId = m_funcTable.getFunctionIdForXPathBuiltinMathFuncs(localName);
		    
		    xslFuncExists = xpathBuiltInFuncExists(funcId, arity);
		}
		else if ((XPathStaticContext.XPATH_BUILT_IN_MAP_FUNCS_NS_URI).equals(namespace)) {
		    Object funcId = m_funcTable.getFunctionIdForXPathBuiltinMapFuncs(localName);
		    
		    xslFuncExists = xpathBuiltInFuncExists(funcId, arity);
		}
		else if ((XPathStaticContext.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI).equals(namespace)) {
		    Object funcId = m_funcTable.getFunctionIdForXPathBuiltinArrayFuncs(localName);
		    
		    xslFuncExists = xpathBuiltInFuncExists(funcId, arity);
		}
		else if ((XMLConstants.W3C_XML_SCHEMA_NS_URI).equals(namespace)) {		    
		    xslFuncExists = true;
		}
		
		if (!xslFuncExists) {
			// Trying to find whether an XSL stylesheet function exists,
			// for the function's available localname and namespace.			
	    	
	    	StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;

	    	if (stylesheetRoot != null) {
	    		TemplateList templateList = stylesheetRoot.getTemplateListComposed();
	    		ElemTemplate elemTemplate = templateList.getXslFunction(new QName(namespace, localName), arity);
	    		if (elemTemplate != null) {
	    			ElemFunction elemFunction = (ElemFunction)elemTemplate;
	    			int xslFuncDefnParamCount = elemFunction.getArity();
	    			if (xslFuncDefnParamCount == arity) {
	    				xslFuncExists = true; 
	    			}
	    		}
	    	}
		}
		
		if (xslFuncExists) {
			if (m_extended_arg == null) {
				XPathNamedFunctionReference xpathNamedFunctionRef = new XPathNamedFunctionReference();
				xpathNamedFunctionRef.setFuncName(localName);
				xpathNamedFunctionRef.setFuncNamespace(namespace);
				xpathNamedFunctionRef.setArity((short)arity);

				result = xpathNamedFunctionRef;
			}
			else {								
				List<String> argListStr = ((XPathExprFuncCallExtendedArg)m_extended_arg).getFunctionArgXPathExprStrList();
				
				String xpathStr1 = null;
				
				if (argListStr != null) {
				   StringBuffer strBuff = new StringBuffer();
				   strBuff.append("(");
				   int size1 = argListStr.size();
				   for (int idx = 0; idx < size1; idx++) {
					  String str1 = argListStr.get(idx);
					  strBuff.append(str1);
					  if (idx < (size1 - 1)) {
						 strBuff.append(","); 
					  }
				   }
				   
				   strBuff.append(")");
				   String argStr = strBuff.toString();
				   
				   xpathStr1 = namespace + ":" + localName + argStr;
				}
				else {
				   xpathStr1 = namespace + ":" + localName + "()";				   				   
				}
				
				List<XMLNSDecl> prefixTable = XslTransformEvaluationHelper.getXSLNsPrefixTable(xctxt);				
				if (prefixTable != null) {
					xpathStr1 = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(xpathStr1, prefixTable); 
				}

				XPath xpathObj = new XPath(xpathStr1, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

				try {
				   result = xpathObj.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
				}
				catch (TransformerException ex) {					
				   throw new TransformerException("XPTY0004 : An XPath expression evaluation error occured, which evaluating an "
                                                                                                           + "XPath expression " + xpathStr1 + ".", srcLocator); 
				}				
			}
		}
		else {
			result = new ResultSequence();
		}

		return result; 
	}

	/**
	 * Method definition, to check whether an XPath built-in function
	 * exists for the supplied function id and arity values.
	 * 
	 * @param funcId                          An XPath function's supplied
	 *                                        function id.
	 * @param arity                           An XPath function's supplied
	 *                                        arity.
	 * @return                                Boolean value true or false
	 * @throws TransformerException
	 */
	private boolean xpathBuiltInFuncExists(Object funcId, int arity) throws TransformerException {
		
		boolean result = false;
		
		if (funcId != null) {
		   Function function = m_funcTable.getFunction((Integer)funcId);
		   Short[] arityArr = function.getArity();
		   if (arityArr != null) {
			  for (int idx = 0; idx < arityArr.length; idx++) {
				 if (arity == arityArr[idx]) {
					 result = true;
					 
					 break;
				 }
			  }
		   }
		   else {
			  int minArity = function.getMinArity(); 
			  int maxArity = function.getMaxArity();		    	  
			  if ((arity >= minArity) && (arity <= maxArity)) {
				  result = true; 
			  }
		   }
		}
		
		return result;
	}
	
	public Expression getExtendedArg() {
		return m_extended_arg;
	}

	public void setExtendedArg(Expression expr) {
	   this.m_extended_arg = expr;		
	}

}
