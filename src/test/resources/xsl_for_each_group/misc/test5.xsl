<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"                
                version="3.0">

    <!-- An XSL stylesheet test case, to test W3C XSLT 3.0 test case for-each-group-041 -->				
	
	<xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>	     
	      <xsl:for-each-group select="'a', 1 to 10, 'b', 11 to 20" group-starting-with=".[. instance of xs:string]">
		     <group><xsl:value-of select="current-group()"/></group>
		  </xsl:for-each-group>
	   </result>
    </xsl:template>
    
</xsl:stylesheet>
