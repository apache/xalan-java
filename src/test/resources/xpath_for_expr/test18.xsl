<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
				exclude-result-prefixes="math"
                version="3.0">			

   <!-- Author: mukulg@apache.org -->
   
   <!--  An XSL stylesheet test case, to test XPath literal sequence constructor 
         whose one or more items are XPath conventional function calls. Also using, 
         XPath 'for' expression and XSLT xsl:for-each instruction to traverse 
         the sequence. -->
         
   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
     <result>
        <one>
		  <xsl:value-of select="for $x in (round(math:pow(1.55777,2),2), round(math:pow(2.55777,2),2)) return round(math:sqrt($x),4)"/>
		</one>
        <two>
           <xsl:for-each select="(round(math:pow(1.55777,2),2), round(math:pow(2.55777,2),2), round(math:pow(3.55777,2),2))">
		      <item><xsl:value-of select="."/></item>
		   </xsl:for-each>
        </two>		
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
