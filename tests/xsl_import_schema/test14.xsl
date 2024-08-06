<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                version="3.0">

   <!-- Author: mukulg@apache.org -->
    
   <!-- use with test7.xml --> 
    
   <!-- An XSL stylesheet test case, to test XSLT 3.0's xsl:import-schema 
        instruction and XPath 3 sequence type schema-attribute test. 
   -->				

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:import-schema schema-location="info_5.xsd"/>

   <xsl:template match="/info">
	  <result>
	     <xsl:for-each select="*/@val">
		   <attr id="{position()}" isDeclaredInSchema="{. instance of schema-attribute(val)}" value="{.}">
			  <containedInElem>
			    <xsl:value-of select="local-name(..)"/>
			  </containedInElem>
		   </attr>
		 </xsl:for-each>
	  </result>	  
   </xsl:template>
   
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
