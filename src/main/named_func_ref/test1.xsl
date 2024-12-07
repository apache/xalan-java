<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fn="http://www.w3.org/2005/xpath-functions"
				exclude-result-prefixes="fn" 
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT test case, to test XPath 3.1 named function references. -->				
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match='/'>
     <result>
	   <one>
         <xsl:value-of select="for-each((-1,-2,3,4), fn:abs#1)"/>
	   </one>
	   <two>
         <xsl:value-of select="for-each-pair(('a', 'b', 'c'), ('x', 'y', 'z'), concat#2)"/>
	   </two>
	   <three>
         <xsl:value-of select="sort((1, -2, 5, 10, -10, 10, 8), (), fn:abs#1)"/>
	   </three>
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
