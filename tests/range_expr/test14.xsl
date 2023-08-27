<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
                
    <!-- use with test1_d.xml -->
    
    <!-- An XSLT stylesheet test case, to test XPath 3.1
         range "to" expression.
     -->
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="repetitionCount" select="3"/>
        
    <xsl:template match="/">
       <alpha>
         <xsl:apply-templates select="alpha/*"/>
       </alpha>
    </xsl:template>
    
    <xsl:template match="*">
      <xsl:variable name="elem" select="."/>
      <xsl:for-each select="1 to $repetitionCount">
         <xsl:element name="{name($elem)}">
            <xsl:if test="position() = 1">
               <xsl:attribute name="strLen" select="string-length(xs:string($elem))"/>
            </xsl:if>
            <xsl:copy-of select="$elem/node()"/>
         </xsl:element>
      </xsl:for-each>
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