<?xml version="1.0" encoding="utf-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                expand-text="yes"
				version="3.0">
				
    <!-- Authors: mukulg@apache.org -->                              
				
    <!-- use with test3.xml -->
    
    <!-- XSL 3 stylesheet test case, to test xsl:mode instruction. -->				
				
    <xsl:output method="xml" indent="yes"/>

    <xsl:mode on-no-match="shallow-copy"/>	

	<xsl:template match="p | q">
	   <xsl:element name="{name()}">
	     <text>{substring-before(., ' ')}</text>
		 <text>{substring-after(., ' ')}</text>
	   </xsl:element>
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
