<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
			 
    <xsl:output method="xml" indent="yes"/>			 
    
	<!-- An XSL stylesheet initial template -->
    <xsl:template name="Template1" visibility="public">
	   <result>
	      <or>
	         <a><xsl:value-of select="false() or false()"/></a>
		     <b><xsl:value-of select="false() or true()"/></b>
		     <c><xsl:value-of select="true() or false()"/></c>
		     <d><xsl:value-of select="true() or true()"/></d>
		  </or>
		  <and>
	         <a><xsl:value-of select="false() and false()"/></a>
		     <b><xsl:value-of select="false() and true()"/></b>
		     <c><xsl:value-of select="true() and false()"/></c>
		     <d><xsl:value-of select="true() and true()"/></d>
		  </and>
		  <not>
		     <a><xsl:value-of select="not(false())"/></a>
		     <b><xsl:value-of select="not(true())"/></b>
		  </not>
	   </result>
	</xsl:template>
    
</xsl:stylesheet>