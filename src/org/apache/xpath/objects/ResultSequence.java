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
package org.apache.xpath.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents, an object for XPath 3.1 data model sequences.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ResultSequence extends XObject
{
    static final long serialVersionUID = -5736721866747906182L;
    
    // the underlying list object, storing items of this result sequence 
    private List<XObject> rsList = new ArrayList<XObject>();
    
    // Class constructor.
    public ResultSequence() {}
    
    public void add(XObject item) {
        rsList.add(item);    
    }
    
    public int size() {
        return rsList.size();   
    }
    
    public List<XObject> getResultSequenceItems() {
        return rsList;   
    }

}
