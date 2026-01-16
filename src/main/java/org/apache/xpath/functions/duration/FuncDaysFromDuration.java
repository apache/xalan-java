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
package org.apache.xpath.functions.duration;

import java.math.BigInteger;

import javax.xml.transform.SourceLocator;

import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.SelfIteratorNoPredicate;
import org.apache.xpath.functions.FunctionOneArg;
import org.apache.xpath.functions.XSL3ConstructorOrExtensionFunction;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.operations.Variable;

import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSInteger;

/**
 * Implementation of XPath 3.1 function fn:days-from-duration.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncDaysFromDuration extends FunctionOneArg {

    private static final long serialVersionUID = -8471054241151786884L;
    
    /**
     * Class constructor.
     */
    public FuncDaysFromDuration() {
    	m_defined_arity = new Short[] { 1 };	
    }

    /**
     * Evaluate the function. The function must return a valid object.
     * 
     * @param xctxt                          An XPath context object
     * @return                               A valid XObject
     *
     * @throws javax.xml.transform.TransformerException
     */
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
            
           XSDuration xsDuration = null;
           if (arg0 instanceof SelfIteratorNoPredicate) {
        	  if (xctxt.getXPath3ContextItem() != null) {
        		 xsDuration = (XSDuration)(xctxt.getXPath3ContextItem());   
        	  }
           }
           
           if (xsDuration == null) {
         	  if (arg0 instanceof Variable) {
         		  xsDuration = (XSDuration)(arg0.execute(xctxt));
         	  }
         	  else {
         		  xsDuration = (XSDuration)(((XSL3ConstructorOrExtensionFunction)arg0).execute(xctxt));  
         	  }        	    
           }
            
           int days = xsDuration.days();
           if (xsDuration.negative()) {
              days = days * -1;
           }
            
           result = new XSInteger(BigInteger.valueOf(days));
        }
        catch (Exception ex) {
           String errMesg = ex.getMessage();           
           throw new javax.xml.transform.TransformerException("FORG0006 : " + errMesg + ".", srcLocator); 
        }
        
        return result;
    }

}
