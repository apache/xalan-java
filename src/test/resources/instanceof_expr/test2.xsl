<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- use with test1_b.xml -->
   
    <!-- An XSLT stylesheet test case, to test XPath 3.1 "instance of" 
         expression. -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="var1" select="/info//* | /info//@* | /info//text()"/>
    
    <xsl:template match="/">       
       <result nodeCount="{count($var1)}">
          <xsl:for-each select="$var1">
             <node position="{position()}">
                <xsl:choose>
                    <xsl:when test=". instance of element()">
                       <xsl:attribute name="type">element</xsl:attribute>
                       <xsl:attribute name="name"><xsl:value-of select="name()"/></xsl:attribute>
                       <val><xsl:value-of select="normalize-space(.)"/></val>
                    </xsl:when>
                    <xsl:when test=". instance of attribute()">
                       <xsl:attribute name="type">attribute</xsl:attribute>
                       <xsl:attribute name="name"><xsl:value-of select="name()"/></xsl:attribute>
                       <val><xsl:value-of select="normalize-space(.)"/></val>
                    </xsl:when>
                    <xsl:when test=". instance of text()">
                       <xsl:attribute name="type">text</xsl:attribute>
                       <xsl:attribute name="name">NA</xsl:attribute>
                       <val><xsl:value-of select="normalize-space(.)"/></val>
                    </xsl:when>
                </xsl:choose>
             </node>
          </xsl:for-each>
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