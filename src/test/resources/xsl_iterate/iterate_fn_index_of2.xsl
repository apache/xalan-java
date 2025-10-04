<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test implementation similar 
         to that of XPath 3.1 function fn:index-of using XSLT 3.0's 
         xsl:iterate instruction. -->                 			
				
    <xsl:output method="xml" indent="yes"/>

	<xsl:variable name="strList1" select="('abc1', 'abc2', 'abc1', 'abc5', 'abc4')" as="xs:string*"/>
				
	<xsl:template match="/">
		<result>
		   <xsl:for-each select="('abc1', 'abc4')">
		     <index srch="{.}"><xsl:value-of select="fn0:index-of($strList1, .)"/></index>
		   </xsl:for-each>
		</result>
	</xsl:template>
	
	<xsl:function name="fn0:index-of" as="xs:integer*">
	   <xsl:param name="strList" as="xs:string*"/>
	   <xsl:param name="srch1" as="xs:string"/>
	   <xsl:iterate select="$strList">
	      <xsl:param name="result" select="()" as="xs:integer*"/>
		  <xsl:on-completion>
		     <xsl:sequence select="$result"/>
		  </xsl:on-completion>
		  <xsl:if test=". eq $srch1">
		     <xsl:next-iteration>
			   <xsl:with-param name="result" select="($result, position())"/>
			 </xsl:next-iteration>
		  </xsl:if>
	   </xsl:iterate>
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
