<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				                
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test4.xml -->
   
   <!-- An XSL stylesheet test case to test tunnel parameters. -->               		

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/info">
	  <result>
	     <xsl:apply-templates select="*">
			<xsl:with-param name="str1" select="'hello'" tunnel="yes"/>
		 </xsl:apply-templates>
	  </result>
   </xsl:template>
   
   <xsl:template match="*">
	 <xsl:param name="str1" tunnel="yes"/>
	 <info1>
	   <xsl:value-of select="'{' || name() || '}' || ' ' || $str1"/>
	 </info1>
	 <info2>
	    <xsl:apply-templates select="M"/>
	 </info2>
   </xsl:template>
   
   <xsl:template match="M">
	 <xsl:param name="str1" tunnel="yes"/>
	 <xsl:value-of select="'*** ' || $str1 || ' ***'"/>
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
