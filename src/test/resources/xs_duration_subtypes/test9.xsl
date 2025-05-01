<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
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
         <xsl:copy-of select="fn0:process1(xs:duration('PT40H'), xs:duration('P1Y3M'))"/>
       </result>
    </xsl:template>
    
    <xsl:function name="fn0:process1" as="element()*">
       <xsl:param name="dtdVal1" as="xs:dayTimeDuration"/>
       <xsl:param name="ymdVal1" as="xs:yearMonthDuration"/>
       <one>
          <xsl:sequence select="$dtdVal1"/>
       </one>
       <two>
          <xsl:sequence select="$ymdVal1"/>
       </two>
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