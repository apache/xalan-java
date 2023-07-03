<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- An XSLT stylesheet to test, using XPath function item
        "inline function" in various ways. -->                 

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="func1" select="function($x) { $x + 1 }"/>
   	 
   <xsl:variable name="func2" select="function($x) { $x + 3 }"/>
   	 
   <xsl:variable name="func3" select="function($x) { count(3 to $x) lt 4 }"/>
   	 
   <xsl:variable name="func4" select="function($x) { count(3 to $x) ge 4 }"/>
   
   <xsl:variable name="func5" select="function($x) { ($x mod 2) eq 0 }"/>

   <xsl:template match="/elem">
      <result>
	     <one>
	        <xsl:copy-of select="for-each(list/idx, $func1)"/>
	     </one>	 
	     <two>
	        <xsl:copy-of select="for-each(list/idx, $func2)"/>
	     </two>	 
	     <three>
	        <xsl:copy-of select="filter(list/idx, $func3)"/>
	     </three>	 
	     <four>
	        <xsl:copy-of select="filter(list/idx, $func4)"/>
	     </four>
	     <five>
	        <xsl:variable name="evenNums" select="filter(list/idx, $func5)"/>
	        <xsl:copy-of select="for-each($evenNums, function($x) { $x * 2 })"/>
	     </five>
	     <six>
	        <xsl:copy-of select="for-each(filter(list/idx, $func5), function($x) { $x * 2 })"/>
	     </six>
	     <seven>
	         <xsl:apply-templates select="list/idx">
	            <xsl:with-param name="funcA" select="function($x) { $x * 2 }"/>
	         </xsl:apply-templates>
	     </seven>
      </result>
   </xsl:template>
   
   <!-- A template, expecting a function item "inline function" 
        argument. -->
   <xsl:template match="idx">
      <xsl:param name="funcA"/>
      <sub>
         <xsl:value-of select="for-each(1 to ., $funcA)"/>
      </sub>
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