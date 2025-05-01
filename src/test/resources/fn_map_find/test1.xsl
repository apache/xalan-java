<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="map array"				
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT test case to test, XPath 3.1 function map:find -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
     <result>
	    <xsl:variable name="seq1" select="(map {'a' : 1, 'p' : 2}, 20, map {'b' : 3, 'q' : 4}, map {'a' : 5, 'r' : 6})"/>
		<xsl:variable name="mapFindResult1" select="map:find($seq1, 'a')" as="array(*)"/>
		<array1 size="{array:size($mapFindResult1)}">
		   <xsl:value-of select="$mapFindResult1"/>
		</array1>
		<xsl:variable name="arr1" select="['hello', map {'a' : 1, 'p' : 2}, map {'b' : 3, 'q' : 4}, map {'a' : 5, 'p' : 6}, 30]"/>
		<xsl:variable name="mapFindResult2" select="map:find($arr1, 'p')" as="array(*)"/>
		<array2 size="{array:size($mapFindResult2)}">
		   <xsl:value-of select="$mapFindResult2"/>
		</array2>
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
