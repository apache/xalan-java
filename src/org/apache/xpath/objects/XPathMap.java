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

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents, an XPath 3.1 xdm map.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathMap extends XObject {
	
	private static final long serialVersionUID = -6876597720235822722L;
	
	// the underlying native map object, to store items of an XPath map 
    private Map<XObject, XObject> fMap = new HashMap<XObject, XObject>();
    
   /*
    * Class constructor.
    */
   public XPathMap() {}
   
   public int getType()
   {
       return CLASS_MAP;
   }
  
   /**
    * For a given key value, get corresponding map entry value.
    */
   public XObject get(XObject key) {
	  return fMap.get(key);  
   }
   
   /**
    * Add an key, value entry to map.
    */
   public void put(XObject key, XObject value) {
	  fMap.put(key, value);  
   }
   
   /**
    * Get native contents of this map object.
    */
   public Map<XObject, XObject> getMap() {
       return fMap;   
   }
   
   /**
    * Get number of entries in this map.
    */
   public int size() {
       return fMap.size();   
   }
   
   /**
    * Cast result object to a boolean.
    *
    * @return True if the size of this 'XPathMap' object
    * is greater than 0.
    */
   public boolean bool() {
       return (fMap.size() > 0);       
   }

}
