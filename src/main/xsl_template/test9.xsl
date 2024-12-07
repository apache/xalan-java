<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs" 
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case to test, named templates returning 
       function items. -->								
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
	    <xsl:for-each select="1 to 3">
           <xsl:variable name="func1" as="function(*)">
		      <xsl:call-template name="getFuncItem">
		        <xsl:with-param name="n0" select="."/>
		      </xsl:call-template>			  
		   </xsl:variable>
		   <value>
			  <xsl:value-of select="$func1(5,7)"/>
		   </value>
        </xsl:for-each>
	 </result>
  </xsl:template>
  
  <xsl:template name="getFuncItem" as="function(*)">
     <xsl:param name="n0" as="xs:integer"/>
	 <xsl:choose>
		<xsl:when test="$n0 eq 1">
		   <xsl:sequence select="function($a,$b) { $a + $b }"/>
		</xsl:when>
		<xsl:when test="$n0 eq 2">
		   <xsl:sequence select="function($a,$b) { $a - $b }"/>
		</xsl:when>
		<xsl:when test="$n0 eq 3">
		   <xsl:sequence select="function($a,$b) { $a * $b }"/>		   
		</xsl:when>
	  </xsl:choose>
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
