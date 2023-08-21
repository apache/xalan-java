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

import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.xs.types.XSAnyType;
import org.apache.xpath.xs.types.XSBoolean;

/**
 * Implementation of the codepoint-equal() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncCodepointEqual extends Function2Args {
    
    private static final long serialVersionUID = -2518383964235644671L;
    
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
        
        XPathCollationSupport xPathCollationSupport = xctxt.getXPathCollationSupport();

        XObject xObject0 = m_arg0.execute(xctxt);        
        XObject xObject1 = m_arg1.execute(xctxt);
        
        // If either argument to this function is an empty sequence, the function returns 
        // an empty sequence (as required by the XPath 3.1 F&O spec).
        
        // Get the string value of first argument, to function call fn:codepoint-equal
        
        String arg0Str = null;
        
        if (xObject0 instanceof XNodeSet) {
           XNodeSet nodeSet = (XNodeSet)xObject0;
           if (nodeSet.getLength() == 0) {
              return result; 
           }
           else {
              arg0Str = nodeSet.str(); 
           }
        }
        else if (xObject0 instanceof ResultSequence) {
           ResultSequence resultSequence = (ResultSequence)xObject0;
           if (resultSequence.size() == 0) {
              return result; 
           }
           else {
              arg0Str = getStringValueOfResultSequence(resultSequence); 
           }
        }
        else if (xObject0 instanceof XSAnyType) {
           arg0Str = ((XSAnyType)xObject0).stringValue();  
        }
        else {
           arg0Str = xObject0.str();  
        }
        
        // Get the string value of second argument, to function call fn:codepoint-equal
        
        String arg1Str = null;
        
        if (xObject1 instanceof XNodeSet) {
           XNodeSet nodeSet = (XNodeSet)xObject1;
           if (nodeSet.getLength() == 0) {
              return result; 
           }
           else {
              arg1Str = nodeSet.str();  
           } 
        }
        else if (xObject1 instanceof ResultSequence) {
           ResultSequence resultSequence = (ResultSequence)xObject1;
           if (resultSequence.size() == 0) {
              return result; 
           }
           else {
              arg1Str = getStringValueOfResultSequence(resultSequence);    
           } 
        }
        else if (xObject1 instanceof XSAnyType) {
           arg1Str = ((XSAnyType)xObject1).stringValue();  
        }
        else {
           arg1Str = xObject1.str();  
        }
        
        // Do the comparison of string arguments of this function, using 'Unicode codepoint collation',
        // as required by XPath 3.1 F&O spec.
        int strComparisonResult = xPathCollationSupport.compareStringsUsingCollation(arg0Str, 
                                                                                          arg1Str, xctxt.getDefaultCollation());        
        if (strComparisonResult == 0) {
           // The strings are equal codepoint by codepoint            
           result.add(new XSBoolean(true));   
        }
        else {
           // The strings are not equal codepoint by codepoint
           result.add(new XSBoolean(false)); 
        }
        
        return result;
    }
    
    /**
     * Get the string value of an 'ResultSequence' object. 
     */
    private String getStringValueOfResultSequence(ResultSequence resultSeq) {
        String strValue = null;
        
        StringBuffer strBuff = new StringBuffer(); 
        
        for (int idx = 0; idx < resultSeq.size(); idx++) {
           XObject resultSeqItem = resultSeq.item(idx);
           
           if (resultSeqItem instanceof XNodeSet) {
              strBuff.append(((XNodeSet)resultSeqItem).str());
           }
           else if (resultSeqItem instanceof XSAnyType) {
              strBuff.append(((XSAnyType)resultSeqItem).stringValue()); 
           }
           else {
              strBuff.append(resultSeqItem.str()); 
           }
        }
        
        return strValue; 
    }

}
