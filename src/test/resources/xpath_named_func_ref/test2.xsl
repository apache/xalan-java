<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="math map"
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT test case, to test XPath 3.1 named function references. -->				
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match='/'>
     <result>
	   <one>
         <xsl:value-of select="for-each((1,2,4,16,25), math:sqrt#1)"/>
	   </one>
       <two>
	     <xsl:variable name="map1" select="map {'a' : 1, 'b' : 2}"/>
		 <xsl:variable name="map2" select="map {'p' : 5}"/>
		 <xsl:variable name="map3" select="map {'q' : 3, 'r' : 4, 's' : 5}"/>
         <xsl:value-of select="for-each(($map1,$map2,$map3), map:size#1)"/>
	   </two>	   
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
