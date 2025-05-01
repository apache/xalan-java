<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet, demonstrating producing an integer 
        number range, using a recursive named template. An XSLT 
        algorithm described within this stylesheet, shall work 
        with an XSLT 1.0 processor as well. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
         <xsl:call-template name="produceNumberRange">
            <xsl:with-param name="from" select="1"/>
            <xsl:with-param name="to" select="10"/>
         </xsl:call-template>
      </result>
   </xsl:template>
   
   <xsl:template name="produceNumberRange">
      <xsl:param name="from"/>
      <xsl:param name="to"/>

      <xsl:choose>
         <xsl:when test="$from &lt; $to">
            <val><xsl:value-of select="$from"/></val>
            <xsl:call-template name="produceNumberRange">
	           <xsl:with-param name="from" select="$from + 1"/>
	           <xsl:with-param name="to" select="$to"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise>
            <val><xsl:value-of select="$from"/></val>
         </xsl:otherwise>
      </xsl:choose>
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