<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"                
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                 

    <!-- An XSL stylesheet test case to test, xsl:for-each-group instruction to 
	     group atomic values using 'group-by' attribute. -->				
	
	<xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>	     
	      <xsl:for-each-group select="('abc', 3, 'mno', 4, 5)" group-by=". instance of xs:string">
		     <group key="{if (current-grouping-key() eq true()) then 'string_values' else 'non_string_values'}">			   
			   <xsl:for-each select="current-group()">
			     <item>
				   <xsl:value-of select="."/>
				 </item>
			   </xsl:for-each>
			 </group>
		  </xsl:for-each-group>
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
