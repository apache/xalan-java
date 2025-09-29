<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
                version="3.0">				    
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1.xml -->
  
    <!-- An XSLT test case, to test XPath 3.1 text() pattern. -->				
				
    <xsl:output method="xml" indent="yes"/>	
				
	<xsl:template match="/info">
		<result>
		   <textNodes count="{count(text())}"/>
		   <info1>
			  <text1><xsl:value-of select="text()[1]"/></text1>
			  <text2><xsl:value-of select="text()[2]"/></text2>
			  <text3><xsl:value-of select="text()[3]"/></text3>
			  <text4><xsl:value-of select="text()[4]"/></text4>
		   </info1>		   
		   <info2>
		      <x>
		         <xsl:copy-of select="text()[. = 'hello']/following-sibling::*"/>
			  </x>
			  <y>
		         <xsl:copy-of select="text()[. = 'there']/following-sibling::*"/>
			  </y>
		   </info2>
		   <info3>		      
			  <xsl:value-of select="let $seq1 := text() return $seq1[3]"/>
		   </info3>
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
