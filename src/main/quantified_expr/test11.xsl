<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, to test the XPath 3.1 quantified 
        expressions 'some' and 'every'.
   -->                 

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/">
      <result>
         <someEvenVal><xsl:value-of select="some $x in (1, 3, 5, 7, 9) satisfies ($x mod 2) eq 0"/></someEvenVal>
         <someEvenVal><xsl:value-of select="some $x in (1, 2, 5, 7, 9) satisfies ($x mod 2) eq 0"/></someEvenVal>
         <everyEvenVal><xsl:value-of select="every $x in (2, 4, 6, 8, 10) satisfies ($x mod 2) eq 0"/></everyEvenVal>
         <everyEvenVal><xsl:value-of select="every $x in (2, 3, 6, 8, 10) satisfies ($x mod 2) eq 0"/></everyEvenVal>
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