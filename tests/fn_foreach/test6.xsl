<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- Test for the XPath 3.1 fn:for-each() function -->
   
   <!-- This XSLT stylesheet tests, passing an XPath function
        item as a template parameter argument. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
        <xsl:call-template name="Template1">
           <xsl:with-param name="funcDefn" select="function($x) { $x * $x }"/>
           <xsl:with-param name="idx1" select="1"/>
           <xsl:with-param name="idx2" select="5"/>
        </xsl:call-template>
      </result>
   </xsl:template>
   
   <xsl:template name="Template1">
      <xsl:param name="funcDefn"/>
      <xsl:param name="idx1"/>
      <xsl:param name="idx2"/>
   
      <xsl:for-each select="for-each($idx1 to $idx2, $funcDefn)">
         <num><xsl:value-of select="."/></num>
      </xsl:for-each>
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