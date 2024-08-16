<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="array"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSLT test case to test, XPath 3.1 literal arrays 
       with nesting of other literal arrays. --> 			    				
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
    <xsl:variable name="arr1" select="[1, [2, 3, 4], 5]"/>
	<xsl:variable name="arr2" select="[1, [2, 3, 4]]"/>
	<xsl:variable name="arr3" select="[[2, 3, 4], 1]"/>
	<xsl:variable name="arr4" select="[[2, 3, 4]]"/>
	<xsl:variable name="arr5" select="[[2, 3, 4], 1, 5]"/>
	<xsl:variable name="arr6" select="[1, 5, [2, 3, 4]]"/>	
	<result>
	   <array>
	      <xsl:for-each select="1 to array:size($arr1)">
	        <item>
			  <xsl:value-of select="array:get($arr1, .)"/>
			</item>
	      </xsl:for-each>
	   </array>
	   <otherArrays>
          <xsl:for-each select="($arr2, $arr3, $arr4, $arr5, $arr6)">
		     <xsl:variable name="arr" select="."/>
		     <size>
			    <xsl:value-of select="array:size($arr)"/>
			 </size>
		  </xsl:for-each>
	   </otherArrays>
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
