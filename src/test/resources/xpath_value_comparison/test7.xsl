<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:java="http://xml.apache.org/xalan/java"
                exclude-result-prefixes="xs java"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet to test, XPath 3.1 value comparison 
        operators 'eq' and 'lt'.
        
        To be able to test XPath 'eq' operator involving expression
        like xs:date($currentDateStr) eq current-date(), where the
        variable $currentDateStr has the current date's string value
        (with format yyyy-mm-dd) when this test is been run, we use
        XalanJ's java extension mechanism to get the current date's
        string value.  
   -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="currentDateStr" select="java:format(java:java.text.SimpleDateFormat.new('yyyy-MM-dd'), 
                                                        java:org.apache.xalan.tests.util.XslTransformTestsUtil.getCurrentDate())"/>
                                                        
   <xsl:variable name="timeZoneOffsetStr" select="java:org.apache.xalan.tests.util.XslTransformTestsUtil.getDefaultTimezoneOffsetStr()"/>                                                                    

   <xsl:template match="/">            
      <elem>                       
        <result1><xsl:value-of select="xs:date('2023-06-19') lt current-date()"/></result1>
        <result2><xsl:value-of select="xs:date('2023-06-20') lt current-date()"/></result2>
        <result3><xsl:value-of select="xs:date('2023-06-21') lt current-date()"/></result3>
        <result4><xsl:value-of select="xs:date($currentDateStr) eq current-date()"/></result4>
        <result5><xsl:value-of select="xs:date('2023-06-21Z') eq current-date()"/></result5>
        <result6><xsl:value-of select="xs:date('2023-06-15Z') eq xs:date('2023-06-15+05:30')"/></result6>        
        <result7><xsl:value-of select="xs:date(concat($currentDateStr,$timeZoneOffsetStr)) eq current-date()"/></result7>
        <result8><xsl:value-of select="xs:date('2023-06-21+10:00') eq xs:date('2023-06-21')"/></result8>
        <result9><xsl:value-of select="current-date() eq current-date()"/></result9>
        <result10><xsl:value-of select="xs:date('2023-06-21') eq xs:date('2023-06-21')"/></result10>
        <result11><xsl:value-of select="xs:date('2023-06-19Z') lt xs:date('2023-06-19+05:30')"/></result11>
        <result12><xsl:value-of select="xs:date('2023-06-19-06:00') lt xs:date('2023-06-19+05:30')"/></result12>
      </elem>
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