<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:num="http://example.com/namespace"               
               exclude-result-prefixes="xs num"
			   version="3.0">
			   
   <!-- Author: mukulg@apache.org -->			   
			   
   <!-- An XSL 3 stylesheet example, to test stylesheet function. 
        Ref : XSLT 3.0 specification, section 10.3.9 Examples of 
        Stylesheet Functions. -->		   
			   
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
        <xsl:iterate select="1 to 10">
		   <value num="{.}"><xsl:value-of select="num:roman(.)"/></value>
		</xsl:iterate>
      </result>
   </xsl:template>			   

   <!-- An XSL stylesheet function, that transforms a supplied 
        decimal integer value to a numeric value with roman 
		numerals. -->
   <xsl:function name="num:roman" as="xs:string">
      <xsl:param name="value" as="xs:integer"/>
      <xsl:number value="$value" format="i"/>
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

</xsl:transform>
