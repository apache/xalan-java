<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- use with test1_a.xml -->
   
    <!-- An XSLT stylesheet test case, to test XPath 3.1 "instance of" 
         expression. -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="seq1" select="(5, 6)"/>
    
    <xsl:template match="/info">       
       <result>
         <one>
            <xsl:value-of select="5 instance of xs:integer"/>
         </one>
         <two>
            <xsl:value-of select="5 instance of xs:decimal"/>
         </two>
         <three>            
            <xsl:value-of select="$seq1 instance of xs:integer+"/>
         </three>
         <four>
	        <xsl:value-of select=". instance of element()"/>
         </four>
         <five>
	        <xsl:value-of select="* instance of element()+"/>
         </five>
         <six>
	        <xsl:value-of select="* instance of element(val)+"/>
         </six>
         <seven>
	        <xsl:value-of select="//@* instance of attribute()+"/>
         </seven>
         <eight>
	        <xsl:value-of select="//@* instance of attribute(attr1)+"/>
         </eight>
         <nine>
	        <xsl:value-of select="//@* instance of xs:integer+"/>
         </nine>
         <ten>
	        <xsl:value-of select="* instance of xs:integer+"/>
         </ten>
         <eleven>
	        <xsl:value-of select="$seq1 instance of element()+"/>
         </eleven>
         <twelve>
	        <xsl:value-of select="5 instance of element()"/>
         </twelve>
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