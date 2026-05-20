<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="3.0">
				
    <!-- Author: mukulg@apache.org --> 
    
    <!-- An XSL 3 stylesheet test case, to test an xdm map entry 
         lookup by key using an XPath expression like .(key),
         where an XPath context item is an xdm map. --> 				
				
    <xsl:output method="xml" indent="yes"/>				
				
    <xsl:variable name="mapSeq1" as="map(*)*" select='
								map{ "id" : 1, "value" : "abc" },
								map{ "id" : 2, "value" : "abc" },
								map{ "id" : 3, "value" : "mno" },
								map{ "id" : 4, "value" : "pqr" }'/>				
				
    <xsl:template match="/">
       <result>
	      <xsl:for-each select="$mapSeq1">
			 <map>
			    <key name="id"><xsl:value-of select=".('id')"/></key>
				<value><xsl:value-of select=".('value')"/></value>
			 </map>
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