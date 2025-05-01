<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL 3 stylesheet test case, to test whether one xs:gYearMonth 
        value precedes another xs:gYearMonth value. -->                 
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
         <xsl:variable name="val1" select="xs:gYearMonth('2005-10')" as="xs:gYearMonth"/>
         <xsl:variable name="val2" select="xs:gYearMonth('2010-10')" as="xs:gYearMonth"/>
         <one><xsl:value-of select="fn0:isAPrecedesB($val1, $val2)"/></one>
         <two><xsl:value-of select="fn0:isAPrecedesB($val2, $val1)"/></two>
         <three><xsl:value-of select="fn0:isAPrecedesB($val1, $val1)"/></three>         
      </result>
   </xsl:template>

   <!-- An XSL stylesheet function definition, to check whether one xs:gYearMonth 
        value precedes another xs:gYearMonth value. --> 
   <xsl:function name="fn0:isAPrecedesB" as="xs:boolean">
      <xsl:param name="gYearMonthVal1" as="xs:gYearMonth"/>
      <xsl:param name="gYearMonthVal2" as="xs:gYearMonth"/>
      <xsl:variable name="strVal1" select="string($gYearMonthVal1)" as="xs:string"/>
      <xsl:variable name="strVal2" select="string($gYearMonthVal2)" as="xs:string"/>
      <xsl:variable name="year1" select="xs:integer(substring($strVal1, 1, 4))" as="xs:integer"/>
      <xsl:variable name="year2" select="xs:integer(substring($strVal2, 1, 4))" as="xs:integer"/>
      <xsl:variable name="month1" select="xs:integer(substring($strVal1, 6))" as="xs:integer"/>
      <xsl:variable name="month2" select="xs:integer(substring($strVal2, 6))" as="xs:integer"/>
      <xsl:choose>        
         <xsl:when test="($year1 lt $year2)">
            <xsl:sequence select="xs:boolean('true')"/>
         </xsl:when>
         <xsl:when test="($year1 gt $year2)">
            <xsl:sequence select="xs:boolean('false')"/>
         </xsl:when>
         <xsl:when test="($year1 eq $year2) and ($month1 eq $month2)">
            <xsl:sequence select="xs:boolean('false')"/>
         </xsl:when>
         <xsl:when test="($year1 eq $year2) and ($month1 lt $month2)">
            <xsl:sequence select="xs:boolean('true')"/>
         </xsl:when>
         <xsl:when test="($year1 eq $year2) and ($month1 gt $month2)">
            <xsl:sequence select="xs:boolean('false')"/>
         </xsl:when>         
      </xsl:choose>
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