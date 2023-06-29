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
package org.apache.xpath.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.xpath.xs.types.XSAnyType;

/**
 * This class represents, the XPath 3.1 data model sequence.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ResultSequence extends XObject
{
    static final long serialVersionUID = -5736721866747906182L;
    
    // the underlying list object, to store items of this result sequence 
    private List<XObject> rsList = new ArrayList<XObject>();
    
    /*
     * Class constructor.
     */
    public ResultSequence() {}
    
    public int getType()
    {
        return CLASS_RESULT_SEQUENCE;
    }
    
    public void add(XObject item) {
        rsList.add(item);    
    }
    
    public XObject item(int idx) {
        return rsList.get(idx);     
    }
    
    public int size() {
        return rsList.size();   
    }
    
    public List<XObject> getResultSequenceItems() {
        return rsList;   
    }
    
    /*
     * Get the string value of this ResultSequence object.
     * 
     * This method, produces a default serialization of this
     * string value, which is space separated string values 
     * of the xdm items of this sequence.
     */
    public String str() {
        String resultStr = null;
        
        StringBuffer strBuff = new StringBuffer();
        for (int idx = 0; idx < rsList.size(); idx++) {
           XObject item = rsList.get(idx);
           if (idx < (rsList.size() - 1)) {
              if (item instanceof XSAnyType) {
                  strBuff.append(((XSAnyType)item).stringValue() + " ");    
              }
              else {
                 strBuff.append((rsList.get(idx)).str() + " ");
              }
           }
           else {
              if (item instanceof XSAnyType) {
                  strBuff.append(((XSAnyType)item).stringValue());     
              }
              else {
                 strBuff.append((rsList.get(idx)).str());
              }
           }
        }
        
        resultStr = strBuff.toString(); 
        
        return resultStr;
    }

}
