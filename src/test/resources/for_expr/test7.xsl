<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with cities.xml -->
   
   <!-- This XSLT stylesheet test, illustrates an XPath 3.1 "for" 
        expression evaluation.
        
        This XSLT stylesheet, borrows an XPath "for" expression 
        example from https://www.altova.com/. -->
   
   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <xsl:variable name="resultSeq" select="for $x in /cities/city, $y in /cities/city 
                                                                 return concat('from: ', $x, '  to: ', $y)"/>
      <result count="{count($resultSeq)}">
         <xsl:value-of select="$resultSeq"/>
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