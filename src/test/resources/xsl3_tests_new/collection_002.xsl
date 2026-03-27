<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:xs="http://www.w3.org/2001/XMLSchema"                			
				exclude-result-prefixes="xs"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->								    			 

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:param name="base-dir" select="'file:/d:/eclipseWorkspaces/xalanj/xalan-j_xslt3.0_mvn/src/test/resources/xsl3_tests/input/'" as="xs:string"/>
	
	<xsl:variable name="search_word_str" select="'hello'" as="xs:string"/>

	<xsl:template match="/">
	   <groups>
	      <xsl:variable name="collectionResult1" select="collection($base-dir || 'differentfile.*[\\.]xml')"/>
		  <xsl:variable name="nodeSet1">
		     <xsl:for-each select="$collectionResult1">
			    <xsl:for-each select="./*/*">
				  <xsl:copy-of select="."/>
				</xsl:for-each>
			 </xsl:for-each>
		  </xsl:variable>
	      <xsl:for-each-group select="$nodeSet1/*" group-by="contains(string(.), $search_word_str)">
		     <group key="{if (current-grouping-key() eq true()) then ('contains_word_' || $search_word_str) else ('doesnt_contain_word_' || $search_word_str)}">
			    <xsl:copy-of select="current-group()"/>
			 </group>
		  </xsl:for-each-group>
	   </groups>
	</xsl:template>

</xsl:stylesheet>