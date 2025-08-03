<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
				
    <!-- use with test2.xml -->
    
    <!-- An XSL stylesheet test case, to test XPath general 
         comparison operators = and !=.-->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
		  <xsl:value-of select="/doc/a = '1'"/>
		  <xsl:text>,</xsl:text>
		  <xsl:value-of select="/doc/a = '2'"/>
		  <xsl:text>,</xsl:text>
		  <xsl:value-of select="/doc/a = '3'"/>
		  <xsl:text>,</xsl:text>
		  <xsl:value-of select="/doc/b = '5'"/>
		  <xsl:text>,</xsl:text>
		  <xsl:value-of select="/doc/b != '5'"/>
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
