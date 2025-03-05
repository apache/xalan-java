<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs map fn0"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath map's unary 
         lookup operation and related XPath expression syntax. -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">	   
	   <result>
	      <xsl:variable name="map1" select="map {'a':5, 'b':4, 'c':3, 'd':2, 'e':1}" as="map (xs:string, xs:integer)"/>
	      <mapEntryValues all="true">
	         <xsl:value-of select="$map1?*"/>
	      </mapEntryValues>
		  <mapEntryValues specific="true">
		     <!-- The following XSL stylesheet's result sibling elements, 
			      are alternate ways to emit XDM map's same functional information 
				  from stylesheet. -->
	         <one><xsl:value-of select="$map1?('c', 'd', 'e')"/></one>
			 <two><xsl:value-of select="for $keyVal in ('c', 'd', 'e') return map:get($map1, $keyVal)"/></two>
			 <three>
			    <xsl:for-each select="('c', 'd', 'e')">
				   <xsl:value-of select="map:get($map1, .)"/><xsl:value-of select="if (position() ne last()) then ' ' else ()"/>
				</xsl:for-each>
			 </three>
	      </mapEntryValues>
		  <mapEntryValues specific="true">
	         <xsl:value-of select="$map1?fn0:getMapKeys()"/>
	      </mapEntryValues>
	   </result>
	</xsl:template>
	
	<!-- An XSL stylesheet function, to return a sequence of xs:string 
	     values. This returned sequence is a map's key specifier for map 
		 unary lookup operation. -->
	<xsl:function name="fn0:getMapKeys" as="xs:string*">
	   <xsl:sequence select="('a','b','c','d')"/>
	</xsl:function>
	
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
