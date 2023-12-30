<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test XPath 3.1 
       function fn:deep-equal. 
  -->                
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
       <one>
         <xsl:variable name="seq1" select="(xs:integer(1), xs:integer(2))"/>
         <xsl:variable name="seq2" select="(xs:integer(1), xs:string('hello1'))"/>
         <xsl:value-of select="deep-equal($seq1, $seq2)"/>
       </one>
       <two>
         <xsl:variable name="seq3" select="(xs:integer(1), xs:integer(2))"/>
	     <xsl:variable name="seq4" select="(xs:integer(1), xs:integer(2))"/>
         <xsl:value-of select="deep-equal($seq3, $seq4)"/>
       </two>
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