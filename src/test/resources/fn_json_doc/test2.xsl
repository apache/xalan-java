<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="#all"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test an XPath 3.1 fn:json-doc 
         function call. -->                 				
				
    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="m1" select="json-doc('item_doc1.json')" as="map(xs:string, map(*))"/>

	<xsl:template match="/">
	   <result>
		   <xsl:for-each select="sort(map:keys($m1))">
		      <xsl:variable name="itemMap1" select="map:get($m1,.)" as="map(*)"/>
			  <xsl:variable name="id" select="map:get($itemMap1, 'id')" as="xs:double"/>
			  <xsl:variable name="desc" select="map:get($itemMap1, 'desc')" as="xs:string"/>
			  <xsl:variable name="info" select="map:get($itemMap1, 'info')" as="map(xs:string, xs:anyAtomicType)"/>
			  <xsl:variable name="size" select="map:get($info, 'size')" as="xs:double"/>
			  <xsl:variable name="weight" select="map:get($info, 'weight')" as="xs:double"/>
			  <xsl:variable name="color" select="map:get($info, 'color')" as="xs:string"/>
			  <object key="{.}">
				 <id>
				    <xsl:value-of select="$id"/>
				 </id>
				 <desc>
					<xsl:value-of select="$desc"/>
				 </desc>
				 <info size="{$size}" weight="{$weight}" color="{$color}"/>
			  </object>
		   </xsl:for-each>
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
