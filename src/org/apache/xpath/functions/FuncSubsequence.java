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
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.xs.types.XSNumericType;

/**
 * Implementation of the subsequence() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncSubsequence extends FunctionMultiArgs {

    private static final long serialVersionUID = -3680881808988095370L;
    
    /**
     * The number of arguments passed to the fn:sort function 
     * call.
     */
    private int numOfArgs = 0;

    /**
     * Execute the function. The function must return a valid object.
     * 
     * @param xctxt The current execution context.
     * @return A valid XObject.
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
            int startingLoc = getIntFromXObject(arg1Obj);
            
            startingLoc = (startingLoc <= 0) ? 1 : startingLoc; 
            
            // This function call requires either two arguments, or three arguments
            if (numOfArgs == 2) {
               for (int idx = (startingLoc - 1); idx < rsArg0.size(); idx++) {
                  result.add(rsArg0.item(idx)); 
               }
            }
            else {
               // The function call has three arguments
               arg2 = m_arg2;
               XObject arg2Obj = arg2.execute(xctxt);           
               int lengthVal = getIntFromXObject(arg2Obj);
               if (lengthVal < 0) {
                  lengthVal = 0;    
               }
               else if ((startingLoc + lengthVal) > rsArg0.size()) {
                  lengthVal = rsArg0.size() - startingLoc + 1;    
               }
               
               for (int idx = (startingLoc - 1); idx < ((startingLoc - 1) + lengthVal); idx++) {
                  result.add(rsArg0.item(idx)); 
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
     * Given an evaluated XPath expression for function fn:subsequence's second or third
     * argument, get its value as an integer. 
     */
    private int getIntFromXObject(XObject xObject) throws javax.xml.transform.TransformerException {
       int returnVal = -1;
       
       double dblVal = 0.0;
       
       if (xObject instanceof XNumber) {
          dblVal = ((XNumber)xObject).num();
          returnVal = roundDoubleToInt(dblVal); 
       }
       else if (xObject instanceof XSNumericType) {
          String strVal = ((XSNumericType)xObject).stringValue();
          dblVal = (Double.valueOf(strVal)).doubleValue();
          returnVal = roundDoubleToInt(dblVal);
       }
       else {
          throw new javax.xml.transform.TransformerException("FORG0006 : The second or third argument's value, to "
                                                                                 + "function fn:subsequence is not numeric or "
                                                                                 + "cannot be cast to numeric."); 
       }
       
       return returnVal;
    }
    
    /**
     * Do a numeric round of a double value, to an integer value.
     */
    private int roundDoubleToInt(double dblVal) {
       int returnVal = -1;
       
       if ((dblVal >= -0.5 && dblVal < 0) || (dblVal == 0.0)) {
          returnVal = 0;  
       }
       else {
          returnVal = (int)(Math.floor(dblVal + 0.5)); 
       }
       
       return returnVal; 
    }

}
