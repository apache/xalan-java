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

import xml.xpath31.processor.types.XSAnyType;

/**
 * This class represents, an XPath 3.1 xdm sequence.
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
    
    /**
     * Class constructor.
     */
    public ResultSequence() {}
    
    public int getType()
    {
        return CLASS_RESULT_SEQUENCE;
    }
    
    /** 
     * Append an item at the end of the sequence.
     */
    public void add(XObject item) {
        rsList.add(item);    
    }
    
    /**
     * Set an item at a particular index.
     */
    public void set(int idx, XObject item) {
        rsList.set(idx, item);
    }
    
    /**
     * Get an item stored at a particular index.
     */
    public XObject item(int idx) {
        return rsList.get(idx);     
    }
    
    /**
     * Get the size of the current sequence object.
     */
    public int size() {
        return rsList.size();   
    }
    
    /**
     * Get the contents of this sequence object, as list of 
     * XObject objects. 
     */
    public List<XObject> getResultSequenceItems() {
        return rsList;   
    }
    
    /**
     * Cast result object to a boolean.
     *
     * @return True if the size of this 'ResultSequence' object
     * is greater than 0.
     */
    public boolean bool() {
        return (rsList.size() > 0);       
    }
    
    /**
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
    
    public boolean equals(Object obj) {
    	boolean isEquals = true;
    	
    	if (!(obj instanceof ResultSequence)) {
    	   return false;	
    	}
    	
    	String strVal1 = str();
    	String strVal2 = ((ResultSequence)obj).str();
    	
    	isEquals = strVal1.equals(strVal2);
    	
    	return isEquals;
    }
    
    public int hashCode() {
    	String strVal = str();
    	
    	return strVal.hashCode();
    }

}
