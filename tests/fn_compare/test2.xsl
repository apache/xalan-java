<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
      
   <!-- Test for the XPath 3.1 fn:compare() function. This stylesheet 
        test case, borrows fn:compare function examples from XPath 3.1 
        F&O spec. This stylesheet, reads input data to be transformed
        from an XML external document.
        
        Whereever within fn:compare function calls, if the third argument
        (for the collation to be used) is not present, then XalanJ uses
        the collation 'unicode codepoint collation' as its default 
        collation.  
   -->                           

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/document">
     <result>
        <one><xsl:value-of select="compare(elem[1]/str1, elem[1]/str2)"/></one>
        <two><xsl:value-of select="compare(elem[2]/str1, elem[2]/str2)"/></two>
        <three><xsl:value-of select="compare(elem[3]/str1, elem[3]/str2, 'http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/></three>
        <four><xsl:value-of select="compare(elem[4]/str1, elem[4]/str2)"/></four>
        
        <five><xsl:value-of select="compare(data[1]/@str1, data[1]/@str2)"/></five>
        <six><xsl:value-of select="compare(data[2]/@str1, data[2]/@str2)"/></six>
        <seven><xsl:value-of select="compare(data[3]/@str1, data[3]/@str2, 'http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/></seven>
        <eight><xsl:value-of select="compare(data[4]/@str1, data[4]/@str2)"/></eight>
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