<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ns0="http://ns0"
                exclude-result-prefixes="xs ns0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- An XSLT stylesheet test case, to test few XPath 3.1 
       functions related to date and time values. 
  -->                 
                
  <xsl:output method="xml" indent="yes"/>    
  
  <!-- An initial template of this stylesheet, that invokes
       a named template multiple times via xsl:call-template 
       instruction, and passing in a xs:dateTime string argument
       (whose value is derived from an XML input document) to 
       the invoked template.
  -->
  <xsl:template match="/info">
    <result>
      <one startDtTime="{xs:string(dateTimeVal[1])}">
         <xsl:call-template name="trfDateInp">
           <xsl:with-param name="dtTimeStr" select="dateTimeVal[1]" as="xs:string"/>
         </xsl:call-template>
      </one>
      <two startDtTime="{xs:string(dateTimeVal[2])}">
         <xsl:call-template name="trfDateInp">
           <xsl:with-param name="dtTimeStr" select="dateTimeVal[2]" as="xs:string"/>
         </xsl:call-template>
      </two>
    </result> 
  </xsl:template>
  
  <!-- A stylesheet named template, that accepts a xs:dateTime string
       as an argument, and produces a progressively increasing (via 
       xsl:iterate instruction) sequence of year values from the year 
       component of an xs:dateTime argument.  
  -->
  <xsl:template name="trfDateInp" as="element(newDt)*">
     <xsl:param name="dtTimeStr" as="xs:string"/>
     
     <xsl:variable name="yrVal" select="year-from-dateTime(xs:dateTime($dtTimeStr))"/>
     <xsl:variable name="mnthVal" select="month-from-dateTime(xs:dateTime($dtTimeStr))"/>
     <xsl:variable name="dtVal" select="day-from-dateTime(xs:dateTime($dtTimeStr))"/>     
     <xsl:iterate select="1 to 5">
        <xsl:variable name="incr" select="."/>
        <newDt>
          <xsl:value-of select="($yrVal + $incr) || '-' || ns0:padNum($mnthVal) || '-' || ns0:padNum($dtVal)"/>
        </newDt>
     </xsl:iterate>
  </xsl:template>
  
  <!-- A stylesheet function, that accepts an xs:integer value 
       as an argument, and prefixes it with a character '0' if
       an xs:integer argument has value less than 10. This
       function returns the resulting string after the prefix 
       operation.
       
       This function intends to produce a two character month
       value, suitable for constructing xs:date strings. 
  -->
  <xsl:function name="ns0:padNum" as="xs:string">
    <xsl:param name="num1" as="xs:integer"/>
    
    <xsl:sequence select="if ($num1 lt 10) then ('0' || $num1) else $num1"/>
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