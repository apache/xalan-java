<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet, to test the XPath 3.1 dynamic function 
        call. We illustrate within this stylesheet example, that
        the argument information passed to dynamic function call  
        are significantly complex XPath expressions. -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="func1" select="function($a) { $a * 2 }"/>
   
   <xsl:variable name="func2" select="function($a) { $a + 5 }"/>
   
   <xsl:variable name="x1" select="5"/>
      
   <xsl:template match="/">
      <xsl:variable name="x2" select="7"/>           
      <result>
        <val1><xsl:value-of select="$func1($func2(10) + 5)"/></val1>
        <val2><xsl:value-of select="$func1(abs(-10) + 20)"/></val2>
        <val3><xsl:value-of select="$func1($func2(10) + abs(-7))"/></val3>
        <val4><xsl:value-of select="$func1($func2(10) + $x1 + math:pi())"/></val4>
        <val5><xsl:value-of select="$func1($func2(10) + abs(-7)) + ($x1 * $x2)"/></val5>
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