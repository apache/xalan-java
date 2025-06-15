<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
			    xmlns:xs="http://www.w3.org/2001/XMLSchema"
			    xmlns:map="http://www.w3.org/2005/xpath-functions/map"
			    xmlns:array="http://www.w3.org/2005/xpath-functions/array"
			    exclude-result-prefixes="#all" 
			    version="3.0">
			    
  <!-- An XSL stylesheet contributed by Martin Honnen, for the 
       jira issue XALANJ-2818. -->
       
  <!-- use with sample2.xml -->       			    

  <xsl:output method="text"/>
  
  <xsl:param name="count" as="xs:integer" select="1"/>

  <xsl:template match="document-node()">
    <xsl:param name="count" select="$count"/>
    <xsl:text>Matching document-node() </xsl:text>
    <xsl:value-of select="$count"/>
    <xsl:text>&#10;</xsl:text>
    <xsl:apply-templates>
      <xsl:with-param name="count" select="$count + 1"/>
    </xsl:apply-templates>
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
