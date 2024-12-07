<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with xml2.xml -->                 

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/info">
       <result>
         <xsl:variable name="c1" select="10" as="xs:integer"/>
         <xsl:apply-templates select="val">
           <xsl:with-param name="c1Val" select="$c1" as="xs:integer"/>
         </xsl:apply-templates>
       </result>
    </xsl:template>

    <xsl:template match="val" as="element(val)">
      <xsl:param name="c1Val" as="xs:integer"/>
      <val>
        <xsl:attribute name="id" select="@id + $c1Val"/>
        <xsl:value-of select="."/>
      </val>
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