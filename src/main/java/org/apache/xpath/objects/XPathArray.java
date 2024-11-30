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

import java.util.ArrayList;
import java.util.List;

import xml.xpath31.processor.types.XSAnyType;

/**
 * This class represents, an XPath 3.1 xdm array.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathArray extends XObject {

   private static final long serialVersionUID = -2635521758204654450L;
 
   private List<XObject> fList = new ArrayList<XObject>();
    
   /*
    * Class constructor.
    */
   public XPathArray() {}
   
   public int getType()
   {
       return CLASS_ARRAY;
   }
  
   /**
    * Get an item at a particular index of an array.
    */
   public XObject get(int index) {
	  return fList.get(index);  
   }
   
   /**
    * Append an xdm item, to an array.
    */
   public void add(XObject value) {
	  fList.add(value);  
   }
   
   /**
    * Get native contents of this array object.
    */
   public List<XObject> getNativeArray() {
       return fList;   
   }
   
   /**
    * Set a new native array object, as content of this XPath array.
    */
   public void setNativeArray(List<XObject> arr) {
	  fList = arr;
   }
   
   /**
    * Get number of entries in this array.
    */
   public int size() {
      return fList.size();
   }
   
   /**
    * Cast result object to a boolean.
    *
    * @return    true, if the size of this 'XPathArray' object 
    *            is greater than 0.
    */
   public boolean bool() {
       return (fList.size() > 0);       
   }
   
   public void reset() {
	   fList.clear(); 
   }
   
   /**
    * Get the string value of this XPathArray object.
    * 
    * This method, produces a default serialization of this
    * string value, which is space separated string values 
    * of the xdm items of this XPathArray.
    */
   public String str() {
       String resultStr = null;
       
       StringBuffer strBuff = new StringBuffer();
       for (int idx = 0; idx < fList.size(); idx++) {
          XObject item = fList.get(idx);
          if (idx < (fList.size() - 1)) {
             if (item instanceof XSAnyType) {
                 strBuff.append(((XSAnyType)item).stringValue() + " ");    
             }
             else {
                strBuff.append((fList.get(idx)).str() + " ");
             }
          }
          else {
             if (item instanceof XSAnyType) {
                 strBuff.append(((XSAnyType)item).stringValue());     
             }
             else {
                strBuff.append((fList.get(idx)).str());
             }
          }
       }
       
       resultStr = strBuff.toString(); 
       
       return resultStr;
   }

}
