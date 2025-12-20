<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
			   exclude-result-prefixes="xs"
               expand-text="yes" 
               version="3.0">
               
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case, to test XPath 3.1 map 
        and array unary lookup expressions. -->               

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="map-sequence1" select="(map {'one' : 5, 'two' : 6, 1 : 'hello'}, map {'one' : 2, 'two' : 3, 1 : 'hello1'})" as="map(*)*"/>

   <xsl:variable name="array-sequence1" select="([5, 10, 7, 8], [2, 12])" as="array(xs:integer)*"/>   

   <xsl:template match="/">
	  <result>
	     <xsl:for-each select="$map-sequence1">
		    <map>
			   <one>{?one}</one>
			   <two>{?two}</two>
			   <three>{?1}</three>
			   <allValues>{?*}</allValues>
			</map>
		 </xsl:for-each>		 
		 <xsl:for-each select="$array-sequence1">			
		    <array>
			   <item>{?2}</item>
			   <allValues>{?*}</allValues>
			</array>
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
   
</xsl:transform>
