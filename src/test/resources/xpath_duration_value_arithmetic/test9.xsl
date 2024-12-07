<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- This XSLT stylesheet, tests XPath 3.1 arithmetic on
        XML Schema duration typed values, by fetching the 
        input values from an XML external source document. -->                

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/temp">
      <result>
         <one><xsl:value-of select="xs:yearMonthDuration(val1/@x1) div val2/num[1]/@x1"/></one>
         <two><xsl:value-of select="xs:yearMonthDuration(val1/@x1) div xs:integer(val2/num[2]/@x1)"/></two>
         <three><xsl:value-of select="xs:yearMonthDuration(val1/@x1) div number(val2/num[3]/@x1)"/></three>
         <four><xsl:value-of select="xs:yearMonthDuration(val1/@x1) div xs:float(val2/num[4]/@x1)"/></four>
         <five><xsl:value-of select="xs:yearMonthDuration(val1/@x1) div xs:double(val2/num[5]/@x1)"/></five>
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