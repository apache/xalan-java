<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1_a.xml -->                
    
    <!-- An XSLT stylesheet test case, to test the sequence type
         declaration attribute "as" on an xsl:template/xsl:param
         element on a template with "match" attribute.
    -->                  
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/info">       
       <result>
         <xsl:apply-templates select="*">
            <xsl:with-param name="dt1" select="xs:date('2005-10-07')"/>
         </xsl:apply-templates>
       </result>
    </xsl:template>
    
    <xsl:template match="abc">
      <xsl:param name="dt1" as="xs:date"/>
      <abc val="{.}">
         <xsl:value-of select="$dt1 instance of xs:date"/>
      </abc>
    </xsl:template>
    
    <xsl:template match="pqr">
      <xsl:param name="dt1" as="xs:date"/>
      <pqr val="{.}">
         <xsl:value-of select="$dt1 instance of xs:integer"/>    
      </pqr>
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