<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="xs math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSL stylesheet, has various XPath 3.1 expressions involving
        functions within the XML namespace http://www.w3.org/2005/xpath-functions/math.
        
        The XPath expressions mentioned within this XSL stylesheet test case, are borrowed
        from XPath 3.1 spec. 
   -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
         <pi><xsl:value-of select="math:pi()"/></pi>
         <pi_expr><xsl:value-of select="round(60 * (math:pi() div 180), 2)"/></pi_expr>
         
         <exp><xsl:value-of select="round(math:exp(0), 2)"/></exp>
         <exp><xsl:value-of select="round(math:exp(1), 2)"/></exp>
         <exp><xsl:value-of select="round(math:exp(2), 2)"/></exp>
         <exp><xsl:value-of select="round(math:exp(-1), 2)"/></exp>
         <exp><xsl:value-of select="round(math:exp(math:pi()), 2)"/></exp>
         <exp><xsl:value-of select="math:exp(xs:double('NaN'))"/></exp>
         <exp><xsl:value-of select="math:exp(xs:double('INF'))"/></exp>
         <exp><xsl:value-of select="math:exp(xs:double('-INF'))"/></exp>
         
         <exp10><xsl:value-of select="round(math:exp10(0), 2)"/></exp10>
         <exp10><xsl:value-of select="round(math:exp10(1), 2)"/></exp10>
         <exp10><xsl:value-of select="round(math:exp10(0.5), 2)"/></exp10>
         <exp10><xsl:value-of select="round(math:exp10(-1), 2)"/></exp10>
         <exp10><xsl:value-of select="math:exp10(xs:double('NaN'))"/></exp10>
         <exp10><xsl:value-of select="math:exp10(xs:double('INF'))"/></exp10>
         <exp10><xsl:value-of select="math:exp10(xs:double('-INF'))"/></exp10>
         
         <log><xsl:value-of select="math:log(0)"/></log>
         <log><xsl:value-of select="round(math:log(math:exp(1)), 2)"/></log>
         <log><xsl:value-of select="round(math:log(1.0e-3), 2)"/></log>
         <log><xsl:value-of select="round(math:log(2), 2)"/></log>
         <log><xsl:value-of select="math:log(-1)"/></log>
         <log><xsl:value-of select="math:log(xs:double('NaN'))"/></log>
         <log><xsl:value-of select="math:log(xs:double('INF'))"/></log>
         <log><xsl:value-of select="math:log(xs:double('-INF'))"/></log>
         
         <log10><xsl:value-of select="math:log10(0)"/></log10>
         <log10><xsl:value-of select="round(math:log10(1.0e3), 2)"/></log10>
         <log10><xsl:value-of select="round(math:log10(1.0e-3), 2)"/></log10>
         <log10><xsl:value-of select="round(math:log10(2), 2)"/></log10>
         <log10><xsl:value-of select="math:log10(-1)"/></log10>
         <log10><xsl:value-of select="math:log10(xs:double('NaN'))"/></log10>
         <log10><xsl:value-of select="math:log10(xs:double('INF'))"/></log10>
         <log10><xsl:value-of select="math:log10(xs:double('-INF'))"/></log10>
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