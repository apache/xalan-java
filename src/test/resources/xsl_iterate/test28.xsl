<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:math="http://www.w3.org/2005/xpath-functions/math"
				xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs math fn0"                
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
  
   <!-- An XSLT stylesheet test case, to test xsl:iterate instruction. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <result>        
	    <xsl:for-each select="(2,3,2.5,5,1.5)">
		  <xsl:variable name="num" select="."/>
	      <item num="{$num}" times="2" result="{math:pow($num,2)}">
		    <xsl:value-of select="fn0:pow($num,2)"/>
		  </item>
		</xsl:for-each>
      </result>
   </xsl:template>
  
   <!-- An XSL stylesheet function, that computes numeric result of 
        a number raised to a numeric power, using xsl:iterate 
        instruction. -->
   <xsl:function name="fn0:pow" as="xs:decimal">
      <xsl:param name="num" as="xs:decimal"/>
	  <xsl:param name="times" as="xs:decimal"/>	 
	  <xsl:iterate select="2 to $times">
         <xsl:param name="result" select="$num"/>			   
	     <xsl:on-completion select="$result"/>
		 <xsl:next-iteration>
		   <xsl:with-param name="result" select="$result * $num"/>
	     </xsl:next-iteration>	   
	  </xsl:iterate>	 
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
