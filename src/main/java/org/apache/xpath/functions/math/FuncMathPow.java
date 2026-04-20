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
import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncArgPlaceholder;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.patterns.NodeTest;

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
    
	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt The current execution context
	 * @return A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	*/
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();                                
        
        XObject arg0Result = null;
        XObject arg1Result = null;
        
        Expression arg0Expr = getArg0();
        Expression arg1Expr = getArg1();
        
        if (arg0Expr instanceof NodeTest) {
     	   if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)arg0Expr)) {
     		   throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the first argument of XPath function pow(), "
     				                                                                   + "but the supplied type is a function type, which cannot be atomized.", srcLocator); 
     	   }
        }
        else if (arg0Expr instanceof XPathInlineFunction) {
      	   if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)arg0Expr)) {
      		   throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the first argument of XPath function pow(), "
      				                                                                   + "but the supplied type is a function type, which cannot be atomized.", srcLocator); 
      	   }
        }
        
        if (arg1Expr instanceof NodeTest) {
        	if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)arg1Expr)) {
        		throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the second argument of XPath function pow(), "
        				                                                                + "but the supplied type is a function type, which cannot be atomized.", srcLocator); 
        	}
        }
        else if (arg1Expr instanceof XPathInlineFunction) {
       	   if (XslTransformEvaluationHelper.isNodeTestExpressionFuntionType((NodeTest)arg1Expr)) {
       		   throw new javax.xml.transform.TransformerException("FOTY0013 : An atomic value is required for the second argument of XPath function pow(), "
       				                                                                   + "but the supplied type is a function type, which cannot be atomized.", srcLocator); 
       	   }
        }
        
        if ((arg0Expr instanceof FuncArgPlaceholder) && (arg1Expr instanceof FuncArgPlaceholder)) {
        	String xpathInlineFuncExprStr = "function($arg0, $arg1) { math:pow($arg0, $arg1) }";
    	    
    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	    	
	    	result = xpathObj.execute(xctxt, DTM.NULL, null);
        }
        else if ((arg0Expr instanceof FuncArgPlaceholder) && !(arg1Expr instanceof FuncArgPlaceholder)) {
        	arg1Result = getEffectiveFuncArgValue(arg1Expr, xctxt);
        	double rDouble = getDoubleValue(arg1Result, srcLocator, "second");
            String xpathInlineFuncExprStr = "function($arg0) { math:pow($arg0, " + rDouble + ") }";
    	    
    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	    	
	    	result = xpathObj.execute(xctxt, DTM.NULL, null);
        }
        else if (!(arg0Expr instanceof FuncArgPlaceholder) && (arg1Expr instanceof FuncArgPlaceholder)) {
        	arg0Result = getEffectiveFuncArgValue(arg0Expr, xctxt);
        	double lDouble = getDoubleValue(arg0Result, srcLocator, "first");
            String xpathInlineFuncExprStr = "function($arg1) { math:pow(" + lDouble + ", $arg1) }";
    	    
    	    XPath xpathObj = new XPath(xpathInlineFuncExprStr, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
	    	
	    	result = xpathObj.execute(xctxt, DTM.NULL, null);
        }
        else {        	
            arg0Result = getEffectiveFuncArgValue(arg0Expr, xctxt);            
            arg1Result = getEffectiveFuncArgValue(arg1Expr, xctxt);            
            
            double lDouble = getDoubleValue(arg0Result, srcLocator, "first");        
            double rDouble = getDoubleValue(arg1Result, srcLocator, "second");
            
            result = new XSDouble(Math.pow(lDouble, rDouble));
            
            return result;
        }                
        
        return result;
    }
    
    /*
     * Method definition, to get a primitive double value, from 
     * an XObject object instance. 
     */
    private double getDoubleValue(XObject xObject, SourceLocator srcLocator, String argNumStr) 
                                                                                 throws javax.xml.transform.TransformerException {
        
        double result = 0.0;
        
        if (xObject instanceof XNumber) {
           result = ((XNumber)xObject).num();
        }
        else if (xObject instanceof XSNumericType) {
           String strVal = ((XSNumericType)xObject).stringValue();
           result = (new XSDouble(strVal)).doubleValue();
        }
        else if (xObject instanceof XMLNodeCursorImpl) {
           XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xObject;
           if (xNodeSet.getLength() != 1) {
        	  throw new javax.xml.transform.TransformerException("XPTY0004 : The " + argNumStr + "argument to XPath function call pow() must be a sequence of length one.", srcLocator);    
           }
           else {
              String strVal = xNodeSet.str();
               
              double arg = 0.0;             
              try {
                 arg = (new XSDouble(strVal)).doubleValue();
              }
              catch (Exception ex) {
            	 throw new javax.xml.transform.TransformerException("FORG0001 : Error occured during XPath function call pow(). Cannot convert "
																									                         + "string valued argument \"" + strVal + "\" to "
																									                         + "a double value.", srcLocator);
              }
               
              result = arg;
           }
        }
        else if (xObject instanceof ResultSequence) {
            ResultSequence resultSeq = (ResultSequence)xObject;
            if (resultSeq.size() != 1) {
               throw new javax.xml.transform.TransformerException("XPTY0004 : The " + argNumStr + "argument to XPath function call pow() must be a sequence of length one.", srcLocator);    
            }
            else {
               XObject val = resultSeq.item(0);
               String strVal = XslTransformEvaluationHelper.getStrVal(val);
                
               double arg = 0.0;             
               try {
                  arg = (new XSDouble(strVal)).doubleValue();
               }
               catch (Exception ex) {
            	   throw new javax.xml.transform.TransformerException("FORG0001 : Error occured during XPath function call pow(). Cannot convert "
																								                           + "string valued argument \"" + strVal + "\" to "
																								                           + "a double value.", srcLocator);
               }
                
               result = arg;
            }
        }
        else {
        	throw new javax.xml.transform.TransformerException("XPTY0004 : An xdm item type of " + argNumStr + " argument to XPath function call pow() is not "
																														  + "an XML Schema type double.", srcLocator); 
        }
        
        return result; 
    }

}
