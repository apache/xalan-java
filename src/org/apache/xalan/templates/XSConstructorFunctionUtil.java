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
package org.apache.xalan.templates;

import javax.xml.XMLConstants;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.xs.types.XSBoolean;
import org.apache.xpath.xs.types.XSDate;
import org.apache.xpath.xs.types.XSDateTime;
import org.apache.xpath.xs.types.XSDayTimeDuration;
import org.apache.xpath.xs.types.XSDecimal;
import org.apache.xpath.xs.types.XSDouble;
import org.apache.xpath.xs.types.XSDuration;
import org.apache.xpath.xs.types.XSFloat;
import org.apache.xpath.xs.types.XSInt;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSLong;
import org.apache.xpath.xs.types.XSString;
import org.apache.xpath.xs.types.XSTime;
import org.apache.xpath.xs.types.XSYearMonthDuration;
import org.xml.sax.SAXException;

/**
 * A utility class that primarily supports, evaluations of XSLT stylesheet
 * function calls (for the stylesheet functions, defined with syntax 
 * xsl:function), and XPath 3.1 constructor function calls.  
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSConstructorFunctionUtil {
    
    /**
     * We use this method, primarily to evaluate XSLT stylesheet function calls (for the
     * stylesheet functions, defined with syntax xsl:function), and XPath 3.1 constructor
     * function calls (having syntax with form xs:type_name(..)).
     * 
     *  XalanJ's extension function handling mechanism, treats at a syntactic level,
     *  function calls belonging to non-null XML namespaces as calls to extension functions.
     *  
     *  Currently, XPath function calls having XML namespaces
     *  http://www.w3.org/2005/xpath-functions, http://www.w3.org/2005/xpath-functions/math
     *  within function names, are treated as XPath built-in functions by XalanJ. All other
     *  XPath function calls having other non-null XML namespaces are handling by XalanJ's
     *  extension function handling mechanism. 
     */
    public static XObject processFuncExtFunctionOrXPathOpn(XPathContext xctxt, Expression expr, TransformerImpl transformer)
                                                                                                              throws TransformerException, SAXException {        
        XObject evalResult = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        if (expr instanceof FuncExtFunction) {
            FuncExtFunction funcExtFunction = (FuncExtFunction)expr;
            
            String funcName = funcExtFunction.getFunctionName();
            String funcNamespace = funcExtFunction.getNamespace();
            
            ExpressionNode expressionNode = expr.getExpressionOwner();
            ExpressionNode stylesheetRootNode = null;
            while (expressionNode != null) {
                stylesheetRootNode = expressionNode;
                expressionNode = expressionNode.exprGetParent();                     
            }
            
            StylesheetRoot stylesheetRoot = (StylesheetRoot)stylesheetRootNode;
            
            TemplateList templateList = stylesheetRoot.getTemplateListComposed();
            
            ElemTemplate elemTemplate = templateList.getTemplate(new QName(funcNamespace, funcName));
                        
            if ((elemTemplate != null) && (elemTemplate instanceof ElemFunction)) {
                // Handle call to XSLT stylesheet function definition, specified with syntax 
                // xsl:function.                
                ResultSequence argSequence = new ResultSequence();
                for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                    XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                    argSequence.add(argVal);
                }
                
                evalResult = ((ElemFunction)elemTemplate).executeXslFunction(transformer, argSequence);
            }            
            else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcExtFunction.getNamespace())) {                
                // Handle call to XPath 3.1 constructor function, having syntax with form
                // xs:type_name(..). 
                ResultSequence argSequence = new ResultSequence();
                ResultSequence evalResultSequence = null;
                
                switch (funcExtFunction.getFunctionName()) {
                    case Keywords.XS_STRING :                        
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSString(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSString()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.XS_DECIMAL :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSDecimal(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSDecimal()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.XS_FLOAT :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSFloat(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSFloat()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;                
                    case Keywords.XS_DOUBLE :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSDouble(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSDouble()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;                
                    case Keywords.XS_INTEGER :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSInteger(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSInteger()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;                
                    case Keywords.XS_LONG :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSLong(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSLong()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.XS_INT :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(new XSInt(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSInt()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.FUNC_BOOLEAN_STRING :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            String argStrVal = XslTransformEvaluationHelper.getStrVal(argVal);
                            Boolean boolVal = Boolean.valueOf(("0".equals(argStrVal) || "false".equals(argStrVal)) ? 
                                                                                                     "false" : "true");
                            argSequence.add(new XSBoolean(boolVal));
                        }
    
                        evalResultSequence = (new XSBoolean()).constructor(argSequence);
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.XS_DATE :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(XSDate.parseDate(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSDate()).constructor(argSequence); 
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.XS_DATETIME :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            argSequence.add(XSDateTime.parseDateTime(XslTransformEvaluationHelper.getStrVal(argVal)));
                        }
    
                        evalResultSequence = (new XSDateTime()).constructor(argSequence); 
                        evalResult = evalResultSequence.item(0);
                        
                        break;
                    case Keywords.XS_DURATION :
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
                            XSDuration xsDuration = XSDuration.parseDuration(strVal);
                            if (xsDuration != null) {
                               argSequence.add(xsDuration);
                               evalResultSequence = (new XSDuration()).constructor(argSequence); 
                               evalResult = evalResultSequence.item(0);
                            }
                            else {
                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:duration value '" + 
                                                                                               strVal + "' is present in the input.", srcLocator); 
                            }
                        }
                        
                        break;
                    case Keywords.XS_YEAR_MONTH_DURATION :                   
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
                            XSDuration xsDuration = XSYearMonthDuration.parseYearMonthDuration(strVal);
                            if (xsDuration != null) {
                               argSequence.add(xsDuration);
                               evalResultSequence = (new XSYearMonthDuration()).constructor(argSequence); 
                               evalResult = evalResultSequence.item(0);
                            }
                            else {
                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:yearMonthDuration value '" + 
                                                                                                strVal + "' is present in the input.", srcLocator); 
                            }
                        }
                        
                        break;
                    case Keywords.XS_DAY_TIME_DURATION :                 
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
                            XSDuration xsDuration = XSDayTimeDuration.parseDayTimeDuration(strVal);
                            if (xsDuration != null) {
                               argSequence.add(xsDuration);
                               evalResultSequence = (new XSDayTimeDuration()).constructor(argSequence); 
                               evalResult = evalResultSequence.item(0);
                            }
                            else {
                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:dayTimeDuration value '" + 
                                                                                                strVal + "' is present in the input.", srcLocator); 
                            }                            
                        }
                        
                        break;
                    case Keywords.XS_TIME :                 
                        for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                            XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                            String strVal = XslTransformEvaluationHelper.getStrVal(argVal);
                            XSTime xsTime = XSTime.parseTime(strVal);
                            if (xsTime != null) {
                               argSequence.add(xsTime);
                               evalResultSequence = (new XSTime()).constructor(argSequence); 
                               evalResult = evalResultSequence.item(0);
                            }
                            else {
                               throw new TransformerException("FORG0001 : An incorrectly formatted xs:time value '" + 
                                                                                                strVal + "' is present in the input.", srcLocator); 
                            }                            
                        }
                        
                        break;
                        
                    default:
                       // no op
                  }
             }
        }
        else {
           evalResult = expr.execute(xctxt);   
        }

        return evalResult;
        
    }

}
