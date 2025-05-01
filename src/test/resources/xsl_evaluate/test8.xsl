<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
			    xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="map array" version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test4.xml, test5.xml, test7.xml  -->
  
  <!-- An XSLT test case to test, xsl:evaluate instruction.
       This stylesheet also does a custom XML serialization of 
       xdm map and array.
   -->							
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/root">
    <xsl:variable name="map1" as="item()">
      <xsl:evaluate xpath="xpath" context-item="/root"/>
    </xsl:variable>
	<xsl:call-template name="emitMap">
	   <xsl:with-param name="map1" select="$map1" as="map(*)"/>	   
	</xsl:call-template>
  </xsl:template>
  
  <!-- An XSL recursive named template, to emit xdm map as 
       an element node. -->
  <xsl:template name="emitMap">
	 <xsl:param name="map1" as="map(*)"/>
	 <map>
		 <xsl:for-each select="sort(map:keys($map1))">
		    <xsl:variable name="key" select="."/>
			<xsl:variable name="value" select="map:get($map1, $key)"/>
			<entry key="{$key}">
				<xsl:choose>
				   <xsl:when test="$value instance of map(*)">
					  <xsl:call-template name="emitMap">
						<xsl:with-param name="map1" select="$value" as="map(*)"/>	   
					  </xsl:call-template>
				   </xsl:when>
				   <xsl:when test="$value instance of array(*)">
					  <xsl:call-template name="emitArray">
						<xsl:with-param name="array1" select="$value" as="array(*)"/>	   
					  </xsl:call-template>
				   </xsl:when>
				   <xsl:otherwise>
					  <value>
						 <xsl:value-of select="$value"/>
					  </value>
				   </xsl:otherwise>
				</xsl:choose>
			</entry>
		 </xsl:for-each>
	 </map>
  </xsl:template>
  
  <!-- An XSL recursive named template, to emit xdm array as 
       an element node. -->
  <xsl:template name="emitArray">
	 <xsl:param name="array1" as="array(*)"/>
	 <array>
	    <xsl:for-each select="1 to array:size($array1)">
	       <xsl:variable name="value" select="array:get($array1, .)"/>
		   <item>
		      <xsl:choose>
				 <xsl:when test="$value instance of map(*)">
					<xsl:call-template name="emitMap">
				       <xsl:with-param name="map1" select="$value" as="map(*)"/>	   
					</xsl:call-template>
				 </xsl:when>
				 <xsl:when test="$value instance of array(*)">
					<xsl:call-template name="emitArray">
					   <xsl:with-param name="array1" select="$value" as="array(*)"/>	   
					</xsl:call-template>
				 </xsl:when>
				 <xsl:otherwise>
					<xsl:value-of select="$value"/>
				 </xsl:otherwise>
			  </xsl:choose>
		   </item>
	    </xsl:for-each>
	 </array>
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
