<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">             

   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, to test XPath 3.1 fn:sort function.
   
        Within this stylesheet test example, the data to be sorted and
        the expected sort results using English and French collations, are
        borrowed from Oracle's Java SE documentation (https://docs.oracle.com/en/java/javase/20/).
   -->
   
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="seq1" select="('péché', 'peach', 'pêche', 'sin')"/>
   
   <xsl:template match="/">
     <result>
        <!-- sort using default collation (for XalanJ, default collation is 'unicode codepoint collation') -->
        <one><xsl:value-of select="sort($seq1)"/></one>
        
        <!-- sort using English collation, using 'unicode collation algorithm' -->
        <two><xsl:value-of select="sort($seq1, 'http://www.w3.org/2013/collation/UCA?lang=en')"/></two>
        
        <!-- sort using French collation, using 'unicode collation algorithm' -->
        <three><xsl:value-of select="sort($seq1, 'http://www.w3.org/2013/collation/UCA?lang=fr')"/></three>
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