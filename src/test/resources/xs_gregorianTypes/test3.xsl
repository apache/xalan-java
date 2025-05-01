<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XML Schema types xs:gDay, xs:gMonth -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>	      
		  <one><xsl:value-of select="xs:gDay('---10') eq xs:gDay('---10')"/></one>
		  <two><xsl:value-of select="xs:gDay('---10') ne xs:gDay('---08')"/></two>
		  
		  <three><xsl:value-of select="xs:gMonth('--05') eq xs:gMonth('--05')"/></three>
		  <four><xsl:value-of select="xs:gMonth('--05') ne xs:gMonth('--10')"/></four>
		  
		  <xsl:variable name="val1" select="xs:gDay('---05')" as="xs:gDay"/>
		  <five><xsl:value-of select="$val1 eq $val1"/></five>
		  <six><xsl:value-of select="$val1 ne $val1"/></six>
		  
		  <xsl:variable name="val2" select="xs:gMonth('--07')" as="xs:gMonth"/>
		  <seven><xsl:value-of select="$val2 eq $val2"/></seven>
		  <eight><xsl:value-of select="$val2 ne xs:gMonth('--05')"/></eight>
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
