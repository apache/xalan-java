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
package org.apache.xpath.xs.types;

import java.math.BigDecimal;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:decimal datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSDecimal extends XSNumericType {

    private static final long serialVersionUID = 3338738472846690263L;
    
    private static final String XS_DECIMAL = "xs:decimal";
    
    private BigDecimal _value;
    
    private XPath3DecimalFormat xpath3DecimalFormat = new XPath3DecimalFormat(
                                                                 "0.####################");
    
    /**
     * Class constructor.
     */
    public XSDecimal() {
       this(BigDecimal.valueOf(0));
    }
    
    /**
     * Class constructor.
     */
    public XSDecimal(BigDecimal bigDecimal) {
       _value = bigDecimal; 
    }
    
    /**
     * Class constructor.
     */
    public XSDecimal(String str) {
        _value = new BigDecimal(str);
    }

    @Override
    public String stringType() {
        return XS_DECIMAL;
    }
    
    public String typeName() {
        return "decimal";
    }

    @Override
    public String stringValue() {
        if (zero()) {
           return "0";
        }

        _value = new BigDecimal((_value.toString()).replaceFirst("0*", ""));
        
        return xpath3DecimalFormat.performStrFormatting(_value);
    }
    
    @Override
    public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        try {            
            XSDecimal xsDecimal = castToDecimal(xsAnyType);            
            resultSeq.add(xsDecimal);
        } catch (NumberFormatException ex) {
            // to do
            return null;
        }        
        
        return resultSeq;
    }
    
    /**
     * Check if this XSDecimal object represents the value 0.
     * 
     * @return    true if this XSDecimal object represents the value 0. 
     *            false otherwise.
     */
    public boolean zero() {
        return (_value.compareTo(new BigDecimal(0.0)) == 0);
    }
    
    /**
     * Get the actual value of the number stored within 
     * this XSDecimal object.
     * 
     * @return   the actual value of the number stored
     */
    public double doubleValue() {
        return _value.doubleValue();
    }
    
    public BigDecimal getValue() {
        return _value;
    }
    
    /**
     * Set the numeric double value, within this XSDecimal object.
     * 
     * @param val    number to be stored
     */
    public void setDouble(double val) {
        _value = new BigDecimal(val);
    }
    
    public boolean equals(XSDecimal xsDecimal) {
        return _value.equals(xsDecimal.getValue()); 
    }
    
    public boolean lt(XSDecimal xsDecimal) {
        return (_value.compareTo(xsDecimal.getValue()) == -1);
    }
    
    public boolean gt(XSDecimal xsDecimal) {
        return (_value.compareTo(xsDecimal.getValue()) == 1);
    }
    
    /*
     * Cast an object of type XSAnyType, to an object of type 
     * XSDecimal.  
     */
    private XSDecimal castToDecimal(XSAnyType xsAnyType) {        
       if (xsAnyType instanceof XSBoolean) {            
          if ((xsAnyType.stringValue()).equals("true")) {
             return new XSDecimal(new BigDecimal("1"));
          } 
          else {
             return new XSDecimal(new BigDecimal("0"));
          }
       }
        
       return new XSDecimal(xsAnyType.stringValue());
    }

}
