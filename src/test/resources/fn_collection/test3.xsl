<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"				
				exclude-result-prefixes="xs map array"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 fn:collection function.
         This stylesheet test case, takes as input all XML and JSON file documents
         from file system's local folder combines information from all those documents
         and produces specific 'XML serialization' as result of this stylesheet's 
         processing.
         
         Xalan-J XSL 3 fn:collection function's implementation, follows a convention of
         absolute URIs whose trailing file name suffix is specified by a regex (with variety 
         defined by XPath 3.1 F&O spec), and absolute URI's prefix are fixed string values to 
         be specified by stylesheet's author.
     -->				

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:param name="localBaseUriPrefix" as="xs:string"/>

	<xsl:template match="/">
	   <result>
	      <xsl:variable name="collectionResult1" select="collection($localBaseUriPrefix || 'fn_collection/file4.*')"/>
		  <xsl:for-each select="$collectionResult1">
		     <xsl:variable name="collectionItem1" select="."/>
			 <xsl:choose>
			    <xsl:when test="$collectionItem1 instance of map(*)">
				   <xsl:variable name="infoItemArr1" select="map:get($collectionItem1, 'info')" as="array(*)"/>
				   <xsl:for-each select="1 to array:size($infoItemArr1)">
				      <xsl:variable name="map1" select="array:get($infoItemArr1, .)" as="map(*)"/>
					  <xsl:variable name="keyStrSeq1" select="map:keys($map1)" as="xs:string*"/>
					  <xsl:element name="{$keyStrSeq1}">					     
					     <xsl:value-of select="map:get($map1, $keyStrSeq1)"/>
					  </xsl:element>
				   </xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
				   <xsl:copy-of select="$collectionItem1/info/*"/>
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
