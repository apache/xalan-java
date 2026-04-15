<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->                
   
    <!-- An XSL 3 stylesheet test case, to test XSL transformation with 
         an initial named function. -->					

	<xsl:output method="text"/>
	
	<xsl:variable name="doc1" select="/"/>

	<xsl:function name="fn0:func1">
       <xsl:sequence select="concat($doc1/test1/message, ' ', fn0:length($doc1/test1/message))"/>
	</xsl:function>
	
	<xsl:function name="fn0:length" as="xs:integer">
	   <xsl:param name="str" as="xs:string"/>
	   <xsl:sequence select="string-length($str)"/>
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