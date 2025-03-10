<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL 3 stylesheet test case, to test year and month numeric 
        component extractions from xs:gYearMonth values. -->                 
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
         <xsl:variable name="val1" select="xs:gYearMonth('2005-10')" as="xs:gYearMonth"/>
         <one><xsl:value-of select="string($val1)"/></one>
         <two><xsl:value-of select="xs:string($val1)"/></two>
         <three><xsl:value-of select="substring(string($val1), 1, 4)"/></three>
         <four><xsl:value-of select="substring(string($val1), 6)"/></four>
         <five inpGYearMonthVal="{$val1}"><xsl:value-of select="fn0:getYearFromGYearMonth($val1)"/></five>
         <six inpGYearMonthVal="{$val1}"><xsl:value-of select="fn0:getMonthFromGYearMonth($val1)"/></six>
      </result>
   </xsl:template>

   <!-- An XSL stylesheet function definition, to extract an year numeric 
        component from xs:gYearMonth value. --> 
   <xsl:function name="fn0:getYearFromGYearMonth" as="xs:integer">
      <xsl:param name="gYearMonthVal" as="xs:gYearMonth"/>
      <xsl:variable name="strVal" select="string($gYearMonthVal)" as="xs:string"/>
      <xsl:sequence select="xs:integer(substring($strVal, 1, 4))"/>
   </xsl:function>

   <!-- An XSL stylesheet function definition, to extract an month numeric 
        component from xs:gYearMonth value. -->
   <xsl:function name="fn0:getMonthFromGYearMonth" as="xs:integer">
      <xsl:param name="gYearMonthVal" as="xs:gYearMonth"/>
      <xsl:variable name="strVal" select="string($gYearMonthVal)" as="xs:string"/>
      <xsl:sequence select="xs:integer(substring($strVal, 6))"/>
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