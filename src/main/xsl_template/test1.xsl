<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
                
    <!-- use with test1_a.xml -->
    
    <!-- This XSLT stylesheet test case, tests the
         xsl:template instruction.
    -->                
                
    <xsl:output method="xml" indent="yes"/>
        
    <xsl:template match="/">
       <alpha>
         <xsl:apply-templates select="alpha/*"/>
       </alpha>
    </xsl:template>
    
    <xsl:template match="alpha">
       <xsl:element name="{name()}">
          <xsl:attribute name="pos" select="position()"/>
          <xsl:copy-of select="node()"/>
       </xsl:element>
    </xsl:template>
    
    <xsl:template match="*">
       <xsl:element name="{name()}">
          <xsl:copy-of select="node()"/>
       </xsl:element>
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