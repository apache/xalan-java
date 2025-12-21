<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
				expand-text="yes"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case, to test XPath 3.1 map 
        unary lookup expressions. -->                  
				
   <xsl:output method="xml" indent="yes"/>				
   
   <xsl:template match="/">
      <result>
	     <xsl:variable name="map-sequence1" select="(map {'a' : 1, 'b' : 2, 'c' : 3}, map {'a' : 5, 'b' : 6, 'c' : 7})" as="map(*)*"/>
		 <xsl:for-each select="$map-sequence1">
		    <xsl:variable name="bVal" select="?b" as="xs:integer"/>
			<temp>
			   <b1>{$bVal + 10}</b1>
			   <b2>{5 + $bVal}</b2>
			</temp>
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