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
package org.apache.xpath.operations;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.objects.XObject;

/**
 * An XPath 3.1 arrow operator, "=>" implementation. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ArrowOp extends Operation
{

	private static final long serialVersionUID = 4577709194385888770L;
	
	/**
	 * Class field, to represent the suffix of a chained XPath 3.1 
	 * arrow expression. For example, with an XPath expression 
	 * a=>func1()=>func2() the value of this class field shall be =>func2().
	 */
	private java.lang.String m_xpath_arrowOpRemainingExprStr;

	/**
	 * Evaluate an XPath arrow operator, and return the result.
	 *
	 * @param left non-null reference to the evaluated left operand
	 * @param right non-null reference to the evaluated right operand
	 *
	 * @return non-null reference to the XObject that represents the result of the operation
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
      XObject result = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      if (m_right instanceof XPathDynamicFunctionCall) {
    	 XPathDynamicFunctionCall dfc = (XPathDynamicFunctionCall)m_right;    	     	 
    	 
    	 Expression lArg = m_left;
    	 XObject lArgObj = lArg.execute(xctxt);
    	 dfc.setArg0(lArgObj);
    	 
    	 result = dfc.execute(xctxt);
      }
      else if (m_right instanceof XSL3ConstructorOrExtensionFunction) {
    	 XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)m_right;
    	 Vector argVector = xsl3ConstructorOrExtensionFunction.getArgVector();
    	 
    	 Expression lArg = m_left;
    	 XObject lArgObj = lArg.execute(xctxt);
    	 argVector.add(0, lArgObj);
    	 
    	 result = xsl3ConstructorOrExtensionFunction.execute(xctxt);
      }
      else {
    	  Function function = (Function)m_right;

    	  if (function instanceof Function3Args) {
    		  Function3Args funcThreeArgs = (Function3Args)function;
    		  Expression arg0 = funcThreeArgs.getArg0();
    		  Expression arg1 = funcThreeArgs.getArg1();
    		  funcThreeArgs.setArg0(m_left);

    		  try {
    			  if (arg0 != null) {
    				  funcThreeArgs.setArg(arg0, 1);
    			  }
    			  
    			  if (arg1 != null) {
    				  funcThreeArgs.setArg(arg1, 2);
    			  }
    		  }
    		  catch (WrongNumberArgsException ex) {
    			  throw new javax.xml.transform.TransformerException("FORX0003 : An error occured, during evaluation for "
    					  																							+ "XPath 3.1 operator =>.", srcLocator);
    		  }

    		  result = funcThreeArgs.execute(xctxt);
    	  }
    	  else if (function instanceof Function2Args) {
    		  Function2Args funcTwoArgs = (Function2Args)function;
    		  Expression arg1 = funcTwoArgs.getArg1();

    		  if (arg1 != null) {
    			  throw new javax.xml.transform.TransformerException("FORX0003 : The function's second argument cannot be provided "
    					                                                                            + "for an XPath function of arity 2 with evaluation using XPath 3.1 "
    					                                                                            + "operator =>.", srcLocator); 
    		  }
    		  else {     		
    			  Expression arg0 = funcTwoArgs.getArg0();
    			  funcTwoArgs.setArg0(m_left);
    			  try {     		   
    				  funcTwoArgs.setArg(arg0, 1);
    			  } 
    			  catch (WrongNumberArgsException ex) {
    				  throw new javax.xml.transform.TransformerException("FORX0003 : An error occured, during evaluation for "
    						                                                                        + "XPath 3.1 operator =>.", srcLocator);
    			  }
    			  
    			  result = funcTwoArgs.execute(xctxt);
    		  }
    	  }
    	  else if (function instanceof FunctionOneArg) {
    		  FunctionOneArg funcOneArg = (FunctionOneArg)function;
    		  Expression arg0 = funcOneArg.getArg0();

    		  if (arg0 != null) {
    			  throw new javax.xml.transform.TransformerException("FORX0003 : The function's first argument cannot be provided "
    					                                                                           + "for an XPath function of arity one with evaluation using "
    					                                                                           + "XPath 3.1 operator =>.", srcLocator); 
    		  }
    		  else {
    			  funcOneArg.setArg0(m_left);
    			  result = funcOneArg.execute(xctxt);
    		  }
    	  }    	  
      }
      
      if (m_xpath_arrowOpRemainingExprStr != null) {         
		  result = getXPathArrowOpFinalResult(result, m_xpath_arrowOpRemainingExprStr, xctxt);
	  }
      
      return result;
    }

    public java.lang.String getArrowOpRemainingXPathExprStr() {
	   return m_xpath_arrowOpRemainingExprStr;
    }

    public void setArrowOpRemainingXPathExprStr(java.lang.String arrowOpRemainingXPathExprStr) {
	   this.m_xpath_arrowOpRemainingExprStr = arrowOpRemainingXPathExprStr;
    }
    
    /**
     * Method definition, to handle more than one occurrence of an XPath 
     * arrow operator, "=>" within an XPath expression.  
     * 
     * @param prevResult                               Partial previous result of
     *                                                 evaluation
     * @param arrowOpRemainingXPathExprStr             An XPath remaining expression string
     *                                                 of the form =>...
     * @param xctxt                                    An XPath context object
     * @return                                         The result of XPath expression evaluation
     * @throws TransformerException
     */
    private XObject getXPathArrowOpFinalResult(XObject prevResult, java.lang.String arrowOpRemainingXPathExprStr, 
    		                                                                          XPathContext xctxt) throws TransformerException {
       XObject result = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator(); 
       
       int idx = arrowOpRemainingXPathExprStr.indexOf("=>");
       java.lang.String arrowOpNextStr = arrowOpRemainingXPathExprStr.substring(idx + 2);
       
       int idx3 = arrowOpNextStr.indexOf("=>");
       java.lang.String arrowOpRemainingXPathExprStr2 = null;
       if (idx3 != -1) {
    	  arrowOpRemainingXPathExprStr2 = arrowOpNextStr.substring(idx3);  
       }
       
       int idx2 = arrowOpNextStr.indexOf('(');
       java.lang.String str1 = arrowOpNextStr.substring(0, idx2 + 1) + "''";
       java.lang.String a1 = arrowOpNextStr.substring(idx2 + 1);
       if (a1.startsWith(")")) {
    	   str1 = str1 + ")";
       }
       else {
    	  str1 = str1 + "," + arrowOpNextStr.substring(idx2 + 1);
       }
       
       arrowOpNextStr = str1;
       
       List<XMLNSDecl> prefixTable = null;       
       if (XslTransformData.m_stylesheetRoot != null) {
    	   prefixTable = (XslTransformData.m_stylesheetRoot).getPrefixTable();  
       }
       else {
    	   ExpressionNode exprNode = getExpressionOwner();
    	   ElemTemplateElement elemTemplateElement = (ElemTemplateElement)exprNode;
    	   prefixTable = elemTemplateElement.getPrefixTable(); 
       }
       
       arrowOpNextStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(arrowOpNextStr, prefixTable);
       
       XPath xpathObj = new XPath(arrowOpNextStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
       Expression expr1 = xpathObj.getExpression();
       
       if (expr1 instanceof XPathDynamicFunctionCall) {
    	   XPathDynamicFunctionCall dfc = (XPathDynamicFunctionCall)expr1;    	     	 
    	   dfc.setArg0(prevResult);
    	   
    	   result = dfc.execute(xctxt);
       }
       else if (expr1 instanceof XSL3ConstructorOrExtensionFunction) {
    	   XSL3ConstructorOrExtensionFunction xsl3ConstructorOrExtensionFunction = (XSL3ConstructorOrExtensionFunction)expr1;
    	   Vector argVector = xsl3ConstructorOrExtensionFunction.getArgVector();

    	   argVector.add(0, prevResult);

    	   result = xsl3ConstructorOrExtensionFunction.execute(xctxt);
       }
       else {
    	   Function function = (Function)expr1;

    	   if (function instanceof Function3Args) {
    		   Function3Args funcThreeArgs = (Function3Args)function;    		   
    		   funcThreeArgs.setArg0(prevResult);    		   
    		   result = funcThreeArgs.execute(xctxt);
    	   }
    	   else if (function instanceof Function2Args) {
    		   Function2Args funcTwoArgs = (Function2Args)function;    		   
    		   funcTwoArgs.setArg0(prevResult);
    			   
    		   result = funcTwoArgs.execute(xctxt);
    	   }
    	   else if (function instanceof FunctionOneArg) {
    		   FunctionOneArg funcOneArg = (FunctionOneArg)function;
    		   funcOneArg.setArg0(prevResult);
    		   
    		   result = funcOneArg.execute(xctxt);
    	   }

    	   if (arrowOpRemainingXPathExprStr2 != null) {         
    		   result = getXPathArrowOpFinalResult(result, arrowOpRemainingXPathExprStr2, xctxt);
    	   }
       }
       
       return result;
    }
    
}
