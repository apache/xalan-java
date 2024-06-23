<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- An XSL stylesheet, contributed by Martin Honnen,
       for the jira issue XALANJ-2753. -->                

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="array">
    <array-test>
      <xsl:variable name="xdm-array" select="parse-json(.)"/>
      <array-instance-of-test>
        <xsl:value-of select="$xdm-array instance of array(*)"/>
      </array-instance-of-test>
    </array-test>
  </xsl:template>

  <xsl:template match="map">
    <map-test>
      <xsl:variable name="xdm-map" select="parse-json(.)"/>
      <map-instance-of-test>
        <xsl:value-of select="$xdm-map instance of map(*)"/>
      </map-instance-of-test>
    </map-test>
  </xsl:template>

  <xsl:template match="/">
    <xsl:copy>
      <xsl:apply-templates/>
    </xsl:copy>
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
