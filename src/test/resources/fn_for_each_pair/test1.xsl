<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- Test for the XPath 3.1 fn:for-each-pair() function.
   
        The XPath fn:for-each-pair() function usage examples, as
        illustrated within this stylesheet are borrowed from
        XPath 3.1 spec, and https://www.altova.com/. -->                 

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="fnMult" select="function($arg1, $arg2) { $arg1 * $arg2 }"/>
      
   <xsl:template match="/">      
      <result>
         <xsl:variable name="seq1" select="('a', 'b', 'c')"/>
         <xsl:variable name="seq2" select="('x', 'y', 'z')"/>
         
         <one><xsl:value-of select="for-each-pair($seq1, $seq2, function($a, $b) { $a || $b })"/></one>
         
         <two><xsl:value-of select="for-each-pair(1 to 5, 1 to 5, function($a, $b){ 10*$a + $b })"/></two>
         
         <xsl:variable name="seq3" select="(1, 10, 100)"/>
         <xsl:variable name="seq4" select="(2, 5, 10)"/>         
         
         <three><xsl:value-of select="for-each-pair($seq3, $seq4, function($arg1, $arg2) { $arg1 * $arg2 })"/></three>
         
         <xsl:variable name="seq5" select="(2, 5)"/>
         <four><xsl:value-of select="for-each-pair($seq3, $seq5, function($arg1, $arg2) { $arg1 * $arg2 })"/></four>
         
         <!-- the following fn:for-each-pair function call example, refers an inline function, 
              via a variable reference. -->
         <five><xsl:value-of select="for-each-pair($seq3, $seq5, $fnMult)"/></five>
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