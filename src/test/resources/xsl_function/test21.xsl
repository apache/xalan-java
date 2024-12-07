<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
			    exclude-result-prefixes="xs fn0" version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
    
  <!-- An XSL stylesheet test case, to test an XSL element xsl:function.
       Using a rudimentary maths problem for this use case.
  -->			    
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <xsl:copy-of select="fn0:triangleArea((1,3),(2,-5),(-8,4))"/>
  </xsl:template>
  
  <!-- An XSL function definition, to find area of a triangle 
       whose vertex co-ordinates are specified. -->
  <xsl:function name="fn0:triangleArea" as="element(area)">
     <xsl:param name="p1" as="xs:integer*"/>
	 <xsl:param name="p2" as="xs:integer*"/>
	 <xsl:param name="p3" as="xs:integer*"/>
	 <xsl:variable name="x1" select="$p1[1]" as="xs:integer"/>
	 <xsl:variable name="y1" select="$p1[2]" as="xs:integer"/>
	 <xsl:variable name="x2" select="$p2[1]" as="xs:integer"/>
	 <xsl:variable name="y2" select="$p2[2]" as="xs:integer"/>
	 <xsl:variable name="x3" select="$p3[1]" as="xs:integer"/>
	 <xsl:variable name="y3" select="$p3[2]" as="xs:integer"/>
	 <area>
	   <xsl:value-of select="abs(0.5 * (($x1 - $x3)*($y2 - $y3) - ($x2 - $x3)*($y1 - $y3)))"/>
	 </area>	 
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
