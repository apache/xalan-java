<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"				
				exclude-result-prefixes="xs map array"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:collection. 
         Xalan-J XSL 3 fn:collection function's implementation, recognizes file names with 
         only one of following extension names : xml, json and txt (files with any other 
         extension names are filtered by Xalan-J's fn:collection function's implementation).
         
         Xalan-J XSL 3 fn:collection function's implementation, follows a convention of
         absolute URIs whose trailing file name suffix is specified by a regex (with variety 
         defined by XPath 3.1 F&O spec), and absolute URI's prefix are fixed string values to 
         be specified by stylesheet's author.
    -->				

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:param name="localBaseUriPrefix" as="xs:string"/>

	<xsl:template match="/">
	   <result>
	      <xsl:variable name="collectionResult1" select="collection($localBaseUriPrefix || 'fn_collection/.*[.](xml|json|txt)')"/>
	      <xsl:for-each select="$collectionResult1">
				<xsl:variable name="collectionResult1" select="."/>
				<xsl:choose>
				   <xsl:when test="$collectionResult1 instance of map(*)">
				      <!-- Process a JSON document which begins with json map -->
					  <xsl:variable name="map1" select="$collectionResult1" as="map(*)"/>
					  <map>
						 <xsl:for-each select="map:keys($map1)">
						   <entry key="{.}"><xsl:value-of select="map:get($map1, .)"/></entry>
						 </xsl:for-each>
					  </map>
				   </xsl:when>
				   <xsl:when test="$collectionResult1 instance of array(*)">
				      <!-- Process a JSON document which begins with json array -->
					  <array>
						 <xsl:for-each select="1 to array:size($collectionResult1)">
						   <item><xsl:value-of select="array:get($collectionResult1, xs:integer(.))"/></item>
						 </xsl:for-each>
					  </array>
				   </xsl:when>
				   <xsl:when test="$collectionResult1 instance of xs:string">
				      <!-- Process a file text document. -->
					  <txtContents><xsl:value-of select="$collectionResult1"/></txtContents>
				   </xsl:when>
				   <xsl:otherwise>
					  <xmlContents>
						 <xsl:copy-of select="."/>
					  </xmlContents>
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