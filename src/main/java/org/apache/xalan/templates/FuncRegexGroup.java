/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xalan.templates;

import java.util.Map;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.RegexEvaluationSupport;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.regex.Matcher;

/**
 * Implementation of XSLT fn:regex-group function.
 * 
 * This function can be used within, XSLT instruction xsl:matching-substring.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncRegexGroup extends FunctionOneArg
{
    private static final long serialVersionUID = 2690898828342290061L;

    /**
      * Execute the function. The function must return a valid object.
      * 
      * @param xctxt The current execution context.
      * @return a valid XObject.
      *
      * @throws javax.xml.transform.TransformerException
    */
    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
        
        XObject result = null;
        
        Expression arg0Expr = this.getArg0();
        
        int regExGrpNumber;
        
        if (arg0Expr != null) {
            XObject arg0 = arg0Expr.execute(xctxt);
            if (arg0 instanceof XNumber) {
               XNumber argNum = (XNumber)arg0;
               double argValue = argNum.num();
               try {
                  regExGrpNumber = (int)argValue;
               }
               catch (NumberFormatException ex) {
                   throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the "
                                                        + "first argument of function fn:regex-group() is xs:integer.", 
                                                                    xctxt.getSAXLocator());   
               }
            }
            else {
                throw new javax.xml.transform.TransformerException("XPTY0004 : The required item type of the "
                                                      + "first argument of function fn:regex-group() is xs:integer.", 
                                                                   xctxt.getSAXLocator());   
            }
        }
        else {
            throw new javax.xml.transform.TransformerException("XPST0017 : Cannot find a '0 argument' function "
                                                                    + "named regex-group().", xctxt.getSAXLocator());    
        }
        
        Map<String, String> customDataMap = xctxt.getCustomDataMap();
        String strValue = customDataMap.get(ElemMatchingSubstring.STR_VALUE);
        String regex = customDataMap.get(ElemMatchingSubstring.REGEX);
        String regexFlags = customDataMap.get(ElemMatchingSubstring.REGEX_FLAGS);
        
        Matcher regexMatcher = RegexEvaluationSupport.compileAndExecute(RegexEvaluationSupport.transformRegexStrForSubtractionOp(
                                                                                   regex), regexFlags, strValue);
        String regexGrpStr = null;
        if (regexMatcher.matches()) {
            try {
               regexGrpStr = regexMatcher.group(regExGrpNumber);
            }
            catch (IllegalStateException ex) {
               throw new javax.xml.transform.TransformerException("An unexpected error occured, during evaluation of "
                                                                              + "function fn:regex-group.", xctxt.getSAXLocator());     
            }
            catch (IndexOutOfBoundsException ex) {
                regexGrpStr = "";   
            }
        }
        else {
            regexGrpStr = "";
        }
        
        result = new XString(regexGrpStr);
       
        return result;
    }
  
}
