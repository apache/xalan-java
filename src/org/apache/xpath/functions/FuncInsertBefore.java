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
import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.xs.types.XSNumericType;

/**
 * Implementation of the insert-before() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncInsertBefore extends Function3Args {

    private static final long serialVersionUID = 4238468031173924757L;

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
        
        try {
            XObject xObject0 = m_arg0.execute(xctxt);        
            XObject xObject1 = m_arg1.execute(xctxt);        
            XObject xObject2 = m_arg2.execute(xctxt);
            
            ResultSequence rsArg0 = XslTransformEvaluationHelper.getResultSequenceFromXObject(
                                                                                          xObject0, xctxt);
            
            int seqInsertPos = getSequenceInsertPosition(xObject1);
            
            if (seqInsertPos < 1) {
               seqInsertPos = 1; 
            }
            
            ResultSequence rsArg2 = XslTransformEvaluationHelper.getResultSequenceFromXObject(
                                                                                          xObject2, xctxt);
            
            for (int idx = 0; idx < (seqInsertPos - 1); idx++) {
               result.add(rsArg0.item(idx));  
            }
            
            for (int idx = 0; idx < rsArg2.size(); idx++) {
               result.add(rsArg2.item(idx));   
            }
            
            for (int idx = (seqInsertPos - 1); idx < rsArg0.size(); idx++) {
               result.add(rsArg0.item(idx));  
            }
        }
        catch (TransformerException ex) {
            throw new TransformerException(ex.getMessage(), srcLocator);  
        }
        
        return result;
    }
    
    /**
     * Given an XObject object instance (representing the second argument [the position
     * argument] of function call fn:insert-before), get its value as an integer.  
     */
    private int getSequenceInsertPosition(XObject xObject) throws TransformerException {
       
       int seqInsertPos = -1;
       
       if (xObject instanceof XNumber) {
          double dbl = ((XNumber)xObject).num();
          if (dbl == (int)dbl) {
             seqInsertPos = (int)dbl;  
          }
          else {
             throw new TransformerException("FORG0006 : Incorrect value " + dbl + " provided to second argument "
                                                                              + "of function fn:insert-before. This argument value "
                                                                              + "needs to be an integer."); 
          }
       }
       else if (xObject instanceof XSNumericType) {
          String argStrVal = ((XSNumericType)xObject).stringValue();
          double dbl = (Double.valueOf(argStrVal)).doubleValue();
          if (dbl == (int)dbl) {
             seqInsertPos = (int)dbl;  
          }
          else {
             throw new TransformerException("FORG0006 : Incorrect value " + dbl + " provided to second argument "
                                                                              + "of function fn:insert-before. This argument value "
                                                                              + "needs to be an integer.");  
          }
       }
       else {
          throw new TransformerException("FORG0006 : The second argument to function fn:insert-before, needs to be "
                                                                                                         + "an integer value");  
       }
       
       return seqInsertPos; 
    }

}
