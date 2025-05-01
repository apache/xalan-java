<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="map"
                version="3.0">                            

    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT stylesheet test, to test the XPath 3.1 map:merge() 
         function. This stylesheet merges a sequence of maps with
         key duplicates resolution policy as 'use-last'.
                  
         The examples of, function map:merge as used within this stylesheet,
         are borrowed from XPath 3.1 F&O spec.
    -->
    
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <xsl:variable name="map1" select="map{0 : 'Sonntag', 1 : 'Montag', 2 : 'Dienstag', 3 : 'Mittwoch', 4 : 'Donnerstag', 5 : 'Freitag', 6 : 'Samstag'}"/>
	   <xsl:variable name="map2" select="map{6 : 'Sonnabend'}"/>
	   <xsl:variable name="mapSeq1" select="($map1,$map2)"/>
	   <xsl:variable name="options" select="map {'duplicates' : 'use-last'}"/>
	   <xsl:variable name="mapAfterMerge" select="map:merge($mapSeq1, $options)"/>
	   <xsl:variable name="mapXmlVar">
	      <xsl:for-each select="map:keys($mapAfterMerge)">
		    <xsl:variable name="key" select="."/>
		    <entry key="{$key}">
		      <xsl:value-of select="map:get($mapAfterMerge, $key)"/>
		    </entry>
	      </xsl:for-each>
	   </xsl:variable>
	   <map>
	     <!-- We sort the result of map:merge function, to have
	          a predictable order of map keys within output. -->	     
		 <xsl:for-each select="$mapXmlVar/entry">
		   <xsl:sort select="@key" order="ascending" data-type="number"/>
		   <xsl:copy-of select="."/>
		 </xsl:for-each>
	   </map>
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
