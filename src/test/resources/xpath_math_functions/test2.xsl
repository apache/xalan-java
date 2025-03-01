<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="xs math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSLT stylesheet, has various XPath 3.1 expressions involving
        functions within the XML namespace http://www.w3.org/2005/xpath-functions/math.
        
        The XPath expressions mentioned within this XSLT stylesheet test, are borrowed
        from XPath 3.1 spec. -->                 

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
         <pow><xsl:value-of select="math:pow(2, 3)"/></pow>
         <pow><xsl:value-of select="math:pow(-2, 3)"/></pow>
         <pow><xsl:value-of select="math:pow(2, -3)"/></pow>
         <pow><xsl:value-of select="math:pow(-2, -3)"/></pow>
         <pow><xsl:value-of select="math:pow(2, 0)"/></pow>
         <pow><xsl:value-of select="math:pow(0, 0)"/></pow>
         <pow><xsl:value-of select="math:pow(xs:double('INF'), 0)"/></pow>
         <pow><xsl:value-of select="math:pow(xs:double('NaN'), 0)"/></pow>
         <pow><xsl:value-of select="math:pow(-math:pi(), 0)"/></pow>
         <pow><xsl:value-of select="math:pow(0e0, 3)"/></pow>
         <pow><xsl:value-of select="math:pow(0e0, 4)"/></pow>
         <pow><xsl:value-of select="math:pow(-0e0, 3)"/></pow>
         <pow><xsl:value-of select="math:pow(0, 4)"/></pow>
         <pow><xsl:value-of select="math:pow(0e0, -3)"/></pow>
         <pow><xsl:value-of select="math:pow(0e0, -4)"/></pow>
         <pow><xsl:value-of select="math:pow(-0e0, -3)"/></pow>
         <pow><xsl:value-of select="math:pow(16, 0.5e0)"/></pow>
         <pow><xsl:value-of select="math:pow(16, 0.25e0)"/></pow>
         <pow><xsl:value-of select="math:pow(-2.5e0, 2.0e0)"/></pow>
         
         <sqrt><xsl:value-of select="math:sqrt(0.0e0)"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(-0.0e0)"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(1.0e6)"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(2.0e0)"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(-2.0e0)"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(xs:double('NaN'))"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(xs:double('INF'))"/></sqrt>
         <sqrt><xsl:value-of select="math:sqrt(xs:double('-INF'))"/></sqrt>
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