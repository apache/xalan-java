<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet, to test xsl:iterate instruction, when xsl:iterate's 
        select attribute evaluates to a sequence of atomic values. -->                

   <xsl:output method="xml" indent="yes"/>

   <!-- A variable, referring to an XPath sequence constructor having various 
        function item expressions and non-function item expression as well. --> 
   <xsl:variable name="sequenceVar" select="(function($a, $b) { $a + $b }, math:pi(), function($a, $b) { $a - $b }, 
                                               function($a, $b) { $a * $b }, function($a, $b) { $a div $b })"/>                                             
                                               
   <xsl:variable name="num1" select="7"/> 
   
   <xsl:variable name="num2" select="3"/>
      
   <xsl:template match="/">
      <result>
        <xsl:iterate select="$sequenceVar">        
          <xsl:choose>
             <xsl:when test="position() = 2">
                <valPi><xsl:value-of select="."/></valPi>
             </xsl:when>
             <xsl:otherwise>
                <xsl:variable name="fnItem" select="."/>
                <val><xsl:value-of select="$fnItem($num1, $num2)"/></val>
             </xsl:otherwise>
          </xsl:choose>
        </xsl:iterate>
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