<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"               
                xmlns:fn="http://www.w3.org/2005/xpath-functions"				
                xmlns="http://www.w3.org/2005/xpath-functions"				
				exclude-result-prefixes="#all"
                version="3.0"> 
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test xsl:sort instruction,
       on a result produced by XPath 3.1 function fn:json-to-xml. 
  -->                            				

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="jsonStr1" as="xs:string">
     {
	    "hellr" : 1,
		"hellp" : {
		   "z" : 10,
		   "x" : 20,
		   "y" : 30 
		},
		"hello" : {
		   "c" : 10,
		   "b" : {
		      "Mu" : 1.1,
			  "Beta" : 2.1,
			  "Alpha" : 3.1
		   },
		   "a" : 30
		},
		"hellq" : 4,
		"hells" : 5
	 }
  </xsl:variable>
  
  <xsl:template match="/">
	 <xsl:variable name="map1" select="json-to-xml($jsonStr1)"/>
	 <xsl:call-template name="sortedXdmMap">
	   <xsl:with-param name="map1" select="$map1/fn:map"/>
	 </xsl:call-template>
  </xsl:template>
  
  <!-- An XSL stylesheet recursive named template, to sort
       {http://www.w3.org/2005/xpath-functions}map elements on 
       their 'key' attribute. -->
  <xsl:template name="sortedXdmMap">
     <xsl:param name="map1"/>
	 <map>
	     <xsl:if test="$map1/@key != ''">
		    <xsl:attribute name="key" select="$map1/@key"/>
		 </xsl:if>
		 <xsl:for-each select="$map1/*">
			<xsl:sort select="@key"/>
			<xsl:choose>
			   <xsl:when test="self::fn:map">
				  <xsl:call-template name="sortedXdmMap">
					 <xsl:with-param name="map1" select="."/>
				  </xsl:call-template>
			   </xsl:when>
			   <xsl:otherwise>
				  <xsl:copy-of select="."/>
			   </xsl:otherwise>
			</xsl:choose>
		 </xsl:for-each>
	 </map>
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
