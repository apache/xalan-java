<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                xmlns:fn0="http://fn0"				
				exclude-result-prefixes="xs math fn0"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 functions 
         specified in math: namespace -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
	      <xsl:variable name="seq1" select="(1,2,3,4,5,6,7,8,9,10)" as="xs:double*"/>
		  <xsl:variable name="mean" select="avg($seq1)" as="xs:double"/>
		  <xsl:variable name="tempNodeSet1">
			  <xsl:for-each select="1 to count($seq1)">
			     <xsl:variable name="seqItem" select="$seq1[xs:integer(.)]" as="xs:double"/>
				 <item><xsl:value-of select="($seqItem - $mean) * ($seqItem - $mean)"/></item>
			  </xsl:for-each>
		  </xsl:variable>
		  <xsl:variable name="sum1" select="sum(for $x in $tempNodeSet1/item return xs:double(string($x)))" as="xs:double"/>
		  <one><xsl:value-of select="round(math:sqrt($sum1 div count($seq1)), 12)"/></one>
		  <two><xsl:value-of select="fn0:stdDeviation($seq1)"/></two>
	   </result>
	</xsl:template>
	
	<xsl:function name="fn0:stdDeviation" as="xs:double">
	   <xsl:param name="seq1" as="xs:double*"/>
	   <xsl:variable name="mean" select="avg($seq1)" as="xs:double"/>
	   <xsl:variable name="tempNodeSet1">
		   <xsl:for-each select="1 to count($seq1)">
			  <xsl:variable name="seqItem" select="$seq1[xs:integer(.)]" as="xs:double"/>
		      <item><xsl:value-of select="($seqItem - $mean) * ($seqItem - $mean)"/></item>
		   </xsl:for-each>
	   </xsl:variable>
	   <xsl:variable name="sum1" select="sum(for $x in $tempNodeSet1/item return xs:double(string($x)))" as="xs:double"/>
	   <xsl:sequence select="round(math:sqrt($sum1 div count($seq1)), 12)"/>
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