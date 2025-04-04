<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                exclude-result-prefixes="xs map"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:transform.
       
       This stylesheet test case, tests XSL transformation using a secondary 
       stylesheet (render2.xsl in this example) that has xsl:result-document 
       instruction. An xsl:result-document instruction's result output 
       produced via this way, are accumulated within function fn:transform's 
       XDM result map.       
  -->			    									

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <result>
		  <xsl:variable name="result" select="transform(
														map {
														   'stylesheet-location' : 'render2.xsl',
														   'source-node' : doc('test1.xml'),
														   'base-output-uri' : 'trfout',
														   'delivery-format' : 'document'
														})" as="map(*)"/>          														
		  <xsl:for-each select="sort(map:keys($result))">
		     <xsl:choose>
			    <xsl:when test="contains(., '/')">
		           <xsl:variable name="urlParts" select="tokenize(xs:string(.), '/')" as="xs:string*"/>
		           <xsl:variable name="urlSuffixStr" select="$urlParts[count($urlParts)]" as="xs:string"/>
		           <doc resultOuputRelativeUri="{$urlSuffixStr}">
                      <xsl:copy-of select="$result(.)"/>
			       </doc>
			    </xsl:when>
			    <xsl:otherwise>
			       <doc resultOuputRelativeUri="{.}">
                     <xsl:copy-of select="$result(.)"/>
			       </doc>
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
  