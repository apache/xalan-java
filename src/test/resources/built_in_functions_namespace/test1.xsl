<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                exclude-result-prefixes="fn"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet, to test the XPath 3.1 built-in
        functions namespace http://www.w3.org/2005/xpath-functions. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
        <one><xsl:value-of select="concat('hello', ' ', 'world')"/></one>
        <two><xsl:value-of select="fn:concat('hello', ' ', 'world')"/></two>
        <three><xsl:value-of select="abs(-10)"/></three>
        <four><xsl:value-of select="fn:abs(-10)"/></four> 
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