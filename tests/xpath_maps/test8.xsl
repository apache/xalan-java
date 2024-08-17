<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="map array"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                
   
   <!-- An XSLT test case to test, XPath 3.1 map constructor expression
        that nests other maps and arrays. -->
   
   <xsl:output method="xml" indent="yes"/>                			

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <xsl:variable name="map1" select="map {
			      'one' : 1,
				  'two' : 2,
				  'three' : ['a', 'b', 'c']
			    }"/>
	  <xsl:variable name="map2" select="map {
			      'one' : [1, 2, 3, 4, 5, 6, 7],
				  'two' : 2,
				  'three' : [1, 2, 3]
			    }"/>
	  <xsl:variable name="map3" select="map {
			    'one' : map {
				           'x1' : 'p',
						   'x2' : 'q',
						   'x3' : 'r',
                           'x4' : 's', 
                           'x5' : 't' 						   
				        },
				'two' : 2,
				'three' : [5, 6, 7, 8, 9, 'a1']
			  }"/>
      <result>
         <xsl:variable name="arr1" select="map:get($map1, 'three')"/>		 
		 <xsl:variable name="arr2" select="map:get($map2, 'one')"/>
		 <xsl:variable name="val1" select="map:get($map2, 'two')"/>
		 <xsl:variable name="arr3" select="map:get($map2, 'three')"/>
		 <one>
		    <xsl:value-of select="array:size($arr1)"/>
		 </one>
		 <two>
		    <xsl:value-of select="array:size($arr2)"/>
		 </two>
		 <three>
		    <xsl:value-of select="$val1"/>
		 </three>
		 <four arrSize="{array:size($arr3)}">
		    <xsl:copy-of select="$arr3"/>
		 </four>
		 <five noOfMapEntries="{map:size($map3)}">
		    <entry1 isNestedMap="true">No of map entries within nested map : <xsl:value-of select="map:size(map:get($map3, 'one'))"/></entry1>
			<entry2 isMap="false">Value : <xsl:value-of select="map:get($map3, 'two')"/></entry2>
		 </five>
		 <six>
		    <xsl:value-of select="array:size(map:get($map3, 'three'))"/>
		 </six>
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
