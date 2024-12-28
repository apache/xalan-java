<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="map array xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case to test, XPath map expression.
       The XPath map expression example used within this test case,
       has been borrowed from XPath 3.1 spec.
  -->	                
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
		<xsl:variable name="map1" select="map {
								     'book': map {
										   'title' : 'Data on the Web',
										   'year' : 2000,
										   'author' : [
												 map {
													'last' : 'Abiteboul',
													'first' : 'Serge'
												 },
												 map {
													'last' : 'Buneman',
													'first' : 'Peter'
												 },
												 map {
													'last' : 'Suciu',
													'first' : 'Dan'
												 }
											 ],
                                          'publisher': 'Morgan Kaufmann Publishers',
                                          'price': 39.95											 
								         } 
									  }" />
												  
		<xsl:variable name="bookMap1" select="map:get($map1, 'book')"/>
		<bookDetailsMap>
		   <xsl:for-each select="sort(map:keys($bookMap1))">
		      <xsl:variable name="keyName" select="."/>
		      <entry>
				  <key>
					<xsl:value-of select="$keyName"/>
				  </key>
				  <value>
					  <xsl:choose>					     
						 <xsl:when test="map:get($bookMap1, $keyName) instance of array(*)">
						    <xsl:variable name="arr1" select="map:get($bookMap1, $keyName)"/>
						    <xsl:value-of select="string-join(for $idx in 1 to array:size($arr1) return fn0:getAuthorDetails(array:get($arr1, $idx)), ', ')"/>
						 </xsl:when>
						 <xsl:otherwise>
							<value>
							   <xsl:value-of select="map:get($bookMap1, $keyName)"/>
							</value>
						 </xsl:otherwise>
					  </xsl:choose>
				  </value>
			  </entry>
		   </xsl:for-each>
		</bookDetailsMap>	      
  </xsl:template>
  
  <!-- An XSL function definition, to serialize information 
       about book author to a string value. -->
  <xsl:function name="fn0:getAuthorDetails" as="xs:string">
     <xsl:param name="map1" as="map(*)"/>
	 <xsl:sequence select="map:get($map1, 'first') || ' ' || map:get($map1, 'last')"/>
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
