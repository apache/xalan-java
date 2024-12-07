<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ns0="http://ns0"
                exclude-result-prefixes="xs ns0"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1_a.xml -->
    
    <!-- An XSLT test case to test, XPath 3.1 function fn:resolve-QName. -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/info">
       <result>
         <xsl:variable name="qNameVal1" select="resolve-QName('ns0:a1', *[1])"/>
         <xsl:variable name="qNameVal2" select="resolve-QName('ns0:a1', ns0:a1)"/>
         
         <one isXsQname="{$qNameVal1 instance of xs:QName}"/>
         <two isXsQname="{$qNameVal2 instance of xs:QName}"/>
         
         <three isXsQname="{resolve-QName('ns0:a1', *[1]) instance of xs:QName}"/>
         <four isXsQname="{resolve-QName('ns0:a1', ns0:a1) instance of xs:QName}"/>
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
