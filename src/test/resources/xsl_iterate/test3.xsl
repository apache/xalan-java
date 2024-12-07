<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test1_b.xml -->
  
  <!-- An XSLT stylesheet test case, that uses xsl:iterate
       instruction multiple times depending on how many XML 
       input elements are there in sequence, for a given 
       XML document input structure. For each application 
       of xsl:iterate instruction, there's a xsl:break instruction 
       to exit from the current xsl:iterate loop at specific 
       conditions.
       
       This XSLT stylesheet, has been adapted from one of W3C
       XSLT 3.0 test cases. -->                
                
  <xsl:output method="xml" indent="yes"/>                

  <xsl:template match="/">
     <result>
        <xsl:apply-templates select="//page"/>
     </result>
  </xsl:template>

  <xsl:template match="page">
     <out>
        <xsl:iterate select="*">
           <xsl:choose>
              <xsl:when test="self::h3">
                <xsl:break>
                  <exit at="{position()}" of="{last()}"/>
                </xsl:break>
              </xsl:when>
              <xsl:otherwise>
                 <xsl:apply-templates select="self::p"/>
              </xsl:otherwise>
           </xsl:choose>
        </xsl:iterate>
     </out>
  </xsl:template>

  <xsl:template match="p">
     <xsl:copy-of select="."/>
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
