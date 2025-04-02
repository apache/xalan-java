<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test XPath 3.1 'map' expression an 
       expression like which can often be used as an argument to 
       XPath function call fn:transform. -->			    									

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <map>
		 <xsl:variable name="map1" select="map {'stylesheet-location' : 'render.xsl', 
		                                         'source-node' : doc('test1.xml')}" as="map(*)"/>
         <entry>
		    <key>stylesheet-location</key>
		    <value><xsl:value-of select="$map1('stylesheet-location')"/></value>
		 </entry>
         <entry>
		    <key>source-node</key>
		    <value><xsl:copy-of select="let $srcNode := $map1('source-node') return $srcNode/info/*"/></value>
		 </entry>		 
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
  