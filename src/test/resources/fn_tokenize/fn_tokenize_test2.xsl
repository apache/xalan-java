<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSL 3 fn:tokenize function test case. -->
   
  <!-- XPath 3.1 spec ref, https://www.w3.org/TR/xpath-functions-31/#func-tokenize -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
	     <xsl:variable name="str1" select="'hello world, this string needs to be tokenized'" as="xs:string"/>
         <xsl:variable name="tokens" select="tokenize($str1, '\s+')"/>
		 <xsl:call-template name="emitTokens">
		    <xsl:with-param name="tokens" select="$tokens"/>
		 </xsl:call-template>
	 </result>
  </xsl:template>
  
  <!-- An XSL named template that accepts a sequence of token strings, and 
       emits them to XSL transform's output. -->
  <xsl:template name="emitTokens">
     <xsl:param name="tokens" as="xs:string*"/>
	 <xsl:attribute name="noOfTokens">
		<xsl:value-of select="count($tokens)"/>
	 </xsl:attribute>
	 <xsl:for-each select="$tokens">
	    <token><xsl:value-of select="."/></token>
	 </xsl:for-each>
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
