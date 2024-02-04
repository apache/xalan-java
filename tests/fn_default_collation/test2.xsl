<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0"> 
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1_a.xml -->
    
    <!-- Test for the XPath 3.1 fn:default-collation() 
         function.
    -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/data">
      <result>
        <xsl:apply-templates select="info"/>
      </result>
    </xsl:template>
    
    <!-- An XSL template to transform an XML input element 'info'.
      If an attribute 'lang' (denoting the language) is specified 
      on an XML input element 'info', then distinct values present  
      within an element 'info' are found using the collation for the
      specified language.
      If an attribute 'lang' is not specified on an XML input element
      'info', then XPath implementation's default collation is used
      while determining distinct values present within an element
      'info'.
    -->
    <xsl:template match="info">
      <info lang="{fn0:getEffectiveLanguageStr(@lang)}">
        <distinctValues>
          <xsl:choose>
             <xsl:when test="@lang">
               <xsl:for-each select="distinct-values(a, 'http://www.w3.org/2013/collation/UCA?lang=' || 
                                                                          @lang || ';strength=primary')">
                 <val>
                   <xsl:value-of select="."/>
                 </val>
               </xsl:for-each>
             </xsl:when>
             <xsl:otherwise>
               <xsl:for-each select="distinct-values(a, default-collation())">
                 <val>
	               <xsl:value-of select="."/>
                 </val>
               </xsl:for-each>
             </xsl:otherwise>
          </xsl:choose>
        </distinctValues>
      </info>
    </xsl:template>
    
    <!-- This stylesheet function determines the effective string value,
         to use as value for info/@lang attribute to produce within XSL 
         transform's output. -->
    <xsl:function name="fn0:getEffectiveLanguageStr" as="xs:string">
       <xsl:param name="langAttr" as="attribute(lang)?"/>
       
       <xsl:sequence select="if ($langAttr) then xs:string($langAttr) else 
                                        ('not_specified. using collation ' || default-collation())"/>
    </xsl:function>
    
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
