<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:mf="http://example.com/mf"
                exclude-result-prefixes="mf xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
                   
   <!-- Initial contribution by Martin Honnen, for the 
        jira issue XALANJ-2808. -->
                   
   <!-- use with test1_a.xml -->                
   
   <!-- An XSL stylesheet test case, for the XPath 3.1 function fn:fold-left. -->                   

   <xsl:output method="xml" indent="yes"/>

   <xsl:function name="mf:mult" as="xs:decimal">
     <xsl:param name="a" as="xs:decimal"/>
     <xsl:param name="b" as="xs:decimal"/>
     <xsl:sequence select="$a * $b"/>
   </xsl:function>
      
   <xsl:template match="/temp">      
      <result>
        <xsl:variable name="valList1" select="val"/>
        <val1><xsl:value-of select="fold-left($valList1, 1, mf:mult#2)"/></val1>
        
        <xsl:variable name="valList2" select="data/@*"/>
        <val2><xsl:value-of select="fold-left($valList2, 1, mf:mult#2)"/></val2>
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