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

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:boolean datatype.
 * 
 * We've this data type implementation, equivalent to XalanJ's legacy 
 * data type implementation org.apache.xpath.objects.XBoolean for boolean 
 * values. We may use, both of these classes for XalanJ's needs.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSBoolean extends XSCtrType {

    private static final long serialVersionUID = -8635660165145453378L;

    private static final String XS_BOOLEAN = "xs:boolean";
    
    private boolean _value;
    
    /*
     * Class constructor.
    */
    public XSBoolean(boolean bool) {
        _value = bool;
    }

    /*
     * Class constructor.
    */
    public XSBoolean() {
        this(false);
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) {        
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        String strVal = xsAnyType.stringValue();
           
        Boolean bool = null;
        if (isBooleanFalse(strVal)) {
           bool = Boolean.FALSE;    
        }
        else {
           bool = Boolean.TRUE;     
        }
           
        resultSeq.add(new XSBoolean(bool.booleanValue()));
           
        return resultSeq;        
    }

    @Override
    public String typeName() {
        return "boolean";
    }

    @Override
    public String stringType() {
        return XS_BOOLEAN;
    }

    @Override
    public String stringValue() {
        return "" + _value;
    }
    
    /**
     * Get the actual boolean value stored, within this object.
     * 
     * @return   the actual boolean value stored
     */
    public boolean value() {
        return _value;
    }
    
    public boolean equals(XSBoolean xsBoolean) {
        return _value == xsBoolean.value();  
    }
    
    public boolean lt(XSBoolean xsBoolean) {
        boolean resultVal = false;

        if (!value() && xsBoolean.value()) {
            resultVal = true;
        }
        
        return resultVal;  
    }
    
    public boolean gt(XSBoolean xsBoolean) {
        boolean resultVal = false;

        if (value() && !xsBoolean.value()) {
            resultVal = true;
        }
        
        return resultVal;  
    }
    
    public int getType() {
        return CLASS_BOOLEAN;
    }
    
    /*
     * Check whether, a string value represents a boolean 
     * 'false' value.
     */
    private boolean isBooleanFalse(String strVal) {
        return strVal.equals("0") || strVal.equals("false") ||
                 strVal.equals("+0") || strVal.equals("-0") ||
                 strVal.equals("0.0E0") || strVal.equals("NaN");
    }

}
