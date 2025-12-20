<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:map="http://www.w3.org/2005/xpath-functions/map"
			    exclude-result-prefixes="map" 
				expand-text="yes" 
				version="3.0">
				
   <!-- Author: mukulg@apache.org -->
  
   <!-- An XSL 3 stylesheet test case to test, xsl:perform-sort 
        instruction, to sort a sequence of xdm map items. -->  				
   
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="map-sequence1" select="(map { 'name' : 'foo', 'value' : 3 }, 
                                                              map { 'name': 'bar', 'value' : 2 },
															  map { 'name': 'hello', 'value' : 1 },
															  map { 'name': 'there', 'value' : 5 },
															  map { 'name': 'how are you', 'value' : 4 })" as="map(*)*"/> 

   <xsl:template match="/">
      <result>
	     <xsl:variable name="sorted-map-sequence1" as="map(*)*">
            <xsl:perform-sort select="$map-sequence1">
		      <xsl:sort select="?value"/>			
		    </xsl:perform-sort>
		 </xsl:variable>
		 <xsl:for-each select="$sorted-map-sequence1">
           <item>
             <name>{?name}</name>
             <value>{?value}</value>
           </item>
        </xsl:for-each>	   
	  </result>  
   </xsl:template>
   
   <!--
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
   -->

</xsl:stylesheet>
