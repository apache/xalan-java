<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="3.0">

	<!-- Author: mukulg@apache.org -->
	
	<!-- An XSL 3 stylesheet test case, that tests xsl:merge instruction 
	     combining data from various input XML documents. -->

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<info>
			<xsl:comment>The following emitted information, contains data grouped by fName and lName fields</xsl:comment>
			<result1 desc="grp_by_fName_and_lName">
				<xsl:merge>
					<xsl:merge-source name="one" select="doc('test4_1.xml')/info/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="fName" />
						<xsl:merge-key select="lName" />
					</xsl:merge-source>
					<xsl:merge-source name="two" select="doc('test4_2.xml')/info/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="fName" />
						<xsl:merge-key select="lName" />
					</xsl:merge-source>
					<xsl:merge-source name="three" select="doc('test4_3.xml')/info/seq/person" sort-before-merge="yes">
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
					<xsl:merge-source name="one" select="doc('test4_1.xml')/info/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="country" />
					</xsl:merge-source>
					<xsl:merge-source name="two" select="doc('test4_2.xml')/info/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="country" />
					</xsl:merge-source>
					<xsl:merge-source name="three" select="doc('test4_3.xml')/info/seq/person" sort-before-merge="yes">
						<xsl:merge-key select="country" />
					</xsl:merge-source>
					<xsl:merge-action>
						<country value="{current-merge-key()}">
							<xsl:for-each select="current-merge-group()">
							   <xsl:element name="{name()}">
							      <xsl:copy-of select="* except country"/>
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
