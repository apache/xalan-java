<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->
   
   <!-- Test for the XPath 3.1 fn:index-of() function,
        when using an optional collation argument -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/info">
       <xsl:variable name="indexOfResult1" select="index-of(a, 'Strasse')" as="xs:integer*"/>
       <xsl:variable name="indexOfResult2" select="index-of(a, 'Strasse', 
                                                                'http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')" as="xs:integer*"/>                                          
       <info>
          <result1 num="{count($indexOfResult1)}">
             <xsl:for-each select="$indexOfResult1">
               <val>
                 <xsl:value-of select="."/>
               </val>
             </xsl:for-each>
          </result1>
          <result2 num="{count($indexOfResult2)}">
             <xsl:for-each select="$indexOfResult2">
               <val>
                 <xsl:value-of select="."/>
               </val>
             </xsl:for-each>
          </result2>
       </info>
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
