<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test2.xml -->
  
    <!-- An XSLT test case, to test an XPath operator '-' applied 
         to node references. -->                				

    <xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="info">
	   <result>
	      <xsl:variable name="val1" select="item[1]/a - item[1]/b"/>
		  <one isInteger="{$val1 instance of xs:integer}" isDecimal="{$val1 instance of xs:decimal}">
		     <xsl:value-of select="$val1"/>
		  </one>
		  <xsl:variable name="val2" select="item[2]/a - item[2]/b"/>
		  <two isInteger="{$val2 instance of xs:integer}" isDecimal="{$val2 instance of xs:decimal}">
		     <xsl:value-of select="$val2"/>
		  </two>
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