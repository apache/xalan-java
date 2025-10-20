<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath higher 
         order functions. -->				
    
    <xsl:output method="xml" indent="yes"/>   
   
    <!-- Variable, referring to an XPath sequence of function items -->
    <xsl:variable name="funcSeq1" select="(upper-case#1, lower-case#1, function($str1) {string-length($str1)})" as="function(*)*"/>							    
     
    <xsl:template match="/">
       <result>
          <xsl:for-each select="$funcSeq1">
		     <item>
			    <xsl:variable name="f1" select="."/>
			    <xsl:value-of select="$f1('Mukul Gandhi')"/>
			 </item>
		  </xsl:for-each>
		  <!-- Emitting result of function call fn0:func1, after 
		       xsl:for-each's evaluation has completed. -->
		  <item>
		     <xsl:value-of select="fn0:func1('Mukul Gandhi')"/>
		  </item>
       </result>
    </xsl:template>
	
	<xsl:function name="fn0:func1" as="xs:string">
	   <xsl:param name="str1" as="xs:string"/>
	   <xsl:sequence select="'hello ' || $str1"/>
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
