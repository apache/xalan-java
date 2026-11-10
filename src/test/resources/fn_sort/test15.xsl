<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">             

   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet to test, XPath 3.1 fn:sort function to sort sequence
        of text strings, both in ascending and descending orders. -->
   
   <xsl:output method="xml" indent="yes"/>
   
   <!-- An XSLT variable, referring to a sequence of string values, 
        that needs to be sort. -->
   <xsl:variable name="seq1" select="('Noah', 'Michael', 'Gary', 'Mukul', 'Dave')"/>      
   
   <xsl:template match="/">
      <result>
         <sorted1 sortOrder="ascending"><xsl:copy-of select="sort($seq1, (), function($str) { string($str) })"/></sorted1>
         
         <sorted2 sortOrder="descending"><xsl:copy-of select="reverse(sort($seq1, (), function($str) { string($str) }))"/></sorted2>
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