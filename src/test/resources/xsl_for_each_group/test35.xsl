<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn="http://www.w3.org/2005/xpath-functions"
				exclude-result-prefixes="xs fn"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test grouping of information provided 
       within a JSON document file using xsl:for-each-group instruction via 
       an intermediate transformation to XML document. -->                  
  
  <xsl:output method="json"/>
  
  <xsl:template match="/">
     <xsl:variable name="personsJsonStrVal" select="unparsed-text('person.json', 'UTF-8')" as="xs:string"/>
     <xsl:variable name="personsXmlNode" select="json-to-xml($personsJsonStrVal)"/>
     <xsl:variable name="groupedMapXmlNode">		
		<map xmlns="http://www.w3.org/2005/xpath-functions">
		  <!-- Forming groups based on range of 'id''s numeric value -->
	      <xsl:for-each-group select="$personsXmlNode/fn:map/fn:array/fn:map" group-by="xs:integer(fn:number[@key = 'id']) lt 5">
		     <array key="{if (current-grouping-key() eq true()) then ('id-lt-5') else ('id-ge-5')}">
			   <xsl:copy-of select="current-group()"/>
			 </array>
		  </xsl:for-each-group>
		</map>
     </xsl:variable>
	 <xsl:value-of select="xml-to-json($groupedMapXmlNode, map {'indent' : true()})"/>
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
