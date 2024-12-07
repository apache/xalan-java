<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="map"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT test case, to test XPath 3.1 maps and, having map 
         literal expression as function argument to function map:merge. -->                                            

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
	   <result>
		   <xsl:variable name="map1" select="map {1 : 'a', 2 : 'b', 3 : 'c'}"/>
		   <xsl:variable name="map2" select="map {4 : 'd', 2 : 'e', 5 : 'f'}"/>
		   <xsl:variable name="maps" select="($map1, $map2)"/>
		   <xsl:variable name="mapMergeResult" select="map:merge($maps, map {'duplicates' : 'use-first'})"/>
		   <map>
		     <!-- Its useful within a test case, to have fn:sort with map:keys as argument. -->	     
			 <xsl:for-each select="sort(map:keys($mapMergeResult))">
			   <entry>
				 <key><xsl:value-of select="."/></key>
				 <value><xsl:value-of select="map:get($mapMergeResult, .)"/></value>
			   </entry>
			 </xsl:for-each>
		   </map>
		   <xsl:variable name="mapMergeResult" select="map:merge($maps, map {'duplicates' : 'use-last'})"/>
		   <map>
		     <!-- Its useful within a test case, to have fn:sort with map:keys as argument. -->	     
			 <xsl:for-each select="sort(map:keys($mapMergeResult))">
			   <entry>
				 <key><xsl:value-of select="."/></key>
				 <value><xsl:value-of select="map:get($mapMergeResult, .)"/></value>
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
