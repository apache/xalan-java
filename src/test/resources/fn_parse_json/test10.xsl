<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="#all"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath function fn:parse-json's 
         duplicate key handling options 'use-first' and 'use-last'. -->                 				
				
    <xsl:output method="xml" indent="yes"/>

    <!-- Uses 'liberal' : false and 'duplicates': 'use-first' by default -->
    <xsl:variable name="m1" select="parse-json(unparsed-text('test7.json'))" as="map(xs:string, xs:double)"/>

    <xsl:variable name="m2" select="parse-json(unparsed-text('test7.json'), map {'duplicates': 'use-last'})" as="map(xs:string, xs:double)"/>

	<xsl:template match="/">
	   <result>
	      <one>
			  <xsl:for-each select="sort(map:keys($m1))">
				<xsl:variable name="var1" select="map:get($m1,.)" as="xs:double"/>
				<key name="{.}">
				   <xsl:value-of select="$var1"/>
				</key>
			  </xsl:for-each>		  
		  </one>
		  <two>
			  <xsl:for-each select="sort(map:keys($m2))">
				<xsl:variable name="var2" select="map:get($m2,.)" as="xs:double"/>
				<key name="{.}">
				   <xsl:value-of select="$var2"/>
				</key>
			  </xsl:for-each>		  
		  </two>
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
