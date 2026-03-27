<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
			 
    <xsl:output method="xml" indent="yes"/>			 
    
	<!-- An XSL stylesheet initial template -->
    <xsl:template name="Template1" visibility="public">
	   <result>
	      <xsl:variable name="v1" select="'a'" as="xs:string"/>
		  <xsl:variable name="v2" select="'b'" as="xs:string"/>
	      <or>
	         <a><xsl:value-of select="($v1 eq $v2) or ($v1 eq $v2)"/></a>
		     <b><xsl:value-of select="($v1 eq $v2) or ($v1 eq $v1)"/></b>
		     <c><xsl:value-of select="($v1 eq $v1) or ($v1 eq $v2)"/></c>
		     <d><xsl:value-of select="($v1 eq $v1) or ($v1 eq $v1)"/></d>
		  </or>
		  <and>
	         <a><xsl:value-of select="($v1 eq $v2) and ($v1 eq $v2)"/></a>
		     <b><xsl:value-of select="($v1 eq $v2) and ($v1 eq $v1)"/></b>
		     <c><xsl:value-of select="($v1 eq $v1) and ($v1 eq $v2)"/></c>
		     <d><xsl:value-of select="($v1 eq $v1) and ($v1 eq $v1)"/></d>
		  </and>
		  <not>
		     <a><xsl:value-of select="not(($v1 eq $v2))"/></a>
		     <b><xsl:value-of select="not(($v1 eq $v1))"/></b>
		  </not>
	   </result>
	</xsl:template>
    
</xsl:stylesheet>