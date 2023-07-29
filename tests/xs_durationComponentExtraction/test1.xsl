<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSLT stylesheet, tests XPath 3.1 duration values,
        component extraction functions. -->                 

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/">
      <result>
         <one><xsl:value-of select="years-from-duration(xs:yearMonthDuration('P20Y15M'))"/></one>
         <two><xsl:value-of select="years-from-duration(xs:yearMonthDuration('-P15M'))"/></two>
         <snip/>
         <three><xsl:value-of select="months-from-duration(xs:yearMonthDuration('P20Y15M'))"/></three>
         <four><xsl:value-of select="months-from-duration(xs:yearMonthDuration('-P20Y18M'))"/></four>
         <five><xsl:value-of select="months-from-duration(xs:dayTimeDuration('-P2DT15H0M0S'))"/></five>
         <snip/>
         <six><xsl:value-of select="days-from-duration(xs:dayTimeDuration('P3DT10H'))"/></six>
	     <seven><xsl:value-of select="days-from-duration(xs:dayTimeDuration('P3DT55H'))"/></seven>
         <eight><xsl:value-of select="days-from-duration(xs:yearMonthDuration('P3Y5M'))"/></eight>
         <snip/>
         <nine><xsl:value-of select="hours-from-duration(xs:dayTimeDuration('P3DT10H'))"/></nine>
         <ten><xsl:value-of select="hours-from-duration(xs:dayTimeDuration('P3DT12H32M12S'))"/></ten>
         <eleven><xsl:value-of select="hours-from-duration(xs:dayTimeDuration('PT123H'))"/></eleven>
         <twelve><xsl:value-of select="hours-from-duration(xs:dayTimeDuration('-P3DT10H'))"/></twelve>
         <snip/>
         <thirteen><xsl:value-of select="minutes-from-duration(xs:dayTimeDuration('P3DT10H'))"/></thirteen>
         <fourteen><xsl:value-of select="minutes-from-duration(xs:dayTimeDuration('-P5DT12H30M'))"/></fourteen>
         <snip/>
         <fifteen><xsl:value-of select="seconds-from-duration(xs:dayTimeDuration('P3DT10H12.5S'))"/></fifteen>
         <sixteen><xsl:value-of select="seconds-from-duration(xs:dayTimeDuration('-PT256S'))"/></sixteen>
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