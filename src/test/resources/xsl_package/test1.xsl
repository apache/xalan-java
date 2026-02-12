<?xml version="1.0" encoding="UTF-8"?>
<xsl:package name="test1.xsl"
	         package-version="1.0.0"	
             version="3.0"
             xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	         xmlns:fn0="http://fn0"
	         exclude-result-prefixes="fn0">
	         
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1.xml -->
  
    <!-- An XSL 3 stylesheet test case to test, xsl:package instruction -->
	
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:mode name="mode1" visibility="public"/>
	
	<xsl:use-package name="test2.xsl" package-version="1.0.0">
	   <xsl:accept component="function" names="fn0:abc#0 fn0:abc2#1" visibility="public"/>		   
	</xsl:use-package>
    
    <xsl:template match="/abc" mode="mode1">
	   <result>
	      <one>
	        <xsl:value-of select="fn0:abc()"/>
		  </one>
		  <two>
		     <xsl:copy-of select="fn0:abc2('hello')"/>
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
  
</xsl:package>
   