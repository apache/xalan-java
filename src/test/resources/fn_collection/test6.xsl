<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->				
				
    <!-- An XSL 3 stylesheet test case, to test XPath 3.1 collection function 
         reading a set of CSV document local files. This stylesheet combines 
         information from various CSV document files into an aggregate JSON 
         result document using XPath function fn:xml-to-json. -->					

	<xsl:output method="text"/>
	
	<xsl:param name="localBaseUriPrefix" as="xs:string"/>

	<xsl:template match="/">
	   <xsl:variable name="fnCollectionResult1" select="collection($localBaseUriPrefix || 'fn_collection/.*[.]csv')" as="xs:string*"/>
	   <xsl:variable name="noOfCsvDocuments" select="count($fnCollectionResult1)"/>
	   <xsl:variable name="nodeSet1">
		   <map xmlns="http://www.w3.org/2005/xpath-functions">		       
			  <xsl:for-each select="1 to $noOfCsvDocuments">
				 <xsl:variable name="csvFileStrValue" select="$fnCollectionResult1[xs:integer(.)]"/>
				 <xsl:variable name="fileStrLines" select="tokenize($csvFileStrValue, '\r\n|\r|\n')" as="xs:string*"/>
				 <xsl:variable name="noOfLinesInFile" select="count($fileStrLines)"/>
				 <array key="csv{xs:integer(.)}">
					 <xsl:for-each select="1 to $noOfLinesInFile">
						<xsl:variable name="lineNo" select="xs:integer(.)" as="xs:integer"/>
						<xsl:variable name="fileLineStr" select="$fileStrLines[$lineNo]" as="xs:string"/>
						<xsl:variable name="tokenSeq" select="tokenize($fileLineStr, ',')" as="xs:string*"/>
						<!-- The first line of every csv file is csv's header value, and 
							 we do not emit that to XSL transform's result output. -->
						<xsl:if test="$lineNo gt 1">
						  <map>
							<string key="id"><xsl:value-of select="$tokenSeq[1]"/></string>
							<string key="employeeId"><xsl:value-of select="$tokenSeq[2]"/></string>
							<string key="employeeName"><xsl:value-of select="$tokenSeq[3]"/></string>
							<string key="departmentId"><xsl:value-of select="$tokenSeq[4]"/></string>
						  </map>
						</xsl:if>
					 </xsl:for-each>
				 </array>
			  </xsl:for-each>
		   </map>
	   </xsl:variable>
	   <xsl:value-of select="xml-to-json($nodeSet1, map {'indent' : true()})"/>
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
