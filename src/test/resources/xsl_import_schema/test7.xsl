<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:types1="http://example1/customTypes"
                exclude-result-prefixes="types1"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_b.xml -->
    
  <!-- An XSLT stylesheet test case, to test XSLT 3.0's xsl:import-schema 
       instruction providing access to definition of a simpleType which is a 
       union of two other simple types.
  -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:import-schema namespace="http://example1/customTypes" schema-location="test7.xsd"/>
  
  <xsl:template match="/info">
     <result>
        <xsl:for-each select="*">
           <xsl:element name="{name()}">
             <xsl:variable name="str" select="types1:UnionType1(.)" as="types1:UnionType1"/>
             <xsl:value-of select="$str"/>
           </xsl:element>
        </xsl:for-each>
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