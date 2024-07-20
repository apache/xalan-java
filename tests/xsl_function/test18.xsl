<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"                
                exclude-result-prefixes="xs fn0" version="3.0">
                
     <!-- Author: mukulg@apache.org -->
     
     <!-- An XSL stylesheet test case, that tests xsl:function calls 
          returning function items. -->            
				
     <xsl:output method="xml" indent="yes"/>          				
  
     <xsl:template match="/">
	    <result>
		   <xsl:variable name="func1" select="fn0:getFuncItem(1)" as="function(*)"/>
		   <xsl:variable name="func2" select="fn0:getFuncItem(2)" as="function(*)"/>
		   <one>
		      <xsl:value-of select="$func1(2,3)"/>
		   </one>
		   <two>
		      <xsl:value-of select="$func2(5,2)"/>
		   </two>
		</result>
	 </xsl:template>
	 
	 <!-- 
	    An XSL stylesheet function, that returns different function 
	    items depending on what argument has been passed with 
	    xsl:function call.
	  -->
	 <xsl:function name="fn0:getFuncItem" as="function(*)">
	    <xsl:param name="n0" as="xs:integer"/>
		<xsl:choose>
		   <xsl:when test="$n0 eq 1">
		      <xsl:sequence select="function($a,$b) { $a + $b }"/>
		   </xsl:when>
		   <xsl:when test="$n0 eq 2">
		      <xsl:sequence select="function($a,$b) { $a - $b }"/>
		   </xsl:when>
		   <xsl:when test="$n0 eq 3">
		      <xsl:sequence select="function($a,$b) { $a * $b }"/>		   
		   </xsl:when>
		</xsl:choose>
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
