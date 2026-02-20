<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
                version="3.0">
				
    <!-- Author: mukulg@apache.org -->

    <!-- An XSLT 3.0 stylesheet example, to find Euler number's
         numeric value. This stylesheet uses, xsl:iterate instruction,
		 to perform this activity. 
		 
		 Ref, https://en.wikipedia.org/wiki/E_(mathematical_constant)
    -->  	
				
    <!-- Number of iterations to carry -->				
    <xsl:variable name="limit" select="50" as="xs:integer"/>				
 
    <xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/">
	   <result>
	      <xsl:value-of select="round(fn0:eulerNumber(),5)"/>
	   </result>
	</xsl:template>
	
	<!-- An XSL stylesheet function, to find Euler number's 
	     numeric value. -->
	<xsl:function name="fn0:eulerNumber" as="xs:double">
	   <xsl:iterate select="1 to $limit">
	      <xsl:param name="result" select="1" as="xs:double"/>
		  <xsl:on-completion>
		     <xsl:sequence select="$result"/>
		  </xsl:on-completion>
		  <xsl:next-iteration>
		     <xsl:with-param name="result" select="$result + (1 div fn0:factorial(.))" as="xs:double"/>
		  </xsl:next-iteration>
	   </xsl:iterate>
	</xsl:function>
	
	<!-- An XSL stylesheet function, to find factorial of
	     a positive integer value. -->
	<xsl:function name="fn0:factorial" as="xs:integer">
	   <xsl:param name="n" as="xs:integer"/>
	   <xsl:sequence select="if ($n eq 1) then 1 else ($n * fn0:factorial($n - 1))"/>
	</xsl:function>
	
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
