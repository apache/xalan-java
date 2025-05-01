<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="xs array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3.0 test case for the, XPath 3.1 function array:for-each.
         XPath expression examples within this test case, are borrowed
         from XPath 3.1 F&O spec.
    -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>
         <xsl:variable name="arr1" select="['A', 'B', 1, 2]" as="array(*)"/>
         <xsl:variable name="forEachResult" select="array:for-each($arr1, function($z) {$z instance of xs:integer})" as="array(xs:boolean)"/>
         <one>
           <xsl:for-each select="$forEachResult"> 
              <value><xsl:value-of select="."/></value>
           </xsl:for-each>
         </one>
         <xsl:variable name="arr2" select="['the cat', 'sat', 'on the mat']" as="array(xs:string)"/>
         <xsl:variable name="forEachResult" select="array:for-each($arr2, function($z) {tokenize($z, '\s+')})" as="array(*)"/>
         <two>
            <xsl:for-each select="$forEachResult">
              <xsl:variable name="seq" select="."/>
              <sequence isStrSeq="{$seq instance of xs:string+}" seqSize="{count($seq)}">
                <xsl:for-each select="$seq">
                  <item>
                     <xsl:value-of select="."/>
                  </item>
                </xsl:for-each>
              </sequence>
            </xsl:for-each>
         </two>                                 
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
