<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case, for the XPath function fn:fold-right.
   
        The XPath function fn:fold-right's usage examples, as
        illustrated within this stylesheet are borrowed from
        XPath 3.1 spec, and https://www.altova.com/. -->                 

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="fnStrConcat" select="function($arg1, $arg2) { concat($arg1, $arg2) }"/>
      
   <xsl:template match="/">      
      <result>
        <val1><xsl:value-of select="fold-right(1 to 5, 0, function($a, $b) { $a + $b })"/></val1>
        
        <val2><xsl:value-of select="fold-right(1 to 5, 0, function($arg1, $arg2) { $arg1 - $arg2 })"/></val2>
        
        <xsl:variable name="charListSeq1" select="('a', 'b', 'c')"/>
        <val3><xsl:value-of select="fold-right($charListSeq1, 'z' , function($arg1, $arg2) { concat($arg1, $arg2) })"/></val3>
        
        <!-- The following fn:fold-right function call example, refers an inline function, 
             via a variable reference. -->
        <xsl:variable name="charListSeq2" select="('a', 'b', 'c', 'd', 'e')"/>
        <val4><xsl:value-of select="fold-right($charListSeq2, 'z' , $fnStrConcat)"/></val4>
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