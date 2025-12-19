<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:map="http://www.w3.org/2005/xpath-functions/map"
			    exclude-result-prefixes="map" 
				expand-text="yes" 
				version="3.0">
				
   <!-- Author: mukulg@apache.org -->
  
   <!-- An XSL 3 stylesheet test case to test, xsl:perform-sort 
        instruction. An input sequence to be sorted, contains a 
        mix of xdm map items and integer values. -->				
				
   <xsl:output method="xml" indent="yes"/>				

   <xsl:variable name="sequence1" select="(map { 'name' : 'foo', 'value' : 3 }, 
                                                              map { 'name': 'bar', 'value' : 2 }, 5, 1)" as="item()*"/>   

   <xsl:template match="/">
      <result>
	     <xsl:variable name="sorted-sequence1" as="item()*">
            <xsl:perform-sort select="$sequence1">
		      <xsl:sort select="if (. instance of map(*)) then map:get(.,'value') else ." data-type="number"/>			
		    </xsl:perform-sort>
		 </xsl:variable>
		 <xsl:for-each select="$sorted-sequence1">
		   <xsl:variable name="item1" select="."/>
           <xsl:choose>
		      <xsl:when test="$item1 instance of map(*)">
			     <map>
			        <xsl:for-each select="map:keys($item1)">
			           <item key="{.}">
				          <xsl:value-of select="map:get($item1,.)"/>
				       </item>
			        </xsl:for-each>
			     </map>
			  </xsl:when>
			  <xsl:otherwise>
			     <value>{.}</value>
			  </xsl:otherwise>
		   </xsl:choose>
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
