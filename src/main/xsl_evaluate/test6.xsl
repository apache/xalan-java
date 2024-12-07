<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
			    xmlns:array="http://www.w3.org/2005/xpath-functions/array"				
			    exclude-result-prefixes="array" version="3.0">
				
  <!-- Author: mukulg@apache.org -->
    
  <!-- use with test2.xml -->
    
  <!-- An XSLT stylesheet test case, to test xsl:evaluate 
       instruction. -->				
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="data">
    <xsl:variable name="array" as="item()">
      <xsl:evaluate xpath="." context-item="."/>
    </xsl:variable>
    <xsl:value-of select="array:sort($array)"/>
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
