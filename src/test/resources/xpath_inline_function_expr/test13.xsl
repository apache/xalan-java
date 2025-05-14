<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math" 				
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                
				
   <!-- An XSL 3 stylesheet test case, to test use of XPath user-defined 
        functions and binary operator '*' together in different ways for the 
		same use case.
   -->	
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
         <xsl:variable name="sqrt" select="function($a) { math:sqrt($a) }" as="function(*)"/>
		 <xsl:variable name="sqr" select="function($a) { $a * $a }" as="function(*)"/>
		 <one><xsl:value-of select="apply($sqrt, [9]) * apply($sqr, [5])"/></one>
		 
		 <xsl:variable name="p" select="$sqrt(9)"/>
		 <xsl:variable name="q" select="$sqr(5)"/>
		 
		 <two><xsl:value-of select="$p * $q"/></two>
		 
		 <xsl:variable name="apply1" select="function($a, $b) { let $L := math:sqrt($a), 
		                                                            $M := $b * $b 
																    return $L * $M }"/>
		 <three><xsl:value-of select="$apply1(9, 5)"/></three>
		 
		 <xsl:variable name="apply2" select="function($a, $b) { let $L := $sqrt($a), 
		                                                            $M := $sqr($b) return $L * $M }"/>
         <four><xsl:value-of select="$apply2(9, 5)"/></four>
         		 
         <five><xsl:value-of select="$sqrt(9) * $sqr(5)"/></five>		 
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