<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="xs map"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                 
				
   <xsl:output method="xml" indent="yes"/>
   
   <!-- An XSLT test case, to test XPath 3.1 function map:for-each. --> 
  
   <xsl:template match="/">
     <result>
	   <xsl:variable name="map1" select="map{1 : 'yes', 2 : 'no'}"/>
	   <xsl:variable name="action1" select="function($k, $v) { $k }"/>
	   <xsl:variable name="seq1" select="map:for-each($map1, $action1)"/>
	   <one itemCount="{count($seq1)}">
	     <ok>
		   <xsl:call-template name="exists">
		     <xsl:with-param name="seq1" select="$seq1"/>
			 <xsl:with-param name="value1" select="1"/>
		   </xsl:call-template>
		   <xsl:text> ### </xsl:text>
		   <xsl:call-template name="exists">
		     <xsl:with-param name="seq1" select="$seq1"/>
			 <xsl:with-param name="value1" select="2"/>
		   </xsl:call-template>
		 </ok>
	   </one>
	 </result>
   </xsl:template>
   
   <!-- An XSL named template, to check whether a sequence 
        contains a specific value. -->
   <xsl:template name="exists" as="xs:boolean">
     <xsl:param name="seq1" as="xs:integer*"/>
	 <xsl:param name="value1" as="xs:integer"/>
	 <xsl:variable name="temp1">
	   <xsl:for-each select="$seq1">
	      <xsl:variable name="x1" select="."/>
	      <xsl:if test="$x1 eq $value1">
		    <yes/>
		  </xsl:if>
	   </xsl:for-each>
	 </xsl:variable>
	 <xsl:choose>
	   <xsl:when test="$temp1/yes">
	      <xsl:value-of select="xs:boolean(1)"/>
	   </xsl:when>
	   <xsl:otherwise>
	      <xsl:value-of select="xs:boolean(0)"/>
	   </xsl:otherwise>
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
