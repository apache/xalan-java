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

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:string datatype.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSString extends XSCtrType {

    private static final long serialVersionUID = -7351932310979358488L;

    private static final String XS_STRING = "xs:string";
    
    private String _value;
    
    private XPathContext fXctxt = new XPathContext();
    
    private XPathCollationSupport fXpathCollationSupport = fXctxt.getXPathCollationSupport();
    
    /*
     * Class constructor.
    */
    public XSString(String str) {
       _value = str;       
    }

    /*
     * Class constructor.
    */
    public XSString() {
       this(null);
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) {        
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        resultSeq.add(new XSString(xsAnyType.stringValue()));
           
        return resultSeq;        
    }

    @Override
    public String typeName() {
        return "string";
    }

    @Override
    public String stringType() {
        return XS_STRING;
    }

    @Override
    public String stringValue() {
        return _value;
    }
    
    /**
     * Get the actual string value stored, within this object.
     * 
     * @return   the actual string value stored
     */
    public String value() {
        return stringValue();
    }
    
    /**
     * This function implements the semantics of XPath 3.1 'eq' operator,
     * on xs:string values.
     */
    public boolean equals(XSString xsStr) throws TransformerException {
        int comparisonResult = fXpathCollationSupport.compareStringsUsingCollation(_value, xsStr.stringValue(), 
                                                                                                       fXctxt.getDefaultCollation());
        return (comparisonResult == 0); 
    }
    
    /**
     * This function implements the semantics of XPath 3.1 'lt' operator,
     * on xs:string values.
     */
    public boolean lt(XSString xsStr) throws TransformerException {
        int comparisonResult = fXpathCollationSupport.compareStringsUsingCollation(_value, xsStr.stringValue(), 
                                                                                                       fXctxt.getDefaultCollation());
        return (comparisonResult < 0);  
    }
    
    /**
     * This function implements the semantics of XPath 3.1 'gt' operator,
     * on xs:string values.
     */
    public boolean gt(XSString xsStr) throws TransformerException {
        int comparisonResult = fXpathCollationSupport.compareStringsUsingCollation(_value, xsStr.stringValue(), 
                                                                                                       fXctxt.getDefaultCollation());
        return (comparisonResult > 0);  
    }
    
    public int getType() {
        return CLASS_STRING;
    }

}
