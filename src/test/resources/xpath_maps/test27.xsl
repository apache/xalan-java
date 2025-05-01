<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                exclude-result-prefixes="map"                
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case to test, XPath map expression. -->				
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
        <xsl:variable name="key1" select="'a'"/>
        <xsl:variable name="value1" select="1"/>
        <xsl:variable name="key2" select="'b'"/>
        <xsl:variable name="value2" select="2"/>
        <xsl:variable name="key3" select="'c'"/>
        <xsl:variable name="value3" select="3"/>
	    <xsl:variable name="map1" select="map {$key1:$value1, $key2:$value2, $key3:$value3}"/>
		<map>
           <xsl:for-each select="sort(map:keys($map1))">
              <entry>
			     <key><xsl:value-of select="."/></key>
				 <value><xsl:value-of select="map:get($map1, .)"/></value>
			  </entry>
           </xsl:for-each>
		</map>
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