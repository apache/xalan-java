<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                

   <!-- use with test1_a.xml -->
   
   <!-- an XSLT stylesheet test case, demonstrating use of the XPath 3.1 
        range "to" expression. This XSLT stylesheet, reads arguments of 
        XPath range "to" operation from an external XML document. This 
        stylesheet, also does grouping on data returned by the XPath 
        range "to" operation. -->
   
   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <xsl:variable name="rangeOfNums">
         <xsl:for-each select="x to y">
           <num><xsl:value-of select="."/></num>
         </xsl:for-each>
      </xsl:variable>
      <result>
         <xsl:for-each-group select="$rangeOfNums/num" group-by="(. mod 2) = 0">
            <xsl:variable name="numberClass">
               <xsl:choose>
                  <xsl:when test="current-grouping-key() = true()">
                     even
                  </xsl:when>
                  <xsl:otherwise>
                     odd
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <grp numberClass="{normalize-space($numberClass)}">
               <xsl:copy-of select="current-group()"/>
            </grp>
         </xsl:for-each-group>
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