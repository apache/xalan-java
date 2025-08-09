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

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.Function3Args;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of XPath 3.1 arrow operator, "=>". 
 * 
 * Ref : https://www.w3.org/TR/xpath-31/#id-arrow-operator
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ArrowOp extends Operation
{

	private static final long serialVersionUID = 4577709194385888770L;
	
	private java.lang.String fArrowOpRemainingXPathExprStr;

   /**
   * Evaluate an XPath arrow operator, and return the result.
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws javax.xml.transform.TransformerException
   */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
      XObject result = null;
      
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
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
  		    		                                                                      + "operator =>.", srcLocator);
	     }
     	 
     	 result = funcThreeArgs.execute(xctxt);
      }
      else if (function instanceof Function2Args) {
    	 Function2Args funcTwoArgs = (Function2Args)function;
    	 Expression arg1 = funcTwoArgs.getArg1();
    	 
    	 if (arg1 != null) {
     	    throw new javax.xml.transform.TransformerException("FORX0003 : The function's 2nd argument cannot be provided "
     	    		                                                   + "lexically for an XPath function of arity 2 with evaluation using "
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
     		   		                                                      + "operator =>.", srcLocator);
			}
     		result = funcTwoArgs.execute(xctxt);
     	 }
      }
      else if (function instanceof FunctionOneArg) {
     	 FunctionOneArg funcOneArg = (FunctionOneArg)function;
     	 Expression arg0 = funcOneArg.getArg0();
     	 
     	 if (arg0 != null) {
     	    throw new javax.xml.transform.TransformerException("FORX0003 : The function's 1st argument cannot be provided lexically "
     	    		                                                   + "for an XPath function of arity 1 with evaluation using "
     	    		                                                   + "operator =>.", srcLocator); 
     	 }
     	 else {
     		funcOneArg.setArg0(m_left);
     		result = funcOneArg.execute(xctxt);
     	 }
      }
      
      if (fArrowOpRemainingXPathExprStr != null) {         
         result = getFinalResult(result, fArrowOpRemainingXPathExprStr, xctxt);
      }
      
      return result;
    }

    public java.lang.String getArrowOpRemainingXPathExprStr() {
	   return fArrowOpRemainingXPathExprStr;
    }

    public void setArrowOpRemainingXPathExprStr(java.lang.String arrowOpRemainingXPathExprStr) {
	   this.fArrowOpRemainingXPathExprStr = arrowOpRemainingXPathExprStr;
    }
    
    /**
     * Within an XPath expression, this method handles more than one occurrence of an 
     * XPath arrow operator, "=>". 
     */
    private XObject getFinalResult(XObject prevResult, java.lang.String arrowOpRemainingXPathExprStr, 
    		                                                                          XPathContext xctxt) throws TransformerException {
       XObject result = null;
       
       SourceLocator srcLocator = xctxt.getSAXLocator(); 
       
       java.lang.String resultStr = XslTransformEvaluationHelper.getStrVal(prevResult); 
  	   java.lang.String xpathStr = "'" + resultStr + "'" + arrowOpRemainingXPathExprStr;
  	   XPath xpath = new XPath(xpathStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
       int contextNode = xctxt.getCurrentNode();
       result = xpath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
       
       java.lang.String xpathStr2 = xpath.getArrowOpRemainingXPathExprStr();
       if (xpathStr2 != null) {
          result = getFinalResult(result, xpathStr2, xctxt); 
       }
       
       return result;
    }
    
}
