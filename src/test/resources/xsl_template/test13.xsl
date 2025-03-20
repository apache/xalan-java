<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
			    version="3.0">
				
  <!-- Author: mukulg@apache.org --> 

  <!--  An XSL 3 stylesheet test case, to test xsl:template match 
        with pattern ".". This stylesheet also tests providing a 
		sequence of XDM atomic value to the called template. -->    

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
      <result>
	     <xsl:apply-templates select="(xs:integer(1), xs:date('2005-10-05'), xs:date('1995-04-12'), xs:integer(4))"/>
	  </result>
  </xsl:template>
  
  <xsl:template match=".">
    <xsl:variable name="var1" select="."/>
	<xsl:choose>
	   <xsl:when test="$var1 instance of xs:integer">
	      <value><xsl:value-of select="'xs:integer : ' || $var1"/></value>
	   </xsl:when>
	   <xsl:when test="$var1 instance of xs:date">
	      <value><xsl:value-of select="'xs:date : ' || $var1"/></value>
	   </xsl:when>
	   <xsl:otherwise>
	      <value><xsl:value-of select="'anotherXsType : ' || $var1"/></value>
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
