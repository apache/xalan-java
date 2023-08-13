<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, for the XPath 3.1 fn:sort
        function. The fn:sort function examples used within this
        stylesheet, are borrowed from XPath 3.1 spec, with slight
        modifications. -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="seq1" select="(1, 4, 6, 5, 3)"/>
   <xsl:variable name="seq2" select="(1, -2, 5, 10, -10, 10, 8)"/>
   
   <xsl:variable name="emptySeq" select="()"/>
   
   <xsl:template match="/">
      <result>
        <one><xsl:value-of select="sort($seq1)"/></one>
                 
        <two><xsl:value-of select="sort($seq2, $emptySeq, function($a) { abs($a) })"/></two>
        
        <three><xsl:value-of select="sort($seq2, $emptySeq)"/></three>
        
        <four><xsl:value-of select="sort($seq2)"/></four>
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