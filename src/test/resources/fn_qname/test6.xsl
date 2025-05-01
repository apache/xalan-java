<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test5.xml -->
    
    <!-- An XSLT test case to test, XPath 3.1 function fn:namespace-uri-for-prefix.
    -->              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/root">
       <result>
         <xsl:variable name="inScopePrefixes" select="in-scope-prefixes(x)"/>         
         <prefixes count="{count($inScopePrefixes)}">
            <xsl:for-each select="$inScopePrefixes">
               <prefix><xsl:value-of select="."/></prefix>
            </xsl:for-each>
         </prefixes>
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
