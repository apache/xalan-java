<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->

  <!--  An XSL 3 stylesheet test case, to test xsl:template
        match pattern, matching text nodes present within an
        intermediate XSL stylesheet result variable.         
  -->			     

  <xsl:output method="html"/>

  <xsl:template match="/">
      <html>
	     <head>
		   <title>XSL transformation result</title>
		 </head>
		 <body>		   
		   <br/>
		   <xsl:variable name="txtLines">
		      <xsl:for-each select="unparsed-text-lines('input.txt')">
		         <node><xsl:value-of select="."/></node>
		      </xsl:for-each>
		   </xsl:variable>
		   <xsl:apply-templates select="$txtLines/node"/>
		 </body>
	  </html>
  </xsl:template>

  <xsl:template match="node">
    <xsl:variable name="strValue1" select="string(.)"/>
    <xsl:choose>
       <xsl:when test="starts-with(., '==')">
          <h1><xsl:value-of select="replace($strValue1, '==', '')"/></h1>
       </xsl:when>
       <xsl:when test="starts-with(., '::')">
          <h2><xsl:value-of select="replace($strValue1, '::', '')"/></h2>
       </xsl:when>
       <xsl:otherwise>
          <h3><xsl:value-of select="$strValue1"/></h3>
       </xsl:otherwise>
    </xsl:choose>
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
