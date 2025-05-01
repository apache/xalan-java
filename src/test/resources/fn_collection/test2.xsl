<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"                			
				exclude-result-prefixes="xs"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:collection. 
         Xalan-J XSL 3 fn:collection function's implementation, recognizes file names with 
         only one of following extension names : xml, json and txt (files with any other 
         extension names are filtered out by Xalan-J's fn:collection function's 
         implementation).
         
         This XSL test case, processes all XML file documents available within a specific 
         file system's local folder, and combines information from all those XML documents by 
         applying xsl:for-each-group instruction as well.
         
         Xalan-J XSL 3 fn:collection function's implementation, follows a convention of
         absolute URIs whose trailing file name suffix is specified by a regex (with variety 
         defined by XPath 3.1 F&O spec), and absolute URI's prefix are fixed string values to 
         be specified by stylesheet's author.
    -->					

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:param name="localBaseUriPrefix" as="xs:string"/>
	
	<xsl:variable name="search_word_str" select="'hello'" as="xs:string"/>

	<xsl:template match="/">
	   <groups>
	      <xsl:variable name="collectionResult1" select="collection($localBaseUriPrefix || 'fn_collection/differentfile.*[.]xml')"/>
		  <xsl:variable name="nodeSet1">
		     <xsl:for-each select="$collectionResult1">
			    <xsl:for-each select="./*/*">
				  <xsl:copy-of select="."/>
				</xsl:for-each>
			 </xsl:for-each>
		  </xsl:variable>
	      <xsl:for-each-group select="$nodeSet1/*" group-by="contains(string(.), $search_word_str)">
		     <group key="{if (current-grouping-key() eq true()) then ('contains_word_' || $search_word_str) else ('doesnt_contain_word_' || $search_word_str)}">
			    <xsl:copy-of select="current-group()"/>
			 </group>
		  </xsl:for-each-group>
	   </groups>
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