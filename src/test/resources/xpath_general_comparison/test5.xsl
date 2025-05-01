<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
				version="3.0">				
		
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL test case to test, XPath 3.1 general comparison 
       relational operators >, >=. -->
       				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match='/'>
     <result>
		<one>
		  <xsl:value-of select="1 &gt; (5,6,7)"/>
		</one>
		<two>
		  <xsl:value-of select="1 &gt; (0,5)"/>
		</two>
		<three>
		  <xsl:value-of select="1 &gt;= (5,6,7)"/>
		</three>
		<four>
		  <xsl:value-of select="1 &gt;= (1,2,3,4,5)"/>
		</four>
		<five>
		  <xsl:value-of select="xs:integer(1) &gt; (5,6,7)"/>
		</five>
		<six>
		  <xsl:value-of select="xs:integer(1) &gt; (xs:integer(0),xs:integer(5))"/>
		</six>
	 </result>
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
