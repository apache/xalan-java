<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
				xmlns:xs="http://www.w3.org/2001/XMLSchema"				
				exclude-result-prefixes="xs" version="3.0">
				
  <!-- An XSL stylesheet, contributed by Martin Honnen,
       for the jira issue XALANJ-2754. -->				
				
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
  
  <xsl:template match="string">
    <string-test>
      <xsl:variable name="str" select="parse-json(.)"/>
	  <string-value>
        <xsl:value-of select="$str"/>
      </string-value>
      <string-instance-of-test>
        <xsl:value-of select="$str instance of xs:string"/>
      </string-instance-of-test>
    </string-test>
  </xsl:template>
  
  <xsl:template match="number">
    <number-test>
      <xsl:variable name="dbl" select="parse-json(.)"/>
      <number-instance-of-test>
        <xsl:value-of select="$dbl instance of xs:double"/>
      </number-instance-of-test>
    </number-test>
  </xsl:template>
  
  <xsl:template match="boolean">
    <boolean-test>
      <xsl:variable name="bool" select="parse-json(.)"/>
      <boolean-instance-of-test>
        <xsl:value-of select="$bool instance of xs:boolean"/>
      </boolean-instance-of-test>
    </boolean-test>
  </xsl:template>
  
  <xsl:template match="empty">
    <empty-seq-test>
      <xsl:variable name="empty-seq" select="parse-json(.)"/>
      <empty-seq-instance-of-test>
        <xsl:value-of select="$empty-seq instance of item()? and empty($empty-seq)"/>
      </empty-seq-instance-of-test>
    </empty-seq-test>
  </xsl:template>
  
  <xsl:template match="/root">
     <result>
       <xsl:apply-templates select="*"/>
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
