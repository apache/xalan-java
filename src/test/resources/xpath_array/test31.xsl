<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="#all"
                version="3.0">             				

  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL styleshee test case, to test xdm sequence type expressions 
       like array(xs:integer*), and few XPath array functions as well. -->
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
        <xsl:variable name="arr1" select="[1,2,3]" as="array(xs:integer)"/>
		<xsl:variable name="arr1" select="array:append($arr1,(4,5))" as="array(xs:integer*)"/>
		<xsl:variable name="arr1" select="array:append($arr1,(6,7))" as="array(xs:integer*)"/>
		<xsl:variable name="arrSize1" select="array:size($arr1)" as="xs:integer"/>
		<array size="{$arrSize1}">
		  <xsl:for-each select="1 to $arrSize1">
		     <item>
			    <xsl:value-of select="$arr1(.)"/>
			 </item>
		  </xsl:for-each>
		</array>
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
