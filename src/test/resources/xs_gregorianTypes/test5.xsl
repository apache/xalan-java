<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath castable as 
         operator on gregorian typed values. -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
	      <test1>
			  <one><xsl:value-of select="'2005-10' castable as xs:gYearMonth"/></one>
			  <two><xsl:value-of select="xs:string('2005-10') castable as xs:gYearMonth"/></two>
		  </test1>
		  <test2>
			  <one><xsl:value-of select="'2005' castable as xs:gYear"/></one>
			  <two><xsl:value-of select="xs:string('2005') castable as xs:gYear"/></two>
		  </test2>
		  <test3>
			  <one><xsl:value-of select="'--05-10' castable as xs:gMonthDay"/></one>
			  <two><xsl:value-of select="xs:string('--05-10') castable as xs:gMonthDay"/></two>
		  </test3>
		  <test4>
			  <one><xsl:value-of select="'---10' castable as xs:gDay"/></one>
			  <two><xsl:value-of select="xs:string('---10') castable as xs:gDay"/></two>
		  </test4>
		  <test5>
			  <one><xsl:value-of select="'--10' castable as xs:gMonth"/></one>
			  <two><xsl:value-of select="xs:string('--10') castable as xs:gMonth"/></two>
		  </test5>
		  <test6>
			  <one><xsl:value-of select="let $b := '--10' castable as xs:gMonth return not($b)"/></one>
			  <two><xsl:value-of select="let $b := xs:string('--10') castable as xs:gMonth return not($b)"/></two>
		  </test6>
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
