<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                exclude-result-prefixes="map"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case to test, xsl:value-of instruction producing 
       result by evaluating contained sequence constructor. This stylesheet test 
       case uses xsl:value-of's 'separator' attribute, and an XSLT named template's 
       evaluation result as xsl:value-of instruction's result.  -->			    
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
	    <xsl:value-of separator=",">
		  <xsl:call-template name="getElemSequence"/>
		</xsl:value-of>
	 </result>
  </xsl:template>
  
  <!-- An XSL stylesheet named template definition, to produce 
       an XML element sequence. -->
  <xsl:template name="getElemSequence">
    <xsl:variable name="map1" select="map {'a' : 1, 'b' : 2, 'c' : 3}"/>
    <xsl:for-each select="('a', 'b', 'c')">
	  <xsl:variable name="item1" select="."/>
	  <xsl:element name="{$item1}">
	    <xsl:value-of select="map:get($map1, $item1)"/>
	  </xsl:element>
	</xsl:for-each>
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
