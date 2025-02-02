<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
				exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSL 3 fn:tokenize function test case. -->
   
  <!-- XPath 3.1 spec ref, https://www.w3.org/TR/xpath-functions-31/#func-tokenize -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
	     <xsl:variable name="str1" select="'hello world, this string needs to be tokenized'" as="xs:string"/>
		 <xsl:variable name="tokens" select="tokenize($str1, '\s+')"/>
		 <xsl:copy-of select="fn0:emitTokens($tokens)"/>
	 </result>
  </xsl:template>
  
  <!-- An XSL stylesheet function, that emits token strings to XSL 
       transform's output via xsl:iterate instruction. A sequence of 
       token strings are passed as arguments to this functions. -->
  <xsl:function name="fn0:emitTokens">
     <xsl:param name="tokens" as="xs:string*"/>	 
	 <xsl:iterate select="1 to count($tokens)">
	    <xsl:variable name="currItem" select="subsequence($tokens, xs:double(.), 1)"/>
	    <token><xsl:value-of select="$currItem"/></token>
	 </xsl:iterate>
  </xsl:function>
  
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
