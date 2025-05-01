<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- This XSLT stylesheet test case, tests the XPath 3.1 function 
         fn:contains-token.
         
         Within this stylesheet, the XPath expression examples for the
         function fn:contains-token are borrowed from XPath 3.1 F&O spec.
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="seq1" select="('red', 'green', 'blue')"/>
    
    <xsl:variable name="collationUri" select="'http://www.w3.org/2005/xpath-functions/collation/html-ascii-case-insensitive'"/>
        
    <xsl:template match="/">
       <result>
         <one>
            <xsl:value-of select="contains-token('red green blue ', 'red')"/> 
         </one>
         <two>            
            <xsl:value-of select="contains-token($seq1, ' red ')"/>
         </two>
         <three>            
	        <xsl:value-of select="contains-token('red, green, blue', 'red')"/>
         </three>
         <four>            
	        <xsl:value-of select="contains-token('red green blue', 'RED', $collationUri)"/>
         </four>
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