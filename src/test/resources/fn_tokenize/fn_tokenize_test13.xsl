<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with fn_tokenize_sample_strings.xml -->
   
  <!-- An XSL 3 fn:tokenize function test case with a successful transformation. 
       Using an XSL xsl:iterate element within this stylesheet. -->                            
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/data">     
     <result attr="{@attr}">
		 <xsl:iterate select="str">
		    <str value="{.}">
		       <xsl:variable name="tokens" select="tokenize(., '\s+')" as="xs:string*"/>
			   <xsl:call-template name="emitStrTokensFromTokenSequence">
			      <xsl:with-param name="tokens" select="$tokens" as="xs:string*"/>
			   </xsl:call-template>
			</str>
		 </xsl:iterate>
	 </result>
  </xsl:template>
  
  <!-- An XSL named template that emits a sequence of XML elements 
       with name 'token' -->
  <xsl:template name="emitStrTokensFromTokenSequence" as="element(token)*">
     <xsl:param name="tokens" as="xs:string*"/>
	 <xsl:iterate select="$tokens">
	    <token><xsl:value-of select="."/></token>
	 </xsl:iterate>
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
