<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSLT stylesheet, tests XPath 3.1 arithmetic on
        XML Schema duration typed values. The XPath expression
        examples, as mentioned within this test case are borrowed
        from XPath 3.1 spec. -->                

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/">
      <result>
         <one><xsl:value-of select="xs:yearMonthDuration('P2Y11M') + xs:yearMonthDuration('P3Y3M')"/></one>
         <two><xsl:value-of select="xs:yearMonthDuration('P2Y11M') - xs:yearMonthDuration('P3Y3M')"/></two>
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