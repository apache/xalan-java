<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math"				
                version="3.0">				    
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test2.xml -->
   
    <!-- An XSL stylesheet test case, to test xsl:apply-templates 
         instruction using a combination of xdm nodes and atomic 
         values as input. --> 

    <xsl:output method="xml" indent="yes"/>	

	<xsl:template match="/">
	   <result>
	      <xsl:apply-templates select="(/root/b, 7, 3, 4, /root/c, /root/a)"/>
	   </result>
	</xsl:template>
	
	<xsl:template match="*">
	   <xsl:element name="{name()}">
	     <xsl:value-of select="'hello ' || ."/>, <xsl:value-of select="following-sibling::*[1]"/>
	   </xsl:element>
	</xsl:template>
	
	<xsl:template match=".">
	   <number value="{.}">
	     <xsl:value-of select="math:sqrt(.)"/>
	   </number>
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
