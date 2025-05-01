<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="xs array"
				version="3.0">

	<!-- Author: mukulg@apache.org -->

	<!-- An XSL 3 stylesheet test case, that tests xsl:merge instruction 
	     combining data from various input JSON documents. 
	     
	     The value of all XPath map lookups within this stylesheet, are done
	     using function call syntax.     
	-->

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<info>
			<xsl:variable name="map1" select="parse-json(unparsed-text('test4_1.json'))" as="map(*)" />
			<xsl:variable name="infoMap1" select="$map1('info')" as="map(*)" />
			<xsl:variable name="personArr1" select="$infoMap1('personList')" as="array(*)" />
			<xsl:variable name="personNodeList1">
				<seq>
					<xsl:variable name="arrSize" select="array:size($personArr1)" as="xs:integer" />
					<xsl:for-each select="1 to $arrSize">
						<person>
							<xsl:variable name="personMap1" select="array:get($personArr1, .)" as="map(*)" />
							<xsl:variable name="id" select="$personMap1('id')" as="xs:integer" />
							<xsl:variable name="fName" select="$personMap1('fName')" as="xs:string" />
							<xsl:variable name="lName" select="$personMap1('lName')" as="xs:string" />
							<xsl:variable name="country" select="$personMap1('country')" as="xs:string" />
							<id><xsl:value-of select="$id" /></id>
							<fName><xsl:value-of select="$fName" /></fName>
							<lName><xsl:value-of select="$lName" /></lName>
							<country><xsl:value-of select="$country" /></country>
						</person>
					</xsl:for-each>
				</seq>
			</xsl:variable>
			<xsl:variable name="map2" select="parse-json(unparsed-text('test4_2.json'))" as="map(*)" />
			<xsl:variable name="infoMap2" select="$map2('info')" as="map(*)" />
			<xsl:variable name="personArr2" select="$infoMap2('personList')" as="array(*)" />
			<xsl:variable name="personNodeList2">
				<seq>
					<xsl:variable name="arrSize" select="array:size($personArr2)" as="xs:integer" />
					<xsl:for-each select="1 to $arrSize">
						<person>
							<xsl:variable name="personMap2" select="array:get($personArr2, .)" as="map(*)" />
							<xsl:variable name="id" select="$personMap2('id')" as="xs:integer" />
							<xsl:variable name="fName" select="$personMap2('fName')" as="xs:string" />
							<xsl:variable name="lName" select="$personMap2('lName')" as="xs:string" />
							<xsl:variable name="country" select="$personMap2('country')" as="xs:string" />
							<id><xsl:value-of select="$id" /></id>
							<fName><xsl:value-of select="$fName" /></fName>
							<lName><xsl:value-of select="$lName" /></lName>
							<country><xsl:value-of select="$country" /></country>
						</person>
					</xsl:for-each>
				</seq>
			</xsl:variable>
			<xsl:variable name="map3" select="parse-json(unparsed-text('test4_3.json'))" as="map(*)" />
			<xsl:variable name="infoMap3" select="$map3('info')" as="map(*)" />
			<xsl:variable name="personArr3" select="$infoMap3('personList')" as="array(*)" />
			<xsl:variable name="personNodeList3">
				<seq>
					<xsl:variable name="arrSize" select="array:size($personArr3)" as="xs:integer" />
					<xsl:for-each select="1 to $arrSize">
						<person>
							<xsl:variable name="personMap3" select="array:get($personArr3, .)" as="map(*)" />
							<xsl:variable name="id" select="$personMap3('id')" as="xs:integer" />
							<xsl:variable name="fName" select="$personMap3('fName')" as="xs:string" />
							<xsl:variable name="lName" select="$personMap3('lName')" as="xs:string" />
							<xsl:variable name="country" select="$personMap3('country')" as="xs:string" />
							<id><xsl:value-of select="$id" /></id>
							<fName><xsl:value-of select="$fName" /></fName>
							<lName><xsl:value-of select="$lName" /></lName>
							<country><xsl:value-of select="$country" /></country>
						</person>
					</xsl:for-each>
				</seq>
			</xsl:variable>
			<xsl:comment>The following emitted information, contains data grouped by fName and lName fields</xsl:comment>
			<result1 desc="grp_by_fName_and_lName">
				<xsl:merge>
					<xsl:merge-source name="one" select="$personNodeList1/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="fName" />
						<xsl:merge-key select="lName" />
					</xsl:merge-source>
					<xsl:merge-source name="two" select="$personNodeList2/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="fName" />
						<xsl:merge-key select="lName" />
					</xsl:merge-source>
					<xsl:merge-source name="three" select="$personNodeList3/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="fName" />
						<xsl:merge-key select="lName" />
					</xsl:merge-source>
					<xsl:merge-action>
						<name value="{current-merge-key()}">
							<xsl:copy-of select="current-merge-group()" />
						</name>
					</xsl:merge-action>
				</xsl:merge>
			</result1>
			<xsl:comment>The following emitted information, contains data grouped by country name</xsl:comment>
			<result2 desc="grp_by_country">
				<xsl:merge>
					<xsl:merge-source name="one" select="$personNodeList1/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="country" />
					</xsl:merge-source>
					<xsl:merge-source name="two" select="$personNodeList2/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="country" />
					</xsl:merge-source>
					<xsl:merge-source name="three" select="$personNodeList3/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="country" />
					</xsl:merge-source>
					<xsl:merge-action>
						<country value="{current-merge-key()}">
							<xsl:for-each select="current-merge-group()">
								<xsl:element name="{name()}">
									<xsl:copy-of select="* except country" />
								</xsl:element>
							</xsl:for-each>
						</country>
					</xsl:merge-action>
				</xsl:merge>
			</result2>
		</info>
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
