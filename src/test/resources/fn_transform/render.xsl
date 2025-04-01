<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
			    version="3.0">
				
    <xsl:output method="xml" indent="yes"/>				
	
    <xsl:template match="/info">
	   <details>
		  <xsl:for-each select="*">
             <xsl:element name="{name()}">
		        <xsl:value-of select="position()"/>
		     </xsl:element>
          </xsl:for-each>
	   </details>
    </xsl:template>
	
</xsl:stylesheet>
				