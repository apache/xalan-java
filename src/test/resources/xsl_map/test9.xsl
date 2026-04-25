<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                exclude-result-prefixes="xs map"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test xsl:map instruction -->                
				
    <xsl:output method="xml" indent="yes"/>				
			
    <xsl:variable name="map1" as="map(xs:integer, item())">
	   <xsl:map>
		  <xsl:map-entry key="1">
		     <one>
			   <a/>
			 </one>
		  </xsl:map-entry>
		  <xsl:map-entry key="2">
		     <two>
			   <b/>
			 </two>
		  </xsl:map-entry>
		  <xsl:map-entry key="3">
             <three>
			   <c/>
			 </three>
          </xsl:map-entry>		  
	   </xsl:map>
    </xsl:variable>

    <xsl:variable name="map2" as="map(xs:integer, element())">
	   <xsl:map>
		  <xsl:map-entry key="1">
		     <one>
			   <p/>
			 </one>
		  </xsl:map-entry>
		  <xsl:map-entry key="2">
		     <two>
			   <q/>
			 </two>
		  </xsl:map-entry>
		  <xsl:map-entry key="3">
             <three>
			   <r/>
			 </three>
          </xsl:map-entry>		  
	   </xsl:map>
    </xsl:variable>

    <xsl:variable name="map3" as="map(*)">
	   <xsl:map>
		  <xsl:map-entry key="1">
		     <one>
			   <x/>
			 </one>
		  </xsl:map-entry>
		  <xsl:map-entry key="2">
		     <two>
			   <y/>
			 </two>
		  </xsl:map-entry>
		  <xsl:map-entry key="3">
             <three>
			   <z/>
			 </three>
          </xsl:map-entry>		  
	   </xsl:map>
    </xsl:variable>		

	<xsl:template match="/">
	   <result>
	      <A>
	        <xsl:copy-of select="map:get($map1, 1)"/>
		    <xsl:copy-of select="map:get($map1, 3)"/>
		  </A>
		  <B>
	        <xsl:copy-of select="map:get($map2, 1)"/>
		    <xsl:copy-of select="map:get($map2, 3)"/>
		  </B>
		  <C>
	        <xsl:copy-of select="map:get($map3, 1)"/>
		    <xsl:copy-of select="map:get($map3, 3)"/>
		  </C>
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