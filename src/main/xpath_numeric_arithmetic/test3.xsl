<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT test case to test, XPath arithmetic operators +,-,div and mod -->								
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
        <xsl:for-each select="(5,6,7)">
	       <xsl:variable name="n0" select="."/>
	       <xsl:variable name="n1" select="2.5"/>
		   <info n0="{$n0}" n1="{$n1}">
			  <sum><xsl:value-of select="$n0 + $n1"/></sum>
			  <diff><xsl:value-of select="$n0 - $n1"/></diff>
			  <div><xsl:value-of select="$n0 div $n1"/></div>
			  <mod><xsl:value-of select="$n0 mod $n1"/></mod>
		   </info>
	    </xsl:for-each>
	 </result>
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
