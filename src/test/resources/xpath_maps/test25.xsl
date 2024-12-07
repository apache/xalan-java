<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs map fn0"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1.xml -->                  			                 				
   
  <!-- An XSLT test case to test, xdm map item construction using 
       xsl:iterate instruction and map:put method calls getting data for 
       map entries from an XML external document. -->			    				
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/info">   
     <xsl:variable name="decisionMap" select="fn0:constructXdmDecisionMap(decision)" as="map(*)"/>
	 <result>
		<xsl:for-each select="sort(map:keys($decisionMap))">
		   <xsl:variable name="id" select="." as="xs:integer"/>
		   <decision id="{$id}" value="{map:get($decisionMap, $id)}">
			  <xsl:copy-of select="/info/details/person[xs:integer(decision) eq $id]/name"/>
		   </decision>
		</xsl:for-each>
	 </result>
  </xsl:template>
  
  <!-- An XSL function, to construct xdm map from data available 
       in an XML element node. -->
  <xsl:function name="fn0:constructXdmDecisionMap" as="map(*)">
     <xsl:param name="elem1" as="element(decision)"/>
	 <xsl:variable name="initKey" select="xs:integer($elem1/item[1]/id)" as="xs:integer"/>
	 <xsl:variable name="initValue" select="xs:string($elem1/item[1]/value)" as="xs:string"/>
	 <xsl:variable name="initMap" select="map {$initKey:$initValue}" as="map(*)"/> 
	 <xsl:iterate select="$elem1/item[position() &gt; 1]">
		<xsl:param name="map1" select="$initMap" as="map(*)"/>
		<xsl:on-completion>
		   <xsl:sequence select="$map1"/>
		</xsl:on-completion>
		<xsl:variable name="map2" select="map:put($map1, xs:integer(id), xs:string(value))" as="map(*)"/>		
		<xsl:next-iteration>
		  <xsl:with-param name="map1" select="$map2" as="map(*)"/>
		</xsl:next-iteration>
	 </xsl:iterate>	 
  </xsl:function>
  
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
