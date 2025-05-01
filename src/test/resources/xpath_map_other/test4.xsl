<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"				
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="xs map array"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->                
   
  <!-- An XSL test case to test literal map and array expressions, and 
       accessing map and array using function call syntax. -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
        <!-- XPath literal map example. This sequence type of this map specifies : every key 
		     is an instance of xs:string and the type of every value is an instance of xs:double. -->	 
		<xsl:variable name="map1" select="map {'a':1.1, 'b':1.2, 'c':1.3, 'd':1.4, 'e':1.5, 'f':'1.6', 'g':1.7}" as="map(xs:string, xs:double)"/> 
		<map>
		   <xsl:for-each select="sort(map:keys($map1))">
		     <entry key="{.}"><xsl:value-of select="$map1(.)"/></entry>
		   </xsl:for-each>
		</map>
		<!-- XPath literal array example. This sequence type of this array specifies : every 
		     array item is an instance of xs:double. -->
		<xsl:variable name="arr1" select="[1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1]" as="array(xs:double)"/>
		<array>
			<xsl:for-each select="1 to array:size($arr1)">
			   <value><xsl:value-of select="$arr1(.)"/></value>
			</xsl:for-each>
		</array>
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
