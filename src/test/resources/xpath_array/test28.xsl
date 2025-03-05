<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs array fn0"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath array's unary 
         lookup operation and related XPath expression syntax. -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">	   
	   <result>
	      <xsl:variable name="array1" select="['a', 'b', 'c', 'd', 'e']" as="array(xs:string)"/>
	      <arrayValues all="true">
	         <xsl:value-of select="$array1?*"/>
	      </arrayValues>
		  <arrayValues specific="true">
		     <!-- The following XSL stylesheet's result sibling elements, 
			      are alternate ways to emit XDM array's same functional information 
				  from stylesheet. -->
	         <one><xsl:value-of select="$array1?(3, 4, 5)"/></one>
			 <two><xsl:value-of select="for $idx in (3, 4, 5) return array:get($array1, $idx)"/></two>
			 <three>
			    <xsl:for-each select="(3, 4, 5)">
				   <xsl:value-of select="array:get($array1, .)"/><xsl:value-of select="if (position() ne last()) then ' ' else ()"/>
				</xsl:for-each>
			 </three>
	      </arrayValues>
		  <arrayValues specific="true">
	         <xsl:value-of select="$array1?fn0:getArrayIndex()"/>
	      </arrayValues>
	   </result>
	</xsl:template>
	
	<!-- An XSL stylesheet function, to return a sequence of xs:integer 
	     values. This returned sequence is an array's key specifier for array 
		 unary lookup operation. -->
	<xsl:function name="fn0:getArrayIndex" as="xs:integer*">
	   <xsl:sequence select="(3, 4, 5)"/>
	</xsl:function>
	
	<!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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
