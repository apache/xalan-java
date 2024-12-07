<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="map"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT test case, to test XPath 3.1 maps and function map:get. -->                                            

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
	   <xsl:variable name="map1" select="map{0 : 'Sonntag', 1 : 'Montag', 2 : 'Dienstag', 3 : 
	                                        'Mittwoch', 4 : 'Donnerstag', 5 : 'Freitag', 6 : 'Samstag'}"/>	   
	   <map>	     
		 <xsl:for-each select="0 to 6">
		   <entry>
			 <key><xsl:value-of select="."/></key>
			 <value><xsl:value-of select="map:get($map1, .)"/></value>
		   </entry>
		 </xsl:for-each>
	   </map>
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
