<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test XPath 3.1 
       function fn:dateTime. 
  -->                  
                
  <xsl:output method="xml" indent="yes"/>    

  <xsl:variable name="dateTime1" select="dateTime(xs:date('1999-12-31'), xs:time('12:00:00'))"/>  
  <xsl:variable name="dateTime2" select="dateTime(xs:date('1999-12-31'), xs:time('24:00:00'))"/>
  <xsl:variable name="dateTime3" select="dateTime(xs:date('2012-10-05+05:30'), xs:time('24:00:00'))"/>
  
  <xsl:template match="/">
    <result>      
      <one isXsDateTimeValue="{$dateTime1 instance of xs:dateTime}">
        <xsl:value-of select="$dateTime1"/>
      </one>
      <two isXsDateTimeValue="{$dateTime2 instance of xs:dateTime}">
        <xsl:value-of select="$dateTime2"/>
      </two>
      <three isXsDateTimeValue="{$dateTime3 instance of xs:dateTime}">
        <xsl:value-of select="$dateTime3"/>
      </three>
    </result> 
  </xsl:template>
  
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