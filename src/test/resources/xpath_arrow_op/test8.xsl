<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
    
  <!-- An XSL stylesheet test case, to test XPath 3.1 operator "=>" -->                
				
  <xsl:output method="xml" indent="yes"/>

  <xsl:variable name="func1" select="function($a, $b) {$a + $b}" as="function(*)"/>  
  
  <xsl:variable name="func2" select="function($map1, $a, $b) {map:get($map1, $a) + $b}" as="function(*)"/>
  
  <xsl:template match="/">
    <result>
	   <one>
	      <xsl:value-of select="3 => $func1(4)"/>
	   </one>
	   <two>
	      <xsl:value-of select="map{'p':1, 'q':2} => $func2('q', 7)"/>
	   </two>
	   <three>
	      <xsl:value-of select="'hello' => fn0:func3('world')"/>
	   </three>
	</result>
  </xsl:template>
  
  <!-- An XSL stylesheet function declaration -->
  <xsl:function name="fn0:func3" as="xs:string">
     <xsl:param name="a" as="xs:string"/>
	 <xsl:param name="b" as="xs:string"/>
	 <xsl:sequence select="$a || ' ' || $b"/>
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