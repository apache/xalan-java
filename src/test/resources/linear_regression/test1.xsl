<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"				
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                
				
   <!-- use with test1.xml -->
   
   <!-- An XSL 3 stylesheet example, to implement statistical 
        prediction with linear regression. -->   

   <xsl:output method="xml" indent="yes"/>
   
   <!-- Value of linear regression weight, using sample data -->
   <xsl:variable name="w" select="fn0:getWeight(data)" as="xs:double"/>
   
   <!-- Value of linear regression bias, using sample data -->
   <xsl:variable name="b" select="fn0:getBias(data, $w)" as="xs:double"/>

   <xsl:template match="/">
      <result>
	     <xsl:for-each select="(23, 37, 42)">
		    <answer x="{.}" y="{fn0:predictY(., $w, $b)}"/>
		 </xsl:for-each>
	  </result>
   </xsl:template>
   
   <!-- An XSL stylesheet function, to predict value of 'y' 
       (dependent variable) for a supplied input value ('x'), 
        using a linear prediction model. -->
   <xsl:function name="fn0:predictY" as="xs:double">
      <xsl:param name="x" as="xs:double"/>
	  <xsl:param name="w" as="xs:double"/>
	  <xsl:param name="b" as="xs:double"/>
	  <xsl:sequence select="($w * $x) + $b"/>
   </xsl:function>
   
   <!-- An XSL stylesheet function, to get linear regression
        weight, using sample data. -->
   <xsl:function name="fn0:getWeight" as="xs:double">
      <xsl:param name="data" as="element(data)"/>
	  <xsl:variable name="meanX" select="sum($data/point/@x) div count($data/point)" as="xs:double"/>
	  <xsl:variable name="meanY" select="sum($data/point/@y) div count($data/point)" as="xs:double"/>
	  <xsl:iterate select="$data/point">
		 <xsl:param name="a" select="0" as="xs:double"/>
		 <xsl:param name="b" select="0" as="xs:double"/>
		 <xsl:on-completion>
			<xsl:sequence select="$a div $b"/>
		 </xsl:on-completion>
		 <xsl:next-iteration>
			<xsl:with-param name="a" select="$a + (./@x - $meanX) * (./@y - $meanY)" as="xs:double"/>
			<xsl:with-param name="b" select="$b + math:pow((./@x - $meanX),2)" as="xs:double"/>
		 </xsl:next-iteration>
	   </xsl:iterate>
   </xsl:function>
   
   <!-- An XSL stylesheet function, to get linear regression
        bias, using sample data. -->
   <xsl:function name="fn0:getBias" as="xs:double">
      <xsl:param name="data" as="element(data)"/>
	  <xsl:param name="w" as="xs:double"/>
	  <xsl:variable name="meanX" select="sum($data/point/@x) div count($data/point)" as="xs:double"/>
	  <xsl:variable name="meanY" select="sum($data/point/@y) div count($data/point)" as="xs:double"/>
	  <xsl:sequence select="($meanY - ($w * $meanX)) div count($data)"/>
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