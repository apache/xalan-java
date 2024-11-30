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

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of the hours-from-duration() function.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncHoursFromDuration extends FunctionOneArg {

    private static final long serialVersionUID = -7046073260941724285L;

    public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
    {
        XObject result = null;
        
        SourceLocator srcLocator = xctxt.getSAXLocator();
        
        try {
           Expression arg0 = getArg0();
           
           if (arg0 == null || isArgCountErr()) {
              ResultSequence resultSeq = new ResultSequence();
              return resultSeq;
           }
            
           XSDuration xsDuration = (XSDuration)(((XSLConstructorStylesheetOrExtensionFunction)arg0).execute(xctxt));
            
           int hours = xsDuration.hours();
           if (xsDuration.negative()) {
              hours = hours * -1;
           }
            
           result = new XSInteger(BigInteger.valueOf(hours));
        }
        catch (Exception ex) {
           throw new javax.xml.transform.TransformerException(ex.getMessage(), srcLocator); 
        }
        
        return result;
    }

}
