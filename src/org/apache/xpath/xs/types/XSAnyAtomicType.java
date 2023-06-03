/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
package org.apache.xpath.xs.types;

import org.apache.xpath.objects.ResultSequence;

/**
 * Base class for all the XML Schema atomic data types.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public abstract class XSAnyAtomicType extends XSAnySimpleType {
    
    private static final long serialVersionUID = 4800376096762047151L;

    /*
     * This function supports, creating XML Schema built-in types, XPath 3.1
     * XDM objects with data types xs:boolean, xs:decimal etc.
     * 
     */
    public abstract ResultSequence constructor(ResultSequence arg);
    
    /**
     * Get the datatype's name
     * 
     * @return  String representation of the datatype's name
     */
    public abstract String typeName();
    
}
