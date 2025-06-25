<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	            xmlns:xs="http://www.w3.org/2001/XMLSchema"
	            exclude-result-prefixes="xs"
	            version="3.0">
	            
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath math arithmetic expressions 
         which have one of the operand as context item expression ".". -->	            

	<xsl:output method="xml" indent="yes"/>

	<xsl:template name="main">
		<result>
			<xsl:variable name="l" as="xs:integer+" select="(0, 3)" />

			<xsl:for-each select="$l">
				<xsl:variable name="p" as="xs:integer" select=". + 1" />
				<a><xsl:value-of select="$p" /></a>
			</xsl:for-each>

			<xsl:for-each select="$l">
				<xsl:variable name="p" as="xs:integer" select=". - 1" />
				<b><xsl:value-of select="$p" /></b>
			</xsl:for-each>

			<xsl:for-each select="$l">
				<xsl:variable name="p" as="xs:integer" select=". * 2" />
				<c><xsl:value-of select="$p" /></c>
			</xsl:for-each>

			<xsl:variable name="m" as="xs:integer+" select="(2, 4)" />

			<xsl:for-each select="$m">
				<xsl:variable name="p" as="xs:integer" select=". div 2" />
				<d><xsl:value-of select="$p" /></d>
			</xsl:for-each>

			<xsl:for-each select="$l">
				<xsl:variable name="p" as="xs:integer" select=". mod 2" />
				<e><xsl:value-of select="$p" /></e>
			</xsl:for-each>

			<xsl:for-each select="$l">
				<xsl:variable name="p" as="xs:integer" select=". idiv 2" />
				<f><xsl:value-of select="$p" /></f>
			</xsl:for-each>
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
