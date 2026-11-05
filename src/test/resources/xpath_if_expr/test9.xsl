<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test, for the XPath 3.1 "if" expression. -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="degToRadian" select="function($deg) { $deg * (math:pi() div 180) }"/>
   
   <xsl:variable name="inpSeq" select="($degToRadian(20), $degToRadian(30), $degToRadian(45))"/>
      
   <xsl:variable name="inpUnit" select="'radian'"/>
   
   <xsl:template match="/">      
      <result xmlns:math="http://www.w3.org/2005/xpath-functions/math" inpUnit="{$inpUnit}">
        <xsl:for-each select="$inpSeq">
           <xsl:variable name="inpVal" select="."/>
           <xsl:variable name="inpValRadian" select="if ($inpUnit = 'degree') then 
                                                              ($inpVal * (math:pi() div 180)) 
                                                                              else $inpVal"/>
           <math:sin inp="{$inpVal}">
             <xsl:value-of select="let $a := xs:double($inpValRadian) return math:sin($a)"/>
           </math:sin>
        </xsl:for-each>
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