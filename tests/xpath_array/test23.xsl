<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"				
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="map array"                
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT test case to test, XPath 3.1 function array:flatten -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="jsonMap1" select="parse-json(unparsed-text('test1.json'))"/>

   <xsl:template match="/">
      <result>
	     <xsl:variable name="arr1" select="map:get($jsonMap1,'arr1')" as="array(*)"/>
	     <xsl:variable name="flattenedArr1" select="array:flatten($arr1)" as="item()*"/>
		 <items arrSizeBeforeFlatten="{array:size($arr1)}" countAfterFlatten="{count($flattenedArr1)}">
		    <before>
		       <xsl:for-each select="1 to array:size($arr1)">
			      <item>
                    <xsl:value-of select="array:get($arr1,.)"/>
                  </item>
			   </xsl:for-each>
			</before>
			<after>
			   <xsl:for-each select="$flattenedArr1">
			      <item>
                    <xsl:value-of select="."/>
                  </item>
			   </xsl:for-each>
			</after>
		 </items>
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
