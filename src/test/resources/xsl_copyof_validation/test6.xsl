<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                			
			    version="3.0">

  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test1.xml -->
  
  <!-- An XSL stylesheet test case to test, xsl:copy-of 
       instruction's attribute 'type' when validating 
       an attribute node. -->			
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:import-schema schema-location="info_1.xsd"/>
  
  <xsl:template match="/info">
	 <result>
		<xsl:apply-templates select="a"/>
	 </result>
  </xsl:template>
  
  <xsl:template match="a">
     <xsl:variable name="var1">	   
	   <newA>
         <xsl:copy-of select="@val" validation="strict"/>	   
	     <xsl:copy-of select="."/>
		 <xsl:copy-of select="."/>
		 <xsl:copy-of select="."/>
	   </newA>
	 </xsl:variable>
	 <xsl:copy-of select="$var1"/>
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
