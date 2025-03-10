<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL 3 stylesheet test case, to test XML Schema constructor function 
        calls for various gregorian type. -->                
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
         <xsl:variable name="val1" select="xs:gYearMonth('2005-10')" as="xs:gYearMonth"/>
         <one><xsl:value-of select="$val1 instance of xs:gYearMonth"/></one>
         <two><xsl:value-of select="$val1 instance of xs:gMonth"/></two>
         <xsl:variable name="val2" select="xs:gYear('2005')" as="xs:gYear"/>
         <three><xsl:value-of select="$val2 instance of xs:gYear"/></three>
         <four><xsl:value-of select="$val2 instance of xs:gDay"/></four>
         <xsl:variable name="val3" select="xs:gMonthDay('--10-05')" as="xs:gMonthDay"/>
         <five><xsl:value-of select="$val3 instance of xs:gMonthDay"/></five>
         <xsl:variable name="val4" select="xs:gDay('---22')" as="xs:gDay"/>
         <six><xsl:value-of select="$val4 instance of xs:gDay"/></six>
         <xsl:variable name="val5" select="xs:gMonth('--12')" as="xs:gMonth"/>
         <seven><xsl:value-of select="$val5 instance of xs:gMonth"/></seven>         
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