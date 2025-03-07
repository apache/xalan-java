<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test comparison of XDM 
         gregorian values using XPath eq and ne operators. -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
	      <test1 reason="comparison of xs:gYearMonth values">
			  <xsl:variable name="val1" select="xs:gYearMonth('2005-10')" as="xs:gYearMonth"/>
			  <xsl:variable name="val2" select="xs:gYearMonth('2007-10')" as="xs:gYearMonth"/>
			  <one><xsl:value-of select="$val1 eq $val1"/></one>
			  <two><xsl:value-of select="$val1 ne $val1"/></two>
			  <three><xsl:value-of select="$val1 eq $val2"/></three>
			  <four><xsl:value-of select="$val1 ne $val2"/></four>
		  </test1>
		  <test2 reason="comparison of xs:gYear values">
			  <xsl:variable name="val1" select="xs:gYear('2005')" as="xs:gYear"/>
			  <xsl:variable name="val2" select="xs:gYear('2007')" as="xs:gYear"/>
			  <one><xsl:value-of select="$val1 eq $val1"/></one>
			  <two><xsl:value-of select="$val1 ne $val1"/></two>
			  <three><xsl:value-of select="$val1 eq $val2"/></three>
			  <four><xsl:value-of select="$val1 ne $val2"/></four>
		  </test2>
		  <test3 reason="comparison of xs:gMonthDay values">
			  <xsl:variable name="val1" select="xs:gMonthDay('--05-10')" as="xs:gMonthDay"/>
			  <xsl:variable name="val2" select="xs:gMonthDay('--10-10')" as="xs:gMonthDay"/>
			  <one><xsl:value-of select="$val1 eq $val1"/></one>
			  <two><xsl:value-of select="$val1 ne $val1"/></two>
			  <three><xsl:value-of select="$val1 eq $val2"/></three>
			  <four><xsl:value-of select="$val1 ne $val2"/></four>
		  </test3>
		  <test4 reason="comparison of xs:gDay values">
			  <xsl:variable name="val1" select="xs:gDay('---05')" as="xs:gDay"/>
			  <xsl:variable name="val2" select="xs:gDay('---10')" as="xs:gDay"/>
			  <one><xsl:value-of select="$val1 eq $val1"/></one>
			  <two><xsl:value-of select="$val1 ne $val1"/></two>
			  <three><xsl:value-of select="$val1 eq $val2"/></three>
			  <four><xsl:value-of select="$val1 ne $val2"/></four>
		  </test4>
		  <test5 reason="comparison of xs:gMonth values">
			  <xsl:variable name="val1" select="xs:gMonth('--05')" as="xs:gMonth"/>
			  <xsl:variable name="val2" select="xs:gMonth('--10')" as="xs:gMonth"/>
			  <one><xsl:value-of select="$val1 eq $val1"/></one>
			  <two><xsl:value-of select="$val1 ne $val1"/></two>
			  <three><xsl:value-of select="$val1 eq $val2"/></three>
			  <four><xsl:value-of select="$val1 ne $val2"/></four>
		  </test5>
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
