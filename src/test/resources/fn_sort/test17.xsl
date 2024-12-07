<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, to test an XPath 3.1 function
        fn:sort which produces the result in an ascending order of input 
        sequence items. This stylesheet, also produces a descending ordered
        sort result of input sequence items using the function fn:reverse
        on result of call to function fn:sort. -->                            

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="seq1" select="('p','a','b','q')"/>
   
   <xsl:template match="/">
      <xsl:variable name="sortResult" select="sort($seq1, ())"/>
      <result>
         <one sortOrder="ascending"><xsl:value-of select="$sortResult"/></one>
         <two sortOrder="descending"><xsl:value-of select="reverse($sortResult)"/></two>
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