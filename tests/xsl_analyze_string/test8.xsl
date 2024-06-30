<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_e.xml -->
   
   <!-- This example demonstrates, using xsl:analyze-string instruction
        as a string tokenizer. -->

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <elem>
         <xsl:variable name="strTokens">
            <xsl:call-template name="strTokenizer">
               <xsl:with-param name="inpStr" select="elem"/>
	       <xsl:with-param name="regex" select="'(\s)+'"/>
            </xsl:call-template>
         </xsl:variable>
         <!-- post process the result of previous 'strTokenizer' template call,
              to produce a csv formatted token list. --> 
         <xsl:for-each select="$strTokens/token">
            <xsl:choose>
               <xsl:when test="position() &lt; last()">
                  <xsl:value-of select="concat(.,',')"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="."/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
      </elem>
   </xsl:template>
   
   <!-- named template, defining a string tokenizer -->
   <xsl:template name="strTokenizer">
      <xsl:param name="inpStr"/>
      <xsl:param name="regex"/>
      
      <xsl:analyze-string select="elem" regex="{$regex}">
	 <xsl:non-matching-substring>
	    <token><xsl:value-of select="."/></token>
	 </xsl:non-matching-substring>
      </xsl:analyze-string>
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