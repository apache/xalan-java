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

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.xs.types.XSInteger;

/**
 * Implementation of the compare() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCompare extends FunctionMultiArgs {
    
    private static final long serialVersionUID = 4648998919300586767L;
    
    /**
     * The number of arguments passed to the fn:compare function 
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
        
        XSInteger result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        Expression arg0 = m_arg0;        
        Expression arg1 = m_arg1;        
        Expression arg2 = null;
        
        XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();
        
        try {
            if (numOfArgs == 2) {
               XObject xObject0 = arg0.execute(xctxt);               
               XObject xObject1 = arg1.execute(xctxt);
               
               String str0 = XslTransformEvaluationHelper.getStrVal(xObject0);
               String str1 = XslTransformEvaluationHelper.getStrVal(xObject1);
               
               // set the collation to default collation
               String collationUri = xctxt.getDefaultCollation();
               
               int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(
                                                                                    str0, str1, collationUri);
               
               result = new XSInteger(BigInteger.valueOf((long)comparisonResult));
            }
            else {
                // a collation uri was, explicitly provided during the function call fn:compare
                
                arg2 = m_arg2;
                
                XObject xObject0 = arg0.execute(xctxt);
                XObject xObject1 = arg1.execute(xctxt);
                
                XObject xObject2 = arg2.execute(xctxt);
                
                String str0 = XslTransformEvaluationHelper.getStrVal(xObject0);
                String str1 = XslTransformEvaluationHelper.getStrVal(xObject1);
                
                String collationUri = XslTransformEvaluationHelper.getStrVal(xObject2);
                
                int comparisonResult = xPathCollationSupport.compareStringsUsingCollation(
                                                                                     str0, str1, collationUri);
                
                result = new XSInteger(BigInteger.valueOf((long)comparisonResult)); 
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

}
