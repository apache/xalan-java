<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"				
                version="3.0">
                
     <!-- Author: mukulg@apache.org -->
     
     <!-- use with test1.xml -->
  
     <!-- An XSL 3 stylesheet test case, to test XPath 3.1 schema aware feature -->                 

     <xsl:output method="xml" indent="yes"/>

	 <xsl:template match="/">
	    <result>
	       <xsl:variable name="value1" select="person/id + 5"/>
	       <one isInteger="{$value1 instance of xs:integer}"><xsl:value-of select="$value1"/></one>
		   <xsl:variable name="date2" select="person/dob + xs:yearMonthDuration('P5Y1M')"/>
		   <two isDate="{$date2 instance of xs:date}"><xsl:value-of select="$date2"/></two>
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