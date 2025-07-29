<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
   
    <!-- An XPath 3.1 test case, to test an XPath 'map' expression 
         and navigating maps via XPath map functions. -->                 				
				
    <xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
		  <xsl:variable name="m1" select="fn0:getMap1()" as="item()"/>
		  <xsl:for-each select="sort(map:keys($m1))">
		     <key>
			    <xsl:value-of select="."/>
			 </key>
			 <value>
			    <xsl:value-of select="map:get($m1,.)"/>
			 </value>
		  </xsl:for-each>
	   </result>
	</xsl:template>
	
	<xsl:function name="fn0:getMap1" as="item()">
	   <xsl:sequence select="map {'a' : 1, 'b' : 2, 'c' : 3, 'd' : 4, 'e' : 5}"/>
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
