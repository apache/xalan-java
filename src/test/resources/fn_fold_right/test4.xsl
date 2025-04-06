<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- An XSL stylesheet test case, for the XPath function fn:fold-right. -->                   

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/temp">      
      <result>
        <xsl:variable name="valList1" select="val"/>
        <val1><xsl:value-of select="fold-right($valList1, 1, fn0:mult#2)"/></val1>
        
        <xsl:variable name="valList2" select="data/@*"/>
        <val2><xsl:value-of select="fold-right($valList2, 1, fn0:mult#2)"/></val2>
      </result>
   </xsl:template>
   
   <!-- An XSL stylesheet function, to multiply two numeric integer values. -->
   <xsl:function name="fn0:mult" as="xs:integer">
      <xsl:param name="a" as="xs:integer"/>
	  <xsl:param name="b" as="xs:integer"/>
	  <xsl:sequence select="$a * $b"/>
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