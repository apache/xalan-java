<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"
                version="3.0">              
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1.xml -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath 3.1 function fn:serialize 
         method 'adaptive'. -->               				 
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/">
	   <result>
		  <one>
		     <xsl:variable name="arr1" select="[1, 2, 3]" as="array(*)"/>
			 <xsl:value-of select="serialize($arr1, map{'method':'adaptive'})"/>
		  </one>
		  <two>
		     <xsl:variable name="map1" select="map {'a1':1, 'a2':2}" as="map(*)"/>
			 <xsl:value-of select="serialize($map1, map{'method':'adaptive'})"/>
		  </two>
		  <three>
		     <xsl:variable name="map1" select="map {'a1':1, 'a2':[1, 2, 3], 'a3':map{'b1':5}}" as="map(*)"/>
			 <xsl:value-of select="serialize($map1, map{'method':'adaptive'})"/>
		  </three>
		  <four>
		     <xsl:value-of select="serialize(function($a) { $a }, map{'method':'adaptive'})"/>
		  </four>
		  <five>
		      <a><xsl:value-of select="serialize(fn:exists#1, map{'method':'adaptive'})"/></a>
			  <b><xsl:value-of select="serialize(Q{http://www.w3.org/2005/xpath-functions}exists#1, map{'method':'adaptive'})"/></b>
			  <c><xsl:value-of select="serialize(math:pi#0, map{'method':'adaptive'})"/></c>
			  <d><xsl:value-of select="serialize(fn0:func1#1, map{'method':'adaptive'})"/></d>
		  </five>		  
	   </result>
	</xsl:template>
	
	<!-- An XSL stylesheet function declaration -->
	<xsl:function name="fn0:func1" as="item()">
	   <xsl:param name="a1" as="xs:string"/>
	   <xsl:sequence select="$a1"/>
	</xsl:function>
	
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
