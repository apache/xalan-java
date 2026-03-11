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
package xml.xpath31.processor.types;

import javax.xml.transform.TransformerException;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XObject;

/**
 * This class represents an XML Schema data type xs:untypedAtomic.
 *  
 * As per XPath 3.1 spec, xs:untypedAtomic is an XML Schema data type 
 * that is used to denote untyped atomic data, such as text that has not 
 * been assigned a more specific type.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSUntypedAtomic extends XSCtrType {

    private static final long serialVersionUID = 3034074443706977457L;
    
    private static final String XS_UNTYPED_ATOMIC = "xs:untypedAtomic";
    
    private String _value;
    
    /**
     * Default constructor.
     */
    public XSUntypedAtomic() {
        this(null);
    }
    
    /**
     * Class constructor.
     * 
     * @param str			   argument string value
     */
    public XSUntypedAtomic(String str) {
        _value = str;
    }

    @Override
    public ResultSequence constructor(ResultSequence arg) {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyAtomicType xsAnyAtomicType = (XSAnyAtomicType)arg.item(0);
        
        resultSeq.add(new XSUntypedAtomic(xsAnyAtomicType.stringValue()));
        
        return resultSeq;
    }

    @Override
    public String typeName() {
        return "untypedAtomic";
    }

    @Override
    public String stringType() {
        return XS_UNTYPED_ATOMIC;
    }

    @Override
    public String stringValue() {
        return _value;
    }
    
    /*
     * Check equality between this XSUntypedAtomic value and an XObject value.  
     */
    public boolean equals(XObject xObject) {
        boolean isEquals = false;
        
        if (xObject instanceof XSUntypedAtomic) {
           isEquals = _value.equals(((XSUntypedAtomic)xObject).stringValue()); 
        }
        else if (xObject instanceof XSUntyped) {
           isEquals = _value.equals(((XSUntyped)xObject).stringValue());  
        }
        else {
           isEquals = _value.equals(XslTransformEvaluationHelper.getStrVal(xObject)); 
        }
        
        return isEquals;
    }
    
    /*
     * Check equality between this XSUntypedAtomic value and an XObject value, considering
     * collation for string comparison. 
     */
    public boolean equals(XObject xObject, String collationUri, XPathCollationSupport xpathCollationSupport) 
    		                                                                        throws TransformerException {
        boolean isEquals = false;
        
        if (collationUri == null) {
        	isEquals = equals(xObject);  	
        }
        else {
	        if (xObject instanceof XSUntypedAtomic) {
	           int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(_value, 
                                                                               ((XSUntypedAtomic)xObject).stringValue(), collationUri);
               if (strComparisonResult == 0) {
                  isEquals = true; 
               } 
	        }
	        else if (xObject instanceof XSUntyped) {
	           int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(_value, 
                                                                                 ((XSUntyped)xObject).stringValue(), collationUri);
               if (strComparisonResult == 0) {
                   isEquals = true; 
               }  
	        }
	        else {
	           int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(_value, 
	        		                                                            XslTransformEvaluationHelper.getStrVal(xObject), collationUri);
               if (strComparisonResult == 0) {
                  isEquals = true; 
               }
	        }
        }
        
        return isEquals;
    }

}
