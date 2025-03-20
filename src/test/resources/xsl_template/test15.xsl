<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="xs fn0"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->

  <!--  An XSL 3 stylesheet test case, to test xsl:template match 
        with pattern ".". This stylesheet also tests providing an 
		XDM atomic value to the called template. -->			      

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <result>
	     <xsl:variable name="seq1" select="(xs:integer(1), xs:date('2005-10-05'), xs:date('1995-04-12'), xs:integer(4))"/>
		 <xsl:for-each select="$seq1">
		    <xsl:variable name="item1" select="."/>
		    <xsl:apply-templates select="$item1"/>
		 </xsl:for-each>
	  </result>
  </xsl:template>
  
  <xsl:template match=".">
    <xsl:variable name="item1" select="."/>
	<value><xsl:value-of select="fn0:getStrResultVal($item1)"/></value>
  </xsl:template>
  
  <!-- An XSL stylesheet function definition, that transforms an XDM input 
       value to a specific string conversion of it. -->
  <xsl:function name="fn0:getStrResultVal" as="xs:string">
    <xsl:param name="item1"/>
	<xsl:choose>
	   <xsl:when test="$item1 instance of xs:integer">
	      <xsl:sequence select="'xs:integer : ' || $item1"/>
	   </xsl:when>
	   <xsl:when test="$item1 instance of xs:date">
	      <xsl:sequence select="'xs:date : ' || $item1"/>
	   </xsl:when>
	   <xsl:otherwise>
	      <xsl:sequence select="'anotherXsType : ' || $item1"/>
	   </xsl:otherwise>
	</xsl:choose>
  </xsl:function>
  
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
