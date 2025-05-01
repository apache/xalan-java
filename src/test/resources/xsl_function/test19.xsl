<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
			    exclude-result-prefixes="xs fn0" version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
    
  <!-- An XSL stylesheet test case, to test an XSL element xsl:function. -->			    
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <xsl:copy-of select="fn0:getDivPoint((6,-4),(0,0),(2,3))"/>
  </xsl:template>
  
  <!-- An XSL function definition, to find division point of 
       a line-segment in the ratio m1:m2. -->
  <xsl:function name="fn0:getDivPoint" as="element(divPoint)">
     <xsl:param name="p1" as="xs:integer*"/>
	 <xsl:param name="p2" as="xs:integer*"/>
	 <xsl:param name="ratio" as="xs:positiveInteger*"/>
	 <xsl:variable name="x1" select="$p1[1]" as="xs:integer"/>
	 <xsl:variable name="y1" select="$p1[2]" as="xs:integer"/>
	 <xsl:variable name="x2" select="$p2[1]" as="xs:integer"/>
	 <xsl:variable name="y2" select="$p2[2]" as="xs:integer"/>
	 <xsl:variable name="m1" select="$ratio[1]" as="xs:positiveInteger"/>
	 <xsl:variable name="m2" select="$ratio[2]" as="xs:positiveInteger"/>
	 <divPoint>
	   <x><xsl:value-of select="($m2*$x1 + $m1*$x2) div ($m1 + $m2)"/></x>
	   <y><xsl:value-of select="($m2*$y1 + $m1*$y2) div ($m1 + $m2)"/></y>
	 </divPoint>	 
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
