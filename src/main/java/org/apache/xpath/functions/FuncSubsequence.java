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
package org.apache.xpath.functions;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;

import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSNumericType;

/**
 * Implementation of XPath 3.1 function fn:subsequence.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSubsequence extends FunctionMultiArgs {

    private static final long serialVersionUID = -3680881808988095370L;
    
    /**
     * Class constructor.
     */
    public FuncSubsequence() {
  	   m_defined_arity = new Short[] { 2, 3 };  
    }
    
    /**
     * The number of arguments passed to the function call 
     * fn:subsequence.
     */
    private int numOfArgs = 0;

    /**
     * Evaluate the function. The function must return a 
     * valid object.
     * 
     * @param xctxt                          An XPath context object
     * @return                               A valid XObject
     *
     * @throws javax.xml.transform.TransformerException
     */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        
        ResultSequence result = new ResultSequence();
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = m_arg0;        
        Expression arg1 = m_arg1;        
        Expression arg2 = null;
        
        try {
            XObject xObject0 = arg0.execute(xctxt);
            
            ResultSequence rsArg0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(
                                                                                          xObject0, xctxt);
            
            XObject arg1Obj = arg1.execute(xctxt);
            boolean isStartingLocNegInf = false;
            boolean isStartingLocNan = false;
            if (arg1Obj instanceof XSDouble) {
                XSDouble xsDouble = (XSDouble)arg1Obj;
                Double dbl = Double.valueOf(xsDouble.doubleValue());
                if (dbl.doubleValue() == Double.NEGATIVE_INFINITY) {
                   isStartingLocNegInf = true;
                }
                else if (dbl.isNaN()) {
                   isStartingLocNan = true;
                }
            }
            
            boolean isLengthInf = false;
            boolean isLengthNan = false;
            XObject arg2Obj = null;
            if (numOfArgs == 3) {
            	arg2 = m_arg2;
            	arg2Obj = arg2.execute(xctxt);
            	if (arg2Obj instanceof XSDouble) {
             	   XSDouble xsDouble = (XSDouble)arg2Obj;
             	   Double dbl = Double.valueOf(xsDouble.doubleValue());
             	   if (dbl.doubleValue() == Double.POSITIVE_INFINITY) {
             		   isLengthInf = true;
             	   }
             	   else if (dbl.isNaN()) {
             		   isLengthNan = true;
             	   }
                }
            }
            
            if (isStartingLocNan || isLengthNan) {
               return result;
            }
            
            if (isStartingLocNegInf && !isLengthInf) {
                return result;
            }
            
            if (isStartingLocNegInf && isLengthInf) {
                return result;
            }
            
            int startingLoc = getIntFromXObject(arg1Obj);
            
            int rSeqLength = rsArg0.size();
            
            // This function call requires either two arguments, or three arguments
            if (numOfArgs == 2) {
            	for (int idx = (startingLoc - 1); idx < rSeqLength; idx++) {
            		if ((idx >= 0) && (idx < rSeqLength)) {
            			result.add(rsArg0.item(idx)); 
            		}
            	}
            }
            else {
            	// The function call has three arguments                                                                                       	            	
            	int rIndex;
            	if (!isLengthInf) {
            		int lengthVal = getIntFromXObject(arg2Obj); 
            		rIndex = ((startingLoc - 1) + lengthVal);
            	}
            	else {            		           		            		
            		rIndex = rSeqLength;
            	}

            	for (int idx = (startingLoc - 1); idx < rIndex; idx++) {
            		if ((idx >= 0) && (idx < rSeqLength)) {
            			result.add(rsArg0.item(idx));
            		}
            	}
            }
        }
        catch (javax.xml.transform.TransformerException ex) {
           throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator);  
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
       if (!(argNum > 1 && argNum <= 3)) {
          reportWrongNumberArgs();
       }
       else {
          numOfArgs = argNum;   
       }
    }
    
    /**
     * Constructs and throws a WrongNumberArgException with the appropriate
     * message for this function object.
     *
     * @throws WrongNumberArgsException
     */
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createXPATHMessage(
                                                                     XPATHErrorResources.ER_TWO_OR_THREE, 
                                                                     null));
    }
    
    /**
     * Method definition, to convert an XObject value which is function fn:subsequence's 
     * second or third argument to primitive integer. 
     * 
     * @param xObject							The supplied XObject value
     * @return                                  The result integer value
     * @throws javax.xml.transform.TransformerException
     */
    private int getIntFromXObject(XObject xObject) throws javax.xml.transform.TransformerException {
       
       int result = -1;
       
       double dblVal = 0.0;
       
       if (xObject instanceof XNumber) {
          dblVal = ((XNumber)xObject).num();
          result = roundDoubleToInt(dblVal); 
       }
       else if (xObject instanceof XSNumericType) {
          String strVal = ((XSNumericType)xObject).stringValue();
          dblVal = (Double.valueOf(strVal)).doubleValue();
          result = roundDoubleToInt(dblVal);
       }
       else if (xObject instanceof XMLNodeCursorImpl) {
    	  String strVal = ((XMLNodeCursorImpl)xObject).str();
    	  try {
    	     dblVal = (Double.valueOf(strVal)).doubleValue();
    	  }
    	  catch (NumberFormatException ex) {
    		  throw new javax.xml.transform.TransformerException("FORG0006 : The second or third argument's value to "
																				                      + "XPath function subsequence is not numeric or "
																				                      + "cannot be cast to numeric."); 
    	  }
    	  
          result = roundDoubleToInt(dblVal);
       }
       else {
          throw new javax.xml.transform.TransformerException("FORG0006 : The second or third argument's value to "
					                                                                                 + "XPath function subsequence is not numeric or "
					                                                                                 + "cannot be cast to numeric."); 
       }
       
       return result;
    }
    
    /**
     * Method definition, to do numeric round of double value 
     * to an integer.
     * 
     * @param dblVal					The supplied numeric double
     *                                  value.
     * @return                          The return value
     */
    private int roundDoubleToInt(double dblVal) {
    	
       int result = -1;
       
       if (((dblVal >= -0.5) && (dblVal < 0)) || (dblVal == 0.0)) {
          result = 0;  
       }
       else {
          result = (int)(Math.floor(dblVal + 0.5)); 
       }
       
       return result;
       
    }

}
