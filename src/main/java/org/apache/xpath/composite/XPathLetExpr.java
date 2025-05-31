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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.templates.ElemFunction;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * An implementation of XPath 3.1 'let' expression.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathLetExpr extends Expression {

    private static final long serialVersionUID = 3063682088023616108L;

    private List<LetExprVarBinding> m_letExprVarBindingList = 
                                                   new ArrayList<LetExprVarBinding>();
    
    private String m_returnExprXPathStr = null;
    
    // The following two fields of this class, are used during 
    // XPath.fixupVariables(..) action as performed within object of 
    // this class.    
    private Vector m_vars;
    
    private int m_globals_size;

    @Override
    public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
       // no op
    }
    
    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
       
       XObject evalResult = null;
        
       SourceLocator srcLocator = xctxt.getSAXLocator();
        
       int contextNode = xctxt.getContextNode();              
       
       ElemTemplateElement elemTemplateElement = (ElemTemplateElement)xctxt.getNamespaceContext();
       List<XMLNSDecl> prefixTable = null;
       if (elemTemplateElement != null) {
          prefixTable = (List<XMLNSDecl>)elemTemplateElement.getPrefixTable();
       }              
       
       Map<QName, XObject> letExprVarBindingMap = new HashMap<QName, XObject>();
       
       for (int idx = 0; idx < m_letExprVarBindingList.size(); idx++) {          
          LetExprVarBinding letExprVarBinding = m_letExprVarBindingList.get(idx);
          String varName = letExprVarBinding.getVarName();
          String varResultXPathExprStr = letExprVarBinding.getXPathExprStr();
          
          if (prefixTable != null) {
             varResultXPathExprStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                  varResultXPathExprStr, prefixTable);
          }
          
          XPath letExprVarBindingXPath = new XPath(varResultXPathExprStr, srcLocator, xctxt.getNamespaceContext(), 
        		                                                                                             XPath.SELECT, null);
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
          
          if (xpathNamedFuncRef != null) {
        	  String funcNamespace = xpathNamedFuncRef.getFuncNamespace();
        	  String funcLocalName = xpathNamedFuncRef.getFuncName();
        	  int funcArity = xpathNamedFuncRef.getFuncArity();
        	  
        	  String funcQualifiedName = "{" + funcNamespace + "}" + funcLocalName; 

        	  FunctionTable funcTable = xctxt.getFunctionTable();

        	  Object funcIdObj = null;
        	  if (FunctionTable.XPATH_BUILT_IN_FUNCS_NS_URI.equals(funcNamespace)) {
        		  funcIdObj = funcTable.getFunctionId(funcLocalName);
        	  }
        	  else if (FunctionTable.XPATH_BUILT_IN_MATH_FUNCS_NS_URI.equals(funcNamespace)) {
        		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMathFuncs(funcLocalName);
        	  }
        	  else if (FunctionTable.XPATH_BUILT_IN_MAP_FUNCS_NS_URI.equals(funcNamespace)) {
        		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinMapFuncs(funcLocalName);
        	  }
        	  else if (FunctionTable.XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI.equals(funcNamespace)) {
        		  funcIdObj = funcTable.getFunctionIdForXPathBuiltinArrayFuncs(funcLocalName);
        	  }
        	  
        	  if (funcIdObj != null) {
        		  String funcIdStr = funcIdObj.toString();
        		  Function function = funcTable.getFunction(Integer.valueOf(funcIdStr));
        		  function.setLocalName(funcLocalName);
        		  function.setNamespace(funcNamespace);
        		  function.setFuncArity(funcArity);
        		  varBindingEvalResult = new XObject(function);
        	  }
        	  else if (xpathNamedFuncRef.getXslStylesheetFunction() != null) {
        		  ElemFunction elemFunction = xpathNamedFuncRef.getXslStylesheetFunction();
        		  varBindingEvalResult = new XObject(elemFunction);
        		  varBindingEvalResult.setXslStylesheetRoot(xpathNamedFuncRef.getXslStylesheetRoot());
        	  }
        	  else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcNamespace)) {
        		  XSL3ConstructorOrExtensionFunction funcObj = new XSL3ConstructorOrExtensionFunction(funcNamespace, funcLocalName, null);
        		  funcObj.setFuncArity(funcArity);
        		  varBindingEvalResult = new XObject(funcObj);        		  
        	  }
        	  else {
        		  throw new TransformerException("FODC0005 : Function definition for named function reference " + 
        				  																		  funcQualifiedName + " doesn't exist.", srcLocator);
        	  }
          }
          else if (varBindingEvalResult == null) {
             varBindingEvalResult = letExprVarBindingXPath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
          }
          
          if (varBindingEvalResult == null) {
        	 throw new TransformerException("FODC0005 : XPath 'let' expression's variable could not be bound to a non-null XDM value.", srcLocator); 
          }
          
          m_xpathVarList.add(new QName(varName));
          
          letExprVarBindingMap.put(new QName(varName), varBindingEvalResult);
       }              
       
       Map<QName, XObject> xpathVarMap = xctxt.getXPathVarMap();
       xpathVarMap.putAll(letExprVarBindingMap);
       
       if (prefixTable != null) {
          m_returnExprXPathStr = XslTransformEvaluationHelper.replaceNsUrisWithPrefixesOnXPathStr(
                                                                                m_returnExprXPathStr, prefixTable);
       }
       
       XPath returnExprXpath = new XPath(m_returnExprXPathStr, srcLocator, xctxt.getNamespaceContext(), 
                                                                                             XPath.SELECT, null);
       
       if (m_vars != null) {
          returnExprXpath.fixupVariables(m_vars, m_globals_size);
       }
       
       evalResult = returnExprXpath.execute(xctxt, contextNode, xctxt.getNamespaceContext());
       
       if (evalResult == null) {
          // To return an empty sequence
          evalResult = new ResultSequence();   
       }
        
       return evalResult;       
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
       m_vars = (Vector)(vars.clone());
       m_globals_size = globalsSize; 
    }

    @Override
    public boolean deepEquals(Expression expr) {        
       return false;
    }

    public List<LetExprVarBinding> getLetExprVarBindingList() {
        return m_letExprVarBindingList;
    }

    public void setLetExprVarBindingList(List<LetExprVarBinding> letExprVarBindingList) {
        this.m_letExprVarBindingList = letExprVarBindingList;
    }

    public String getReturnExprXPathStr() {
        return m_returnExprXPathStr;
    }

    public void setReturnExprXPathStr(String returnExprXPathStr) {
        this.m_returnExprXPathStr = returnExprXPathStr;
    }

}
