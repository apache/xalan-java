<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="map"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSLT test case, to test XPath 3.1 function map:remove. -->              
				
   <xsl:output method="xml" indent="yes"/>                                                 

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
	   <xsl:variable name="map1" select="map{0 : 'Sonntag', 1 : 'Montag', 2 : 'Dienstag', 3 : 
	                                        'Mittwoch', 4 : 'Donnerstag', 5 : 'Freitag', 6 : 'Samstag'}"/>	   
	   <result>
	      <xsl:variable name="mapAfterRemove1" select="map:remove($map1,4)"/>
		  <one>
			 <xsl:value-of select="(map:contains($mapAfterRemove1,4) eq false()) and (map:size($mapAfterRemove1) eq 6)"/>
		  </one>
		  <xsl:variable name="mapAfterRemove2" select="map:remove($map1,23)"/>
		  <two>
			 <xsl:value-of select="map:size($mapAfterRemove2) eq 7"/>
		  </two>
		  <xsl:variable name="keysToBeRemoved" select="(0, 6 to 7)"/>
		  <xsl:variable name="mapAfterRemove3" select="map:remove($map1,$keysToBeRemoved)"/>
		  <map size="{map:size($mapAfterRemove3)}">
			<xsl:for-each select="sort(map:keys($mapAfterRemove3))">
			  <entry>
				<key><xsl:value-of select="."/></key>
				<value><xsl:value-of select="map:get($mapAfterRemove3, .)"/></value>
		      </entry>
			</xsl:for-each>
		  </map>
		  <xsl:variable name="mapAfterRemove4" select="map:remove($map1,())"/>
		  <map size="{map:size($mapAfterRemove4)}">
			<xsl:for-each select="sort(map:keys($mapAfterRemove4))">
			  <entry>
				<key><xsl:value-of select="."/></key>
				<value><xsl:value-of select="map:get($mapAfterRemove4, .)"/></value>
		      </entry>
			</xsl:for-each>
		  </map>
	   </result>
    </xsl:template>
    
    <!--
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
   -->
    
</xsl:stylesheet>
