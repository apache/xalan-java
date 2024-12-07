<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				                
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case to test tunnel parameters. -->                			

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
	  <result>
	     <xsl:call-template name="A1">
		   <xsl:with-param name="str1" select="'hello'"/>
	       <xsl:with-param name="str2" select="'world'" tunnel="yes"/>		   
		 </xsl:call-template>
	  </result>
   </xsl:template>
   
   <xsl:template name="A1">
      <xsl:param name="str1"/>
	  <xsl:param name="str2"/>
	  <info1>
	     <xsl:value-of select="$str1 || ' ' || $str2"/>
	  </info1>
	  <info2>
	    <xsl:call-template name="P1">
		   <xsl:with-param name="str1" select="$str1"/>		   
		</xsl:call-template>
	  </info2>
   </xsl:template>
   
   <xsl:template name="P1">
      <xsl:param name="str1"/>
	  <xsl:param name="str2" tunnel="yes"/>
	  <xsl:value-of select="'*** ' || $str1 || ' ' || $str2 || ' ***'"/>
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
