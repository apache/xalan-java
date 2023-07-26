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
import javax.xml.transform.TransformerException;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.FuncExtFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Operation;
import org.apache.xpath.xs.types.XSAnyType;
import org.apache.xpath.xs.types.XSBoolean;
import org.apache.xpath.xs.types.XSDate;
import org.apache.xpath.xs.types.XSDecimal;
import org.apache.xpath.xs.types.XSDouble;
import org.apache.xpath.xs.types.XSFloat;
import org.apache.xpath.xs.types.XSInt;
import org.apache.xpath.xs.types.XSInteger;
import org.apache.xpath.xs.types.XSLong;
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
    
    /*
     * Process an XPath expression of the type FuncExtFunction, XPath operation, 
     * and also few default XPath expression processing.
     * 
     * We use the XalanJ extension function evaluation mechanism, to evaluate
     * XPath 3.1 constructor functions.
     */
    public static XObject processFuncExtFunctionOrXPathOpn(XPathContext xctxt, Expression expr)
                                                                    throws TransformerException, SAXException {        
        XObject evalResult = null;

        if (expr instanceof FuncExtFunction) {
            FuncExtFunction funcExtFunction = (FuncExtFunction)expr;
            if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(funcExtFunction.getNamespace())) {
                // evaluate XPath 3.1 constructor function calls, corresponding to XML Schema 
                // built-in types.
                
                if ((Keywords.FUNC_XS_DECIMAL).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(new XSDecimal(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSDecimal()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_XS_FLOAT).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(new XSFloat(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSFloat()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_XS_DOUBLE).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(new XSDouble(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSDouble()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_XS_INTEGER).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(new XSInteger(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSInteger()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_XS_LONG).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(new XSLong(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSLong()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_XS_INT).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(new XSInt(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSInt()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_BOOLEAN_STRING).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        Boolean boolVal = Boolean.valueOf("0".equals(getSimpleStrVal(argVal)) ? 
                                                                                 "false" : "true");
                        argSequence.add(new XSBoolean(boolVal));
                    }

                    ResultSequence rSeq = (new XSBoolean()).constructor(argSequence);
                    evalResult = rSeq.item(0);              
                }
                else if ((Keywords.FUNC_XS_DATE).equals(funcExtFunction.getFunctionName())) {                              
                    ResultSequence argSequence = new ResultSequence();
                    for (int idx = 0; idx < funcExtFunction.getArgCount(); idx++) {
                        XObject argVal = (funcExtFunction.getArg(idx)).execute(xctxt);
                        argSequence.add(XSDate.parseDate(getSimpleStrVal(argVal)));
                    }

                    ResultSequence rSeq = (new XSDate()).constructor(argSequence); 
                    evalResult = rSeq.item(0);              
                }
            }
        }
        else if (expr instanceof Operation) {
            // we need to call this method recursively, for the possibility of more than one
            // XPath expression evaluation operator present within an XPath expression.
            // for e.g, a + b - c.
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
    
    /*
     * Given an XObject object reference, return the string value
     * of this object. 
     */
    private static String getSimpleStrVal(XObject xObj) {
       String strVal = null;
       
       if (xObj instanceof XSAnyType) {
          strVal = ((XSAnyType)xObj).stringValue();    
       }
       else {
          strVal = xObj.str();  
       }
       
       return strVal;
    }

}
