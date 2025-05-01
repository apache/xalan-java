<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- Test for the XPath 3.1 fn:distinct-values() function,
        when providing collation argument.
   -->                               

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/info">
       <xsl:variable name="distValues1" select="distinct-values(a)"/>
       <xsl:variable name="distValues2" select="distinct-values(a, 'http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/>
       <info>
          <result1 num="{count($distValues1)}">
             <xsl:for-each select="$distValues1">
               <val>
                 <xsl:value-of select="."/>
               </val>
             </xsl:for-each>
          </result1>
          <result2 num="{count($distValues2)}">
             <xsl:for-each select="$distValues2">
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
