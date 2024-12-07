<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT stylesheet test case, to test XPath 3.1 sequence type 
         expressions,
         1) Where a value of schema type xs:duration is tried to be 
            ill-legitemately assigned to the type xs:dayTimeDuration.
         2) Where a value of schema type xs:duration is tried to be 
            ill-legitemately assigned to the type xs:yearMonthDuration.
    -->                

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/"> 
      <result>
         <xsl:call-template name="process1">
            <xsl:with-param name="dtdVal1" select="xs:duration('PT40H')" as="xs:dayTimeDuration"/>
            <xsl:with-param name="ymdVal1" select="xs:duration('P1Y3M')" as="xs:yearMonthDuration"/>
         </xsl:call-template>
      </result>
    </xsl:template>
    
    <xsl:template name="process1">
       <xsl:param name="dtdVal1" as="xs:dayTimeDuration"/>
       <xsl:param name="ymdVal1" as="xs:yearMonthDuration"/>
       <one>
          <xsl:copy-of select="$dtdVal1"/>
       </one>
       <two>
          <xsl:copy-of select="$ymdVal1"/>
       </two>
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