<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"				
				exclude-result-prefixes="xs map array"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->							

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:param name="base-dir" select="'file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/resources/xsl3_tests/input/'" as="xs:string"/>

	<xsl:template match="/">
	   <result>
	      <xsl:variable name="collectionResult1" select="collection($base-dir || '.*[\\.](xml)')"/>
	      <xsl:variable name="collectionResult2" select="collection($base-dir || '.*[\\.](json)')"/>
	      <xsl:variable name="collectionResult3" select="collection($base-dir || '.*[\\.](txt)')"/>
	      
	      <xsl:variable name="seq1" select="insert-before($collectionResult3, 0, $collectionResult1)"/>
	      <xsl:variable name="seq2" select="insert-before($collectionResult2, 0, $seq1)"/>
	      
	      <xsl:for-each select="$seq2">
				<xsl:variable name="collectionResult1" select="."/>
				<xsl:choose>
				   <xsl:when test="$collectionResult1 instance of map(*)">
				      <!-- Process a JSON document which begins with json map -->
					  <xsl:variable name="map1" select="$collectionResult1" as="map(*)"/>
					  <map>
						 <xsl:for-each select="map:keys($map1)">
						   <entry key="{.}"><xsl:value-of select="map:get($map1, .)"/></entry>
						 </xsl:for-each>
					  </map>
				   </xsl:when>
				   <xsl:when test="$collectionResult1 instance of array(*)">
				      <!-- Process a JSON document which begins with json array -->
					  <array>
						 <xsl:for-each select="1 to array:size($collectionResult1)">
						   <item><xsl:value-of select="array:get($collectionResult1, xs:integer(.))"/></item>
						 </xsl:for-each>
					  </array>
				   </xsl:when>
				   <xsl:when test="$collectionResult1 instance of xs:string">
				      <!-- Process a file text document. -->
					  <txtContents><xsl:value-of select="$collectionResult1"/></txtContents>
				   </xsl:when>
				   <xsl:otherwise>
					  <xmlContents>
						 <xsl:copy-of select="."/>
					  </xmlContents>
				   </xsl:otherwise>
				</xsl:choose>
		  </xsl:for-each>
	   </result>
	</xsl:template>

</xsl:stylesheet>
