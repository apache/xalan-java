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
         <sin><xsl:value-of select="math:sin(0)"/></sin>
         <sin><xsl:value-of select="math:sin(-0.0e0)"/></sin>
         <sin><xsl:value-of select="math:sin(math:pi() div 2)"/></sin>
         <sin><xsl:value-of select="math:sin(-math:pi() div 2)"/></sin>
         <sin><xsl:value-of select="math:sin(math:pi())"/></sin>
         
         <cos><xsl:value-of select="math:cos(0)"/></cos>
         <cos><xsl:value-of select="math:cos(math:pi())"/></cos>
         
         <tan><xsl:value-of select="math:tan(0)"/></tan>
         <tan><xsl:value-of select="math:tan(math:pi())"/></tan>
         
         <asin><xsl:value-of select="math:asin(0)"/></asin>
         <asin><xsl:value-of select="math:asin(1.0e0)"/></asin>
         <asin><xsl:value-of select="math:asin(-1.0e0)"/></asin>
         
         <acos><xsl:value-of select="math:acos(0)"/></acos>
         <acos><xsl:value-of select="math:acos(-1.0e0)"/></acos>
         
         <atan><xsl:value-of select="math:atan(1.0e0)"/></atan>
         <atan><xsl:value-of select="math:atan(-1.0e0)"/></atan>
         
         <atan2><xsl:value-of select="math:atan2(-0.0e0, -0.0e0)"/></atan2>
         <atan2><xsl:value-of select="math:atan2(-0.0e0, -1)"/></atan2>
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