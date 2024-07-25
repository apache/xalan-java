/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
package org.apache.xpath.functions.array;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.InlineFunctionParameter;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.operations.Variable;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * Implementation of the array:filter function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncArrayFilter extends Function2Args {
   
   private static final long serialVersionUID = -7341393974878279742L;
   
   // The following two fields of this class, are used during 
   // XPath.fixupVariables(..) action as performed within object of 
   // this class.    
   private Vector fVars;    
   private int fGlobalsSize;

   /**
   * Implementation of the function. The function must return a valid object.
   * 
   * @param xctxt The current execution context.
   * 
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
      
	    XPathArray result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        final int contextNode = xctxt.getCurrentNode();
        
        Expression arg0 = getArg0();
        Expression arg1 = getArg1();
        
        if (fVars != null) {
           arg0.fixupVariables(fVars, fGlobalsSize);
        }
        
        XObject arg0XsObject = arg0.execute(xctxt, contextNode);
        
        if (!(arg0XsObject instanceof XPathArray)) {
           throw new javax.xml.transform.TransformerException("FORG0006 : The 1st argument of function call array:filter "
            		                                                            + "is not an array.", xctxt.getSAXLocator());
        }
                    
        if (arg1 instanceof XPathInlineFunction) {
            XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1;
            verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
            result = evaluateArrayFilter(xctxt, (XPathArray)arg0XsObject, inlineFuncArg); 
        }
        else if (arg1 instanceof Variable) {
            XObject arg1VarValue = arg1.execute(xctxt);
            if (arg1VarValue instanceof XPathInlineFunction) {
                XPathInlineFunction inlineFuncArg = (XPathInlineFunction)arg1VarValue;
                verifyInlineFunctionParamCardinality(inlineFuncArg, srcLocator);
                result = evaluateArrayFilter(xctxt, (XPathArray)arg0XsObject, inlineFuncArg);   
            }
            else {
                throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument to function call array:filter, "
                                                                                               + "is not a function item.", xctxt.getSAXLocator());    
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("FORG0006 : The 2nd argument to function call array:filter, "
                                                                                           + "is not a function item.", xctxt.getSAXLocator());               
        }
        
        return result;
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException
  {
     if (argNum != 2) {
        reportWrongNumberArgs();
     }
  }
  
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
      fVars = (Vector)(vars.clone());
      fGlobalsSize = globalsSize; 
  }
  
  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
      throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                              XPATHErrorResources.ER_TWO, null)); //"2"
  }
  
  /**
   * Verify the, number of function parameters, that an inline function argument is allowed 
   * to have for array:filter's function call.
   */
  private void verifyInlineFunctionParamCardinality(XPathInlineFunction inlineFuncArg, SourceLocator srcLocator) throws 
                                                                                                javax.xml.transform.TransformerException {
      List<InlineFunctionParameter> funcParamList = inlineFuncArg.getFuncParamList();
      if (funcParamList.size() != 1) {
          throw new javax.xml.transform.TransformerException("XPTY0004 : The supplied function fn:filter's function item argument has " + 
                                                                                                      funcParamList.size() + " parameters. "
                                                                                                      + "Expected 1.", srcLocator);   
      }
  }
  
  /**
   * Evaluate an array:filter function call, given its computed arguments.
   * 
   * @param xctxt						XPath context
   * @param xpathArr					An XPath input, array
   * @param funcItem					Function item that is evaluated for each member of array
   * @return							An array whose members are, those array input items for which,
   *                                    function item has evaluated to true.
   * @throws TransformerException
   */
  private XPathArray evaluateArrayFilter(XPathContext xctxt, XPathArray xpathArr, XPathInlineFunction funcItem) 
                                                                                    throws TransformerException {
	  XPathArray resultArr = new XPathArray();
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  List<InlineFunctionParameter> funcParamList = funcItem.getFuncParamList();
	  QName funcItemParamName = new QName((funcParamList.get(0)).getParamName());

	  String funcBodyXPathExprStr = funcItem.getFuncBodyXPathExprStr();

	  if (funcBodyXPathExprStr == null || "".equals(funcBodyXPathExprStr)) {
		  return resultArr;
	  }	  

	  ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
      List<XMLNSDecl> prefixTable = null;
      if (elemTemplateElement != null) {
         prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
      }
      
      if (prefixTable != null) {
         funcBodyXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
      		                                                                                funcBodyXPathExprStr, prefixTable);
      }

	  XPath inlineFnXpath = new XPath(funcBodyXPathExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);

	  XPathContext xpathContextNew = new XPathContext(false);
	  Map<QName, XObject> inlineFunctionVarMap = xpathContextNew.getXPathVarMap();

	  for (int idx = 0; idx < xpathArr.size(); idx++) {
		  XObject inpItem = xpathArr.get(idx);
		  if (funcItemParamName != null) {
			  inlineFunctionVarMap.put(funcItemParamName, inpItem);
		  }

		  XObject resultObj = inlineFnXpath.execute(xpathContextNew, DTM.NULL, null);
		  if (resultObj instanceof XBoolean) {
			  if (((XBoolean)resultObj).bool()) {
				  resultArr.add(inpItem);
			  }
		  }
		  else {
			  throw new javax.xml.transform.TransformerException("XPTY0004 : While evaluating an array:filter function call, for one of "
			  		                                                   + "an array member, array:filter's funtion item argument didn't evaluate to "
			  		                                                   + "a xs:boolean result.", srcLocator); 
		  }
	  }

	  inlineFunctionVarMap.clear();

	  return resultArr;
   }

}
