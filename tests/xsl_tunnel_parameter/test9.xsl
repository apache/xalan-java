<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test5.xml -->
   
   <!-- An XSL stylesheet test case to test tunnel parameters, with greater 
        tunnel parameter propagation to child templates. This stylesheet
        also uses tunnel parameters to propagate node values. --> 				

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/info">
	  <result>
	     <xsl:call-template name="Template1">
		   <xsl:with-param name="node1" select="L1"/>
		   <xsl:with-param name="node2" select="L2" tunnel="yes"/>
		 </xsl:call-template>
	  </result>
   </xsl:template>
   
   <xsl:template name="Template1">
      <xsl:param name="node1"/>
	  <xsl:param name="node2" tunnel="yes"/>
	  <xsl:call-template name="Template2">
		<xsl:with-param name="node1" select="$node1"/>
		<xsl:with-param name="str1" select="'HELLO'" tunnel="yes"/>
	  </xsl:call-template>
   </xsl:template>
   
   <xsl:template name="Template2">      
	  <xsl:param name="node1"/>
	  <xsl:param name="str1" tunnel="yes"/>
	  <xsl:param name="node2" tunnel="yes"/>
	  <xsl:copy-of select="$node1"/>
	  <xsl:copy-of select="$node2"/>
	  <xsl:call-template name="Template3"/>
   </xsl:template>
   
   <xsl:template name="Template3">      
	  <xsl:param name="str1" tunnel="yes"/>
	  <xsl:param name="node2" tunnel="yes"/>
      <info>
	     <xsl:value-of select="name($node2) || ' ' || $str1"/>
	  </info>
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
