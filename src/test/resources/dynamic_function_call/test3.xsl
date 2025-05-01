<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- An XSLT stylesheet, to test the XPath 3.1 dynamic function 
        call. -->                 

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/list">
      <result>
        <xsl:for-each select="idx">
           <xsl:call-template name="Template1">                            
              <xsl:with-param name="funcA" select="function($x) { $x + 3 }"/>
              <xsl:with-param name="num" select="."/>
           </xsl:call-template>
        </xsl:for-each>
      </result>
   </xsl:template>
   
   <!-- This template expects following two arguments,
        1) function item "inline function" definition.
        2) a number.
        
        This template makes, a function call having a 
        numeric argument. The function definition used,
        is the function item passed as argument. -->
   <xsl:template name="Template1">            
      <xsl:param name="funcA"/>
      <xsl:param name="num"/>
      
      <val><xsl:value-of select="$funcA($num)"/></val>
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