<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"			    
				xmlns:err="http://www.w3.org/2005/xqt-errors"
				xmlns:fn0="http://fn0"
			    exclude-result-prefixes="xs err fn0"
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test xsl:try and xsl:catch 
       instructions. -->				

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
     <result>
         <xsl:variable name="x" select="5"/>
         <xsl:variable name="y" select="0"/>		 
		 <xsl:try select="$x div $y">
		   <xsl:catch errors="*" select="fn0:xslCatchProcessing(string($err:code), $err:description, $err:line-number, $err:column-number)"/>
		 </xsl:try>
	 </result>
  </xsl:template>
  
  <!-- An XSL stylesheet function definition, to do processing for xsl:catch 
       element. -->
  <xsl:function name="fn0:xslCatchProcessing" as="element(error)">    
	<xsl:param name="errCode" as="xs:string"/>
	<xsl:param name="errDesc" as="xs:string"/>
	<xsl:param name="lineNum" as="xs:integer"/>
	<xsl:param name="columnNum" as="xs:integer"/>
	<error location="line : {$lineNum}, column : {$columnNum}">
	  <errCode><xsl:value-of select="$errCode"/></errCode>
      <reason><xsl:value-of select="$errDesc"/></reason>
    </error>
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
