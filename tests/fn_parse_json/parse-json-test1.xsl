<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				version="3.0"
                exclude-result-prefixes="xs map array">
                
  <!-- XSL test case stylesheet, provided within XalanJ jira issue 
       XALANJ-2738. Courtesy, Martin Honnen. -->
       
  <!-- use with xml-sample-with-json-data1.xml -->                      

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <result>
       <value>
	      <xsl:apply-templates select="root/data"/>
	   </value>
	</result>
  </xsl:template>
  
  <xsl:template match="data">
    <xsl:variable name="json-map" select="parse-json(.)"/>
    <xsl:value-of select="array:get(map:get($json-map, 'data'), 1)"/>
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
