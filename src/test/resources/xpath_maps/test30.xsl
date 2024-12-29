<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="array"
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
       <book>
		  <xsl:variable name="bookVar1" select="$map1('book')" as="map(*)"/>
		  <title>		    
		 	 <xsl:value-of select="$bookVar1('title')"/>
		  </title>
		  <year>		    
		 	 <xsl:value-of select="$bookVar1('year')"/>
		  </year>
		  <author>
			 <xsl:variable name="bookAuthorVar1" select="$bookVar1('author')" as="array(*)"/>
			 <xsl:for-each select="1 to array:size($bookAuthorVar1)">
				<xsl:variable name="bookAuthor" select="array:get($bookAuthorVar1, .)" as="map(*)"/>
				<xsl:value-of select="if (. lt array:size($bookAuthorVar1)) then ($bookAuthor('first') || ' ' || $bookAuthor('last') || ', ') 
																				else 
																				 ($bookAuthor('first') || ' ' || $bookAuthor('last'))"/>
		     </xsl:for-each>
		  </author>
		  <publisher>		    
		 	 <xsl:value-of select="$bookVar1('publisher')"/>
		  </publisher>
		  <price>		    
		 	 <xsl:value-of select="$bookVar1('price')"/>
		  </price>
	   </book>			      
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
