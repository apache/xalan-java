<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"                           
                exclude-result-prefixes="xs"
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL stylesheet test case, to test XPath function 
        fn:format-dateTime. --> 			    
				
   <xsl:output method="xml" indent="yes"/>

   <xsl:variable name="value1" select="xs:dateTime('2002-12-31T10:15:20Z')" as="xs:dateTime"/>   
   
   <xsl:variable name="value2" select="xs:dateTime('2002-05-31T15:15:20+02:30')" as="xs:dateTime"/>

   <xsl:template match="/">
	  <result>
	     <one><xsl:value-of select="format-dateTime($value1, '[Y0001]-[M01]-[D01] [z]')"/></one>		 
		 <two><xsl:value-of select="format-dateTime($value2, '[D1] [MI] [Y] [Z]')"/></two>		 
		 <three><xsl:value-of select="format-dateTime($value1, '[h].[m01][Pn] on [FNn], [D1o] [MNn]')"/></three>
		 <four><xsl:value-of select="format-dateTime($value2, '[M01]/[D01]/[Y0001] at [H01]:[m01]:[s01]')"/></four>
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
