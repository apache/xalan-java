<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- An XSLT stylesheet, to test the XPath 3.1 dynamic function 
        call. We illustrate within this stylesheet example, that
        the argument information passed to dynamic function call  
        are significantly complex XPath expressions. -->                  

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/temp">      
      <result>
        <xsl:variable name="x1" select="val1 + 2"/>
        <xsl:variable name="x2" select="val1 + 3"/>
        
        <xsl:variable name="func1" select="function($a) { ($a * 2) + $x1 }"/>
        <xsl:variable name="func2" select="function($b) { $b + $x2 }"/>
        
        <val><xsl:value-of select="$func1(($func2(val1) + val2) + math:pi())"/></val>
      </result>
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