<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                exclude-result-prefixes="xs map"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test xsl:map instruction -->                
				
    <xsl:output method="xml" indent="yes"/>
		 
    <!-- An XSL stylesheet global variable, referring to sequence of xdm maps. 
         The information contained within the run-time map is hard-coded within 
         this variable declaration.-->		 
    <xsl:variable name="map1" as="map(xs:integer, xs:string)">
	   <xsl:map>
	      <xsl:for-each select="1 to 5">
		     <xsl:map-entry key="." select="'hello ' || ."/>
          </xsl:for-each>		  
	   </xsl:map>
    </xsl:variable>	
				
    <xsl:template match="/">
	   <result>
	      <one><xsl:value-of select="map:get($map1, 2)"/></one>
		  <two><xsl:value-of select="map:get($map1, 3)"/></two>
		  <three><xsl:value-of select="map:get($map1, 4)"/></three>
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