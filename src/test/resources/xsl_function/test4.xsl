<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn1="http://fn1"                
                exclude-result-prefixes="xs fn1"
                version="3.0">
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT stylesheet test case, to test a stylesheet
         function defined with an XSL element xsl:function.
    -->
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
       <factorial>
          <xsl:for-each select="1 to 10">
             <xsl:variable name="num" select="."/>
             <number inp="{$num}">
                <xsl:value-of select="fn1:factorial($num)"/>
             </number>
          </xsl:for-each>
       </factorial>
    </xsl:template>
    
    <!-- A function that uses recursion, to calculate factorial of 
         a numeric argument value. -->
    <xsl:function name="fn1:factorial" as="xs:integer">
       <xsl:param name="num" as="xs:integer"/>
       <xsl:value-of select="if ($num = 0) then 1 else $num * fn1:factorial($num - 1)"/>
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