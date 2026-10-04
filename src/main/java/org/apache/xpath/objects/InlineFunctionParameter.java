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
package org.apache.xpath.objects;

import org.apache.xpath.composite.XPathSequenceType;

/**
 * Class definition, that represents an XPath 3.1 
 * 'inline function' item parameter.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *
 * @xsl.usage advanced
 */
public class InlineFunctionParameter {
    
    private String m_paramName;
    
    private XPathSequenceType m_paramType;

    public String getParamName() {
        return m_paramName;
    }

    public void setParamName(String paramName) {
        this.m_paramName = paramName;
    }

    public XPathSequenceType getParamType() {
        return m_paramType;
    }

    public void setParamType(XPathSequenceType paramType) {
        this.m_paramType = paramType;
    }

}
