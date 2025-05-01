<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with https://xalan.apache.org/xalan-j/xsl3/tests/data/test1_a.xml -->
   
    <!-- An XSLT stylesheet test case, for the XPath 3.1 fn:base-uri 
         function.
    -->                             

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/info">
       <result>
          <xsl:apply-templates select="a1"/>
       </result>
    </xsl:template>
    
    <xsl:template match="a1">
       <xsl:variable name="baseUri1" select="base-uri(.)"/>
       <xsl:variable name="baseUri2" select="base-uri()"/>
       <xsl:variable name="emptySeq1" select="()"/>
       <baseUri1 isOfTypeXsAnyURI="{$baseUri1 instance of xs:anyURI}">
          <xsl:value-of select="$baseUri1"/>
       </baseUri1>
       <baseUri2 isOfTypeXsAnyURI="{$baseUri2 instance of xs:anyURI}">
          <xsl:value-of select="$baseUri2"/>
       </baseUri2>
       <baseUriOfEmptySequenceIsEmptySequence>
          <xsl:value-of select="base-uri($emptySeq1) instance of empty-sequence()"/>
       </baseUriOfEmptySequenceIsEmptySequence>
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
