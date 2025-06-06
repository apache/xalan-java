<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="#all"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath dynamic function call syntax 
         with chained arguments for information lookup within a source array. This makes 
         it easier to access specific information from an XDM array, without using other 
         XSL stylesheet language facilities. -->				
	
	<xsl:output method="xml" indent="yes"/>
	
    <xsl:template match="/">       
	   <result>
	      <xsl:variable name="array1" select="parse-json(unparsed-text('test3.json'))" as="array(*)"/>
		  <one>
		     <xsl:value-of select="$array1(4)('d')(2)('p')"/>,<xsl:value-of select="$array1(4)('d')(2)('q')"/>
		  </one>
		  <two>
		     <xsl:value-of select="$array1(4)('f')(2)(1)"/>,<xsl:value-of select="$array1(4)('f')(2)(2)"/>
		  </two>
		  <three>
		     <xsl:value-of select="$array1(4)('g')(2)(1)('one')"/>,<xsl:value-of select="$array1(4)('g')(2)(1)('two')"/>
		  </three>
		  <four>
		     <xsl:variable name="arr2" select="$array1(4)('f')(2)" as="array(xs:string)"/>
			 <array>
			   <xsl:for-each select="1 to array:size($arr2)">
			     <item><xsl:value-of select="$arr2(.)"/></item>
			   </xsl:for-each>
			 </array>
		  </four>
		  <five>
		     <xsl:variable name="map2" select="$array1(4)('g')(2)(1)" as="map(xs:string, xs:string)"/>
			 <map>
			    <xsl:for-each select="map:keys($map2)">
				   <entry key="{.}" value="{map:get($map2, .)}"/>
				</xsl:for-each>
			 </map>
		  </five>
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
