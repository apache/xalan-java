<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:err="http://www.w3.org/2005/xqt-errors"
			    exclude-result-prefixes="xs err"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1.xml -->
  
  <!-- An XSL stylesheet test case, to test xsl:try and xsl:catch 
       instructions. -->  				

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/root">
    <result>
	   <xsl:for-each select="num">
	      <xsl:variable name="num1" select="xs:integer(.)"/>
	      <xsl:for-each select="../denom">
		     <xsl:variable name="denom1" select="xs:integer(.)"/>
			 <item>
				 <xsl:try select="round($num1 div $denom1, 5)">
				   <xsl:catch errors="err:FOAR0001" select="'num : ' || $num1 || ', denom : ' || $denom1 || ' = div_by_zero_err'"/>
				 </xsl:try>
			 </item>
	      </xsl:for-each>
	   </xsl:for-each>
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
  