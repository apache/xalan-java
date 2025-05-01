<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- An XSLT stylesheet test, to test the XPath 3.1 sequence construction
        using comma operator.
        
        The XSLT stylesheet example transforms an XML input document into a list
        in which each author's name appears only once, followed by a list of 
        titles of books written by that author. This example assumes that the 
        context item is the bib element in an XML input document.
        
        The XPath expression strings, that're illustrated within this
        XSLT stylesheet are borrowed from XPath 3.1 spec. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/bib">
      <result>
        <xsl:copy-of select="for $a in distinct-values(book/author)
                                                          return ((book/author[. = $a])[1], book[author = $a]/title)"/>
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