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
package org.apache.xpath.functions.math;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of the math:pow() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMathPow extends Function2Args {

    private static final long serialVersionUID = 1342863964649663483L;
    
    /**
	 * Class constructor.
	 */
	public FuncMathPow() {
		m_defined_arity = new Short[] { 2 };
	}
    
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        XObject arg0Result = getEffectiveFuncArgValue(getArg0(), xctxt);        
        XObject arg1Result = getEffectiveFuncArgValue(getArg1(), xctxt);
        
        double lDouble = getDoubleValue(arg0Result, srcLocator, "first");
        double rDouble = getDoubleValue(arg1Result, srcLocator, "second");
        
        result = new XSDouble(Math.pow(lDouble, rDouble));
        
        return result;
    }
    
    /*
     * Get an 'double' value from an object of type XObject. 
     */
    private double getDoubleValue(XObject xObject, SourceLocator srcLocator, String argNumStr) 
                                                                                 throws javax.xml.transform.TransformerException {
        
        double resultVal = 0.0;
        
        if (xObject instanceof XNumber) {
           resultVal = ((XNumber)xObject).num();
        }
        else if (xObject instanceof XSNumericType) {
           String strVal = ((XSNumericType)xObject).stringValue();
           resultVal = (new XSDouble(strVal)).doubleValue();
        }
        else if (xObject instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xObject;
           if (xNodeSet.getLength() != 1) {
              throw new javax.xml.transform.TransformerException("XPTY0004 : The " + argNumStr + " argument to math:pow "
                                                                       + "function must be a sequence of length one.", srcLocator);    
           }
           else {
              String strVal = xNodeSet.str();
               
              double arg = 0.0;             
              try {
                 arg = (new XSDouble(strVal)).doubleValue();
              }
              catch (Exception ex) {
                 throw new javax.xml.transform.TransformerException("FORG0001 : Error with the " + argNumStr + " argument of "
                                                                          + "math:pow. Cannot convert the string \"" + strVal + "\" "
                                                                                                 + "to a double value.", srcLocator);
              }
               
              resultVal = arg;
           }
        }
        else if (xObject instanceof ResultSequence) {
            ResultSequence resultSeq = (ResultSequence)xObject;
            if (resultSeq.size() != 1) {
               throw new javax.xml.transform.TransformerException("XPTY0004 : The " + argNumStr + " argument to math:pow "
                                                                        + "function must be a sequence of length one.", srcLocator);    
            }
            else {
               XObject val = resultSeq.item(0);
               String strVal = XslTransformEvaluationHelper.getStrVal(val);
                
               double arg = 0.0;             
               try {
                  arg = (new XSDouble(strVal)).doubleValue();
               }
               catch (Exception ex) {
                  throw new javax.xml.transform.TransformerException("FORG0001 : Error with the " + argNumStr + " argument of "
                                                                           + "math:pow. Cannot convert the string \"" + strVal + "\" "
                                                                                                  + "to a double value.", srcLocator);
               }
                
               resultVal = arg;
            }
        }
        else {
           throw new javax.xml.transform.TransformerException("XPTY0004 : The item type of " + argNumStr + " argument to function "
                                                                                                     + "math:pow is not xs:double.", srcLocator); 
        }
        
        return resultVal; 
    }

}
