<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"                        
				version="3.0"
                exclude-result-prefixes="map">
                
   <!-- Author: mukulg@apache.org -->                
   
   <!-- An XPath 3.1 test case, to test an XPath 'map' 
        expression and navigating maps via XPath map
        functions. -->                

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <xsl:variable name="map1" select="map {0 : 'hello', 1 : 'there', 2 : ('yes','no')}"/>
	<xsl:variable name="map2" select="map {0 : 'hello1', 1 : ('yes1','no1')}"/>
	<info>
		<map1>       
		  <xsl:for-each select="sort(map:keys($map1))">
			<entry key="{.}" value="{map:get($map1, .)}"/>
		  </xsl:for-each>
		</map1>
		<map2>       
		  <xsl:for-each select="sort(map:keys($map2))">
			<entry key="{.}" value="{map:get($map2, .)}"/>
		  </xsl:for-each>
		</map2>
	</info>
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
