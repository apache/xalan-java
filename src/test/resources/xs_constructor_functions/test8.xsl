<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSLT stylesheet, tests XPath 3.1 constructor function
        xs:double(), along with logical '=' operation evaluation on 
        xs:double values. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <xsl:variable name="double1" select="xs:double(12.23)"/>
      <xsl:variable name="double2" select="xs:double(12.2)"/>
      <result>
         <one><xsl:value-of select="xs:double(12.23)"/></one>
         <two><xsl:value-of select="xs:double('12.23')"/></two>
         <three><xsl:value-of select="xs:double(12.23) = xs:double(12.23)"/></three>
         <four><xsl:value-of select="xs:double(12.23) = xs:double('12.23')"/></four>
         <five><xsl:value-of select="xs:double(12.23) = xs:double(12.2)"/></five>
         <six><xsl:value-of select="$double1"/></six>
	     <seven><xsl:value-of select="$double2"/></seven>
	     <eight><xsl:value-of select="$double1 = $double1"/></eight>
         <nine><xsl:value-of select="$double1 = $double2"/></nine>
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