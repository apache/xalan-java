<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
				exclude-result-prefixes="fn"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL 3 stylesheet test case, to test xsl:result-document 
       instruction having method="json". -->			    					
  
  <xsl:output method="text"/>

  <xsl:template match="/">
     <xsl:variable name="xmlMapNode1" select="doc('map1.xml')"/>
     <xsl:result-document href="result3.json" method="json">
		<xsl:variable name="xmlNodeSet1">
		   <xsl:apply-templates select="$xmlMapNode1/fn:map"/>
		</xsl:variable>
        <xsl:value-of select="xml-to-json($xmlNodeSet1, map {'indent' : true()})"/> 		
     </xsl:result-document>
  </xsl:template>
  
  <!-- This stylesheet template, appends more XML structure information to the 
       original XML element node matched by this template. -->
  <xsl:template match="fn:map">
    <xsl:copy>
	   <fn:array>
	      <xsl:copy-of select="fn:array/@*"/>
		  <xsl:copy-of select="fn:array/*"/>
		  <fn:map>
	        <fn:number key="x1">10</fn:number>
	        <fn:number key="y1">11</fn:number>
	        <fn:number key="z1">12</fn:number>
	      </fn:map>
	   </fn:array>
	</xsl:copy>
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
