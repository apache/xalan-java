<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="xs math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSLT stylesheet test, illustrates an XPath 3.1 "for" 
        expression evaluation, with XML namespace references within
        the XPath "for" expression's return expression. -->                 

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>
        <xsl:value-of select="for $a in (1 to 5) return xs:integer(10) * math:sin($a)"/>
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