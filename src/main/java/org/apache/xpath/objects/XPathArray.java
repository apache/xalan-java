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

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;

import xml.xpath31.processor.types.XSAnyType;

/**
 * Class definition, to represent, an XPath 3.1 xdm array.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathArray extends XObject {

   private static final long serialVersionUID = -2635521758204654450L;
 
   /**
    * A java.util.List object instance, that is the native
    * XObject list, store for this xdm array object instance.
    */
   private List<XObject> m_list = new ArrayList<XObject>();
    
   /**
    * Class constructor.
    */
   public XPathArray() {
	  // No op 
   }
  
   /**
    * Method definition, to get an xdm item from a
    * particular index, within an xdm array.
    * 
    * @param index            The supplied xdm array
    *                         index value.
    * @return                 An XObject, object instance
    *                         value.
    */
   public XObject get(int index) {
	  return m_list.get(index);  
   }
   
   /**
    * Method definition, to append an xdm item,
    * to an xdm array.
    * 
    * @param xObj             The supplied XObject, object 
    *                         instance.
    */
   public void add(XObject xObj) {
	  m_list.add(xObj);  
   }
   
   /**
    * Method definition, to get native list contents 
    * from this xdm array object.
    */
   public List<XObject> getNativeArray() {
       return m_list;   
   }
   
   /**
    * Method definition, to set a new native list
    * object, as content for this xdm array.
    * 
    * @param list                 The supplied java.util.List,
    *                             object instance
    */
   public void setNativeArray(List<XObject> list) {
	  m_list = list;
   }
   
   /**
    * Method definition, to get the size for this 
    * xdm array.
    */
   public int size() {
      return m_list.size();
   }
   
   /**
    * Method definition, to get effective boolean
    * value for this xdm array object instance.
    *
    * @return             Boolean value true, if the size of 
    *                     this xdm array object is greater 
    *                     than zero. Otherwise, false.
    */
   public boolean bool() {
       return (m_list.size() > 0);       
   }
   
   /**
    * Method definition, to reset the contents
    * of this xdm array object instance.
    */
   public void reset() {
	   m_list.clear(); 
   }
   
   public int getType()
   {
       return CLASS_ARRAY;
   }
   
   /**
    * Method definition, to atomize an xdm array, 
    * to an xdm sequence.
    * 
    * @return		              An xdm sequence, which is the 
    *                             result of atomizing an xdm array.		
    */
   public ResultSequence atomize() {
	   
	   ResultSequence result = new ResultSequence();
	   
	   int size1 = this.size();
	   
	   for (int idx = 0; idx < size1; idx++) {
		   XObject arrItem = this.get(idx);
		   
		   if (arrItem instanceof ResultSequence) {
		      ResultSequence expandedResultSeq = new ResultSequence();
	          
		      XslTransformEvaluationHelper.expandResultSequence((ResultSequence)arrItem, expandedResultSeq);
	          
	          int size2 = expandedResultSeq.size();
	          
	          for (int idx2 = 0; idx2 < size2; idx2++) {
	        	 XObject xObj = expandedResultSeq.item(idx2);
	        	 result.add(xObj);
	          }
	       }
		   if (arrItem instanceof XPathArray) {
	          XPathArray xpathArr = (XPathArray)arrItem;
	          ResultSequence rSeq = xpathArr.atomize();
	          
	          int size2 = rSeq.size();
	          
	          for (int idx2 = 0; idx2 < size2; idx2++) {
	        	  XObject xObj = rSeq.item(idx2);
	        	  result.add(xObj);
		      }
	       }
	       else {
		      result.add(arrItem);
	       }
	   }

	   return result;
   }
   
   /**
    * Method definition, to get an xdm array's string value.
    * 
    * This method, produces a default string valued serialization 
    * of an xdm array, which is space separated string values of 
    * the xdm items of this xdm array object instance.
    */
   public String str() {
	   
       String result = null;
       
       StringBuffer strBuff = new StringBuffer();
       
       int size1 = m_list.size();
       
       for (int idx = 0; idx < size1; idx++) {
          XObject item = m_list.get(idx);
          if (idx < (size1 - 1)) {
             if (item instanceof XSAnyType) {
                 strBuff.append(((XSAnyType)item).stringValue() + " ");    
             }
             else {
                strBuff.append((m_list.get(idx)).str() + " ");
             }
          }
          else {
             if (item instanceof XSAnyType) {
                 strBuff.append(((XSAnyType)item).stringValue());     
             }
             else {
                strBuff.append((m_list.get(idx)).str());
             }
          }
       }
       
       result = strBuff.toString(); 
       
       return result;
   }

}
