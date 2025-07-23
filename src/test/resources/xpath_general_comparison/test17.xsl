<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"                				
                version="3.0">
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test16.xml -->
  
    <!-- An XSL stylesheet test case, to test XPath 3.1 general 
         comparison operator = with XPath literal array on LHS. 
         
         This stylesheet also tests, calling XSL stylesheet function 
         with arguments that're XPath literal sequence or array.         
    -->				
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/doc">
	   <result>
			<a>
				<xsl:variable name="var1">
					<xsl:if test="[two, one, three] = two">
						1
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="normalize-space($var1)" />
			</a>
			<b>
				<xsl:if test="[two, one, three] = four">
					2
				</xsl:if>
			</b>
			<c>
				<xsl:variable name="var1">
					<xsl:if test="fn0:compareXdmSeq((two, one, three), two)">
						3
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="normalize-space($var1)" />
			</c>
			<d>
				<xsl:variable name="var1">
					<xsl:if test="fn0:compareXdmArray([two, one, three], two)">
						4
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="normalize-space($var1)" />
			</d>
       </result>
	</xsl:template>
	
	<!-- An XSL stylesheet function, that compares an xdm sequence of 
	     elements with an element node using XPath operator '='. -->
	<xsl:function name="fn0:compareXdmSeq" as="xs:boolean">
	   <xsl:param name="p1" as="element()*"/>
	   <xsl:param name="p2" as="element()"/>
	   <xsl:sequence select="$p1 = $p2"/>
	</xsl:function>
	
	<!-- An XSL stylesheet function, that compares an xdm array of 
	     elements with an element node using XPath operator '='. -->
	<xsl:function name="fn0:compareXdmArray" as="xs:boolean">
	   <xsl:param name="p1" as="array(*)"/>
	   <xsl:param name="p2" as="element()"/>
	   <xsl:sequence select="$p1 = $p2"/>
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
