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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.xs.types.XSBoolean;
import org.apache.xpath.xs.types.XSDate;
import org.apache.xpath.xs.types.XSDayTimeDuration;
import org.apache.xpath.xs.types.XSDecimal;
import org.apache.xpath.xs.types.XSDouble;
import org.apache.xpath.xs.types.XSDuration;
import org.apache.xpath.xs.types.XSFloat;
import org.apache.xpath.xs.types.XSInt;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSLong;
import org.apache.xpath.xs.types.XSString;
import org.apache.xpath.xs.types.XSYearMonthDuration;
import org.xml.sax.SAXException;

/**
 * An utility class, to support evaluation of XPath 3.1 constructor 
 * functions (ref, https://www.w3.org/TR/xpath-functions-31/#constructor-functions), 
 * and few other XPath expression evaluations.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSConstructorFunctionUtil {
    
    /**
     * Process an XPath expression of type FuncExtFunction, XPath operation, 
     * and also few XPath default expression processing.
     */
    public static XObject processFuncExtFunctionOrXPathOpn(XPathContext xctxt, Expression expr)
                                                                    throws TransformerException, SAXException {        
        XObject evalResult = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();

        // XalanJ's extension function handler mechanism, treats at a syntactic level,
        // XPath 3.1 constructor function calls like xs:type_name(..) as calls to XSLT/XPath
        // extension functions. If the XML namespace of XSLT/XPath function calls is an XML Schema
        // namespace, then we use this fact to treat such function calls as XPath 3.1 constructor 
        // function calls, as has been implemented within code below.
        
        if (expr instanceof FuncExtFunction) {
            FuncExtFunction funcExtFunction = (FuncExtFunction)expr;
            
            if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcExtFunction.getNamespace())) {
                // Handle as an XPath 3.1 constructor function call
                
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
                            Boolean boolVal = Boolean.valueOf("0".equals(XslTransformEvaluationHelper.getStrVal(argVal)) ? 
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
                        
                    default:
                       // no op
                  }
            }
        }
        else if (expr instanceof Operation) {
            // We need to call this method recursively, for the possibility of more than one
            // XPath expression evaluation operator present within an XPath expression 
            // (for e.g, a + b - c).
            Operation opn = (Operation)expr;
            XObject leftOperand = processFuncExtFunctionOrXPathOpn(xctxt, opn.getLeftOperand());
            XObject rightOperand = processFuncExtFunctionOrXPathOpn(xctxt, opn.getRightOperand());
            evalResult = opn.operate(leftOperand, rightOperand);
        }
        else {
            evalResult = expr.execute(xctxt);   
        }

        return evalResult;
        
    }

}
