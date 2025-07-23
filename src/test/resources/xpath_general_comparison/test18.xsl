<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
  
    <!-- An XSL stylesheet test case, to test XPath 3.1 general 
         comparison operator = with XPath literal array on LHS
         and XPath literal array or sequence on RHS.         
         
         As per XPath 3.1 spec, from the point of view of XPath 
         comparison operator =, XPath literal arrays are atomized 
         to sequence before comparison, which means that XPath literal 
         array is similar to literal sequence for comparison purposes.         
    -->	                				
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/">
	   <result>
		  <one>
		     <xsl:value-of select="[1,2,3] = [2,5]"/>
		  </one>
		  <two>
		     <xsl:value-of select="[1,2,3] = (2,5)"/>
		  </two>
		  <three>
		     <xsl:value-of select="[1,2,3] = [6,7]"/>
		  </three>
		  <four>
		     <xsl:value-of select="[1,2,3] = (6,7)"/>
		  </four>
		  
		  <!-- Using xsl:variable, to do same comparisons -->
		  <xsl:variable name="n1" select="1" as="xs:integer"/>
		  <xsl:variable name="n2" select="2" as="xs:integer"/>
		  <xsl:variable name="n3" select="3" as="xs:integer"/>
		  <xsl:variable name="n5" select="5" as="xs:integer"/>
		  <xsl:variable name="n6" select="6" as="xs:integer"/>
		  <xsl:variable name="n7" select="7" as="xs:integer"/>
		  <a>
		    <xsl:value-of select="[$n1, $n2, $n3] = [$n2, $n5]"/>
		  </a>
		  <b>
		    <xsl:value-of select="[$n1, $n2, $n3] = ($n2, $n5)"/>
		  </b>
		  <c>
		    <xsl:value-of select="[$n1, $n2, $n3] = [$n6, $n7]"/>
		  </c>
		  <d>
		    <xsl:value-of select="[$n1, $n2, $n3] = ($n6, $n7)"/>
		  </d>
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
