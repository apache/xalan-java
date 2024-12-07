<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test, to test XPath 3.1 function 
        fn:insert-before. -->                               

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="abc" select="('a', 'b', 'c')"/>
   
   <xsl:template match="/">
      <result>
         <one><xsl:value-of select="insert-before($abc, 0, 'z')"/></one>
         <two><xsl:value-of select="insert-before($abc, 1, 'z')"/></two>
         <three><xsl:value-of select="insert-before($abc, 2, 'z')"/></three>
         <four><xsl:value-of select="insert-before($abc, 3, 'z')"/></four>
         <five><xsl:value-of select="insert-before($abc, 4, 'z')"/></five>
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