<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT test case, to test XPath 3.1 "castable as" 
        expression.
   -->                 				

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">	  
      <result>
         <xsl:variable name="srcVal" select="xs:integer(2)"/>
		 <xsl:variable name="resultVal" select="$srcVal castable as xs:double"/>
		 <castableAsResult inpValue="{$srcVal}" srcType="xs:integer" targetType="xs:double">
		    <xsl:value-of select="$resultVal"/>
		 </castableAsResult>
		 
		 <xsl:variable name="srcVal" select="xs:normalizedString('hello')"/>
		 <xsl:variable name="resultVal" select="$srcVal castable as xs:string"/>
		 <castableAsResult inpValue="{$srcVal}" srcType="xs:normalizedString" targetType="xs:string">
		    <xsl:value-of select="$resultVal"/>
		 </castableAsResult>

		 <xsl:variable name="srcVal" select="xs:string('hello')"/>
		 <xsl:variable name="resultVal" select="$srcVal castable as xs:normalizedString"/>
		 <castableAsResult inpValue="{$srcVal}" srcType="xs:string" targetType="xs:normalizedString">
		    <xsl:value-of select="$resultVal"/>
		 </castableAsResult>
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
