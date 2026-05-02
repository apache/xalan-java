<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="map"
                version="3.0">
                
  <!-- This XSL stylesheet test case, is motivated by XSL stylesheet
       examples provided by Martin Honnen. -->                
  
  <!-- Author: mukulg@apache.org -->
    
  <!-- An XSL stylesheet test case, to test XPath 3.1 operator "=>" -->                
				
  <xsl:output method="xml" indent="yes"/>				
  
  <xsl:template match="/">
    <result>
	   <one>
	      <xsl:value-of select="map{'a':1} => serialize(map{'method':'adaptive'})"/>
	   </one>
	   <two>
	      <a><xsl:value-of select="map{'a':1, 'b':2, 'c':3} => map:contains('a')"/></a>
		  <b><xsl:value-of select="map{'a':1, 'b':2, 'c':3} => map:contains('d')"/></b>
		  <c><xsl:value-of select="map{'a':1, 'b':2, 'c':3} => map:keys()"/></c>
		  <xsl:variable name="map1" select="map{'a':1, 'b':2, 'c':3}" as="map(*)"/>
		  <d><xsl:value-of select="$map1 => map:keys()"/></d>
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