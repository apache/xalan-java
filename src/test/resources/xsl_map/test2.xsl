<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs map fn0"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test xsl:map instruction -->                
				
    <xsl:output method="xml" indent="yes"/>								      			

	<xsl:template match="/">
	   <result>
	      <xsl:variable name="week" select="fn0:getMap1()" as="map(xs:string, xs:string)"/>
	      <one><xsl:value-of select="map:get($week, 'Mo')"/></one>
		  <two><xsl:value-of select="map:get($week, 'Tu')"/></two>
		  <three><xsl:value-of select="map:get($week, 'We')"/></three>
	   </result>
	</xsl:template>
	
	<!-- An XSL 3 stylesheet function, that retuns an xdm map 
	     hard-coded within this function declaration. -->
	<xsl:function name="fn0:getMap1" as="map(xs:string, xs:string)">
	   <xsl:map>
		  <xsl:map-entry key="'Mo'" select="'Monday'"/>
		  <xsl:map-entry key="'Tu'" select="'Tuesday'"/>
		  <xsl:map-entry key="'We'" select="'Wednesday'"/>
		  <xsl:map-entry key="'Th'" select="'Thursday'"/>
		  <xsl:map-entry key="'Fr'" select="'Friday'"/>
		  <xsl:map-entry key="'Sa'" select="'Saturday'"/>
		  <xsl:map-entry key="'Su'" select="'Sunday'"/>
	   </xsl:map>
    </xsl:function>
    
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