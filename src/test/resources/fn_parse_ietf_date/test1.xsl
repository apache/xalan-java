<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"                           
                exclude-result-prefixes="xs"
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL 3 stylesheet test case, to test an XPath 3.1 function 
        fn:parse-ietf-date. -->  			    
				
   <xsl:output method="xml" indent="yes"/>

   <xsl:variable name="str1" select="xs:string('Tue, 15 Nov 1994 08:12:31 GMT')" as="xs:string"/>
   <xsl:variable name="str2" select="xs:string('Wed, 16 Nov 1994 10:15:20 GMT')" as="xs:string"/>      

   <xsl:template match="/">
	  <result>
		 <one><xsl:value-of select="parse-ietf-date($str1)"/></one>
		 <two><xsl:value-of select="parse-ietf-date($str2)"/></two>		 
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
