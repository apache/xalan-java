<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath function fn:xml-to-json using 
         strong type checking of intermediate information. -->				

	<xsl:output method="json"/>

	<xsl:template match="/">
	   <xsl:variable name="xmlMapNodeVar1">
		   <map xmlns="http://www.w3.org/2005/xpath-functions">		  
			  <xsl:variable name="seq1" select="(xs:gYearMonth('2005-10'), xs:gYearMonth('2007-10'))"/>
			  <number key="countVal1"><xsl:value-of select="count($seq1)"/></number>
			  <xsl:variable name="seq2" select="(xs:gYearMonth('2005-10'))" as="item()?"/>
			  <number key="countVal2"><xsl:value-of select="count($seq2)"/></number>
			  <xsl:variable name="seq3" select="(xs:gYearMonth('2005-10'), xs:gYearMonth('2007-10'), xs:gYearMonth('2010-10'))" as="item()*"/>
			  <number key="countVal3"><xsl:value-of select="count($seq3)"/></number>
			  <xsl:variable name="seq4" select="(xs:gYearMonth('2005-10'))" as="xs:gYearMonth?"/>
			  <number key="countVal4"><xsl:value-of select="count($seq4)"/></number>
			  <xsl:variable name="seq5" select="(xs:gYearMonth('2005-10'), xs:gYearMonth('2007-10'), xs:gYearMonth('2010-10'))" as="xs:gYearMonth*"/>
			  <number key="countVal5"><xsl:value-of select="count($seq5)"/></number>
		   </map>
	   </xsl:variable>
	   <xsl:value-of select="xml-to-json($xmlMapNodeVar1, map {'indent' : true()})"/> 
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
