<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"                           
                exclude-result-prefixes="xs"
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL stylesheet test case, to test XPath function 
        fn:format-time. -->			    
				
   <xsl:output method="xml" indent="yes"/>

   <xsl:variable name="value1" select="xs:time('15:58:00')" as="xs:time"/>   
   <xsl:variable name="value2" select="xs:time('15:58:45')" as="xs:time"/>
   <xsl:variable name="value3" select="xs:time('15:58:45+02:00')" as="xs:time"/>

   <xsl:template match="/">
	  <result>
	     <one><xsl:value-of select="format-time($value1, '[h]:[m01] [PN]', 'en', (), ())"/></one>		 
		 <two><xsl:value-of select="format-time($value2, '[h]:[m01]:[s01] [Pn]', 'en', (), ())"/></two>		 
		 <three><xsl:value-of select="format-time($value1, '[H01]:[m01]')"/></three>
		 <four><xsl:value-of select="format-time($value3, '[H01]:[m01]:[s01] [z]', 'en', (), ())"/></four>
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
