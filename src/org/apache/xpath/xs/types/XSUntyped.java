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

import org.apache.xpath.objects.XObject;

/**
 * This class represents an XML Schema data type xs:untyped.
 *  
 * As per XPath 3.1 spec, xs:untyped is used as the type annotation of an 
 * XML element node that has not been validated, or has been validated in 
 * skip mode.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XSUntyped extends XSAnyType {

    private static final long serialVersionUID = 6146147730252441632L;
    
    private static final String XS_UNTYPED = "untyped";
    
    private String _value;
    
    public XSUntyped() {
        this(null);
    }
    
    public XSUntyped(String str) {
        _value = str;
    }

    public String typeName() {
        return "untyped";
    }
    
    @Override
    public String stringType() {
        return XS_UNTYPED;
    }

    @Override
    public String stringValue() {
        return _value;
    }
    
    public boolean equals(XObject xObject) {
       boolean isEquals = false;
        
       if (xObject instanceof XSUntyped) {
          isEquals = _value.equals(((XSUntyped)xObject).stringValue()); 
       }
       else if (xObject instanceof XSUntypedAtomic) {
          isEquals = _value.equals(((XSUntypedAtomic)xObject).stringValue());  
       }
       else {
          isEquals = _value.equals(xObject.str()); 
       }
        
       return isEquals;
    }

}
